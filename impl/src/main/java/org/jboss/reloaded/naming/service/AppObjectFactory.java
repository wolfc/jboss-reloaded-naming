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
package org.jboss.reloaded.naming.service;

import org.jboss.naming.ENCFactory;
import org.jboss.reloaded.naming.CurrentComponent;
import org.jboss.reloaded.naming.spi.JavaEEApplication;
import org.jboss.reloaded.naming.spi.JavaEEComponent;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;

/**
 * The AppObjectFactory is responsible for resolving java:app.
 * Normally it's installed via the NameSpaces class.
 *
 * @see org.jboss.reloaded.naming.service.NameSpaces
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class AppObjectFactory implements ObjectFactory
{
   public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception
   {
      JavaEEComponent current = CurrentComponent.get();
      Object currentLegacyId = ENCFactory.getCurrentId();
      // if there is no current set or some legacy component has pushed another id
      if(current == null || !currentLegacyId.equals(ComponentObjectFactory.id(current)))
      {
         // do legacy resolution
         throw new NamingException("java:app not supported by legacy component " + currentLegacyId);
      }
      else
      {
         JavaEEApplication application = current.getModule().getApplication();
         if(application == null)
            throw new NamingException("module " + current.getModule().getName() + " is not part of an application");
         return application.getContext();
      }
   }
}
