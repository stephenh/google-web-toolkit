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
package org.hibernate.jsr303.tck.tests.constraints.groups;

import com.google.gwt.junit.client.GWTTestCase;

import org.hibernate.jsr303.tck.util.client.Failing;

/**
 * Test wrapper for {@link GroupTest}.
 */
public class GroupGwtTest extends GWTTestCase {
  private final GroupTest delegate = new GroupTest();

  @Override
  public String getModuleName() {
    return "org.hibernate.jsr303.tck.tests.constraints.groups.TckTest";
  }

  public void testConstraintCanBelongToMoreThanOneGroup() {
    delegate.testConstraintCanBelongToMoreThanOneGroup();
  }

  public void testConstraintWithNoExplicitlySpecifiedGroupBelongsToDefault() {
    delegate.testConstraintWithNoExplicitlySpecifiedGroupBelongsToDefault();
  }

  public void testCyclicGroupSequence() {
    delegate.testCyclicGroupSequence();
  }

  public void testGroups() {
    delegate.testGroups();
  }

  @Failing(issue = 5801)
  public void testGroupSequence() {
    delegate.testGroupSequence();
  }

  public void testGroupSequenceFollowedByGroup() {
    delegate.testGroupSequenceFollowedByGroup();
  }

  public void testImplicitGrouping() {
    delegate.testImplicitGrouping();
  }

  public void testValidateAgainstDifferentGroups() {
    delegate.testValidateAgainstDifferentGroups();
  }

  public void testValidationFailureInMultipleGroups() {
    delegate.testValidationFailureInMultipleGroups();
  }

}
