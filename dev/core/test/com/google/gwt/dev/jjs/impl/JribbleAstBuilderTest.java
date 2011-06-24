package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JNode;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.util.AbstractTextOutput;
import com.google.gwt.dev.util.TextOutput;
import com.google.jribble.ast.Array;
import com.google.jribble.ast.ArrayInitializer;
import com.google.jribble.ast.ArrayRef;
import com.google.jribble.ast.Assignment;
import com.google.jribble.ast.Block;
import com.google.jribble.ast.ClassBodyElement;
import com.google.jribble.ast.ClassDef;
import com.google.jribble.ast.Constructor;
import com.google.jribble.ast.ConstructorCall;
import com.google.jribble.ast.Expression;
import com.google.jribble.ast.FieldDef;
import com.google.jribble.ast.FieldRef;
import com.google.jribble.ast.IntLiteral;
import com.google.jribble.ast.InterfaceDef;
import com.google.jribble.ast.MethodCall;
import com.google.jribble.ast.MethodDef;
import com.google.jribble.ast.NewArray;
import com.google.jribble.ast.NewCall;
import com.google.jribble.ast.Package;
import com.google.jribble.ast.ParamDef;
import com.google.jribble.ast.Primitive;
import com.google.jribble.ast.Ref;
import com.google.jribble.ast.Return;
import com.google.jribble.ast.Signature;
import com.google.jribble.ast.Statement;
import com.google.jribble.ast.StaticFieldRef;
import com.google.jribble.ast.StaticMethodCall;
import com.google.jribble.ast.StringLiteral;
import com.google.jribble.ast.Try;
import com.google.jribble.ast.ThisRef$;
import com.google.jribble.ast.Type;
import com.google.jribble.ast.VarDef;
import com.google.jribble.ast.VarRef;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import scala.Option;
import scala.Some;
import scala.Tuple3;
import scala.collection.immutable.HashSet;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.collection.immutable.Set;
import scala.collection.immutable.$colon$colon;

public class JribbleAstBuilderTest extends TestCase {

  public void testEmptyClass() throws Exception {
    ClassDefBuilder foo = new ClassDefBuilder("foo.Bar");
    JDeclaredType fooType = new JribbleAstBuilder().process(foo.build()).get(0);
    assertEquals(fooType, "testEmptyClass");
  }

  public void testOneVoidMethod() throws Exception {
    ClassDefBuilder foo = new ClassDefBuilder("foo.Bar");
    MethodDefBuilder zaz = new MethodDefBuilder("zaz");
    Option<Expression> none = Option.apply(null);
    zaz.stmts = list((Statement) new Return(none));
    foo.classBody = list(zaz.build());

    JDeclaredType fooType = new JribbleAstBuilder().process(foo.build()).get(0);
    assertEquals(fooType, "testOneVoidMethod");
    Assert.assertEquals("zaz()V", fooType.getMethods().get(3).getSignature());
    Assert.assertFalse(fooType.isExternal());
    Assert.assertEquals(JPrimitiveType.VOID, fooType.getMethods().get(3).getOriginalReturnType());
  }

  public void testOneStringMethod() throws Exception {
    ClassDefBuilder foo = new ClassDefBuilder("foo.Bar");
    MethodDefBuilder zaz = new MethodDefBuilder("zaz");
    zaz.returnType = toRef("java.lang.String");
    zaz.stmts = list((Statement) new Return(new Some<Expression>(new StringLiteral("hello"))));
    foo.classBody = list(zaz.build());

    JDeclaredType fooType = new JribbleAstBuilder().process(foo.build()).get(0);
    assertEquals(fooType, "testOneStringMethod");
    Assert.assertEquals("zaz()Ljava/lang/String;", fooType.getMethods().get(3).getSignature());
    Assert.assertFalse(fooType.isExternal());
    Assert.assertTrue(fooType.getMethods().get(3).getOriginalReturnType().isExternal());
  }

