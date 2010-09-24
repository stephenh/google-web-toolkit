/*
 * Copyright 2007 Google Inc.
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
package com.google.gwt.sample.dynatablerf.server;

import com.google.gwt.sample.dynatablerf.domain.Address;
import com.google.gwt.sample.dynatablerf.domain.Person;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * The server side service class.
 */
public class SchoolCalendarService implements Filter {
  private static final ThreadLocal<PersonSource> PERSON_SOURCE = new ThreadLocal<PersonSource>();

  public static Person createPerson() {
    checkPersonSource();
    Person person = PersonFuzzer.generatePerson();
    PERSON_SOURCE.get().persist(person);
    return person;
  }

  public static Person findPerson(String id) {
    checkPersonSource();
    return PERSON_SOURCE.get().findPerson(id);
  }

  /**
   * XXX Remove this once primitive lists work.
   */
  public static List<Person> getPeople(int startIndex, int maxCount) {
    return getPeople(startIndex, maxCount, new boolean[] {
        true, true, true, true, true, true, true});
  }

  public static void persist(Address address) {
    checkPersonSource();
    PERSON_SOURCE.get().persist(address);
  }

  public static void persist(Person person) {
    checkPersonSource();
    PERSON_SOURCE.get().persist(person);
  }

  private static void checkPersonSource() {
    if (PERSON_SOURCE.get() == null) {
      throw new IllegalStateException(
          "Calling service method outside of HTTP request");
    }
  }

  /**
   * XXX private due to inability to add method overloads.
   */
  private static List<Person> getPeople(int startIndex, int maxCount,
      boolean[] filter) {
    checkPersonSource();
    return PERSON_SOURCE.get().getPeople(startIndex, maxCount, filter);
  }

  private PersonSource backingStore;

  public void destroy() {
  }

  public void doFilter(ServletRequest req, ServletResponse resp,
      FilterChain chain) throws IOException, ServletException {
    try {
      PERSON_SOURCE.set(PersonSource.of(backingStore));
      chain.doFilter(req, resp);
    } finally {
      PERSON_SOURCE.set(null);
    }
  }

  public void init(FilterConfig config) {
    backingStore = (PersonSource) config.getServletContext().getAttribute(
        SchoolCalendarService.class.getName());
    if (backingStore == null) {
      backingStore = PersonSource.of(PersonFuzzer.generateRandomPeople());
      config.getServletContext().setAttribute(
          SchoolCalendarService.class.getName(), backingStore);
    }
  }
}
