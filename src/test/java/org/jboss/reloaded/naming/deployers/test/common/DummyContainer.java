/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.reloaded.naming.deployers.test.common;

import org.jboss.logging.Logger;
import org.jboss.reloaded.naming.CurrentComponent;
import org.jboss.reloaded.naming.spi.JavaEEComponent;
import org.jboss.util.naming.NonSerializableFactory;
import org.jboss.util.naming.Util;

import javax.naming.InitialContext;
import java.util.concurrent.Callable;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class DummyContainer
{
   private static Logger log = Logger.getLogger(DummyContainer.class);
   
   private final JavaEEComponent component;
   private InitialContext ctx;

   public DummyContainer(JavaEEComponent component)
   {
      this.component = component;
   }

   private <R> R around(Callable<R> callable)
   {
      CurrentComponent.push(component);
      try
      {
         return callable.call();
      }
      catch(Exception e)
      {
         if(e instanceof RuntimeException)
            throw (RuntimeException) e;
         throw new RuntimeException(e);
      }
      finally
      {
         CurrentComponent.pop();
      }
   }

   public Object getValue(final String name)
   {
      return around(new Callable<Object>() {
         public Object call() throws Exception
         {
            return ctx.lookup(name);
         }
      });
   }

   public void setValue(final String name, final String value)
   {
      around(new Callable<Void>() {
         public Void call() throws Exception
         {
            Util.bind(ctx, name, value);
            return null;
         }
      });
   }

   public void start() throws Exception
   {
      ctx = new InitialContext();

      // normally done somewhere else
      NonSerializableFactory.rebind(component.getModule().getContext(), component.getName(), this);
      
      log.info("Started container " + this + " with java:comp context " + component.getContext());
   }

   public void stop() throws Exception
   {
      Util.unbind(component.getModule().getContext(), component.getName());

      if(ctx != null)
         ctx.close();
      ctx = null;

      log.info("Stopped container " + this);
   }

   @Override
   public String toString()
   {
      return "DummyContainer{" +
         "component=" + component +
         '}';
   }
}
