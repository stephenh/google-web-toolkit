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
package elemental.html;
import elemental.util.Indexable;

import elemental.events.*;
import elemental.util.*;
import elemental.dom.*;
import elemental.html.*;
import elemental.css.*;
import elemental.stylesheets.*;

import java.util.Date;

/**
  * 
  */
public interface WebGLRenderingContext extends CanvasRenderingContext {

    static final int ACTIVE_ATTRIBUTES = 0x8B89;

    static final int ACTIVE_TEXTURE = 0x84E0;

    static final int ACTIVE_UNIFORMS = 0x8B86;

    static final int ALIASED_LINE_WIDTH_RANGE = 0x846E;

    static final int ALIASED_POINT_SIZE_RANGE = 0x846D;

    static final int ALPHA = 0x1906;

    static final int ALPHA_BITS = 0x0D55;

    static final int ALWAYS = 0x0207;

    static final int ARRAY_BUFFER = 0x8892;

    static final int ARRAY_BUFFER_BINDING = 0x8894;

    static final int ATTACHED_SHADERS = 0x8B85;

    static final int BACK = 0x0405;

    static final int BLEND = 0x0BE2;

    static final int BLEND_COLOR = 0x8005;

    static final int BLEND_DST_ALPHA = 0x80CA;

    static final int BLEND_DST_RGB = 0x80C8;

    static final int BLEND_EQUATION = 0x8009;

    static final int BLEND_EQUATION_ALPHA = 0x883D;

    static final int BLEND_EQUATION_RGB = 0x8009;

    static final int BLEND_SRC_ALPHA = 0x80CB;

    static final int BLEND_SRC_RGB = 0x80C9;

    static final int BLUE_BITS = 0x0D54;

    static final int BOOL = 0x8B56;

    static final int BOOL_VEC2 = 0x8B57;

    static final int BOOL_VEC3 = 0x8B58;

    static final int BOOL_VEC4 = 0x8B59;

    static final int BROWSER_DEFAULT_WEBGL = 0x9244;

    static final int BUFFER_SIZE = 0x8764;

    static final int BUFFER_USAGE = 0x8765;

    static final int BYTE = 0x1400;

    static final int CCW = 0x0901;

    static final int CLAMP_TO_EDGE = 0x812F;

    static final int COLOR_ATTACHMENT0 = 0x8CE0;

    static final int COLOR_BUFFER_BIT = 0x00004000;

    static final int COLOR_CLEAR_VALUE = 0x0C22;

    static final int COLOR_WRITEMASK = 0x0C23;

    static final int COMPILE_STATUS = 0x8B81;

    static final int COMPRESSED_TEXTURE_FORMATS = 0x86A3;

    static final int CONSTANT_ALPHA = 0x8003;

    static final int CONSTANT_COLOR = 0x8001;

    static final int CONTEXT_LOST_WEBGL = 0x9242;

    static final int CULL_FACE = 0x0B44;

    static final int CULL_FACE_MODE = 0x0B45;

    static final int CURRENT_PROGRAM = 0x8B8D;

    static final int CURRENT_VERTEX_ATTRIB = 0x8626;

    static final int CW = 0x0900;

    static final int DECR = 0x1E03;

    static final int DECR_WRAP = 0x8508;

    static final int DELETE_STATUS = 0x8B80;

    static final int DEPTH_ATTACHMENT = 0x8D00;

    static final int DEPTH_BITS = 0x0D56;

    static final int DEPTH_BUFFER_BIT = 0x00000100;

    static final int DEPTH_CLEAR_VALUE = 0x0B73;

    static final int DEPTH_COMPONENT = 0x1902;

    static final int DEPTH_COMPONENT16 = 0x81A5;

    static final int DEPTH_FUNC = 0x0B74;

    static final int DEPTH_RANGE = 0x0B70;

    static final int DEPTH_STENCIL = 0x84F9;

    static final int DEPTH_STENCIL_ATTACHMENT = 0x821A;

    static final int DEPTH_TEST = 0x0B71;

    static final int DEPTH_WRITEMASK = 0x0B72;

    static final int DITHER = 0x0BD0;

    static final int DONT_CARE = 0x1100;

    static final int DST_ALPHA = 0x0304;

    static final int DST_COLOR = 0x0306;

    static final int DYNAMIC_DRAW = 0x88E8;

    static final int ELEMENT_ARRAY_BUFFER = 0x8893;

    static final int ELEMENT_ARRAY_BUFFER_BINDING = 0x8895;

