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
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JBinaryOperation;
import com.google.gwt.dev.jjs.ast.JBinaryOperator;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JModVisitor;
import com.google.gwt.dev.jjs.ast.JPostfixOperation;
import com.google.gwt.dev.jjs.ast.JPrefixOperation;
import com.google.gwt.dev.jjs.ast.JProgram;

/**
 * Most autoboxing is handled by {@link GenerateJavaAST}. The only cases it
 * does not handle are <code>++</code>, <code>--</code>, and compound
 * assignment operations (<code>+=</code>, etc.) when applied to a boxed
 * type. This class fixes such cases in two steps. First, an internal subclass
 * of {@link CompoundAssignmentNormalizer} simplifies such expressions to a
 * simple assignment expression. Second, this visitor replaces an assignment to
 * an unboxing method (<code>unbox(x) = unbox(x) + 1</code>) with an
 * assignment to the underlying box (<code>x = box(unbox(x) + 1)</code>).
 */
public class FixAssignmentToUnbox extends JModVisitor {
  /**
   * Normalize compound assignments where the lhs is an unbox operation.
   */
  private static class CompoundAssignmentToUnboxNormalizer extends
      CompoundAssignmentNormalizer {
    private final AutoboxUtils autoboxUtils;

    protected CompoundAssignmentToUnboxNormalizer(JProgram program) {
      super(program, false);
      autoboxUtils = new AutoboxUtils(program);
    }

    /**
     * If the lhs is an unbox operation, then return the box rather than the
     * original value.
     */
    @Override
    protected JExpression expressionToReturn(JExpression lhs) {
      JExpression boxed = autoboxUtils.undoUnbox(lhs);
      if (boxed != null) {
        return boxed;
      }
      return lhs;
    }

    @Override
    protected boolean shouldBreakUp(JBinaryOperation x) {
      return isUnboxExpression(x.getLhs());
    }

    @Override
    protected boolean shouldBreakUp(JPostfixOperation x) {
      return isUnboxExpression(x.getArg());
    }

    @Override
    protected boolean shouldBreakUp(JPrefixOperation x) {
      return isUnboxExpression(x.getArg());
    }

    private boolean isUnboxExpression(JExpression x) {
      return (autoboxUtils.undoUnbox(x) != null);
    }
  }

  public static void exec(JProgram program) {
    (new CompoundAssignmentToUnboxNormalizer(program)).breakUpAssignments();
    (new FixAssignmentToUnbox(program)).accept(program);
  }

  private final AutoboxUtils autoboxUtils;
  private final JProgram program;

  private FixAssignmentToUnbox(JProgram program) {
    this.program = program;
    this.autoboxUtils = new AutoboxUtils(program);
  }

  @Override
  public void endVisit(JBinaryOperation x, Context ctx) {
    // unbox(x) = foo -> x = box(foo)

    if (x.getOp() != JBinaryOperator.ASG) {
      return;
    }

    JExpression boxed = autoboxUtils.undoUnbox(x.getLhs());
    if (boxed == null) {
      return;
    }

    JClassType boxedType = (JClassType) boxed.getType();

    ctx.replaceMe(new JBinaryOperation(program, x.getSourceInfo(), boxedType,
        JBinaryOperator.ASG, boxed, autoboxUtils.box(x.getRhs(), boxedType)));
  }
}
