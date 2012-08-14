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
package org.hibernate.jsr303.tck.tests.bootstrap;

import com.google.gwt.junit.client.GWTTestCase;

import org.hibernate.jsr303.tck.util.client.NonTckTest;
import org.hibernate.jsr303.tck.util.client.NotSupported;
import org.hibernate.jsr303.tck.util.client.NotSupported.Reason;

/**
 * Wraps {@link ValidationProviderTest} .
 */
public class ValidationProviderGwtTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "org.hibernate.jsr303.tck.tests.bootstrap.TckTest";
  }

  @NotSupported(reason = Reason.CUSTOM_PROVIDERS)
  public void testByDefaultProviderUsesTheFirstProviderReturnedByValidationProviderResolver() {
    fail("Custom validation providers are not supported");
  }

  @NotSupported(reason = Reason.CUSTOM_PROVIDERS)
  public void testFirstMatchingValidationProviderResolverIsReturned() {
    fail("Custom validation providers are not supported");
  }

  @NonTckTest
  public void testThereMustBeOnePassingTest(){}

  @NotSupported(reason = Reason.CUSTOM_PROVIDERS)
  public void testValidationExceptionIsThrownInCaseValidatorFactoryCreationFails() {
    fail("Custom validation providers are not supported");
  }

  @NotSupported(reason = Reason.CUSTOM_PROVIDERS)
  public void testValidationProviderContainsNoArgConstructor() {
    fail("Custom validation providers are not supported");
  }
}
