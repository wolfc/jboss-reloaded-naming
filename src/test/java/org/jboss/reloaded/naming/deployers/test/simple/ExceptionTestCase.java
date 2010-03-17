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

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.attachments.MutableAttachments;
import org.jboss.deployers.vfs.plugins.client.AbstractVFSDeployment;
import org.jboss.deployers.vfs.spi.client.VFSDeployment;
import org.jboss.reloaded.naming.deployers.test.common.AbstractNamingDeployersTestCase;
import org.jboss.reloaded.naming.deployers.test.common.DummiesMetaData;
import org.jboss.virtual.AssembledDirectory;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class ExceptionTestCase extends AbstractNamingDeployersTestCase
{
   @Test
   public void test1() throws Exception
   {
      AssembledDirectory root = AssembledDirectory.createAssembledDirectory("components", "components.jar");
      VFSDeployment deployment = new AbstractVFSDeployment(root);
      ((MutableAttachments) deployment.getPredeterminedManagedObjects()).addAttachment(DummiesMetaData.class, DummiesMetaData.create("A", "B"));
      ((MutableAttachments) deployment.getPredeterminedManagedObjects()).addAttachment(Exception.class, new Exception("fail"));
      try
      {
         mainDeployer.deploy(deployment);
         fail("Expected DeploymentException");
      }
      catch(DeploymentException e)
      {
         // good
      }

      AssembledDirectory root2 = AssembledDirectory.createAssembledDirectory("components2", "components2.jar");
      VFSDeployment deployment2 = new AbstractVFSDeployment(root2);
      ((MutableAttachments) deployment2.getPredeterminedManagedObjects()).addAttachment(DummiesMetaData.class, DummiesMetaData.create("A", "B"));
      mainDeployer.deploy(deployment2);
      
      mainDeployer.undeploy(deployment2);
   }
}
