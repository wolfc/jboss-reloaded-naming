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
package org.jboss.reloaded.naming.deployers.javaee;

import org.jboss.deployers.structure.spi.DeploymentUnit;

/**
 * Obtain information about a JavaEE application given a deployment unit.
 * The informer should only use meta data to obtain the information
 * being asked.
 *
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public interface JavaEEApplicationInformer extends DeploymentUnitInformer
{
   /**
    * Obtain the name of the JavaEE application for this deployment unit.
    *
    * @param deploymentUnit the deployment unit of the application
    * @return the name of the JavaEE application
    * @throws IllegalArgumentException if the deployment unit is not a JavaEE application
    */
   String getApplicationName(DeploymentUnit deploymentUnit) throws IllegalArgumentException;

   /**
    * Determine whether a deployment unit is a JavaEE application.
    * 
    * @param deploymentUnit the deployment unit to analyze
    * @return true if the deployment unit is a JavaEE application, false otherwise
    */
   boolean isJavaEEApplication(DeploymentUnit deploymentUnit);
}
