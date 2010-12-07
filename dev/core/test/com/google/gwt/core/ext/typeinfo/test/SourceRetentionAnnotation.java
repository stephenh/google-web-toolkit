/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.core.ext.typeinfo.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This type is used to test that source-only annotations do not appear in the
 * TypeOracle model.
 */
@Retention(RetentionPolicy.SOURCE)
@Target( {
    ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.FIELD,
    ElementType.METHOD, ElementType.PACKAGE, ElementType.PARAMETER,
    ElementType.TYPE})
public @interface SourceRetentionAnnotation {
}