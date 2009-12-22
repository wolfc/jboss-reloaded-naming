/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.reloaded.naming.service;

import javax.naming.*;
import javax.naming.spi.ObjectFactory;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class NameSpaces
{
   private InitialContext iniCtx;

   protected Reference createRef(String nns, Class<? extends ObjectFactory> objectFactory)
   {
      RefAddr refAddr = new StringRefAddr("nns", nns);
      Reference ref = new Reference("javax.naming.Context", refAddr, objectFactory.getName(), null);
      return ref;
   }

   public void start() throws NamingException
   {
      iniCtx = new InitialContext();
      Context javaContext = (Context) iniCtx.lookup("java:");
      javaContext.rebind("comp", createRef("ENC", ComponentObjectFactory.class));
      javaContext.rebind("module", createRef("MOD", ModuleObjectFactory.class));
      javaContext.rebind("app", createRef("APP", AppObjectFactory.class));
      javaContext.createSubcontext("global");
   }
   
   public void stop() throws NamingException
   {
      if(iniCtx == null)
         return;
      
      Context javaContext = (Context) iniCtx.lookup("java:");
      javaContext.unbind("global");
      javaContext.unbind("app");
      javaContext.unbind("module");
      javaContext.unbind("comp");
      
      iniCtx.close();
   }
}
