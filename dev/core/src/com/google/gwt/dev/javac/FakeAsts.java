package com.google.gwt.dev.javac;

import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.SourceOrigin;
import com.google.gwt.dev.jjs.ast.JBlock;
import com.google.gwt.dev.jjs.ast.JCastOperation;
import com.google.gwt.dev.jjs.ast.JClassLiteral;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JConstructor;
import com.google.gwt.dev.jjs.ast.JDeclarationStatement;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JField.Disposition;
import com.google.gwt.dev.jjs.ast.JFieldRef;
import com.google.gwt.dev.jjs.ast.JInterfaceType;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JNewInstance;
import com.google.gwt.dev.jjs.ast.JParameter;
import com.google.gwt.dev.jjs.ast.JParameterRef;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.jjs.ast.JReturnStatement;
import com.google.gwt.dev.jjs.ast.JStringLiteral;
import com.google.gwt.dev.jjs.ast.JThisRef;
import com.google.gwt.dev.jjs.ast.JType;

import java.util.ArrayList;

public class FakeAsts {

  static JDeclaredType makeScalaObject() {
    JInterfaceType t = new JInterfaceType(SourceOrigin.UNKNOWN, "scala.ScalaObject");
    FakeAsts.createSyntheticMethod(SourceOrigin.UNKNOWN, "$clinit", t, JPrimitiveType.VOID, false, true, true, true);
    return t;
  }

  static JDeclaredType makeGreetingService() {
    JInterfaceType t = new JInterfaceType(SourceOrigin.UNKNOWN, "scalatest.client.GreetingService");
    t.addImplements(new JInterfaceType("com.google.gwt.user.client.rpc.RemoteService"));
    // clinit
    JMethod clinit = new JMethod(SourceOrigin.UNKNOWN, "$clinit", t, JPrimitiveType.VOID, false, true, true, true);
    clinit.setBody(new JMethodBody(SourceOrigin.UNKNOWN));
    clinit.freezeParamTypes();
    clinit.setSynthetic();
    t.addMethod(clinit);
    // greet
    JMethod greet = new JMethod(SourceOrigin.UNKNOWN, "greet", t, JPrimitiveType.VOID, true, false, false, false);
    greet.addParam(new JParameter(SourceOrigin.UNKNOWN, "input", new JClassType("java.lang.String"), false, false, greet));
    greet.freezeParamTypes();
    t.addMethod(greet);
    return t;
  }

  static JDeclaredType makeGreetingServiceAsync() {
    JInterfaceType t = new JInterfaceType(SourceOrigin.UNKNOWN, "scalatest.client.GreetingServiceAsync");
    // clinit
    JMethod clinit = new JMethod(SourceOrigin.UNKNOWN, "$clinit", t, JPrimitiveType.VOID, false, true, true, true);
    clinit.setBody(new JMethodBody(SourceOrigin.UNKNOWN));
    clinit.freezeParamTypes();
    clinit.setSynthetic();
    t.addMethod(clinit);
    // greet
    JMethod greet = new JMethod(SourceOrigin.UNKNOWN, "greet", t, JPrimitiveType.VOID, true, false, false, false);
    greet.addParam(new JParameter(SourceOrigin.UNKNOWN, "input", new JClassType("java.lang.String"), false, false, greet));
    greet.addParam(new JParameter(SourceOrigin.UNKNOWN, "callback", new JClassType("com.google.gwt.user.client.rpc.AsyncCallback"), false, false, greet));
    greet.freezeParamTypes();
    t.addMethod(greet);
    return t;
  }

