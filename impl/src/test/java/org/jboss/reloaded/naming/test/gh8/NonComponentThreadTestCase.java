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
package org.jboss.reloaded.naming.test.gh8;

import org.jboss.reloaded.naming.CurrentComponent;
import org.jboss.reloaded.naming.spi.JavaEEComponent;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class NonComponentThreadTestCase
{
   @Test
   public void testNPE() throws Exception
   {
      // associate the current thread
      JavaEEComponent current = CurrentComponent.get();
      assertNull(current);
      ExecutorService service = Executors.newSingleThreadExecutor();
      Callable<String> task = new Callable<String>() {

         public String call() throws Exception
         {
            JavaEEComponent current = CurrentComponent.get();
            assertNull(current);
            return (String) "done";
         }
      };
      Future<String> future = service.submit(task);
      String result = future.get();
      assertEquals("done", result);
   }
}
