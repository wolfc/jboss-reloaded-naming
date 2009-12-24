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
package org.jboss.reloaded.naming.deployers;

import org.jboss.beans.metadata.plugins.builder.BeanMetaDataBuilderFactory;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.AbstractRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.metadata.ear.spec.EarMetaData;
import org.jboss.metadata.plugins.scope.ApplicationScope;
import org.jboss.reloaded.naming.deployers.mc.MCJavaEEApplication;

import static org.jboss.reloaded.naming.deployers.util.AnnotationHelper.annotation;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class AppNamingDeployer extends AbstractRealDeployer
{
   public AppNamingDeployer()
   {
      setInputs(EarMetaData.class);
      setOutputs("java:app");
      setOutput(BeanMetaData.class);
   }

   @Override
   protected void internalDeploy(DeploymentUnit deploymentUnit) throws DeploymentException
   {
      if(!isJavaEEApplication(deploymentUnit))
         return;

      String appName = getAppName(deploymentUnit);
      BeanMetaDataBuilder builder = BeanMetaDataBuilderFactory.createBuilder("java:app", MCJavaEEApplication.class.getName())
         .addAnnotation(annotation(ApplicationScope.class, appName))
         .addConstructorParameter(String.class.getName(), appName);
      builder.addPropertyMetaData("nameSpaces", builder.createInject("NameSpaces"));
      deploymentUnit.addAttachment(BeanMetaData.class, builder.getBeanMetaData());
   }

   protected boolean isJavaEEApplication(DeploymentUnit deploymentUnit)
   {
      return deploymentUnit.getSimpleName().endsWith(".ear") || deploymentUnit.getAttachment(EarMetaData.class) != null;
   }

   protected String getAppName(DeploymentUnit deploymentUnit)
   {
      String name = deploymentUnit.getSimpleName();
      if(name.endsWith(".ear"))
         return name.substring(0, name.length() - 4);
      return name;
   }
}
