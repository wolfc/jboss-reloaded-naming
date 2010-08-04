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
package org.jboss.reloaded.naming;

import org.jboss.naming.ENCFactory;
import org.jboss.reloaded.naming.spi.JavaEEComponent;
import org.jboss.reloaded.naming.util.ThreadLocalStack;

/**
 * Provides the bridge between the JNDI object factory and the namespaces.
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class CurrentComponent
{
   private static ThreadLocalStack<JavaEEComponent> stack = new ThreadLocalStack<JavaEEComponent>();
   
   /**
    * @return the current JavaEEComponent
    */
   public static JavaEEComponent get()
   {
      return stack.get();
   }
   
   public static JavaEEComponent pop()
   {
      JavaEEComponent comp = stack.pop();
      
      // to enable legacy java:comp resolution we must also pop from ENCFactory
      ENCFactory.popContextId();
      
      return comp;
   }
   
   public static void push(JavaEEComponent component)
   {
      // to enable legacy java:comp resolution we must also push to ENCFactory
      ENCFactory.pushContextId(component.getName());
      
      stack.push(component);
   }
}