  public void testLocalVariables() throws Exception {
    ClassDefBuilder foo = new ClassDefBuilder("foo.Bar");
    MethodDefBuilder zaz = new MethodDefBuilder("zaz");
    Option<Expression> none = Option.apply(null);

    Statement intDef = new VarDef(new Primitive("int"), "i", none);
    Statement intAssignment = new Assignment(new VarRef("i"), new IntLiteral(10));
    Statement stringDef = new VarDef(toRef("java.lang.String"), "s", none);
    Statement stringAssignment = new Assignment(new VarRef("s"), new StringLiteral("string"));

    zaz.stmts = list(intDef, intAssignment, stringDef, stringAssignment);
    foo.classBody = list(zaz.build());

    JDeclaredType fooType = new JribbleAstBuilder().process(foo.build()).get(0);
    assertEquals(fooType, "testLocalVariables");
  }

  public void testTryCatchFinally() throws Exception {
    ClassDefBuilder foo = new ClassDefBuilder("foo.Bar");
    MethodDefBuilder zaz = new MethodDefBuilder("zaz");
    Option<Expression> none = Option.apply(null);

    Statement intDef = new VarDef(new Primitive("int"), "i", none);
    Block tryBlock = new Block(list(intDef));

    Tuple3<Ref, String, Block> catchTuple =
        new Tuple3<Ref, String, Block>(toRef("java.lang.Exception"), "e", new Block(
            list(newWindowAlert("caught"))));
    List<Tuple3<Ref, String, Block>> catches = list(catchTuple);

    Block finalBlock = new Block(list(newWindowAlert("finally")));

    Statement tryStmt = new Try(tryBlock, catches, Option.apply(finalBlock));

    zaz.stmts = list(tryStmt);
    foo.classBody = list(zaz.build());

    JDeclaredType fooType = new JribbleAstBuilder().process(foo.build()).get(0);
    assertEquals(fooType, "testTryCatchFinally");
  }

  public void testNewCall() throws Exception {
    ClassDefBuilder foo = new ClassDefBuilder("foo.Bar");
    MethodDefBuilder zaz = new MethodDefBuilder("zaz");

    // what do cstr signatures actually look like?
    Signature cstrSig =
        new Signature(toRef("java.util.ArrayList"), "ArrayList", list((Type) new Primitive("int")),
            toRef("java.util.ArrayList"));
    Expression cstrArg = new IntLiteral(1);
    Statement varDef =
        new VarDef(toRef("java.util.List"), "l", new Some<Expression>(new NewCall(
            new ConstructorCall(cstrSig, list(cstrArg)))));

    Signature addSig =
        new Signature(toRef("java.util.List"), "add", list((Type) toRef("java.lang.Object")),
            new Primitive("void"));
    Expression addParam = new IntLiteral(1); // should be boxed
    Statement addCall = new MethodCall(new VarRef("l"), addSig, list(addParam));

    zaz.stmts = list(varDef, addCall);
    foo.classBody = list(zaz.build());

    JDeclaredType fooType = new JribbleAstBuilder().process(foo.build()).get(0);
    assertEquals(fooType, "testNewCall");
  }

  public void testConstructors() throws Exception {
    ClassDefBuilder foo = new ClassDefBuilder("foo.Bar");

    Set<String> modifs = new HashSet<String>();
    Constructor cstr1 =
        new Constructor(modifs, "Bar", list(new ParamDef("s", toRef("java.lang.String"))),
            new Block(list(newWindowAlertVar("s"))));
    // cstr2 calls cstr1, which means it shouldn't have an $init call
    ConstructorCall cstr1Call =
        new ConstructorCall(new Signature(toRef("foo.Bar"), "Bar",
            list((Type) toRef("java.lang.String")), new Primitive("void")),
            list((Expression) new StringLiteral("a")));
    Constructor cstr2 =
        new Constructor(modifs, "Bar", list(new ParamDef("i", toRef("java.lang.Integer"))),
            new Block(list(cstr1Call, newWindowAlertVar("i"))));

    foo.classBody = list((ClassBodyElement) cstr1, cstr2);

    JDeclaredType fooType = new JribbleAstBuilder().process(foo.build()).get(0);
    assertEquals(fooType, "testConstructors");
  }

