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
package org.jboss.reloaded.naming.test.legacy.unit;

import org.jboss.naming.ENCFactory;
import org.jboss.reloaded.naming.CurrentComponent;
import org.jboss.reloaded.naming.simple.SimpleJavaEEComponent;
import org.jboss.reloaded.naming.spi.JavaEEComponent;
import org.jboss.reloaded.naming.spi.JavaEEModule;
import org.jboss.reloaded.naming.test.common.AbstractNamingTestCase;
import org.jboss.util.naming.Util;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.NamingException;

import static junit.framework.Assert.assertEquals;

/**
 * Test the interaction between the legacy java:comp (ENCFactory) and the new
 * one (CurrentComponent).
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class LegacyAndNewTestCase extends AbstractNamingTestCase
{
   private JavaEEComponent component;

   @After
   public void after() throws NamingException
   {
      javaGlobal.unbind(component.getModule().getName());
   }

   @Before
   public void before() throws NamingException
   {
      JavaEEModule module = createStandaloneModule("module");
      component = new SimpleJavaEEComponent("new", createContext(iniCtx.getEnvironment()), module);
   }

   @Test
   public void testLegacyCallingNew() throws NamingException
   {
      final String newValue = "testNewCallingLegacy new";
      final String legacyValue = "testNewCallingLegacy legacy";
      
      Util.rebind(component.getContext(), "env/value", newValue);

      ENCFactory.pushContextId("legacy");
      try
      {
         Context ctx = (Context) iniCtx.lookup("java:comp");
         Util.rebind(ctx, "env/value", legacyValue);
      }
      finally
      {
         ENCFactory.popContextId();
      }
      
      ENCFactory.pushContextId("legacy");
      try
      {
         String value = (String) iniCtx.lookup("java:comp/env/value");
         assertEquals(legacyValue, value);
         
         CurrentComponent.push(component);
         try
         {
            value = (String) iniCtx.lookup("java:comp/env/value");
            assertEquals(newValue, value);
         }
         finally
         {
            CurrentComponent.pop();
         }
         
         value = (String) iniCtx.lookup("java:comp/env/value");
         assertEquals(legacyValue, value);
      }
      finally
      {
         ENCFactory.popContextId();
      }
   }
   
   @Test
   public void testNewCallingLegacy() throws NamingException
   {
      final String newValue = "testNewCallingLegacy new";
      final String legacyValue = "testNewCallingLegacy legacy";
      
      Util.rebind(component.getContext(), "env/value", newValue);

      ENCFactory.pushContextId("legacy");
      try
      {
         Context ctx = (Context) iniCtx.lookup("java:comp");
         Util.rebind(ctx, "env/value", legacyValue);
      }
      finally
      {
         ENCFactory.popContextId();
      }
      
      CurrentComponent.push(component);
      try
      {
         String value = (String) iniCtx.lookup("java:comp/env/value");
         assertEquals(newValue, value);
         
         ENCFactory.pushContextId("legacy");
         try
         {
            value = (String) iniCtx.lookup("java:comp/env/value");
            assertEquals(legacyValue, value);
         }
         finally
         {
            ENCFactory.popContextId();
         }
         
         value = (String) iniCtx.lookup("java:comp/env/value");
         assertEquals(newValue, value);
      }
      finally
      {
         CurrentComponent.pop();
      }
   }
}
