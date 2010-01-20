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
package org.jboss.reloaded.naming.deployers;

import org.jboss.beans.metadata.plugins.AbstractInjectionValueMetaData;
import org.jboss.beans.metadata.plugins.builder.BeanMetaDataBuilderFactory;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.AbstractRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.metadata.plugins.scope.ApplicationScope;
import org.jboss.metadata.plugins.scope.DeploymentScope;
import org.jboss.metadata.plugins.scope.InstanceScope;
import org.jboss.reloaded.naming.deployers.dependency.ParentsLookupStrategy;
import org.jboss.reloaded.naming.deployers.javaee.JavaEEComponentInformer;
import org.jboss.reloaded.naming.deployers.mc.MCJavaEEComponent;
import org.jboss.reloaded.naming.spi.JavaEEModule;

import static org.jboss.reloaded.naming.deployers.util.AnnotationHelper.annotation;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class ComponentNamingDeployer extends AbstractRealDeployer
{
   private JavaEEComponentInformer informer;

   public ComponentNamingDeployer(JavaEEComponentInformer informer)
   {
      this.informer = informer;
      setInputs(informer.getRequiredAttachments());
      addInput("java:module");
      setOutput(BeanMetaData.class);
      // if we don't work on components only you'll see a duplicate install of java:module
      // because AbstractDeploymentUnit.getAttachments inherits attachments from the parent.
      setComponentsOnly(true);
   }

   @Override
   protected void internalDeploy(DeploymentUnit unit) throws DeploymentException
   {
      if(!informer.isJavaEEComponent(unit))
         return;

      String appName = informer.getApplicationName(unit);
      String moduleName = informer.getModulePath(unit);
      String componentName = informer.getComponentName(unit);

      // create JavaEEModule bean
      String name = "jboss.naming:";
      if(appName != null)
         name += "application=" + appName + ",";
      name += "module=" + moduleName + ",component=" + componentName;
      BeanMetaDataBuilder builder = BeanMetaDataBuilderFactory.createBuilder(name, MCJavaEEComponent.class.getName())
         .addAnnotation(annotation(DeploymentScope.class, moduleName))
         .addAnnotation(annotation(InstanceScope.class, componentName))
         .addConstructorParameter(String.class.getName(), componentName)
         .addAlias("java:comp");
      if(appName != null)
         builder.addAnnotation(annotation(ApplicationScope.class, appName));
      AbstractInjectionValueMetaData javaModule = new AbstractInjectionValueMetaData("java:module");
      javaModule.setSearch(new ParentsLookupStrategy());
      builder.addConstructorParameter(JavaEEModule.class.getName(), javaModule);
      builder.addPropertyMetaData("nameSpaces", builder.createInject("NameSpaces"));      

      // VDF can't do component composition, so each BMD must be in a separate component
      //DeploymentUnit component = unit.getParent().addComponent(componentName + ".java:comp");
      //component.addAttachment(BeanMetaData.class, builder.getBeanMetaData());
      // putting this into a separate component will make uninstall throw a fit, so lets put it in the parent with an unique name
      unit.getParent().addAttachment(BeanMetaData.class.getName() + "." + name, builder.getBeanMetaData());
   }

   @Override
   protected void internalUndeploy(DeploymentUnit unit)
   {
      if(!informer.isJavaEEComponent(unit))
         return;
      
      String name = informer.getComponentName(unit);
      unit.removeComponent(name + ".java:comp");
   }
}