  public void testFields() throws Exception {
    ClassDefBuilder foo = new ClassDefBuilder("foo.Bar");
    Option<Expression> none = Option.apply(null);

    // initialized
    FieldDef f1 =
        new FieldDef(set("private"), toRef("java.lang.String"), "f1", new Some<Expression>(
            new StringLiteral("f1")));
    // un-initialized
    FieldDef f2 = new FieldDef(set("private"), toRef("java.lang.String"), "f2", none);
    // static initialized
    FieldDef f3 =
        new FieldDef(set("private", "static"), toRef("java.lang.String"), "f3",
            new Some<Expression>(new StringLiteral("f3")));
    // static un-initialized
    FieldDef f4 = new FieldDef(set("private", "static"), toRef("java.lang.String"), "f4", none);

    MethodDefBuilder zaz = new MethodDefBuilder("zaz");
    zaz.params = list(new ParamDef("other", toRef("foo.Bar")));
    Statement assignf1 =
        new Assignment(new FieldRef(ThisRef$.MODULE$, toRef("foo.Bar"), "f1"), new StringLiteral(
            "f11"));
    Statement assignf2 =
        new Assignment(new FieldRef(new VarRef("other"), toRef("foo.Bar"), "f2"),
            new StringLiteral("f22"));
    Statement assignf3 =
        new Assignment(new StaticFieldRef(toRef("foo.Bar"), "f3"), new StringLiteral("f33"));
    Statement assignfOther =
        new Assignment(new StaticFieldRef(toRef("foo.Other"), "i"), new IntLiteral(1));
    zaz.stmts = list(assignf1, assignf2, assignf3, assignfOther);
    foo.classBody = list((ClassBodyElement) f1, f2, f3, f4, zaz.build());

    JDeclaredType fooType = new JribbleAstBuilder().process(foo.build()).get(0);
    assertEquals(fooType, "testFields");
    assertEquals(4, fooType.getFields().size());
  }

  public void testInterface() throws Exception {
    Option<Block> none = Option.apply(null);
    // are interface methods abstract? otherwise SourceGenerationVisitor fails
    MethodDef zaz =
        new MethodDef(set("abstract"), toRef("java.lang.String"), "zaz", list(new ParamDef("x",
            toRef("java.lang.Integer"))), none);
    InterfaceDef specialList =
        new InterfaceDef(set("private"), toRef("foo.SpecialList"), list(toRef("java.util.List")),
            list(zaz));

    JDeclaredType fooType = new JribbleAstBuilder().process(specialList).get(0);
    assertEquals(fooType, "testInterface");
    Assert.assertTrue(fooType.getMethods().get(1).isAbstract());
  }

  public void testArrays() throws Exception {
    ClassDefBuilder foo = new ClassDefBuilder("foo.Bar");
    MethodDefBuilder zaz = new MethodDefBuilder("zaz");

    Type stringAA = new Array(new Array(toRef("java.lang.String")));
    Statement s1 =
        new VarDef(stringAA, "aa", new Some<Expression>(new NewArray(stringAA, list(
            (Option<Expression>) new Some<Expression>(new IntLiteral(1)), new Some<Expression>(
                new IntLiteral(1))))));
    Statement s2 =
        new Assignment(new ArrayRef(new ArrayRef(new VarRef("aa"), new IntLiteral(0)),
            new IntLiteral(0)), new StringLiteral("s"));

    Type stringA = new Array(toRef("java.lang.String"));
    Statement s3 =
        new VarDef(stringA, "a", new Some<Expression>(new ArrayInitializer(stringA, list(
            (Expression) new StringLiteral("1"), new StringLiteral("2")))));
    Statement s4 =
        new Assignment(new ArrayRef(new VarRef("a"), new IntLiteral(0)), new StringLiteral("0"));

    zaz.stmts = list(s1, s2, s3, s4);
    foo.classBody = list(zaz.build());

    JDeclaredType fooType = new JribbleAstBuilder().process(foo.build()).get(0);
    assertEquals(fooType, "testArrays");
  }

