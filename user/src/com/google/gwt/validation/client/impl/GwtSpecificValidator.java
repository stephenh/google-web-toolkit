/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.validation.client.impl;

import com.google.gwt.validation.client.GroupInheritanceMap;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;

/**
 * <strong>EXPERIMENTAL</strong> and subject to change. Do not use this in
 * production code.
 * <p>
 * Defines GWT version of {@link javax.validation.Validator}. This used by
 * generate a specific Validator for a given class G.
 * 
 * @param <G> the type of bean for this validator
 */
public interface GwtSpecificValidator<G> {

  /**
   * Return the descriptor object describing bean constraints. The returned
   * object (and associated objects including
   * <code>ConstraintDescriptor<code>s) are immutable.
   *
   * @param groupInheritanceMap The group inheritance map for the validator.
   * @return the bean descriptor for the class associated with this validator.
   *
   * @throws IllegalArgumentException if clazz is null
   * @throws ValidationException if a non recoverable error happens during the
   *           metadata discovery or if some constraints are invalid.
   */
  GwtBeanDescriptor<G> getConstraints(GroupInheritanceMap groupInheritanceMap)
      throws ValidationException;

  /**
   * Validates all constraints on <code>object</code>.
   *
   * @param<T> the type of the RootBean for this validation context
   * @param context The gwt validation context
   * @param object object to validate
   * @param groups group or list of groups targeted for validation (default to
   *          {@link javax.validation.groups.Default})
   *
   * @return constraint violations or an empty Set if none
   *
   * @throws IllegalArgumentException if object is null or if null is passed to
   *           the varargs groups
   * @throws ValidationException if a non recoverable error happens during the
   *           validation process
   */
  <T> Set<ConstraintViolation<T>> validate(GwtValidationContext<T> context,
      G object, Class<?>... groups) throws ValidationException;

  /**
   * Validates all constraints placed on the property of <code>object</code>
   * named <code>propertyName</code>.
   *
   * @param<T> the type of the RootBean for this validation context
   * @param context The gwt validation context
   * @param object object to validate
   * @param propertyName property to validate (ie field and getter constraints)
   * @param groups group or list of groups targeted for validation (default to
   *          {@link javax.validation.groups.Default})
   *
   * @return constraint violations or an empty Set if none
   *
   * @throws IllegalArgumentException if <code>object</code> is null, if
   *           <code>propertyName</code> null, empty or not a valid object
   *           property or if null is passed to the varargs groups
   * @throws ValidationException if a non recoverable error happens during the
   *           validation process
   */
  <T> Set<ConstraintViolation<T>> validateProperty(
      GwtValidationContext<T> context, G object, String propertyName,
      Class<?>... groups) throws ValidationException;

  /**
   * Validates all constraints placed on the property named
   * <code>propertyName</code> of the class <code>beanType</code> where the
   * property value is <code>value</code>.
   * <p/>
   * <code>ConstraintViolation</code> objects return null for
   * {@link ConstraintViolation#getRootBean()} and
   * {@link ConstraintViolation#getLeafBean()}
   *
   * @param<T> the type of the RootBean for this validation context
   * @param context The gwt validation context
   * @param beanType the bean type
   * @param propertyName property to validate
   * @param value property value to validate
   * @param groups group or list of groups targeted for validation (default to
   *          {@link javax.validation.groups.Default})
   *
   * @return constraint violations or an empty Set if none
   *
   * @throws IllegalArgumentException if <code>beanType</code> is null, if
   *           <code>propertyName</code> null, empty or not a valid object
   *           property or if null is passed to the varargs groups
   * @throws ValidationException if a non recoverable error happens during the
   *           validation process
   */
  <T> Set<ConstraintViolation<T>> validateValue(
      GwtValidationContext<T> context, Class<G> beanType, String propertyName,
      Object value, Class<?>... groups) throws ValidationException;
}
