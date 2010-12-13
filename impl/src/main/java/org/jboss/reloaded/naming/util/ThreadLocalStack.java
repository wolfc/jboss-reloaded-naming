/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.reloaded.naming.util;

import java.util.LinkedList;

/**
 * INTERNAL
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @version $Revision$
 */
public class ThreadLocalStack<T>
{
   private ThreadLocal<LinkedList<T>> stack = new InheritableThreadLocal<LinkedList<T>>() {
      @Override
      protected LinkedList<T> childValue(LinkedList<T> parentValue)
      {
         // do a shallow clone
         return (LinkedList<T>) parentValue.clone();
      }
   };

   public void push(T obj)
   {
      LinkedList<T> list = stack.get();
      if (list == null)
      {
         list = new LinkedList<T>();
         stack.set(list);
      }
      list.addLast(obj);
   }

   public T pop()
   {
      LinkedList<T> list = stack.get();
      if (list == null)
      {
         return null;
      }
      T rtn = list.removeLast();
      if (list.size() == 0)
      {
         //stack.set(null);
         stack.remove();
         //list.clear();
      }
      return rtn;
   }

   public T get()
   {
      LinkedList<T> list = stack.get();
      if (list == null)
      {
         return null;
      }
      return list.getLast();
   }

   /*
   public List<T> getList()
   {
      return stack.get();     
   }
   */
}
