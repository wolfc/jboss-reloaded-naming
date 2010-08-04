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
package org.jboss.reloaded.naming.test.legacy.unit;

import junit.framework.Assert;
import org.jboss.naming.ENCFactory;
import org.jboss.reloaded.naming.test.common.AbstractNamingTestCase;
import org.jboss.util.naming.Util;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.NamingException;

import static org.junit.Assert.fail;

/**
 * Some simple tests against the legacy java:comp (ENCFactory).
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class LegacyTestCase extends AbstractNamingTestCase
{
   @Test
   public void test1() throws Exception
   {
      Context context;
      ENCFactory.pushContextId("component");
      try
      {
         context = (Context) iniCtx.lookup("java:comp");
      }
      finally
      {
         ENCFactory.popContextId();
      }
      Util.rebind(context, "env/value", "Hello LegacyTestCase");
      
      ENCFactory.pushContextId("component");
      try
      {
         String value = (String) iniCtx.lookup("java:comp/env/value");
         Assert.assertEquals("Hello LegacyTestCase", value);
      }
      finally
      {
         ENCFactory.popContextId();
      }
   }

   @Test
   public void testLegacyCallingApp() throws NamingException
   {
      ENCFactory.pushContextId("legacy");
      try
      {
         iniCtx.lookup("java:app/env/value");
         fail("Expected NamingException");
      }
      catch(NamingException e)
      {
         // good
      }
      finally
      {
         ENCFactory.popContextId();
      }
   }

   @Test
   public void testLegacyCallingModule() throws NamingException
   {
      ENCFactory.pushContextId("legacy");
      try
      {
         iniCtx.lookup("java:module/env/value");
         fail("Expected NamingException");
      }
      catch(NamingException e)
      {
         // good
      }
      finally
      {
         ENCFactory.popContextId();
      }
   }
}
