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

import org.jboss.deployers.vfs.spi.deployer.AbstractVFSParsingDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.virtual.VirtualFile;

import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class DummiesSerializedMetaDataDeployer extends AbstractVFSParsingDeployer<DummiesMetaData>
{
   public DummiesSerializedMetaDataDeployer()
   {
      super(DummiesMetaData.class);
      setSuffix("dummy-components.ser");
   }

   @Override
   protected DummiesMetaData parse(VFSDeploymentUnit unit, VirtualFile file, DummiesMetaData root) throws Exception
   {
      InputStream in = openStreamAndValidate(file);
      try
      {
         ObjectInputStream ois = new ObjectInputStream(in);
         return (DummiesMetaData) ois.readObject();
      }
      finally
      {
         in.close();
      }
   }
}
