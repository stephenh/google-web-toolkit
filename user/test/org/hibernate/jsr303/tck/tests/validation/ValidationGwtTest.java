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
package org.hibernate.jsr303.tck.tests.validation;

import org.hibernate.jsr303.tck.util.client.NotSupported;
import org.hibernate.jsr303.tck.util.client.NotSupported.Reason;
import org.hibernate.jsr303.tck.util.client.TestNotCompatible;

/**
 * Test wrapper for {@link ValidationTest}.
 */
public class ValidationGwtTest extends AbstractValidationTest {

  private final ValidationTest delegate = new ValidationTest();

  public void testBuildDefaultValidatorFactory() {
    delegate.testBuildDefaultValidatorFactory();
  }

  @NotSupported(reason = Reason.CUSTOM_PROVIDERS)
  public void testCustomValidationProviderResolution() {
    fail("Custom validation providers are not supported");
  }

  @NotSupported(reason = Reason.CUSTOM_PROVIDERS)
  public void testSpecificValidationProvider() {
    fail("Custom validation providers are not supported");
  }

  @TestNotCompatible(reason = TestNotCompatible.Reason.REFLECTION, 
      whereTested = "This test checks the methods of the API itself, it does not need to be tested here also.")
  public void testVerifyMethodsOfValidationObjects() {
    // This method is excluded because it does not compile.
    // delegate.testVerifyMethodsOfValidationObjects();
  }
}
