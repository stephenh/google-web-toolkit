package com.google.gwt.sample.hello.client;

import com.google.gwt.core.client.JavaScriptObject;

public final class MyBean extends JavaScriptObject implements Sameable<MyBean> {

  public static native MyBean create(String id) /*-{
    var obj = { "id": id };
    return obj;
  }-*/;

  protected MyBean() {
  }

  public native String getId() /*-{
    return this.id;
  }-*/;

  @Override
  public boolean sameAs(MyBean other) {
    return this.getId().equals(other.getId());
  }
}
