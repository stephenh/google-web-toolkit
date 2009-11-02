/*
 * Copyright 2009 Google Inc.
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
package com.google.gwt.uibinder.rebind;

import org.w3c.dom.Element;

@SuppressWarnings("deprecation")
//BundleAttributeParser not quite ready to die
class XMLElementProviderImpl implements XMLElementProvider {
  private final AttributeParsers attributeParsers;
  private final BundleAttributeParsers bundleParsers; 
  private final MortalLogger logger;
  
  public XMLElementProviderImpl(AttributeParsers attributeParsers,
    BundleAttributeParsers bundleParsers, MortalLogger logger) {
    this.attributeParsers = attributeParsers;
    this.bundleParsers = bundleParsers;
    this.logger = logger;
  }
  
  public XMLElement get(Element e) {
    return new XMLElement(e, attributeParsers, bundleParsers, logger, this);
  }
}