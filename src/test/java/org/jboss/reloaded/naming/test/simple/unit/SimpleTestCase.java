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

import org.jboss.reloaded.naming.CurrentComponent;
import org.jboss.reloaded.naming.simple.SimpleJavaEEApplication;
import org.jboss.reloaded.naming.simple.SimpleJavaEEComponent;
import org.jboss.reloaded.naming.simple.SimpleJavaEEModule;
import org.jboss.reloaded.naming.spi.JavaEEApplication;
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
import static org.jboss.util.naming.Util.rebind;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class SimpleTestCase extends AbstractNamingTestCase
{
   private JavaEEModule module;
   private JavaEEComponent component;
   
   protected static JavaEEApplication createApplication(String appName) throws NamingException
   {
      Context context = javaGlobal.createSubcontext(appName);
      JavaEEApplication application = new SimpleJavaEEApplication(appName, context);
      return application;
   }

   protected static JavaEEComponent createComponent(JavaEEModule module, String componentName) throws NamingException
   {
      Context context = createContext(module.getContext().getEnvironment());
      JavaEEComponent component = new SimpleJavaEEComponent(componentName, context, module);
      return component;
   }

   protected static JavaEEModule createModule(JavaEEApplication app, String moduleName) throws NamingException
   {
      Context context = app.getContext().createSubcontext(moduleName);
      JavaEEModule module = new SimpleJavaEEModule(moduleName, context, app);
      return module;
   }

   @After
   public void after() throws NamingException
   {
      javaGlobal.unbind("a_module");
   }
   
   @Before
   public void before() throws NamingException
   {
      module = createStandaloneModule("a_module");
      component = createComponent(module, "a_component");

      Util.rebind(component.getContext(), "env/value", "Hello world");
   }

   @Test
   public void testApp() throws Exception
   {
      JavaEEApplication app = createApplication("an_app");
      JavaEEModule moduleB = createModule(app, "moduleB");
      JavaEEComponent component1 = createComponent(moduleB, "component1");
      JavaEEModule moduleC = createModule(app, "moduleC");
      JavaEEComponent component2 = createComponent(moduleB, "component2");

      rebind(app.getContext(), "env/value", "sharedInApp");

      CurrentComponent.push(component1);
      try
      {
         String value = (String) iniCtx.lookup("java:app/env/value");
         assertEquals("sharedInApp", value);
      }
      finally
      {
         CurrentComponent.pop();
      }

      CurrentComponent.push(component2);
      try
      {
         String value = (String) iniCtx.lookup("java:app/env/value");
         assertEquals("sharedInApp", value);
      }
      finally
      {
         CurrentComponent.pop();
      }
   }

   @Test
   public void testComponent() throws Exception
   {
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

   @Test
   public void testGlobal() throws Exception
   {
      rebind(javaGlobal, "env/value", "testGlobal");
      String value = (String) iniCtx.lookup("java:global/env/value");
      assertEquals("testGlobal", value);
   }

   @Test
   public void testModule() throws Exception
   {
      JavaEEComponent componentB = createComponent(module, "b_component");

      rebind(module.getContext(), "env/value", "sharedInModule");

      CurrentComponent.push(component);
      try
      {
         String value = (String) iniCtx.lookup("java:module/env/value");
         assertEquals("sharedInModule", value);
      }
      finally
      {
         CurrentComponent.pop();
      }

      CurrentComponent.push(componentB);
      try
      {
         String value = (String) iniCtx.lookup("java:module/env/value");
         assertEquals("sharedInModule", value);
      }
      finally
      {
         CurrentComponent.pop();
      }
   }
}
