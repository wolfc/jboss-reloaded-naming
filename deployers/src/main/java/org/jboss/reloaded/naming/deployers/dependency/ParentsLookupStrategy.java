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
package org.jboss.reloaded.naming.deployers.dependency;

import org.jboss.dependency.plugins.AbstractController;
import org.jboss.dependency.plugins.graph.AbstractLookupStrategy;
import org.jboss.dependency.spi.ControllerContext;
import org.jboss.dependency.spi.ControllerState;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class ParentsLookupStrategy extends AbstractLookupStrategy
{
   @Override
   protected ControllerContext getContextInternal(AbstractController controller, Object name, ControllerState state)
   {
      ControllerContext context = controller.getContext(name, state);
      if(context != null)
         return context;
      AbstractController parent = controller.getParentController();
      if (parent != null)
         return getContextInternal(parent, name, state);
      else
         return null;
   }
}
