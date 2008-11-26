/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.dev.shell;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.shell.BrowserChannel.SessionHandler.ReturnOrException;
import com.google.gwt.dev.shell.BrowserChannel.SessionHandler.SpecialDispatchId;
import com.google.gwt.dev.shell.BrowserChannel.Value.ValueType;
import com.google.gwt.util.tools.Utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 
 */
public abstract class BrowserChannel {

  /**
   * Class representing a reference to a Java object.
   */
  public static class JavaObjectRef {
    private int refId;

    public JavaObjectRef(int refId) {
      this.refId = refId;
    }

    public int getRefid() {
      return Math.abs(refId);
    }

    @Override
    public String toString() {
      return "JavaObjectRef(ref=" + refId + ")";
    }
  }

  /**
   * Class representing a reference to a JS object.
   */
  public static class JsObjectRef {
    private int refId;

    public JsObjectRef(int refId) {
      assert !JSOBJECT_ID_MAP.get().containsKey(refId)
          || (JSOBJECT_ID_MAP.get().get(refId).get() == null);
      this.refId = refId;
    }

    @Override
    public boolean equals(Object o) {
      return (o instanceof JsObjectRef) && ((JsObjectRef) o).refId == refId;
    }

    public int getRefid() {
      // exceptions are negative, so we get the absolute value
      return Math.abs(refId);
    }

    @Override
    public int hashCode() {
      return refId;
    }

    public boolean isException() {
      return refId < 0;
    }

    @Override
    public String toString() {
      return "JsObjectRef(" + refId + ")";
    }
  }

  /**
   * Enumeration of message type ids.
   */
  public enum MessageType {
    Invoke, Return, LoadModule, Quit, LoadJsni, InvokeSpecial, FreeValue;
  }

  /**
   * Hook interface for responding to messages.
   */
  public abstract static class SessionHandler {

    /**
     * Wrapper to return both a return value/exception and a flag as to whether
     * an exception was thrown or not.
     */
    public static class ReturnOrException {
      private final boolean isException;
      private final Value returnValue;

      public ReturnOrException(boolean isException, Value returnValue) {
        this.isException = isException;
        this.returnValue = returnValue;
      }

      public Value getReturnValue() {
        return returnValue;
      }

      public boolean isException() {
        return isException;
      }
    }

    /**
     * Enumeration of dispatch IDs on object 0 (the ServerMethods object).
     * 
     * TODO: hasMethod/hasProperty no longer used, remove them!
     */
    public enum SpecialDispatchId {
      HasMethod, HasProperty, GetProperty, SetProperty,
    }

    public abstract void freeValue(BrowserChannel channel, int[] ids);

    public abstract ReturnOrException getProperty(BrowserChannel channel,
        int refId, int dispId);

    public abstract ReturnOrException invoke(BrowserChannel channel,
        Value thisObj, int dispId, Value[] args);

    public abstract TreeLogger loadModule(TreeLogger logger,
        BrowserChannel channel, String moduleName, String userAgent);

    public abstract ReturnOrException setProperty(BrowserChannel channel,
        int refId, int dispId, Value newValue);

    public abstract void unloadModule(BrowserChannel channel, String moduleName);
  }

  /**
   * Represents a value for BrowserChannel.
   */
  public static class Value {
    /**
     * Enum of type tags sent across the wire.
     */
    public enum ValueType {
      /**
       * Primitive values.
       */
      NULL, BOOLEAN, BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE, STRING,

      /**
       * Representations of Java or JS objects, sent as an index into a table
       * kept on the side holding the actual object.
       */
      JAVA_OBJECT, JS_OBJECT,

      /**
       * A Javascript undef value, also used for void returns.
       */
      UNDEFINED;

      ValueType() {
      }

      byte getTag() {
        return (byte) this.ordinal();
      }
    }

    /**
     * Type tag value.
     */
    private ValueType type = ValueType.UNDEFINED;

    /**
     * Represents a value sent/received across the wire.
     */
    private Object value = null;

    public Value() {
    }

    public Value(Object obj) {
      convertFromJavaValue(obj);
    }

