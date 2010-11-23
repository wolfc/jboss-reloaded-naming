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
package org.jboss.reloaded.naming.deployers.mc;

import org.jboss.logging.Logger;
import org.jboss.reloaded.naming.spi.JavaEEComponent;
import org.jboss.reloaded.naming.spi.JavaEEModule;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class MCJavaEEComponent extends AbstractNameSpace implements JavaEEComponent
{
   private static final Logger log = Logger.getLogger(MCJavaEEComponent.class);
   
   private JavaEEModule module;

   private boolean useJavaModuleContext;
   
   /**
    * This is the same as calling {@link #MCJavaEEComponent(String, JavaEEModule, boolean) MCJavaEEComponent(name, module, false)}
    * @param name The name of the {@link JavaEEComponent}
    * @param module The parent {@link JavaEEModule module} to which this {@link JavaEEComponent component} belongs
    * 
    */
   public MCJavaEEComponent(String name, JavaEEModule module)
   {
      this(name, module, false);
   }
   
   /**
    * Creates a {@link MCJavaEEComponent JavaEE component} with the specified name and the specified parent
    * {@link JavaEEModule module}.
    * 
    * @param name The name of the {@link JavaEEComponent}
    * @param module The parent {@link JavaEEModule module} to which this {@link JavaEEComponent component} belongs
    * @param useJavaModuleContext Whether this {@link JavaEEComponent component} should use(/share) the {@link Context context} of
    *                           its parent {@link JavaEEModule module} or should create a separate {@link Context context} for this
    *                           {@link JavaEEComponent component}. If true, this {@link JavaEEComponent component} will use the 
    *                           {@link Context context} of the passed {@link JavaEEModule module}. Else creates a new {@link Context context} 
    */
   public MCJavaEEComponent(String name, JavaEEModule module, boolean useJavaModuleContext)
   {
      super(name);
      this.module = module;
      this.useJavaModuleContext = useJavaModuleContext;
   }

   public JavaEEModule getModule()
   {
      return module;
   }

   @Override
   public void start() throws NamingException
   {
      // if we don't share the java:module context, then create a new one
      if (!this.useJavaModuleContext)
      {
         super.start();
         log.debug("Installed context " + context + " for JavaEE component " + name + " in module " + module.getName());
      }
      else
      {
         // share the java:module context
         this.context = this.module.getContext();
         log.debug("Sharing JavaEE module context " + context + " for JavaEE component " + name + " in module " + module.getName());
      }
   }

   @Override
   public void stop() throws NamingException
   {
      super.stop();

      log.debug("Uninstalled context " + context + " for JavaEE component " + name + " in module " + module.getName());
   }

   @Override
   public String toString()
   {
      return "MCJavaEEComponent{" +
         "module=" + module +
         ", name='" + name + '\'' +
         ", useJavaModuleContext=" + this.useJavaModuleContext + "}";
   }
}
