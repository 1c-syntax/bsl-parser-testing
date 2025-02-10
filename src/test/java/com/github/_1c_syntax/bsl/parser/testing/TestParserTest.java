/*
 * This file is a part of BSL Parser Testing.
 *
 * Copyright (c) 2023-2025
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
package com.github._1c_syntax.bsl.parser.testing;

import com.github._1c_syntax.bsl.parser.testing.demo.DemoEmptyParser;
import com.github._1c_syntax.bsl.parser.testing.demo.DemoLexer;
import com.github._1c_syntax.bsl.parser.testing.demo.DemoParser;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class TestParserTest {

  @Test
  void testParser() {
    var testParser = new TestParser<>(DemoParser.class, DemoLexer.class);
    assertThat(testParser.getParserClazz())
      .isNotNull()
      .isEqualTo(DemoParser.class);
    assertThat(testParser.getLexerClazz())
      .isNotNull()
      .isEqualTo(DemoLexer.class);

    assertThat(testParser.ruleName(0)).isEqualTo("rule1");
    assertThat(testParser.ruleName(5)).isEqualTo("5");

    var testParser2 = new TestParser<>(DemoEmptyParser.class, DemoLexer.class);
    assertThat(testParser2.getParserClazz())
      .isNotNull()
      .isEqualTo(DemoEmptyParser.class);
    assertThat(testParser2.getLexerClazz())
      .isNotNull()
      .isEqualTo(DemoLexer.class);

    assertThat(testParser2.ruleName(0)).isEqualTo("0");
    assertThat(testParser2.ruleName(5)).isEqualTo("5");
  }

  @Test
  void testAssert() {
    var testParser = spy(new TestParser<>(DemoParser.class, DemoLexer.class));

    var testLexer = spy(new TestLexer<>(DemoLexer.class));
    doReturn(new CommonTokenStream(testLexer.getLexer()))
      .when(testLexer).getTokensStream(DemoLexer.DEFAULT_MODE, "");

    doReturn(testLexer).when(testParser).getLexer();

    // все варианты создания
    var testAsserts = List.of(
      testParser.assertThat(""),
      testParser.assertThat(DemoLexer.DEFAULT_MODE, "")
    );

    testAsserts.forEach(parserAsserts -> {
        assertThat(parserAsserts).isNotNull();
        assertThat(parserAsserts.matches(testParser.parser().rule1())).isEqualTo(parserAsserts);
        assertThat(parserAsserts.noMatches(testParser.parser().rule2())).isEqualTo(parserAsserts);
        assertThat(parserAsserts.containsRule(0, 1)).isEqualTo(parserAsserts);
        assertThat(parserAsserts.containsRule(1, 2)).isEqualTo(parserAsserts);
        assertThat(parserAsserts.containsRule(2, 1)).isEqualTo(parserAsserts);
        assertThat(parserAsserts.containsRule(3, 0)).isEqualTo(parserAsserts);
      }
    );
  }
}
