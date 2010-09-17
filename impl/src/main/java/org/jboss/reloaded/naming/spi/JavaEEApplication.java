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
package org.jboss.reloaded.naming.spi;

import javax.naming.Context;

/**
 * A named reference to the application scoped namespace.
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public interface JavaEEApplication
{
   /**
    * The JNDI namespace which holds java:app.
    * @return the context for java:app
    */
   Context getContext();
   
   /**
    * The name of the application.
    * @return the name of the application
    */
   String getName();

   /**
    * JavaEE 6 EE.8.5
    *
    * A Java EE application is delivered as a .ear file or a
    * stand-alone Java EE module.
    */
   boolean isEnterpriseApplicationArchive();
}
