package com.google.gwt.dev.jjs.impl;

import com.google.jribble.ast.Array;
import com.google.jribble.ast.Primitive;

import static com.google.gwt.thirdparty.guava.common.collect.Sets.newHashSet;

import junit.framework.Assert;
import static com.google.gwt.dev.jjs.impl.AstUtils.toRef;

import junit.framework.TestCase;

public class JribbleReferenceMapperTest extends TestCase {

  public void testTouchedTypes() {
    JribbleReferenceMapper m = new JribbleReferenceMapper();
    m.getType(toRef("foo.T1"));
    m.getType(new Primitive("int"));
    m.getType(new Array(toRef("foo.T4")));
    m.getClassType("foo.T2");
    m.getInterfaceType("foo.T3");
    Assert.assertEquals(newHashSet("foo.T1", "foo.T2", "foo.T3", "foo.T4"), m.getTouchedTypes());

    m.clearSource();
    Assert.assertEquals(newHashSet(), m.getTouchedTypes());

    m.getType(toRef("foo.T1"));
    Assert.assertEquals(newHashSet("foo.T1"), m.getTouchedTypes());
  }

}
