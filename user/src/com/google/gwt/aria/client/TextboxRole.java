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

import com.google.gwt.aria.client.CommonAttributeTypes.IdReference;
import com.google.gwt.aria.client.PropertyTokenTypes.AutocompleteToken;
import com.google.gwt.dom.client.Element;

/**
 * TextboxRole interface.
 * The interface defines methods for setting, getting, removing states and properties.
 * <p>Allows ARIA Accessibility attributes to be added to widgets so that they can be identified by
 * assistive technology.</p>
 *
 * <p>ARIA roles define widgets and page structure that can be interpreted by a reader
 * application/device. There is a set of abstract roles which are used as
 * building blocks of the roles hierarchy structural and define the common properties and states
 * for the concrete roles. Abstract roles cannot be set to HTML elements.</p>
 *
 * <p>There are states and properties that are defined for a role. As roles are organized in a
 * hierarchy, a role has inherited and own properties and states which can be set to the
 * element.</p>
 *
 * <p>For more details about ARIA roles check <a href="http://www.w3.org/TR/wai-aria/roles">
 * The Roles Model </a>.</p>
 */
public interface TextboxRole extends InputRole {
  String getAriaActivedescendantProperty(Element element);

  String getAriaAutocompleteProperty(Element element);

  String getAriaMultilineProperty(Element element);

  String getAriaReadonlyProperty(Element element);

  String getAriaRequiredProperty(Element element);

  void removeAriaActivedescendantProperty(Element element);

  void removeAriaAutocompleteProperty(Element element);

  void removeAriaMultilineProperty(Element element);

  void removeAriaReadonlyProperty(Element element);

  void removeAriaRequiredProperty(Element element);

  void setAriaActivedescendantProperty(Element element, IdReference value);

  /**
   * @deprecated This method will be deleted. Do not use!!!
   * Use {@link #setAriaAutocompleteProperty(Element element, AutocompleteValue value)} instead
   */
  @Deprecated
  void setAriaAutocompleteProperty(Element element, AutocompleteToken value);

  void setAriaAutocompleteProperty(Element element, AutocompleteValue value);

  void setAriaMultilineProperty(Element element, boolean value);

  void setAriaReadonlyProperty(Element element, boolean value);

  void setAriaRequiredProperty(Element element, boolean value);
}