    /**
     * Convert a Java object to a value. Objects must be primitive wrappers,
     * Strings, or JsObjectRef/JavaObjectRef instances.
     * 
     * @param obj value to convert.
     */
    public void convertFromJavaValue(Object obj) {
      if (obj == null) {
        type = ValueType.NULL;
      } else if (obj instanceof Boolean) {
        type = ValueType.BOOLEAN;
      } else if (obj instanceof Byte) {
        type = ValueType.BYTE;
      } else if (obj instanceof Character) {
        type = ValueType.CHAR;
      } else if (obj instanceof Double) {
        type = ValueType.DOUBLE;
      } else if (obj instanceof Float) {
        type = ValueType.FLOAT;
      } else if (obj instanceof Integer) {
        type = ValueType.INT;
      } else if (obj instanceof Long) {
        type = ValueType.LONG;
      } else if (obj instanceof Short) {
        type = ValueType.SHORT;
      } else if (obj instanceof String) {
        type = ValueType.STRING;
      } else if (obj instanceof JsObjectRef) {
        // TODO: exception handling?
        type = ValueType.JS_OBJECT;
      } else if (obj instanceof JavaObjectRef) {
        // TODO: exception handling?
        type = ValueType.JAVA_OBJECT;
      } else {
        throw new RuntimeException(
            "Unexpected Java type in convertFromJavaValue: " + obj);
      }
      value = obj;
    }

    /**
     * Convert a value to the requested Java type.
     * 
     * @param reqType type to convert to
     * @return value as that type.
     */
    public Object convertToJavaType(Class<?> reqType) {
      if (reqType.isArray()) {
        // TODO(jat): handle arrays?
      }
      if (reqType.equals(Boolean.class)) {
        assert type == ValueType.BOOLEAN;
        return value;
      } else if (reqType.equals(Byte.class) || reqType.equals(byte.class)) {
        assert isNumber();
        return Byte.valueOf(((Number) value).byteValue());
      } else if (reqType.equals(Character.class) || reqType.equals(char.class)) {
        if (type == ValueType.CHAR) {
          return value;
        } else {
          assert isNumber();
          return Character.valueOf((char) ((Number) value).shortValue());
        }
      } else if (reqType.equals(Double.class) || reqType.equals(double.class)) {
        assert isNumber();
        return Double.valueOf(((Number) value).doubleValue());
      } else if (reqType.equals(Float.class) || reqType.equals(float.class)) {
        assert isNumber();
        return Float.valueOf(((Number) value).floatValue());
      } else if (reqType.equals(Integer.class) || reqType.equals(int.class)) {
        assert isNumber();
        return Integer.valueOf(((Number) value).intValue());
      } else if (reqType.equals(Long.class) || reqType.equals(long.class)) {
        assert isNumber();
        return Long.valueOf(((Number) value).longValue());
      } else if (reqType.equals(Short.class) || reqType.equals(short.class)) {
        assert isNumber();
        return Short.valueOf(((Number) value).shortValue());
      } else if (reqType.equals(String.class)) {
        assert type == ValueType.STRING;
        return value;
      } else {
        // Wants an object, caller must deal with object references.
        return value;
      }
    }

    public boolean getBoolean() {
      assert type == ValueType.BOOLEAN;
      return ((Boolean) value).booleanValue();
    }

    public byte getByte() {
      assert type == ValueType.BYTE;
      return ((Byte) value).byteValue();
    }

    public char getChar() {
      assert type == ValueType.CHAR;
      return ((Character) value).charValue();
    }

    public double getDouble() {
      assert type == ValueType.DOUBLE;
      return ((Double) value).doubleValue();
    }

    public float getFloat() {
      assert type == ValueType.FLOAT;
      return ((Float) value).floatValue();
    }

    public int getInt() {
      assert type == ValueType.INT;
      return ((Integer) value).intValue();
    }

    public JavaObjectRef getJavaObject() {
      assert type == ValueType.JAVA_OBJECT;
      return (JavaObjectRef) value;
    }

    public JsObjectRef getJsObject() {
      assert type == ValueType.JS_OBJECT;
      return (JsObjectRef) value;
    }

    public long getLong() {
      assert type == ValueType.LONG;
      return ((Long) value).longValue();
    }

    public short getShort() {
      assert type == ValueType.SHORT;
      return ((Short) value).shortValue();
    }

    public String getString() {
      assert type == ValueType.STRING;
      return (String) value;
    }

    public ValueType getType() {
      return type;
    }

    public Object getValue() {
      return value;
    }

    public boolean isBoolean() {
      return type == ValueType.BOOLEAN;
    }

    public boolean isByte() {
      return type == ValueType.BYTE;
    }

    public boolean isChar() {
      return type == ValueType.CHAR;
    }

    public boolean isDouble() {
      return type == ValueType.DOUBLE;
    }

