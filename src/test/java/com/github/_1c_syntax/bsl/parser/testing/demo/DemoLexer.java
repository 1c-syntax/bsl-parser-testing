/*
 * This file is a part of BSL Parser Testing.
 *
 * Copyright (c) 2023-2026
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

import org.antlr.v4.parse.BlockSetTransformer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class DemoLexer extends Lexer {

  public static final String[] tokenNames = new String[]{"token1", "token2"};;
  public static final Vocabulary VOCABULARY = new VocabularyImpl(new String[0], tokenNames);

  @Override
  public String[] getChannelNames() {
    return new String[0];
  }

  @Override
  public String[] getModeNames() {
    return new String[0];
  }

  public String[] getTokenNames() {
    return tokenNames;
  }

  public DemoLexer(CharStream input) {
    super(input);
  }

  @Override
  public String[] getRuleNames() {
    return new String[0];
  }

  @Override
  public Vocabulary getVocabulary() {
    return VOCABULARY;
  }

  @Override
  public String getGrammarFileName() {
    return "fake";
  }
}