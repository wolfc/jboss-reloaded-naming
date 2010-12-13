/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.reloaded.naming.test.legacy.unit;

import org.jboss.reloaded.naming.CurrentComponent;
import org.jboss.reloaded.naming.simple.SimpleJavaEEComponent;
import org.jboss.reloaded.naming.spi.JavaEEComponent;
import org.jboss.reloaded.naming.spi.JavaEEModule;
import org.jboss.reloaded.naming.test.common.AbstractNamingTestCase;
import org.jboss.util.naming.Util;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.naming.NamingException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class ThreadedLegacyTestCase extends AbstractNamingTestCase
{
   private JavaEEModule module;

   @After
   public void after() throws NamingException
   {
      javaGlobal.unbind(module.getName());
   }

   @Before
   public void before() throws NamingException
   {
      this.module = createStandaloneModule("module");
   }

   @Test
   public void test1() throws Exception
   {
      ExecutorService service = Executors.newSingleThreadExecutor();
      JavaEEComponent component = new SimpleJavaEEComponent("nolegacy", createContext(iniCtx.getEnvironment()), module);
      Util.rebind(component.getContext(), "env/test", "Hello world");
      CurrentComponent.push(component);
      try
      {
         Callable<String> task = new Callable<String>() {

            public String call() throws Exception
            {
               return (String) iniCtx.lookup("java:comp/env/test");               
            }
         };
         Future<String> future = service.submit(task);
         String result = future.get();
         assertEquals("Hello world", result);
      }
      finally
      {
         CurrentComponent.pop();
      }
   }
}