    public boolean isFloat() {
      return type == ValueType.FLOAT;
    }

    public boolean isInt() {
      return type == ValueType.INT;
    }

    public boolean isJavaObject() {
      return type == ValueType.JAVA_OBJECT;
    }

    public boolean isJsObject() {
      return type == ValueType.JS_OBJECT;
    }

    public boolean isLong() {
      return type == ValueType.LONG;
    }

    public boolean isNull() {
      return type == ValueType.NULL;
    }

    public boolean isNumber() {
      switch (type) {
        case BYTE:
        case CHAR:
        case DOUBLE:
        case FLOAT:
        case INT:
        case LONG:
        case SHORT:
          return true;
        default:
          return false;
      }
    }

    public boolean isPrimitive() {
      switch (type) {
        case BOOLEAN:
        case BYTE:
        case CHAR:
        case DOUBLE:
        case FLOAT:
        case INT:
        case LONG:
        case SHORT:
          return true;
        default:
          return false;
      }
    }

    public boolean isShort() {
      return type == ValueType.SHORT;
    }

    public boolean isString() {
      return type == ValueType.STRING;
    }

    public boolean isUndefined() {
      return type == ValueType.UNDEFINED;
    }

    public void setBoolean(boolean val) {
      type = ValueType.BOOLEAN;
      value = Boolean.valueOf(val);
    }

    public void setByte(byte val) {
      type = ValueType.BYTE;
      value = Byte.valueOf(val);
    }

    public void setChar(char val) {
      type = ValueType.CHAR;
      value = Character.valueOf(val);
    }

    public void setDouble(double val) {
      type = ValueType.DOUBLE;
      value = Double.valueOf(val);
    }

    public void setFloat(float val) {
      type = ValueType.FLOAT;
      value = Float.valueOf(val);
    }

    public void setInt(int val) {
      type = ValueType.INT;
      value = Integer.valueOf(val);
    }

    public void setJavaObject(JavaObjectRef val) {
      type = ValueType.JAVA_OBJECT;
      value = val;
    }

    public void setJsObject(JsObjectRef val) {
      type = ValueType.JS_OBJECT;
      value = val;
    }

    public void setLong(long val) {
      type = ValueType.BOOLEAN;
      value = Long.valueOf(val);
    }

    public void setNull() {
      type = ValueType.NULL;
      value = null;
    }

    public void setShort(short val) {
      type = ValueType.SHORT;
      value = Short.valueOf(val);
    }

    public void setString(String val) {
      type = ValueType.STRING;
      value = val;
    }

    public void setUndefined() {
      type = ValueType.UNDEFINED;
      value = null;
    }

    @Override
    public String toString() {
      return type + ": " + value;
    }
  }

  /**
   * A message asking the other side to free object references. Note that there
   * is no response to this message, and this must only be sent immediately
   * before an Invoke or Return message.
   */
  protected static class FreeMessage extends Message {
    public static FreeMessage receive(BrowserChannel channel)
        throws IOException {
      DataInputStream stream = channel.getStreamFromOtherSide();
      int numIds = stream.readInt();
      // TODO: sanity check id count
      int ids[] = new int[numIds];
      for (int i = 0; i < numIds; ++i) {
        ids[i] = stream.readInt();
      }
      return new FreeMessage(channel, ids);
    }

    public static void send(BrowserChannel channel, int[] ids)
        throws IOException {
      DataOutputStream stream = channel.getStreamToOtherSide();
      stream.writeByte(MessageType.FreeValue.ordinal());
      stream.writeInt(ids.length);
      for (int id : ids) {
        stream.writeInt(id);
      }
      stream.flush();
    }

    private final int ids[];

    public FreeMessage(BrowserChannel channel, int[] ids) {
      super(channel);
      this.ids = ids;
    }

    public int[] getIds() {
      return ids;
    }

    @Override
    public boolean isAsynchronous() {
      return true;
    }

    @Override
    public void send() throws IOException {
      send(getBrowserChannel(), ids);
    }
  }

  /**
   * A request from the to invoke a function on the other side.
   */
  protected static class InvokeMessage extends Message {
    public static InvokeMessage receive(BrowserChannel channel)
        throws IOException {
      final DataInputStream stream = channel.getStreamFromOtherSide();
      // NOTE: Tag has already been read.
      final int methodDispatchId = stream.readInt();
      final Value thisRef = readValue(stream);
      final int argLen = stream.readInt();
      final Value[] args = new Value[argLen];
      for (int i = 0; i < argLen; i++) {
        args[i] = readValue(stream);
      }
      return new InvokeMessage(channel, methodDispatchId, thisRef, args);
    }

