/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.SourceOrigin;
import com.google.gwt.dev.jjs.ast.JArrayType;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JConstructor;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JInterfaceType;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JNullType;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.jjs.ast.JReferenceType;
import com.google.gwt.dev.jjs.ast.JType;
import com.google.gwt.dev.util.StringInterner;
import com.google.jribble.ast.Array;
import com.google.jribble.ast.ClassDef;
import com.google.jribble.ast.DeclaredType;
import com.google.jribble.ast.FieldDef;
import com.google.jribble.ast.Primitive;
import com.google.jribble.ast.Ref;
import com.google.jribble.ast.Signature;
import com.google.jribble.ast.Type;
import com.google.jribble.ast.Void$;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates unresolved references to types, fields, and methods.
 * 
 * An instance of this class can be reused to create several mini ASTs, as we can reuse the external
 * JFields, JMethods, and JReferenceTypes across compilation units. The non-external ASTs are
 * flushed after each CompilationUnit is built.
 * 
 * Closely modeled after the canonical Java {@ReferenceMapper}.
 */
public class JribbleReferenceMapper {

  private static final StringInterner stringInterner = StringInterner.get();
  private final List<String> argNames = new ArrayList<String>();
  // full instances, flushed per CompilationUnit
  private final Map<String, JField> sourceFields = new HashMap<String, JField>();
  private final Map<String, JMethod> sourceMethods = new HashMap<String, JMethod>();
  private final Map<String, JReferenceType> sourceTypes = new HashMap<String, JReferenceType>();
  // external instances, kept across CompilationUnits
  private final Map<String, JField> fields = new HashMap<String, JField>();
  private final Map<String, JMethod> methods = new HashMap<String, JMethod>();
  private final Map<String, JType> types = new HashMap<String, JType>();

  {
    put(JPrimitiveType.BOOLEAN, JPrimitiveType.BYTE, JPrimitiveType.CHAR, JPrimitiveType.DOUBLE,
        JPrimitiveType.FLOAT, JPrimitiveType.INT, JPrimitiveType.LONG, JPrimitiveType.SHORT,
        JPrimitiveType.VOID, JNullType.INSTANCE);
  }

  public void clearSource() {
    sourceFields.clear();
    sourceMethods.clear();
    sourceTypes.clear();
  }

  /** @return an existing source or external JType, only if it exists */
  public JType getType(Type type) {
    if (type instanceof Array) {
      return new JArrayType(getType(((Array) type).typ()));
    } 
    return getType(javaName(type));
  }

  public JType getType(String name) {
    JType existing = getExistingType(name);
    if (existing != null) {
      return existing;
    }
    // if no existing type, assume a new external JClassType is okay
    return getClassType(name);
  }

  /**
   * @return an existing source or external type, or makes a new external type. Assumes the caller
   *         knows name is a class type.
   */
  public JClassType getClassType(String name) {
    JClassType existing = (JClassType) getExistingType(name);
    if (existing != null) {
      return existing;
    }
    JClassType newExternal = new JClassType(name);
    assert newExternal.isExternal();
    types.put(name, newExternal);
    // ReferenceMapper fills in super class, interfaces, and clinit...do we need that for these external types?
    // see {@link ReferenceMapper#get(TypeBinding)}
    // Emulate clinit method for super clinit calls.
    {
      JMethod clinit =
          new JMethod(SourceOrigin.UNKNOWN, "$clinit", newExternal, JPrimitiveType.VOID, false,
              true, true, true);
      clinit.freezeParamTypes();
      clinit.setSynthetic();
      newExternal.addMethod(clinit);
    }
    return newExternal;
  }

  /** assumes the caller knows name is an interface type */
  public JInterfaceType getInterfaceType(String name) {
    JInterfaceType existing = (JInterfaceType) getExistingType(name);
    if (existing != null) {
      return existing;
    }
    JInterfaceType newExternal = new JInterfaceType(name);
    assert newExternal.isExternal();
    types.put(name, newExternal);
    // ReferenceMapper fills in interfaces, and clinit
    // see {@link ReferenceMapper#get(TypeBinding)} ... do we need that?
    return newExternal;
  }

