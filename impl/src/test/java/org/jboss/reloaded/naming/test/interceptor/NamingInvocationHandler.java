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
package org.jboss.reloaded.naming.test.interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.jboss.reloaded.naming.interceptor.AbstractNamingInterceptor;
import org.jboss.reloaded.naming.spi.JavaEEComponent;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class NamingInvocationHandler extends AbstractNamingInterceptor<NamingInvocationHandler.InvocationContext> implements InvocationHandler
{
   public static class InvocationContext
   {
      Object target;
      Method method;
      Object parameters[];
   }
   
   private JavaEEComponent component;
   private Object target;
   
   public NamingInvocationHandler(JavaEEComponent component, Object target)
   {
      this.component = component;
      this.target = target;
   }
   
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
   {
      InvocationContext context = new InvocationContext();
      context.target = target;
      context.method = method;
      context.parameters = args;
      return invoke(context);
   }

   @Override
   protected JavaEEComponent getComponent()
   {
      return component;
   }

   @Override
   protected Object proceed(NamingInvocationHandler.InvocationContext context) throws Throwable
   {
      return context.method.invoke(context.target, context.parameters);
   }
}