    private final int methodDispatchId;
    private final String methodName;

    private final Value thisRef;
    private final Value[] args;

    public InvokeMessage(BrowserChannel channel, int methodDispatchId,
        Value thisRef, Value[] args) {
      super(channel);
      this.thisRef = thisRef;
      this.methodName = null;
      this.methodDispatchId = methodDispatchId;
      this.args = args;
    }

    public InvokeMessage(BrowserChannel channel, String methodName,
        Value thisRef, Value[] args) {
      super(channel);
      this.thisRef = thisRef;
      this.methodName = methodName;
      this.methodDispatchId = -1;
      this.args = args;
    }

    public Value[] getArgs() {
      return args;
    }

    public int getMethodDispatchId() {
      return methodDispatchId;
    }

    public String getMethodName() {
      return methodName;
    }

    public Value getThis() {
      return thisRef;
    }

    @Override
    public void send() throws IOException {
      final DataOutputStream stream = getBrowserChannel().getStreamToOtherSide();

      stream.writeByte(MessageType.Invoke.ordinal());
      writeUntaggedString(stream, methodName);
      writeValue(stream, thisRef);
      stream.writeInt(args.length);
      for (int i = 0; i < args.length; i++) {
        writeValue(stream, args[i]);
      }
      stream.flush();
    }
  }

  /**
   * A request from the to invoke a function on the other side.
   */
  protected static class InvokeSpecialMessage extends Message {
    public static InvokeSpecialMessage receive(BrowserChannel channel)
        throws IOException, BrowserChannelException {
      final DataInputStream stream = channel.getStreamFromOtherSide();
      // NOTE: Tag has already been read.
      final int specialMethodInt = stream.readByte();
      SpecialDispatchId[] ids = SpecialDispatchId.values();
      if (specialMethodInt < 0 || specialMethodInt >= ids.length) {
        throw new BrowserChannelException("Invalid dispatch id "
            + specialMethodInt);
      }
      final SpecialDispatchId dispatchId = ids[specialMethodInt];
      final int argLen = stream.readInt();
      final Value[] args = new Value[argLen];
      for (int i = 0; i < argLen; i++) {
        args[i] = readValue(stream);
      }
      return new InvokeSpecialMessage(channel, dispatchId, args);
    }

    private final SpecialDispatchId dispatchId;
    private final Value[] args;

    public InvokeSpecialMessage(BrowserChannel channel,
        SpecialDispatchId dispatchId, Value[] args) {
      super(channel);
      this.dispatchId = dispatchId;
      this.args = args;
    }

    public Value[] getArgs() {
      return args;
    }

    public SpecialDispatchId getDispatchId() {
      return dispatchId;
    }

    @Override
    public void send() throws IOException {
      final DataOutputStream stream = getBrowserChannel().getStreamToOtherSide();

      stream.writeByte(MessageType.InvokeSpecial.ordinal());
      stream.writeByte(dispatchId.ordinal());
      stream.writeInt(args.length);
      for (int i = 0; i < args.length; i++) {
        writeValue(stream, args[i]);
      }
      stream.flush();
    }
  }

  /**
   * A message sending JSNI code to be evaluated. Note that there is no response
   * to this message, and this must only be sent immediately before an Invoke or
   * Return message.
   */
  protected static class LoadJsniMessage extends Message {
    public static LoadJsniMessage receive(BrowserChannel channel)
        throws IOException {
      DataInputStream stream = channel.getStreamFromOtherSide();
      String js = stream.readUTF();
      return new LoadJsniMessage(channel, js);
    }

    public static void send(BrowserChannel channel, String js)
        throws IOException {
      DataOutputStream stream = channel.getStreamToOtherSide();
      stream.write(MessageType.LoadJsni.ordinal());
      writeUntaggedString(stream, js);
      stream.flush();
    }

    private final String js;

    public LoadJsniMessage(BrowserChannel channel, String js) {
      super(channel);
      this.js = js;
    }

    @Override
    public boolean isAsynchronous() {
      return true;
    }

    @Override
    public void send() throws IOException {
      send(getBrowserChannel(), js);
    }
  }

