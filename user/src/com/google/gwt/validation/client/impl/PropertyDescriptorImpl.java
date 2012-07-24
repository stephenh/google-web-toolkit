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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

/**
 * <strong>EXPERIMENTAL</strong> and subject to change. Do not use this in
 * production code.
 * <p>
 */
public final class PropertyDescriptorImpl implements PropertyDescriptor {

  private boolean cascaded;
  private Set<ConstraintDescriptorImpl<?>> descriptors;
  private Class<?> elementClass;
  private String name;
  private GroupInheritanceMap groupInheritanceMap;

  public PropertyDescriptorImpl(String name, Class<?> elementClass,
      boolean cascaded, ConstraintDescriptorImpl<?>... descriptors) {
    this(name, elementClass, cascaded, null, descriptors);
  }

  public PropertyDescriptorImpl(String name, Class<?> elementClass,
      boolean cascaded, GroupInheritanceMap groupInheritanceMap,
      ConstraintDescriptorImpl<?>... descriptors) {
    super();

    this.elementClass = elementClass;
    this.cascaded = cascaded;
    this.name = name;
    this.groupInheritanceMap = groupInheritanceMap;
    this.descriptors = new HashSet<ConstraintDescriptorImpl<?>>(
        Arrays.asList(descriptors));
  }

  @Override
  public ConstraintFinder findConstraints() {
    return new ConstraintFinderImpl(groupInheritanceMap, descriptors);
  }

  @Override
  public Set<ConstraintDescriptor<?>> getConstraintDescriptors() {
    return findConstraints().getConstraintDescriptors();
  }

  @Override
  public Class<?> getElementClass() {
    return elementClass;
  }

  @Override
  public String getPropertyName() {
    return name;
  }

  @Override
  public boolean hasConstraints() {
    return !descriptors.isEmpty();
  }

  @Override
  public boolean isCascaded() {
    return cascaded;
  }

  public void setGroupInheritanceMap(GroupInheritanceMap groupInheritanceMap) {
    // TODO(idol) Find some way to pass this via the constructor rather than after creation
    this.groupInheritanceMap = groupInheritanceMap;
  }

  public PropertyDescriptorImpl shallowCopy() {
    ConstraintDescriptorImpl<?>[] desc = new ConstraintDescriptorImpl<?>[descriptors.size()];
    descriptors.toArray(desc);
    return new PropertyDescriptorImpl(name, elementClass, cascaded, groupInheritanceMap, desc);
  }
}
