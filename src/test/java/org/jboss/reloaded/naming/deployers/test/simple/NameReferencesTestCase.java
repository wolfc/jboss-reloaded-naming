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
package org.jboss.reloaded.naming.deployers.test.simple;

import org.jboss.deployers.spi.attachments.MutableAttachments;
import org.jboss.deployers.vfs.spi.client.VFSDeployment;
import org.jboss.deployers.vfs.spi.client.VFSDeploymentFactory;
import org.jboss.metadata.ear.jboss.JBossAppMetaData;
import org.jboss.reloaded.naming.deployers.test.common.AbstractNamingDeployersTestCase;
import org.jboss.reloaded.naming.deployers.test.common.DummiesMetaData;
import org.jboss.reloaded.naming.deployers.test.common.DummyContainer;
import org.jboss.virtual.AssembledDirectory;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertEquals;

/**
 * JavaEE 6 FR 5.15:
 * A component may access the name of the current application using the pre-defined
 * JNDI name java:app/AppName. A component may access the name of the current
 * module using the pre-defined JNDI name java:module/ModuleName. Both of these
 * names are represented by String objects.
 * 
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class NameReferencesTestCase extends AbstractNamingDeployersTestCase
{
   protected static DummiesMetaData components(String... names)
   {
      return DummiesMetaData.create(names);
   }

   protected static byte[] serialized(Object obj) throws IOException
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(obj);
      oos.flush();
      return baos.toByteArray();
   }

   @Test
   public void testAppName() throws Exception
   {
      AssembledDirectory root = AssembledDirectory.createAssembledDirectory("testApp", "testApp.ear");
      AssembledDirectory moduleA = root.mkdir("moduleA.jar");
      moduleA.mkdir("META-INF").addBytes(serialized(components("component1")), "dummy-components.ser");
      VFSDeployment deployment = VFSDeploymentFactory.getInstance().createVFSDeployment(root);
      ((MutableAttachments) deployment.getPredeterminedManagedObjects()).addAttachment(JBossAppMetaData.class, new JBossAppMetaData());

      mainDeployer.deploy(deployment);

      DummyContainer containerA = (DummyContainer) ctx.lookup("java:global/testApp/moduleA/component1");

      String appName = (String) containerA.getValue("java:app/AppName");

      assertEquals("testApp", appName);

      mainDeployer.undeploy(deployment);
   }


   @Test
   public void testModuleName() throws Exception
   {
      AssembledDirectory root = AssembledDirectory.createAssembledDirectory("testApp", "testApp.ear");
      AssembledDirectory moduleA = root.mkdir("moduleA.jar");
      moduleA.mkdir("META-INF").addBytes(serialized(components("component1")), "dummy-components.ser");
      VFSDeployment deployment = VFSDeploymentFactory.getInstance().createVFSDeployment(root);
      ((MutableAttachments) deployment.getPredeterminedManagedObjects()).addAttachment(JBossAppMetaData.class, new JBossAppMetaData());

      mainDeployer.deploy(deployment);

      DummyContainer containerA = (DummyContainer) ctx.lookup("java:global/testApp/moduleA/component1");

      String moduleName = (String) containerA.getValue("java:module/ModuleName");

      assertEquals("moduleA", moduleName);

      mainDeployer.undeploy(deployment);
   }
}
