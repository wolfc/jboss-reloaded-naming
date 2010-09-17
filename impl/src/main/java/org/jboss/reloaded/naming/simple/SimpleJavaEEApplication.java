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

import org.jboss.reloaded.naming.spi.JavaEEApplication;

import javax.naming.Context;

/**
 * A simple implementation of {@link JavaEEApplication}.
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class SimpleJavaEEApplication implements JavaEEApplication
{
   private Context context;
   private String name;
   private Boolean isEnterpriseApplicationArchive;

   @Deprecated
   public SimpleJavaEEApplication(String name, Context context)
   {
      this(name, context, null);
   }

   public SimpleJavaEEApplication(String name, Context context, boolean isEnterpriseApplicationArchive)
   {
      this(name, context, (Boolean) isEnterpriseApplicationArchive);
   }

   @Deprecated
   protected SimpleJavaEEApplication(String name, Context context, Boolean isEnterpriseApplicationArchive)
   {
      assert name != null : "name is null";
      assert context != null : "context is null";
      
      this.name = name;
      this.context = context;
      this.isEnterpriseApplicationArchive = isEnterpriseApplicationArchive;
   }
   
   public Context getContext()
   {
      return context;
   }

   public String getName()
   {
      return name;
   }

   public boolean isEnterpriseApplicationArchive()
   {
      if(isEnterpriseApplicationArchive == null)
         throw new UnsupportedOperationException("isEnterpriseApplicationArchive is not set on " + this);
      return isEnterpriseApplicationArchive.booleanValue();
   }
}