  static JDeclaredType makeScalaTestInnerClass() {
    JClassType jlObject = new JClassType("java.lang.Object");
    JClassType t = new JClassType(SourceOrigin.UNKNOWN, "scalatest.client.ScalaTest$$anon$1", false, false);
    t.setSuperClass(new JClassType("java.lang.Object")); // external
    t.addImplements(new JInterfaceType("scala.ScalaObject")); // external
    t.addImplements(new JInterfaceType("com.google.gwt.user.client.rpc.AsyncCallback")); // external
    JMethod clinit = FakeAsts.createSyntheticMethod(SourceOrigin.UNKNOWN, "$clinit", t, JPrimitiveType.VOID, false, true, true, true);
    JMethod init = FakeAsts.createSyntheticMethod(SourceOrigin.UNKNOWN, "$init", t, JPrimitiveType.VOID, false, false, true, true);
    JMethod getClass = FakeAsts.createSyntheticMethod(SourceOrigin.UNKNOWN, "getClass", t, new JClassType("java.lang.Class"), false, false, false, false);
    ((JMethodBody) getClass.getBody()).getBlock().addStmt(new JReturnStatement(SourceOrigin.UNKNOWN, new JClassLiteral(SourceOrigin.UNKNOWN, t)));
    JMethod jlObjectClinit = new JMethod(SourceOrigin.UNKNOWN, "$clinit", jlObject, JPrimitiveType.VOID, false, true, true, true);
    jlObjectClinit.freezeParamTypes();
    JMethodCall jlObjectClinitCall = new JMethodCall(SourceOrigin.UNKNOWN, null, jlObjectClinit);
    ((JMethodBody) clinit.getBody()).getBlock().addStmt(jlObjectClinitCall.makeStatement());
    // cstr
    JMethod cstr = new JConstructor(SourceOrigin.UNKNOWN, t);
    cstr.addParam(new JParameter(SourceOrigin.UNKNOWN, "outer", new JClassType("scalatest.client.ScalaTest"), false, false, cstr));
    cstr.freezeParamTypes();
    JMethodBody cstrBody = new JMethodBody(SourceOrigin.UNKNOWN);
    cstrBody.setBlock(new JBlock(SourceOrigin.UNKNOWN));
    // manually call super and init
    JConstructor objectCstr = new JConstructor(SourceOrigin.UNKNOWN, jlObject);
    objectCstr.freezeParamTypes();
    jlObject.addMethod(objectCstr);
    JMethodCall superCall = new JMethodCall(SourceOrigin.UNKNOWN, new JThisRef(SourceOrigin.UNKNOWN, t), objectCstr);
    superCall.setStaticDispatchOnly();
    cstrBody.getBlock().addStmt(superCall.makeStatement());
    cstr.setBody(cstrBody);
    JMethodCall initCall = new JMethodCall(SourceOrigin.UNKNOWN, new JThisRef(SourceOrigin.UNKNOWN, t), init);
    cstrBody.getBlock().addStmt(initCall.makeStatement());
    t.addMethod(cstr);
    // onSuccess
    {
      JMethod onSuccess = new JMethod(SourceOrigin.UNKNOWN, "onSuccess", t, JPrimitiveType.VOID, false, false, false, false);
      JParameter result = new JParameter(SourceOrigin.UNKNOWN, "result", new JClassType("java.lang.String"), false, false, onSuccess);
      onSuccess.addParam(result);
      onSuccess.freezeParamTypes();
      // create mini ASTs for Window.alert call
      JDeclaredType window = new JClassType("com.google.gwt.user.client.Window");
      JMethod alert = new JMethod(SourceOrigin.UNKNOWN, "alert", window, JPrimitiveType.VOID, false, true, false, false);
      alert.addParam(new JParameter(SourceOrigin.UNKNOWN, "message", new JClassType("java.lang.String"), false ,false, alert));
      alert.freezeParamTypes();
      window.addMethod(alert);
      JMethodCall alertCall = new JMethodCall(SourceOrigin.UNKNOWN, null, alert);
      alertCall.addArg(new JParameterRef(SourceOrigin.UNKNOWN, result));
      JBlock block = new JBlock(SourceOrigin.UNKNOWN);
      block.addStmt(alertCall.makeStatement());
      JMethodBody body = new JMethodBody(SourceOrigin.UNKNOWN);
      body.setBlock(block);
      onSuccess.setBody(body);
      t.addMethod(onSuccess);
      // onSuccess(Object)
      {
        JMethod onSuccess2 = new JMethod(SourceOrigin.UNKNOWN, "onSuccess", t, JPrimitiveType.VOID, false, false, false, false);
        JParameter result2 = new JParameter(SourceOrigin.UNKNOWN, "result", new JClassType("java.lang.Object"), false, false, onSuccess);
        onSuccess2.addParam(result2);
        onSuccess2.freezeParamTypes();
        JMethodCall cast = new JMethodCall(SourceOrigin.UNKNOWN, new JThisRef(SourceOrigin.UNKNOWN, t), onSuccess);
        cast.addArg(new JCastOperation(SourceOrigin.UNKNOWN, new JClassType("java.lang.String"), new JParameterRef(SourceOrigin.UNKNOWN, result2)));
        JBlock block2 = new JBlock(SourceOrigin.UNKNOWN);
        block2.addStmt(cast.makeStatement());
        JMethodBody body2 = new JMethodBody(SourceOrigin.UNKNOWN);
        body2.setBlock(block2);
        onSuccess2.setBody(body2);
        t.addMethod(onSuccess2);
      }
    }
    // onFailure
    {
      JMethod onFailure = new JMethod(SourceOrigin.UNKNOWN, "onFailure", t, JPrimitiveType.VOID, false, false, false, false);
      onFailure.addParam(new JParameter(SourceOrigin.UNKNOWN, "caught", new JClassType("java.lang.Throwable"), false, false, onFailure));
      JBlock block = new JBlock(SourceOrigin.UNKNOWN);
      JMethodBody body = new JMethodBody(SourceOrigin.UNKNOWN);
      body.setBlock(block);
      onFailure.setBody(body);
      onFailure.setOriginalTypes(JPrimitiveType.VOID, new ArrayList<JType>());
      t.addMethod(onFailure);
    }
    return t;
  }