    static final int EQUAL = 0x0202;

    static final int FASTEST = 0x1101;

    static final int FLOAT = 0x1406;

    static final int FLOAT_MAT2 = 0x8B5A;

    static final int FLOAT_MAT3 = 0x8B5B;

    static final int FLOAT_MAT4 = 0x8B5C;

    static final int FLOAT_VEC2 = 0x8B50;

    static final int FLOAT_VEC3 = 0x8B51;

    static final int FLOAT_VEC4 = 0x8B52;

    static final int FRAGMENT_SHADER = 0x8B30;

    static final int FRAMEBUFFER = 0x8D40;

    static final int FRAMEBUFFER_ATTACHMENT_OBJECT_NAME = 0x8CD1;

    static final int FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE = 0x8CD0;

    static final int FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE = 0x8CD3;

    static final int FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL = 0x8CD2;

    static final int FRAMEBUFFER_BINDING = 0x8CA6;

    static final int FRAMEBUFFER_COMPLETE = 0x8CD5;

    static final int FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 0x8CD6;

    static final int FRAMEBUFFER_INCOMPLETE_DIMENSIONS = 0x8CD9;

    static final int FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 0x8CD7;

    static final int FRAMEBUFFER_UNSUPPORTED = 0x8CDD;

    static final int FRONT = 0x0404;

    static final int FRONT_AND_BACK = 0x0408;

    static final int FRONT_FACE = 0x0B46;

    static final int FUNC_ADD = 0x8006;

    static final int FUNC_REVERSE_SUBTRACT = 0x800B;

    static final int FUNC_SUBTRACT = 0x800A;

    static final int GENERATE_MIPMAP_HINT = 0x8192;

    static final int GEQUAL = 0x0206;

    static final int GREATER = 0x0204;

    static final int GREEN_BITS = 0x0D53;

    static final int HIGH_FLOAT = 0x8DF2;

    static final int HIGH_INT = 0x8DF5;

    static final int INCR = 0x1E02;

    static final int INCR_WRAP = 0x8507;

    static final int INT = 0x1404;

    static final int INT_VEC2 = 0x8B53;

    static final int INT_VEC3 = 0x8B54;

    static final int INT_VEC4 = 0x8B55;

    static final int INVALID_ENUM = 0x0500;

    static final int INVALID_FRAMEBUFFER_OPERATION = 0x0506;

    static final int INVALID_OPERATION = 0x0502;

    static final int INVALID_VALUE = 0x0501;

    static final int INVERT = 0x150A;

    static final int KEEP = 0x1E00;

    static final int LEQUAL = 0x0203;

    static final int LESS = 0x0201;

    static final int LINEAR = 0x2601;

    static final int LINEAR_MIPMAP_LINEAR = 0x2703;

    static final int LINEAR_MIPMAP_NEAREST = 0x2701;

    static final int LINES = 0x0001;

    static final int LINE_LOOP = 0x0002;

    static final int LINE_STRIP = 0x0003;

    static final int LINE_WIDTH = 0x0B21;

    static final int LINK_STATUS = 0x8B82;

    static final int LOW_FLOAT = 0x8DF0;

    static final int LOW_INT = 0x8DF3;

    static final int LUMINANCE = 0x1909;

    static final int LUMINANCE_ALPHA = 0x190A;

    static final int MAX_COMBINED_TEXTURE_IMAGE_UNITS = 0x8B4D;

    static final int MAX_CUBE_MAP_TEXTURE_SIZE = 0x851C;

    static final int MAX_FRAGMENT_UNIFORM_VECTORS = 0x8DFD;

    static final int MAX_RENDERBUFFER_SIZE = 0x84E8;

    static final int MAX_TEXTURE_IMAGE_UNITS = 0x8872;

    static final int MAX_TEXTURE_SIZE = 0x0D33;

    static final int MAX_VARYING_VECTORS = 0x8DFC;

    static final int MAX_VERTEX_ATTRIBS = 0x8869;

    static final int MAX_VERTEX_TEXTURE_IMAGE_UNITS = 0x8B4C;

    static final int MAX_VERTEX_UNIFORM_VECTORS = 0x8DFB;

    static final int MAX_VIEWPORT_DIMS = 0x0D3A;

    static final int MEDIUM_FLOAT = 0x8DF1;

    static final int MEDIUM_INT = 0x8DF4;

    static final int MIRRORED_REPEAT = 0x8370;

    static final int NEAREST = 0x2600;