  /**
   * A request from the client that the server load and initialize a given
   * module.
   */
  protected static class LoadModuleMessage extends Message {
    public static LoadModuleMessage receive(BrowserChannel channel)
        throws IOException, BrowserChannelException {
      final DataInputStream stream = channel.getStreamFromOtherSide();
      final int version = stream.readInt();
      checkProtocolVersion(version);
      final String moduleName = readUtf8String(stream);
      final String userAgent = readUtf8String(stream);
      return new LoadModuleMessage(channel, version, moduleName, userAgent);
    }

    private static void checkProtocolVersion(int version)
        throws BrowserChannelException {
      if (version != BROWSERCHANNEL_PROTOCOL_VERSION) {
        throw new BrowserChannelException(
            "Incompatible client version: server="
                + BROWSERCHANNEL_PROTOCOL_VERSION + ", client=" + version);
      }
    }

    private final String moduleName;

    private final String userAgent;

    private final int protocolVersion;

    public LoadModuleMessage(BrowserChannel channel, int protocolVersion,
        String moduleName, String userAgent) {
      super(channel);
      this.moduleName = moduleName;
      this.userAgent = userAgent;
      this.protocolVersion = protocolVersion;
    }

    public String getModuleName() {
      return moduleName;
    }

    public int getProtocolVersion() {
      return protocolVersion;
    }

    public String getUserAgent() {
      return userAgent;
    }

    @Override
    public void send() throws IOException {
      final DataOutputStream stream = getBrowserChannel().getStreamToOtherSide();
      stream.writeByte(MessageType.LoadModule.ordinal());
      stream.writeInt(protocolVersion);
      writeUntaggedString(stream, moduleName);
      writeUntaggedString(stream, userAgent);
      stream.flush();
    }
  }

  /**
   * Abstract base class of OOPHM messages.
   */
  protected abstract static class Message {
    public static MessageType readMessageType(DataInputStream stream)
        throws IOException, BrowserChannelException {
      stream.mark(1);
      int type = stream.readByte();
      MessageType[] types = MessageType.values();
      if (type < 0 || type >= types.length) {
        stream.reset();
        throw new BrowserChannelException("Invalid message type " + type);
      }
      return types[type];
    }

    private final BrowserChannel channel;

    public Message(BrowserChannel channel) {
      this.channel = channel;
    }

    public final BrowserChannel getBrowserChannel() {
      return channel;
    }

    /**
     * @return true if this message type is asynchronous and does not expect a
     *         return message.
     */
    public boolean isAsynchronous() {
      return false;
    }

    // IOException thrown by subclasses
    public void send() throws IOException {
      throw new UnsupportedOperationException(getClass().getName()
          + " is a message format that can only be received.");
    }
  }

  /**
   * A message signifying a soft close of the communications channel.
   */
  protected static class QuitMessage extends Message {
    public static QuitMessage receive(BrowserChannel channel) {
      return new QuitMessage(channel);
    }

    public static void send(BrowserChannel channel) throws IOException {
      final DataOutputStream stream = channel.getStreamToOtherSide();
      stream.writeByte(MessageType.Quit.ordinal());
      stream.flush();
    }

    public QuitMessage(BrowserChannel channel) {
      super(channel);
    }

    @Override
    public void send() throws IOException {
      send(getBrowserChannel());
    }
  }

  /**
   * Signifies a return from a previous invoke.
   */
  protected static class ReturnMessage extends Message {
    public static ReturnMessage receive(BrowserChannel channel)
        throws IOException {
      final DataInputStream stream = channel.getStreamFromOtherSide();
      final boolean isException = stream.readBoolean();
      final Value returnValue = readValue(stream);
      return new ReturnMessage(channel, isException, returnValue);
    }

    public static void send(BrowserChannel channel, boolean isException,
        Value returnValue) throws IOException {
      final DataOutputStream stream = channel.getStreamToOtherSide();
      stream.writeByte(MessageType.Return.ordinal());
      stream.writeBoolean(isException);
      writeValue(stream, returnValue);
      stream.flush();
    }

    public static void send(BrowserChannel channel,
        ReturnOrException returnOrException) throws IOException {
      send(channel, returnOrException.isException(),
          returnOrException.getReturnValue());
    }

    private final Value returnValue;
    private final boolean isException;

    public ReturnMessage(BrowserChannel channel, boolean isException,
        Value returnValue) {
      super(channel);
      this.returnValue = returnValue;
      this.isException = isException;
    }

    public Value getReturnValue() {
      return returnValue;
    }

    public boolean isException() {
      return isException;
    }

