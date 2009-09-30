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
package org.jboss.reloaded.naming.test.simple.unit;

import static junit.framework.Assert.assertEquals;

import org.jboss.reloaded.naming.CurrentComponent;
import org.jboss.reloaded.naming.simple.SimpleJavaEEComponent;
import org.jboss.reloaded.naming.simple.SimpleJavaEEModule;
import org.jboss.reloaded.naming.spi.JavaEEComponent;
import org.jboss.reloaded.naming.spi.JavaEEModule;
import org.jboss.reloaded.naming.test.common.AbstractNamingTestCase;
import org.jboss.util.naming.Util;
import org.junit.Test;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class SimpleTestCase extends AbstractNamingTestCase
{
   @Test
   public void test1() throws Exception
   {
      JavaEEModule module = new SimpleJavaEEModule("module", createContext(iniCtx.getEnvironment()), null);
      JavaEEComponent component = new SimpleJavaEEComponent("component", createContext(iniCtx.getEnvironment()), module);
      
      Util.rebind(component.getContext(), "env/value", "Hello world");
      
      CurrentComponent.push(component);
      try
      {
         String value = (String) iniCtx.lookup("java:comp/env/value");
         assertEquals("Hello world", value);
      }
      finally
      {
         CurrentComponent.pop();
      }
   }
}
