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
package elemental.js.html;
import elemental.html.BlobBuilder;
import elemental.html.ArrayBuffer;
import elemental.html.Blob;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

import elemental.events.*;
import elemental.util.*;
import elemental.dom.*;
import elemental.html.*;
import elemental.css.*;
import elemental.js.stylesheets.*;
import elemental.js.events.*;
import elemental.js.util.*;
import elemental.js.dom.*;
import elemental.js.html.*;
import elemental.js.css.*;
import elemental.js.stylesheets.*;

import java.util.Date;

public class JsBlobBuilder extends JsElementalMixinBase  implements BlobBuilder {
  protected JsBlobBuilder() {}

  public final native void append(Blob blob) /*-{
    this.append(blob);
  }-*/;

  public final native void append(ArrayBuffer arrayBuffer) /*-{
    this.append(arrayBuffer);
  }-*/;

  public final native void append(String value) /*-{
    this.append(value);
  }-*/;

  public final native void append(String value, String endings) /*-{
    this.append(value, endings);
  }-*/;

  public final native JsBlob getBlob() /*-{
    return this.getBlob();
  }-*/;

  public final native JsBlob getBlob(String contentType) /*-{
    return this.getBlob(contentType);
  }-*/;
}
