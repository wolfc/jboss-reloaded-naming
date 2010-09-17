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
import org.jboss.reloaded.naming.service.NameSpaces;
import org.jnp.interfaces.NamingContext;
import org.jnp.server.NamingServer;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public abstract class AbstractNameSpace
{
   private static final Logger log = Logger.getLogger(AbstractNameSpace.class);
   
   protected final String name;
   protected NameSpaces nameSpaces;
   protected Context context;

   protected AbstractNameSpace(String name)
   {
      this.name = name;
   }

   public Context getContext()
   {
      return context;
   }

   protected Context getGlobalContext()
   {
      return nameSpaces.getGlobalContext();
   }

   public String getName()
   {
      return name;
   }

   public void setNameSpaces(NameSpaces nameSpaces)
   {
      this.nameSpaces = nameSpaces;
   }

   public void start() throws NamingException
   {
      NamingServer srv = new NamingServer();
      context = new NamingContext(nameSpaces.getGlobalContext().getEnvironment(), null, srv);
   }

   public void stop() throws NamingException
   {
      context = null;
   }
}