    @Override
    public void send() throws IOException {
      send(getBrowserChannel(), isException, returnValue);
    }
  }

  public static final int BROWSERCHANNEL_PROTOCOL_VERSION = 1;

  public static final int SPECIAL_CLIENTMETHODS_OBJECT = 0;

  public static final int SPECIAL_SERVERMETHODS_OBJECT = 0;

  /**
   * This accumulates JsObjectRefs that are no longer referenced in the JVM.
   */
  private static final ThreadLocal<ReferenceQueue<JsObjectRef>> JSOBJECT_REF_QUEUE = new ThreadLocal<ReferenceQueue<JsObjectRef>>() {
    @Override
    protected ReferenceQueue<JsObjectRef> initialValue() {
      return new ReferenceQueue<JsObjectRef>();
    }
  };

  /**
   * This map associates a JS reference id with a Reference to the JSObjectRef
   * that currently represents that id.
   */
  private static final ThreadLocal<Map<Integer, Reference<JsObjectRef>>> JSOBJECT_ID_MAP = new ThreadLocal<Map<Integer, Reference<JsObjectRef>>>() {
    @Override
    protected Map<Integer, Reference<JsObjectRef>> initialValue() {
      return new TreeMap<Integer, Reference<JsObjectRef>>();
    }
  };

  /**
   * This maps References to JsObjectRefs back to the original refId. Because we
   * need the refId of the JsValueRef after it's been garbage-collected, this
   * state must be stored externally.
   */
  private static final ThreadLocal<Map<Reference<JsObjectRef>, Integer>> REFERENCE_ID_MAP = new ThreadLocal<Map<Reference<JsObjectRef>, Integer>>() {
    @Override
    protected Map<Reference<JsObjectRef>, Integer> initialValue() {
      return new IdentityHashMap<Reference<JsObjectRef>, Integer>();
    }
  };

  /**
   * Obtain the JsObjectRef that is currently in use to act as a proxy for the
   * given JS object id.
   */
  protected static JsObjectRef getJsObjectRef(int refId) {
    // Access is implicitly synchronous due to ThreadLocal
    Map<Integer, Reference<JsObjectRef>> map = JSOBJECT_ID_MAP.get();
    if (map.containsKey(refId)) {
      Reference<JsObjectRef> ref = map.get(refId);
      JsObjectRef toReturn = ref.get();
      if (toReturn != null) {
        return toReturn;
      }
    }

    JsObjectRef toReturn = new JsObjectRef(refId);
    Reference<JsObjectRef> ref = new WeakReference<JsObjectRef>(toReturn,
        JSOBJECT_REF_QUEUE.get());
    map.put(refId, ref);
    REFERENCE_ID_MAP.get().put(ref, refId);
    return toReturn;
  }

  protected static String readUtf8String(DataInputStream stream)
      throws IOException {
    final int len = stream.readInt();
    final byte[] data = new byte[len];
    stream.readFully(data);
    return new String(data, "UTF8");
  }

  protected static Value readValue(DataInputStream stream) throws IOException {
    ValueType tag;
    try {
      tag = readValueType(stream);
    } catch (BrowserChannelException e) {
      IOException ee = new IOException();
      ee.initCause(e);
      throw ee;
    }
    Value value = new Value();
    switch (tag) {
      case NULL:
        value.setNull();
        break;
      case UNDEFINED:
        value.setUndefined();
        break;
      case BOOLEAN:
        value.setBoolean(stream.readByte() != 0);
        break;
      case BYTE:
        value.setByte(stream.readByte());
        break;
      case CHAR:
        value.setChar(stream.readChar());
        break;
      case FLOAT:
        value.setFloat(stream.readFloat());
        break;
      case INT:
        value.setInt(stream.readInt());
        break;
      case LONG:
        value.setLong(stream.readLong());
        break;
      case DOUBLE:
        value.setDouble(stream.readDouble());
        break;
      case SHORT:
        value.setShort(stream.readShort());
        break;
      case STRING:
        value.setString(readUtf8String(stream));
        break;
      case JS_OBJECT:
        value.setJsObject(getJsObjectRef(stream.readInt()));
        break;
      case JAVA_OBJECT:
        value.setJavaObject(new JavaObjectRef(stream.readInt()));
        break;
    }
    return value;
  }

  protected static ValueType readValueType(DataInputStream stream)
      throws IOException, BrowserChannelException {
    int type = stream.readByte();
    ValueType[] types = ValueType.values();
    if (type < 0 || type >= types.length) {
      throw new BrowserChannelException("Invalid value type " + type);
    }
    return types[type];
  }

