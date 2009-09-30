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
package org.jboss.reloaded.naming.test.legacy.unit;

import static junit.framework.Assert.assertEquals;

import javax.naming.Context;
import javax.naming.NamingException;

import junit.framework.Assert;

import org.jboss.naming.ENCFactory;
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
public class LegacyTestCase extends AbstractNamingTestCase
{
   @Test
   public void test1() throws Exception
   {
      Context context;
      ENCFactory.pushContextId("component");
      try
      {
         context = (Context) iniCtx.lookup("java:comp");
      }
      finally
      {
         ENCFactory.popContextId();
      }
      Util.rebind(context, "env/value", "Hello LegacyTestCase");
      
      ENCFactory.pushContextId("component");
      try
      {
         String value = (String) iniCtx.lookup("java:comp/env/value");
         Assert.assertEquals("Hello LegacyTestCase", value);
      }
      finally
      {
         ENCFactory.popContextId();
      }
   }
   
   @Test
   public void testLegacyCallingNew() throws NamingException
   {
      final String newValue = "testNewCallingLegacy new";
      final String legacyValue = "testNewCallingLegacy legacy";
      
      JavaEEModule module = new SimpleJavaEEModule("module", createContext(iniCtx.getEnvironment()), null);
      JavaEEComponent component = new SimpleJavaEEComponent("new", createContext(iniCtx.getEnvironment()), module);
      
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
      
      JavaEEModule module = new SimpleJavaEEModule("module", createContext(iniCtx.getEnvironment()), null);
      JavaEEComponent component = new SimpleJavaEEComponent("new", createContext(iniCtx.getEnvironment()), module);
      
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
