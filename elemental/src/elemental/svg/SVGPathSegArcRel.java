/*
 * Copyright 2012 Google Inc.
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
package elemental.svg;

import elemental.events.*;
import elemental.util.*;
import elemental.dom.*;
import elemental.html.*;
import elemental.css.*;
import elemental.stylesheets.*;

import java.util.Date;

/**
  * 
  */
public interface SVGPathSegArcRel extends SVGPathSeg {

  float getAngle();

  void setAngle(float arg);

  boolean isLargeArcFlag();

  void setLargeArcFlag(boolean arg);

  float getR1();

  void setR1(float arg);

  float getR2();

  void setR2(float arg);

  boolean isSweepFlag();

  void setSweepFlag(boolean arg);

  float getX();

  void setX(float arg);

  float getY();

  void setY(float arg);
}