    static final int NEAREST_MIPMAP_LINEAR = 0x2702;

    static final int NEAREST_MIPMAP_NEAREST = 0x2700;

    static final int NEVER = 0x0200;

    static final int NICEST = 0x1102;

    static final int NONE = 0;

    static final int NOTEQUAL = 0x0205;

    static final int NO_ERROR = 0;

    static final int ONE = 1;

    static final int ONE_MINUS_CONSTANT_ALPHA = 0x8004;

    static final int ONE_MINUS_CONSTANT_COLOR = 0x8002;

    static final int ONE_MINUS_DST_ALPHA = 0x0305;

    static final int ONE_MINUS_DST_COLOR = 0x0307;

    static final int ONE_MINUS_SRC_ALPHA = 0x0303;

    static final int ONE_MINUS_SRC_COLOR = 0x0301;

    static final int OUT_OF_MEMORY = 0x0505;

    static final int PACK_ALIGNMENT = 0x0D05;

    static final int POINTS = 0x0000;

    static final int POLYGON_OFFSET_FACTOR = 0x8038;

    static final int POLYGON_OFFSET_FILL = 0x8037;

    static final int POLYGON_OFFSET_UNITS = 0x2A00;

    static final int RED_BITS = 0x0D52;

    static final int RENDERBUFFER = 0x8D41;

    static final int RENDERBUFFER_ALPHA_SIZE = 0x8D53;

    static final int RENDERBUFFER_BINDING = 0x8CA7;

    static final int RENDERBUFFER_BLUE_SIZE = 0x8D52;

    static final int RENDERBUFFER_DEPTH_SIZE = 0x8D54;

    static final int RENDERBUFFER_GREEN_SIZE = 0x8D51;

    static final int RENDERBUFFER_HEIGHT = 0x8D43;

    static final int RENDERBUFFER_INTERNAL_FORMAT = 0x8D44;

    static final int RENDERBUFFER_RED_SIZE = 0x8D50;

    static final int RENDERBUFFER_STENCIL_SIZE = 0x8D55;

    static final int RENDERBUFFER_WIDTH = 0x8D42;

    static final int RENDERER = 0x1F01;

    static final int REPEAT = 0x2901;

    static final int REPLACE = 0x1E01;

    static final int RGB = 0x1907;

    static final int RGB565 = 0x8D62;

    static final int RGB5_A1 = 0x8057;

    static final int RGBA = 0x1908;

    static final int RGBA4 = 0x8056;

    static final int SAMPLER_2D = 0x8B5E;

    static final int SAMPLER_CUBE = 0x8B60;

    static final int SAMPLES = 0x80A9;

    static final int SAMPLE_ALPHA_TO_COVERAGE = 0x809E;

    static final int SAMPLE_BUFFERS = 0x80A8;

    static final int SAMPLE_COVERAGE = 0x80A0;

    static final int SAMPLE_COVERAGE_INVERT = 0x80AB;

    static final int SAMPLE_COVERAGE_VALUE = 0x80AA;

    static final int SCISSOR_BOX = 0x0C10;

    static final int SCISSOR_TEST = 0x0C11;

    static final int SHADER_TYPE = 0x8B4F;

    static final int SHADING_LANGUAGE_VERSION = 0x8B8C;

    static final int SHORT = 0x1402;

    static final int SRC_ALPHA = 0x0302;

    static final int SRC_ALPHA_SATURATE = 0x0308;

    static final int SRC_COLOR = 0x0300;

    static final int STATIC_DRAW = 0x88E4;

    static final int STENCIL_ATTACHMENT = 0x8D20;

    static final int STENCIL_BACK_FAIL = 0x8801;

    static final int STENCIL_BACK_FUNC = 0x8800;

    static final int STENCIL_BACK_PASS_DEPTH_FAIL = 0x8802;

    static final int STENCIL_BACK_PASS_DEPTH_PASS = 0x8803;

    static final int STENCIL_BACK_REF = 0x8CA3;

    static final int STENCIL_BACK_VALUE_MASK = 0x8CA4;

    static final int STENCIL_BACK_WRITEMASK = 0x8CA5;

    static final int STENCIL_BITS = 0x0D57;

    static final int STENCIL_BUFFER_BIT = 0x00000400;

    static final int STENCIL_CLEAR_VALUE = 0x0B91;

    static final int STENCIL_FAIL = 0x0B94;

    static final int STENCIL_FUNC = 0x0B92;

    static final int STENCIL_INDEX = 0x1901;

    static final int STENCIL_INDEX8 = 0x8D48;