  private static Statement newWindowAlert(String message) {
    Expression alertParam = new StringLiteral(message);
    return new StaticMethodCall(toRef("gwt.Window"), new Signature(toRef("gwt.Window"), "alert",
        list((Type) toRef("java.lang.String")), new Primitive("void")), list(alertParam));
  }

  private static Statement newWindowAlertVar(String varName) {
    Expression alertParam = new VarRef(varName);
    return new StaticMethodCall(toRef("gwt.Window"), new Signature(toRef("gwt.Window"), "alert",
        list((Type) toRef("java.lang.String")), new Primitive("void")), list(alertParam));
  }

  private static class MethodDefBuilder {
    private final String name;
    Set<String> modifs = new HashSet<String>();
    List<ParamDef> params = list();
    List<Statement> stmts = list();
    Type returnType = new Primitive("void");

    private MethodDefBuilder(String name) {
      this.name = name;
    }

    private ClassBodyElement build() {
      return new MethodDef(modifs, returnType, name, params, new Some<Block>(new Block(stmts)));
    }
  }

  private void assertEquals(JNode node, String name) throws IOException {
    dump(node, name);

    String actualFile =
        "../../dev/core/test/" + getClass().getName().replace('.', '/') + "." + name
            + ".ast.actual";
    String actual = FileUtils.readFileToString(new File(actualFile));

    String expectedFile =
        "../../dev/core/test/" + getClass().getName().replace('.', '/') + "." + name + ".ast";
    String expected;
    if (new File(expectedFile).exists()) {
      expected = FileUtils.readFileToString(new File(expectedFile));
    } else {
      expected = "";
    }

    Assert.assertEquals(expected, actual);
  }

  private void dump(JNode node, String name) throws IOException {
    String dumpFile =
        "../../dev/core/test/" + getClass().getName().replace('.', '/') + "." + name
            + ".ast.actual";
    FileOutputStream os = new FileOutputStream(dumpFile, false);
    final PrintWriter pw = new PrintWriter(os);
    TextOutput out = new AbstractTextOutput(false) {
      {
        setPrintWriter(pw);
      }
    };
    SourceGenerationVisitor v = new SourceGenerationVisitor(out);
    v.accept(node);
    pw.close();
  }

  private static class ClassDefBuilder {
    private final Ref name;
    private Set<String> modifs = new HashSet<String>();
    private Option<Ref> ext = new Some<Ref>(toRef("java.lang.Object"));
    private List<Ref> impls = list();
    private List<ClassBodyElement> classBody = list();

    private ClassDefBuilder(String name) {
      this.name = toRef(name);
    }

    private ClassDef build() {
      return new ClassDef(modifs, name, ext, impls, classBody);
    }
  }

  private static <T> List<T> list() {
    return List$.MODULE$.empty();
  }

  private static <T> List<T> list(T t) {
    List<T> result = List$.MODULE$.empty();
    result = new $colon$colon<T>(t, result);
    return result;
  }

  private static <T> List<T> list(T... ts) {
    List<T> result = List$.MODULE$.empty();
    for (int i = ts.length; i > 0; i--) {
      result = new $colon$colon<T>(ts[i - 1], result);
    }
    return result;
  }

  private static <T> Set<T> set(T... ts) {
    return list(ts).toSet();
  }

  private static Ref toRef(String name) {
    int i = name.lastIndexOf(".");
    if (i == -1) {
      Option<Package> none = Option.apply(null);
      return new Ref(none, name);
    } else {
      return new Ref(new Some<Package>(new Package(name.substring(0, i))), name.substring(i + 1));
    }
  }

}
