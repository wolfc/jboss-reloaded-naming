/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2009, Red Hat Middleware LLC, and individual contributors
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

package org.jboss.reloaded.naming.deployers.test.simple;

import org.jboss.dependency.spi.ControllerContext;
import org.jboss.dependency.spi.ControllerMode;
import org.jboss.dependency.spi.ControllerState;
import org.jboss.deployers.client.spi.main.MainDeployer;
import org.jboss.deployers.vfs.plugins.client.AbstractVFSDeployment;
import org.jboss.deployers.vfs.spi.client.VFSDeployment;
import org.jboss.kernel.Kernel;
import org.jboss.kernel.plugins.bootstrap.AbstractBootstrap;
import org.jboss.kernel.plugins.bootstrap.basic.BasicBootstrap;
import org.jboss.kernel.plugins.deployment.xml.BasicXMLDeployer;
import org.jboss.kernel.spi.deployment.KernelDeployment;
import org.jboss.reloaded.naming.service.NameSpaces;
import org.jboss.virtual.AssembledDirectory;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.naming.InitialContext;
import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class SimpleTestCase
{
   private static Kernel kernel;
   private static BasicXMLDeployer deployer;

   @BeforeClass
   public static void beforeClass()
   {
      AbstractBootstrap bootstrap = new BasicBootstrap();
      bootstrap.run();
      kernel = bootstrap.getKernel();
      deployer = new BasicXMLDeployer(kernel, ControllerMode.AUTOMATIC);
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

   @Test
   public void test1() throws Exception
   {
      deploy(getClass().getClassLoader(), "classloader.xml");
      deploy(getClass().getClassLoader(), "deployers.xml");

      MainDeployer mainDeployer = getBean("MainDeployer", ControllerState.INSTALLED, MainDeployer.class);

      deploy(getClass().getClassLoader(), "jndi-beans.xml");
      deploy(getClass().getClassLoader(), "reloaded-naming-deployers-beans.xml");

      {
         AssembledDirectory root = AssembledDirectory.createAssembledDirectory("test1", "test1.ear");
         VFSDeployment deployment = new AbstractVFSDeployment(root);
         mainDeployer.deploy(deployment);
      }
      {
         AssembledDirectory root = AssembledDirectory.createAssembledDirectory("test2", "test2.ear");
         AssembledDirectory moduleA = root.mkdir("moduleA.jar");
         VFSDeployment deployment = new AbstractVFSDeployment(root);
         mainDeployer.deploy(deployment);
      }

      InitialContext ctx = new InitialContext();
      NameSpaces nameSpaces = getBean("NameSpaces", ControllerState.INSTALLED, NameSpaces.class);
      System.out.println(nameSpaces.getGlobalContext());
      System.out.println(ctx.lookup("java:global"));
      System.out.println(ctx.lookup("java:global/test1"));
      System.out.println(ctx.lookup("java:global/test2"));
      System.out.println(ctx.lookup("java:global/test2/moduleA"));
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
