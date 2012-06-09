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
import elemental.dom.MediaStream;
import elemental.js.dom.JsMediaStreamList;
import elemental.dom.MediaStreamList;
import elemental.js.events.JsEvent;
import elemental.html.DeprecatedPeerConnection;
import elemental.events.EventListener;
import elemental.js.dom.JsMediaStream;
import elemental.js.events.JsEventListener;
import elemental.events.Event;

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

public class JsDeprecatedPeerConnection extends JsElementalMixinBase  implements DeprecatedPeerConnection {
  protected JsDeprecatedPeerConnection() {}

  public final native JsMediaStreamList getLocalStreams() /*-{
    return this.localStreams;
  }-*/;

  public final native EventListener getOnaddstream() /*-{
    return @elemental.js.dom.JsElementalMixinBase::getListenerFor(Lcom/google/gwt/core/client/JavaScriptObject;)(this.onaddstream);
  }-*/;

  public final native void setOnaddstream(EventListener listener) /*-{
    this.onaddstream = @elemental.js.dom.JsElementalMixinBase::getHandlerFor(Lelemental/events/EventListener;)(listener);
  }-*/;
  public final native EventListener getOnconnecting() /*-{
    return @elemental.js.dom.JsElementalMixinBase::getListenerFor(Lcom/google/gwt/core/client/JavaScriptObject;)(this.onconnecting);
  }-*/;

  public final native void setOnconnecting(EventListener listener) /*-{
    this.onconnecting = @elemental.js.dom.JsElementalMixinBase::getHandlerFor(Lelemental/events/EventListener;)(listener);
  }-*/;
  public final native EventListener getOnmessage() /*-{
    return @elemental.js.dom.JsElementalMixinBase::getListenerFor(Lcom/google/gwt/core/client/JavaScriptObject;)(this.onmessage);
  }-*/;

  public final native void setOnmessage(EventListener listener) /*-{
    this.onmessage = @elemental.js.dom.JsElementalMixinBase::getHandlerFor(Lelemental/events/EventListener;)(listener);
  }-*/;
  public final native EventListener getOnopen() /*-{
    return @elemental.js.dom.JsElementalMixinBase::getListenerFor(Lcom/google/gwt/core/client/JavaScriptObject;)(this.onopen);
  }-*/;

  public final native void setOnopen(EventListener listener) /*-{
    this.onopen = @elemental.js.dom.JsElementalMixinBase::getHandlerFor(Lelemental/events/EventListener;)(listener);
  }-*/;
  public final native EventListener getOnremovestream() /*-{
    return @elemental.js.dom.JsElementalMixinBase::getListenerFor(Lcom/google/gwt/core/client/JavaScriptObject;)(this.onremovestream);
  }-*/;

  public final native void setOnremovestream(EventListener listener) /*-{
    this.onremovestream = @elemental.js.dom.JsElementalMixinBase::getHandlerFor(Lelemental/events/EventListener;)(listener);
  }-*/;
  public final native EventListener getOnstatechange() /*-{
    return @elemental.js.dom.JsElementalMixinBase::getListenerFor(Lcom/google/gwt/core/client/JavaScriptObject;)(this.onstatechange);
  }-*/;

  public final native void setOnstatechange(EventListener listener) /*-{
    this.onstatechange = @elemental.js.dom.JsElementalMixinBase::getHandlerFor(Lelemental/events/EventListener;)(listener);
  }-*/;
  public final native int getReadyState() /*-{
    return this.readyState;
  }-*/;

  public final native JsMediaStreamList getRemoteStreams() /*-{
    return this.remoteStreams;
  }-*/;

  public final native void addStream(MediaStream stream) /*-{
    this.addStream(stream);
  }-*/;

  public final native void close() /*-{
    this.close();
  }-*/;

  public final native void processSignalingMessage(String message) /*-{
    this.processSignalingMessage(message);
  }-*/;

  public final native void removeStream(MediaStream stream) /*-{
    this.removeStream(stream);
  }-*/;

  public final native void send(String text) /*-{
    this.send(text);
  }-*/;
}
