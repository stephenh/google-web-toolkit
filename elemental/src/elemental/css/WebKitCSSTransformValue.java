
package elemental.css;

import elemental.events.*;

/**
  * 
  */
public interface WebKitCSSTransformValue extends CSSValueList {

    static final int CSS_MATRIX = 11;

    static final int CSS_MATRIX3D = 21;

    static final int CSS_PERSPECTIVE = 20;

    static final int CSS_ROTATE = 4;

    static final int CSS_ROTATE3D = 17;

    static final int CSS_ROTATEX = 14;

    static final int CSS_ROTATEY = 15;

    static final int CSS_ROTATEZ = 16;

    static final int CSS_SCALE = 5;

    static final int CSS_SCALE3D = 19;

    static final int CSS_SCALEX = 6;

    static final int CSS_SCALEY = 7;

    static final int CSS_SCALEZ = 18;

    static final int CSS_SKEW = 8;

    static final int CSS_SKEWX = 9;

    static final int CSS_SKEWY = 10;

    static final int CSS_TRANSLATE = 1;

    static final int CSS_TRANSLATE3D = 13;

    static final int CSS_TRANSLATEX = 2;

    static final int CSS_TRANSLATEY = 3;

    static final int CSS_TRANSLATEZ = 12;

  int getOperationType();
}
