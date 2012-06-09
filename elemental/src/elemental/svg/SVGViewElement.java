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
  * The <code>SVGViewElement</code> interface provides access to the properties of <code><a rel="custom" href="https://developer.mozilla.org/en/SVG/Element/view">&lt;view&gt;</a></code>
 elements, as well as methods to manipulate them.
  */
public interface SVGViewElement extends SVGElement, SVGExternalResourcesRequired, SVGFitToViewBox, SVGZoomAndPan {


  /**
    * Corresponds to attribute 
<code><a rel="internal" href="https://developer.mozilla.org/en/SVG/Attribute/viewTarget" class="new">viewTarget</a></code> on the given <code><a rel="custom" href="https://developer.mozilla.org/en/SVG/Element/view">&lt;view&gt;</a></code>
 element. A list of DOMString values which contain the names listed in the 
<code><a rel="internal" href="https://developer.mozilla.org/en/SVG/Attribute/viewTarget" class="new">viewTarget</a></code> attribute. Each of the DOMString values can be associated with the corresponding element using the getElementById() method call.
    */
  SVGStringList getViewTarget();
}
