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

import org.jboss.beans.metadata.plugins.AbstractInjectionValueMetaData;
import org.jboss.beans.metadata.plugins.builder.BeanMetaDataBuilderFactory;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.dependency.plugins.graph.Search;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.AbstractSimpleRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.metadata.plugins.scope.ApplicationScope;
import org.jboss.metadata.plugins.scope.DeploymentScope;
import org.jboss.metadata.plugins.scope.InstanceScope;
import org.jboss.reloaded.naming.deployers.javaee.JavaEEComponentInformer;
import org.jboss.reloaded.naming.spi.JavaEEComponent;

import static org.jboss.reloaded.naming.deployers.util.AnnotationHelper.annotation;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class DummyDeployer extends AbstractSimpleRealDeployer<DummyMetaData>
{
   private JavaEEComponentInformer informer;
   
   public DummyDeployer()
   {
      super(DummyMetaData.class);
      setOutput(BeanMetaData.class);
      setComponentsOnly(true);
      //setUseUnitName(true);
   }

   @Override
   public void deploy(DeploymentUnit unit, DummyMetaData deployment) throws DeploymentException
   {
      String appName = informer.getApplicationName(unit);
      String moduleName = informer.getModulePath(unit);
      String componentName = informer.getComponentName(unit);

      // create dummy container bean
      String name = "jboss.dummy:";
      if(appName != null)
         name += "application=" + appName + ",";
      name += "module=" + moduleName + ",component=" + componentName;
      BeanMetaDataBuilder builder = BeanMetaDataBuilderFactory.createBuilder(name, DummyContainer.class.getName())
         .addAnnotation(annotation(DeploymentScope.class, moduleName))
         .addAnnotation(annotation(InstanceScope.class, componentName))
         .addAlias(componentName);
      if(appName != null)
         builder.addAnnotation(annotation(ApplicationScope.class, appName));
      AbstractInjectionValueMetaData javaComponent = new AbstractInjectionValueMetaData("java:comp");
      javaComponent.setSearch(Search.LOCAL);
      builder.addConstructorParameter(JavaEEComponent.class.getName(), javaComponent);

      unit.getParent().addAttachment(BeanMetaData.class.getName() + "." + name, builder.getBeanMetaData());
      //unit.addAttachment(BeanMetaData.class, builder.getBeanMetaData());
   }

   public void setJavaEEComponentInformer(JavaEEComponentInformer informer)
   {
      this.informer = informer;
   }
}
