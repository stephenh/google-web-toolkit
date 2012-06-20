/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.gwt.aria.client;
/////////////////////////////////////////////////////////
// This is auto-generated code.  Do not manually edit! //
/////////////////////////////////////////////////////////

import com.google.gwt.aria.client.CommonAttributeTypes.AriaAttributeType;

/**
 * State enum for 'aria-selected' values.
 */
public enum SelectedValue implements AriaAttributeType {
  TRUE, FALSE, UNDEFINED;
  /**
   * Gets the enum constant corresponding to {@code value} for the token type
   * SelectedValue.
   *
   * @param value Boolean value for which we want to get the corresponding enum constant.
   */
  public static SelectedValue of(boolean value) {
    return value ? TRUE : FALSE;
  }

  @Override
  public String getAriaValue() {
    switch (this) {
        case TRUE:
          return "true";
        case FALSE:
          return "false";
        case UNDEFINED:
          return "undefined";
    }
    return null; // not reachable
  }
}
