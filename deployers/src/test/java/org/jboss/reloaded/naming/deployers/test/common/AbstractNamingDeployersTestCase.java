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

import org.jboss.dependency.spi.ControllerContext;
import org.jboss.dependency.spi.ControllerMode;
import org.jboss.dependency.spi.ControllerState;
import org.jboss.deployers.client.spi.main.MainDeployer;
import org.jboss.kernel.Kernel;
import org.jboss.kernel.plugins.bootstrap.AbstractBootstrap;
import org.jboss.kernel.plugins.bootstrap.basic.BasicBootstrap;
import org.jboss.kernel.plugins.deployment.xml.BasicXMLDeployer;
import org.jboss.kernel.spi.deployment.KernelDeployment;
import org.junit.BeforeClass;

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import java.net.URL;

import static org.junit.Assert.fail;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public abstract class AbstractNamingDeployersTestCase
{
   protected static Kernel kernel;
   private static BasicXMLDeployer deployer;
   protected static MainDeployer mainDeployer;
   protected static InitialContext ctx;

   protected static final void assertNameNotFound(String name) throws NamingException
   {
      try
      {
         ctx.lookup(name);
         fail("Expected NameNotFoundException for " + name);
      }
      catch(NameNotFoundException e)
      {
         // good
      }
   }

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      AbstractBootstrap bootstrap = new BasicBootstrap();
      bootstrap.run();
      kernel = bootstrap.getKernel();
      deployer = new BasicXMLDeployer(kernel, ControllerMode.AUTOMATIC);

      ClassLoader cl = AbstractNamingDeployersTestCase.class.getClassLoader();
      deploy(cl, "classloader.xml");
      deploy(cl, "deployers.xml");

      mainDeployer = getBean("MainDeployer", ControllerState.INSTALLED, MainDeployer.class);

      deploy(cl, "jndi-beans.xml");
      deploy(cl, "reloaded-naming-deployers-beans.xml");

      deploy(cl, "dummy-deployers-beans.xml");

      ctx = new InitialContext();
   }

   protected static <T> T getBean(String name, ControllerState state, Class<T> expectedType)
   {
      ControllerContext context = kernel.getController().getContext(name, state);
      return expectedType.cast(context.getTarget());
   }
   
   protected static KernelDeployment deploy(URL url) throws Exception
   {
      try
      {
         return deployer.deploy(url);
      }
      catch(Throwable t)
      {
         if(t instanceof Error)
            throw (Error) t;
         if(t instanceof RuntimeException)
            throw (RuntimeException) t;
         throw (Exception) t;
      }
   }

   public static KernelDeployment deploy(ClassLoader cl, String resource) throws Exception
   {
      URL url = cl.getResource(resource);
      if(url == null)
         throw new IllegalArgumentException("Can't find resource '" + resource + "'");
      try
      {
         return deploy(url);
      }
      finally
      {
         validate();
      }
   }

   protected static void validate() throws Exception
   {
      try
      {
         deployer.validate();
      }
      catch(Throwable t)
      {
         if(t instanceof Error)
            throw (Error) t;
         if(t instanceof RuntimeException)
            throw (RuntimeException) t;
         throw (Exception) t;
      }
   }
}