    static final int STENCIL_PASS_DEPTH_FAIL = 0x0B95;

    static final int STENCIL_PASS_DEPTH_PASS = 0x0B96;

    static final int STENCIL_REF = 0x0B97;

    static final int STENCIL_TEST = 0x0B90;

    static final int STENCIL_VALUE_MASK = 0x0B93;

    static final int STENCIL_WRITEMASK = 0x0B98;

    static final int STREAM_DRAW = 0x88E0;

    static final int SUBPIXEL_BITS = 0x0D50;

    static final int TEXTURE = 0x1702;

    static final int TEXTURE0 = 0x84C0;

    static final int TEXTURE1 = 0x84C1;

    static final int TEXTURE10 = 0x84CA;

    static final int TEXTURE11 = 0x84CB;

    static final int TEXTURE12 = 0x84CC;

    static final int TEXTURE13 = 0x84CD;

    static final int TEXTURE14 = 0x84CE;

    static final int TEXTURE15 = 0x84CF;

    static final int TEXTURE16 = 0x84D0;

    static final int TEXTURE17 = 0x84D1;

    static final int TEXTURE18 = 0x84D2;

    static final int TEXTURE19 = 0x84D3;

    static final int TEXTURE2 = 0x84C2;

    static final int TEXTURE20 = 0x84D4;

    static final int TEXTURE21 = 0x84D5;

    static final int TEXTURE22 = 0x84D6;

    static final int TEXTURE23 = 0x84D7;

    static final int TEXTURE24 = 0x84D8;

    static final int TEXTURE25 = 0x84D9;

    static final int TEXTURE26 = 0x84DA;

    static final int TEXTURE27 = 0x84DB;

    static final int TEXTURE28 = 0x84DC;

    static final int TEXTURE29 = 0x84DD;

    static final int TEXTURE3 = 0x84C3;

    static final int TEXTURE30 = 0x84DE;

    static final int TEXTURE31 = 0x84DF;

    static final int TEXTURE4 = 0x84C4;

    static final int TEXTURE5 = 0x84C5;

    static final int TEXTURE6 = 0x84C6;

    static final int TEXTURE7 = 0x84C7;

    static final int TEXTURE8 = 0x84C8;

    static final int TEXTURE9 = 0x84C9;

    static final int TEXTURE_2D = 0x0DE1;

    static final int TEXTURE_BINDING_2D = 0x8069;

    static final int TEXTURE_BINDING_CUBE_MAP = 0x8514;

    static final int TEXTURE_CUBE_MAP = 0x8513;

    static final int TEXTURE_CUBE_MAP_NEGATIVE_X = 0x8516;

    static final int TEXTURE_CUBE_MAP_NEGATIVE_Y = 0x8518;

    static final int TEXTURE_CUBE_MAP_NEGATIVE_Z = 0x851A;

    static final int TEXTURE_CUBE_MAP_POSITIVE_X = 0x8515;

    static final int TEXTURE_CUBE_MAP_POSITIVE_Y = 0x8517;

    static final int TEXTURE_CUBE_MAP_POSITIVE_Z = 0x8519;

    static final int TEXTURE_MAG_FILTER = 0x2800;

    static final int TEXTURE_MIN_FILTER = 0x2801;

    static final int TEXTURE_WRAP_S = 0x2802;

    static final int TEXTURE_WRAP_T = 0x2803;

    static final int TRIANGLES = 0x0004;

    static final int TRIANGLE_FAN = 0x0006;

    static final int TRIANGLE_STRIP = 0x0005;

    static final int UNPACK_ALIGNMENT = 0x0CF5;

    static final int UNPACK_COLORSPACE_CONVERSION_WEBGL = 0x9243;

    static final int UNPACK_FLIP_Y_WEBGL = 0x9240;

    static final int UNPACK_PREMULTIPLY_ALPHA_WEBGL = 0x9241;

    static final int UNSIGNED_BYTE = 0x1401;

    static final int UNSIGNED_INT = 0x1405;

    static final int UNSIGNED_SHORT = 0x1403;

    static final int UNSIGNED_SHORT_4_4_4_4 = 0x8033;

    static final int UNSIGNED_SHORT_5_5_5_1 = 0x8034;

    static final int UNSIGNED_SHORT_5_6_5 = 0x8363;

    static final int VALIDATE_STATUS = 0x8B83;

    static final int VENDOR = 0x1F00;

    static final int VERSION = 0x1F02;

    static final int VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 0x889F;

