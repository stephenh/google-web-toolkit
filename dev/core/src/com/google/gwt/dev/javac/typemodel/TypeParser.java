package com.google.gwt.dev.javac.typemodel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.ext.typeinfo.BadTypeArgsException;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.ParseException;
import com.google.gwt.core.ext.typeinfo.TypeOracleException;

public class TypeParser {
  
  private final TypeOracle oracle;
  
  public TypeParser(TypeOracle oracle) {
    this.oracle = oracle;
  }
  
  public JType parse(String type) throws TypeOracleException {
    // Remove all internal and external whitespace.
    type = type.replaceAll("\\\\s", "");
    // Recursively parse.
    return parseImpl(type);
  }
  
  private JType parseImpl(String type) throws NotFoundException, ParseException,
      BadTypeArgsException {
    if (type.endsWith("[]")) {
      String remainder = type.substring(0, type.length() - 2);
      JType componentType = parseImpl(remainder);
      return oracle.getArrayType(componentType);
    }

    if (type.endsWith(">")) {
      int bracket = type.indexOf('<');
      if (bracket == -1) {
        throw new ParseException("Mismatched brackets; expected '<' to match subsequent '>'");
      }

      // Resolve the raw type.
      //
      String rawTypeName = type.substring(0, bracket);
      JType rawType = parseImpl(rawTypeName);
      if (rawType.isParameterized() != null) {
        // The raw type cannot itself be parameterized.
        //
        throw new BadTypeArgsException(
            "Only non-parameterized classes and interface can be parameterized");
      } else if (rawType.isClassOrInterface() == null) {
        // The raw type must be a class or interface
        // (not an array or primitive).
        //
        throw new BadTypeArgsException("Only classes and interface can be parameterized, so "
            + rawType.getQualifiedSourceName() + " cannot be used in this context");
      } else if (rawType.isGenericType() == null) {
        throw new BadTypeArgsException("'" + rawType.getQualifiedSourceName()
            + "' is not a generic type; only generic types can be parameterized");
      }

      // Resolve each type argument.
      String typeArgContents = type.substring(bracket + 1, type.length() - 1);
      JClassType[] typeArgs = parseTypeArgContents(typeArgContents);

      // Intern this type.
      return oracle.getParameterizedType(rawType.isGenericType(), typeArgs);
    }

    JType result = JPrimitiveType.parse(type);
    if (result != null) {
      return result;
    }

    result = oracle.findType(type);
    if (result != null) {
      return result;
    }

    throw new NotFoundException("Unable to recognize '" + type
        + "' as a type name (is it fully qualified?)");
  }

  private void parseTypeArgComponent(List<JClassType> typeArgList, String typeArgComponent)
      throws NotFoundException, ParseException, BadTypeArgsException {
    JType typeArg = parseImpl(typeArgComponent);
    if (typeArg.isPrimitive() != null) {
      // Cannot be primitive.
      throw new BadTypeArgsException("Type arguments cannot be primitives, so '"
          + typeArg.getQualifiedSourceName() + "' cannot be used in this context");
    }
    typeArgList.add((JClassType) typeArg);
  }

  /**
   * Returns an array of types specified inside of a gwt.typeArgs javadoc
   * annotation.
   */
  private JClassType[] parseTypeArgContents(String typeArgContents) throws ParseException,
      NotFoundException, BadTypeArgsException {
    List<JClassType> typeArgList = new ArrayList<JClassType>();

    int start = 0;
    for (int offset = 0, length = typeArgContents.length(); offset < length; ++offset) {
      char ch = typeArgContents.charAt(offset);
      switch (ch) {
        case '<':
          // scan for closing '>' while ignoring commas
          for (int depth = 1; depth > 0;) {
            if (++offset == length) {
              throw new ParseException("Mismatched brackets; expected '<' to match subsequent '>'");
            }

            char ich = typeArgContents.charAt(offset);
            if (ich == '<') {
              ++depth;
            } else if (ich == '>') {
              --depth;
            }
          }
          break;
        case '>':
          throw new ParseException("No matching '<' for '>'");
        case ',':
          String typeArgComponent = typeArgContents.substring(start, offset);
          parseTypeArgComponent(typeArgList, typeArgComponent);
          start = offset + 1;
          break;
        default:
          break;
      }
    }

    String typeArgComponent = typeArgContents.substring(start);
    parseTypeArgComponent(typeArgList, typeArgComponent);

    JClassType[] typeArgs = typeArgList.toArray(new JClassType[typeArgList.size()]);
    return typeArgs;
  }

}
