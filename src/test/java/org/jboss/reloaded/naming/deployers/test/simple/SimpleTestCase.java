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
import org.jboss.deployers.spi.attachments.MutableAttachments;
import org.jboss.deployers.spi.structure.StructureMetaData;
import org.jboss.deployers.spi.structure.StructureMetaDataFactory;
import org.jboss.deployers.vfs.plugins.client.AbstractVFSDeployment;
import org.jboss.deployers.vfs.spi.client.VFSDeployment;
import org.jboss.kernel.Kernel;
import org.jboss.kernel.plugins.bootstrap.AbstractBootstrap;
import org.jboss.kernel.plugins.bootstrap.basic.BasicBootstrap;
import org.jboss.kernel.plugins.deployment.xml.BasicXMLDeployer;
import org.jboss.kernel.spi.deployment.KernelDeployment;
import org.jboss.metadata.ear.jboss.JBossAppMetaData;
import org.jboss.reloaded.naming.deployers.test.common.AbstractNamingDeployersTestCase;
import org.jboss.reloaded.naming.deployers.test.common.DummiesMetaData;
import org.jboss.reloaded.naming.deployers.test.common.DummyContainer;
import org.jboss.reloaded.naming.service.NameSpaces;
import org.jboss.util.naming.Util;
import org.jboss.virtual.AssembledDirectory;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class SimpleTestCase extends AbstractNamingDeployersTestCase
{
   @Test
   public void test1() throws Exception
   {
      AssembledDirectory root1 = AssembledDirectory.createAssembledDirectory("test1", "test1.ear");
      VFSDeployment deployment1 = new AbstractVFSDeployment(root1);
      ((MutableAttachments) deployment1.getPredeterminedManagedObjects()).addAttachment(JBossAppMetaData.class, new JBossAppMetaData());
      mainDeployer.deploy(deployment1);

      AssembledDirectory root2 = AssembledDirectory.createAssembledDirectory("test2", "test2.ear");
      AssembledDirectory moduleA = root2.mkdir("moduleA.jar");
      VFSDeployment deployment2 = new AbstractVFSDeployment(root2);
      ((MutableAttachments) deployment2.getPredeterminedManagedObjects()).addAttachment(JBossAppMetaData.class, new JBossAppMetaData());
      mainDeployer.deploy(deployment2);

      InitialContext ctx = new InitialContext();
      NameSpaces nameSpaces = getBean("NameSpaces", ControllerState.INSTALLED, NameSpaces.class);
      assertNotNull(nameSpaces.getGlobalContext());
      // basically the lookup is what really checks the functionality, not null is a bonus
      assertNotNull(ctx.lookup("java:global"));
      assertNotNull(ctx.lookup("java:global/test1"));
      assertNotNull(ctx.lookup("java:global/test2"));
      assertNotNull(ctx.lookup("java:global/test2/moduleA"));

      mainDeployer.undeploy(deployment1);
      mainDeployer.undeploy(deployment2);

      assertNameNotFound("java:global/test1");
      assertNameNotFound("java:global/test2");
   }

   @Test
   public void testComponents() throws Exception
   {
      AssembledDirectory root = AssembledDirectory.createAssembledDirectory("components", "components.jar");
      VFSDeployment deployment = new AbstractVFSDeployment(root);
      //((MutableAttachments) deployment.getPredeterminedManagedObjects()).addAttachment(DummiesMetaData.class, DummiesMetaData.create("A", "B"));
      ((MutableAttachments) deployment.getPredeterminedManagedObjects()).addAttachment(DummiesMetaData.class, DummiesMetaData.create("A", "B"));
      mainDeployer.deploy(deployment);

      // basically the lookup is what really checks the functionality, not null is a bonus
      assertNotNull(ctx.lookup("java:global"));
      Context c = (Context) ctx.lookup("java:global/components");
      assertNotNull(c);

      Util.bind(c, "test", "Hello world");

      DummyContainer containerA = (DummyContainer) ctx.lookup("java:global/components/A");

      String result = (String) containerA.getValue("java:module/test");
      assertEquals("Hello world", result);

      DummyContainer containerB = (DummyContainer) ctx.lookup("java:global/components/B");

      containerA.setValue("java:comp/local", "a local value");
      try
      {
         containerB.getValue("java:comp/local");
      }
      catch(RuntimeException e)
      {
         // good
         // TODO: should be a NameNotFoundException
      }

      mainDeployer.undeploy(deployment);

      assertNameNotFound("java:global/components");
   }

   @Test
   public void testPath() throws Exception
   {
      AssembledDirectory root = AssembledDirectory.createAssembledDirectory("testPath", "testPath.ear");
      AssembledDirectory path = root.mkdir("path");
      AssembledDirectory moduleA = path.mkdir("modulePath.jar");
      VFSDeployment deployment = new AbstractVFSDeployment(root);
      // so we don't need an EAR structure deployer
      StructureMetaData smd = StructureMetaDataFactory.createStructureMetaData();
      smd.addContext(StructureMetaDataFactory.createContextInfo("path/modulePath.jar"));
      ((MutableAttachments) deployment.getPredeterminedManagedObjects()).addAttachment(StructureMetaData.class, smd);
      ((MutableAttachments) deployment.getPredeterminedManagedObjects()).addAttachment(JBossAppMetaData.class, new JBossAppMetaData());
      mainDeployer.deploy(deployment);

      // basically the lookup is what really checks the functionality, not null is a bonus
      assertNotNull(ctx.lookup("java:global"));
      assertNotNull(ctx.lookup("java:global/testPath/path/modulePath"));

      mainDeployer.undeploy(deployment);

      assertNameNotFound("java:global/testPath/path/modulePath");
   }

   @Test
   public void testStandaloneModule() throws Exception
   {
      AssembledDirectory root = AssembledDirectory.createAssembledDirectory("standalone", "standalone.jar");
      VFSDeployment deployment = new AbstractVFSDeployment(root);
      mainDeployer.deploy(deployment);

      // basically the lookup is what really checks the functionality, not null is a bonus
      assertNotNull(ctx.lookup("java:global"));
      assertNotNull(ctx.lookup("java:global/standalone"));

      mainDeployer.undeploy(deployment);

      assertNameNotFound("java:global/standalone");
   }

   // make sure we don't undeploy the wrong module (because of BeanMetaDataDeployer.undeploy ignoring scope)
   @Test
   public void testStandaloneModule2() throws Exception
   {
      AssembledDirectory root1 = AssembledDirectory.createAssembledDirectory("standalone1", "standalone1.jar");
      VFSDeployment deployment1 = new AbstractVFSDeployment(root1);
      mainDeployer.deploy(deployment1);

      AssembledDirectory root2 = AssembledDirectory.createAssembledDirectory("standalone2", "standalone2.jar");
      VFSDeployment deployment2 = new AbstractVFSDeployment(root2);
      mainDeployer.deploy(deployment2);

      // basically the lookup is what really checks the functionality, not null is a bonus
      assertNotNull(ctx.lookup("java:global/standalone1"));
      assertNotNull(ctx.lookup("java:global/standalone2"));

      mainDeployer.undeploy(deployment2);

//      assertNotNull(ctx.lookup("java:global/standalone1"));
//      assertNameNotFound("java:global/standalone2");

      mainDeployer.deploy(deployment2);

      assertNotNull(ctx.lookup("java:global/standalone1"));
      assertNotNull(ctx.lookup("java:global/standalone2"));
      
      mainDeployer.undeploy(deployment1);
      mainDeployer.undeploy(deployment2);
   }
}
