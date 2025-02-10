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

import com.github._1c_syntax.bsl.parser.testing.demo.DemoEmptyLexer;
import com.github._1c_syntax.bsl.parser.testing.demo.DemoLexer;
import com.github._1c_syntax.bsl.parser.testing.demo.DemoToken;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class TestLexerTest {

  @Test
  void testLexer() {
    var testLexer = new TestLexer<>(DemoLexer.class);
    assertThat(testLexer.getLexer())
      .isNotNull()
      .isInstanceOf(DemoLexer.class);
    assertThat(testLexer.tokenName(0)).isEqualTo("token1");
    assertThat(testLexer.tokenName(5)).isEqualTo("5");
    assertThat(testLexer.tokenName(new Integer[]{2, 1, 0})).isEqualTo("2, token2, token1");

    var testLexer2 = new TestLexer<>(DemoEmptyLexer.class);
    assertThat(testLexer2.getLexer())
      .isNotNull()
      .isInstanceOf(DemoEmptyLexer.class);
    assertThat(testLexer2.tokenName(0)).isEqualTo("0");
    assertThat(testLexer2.tokenName(5)).isEqualTo("5");
    assertThat(testLexer2.tokenName(new Integer[]{2, 1, 0})).isEqualTo("2, 1, 0");
  }

  @Test
  void testAssert() {
    var testLexer = spy(new TestLexer<>(DemoLexer.class));
    List<Token> tokenList1 = List.of(new DemoToken("t1", 1, 1, 1),
      new DemoToken("t2", 2, 2, 2),
      new DemoToken("EOF", DemoLexer.EOF, 5, 5));
    List<Token> tokenList2 = List.of(new DemoToken("t1", 2, 1, 1),
      new DemoToken("t2", 3, 2, 2));

    doReturn(tokenList1).when(testLexer).getTokens(DemoLexer.DEFAULT_MODE, "");
    doReturn(tokenList1).when(testLexer).getTokens(DemoLexer.DEFAULT_MODE, "блабла");
    doReturn(Collections.emptyList()).when(testLexer).getTokens(DemoLexer.DEFAULT_MODE, "блабла1");
    doReturn(tokenList2).when(testLexer).getTokens(DemoLexer.DEFAULT_MODE, "блабла2");

    // все варианты создания
    var testAsserts = List.of(
      testLexer.assertThat(""),
      testLexer.assertThat("", DemoLexer.DEFAULT_TOKEN_CHANNEL),
      testLexer.assertThat(DemoLexer.DEFAULT_MODE, ""),
      testLexer.assertThat(DemoLexer.DEFAULT_MODE, "", DemoLexer.DEFAULT_TOKEN_CHANNEL)
    );

    testAsserts.forEach(lexerAsserts -> {
        assertThat(lexerAsserts).isNotNull();
        assertThat(lexerAsserts.containsAll(1, 2)).isEqualTo(lexerAsserts);
        assertThat(lexerAsserts.containsExactly(1, 2, DemoLexer.EOF)).isEqualTo(lexerAsserts);

        assertThat(lexerAsserts.tokenHasText(0, "t1")).isEqualTo(lexerAsserts);
        assertThat(lexerAsserts.tokenHasText(2, "EOF")).isEqualTo(lexerAsserts);
        assertThat(lexerAsserts.tokenOnLine(2, 5)).isEqualTo(lexerAsserts);
        assertThat(lexerAsserts.tokenOnLine(0, 1)).isEqualTo(lexerAsserts);

        assertThat(lexerAsserts.isEqualTo("блабла")).isEqualTo(lexerAsserts);
        assertThrows(AssertionFailedError.class, () -> lexerAsserts.isEqualTo("блабла1"));
        assertThrows(AssertionFailedError.class, () -> lexerAsserts.isEqualTo("блабла2"));
      }
    );
  }
}