  protected static void writeBoolean(DataOutputStream stream, boolean value)
      throws IOException {
    stream.writeByte(ValueType.BOOLEAN.getTag());
    stream.writeBoolean(value);
  }

  protected static void writeByte(DataOutputStream stream, byte value)
      throws IOException {
    stream.writeByte(ValueType.BYTE.getTag());
    stream.writeByte(value);
  }

  protected static void writeChar(DataOutputStream stream, char value)
      throws IOException {
    stream.writeByte(ValueType.CHAR.getTag());
    stream.writeChar(value);
  }

  protected static void writeDouble(DataOutputStream stream, double value)
      throws IOException {
    stream.writeByte(ValueType.DOUBLE.getTag());
    stream.writeDouble(value);
  }

  protected static void writeFloat(DataOutputStream stream, float value)
      throws IOException {
    stream.writeByte(ValueType.FLOAT.getTag());
    stream.writeFloat(value);
  }

  protected static void writeInt(DataOutputStream stream, int value)
      throws IOException {
    stream.writeByte(ValueType.INT.getTag());
    stream.writeInt(value);
  }

  protected static void writeJavaObject(DataOutputStream stream,
      JavaObjectRef value) throws IOException {
    stream.writeByte(ValueType.JAVA_OBJECT.getTag());
    stream.writeInt(value.getRefid());
  }

  protected static void writeJsObject(DataOutputStream stream, JsObjectRef value)
      throws IOException {
    stream.writeByte(ValueType.JS_OBJECT.getTag());
    stream.writeInt(value.getRefid());
  }

  protected static void writeNull(DataOutputStream stream) throws IOException {
    stream.writeByte(ValueType.NULL.getTag());
  }

  protected static void writeShort(DataOutputStream stream, short value)
      throws IOException {
    stream.writeByte(ValueType.SHORT.getTag());
    stream.writeShort(value);
  }

  protected static void writeUntaggedString(DataOutputStream stream, String data)
      throws IOException {
    try {
      final byte[] bytes = data.getBytes("UTF8");
      stream.writeInt(bytes.length);
      stream.write(bytes);
    } catch (UnsupportedEncodingException e) {
      // TODO: Add description.
      throw new RuntimeException();
    }
  }

  protected static void writeUtf8String(DataOutputStream stream, String data)
      throws IOException {
    stream.writeByte(ValueType.STRING.getTag());
    writeUntaggedString(stream, data);
  }

  protected static void writeValue(DataOutputStream stream, Value value)
      throws IOException {
    if (value.isNull()) {
      writeNull(stream);
    } else if (value.isUndefined()) {
      writeUndefined(stream);
    } else if (value.isJsObject()) {
      writeJsObject(stream, value.getJsObject());
    } else if (value.isJavaObject()) {
      writeJavaObject(stream, value.getJavaObject());
    } else if (value.isBoolean()) {
      writeBoolean(stream, value.getBoolean());
    } else if (value.isByte()) {
      writeByte(stream, value.getByte());
    } else if (value.isChar()) {
      writeChar(stream, value.getChar());
    } else if (value.isShort()) {
      writeShort(stream, value.getShort());
    } else if (value.isDouble()) {
      writeDouble(stream, value.getDouble());
    } else if (value.isFloat()) {
      writeFloat(stream, value.getFloat());
    } else if (value.isInt()) {
      writeInt(stream, value.getInt());
    } else if (value.isString()) {
      writeUtf8String(stream, value.getString());
    } else {
      assert false;
    }
  }

  private static void writeUndefined(DataOutputStream stream)
      throws IOException {
    stream.writeByte(ValueType.UNDEFINED.getTag());
  }

  private final DataInputStream streamFromOtherSide;

  private final DataOutputStream streamToOtherSide;

  private Socket socket;

  public BrowserChannel(Socket socket) throws IOException {
    streamFromOtherSide = new DataInputStream(new BufferedInputStream(
        socket.getInputStream()));
    streamToOtherSide = new DataOutputStream(new BufferedOutputStream(
        socket.getOutputStream()));
    this.socket = socket;
  }

  public void endSession() {
    Utility.close(streamFromOtherSide);
    Utility.close(streamToOtherSide);
    Utility.close(socket);
  }