    static final int VERTEX_ATTRIB_ARRAY_ENABLED = 0x8622;

    static final int VERTEX_ATTRIB_ARRAY_NORMALIZED = 0x886A;

    static final int VERTEX_ATTRIB_ARRAY_POINTER = 0x8645;

    static final int VERTEX_ATTRIB_ARRAY_SIZE = 0x8623;

    static final int VERTEX_ATTRIB_ARRAY_STRIDE = 0x8624;

    static final int VERTEX_ATTRIB_ARRAY_TYPE = 0x8625;

    static final int VERTEX_SHADER = 0x8B31;

    static final int VIEWPORT = 0x0BA2;

    static final int ZERO = 0;

  int getDrawingBufferHeight();

  int getDrawingBufferWidth();

  void activeTexture(int texture);

  void attachShader(WebGLProgram program, WebGLShader shader);

  void bindAttribLocation(WebGLProgram program, int index, String name);

  void bindBuffer(int target, WebGLBuffer buffer);

  void bindFramebuffer(int target, WebGLFramebuffer framebuffer);

  void bindRenderbuffer(int target, WebGLRenderbuffer renderbuffer);

  void bindTexture(int target, WebGLTexture texture);

  void blendColor(float red, float green, float blue, float alpha);

  void blendEquation(int mode);

  void blendEquationSeparate(int modeRGB, int modeAlpha);

  void blendFunc(int sfactor, int dfactor);

