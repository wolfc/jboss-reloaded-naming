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

import org.jboss.reloaded.naming.spi.JavaEEApplication;
import org.jboss.reloaded.naming.spi.JavaEEModule;

/**
 * A simple implementation of {@link JavaEEModule}.
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class SimpleJavaEEModule implements JavaEEModule
{
   private JavaEEApplication application;
   private Context context;
   private String name;

   /**
    * Instantiate a {@link SimpleJavaEEModule}.
    * 
    * @param name the name of the module
    * @param context the naming context of the module
    * @param application the application of which this module is part, or null if stand alone
    */
   public SimpleJavaEEModule(String name, Context context, JavaEEApplication application)
   {
      assert name != null : "name is null";
      assert context != null : "context is null";
      
      this.name = name;
      this.context = context;
      this.application = application;
   }
   
   public JavaEEApplication getApplication()
   {
      return application;
   }

   public Context getContext()
   {
      return context;
   }

   public String getName()
   {
      return name;
   }
}
