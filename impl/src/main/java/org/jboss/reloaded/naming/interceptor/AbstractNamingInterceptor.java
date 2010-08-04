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
package org.jboss.reloaded.naming.interceptor;

import org.jboss.reloaded.naming.CurrentComponent;
import org.jboss.reloaded.naming.spi.JavaEEComponent;

/**
 * Provides the means of pushing the JavaEE namespace upon invocations.
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public abstract class AbstractNamingInterceptor<T>
{
   protected void afterInvoke(T context)
   {
      JavaEEComponent component = CurrentComponent.pop();
      assert component == getComponent() : "popped wrong component " + component + ", was expecting " + getComponent();
   }
   
   protected void beforeInvoke(T context)
   {
      JavaEEComponent component = getComponent();
      assert component != null : "component is null";
      CurrentComponent.push(component);
   }
   
   protected abstract JavaEEComponent getComponent();
   
   public Object invoke(T context) throws Throwable
   {
      beforeInvoke(context);
      try
      {
         return proceed(context);
      }
      finally
      {
         afterInvoke(context);
      }
   }
   
   protected abstract Object proceed(T context) throws Throwable;
}
