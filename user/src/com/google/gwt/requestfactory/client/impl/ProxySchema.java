/*
 * Copyright 2010 Google Inc.
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
package com.google.gwt.requestfactory.client.impl;

import com.google.gwt.requestfactory.shared.EntityProxy;

import com.google.gwt.requestfactory.shared.EntityProxyChange;
import com.google.gwt.requestfactory.shared.WriteOperation;
import com.google.gwt.requestfactory.shared.impl.Property;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * <span style="color:red">Experimental API: This class is still under rapid
 * development, and is very likely to be deleted. Use it at your own risk.
 * </span>
 * </p>
 * Used by {@link EntityProxy} implementations generated by
 * {@link com.google.gwt.requestfactory.rebind.RequestFactoryGenerator
 * RequestFactoryGenerator}. Defines the set of properties for a class of
 * Proxy, and serves as a factory for these proxies and their
 * {@link EntityProxyChange}s.
 * 
 * @param <P> the type of the Proxies this schema describes
 */
public abstract class ProxySchema<P extends ProxyImpl> {
  
  private final String token;
  
  private final Set<Property<?>> allProperties;
  
  {
    Set<Property<?>> set = new HashSet<Property<?>>();
    set.add(ProxyImpl.version);
    allProperties = Collections.unmodifiableSet(set);
  }
  
  public ProxySchema(String token) {
    this.token = token;
  }
  public Set<Property<?>> allProperties() {
    return allProperties;
  }

  public final P create(ProxyJsoImpl jso) {
    return create(jso, RequestFactoryJsonImpl.NOT_FUTURE);
  }

  public abstract P create(ProxyJsoImpl jso, boolean isFuture);

  @SuppressWarnings("unchecked")
  public EntityProxyChange<P> createChangeEvent(EntityProxy proxy,
      WriteOperation operation) {
    assert isCorrectClass(proxy);
    return new EntityProxyChange<P>((P) proxy, operation);
  }
  
  public abstract Class<? extends EntityProxy> getProxyClass();

  public String getToken() {
    return token;
  }

  private boolean isCorrectClass(EntityProxy proxy) {
    // What we really want to check is isAssignableFrom. Sigh.
    Class<? extends EntityProxy> actual = ((ProxyImpl) proxy).asJso().getRequestFactory().getClass(
        proxy.stableId());
    Class<? extends EntityProxy> expected = getProxyClass();
    boolean equals = actual.equals(expected);
    return equals;
  }
}
