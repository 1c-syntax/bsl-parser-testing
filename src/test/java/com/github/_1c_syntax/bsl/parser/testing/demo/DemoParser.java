/*
 * This file is a part of BSL Parser Testing.
 *
 * Copyright (c) 2023-2024
 * 1c-syntax team and Valery Maximov <maximovvalery@gmail.com>
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * BSL Parser Testing is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * BSL Parser Testing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BSL Parser Testing.
 */
package com.github._1c_syntax.bsl.parser.testing.demo;

import com.github._1c_syntax.bsl.parser.BSLParserRuleContext;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
//import org.antlr.v4.runtime.RuleVersion;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Collections;
import java.util.List;


public class DemoParser extends Parser {

  public static final String[] ruleNames = new String[]{"rule1", "rule2"};

  public DemoParser(TokenStream input) {
    super(input);
  }

  @Override
  public String[] getTokenNames() {
    return new String[0];
  }

  @Override
  public String[] getRuleNames() {
    return ruleNames;
  }

  @Override
  public String getGrammarFileName() {
    return null;
  }

  @Override
  public ATN getATN() {
    return null;
  }

  @Override
  public void reset() {
    // no op
  }

//  @RuleVersion(0)
  public final BSLParserRuleContext rule1() throws RecognitionException {
    return new DemoRuleContext(0, new DemoRuleContext(1), new DemoRuleContext(1), new DemoRuleContext(2));
  }

//  @RuleVersion(0)
  public final BSLParserRuleContext rule2() throws RecognitionException {
    return new DemoRuleContext(1);
  }

  private static final class DemoRuleContext extends BSLParserRuleContext {

    private final List<ParseTree> children;

    DemoRuleContext(int state) {
      super(new BSLParserRuleContext(), state);
      children = Collections.emptyList();
    }

    DemoRuleContext(int state, DemoRuleContext... children) {
      super(new BSLParserRuleContext(), state);
      this.children = List.of(children);
    }

    @Override
    public ParseTree getChild(int i) {
      return children.get(i);
    }

    @Override
    public int getChildCount() {
      return children.size();
    }

    @Override
    public int getRuleIndex() {
      return invokingState;
    }
  }
}
