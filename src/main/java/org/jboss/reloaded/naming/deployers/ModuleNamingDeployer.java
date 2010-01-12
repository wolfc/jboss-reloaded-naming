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

import org.jboss.beans.metadata.plugins.AbstractInjectionValueMetaData;
import org.jboss.beans.metadata.plugins.builder.BeanMetaDataBuilderFactory;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.AbstractRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.metadata.plugins.scope.ApplicationScope;
import org.jboss.metadata.plugins.scope.DeploymentScope;
import org.jboss.reloaded.naming.deployers.dependency.ParentsLookupStrategy;
import org.jboss.reloaded.naming.deployers.javaee.JavaEEModuleInformer;
import org.jboss.reloaded.naming.deployers.mc.MCJavaEEModule;
import org.jboss.reloaded.naming.spi.JavaEEApplication;

import static org.jboss.reloaded.naming.deployers.util.AnnotationHelper.annotation;

/**
 * The ModuleNamingDeployer installs a JavaEEModule MC bean under the name of java:module
 * within an application scope with the JavaEE application name and an deployment scope
 * with the JavaEE module name.
 *
 * The JavaEEApplication MC bean will take care of initiating the java:app name space.
 *
 * To work properly it needs a JavaEEApplicationInformer.
 *
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class ModuleNamingDeployer extends AbstractRealDeployer
{
   private JavaEEModuleInformer informer;

   public ModuleNamingDeployer(JavaEEModuleInformer informer)
   {
      this.informer = informer;
      setInputs(informer.getRequiredAttachments());
      addInput("java:app");
      setOutput(BeanMetaData.class);
   }

   @Override
   protected void internalDeploy(DeploymentUnit unit) throws DeploymentException
   {
      if(!isJavaEEModule(unit))
         return;
      
      // appName is either the name of the JavaEE application or null for a stand-alone JavaEE module
      String appName = informer.getApplicationName(unit);
      String moduleName = informer.getModulePath(unit);

      // create JavaEEModule bean
      String name = "jboss.naming:";
      if(appName != null)
         name += "application=" + appName + ",";
      name += "module=" + moduleName;
      BeanMetaDataBuilder builder = BeanMetaDataBuilderFactory.createBuilder(name, MCJavaEEModule.class.getName())
         .addAnnotation(annotation(DeploymentScope.class, moduleName))
         .addConstructorParameter(String.class.getName(), moduleName)
         .addAlias("java:module");
      if(appName != null)
      {
         builder.addAnnotation(annotation(ApplicationScope.class, appName));
         AbstractInjectionValueMetaData javaApp = new AbstractInjectionValueMetaData("java:app");
         javaApp.setSearch(new ParentsLookupStrategy());
         builder.addConstructorParameter(JavaEEApplication.class.getName(), javaApp);
      }
      else
         builder.addConstructorParameter(JavaEEApplication.class.getName(), (Object) null);
      builder.addPropertyMetaData("nameSpaces", builder.createInject("NameSpaces"));

      unit.addAttachment("java:module:" + BeanMetaData.class, builder.getBeanMetaData());
   }

   /**
    * Determine whether the given deployment unit is a JavaEE module which needs
    * a java:module name space.
    * @param unit
    * @return
    */
   protected boolean isJavaEEModule(DeploymentUnit unit)
   {
      // TODO: isn't this using too much inside information? It would be better to have the deployer execute explicitly on naming meta data.
      JavaEEModuleInformer.ModuleType type = informer.getModuleType(unit);
      return type != null && type != JavaEEModuleInformer.ModuleType.JAVA;
   }
}
