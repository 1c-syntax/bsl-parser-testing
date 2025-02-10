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

import org.antlr.v4.runtime.Token;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Objects;

/**
 * Реализация утверждений для лексера
 */
public class LexerAsserts {
  private final TestLexer<?> lexer;
  private final int channel;
  private final List<Token> tokens;

  private final int mode;

  public LexerAsserts(TestLexer<?> lexer, String inputString, int mode, int channel) {
    this.lexer = lexer;
    this.channel = channel;
    this.mode = mode;
    this.tokens = lexer.getTokens(mode, inputString);
  }

  /**
   * Выполняет проверку совпадения состава и порядка токенов в строке с переданным массивом.
   * При проверке анализируются все токены всех каналов, включая служебный конца строки
   *
   * @param expectedTokens контрольный массив токенов
   * @return
   */
  public LexerAsserts containsExactly(Integer... expectedTokens) {
    var tokenTypes = tokens.stream()
      .map(Token::getType)
      .toArray(Integer[]::new);
    return isEqualTo(expectedTokens, tokenTypes);
  }

  /**
   * Выполняет проверку совпадения состава и порядка токенов в строке с переданным массивом.
   * При проверке анализируются токены указанного в assertThat канала, исключая служебный конца строки
   *
   * @param expectedTokens контрольный массив токенов
   * @return
   */
  public LexerAsserts containsAll(Integer... expectedTokens) {
    var tokenTypes = tokens.stream()
      .filter(token -> token.getChannel() == channel)
      .map(Token::getType)
      .filter(type -> type != Token.EOF)
      .toArray(Integer[]::new);
    return isEqualTo(expectedTokens, tokenTypes);
  }

  /**
   * Проверяет соответствие текста выбранного токена (индекс с 0) с заданным значение
   *
   * @param index Порядковый индекс токена в строке
   * @param text  текст токена
   * @return
   */
  public LexerAsserts tokenHasText(int index, String text) {
    var token = tokens.get(index);
    Assertions.assertThat(token).isNotNull();
    Assertions.assertThat(token.getText()).isEqualTo(text);
    return this;
  }

  /**
   * Проверяет расположение (номер строки) выбранного токена (индекс с 0) в исходном тексте
   *
   * @param index  Порядковый индекс токена в строке
   * @param lineNo номер строки расположения токена
   * @return
   */
  public LexerAsserts tokenOnLine(int index, int lineNo) {
    var token = tokens.get(index);
    Assertions.assertThat(token).isNotNull();
    Assertions.assertThat(token.getLine()).isEqualTo(lineNo);
    return this;
  }

  /**
   * Выполняет сопоставление переданной строки с установленной посредством assertThat по составу и порядку
   * идентификаторов токенов (с учетом установленных mode и channel)
   *
   * @param actual строка для сравнения
   * @return
   */
  public LexerAsserts isEqualTo(String actual) {
    var actualTokens = lexer.getTokens(mode, actual);
    return isEqualTo(actualTokens);
  }

  private LexerAsserts isEqualTo(List<Token> actual) {
    return isEqualTo(
      tokens.stream()
        .filter(token -> token.getChannel() == channel)
        .map(Token::getType)
        .filter(type -> type != Token.EOF)
        .toArray(Integer[]::new),
      actual.stream()
        .filter(token -> token.getChannel() == channel)
        .map(Token::getType)
        .filter(type -> type != Token.EOF)
        .toArray(Integer[]::new)
    );
  }

  private LexerAsserts isEqualTo(Integer[] expected, Integer[] actual) {
    if (expected != actual) {
      if (expected.length != actual.length) {
        var expectedDescription = lexer.tokenName(expected);
        var actualDescription = lexer.tokenName(actual);
        Assertions.assertThat(expectedDescription).isEqualTo(actualDescription);
      }

      for (int i = 0; i < expected.length; ++i) {
        var expectedElement = expected[i];
        var actualElement = actual[i];
        if (!Objects.equals(expectedElement, actualElement)) {
          Assertions.assertThat(lexer.tokenName(expectedElement))
            .describedAs("position " + i)
            .isEqualTo(lexer.tokenName(actualElement));
        }
      }
    }
    return this;
  }
}
