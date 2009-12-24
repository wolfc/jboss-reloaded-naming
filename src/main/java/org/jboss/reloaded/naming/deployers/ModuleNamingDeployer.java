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

import org.jboss.beans.metadata.api.model.InjectOption;
import org.jboss.beans.metadata.plugins.AbstractInjectionValueMetaData;
import org.jboss.beans.metadata.plugins.builder.BeanMetaDataBuilderFactory;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.dependency.plugins.graph.ScopeKeyLookupStrategy;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.AbstractRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.metadata.plugins.scope.DeploymentScope;
import org.jboss.metadata.spi.scope.CommonLevels;
import org.jboss.metadata.spi.scope.ScopeKey;
import org.jboss.reloaded.naming.deployers.mc.MCJavaEEModule;
import org.jboss.reloaded.naming.spi.JavaEEApplication;

import static org.jboss.reloaded.naming.deployers.util.AnnotationHelper.annotation;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class ModuleNamingDeployer extends AbstractRealDeployer
{
   public ModuleNamingDeployer()
   {
      setInputs("java:app");
      setOutput(BeanMetaData.class);
   }

   // TODO: remove
   protected String getAppName(DeploymentUnit deploymentUnit)
   {
      String name = deploymentUnit.getTopLevel().getSimpleName();
      if(name.endsWith(".ear"))
         return name.substring(0, name.length() - 4);
      return name;
   }

   @Override
   protected void internalDeploy(DeploymentUnit unit) throws DeploymentException
   {
      if(!isJavaEEModule(unit))
         return;
      
      // TODO: a lot
      String appName = getAppName(unit);
      String name = unit.getSimpleName();
      name = name.substring(0, name.length() - 4);

      // create JavaEEModule bean
      BeanMetaDataBuilder builder = BeanMetaDataBuilderFactory.createBuilder("java:module", MCJavaEEModule.class.getName())
         .addAnnotation(annotation(DeploymentScope.class, name))
         .addConstructorParameter(String.class.getName(), name);
      AbstractInjectionValueMetaData javaApp = new AbstractInjectionValueMetaData("java:app");
      javaApp.setSearch(new ScopeKeyLookupStrategy(new ScopeKey(CommonLevels.APPLICATION, appName)));
      javaApp.setInjectionOption(InjectOption.OPTIONAL);
      builder.addConstructorParameter(JavaEEApplication.class.getName(), javaApp);
      builder.addPropertyMetaData("nameSpaces", builder.createInject("NameSpaces"));

      unit.addAttachment(BeanMetaData.class, builder.getBeanMetaData());
   }

   protected boolean isJavaEEModule(DeploymentUnit unit)
   {
      // TODO: a lot
      if(unit.getSimpleName().endsWith(".jar"))
         return true;
      return false;
   }
}
