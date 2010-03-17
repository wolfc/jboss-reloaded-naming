/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.reloaded.naming.deployers.mc;

import org.jboss.logging.Logger;
import org.jboss.reloaded.naming.spi.JavaEEApplication;

import javax.naming.NamingException;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class MCJavaEEApplication extends AbstractNameSpace implements JavaEEApplication
{
   private static final Logger log = Logger.getLogger(MCJavaEEApplication.class);
   
   public MCJavaEEApplication(String name)
   {
      super(name);
   }

   @Override
   public void start() throws NamingException
   {
      context = nameSpaces.getGlobalContext().createSubcontext(name);
      // JavaEE 6 5.15
      context.bind("AppName", name);
      log.debug("Installed context " + context + " for JavaEE application " + name);
   }

   @Override
   public void stop() throws NamingException
   {
      nameSpaces.getGlobalContext().unbind(name);
      context = null;
   }

   @Override
   public String toString()
   {
      return "MCJavaEEApplication{" +
         "name='" + name + '\'' +
         '}';
   }
}
