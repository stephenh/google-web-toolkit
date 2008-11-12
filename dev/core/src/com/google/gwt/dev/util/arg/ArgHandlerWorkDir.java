/*
 * Copyright 2006 Google Inc.
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
package com.google.gwt.dev.util.arg;

import com.google.gwt.util.tools.ArgHandlerDir;

import java.io.File;

/**
 * Argument handler for processing the output directory flag.
 */
public final class ArgHandlerWorkDir extends ArgHandlerDir {

  public static final String GWT_TMP_DIR = "gwt-tmp";

  private final OptionWorkDir option;

  public ArgHandlerWorkDir(OptionWorkDir option) {
    this.option = option;
  }

  public String[] getDefaultArgs() {
    return new String[] {
        "-workDir",
        new File(System.getProperty("java.io.tmpdir"), GWT_TMP_DIR).getAbsolutePath()};
  }

  public String getPurpose() {
    return "The compiler work directory (must be writeable; defaults to system temp dir)";
  }

  public String getTag() {
    return "-workDir";
  }

  @Override
  public void setDir(File dir) {
    option.setWorkDir(dir);
  }

}