  // searches sourceTypes then external types else null
  private JType getExistingType(String name) {
    JType sourceType = sourceTypes.get(name);
    if (sourceType != null) {
      assert !sourceType.isExternal();
      return sourceType;
    }
    JType externalType = types.get(name);
    if (externalType != null) {
      assert externalType instanceof JPrimitiveType || externalType == JNullType.INSTANCE
          || externalType.isExternal();
      return externalType;
    }
    return null;
  }

  private static String key(Signature signature, boolean isCstr) {
    StringBuilder sb = new StringBuilder();
    sb.append(signature.on().javaName());
    sb.append('.');
    // TODO ensure this is needed
    // jribble method call signatures for cstr come in with "this" as the method name
    if (isCstr) {
      sb.append("this");
    } else {
      sb.append(signature.name());
    }
    sb.append('(');
    for (Type paramType : signature.jparamTypes()) {
      sb.append(javaName(paramType));
    }
    sb.append(')');
    sb.append(javaName(signature.returnType()));
    return sb.toString();
  }

  private static String javaName(Type type) {
    if (type instanceof Ref) {
      return ((Ref) type).javaName();
    } else if (type instanceof Array) {
      return javaName(((Array) type).typ());
    } else if (type instanceof Primitive) {
      return ((Primitive) type).name();
    } else if (type == Void$.MODULE$) {
      return "void";
    } else {
      throw new RuntimeException("Unhandled type " + type);
    }
  }

  public JField getField(String typeName, String fieldName) {
    String key = typeName + "." + fieldName + ":";
    JField sourceField = sourceFields.get(key);
    if (sourceField != null) {
      assert !sourceField.isExternal();
      return sourceField;
    }
    JField externalField = fields.get(key);
    if (externalField != null) {
      assert externalField.isExternal();
      return externalField;
    }
    JField newExternal = new JField(fieldName, (JDeclaredType) getType(typeName));
    assert newExternal.isExternal();
    fields.put(key, newExternal);
    return newExternal;
  }

  public JMethod getMethod(Signature signature, boolean isStatic, boolean isCstr) {
    String key = key(signature, isCstr);
    JMethod sourceMethod = sourceMethods.get(key);
    if (sourceMethod != null) {
      assert !sourceMethod.isExternal();
      return sourceMethod;
    }
    JMethod externalMethod = methods.get(key);
    if (externalMethod != null) {
      assert externalMethod.isExternal();
      return externalMethod;
    }
    JMethod newExternal;
    if (isCstr) {
      newExternal =
          new JConstructor(SourceOrigin.UNKNOWN, (JClassType) getType(signature.on().javaName()));
    } else {
      newExternal =
          new JMethod(SourceOrigin.UNKNOWN, signature.name(), (JDeclaredType) getType(signature
              .on().javaName()), getType(javaName(signature.returnType())), false, isStatic, false,
              false);
    }
    assert newExternal.isExternal();
    methods.put(key, newExternal);
    return newExternal;
  }

  public void setSourceMethod(Signature signature, JMethod method) {
    assert !method.isExternal();
    sourceMethods.put(key(signature), method);
  }

  public void setSourceField(ClassDef jrClassDef, FieldDef jrFieldDef, JField field) {
    assert !field.isExternal();
    sourceFields.put(signature(jrClassDef, jrFieldDef), field);
  }

  public void setSourceType(DeclaredType type, JDeclaredType jtype) {
    assert !jtype.isExternal();
    sourceTypes.put(type.name().javaName(), jtype);
  }

  private String intern(String s) {
    return stringInterner.intern(s);
  }

  private void put(JType... baseTypes) {
    for (JType type : baseTypes) {
      types.put(type.getName(), type);
    }
  }

  private static String signature(ClassDef jrClassDef, FieldDef jrField) {
    StringBuilder sb = new StringBuilder();
    sb.append(jrClassDef.name().javaName());
    sb.append('.');
    sb.append(jrField.name());
    sb.append(':');
    // GwtAstBuilder had field type here--why?
    // sb.append(javaName(jrField.typ()));
    return sb.toString();
  }

}