  static JDeclaredType makeScalaTest() {
    JClassType jlObject = new JClassType("java.lang.Object");
    JClassType t = new JClassType(SourceOrigin.UNKNOWN, "scalatest.client.ScalaTest", false, false);
    t.setSuperClass(jlObject); // external
    t.addImplements(new JInterfaceType("scala.ScalaObject")); // external
    t.addImplements(new JInterfaceType("com.google.gwt.core.client.EntryPoint")); // external
    JMethod clinit = FakeAsts.createSyntheticMethod(SourceOrigin.UNKNOWN, "$clinit", t, JPrimitiveType.VOID, false, true, true, true);
    JMethod init = FakeAsts.createSyntheticMethod(SourceOrigin.UNKNOWN, "$init", t, JPrimitiveType.VOID, false, false, true, true);
    JMethod getClass = FakeAsts.createSyntheticMethod(SourceOrigin.UNKNOWN, "getClass", t, new JClassType("java.lang.Class"), false, false, false, false);
    ((JMethodBody) getClass.getBody()).getBlock().addStmt(new JReturnStatement(SourceOrigin.UNKNOWN, new JClassLiteral(SourceOrigin.UNKNOWN, t)));
    // clinit
    JMethod jlObjectClinit = new JMethod(SourceOrigin.UNKNOWN, "$clinit", jlObject, JPrimitiveType.VOID, false, true, true, true);
    jlObjectClinit.freezeParamTypes();
    JMethodCall jlObjectClinitCall = new JMethodCall(SourceOrigin.UNKNOWN, null, jlObjectClinit);
    ((JMethodBody) clinit.getBody()).getBlock().addStmt(jlObjectClinitCall.makeStatement());
    // cstr
    JMethod cstr = new JConstructor(SourceOrigin.UNKNOWN, t);
    cstr.freezeParamTypes();
    JMethodBody cstrBody = new JMethodBody(SourceOrigin.UNKNOWN);
    cstr.setBody(cstrBody);
    t.addMethod(cstr);
    // manually call super and init
    JConstructor objectCstr = new JConstructor(SourceOrigin.UNKNOWN, jlObject);
    objectCstr.freezeParamTypes();
    jlObject.addMethod(objectCstr);
    JMethodCall superCall = new JMethodCall(SourceOrigin.UNKNOWN, new JThisRef(SourceOrigin.UNKNOWN, t), objectCstr);
    superCall.setStaticDispatchOnly();
    cstrBody.getBlock().addStmt(superCall.makeStatement());
    JMethodCall initCall = new JMethodCall(SourceOrigin.UNKNOWN, new JThisRef(SourceOrigin.UNKNOWN, t), init);
    cstrBody.getBlock().addStmt(initCall.makeStatement());
    // GreetingServiceAsync field
    JDeclaredType gwt = new JClassType("com.google.gwt.core.client.GWT");
    JMethod gwtCreate = new JMethod(SourceOrigin.UNKNOWN, "create", gwt, new JClassType("java.lang.Object"), false, true, false, false);
    gwtCreate.addParam(new JParameter(SourceOrigin.UNKNOWN, "classLiteral", new JClassType("java.lang.Class"), false, false, gwtCreate));
    gwtCreate.freezeParamTypes();
    gwt.addMethod(gwtCreate);
    JDeclaredType gs = new JInterfaceType("scalatest.client.GreetingService");
    JDeclaredType gsAsync = new JInterfaceType("scalatest.client.GreetingServiceAsync");
    JField gsField = new JField(SourceOrigin.UNKNOWN, "greetingService", t, gsAsync, false, Disposition.FINAL);
    JMethodCall gsInit = new JMethodCall(SourceOrigin.UNKNOWN, null, gwtCreate, gsAsync);
    gsInit.addArg(new JClassLiteral(SourceOrigin.UNKNOWN, gs));
    JFieldRef gsFieldRef = new JFieldRef(SourceOrigin.UNKNOWN, new JThisRef(SourceOrigin.UNKNOWN, t), gsField, t);
    ((JMethodBody) init.getBody()).getBlock().addStmt(new JDeclarationStatement(SourceOrigin.UNKNOWN, gsFieldRef, gsInit));
    t.addField(gsField);
    // onModuleLoad
    JMethod onModuleLoad = new JMethod(SourceOrigin.UNKNOWN, "onModuleLoad", t, JPrimitiveType.VOID, false, false, false, false);
    JMethodBody body = new JMethodBody(SourceOrigin.UNKNOWN);
    // create mini ASTs for Window.alert call
    JDeclaredType window = new JClassType("com.google.gwt.user.client.Window");
    JMethod alert = new JMethod(SourceOrigin.UNKNOWN, "alert", window, JPrimitiveType.VOID, false, true, false, false);
    alert.addParam(new JParameter(SourceOrigin.UNKNOWN, "message", new JClassType("java.lang.String"), false ,false, alert));
    alert.freezeParamTypes();
    window.addMethod(alert);
    JMethodCall alertCall = new JMethodCall(SourceOrigin.UNKNOWN, null, alert);
    JClassType stringType = new JClassType("java.lang.String");
    alertCall.addArg(new JStringLiteral(SourceOrigin.UNKNOWN, "hello from CompilationStateBuilder", stringType));
    JBlock block = new JBlock(SourceOrigin.UNKNOWN);
    block.addStmt(alertCall.makeStatement());
    // greetingService.greet
    JMethod greet = new JMethod("greet(Ljava/lang/String;Lcom/google/gwt/user/client/rpc/AsyncCallback;)V", gsAsync);
    JFieldRef gsFieldRef2 = new JFieldRef(SourceOrigin.UNKNOWN, new JThisRef(SourceOrigin.UNKNOWN, t), gsField, t);
    JMethodCall greetCall = new JMethodCall(SourceOrigin.UNKNOWN, gsFieldRef2, greet);
    greetCall.addArg(new JStringLiteral(SourceOrigin.UNKNOWN, "hello", stringType));
    JClassType callbackType = new JClassType("scalatest.client.ScalaTest$$anon$1");
    JConstructor callbackCstr = new JConstructor(SourceOrigin.UNKNOWN, callbackType);
    callbackCstr.addParam(new JParameter(SourceOrigin.UNKNOWN, "outer", t, false, false, callbackCstr));
    callbackCstr.freezeParamTypes();
    callbackType.addMethod(callbackCstr);
    JMethodCall callbackNew = new JNewInstance(SourceOrigin.UNKNOWN, callbackCstr, t);
    callbackNew.addArg(new JThisRef(SourceOrigin.UNKNOWN, t));
    greetCall.addArg(callbackNew);
    block.addStmt(greetCall.makeStatement());
    // finish method body
    body.setBlock(block);
    onModuleLoad.setBody(body);
    onModuleLoad.setOriginalTypes(JPrimitiveType.VOID, new ArrayList<JType>());
    t.addMethod(onModuleLoad);
    return t;
  }

  static JMethod createSyntheticMethod(SourceInfo info, String name, JDeclaredType enclosingType,
      JType returnType, boolean isAbstract, boolean isStatic, boolean isFinal, boolean isPrivate) {
    JMethod method = new JMethod(info, name, enclosingType, returnType, isAbstract, isStatic, isFinal, isPrivate);
    method.freezeParamTypes();
    method.setSynthetic();
    method.setBody(new JMethodBody(info));
    enclosingType.addMethod(method);
    return method;
  }

}