  public Set<Integer> getRefIdsForCleanup() {
    // Access to these objects is inherently synchronous
    Map<Integer, Reference<JsObjectRef>> objectMap = JSOBJECT_ID_MAP.get();
    Map<Reference<JsObjectRef>, Integer> refIdMap = REFERENCE_ID_MAP.get();
    ReferenceQueue<JsObjectRef> q = JSOBJECT_REF_QUEUE.get();
    Set<Integer> toReturn = new HashSet<Integer>();

    // Find all refIds associated with previous garbage collection cycles
    Reference<? extends JsObjectRef> ref;
    while ((ref = q.poll()) != null) {
      Integer i = refIdMap.remove(ref);
      assert i != null;
      toReturn.add(i);
    }

    /*
     * Check for liveness. This is necessary because the last reference to a
     * JsObjectRef could have been cleared and a new reference to that refId
     * created before this method has been called.
     */
    for (Iterator<Integer> i = toReturn.iterator(); i.hasNext();) {
      Integer refId = i.next();
      if (objectMap.containsKey(refId)) {
        if (objectMap.get(refId).get() != null) {
          i.remove();
        } else {
          objectMap.remove(refId);
        }
      }
    }

    return toReturn;
  }

  public String getRemoteEndpoint() {
    return socket.getInetAddress().getCanonicalHostName() + ":"
        + socket.getPort();
  }

  public Value invoke(String methodName, Value vthis, Value[] vargs,
      SessionHandler handler) throws IOException, BrowserChannelException {
    new InvokeMessage(this, methodName, vthis, vargs).send();
    final ReturnMessage msg = reactToMessagesWhileWaitingForReturn(handler);
    return msg.returnValue;
  }

  public void reactToMessages(SessionHandler handler) throws IOException,
      BrowserChannelException {
    do {
      getStreamToOtherSide().flush();
      MessageType messageType = Message.readMessageType(getStreamFromOtherSide());
      switch (messageType) {
        case FreeValue:
          final FreeMessage freeMsg = FreeMessage.receive(this);
          handler.freeValue(this, freeMsg.getIds());
          break;
        case Invoke:
          final InvokeMessage imsg = InvokeMessage.receive(this);
          ReturnMessage.send(this, handler.invoke(this, imsg.getThis(),
              imsg.getMethodDispatchId(), imsg.getArgs()));
          break;
        case InvokeSpecial:
          handleInvokeSpecial(handler);
          break;
        case Quit:
          return;
        default:
          throw new BrowserChannelException("Invalid message type "
              + messageType);
      }
    } while (true);
  }

  public ReturnMessage reactToMessagesWhileWaitingForReturn(
      SessionHandler handler) throws IOException, BrowserChannelException {
    do {
      getStreamToOtherSide().flush();
      MessageType messageType = Message.readMessageType(getStreamFromOtherSide());
      switch (messageType) {
        case FreeValue:
          final FreeMessage freeMsg = FreeMessage.receive(this);
          handler.freeValue(this, freeMsg.getIds());
          break;
        case Return:
          return ReturnMessage.receive(this);
        case Invoke:
          final InvokeMessage imsg = InvokeMessage.receive(this);
          ReturnMessage.send(this, handler.invoke(this, imsg.getThis(),
              imsg.getMethodDispatchId(), imsg.getArgs()));
          break;
        case InvokeSpecial:
          handleInvokeSpecial(handler);
          break;
        default:
          throw new BrowserChannelException("Invalid message type "
              + messageType + " received waiting for return.");
      }
    } while (true);
  }

  protected DataInputStream getStreamFromOtherSide() {
    return streamFromOtherSide;
  }

  protected DataOutputStream getStreamToOtherSide() {
    return streamToOtherSide;
  }

  private void handleInvokeSpecial(SessionHandler handler) throws IOException,
      BrowserChannelException {
    final InvokeSpecialMessage ismsg = InvokeSpecialMessage.receive(this);
    Value[] args = ismsg.getArgs();
    ReturnOrException retExc = null;
    switch (ismsg.getDispatchId()) {
      case GetProperty:
        assert args.length == 2;
        retExc = handler.getProperty(this, args[0].getInt(), args[1].getInt());
        break;
      case SetProperty:
        assert args.length == 3;
        retExc = handler.setProperty(this, args[0].getInt(), args[1].getInt(),
            args[2]);
        break;
      default:
        throw new HostedModeException("Unexpected InvokeSpecial method "
            + ismsg.getDispatchId());
    }
    ReturnMessage.send(this, retExc);
  }
}