  void blendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha);

  void bufferData(int target, ArrayBuffer data, int usage);

  void bufferData(int target, ArrayBufferView data, int usage);

  void bufferData(int target, double size, int usage);

  void bufferSubData(int target, double offset, ArrayBuffer data);

  void bufferSubData(int target, double offset, ArrayBufferView data);

  int checkFramebufferStatus(int target);

  void clear(int mask);

  void clearColor(float red, float green, float blue, float alpha);

  void clearDepth(float depth);

  void clearStencil(int s);

  void colorMask(boolean red, boolean green, boolean blue, boolean alpha);

  void compileShader(WebGLShader shader);

  void compressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, ArrayBufferView data);

  void compressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, ArrayBufferView data);

  void copyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border);

  void copyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height);

  WebGLBuffer createBuffer();

  WebGLFramebuffer createFramebuffer();

  WebGLProgram createProgram();

  WebGLRenderbuffer createRenderbuffer();

  WebGLShader createShader(int type);

  WebGLTexture createTexture();

  void cullFace(int mode);

  void deleteBuffer(WebGLBuffer buffer);

  void deleteFramebuffer(WebGLFramebuffer framebuffer);

  void deleteProgram(WebGLProgram program);

  void deleteRenderbuffer(WebGLRenderbuffer renderbuffer);

  void deleteShader(WebGLShader shader);

  void deleteTexture(WebGLTexture texture);

  void depthFunc(int func);

  void depthMask(boolean flag);

  void depthRange(float zNear, float zFar);

  void detachShader(WebGLProgram program, WebGLShader shader);

  void disable(int cap);

  void disableVertexAttribArray(int index);

  void drawArrays(int mode, int first, int count);

  void drawElements(int mode, int count, int type, double offset);

  void enable(int cap);

  void enableVertexAttribArray(int index);

  void finish();

  void flush();

  void framebufferRenderbuffer(int target, int attachment, int renderbuffertarget, WebGLRenderbuffer renderbuffer);

  void framebufferTexture2D(int target, int attachment, int textarget, WebGLTexture texture, int level);

  void frontFace(int mode);

  void generateMipmap(int target);

  WebGLActiveInfo getActiveAttrib(WebGLProgram program, int index);

  WebGLActiveInfo getActiveUniform(WebGLProgram program, int index);

  Indexable getAttachedShaders(WebGLProgram program);

  int getAttribLocation(WebGLProgram program, String name);

  Object getBufferParameter(int target, int pname);

  WebGLContextAttributes getContextAttributes();

  int getError();

  Object getExtension(String name);

  Object getFramebufferAttachmentParameter(int target, int attachment, int pname);

  Object getParameter(int pname);

  String getProgramInfoLog(WebGLProgram program);

  Object getProgramParameter(WebGLProgram program, int pname);

  Object getRenderbufferParameter(int target, int pname);

  String getShaderInfoLog(WebGLShader shader);

  Object getShaderParameter(WebGLShader shader, int pname);

  WebGLShaderPrecisionFormat getShaderPrecisionFormat(int shadertype, int precisiontype);

  String getShaderSource(WebGLShader shader);

  Object getTexParameter(int target, int pname);

  Object getUniform(WebGLProgram program, WebGLUniformLocation location);

  WebGLUniformLocation getUniformLocation(WebGLProgram program, String name);

  Object getVertexAttrib(int index, int pname);

  double getVertexAttribOffset(int index, int pname);

  void hint(int target, int mode);

  boolean isBuffer(WebGLBuffer buffer);

  boolean isContextLost();

  boolean isEnabled(int cap);

  boolean isFramebuffer(WebGLFramebuffer framebuffer);

  boolean isProgram(WebGLProgram program);

  boolean isRenderbuffer(WebGLRenderbuffer renderbuffer);

  boolean isShader(WebGLShader shader);

  boolean isTexture(WebGLTexture texture);

  void lineWidth(float width);

  void linkProgram(WebGLProgram program);

  void pixelStorei(int pname, int param);

  void polygonOffset(float factor, float units);

  void readPixels(int x, int y, int width, int height, int format, int type, ArrayBufferView pixels);

  void releaseShaderCompiler();

  void renderbufferStorage(int target, int internalformat, int width, int height);

  void sampleCoverage(float value, boolean invert);

  void scissor(int x, int y, int width, int height);

  void shaderSource(WebGLShader shader, String string);

  void stencilFunc(int func, int ref, int mask);

  void stencilFuncSeparate(int face, int func, int ref, int mask);

  void stencilMask(int mask);

  void stencilMaskSeparate(int face, int mask);

  void stencilOp(int fail, int zfail, int zpass);

  void stencilOpSeparate(int face, int fail, int zfail, int zpass);

  void texImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ArrayBufferView pixels);

  void texImage2D(int target, int level, int internalformat, int format, int type, ImageData pixels);

  void texImage2D(int target, int level, int internalformat, int format, int type, ImageElement image);

  void texImage2D(int target, int level, int internalformat, int format, int type, CanvasElement canvas);

  void texImage2D(int target, int level, int internalformat, int format, int type, VideoElement video);

  void texParameterf(int target, int pname, float param);

  void texParameteri(int target, int pname, int param);

  void texSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, ArrayBufferView pixels);

  void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type, ImageData pixels);

  void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type, ImageElement image);

  void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type, CanvasElement canvas);

  void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type, VideoElement video);

  void uniform1f(WebGLUniformLocation location, float x);

  void uniform1fv(WebGLUniformLocation location, Float32Array v);

  void uniform1i(WebGLUniformLocation location, int x);

  void uniform1iv(WebGLUniformLocation location, Int32Array v);

  void uniform2f(WebGLUniformLocation location, float x, float y);

  void uniform2fv(WebGLUniformLocation location, Float32Array v);

  void uniform2i(WebGLUniformLocation location, int x, int y);

  void uniform2iv(WebGLUniformLocation location, Int32Array v);

  void uniform3f(WebGLUniformLocation location, float x, float y, float z);

  void uniform3fv(WebGLUniformLocation location, Float32Array v);

  void uniform3i(WebGLUniformLocation location, int x, int y, int z);

  void uniform3iv(WebGLUniformLocation location, Int32Array v);

  void uniform4f(WebGLUniformLocation location, float x, float y, float z, float w);

  void uniform4fv(WebGLUniformLocation location, Float32Array v);

  void uniform4i(WebGLUniformLocation location, int x, int y, int z, int w);

  void uniform4iv(WebGLUniformLocation location, Int32Array v);

  void uniformMatrix2fv(WebGLUniformLocation location, boolean transpose, Float32Array array);

  void uniformMatrix3fv(WebGLUniformLocation location, boolean transpose, Float32Array array);

  void uniformMatrix4fv(WebGLUniformLocation location, boolean transpose, Float32Array array);

  void useProgram(WebGLProgram program);

  void validateProgram(WebGLProgram program);

  void vertexAttrib1f(int indx, float x);

  void vertexAttrib1fv(int indx, Float32Array values);

  void vertexAttrib2f(int indx, float x, float y);

  void vertexAttrib2fv(int indx, Float32Array values);

  void vertexAttrib3f(int indx, float x, float y, float z);

  void vertexAttrib3fv(int indx, Float32Array values);

  void vertexAttrib4f(int indx, float x, float y, float z, float w);

  void vertexAttrib4fv(int indx, Float32Array values);

  void vertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, double offset);

  void viewport(int x, int y, int width, int height);
}
