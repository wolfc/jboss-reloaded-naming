/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.reloaded.naming.simple;

import javax.naming.Context;

import org.jboss.reloaded.naming.spi.JavaEEComponent;
import org.jboss.reloaded.naming.spi.JavaEEModule;

/**
 * A simple implementation of {@link JavaEEComponent}.
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class SimpleJavaEEComponent implements JavaEEComponent
{
   private Context context;
   private JavaEEModule module;
   private String name;

   /**
    * Instantiate a {@link SimpleJavaEEComponent}.
    * 
    * @param name the name of the component
    * @param context the naming context of the component
    * @param module the module of which this component is part
    */
   public SimpleJavaEEComponent(String name, Context context, JavaEEModule module)
   {
      assert name != null : "name is null";
      assert context != null : "context is null";
      assert module != null : "module is null";
      
      this.name = name;
      this.context = context;
      this.module = module;
   }
   
   public Context getContext()
   {
      return context;
   }

   public JavaEEModule getModule()
   {
      return module;
   }

   public String getName()
   {
      return name;
   }
}
