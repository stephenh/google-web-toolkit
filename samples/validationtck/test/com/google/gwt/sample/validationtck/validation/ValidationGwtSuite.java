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
package com.google.gwt.sample.validationtck.validation;

import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;

/**
 * Tck Tests for the {@code validation} package.
 */
public class ValidationGwtSuite {
  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite(
        "TCK for GWT Validation, validation package");
    suite.addTestSuite(PropertyPathTest.class);
    suite.addTestSuite(ValidatePropertyTest.class);
    suite.addTestSuite(ValidateValueTest.class);
    suite.addTestSuite(ValidationTest.class);
    return suite;
  }
}
