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

import com.github._1c_syntax.bsl.parser.CaseChangingCharStream;
import com.github._1c_syntax.bsl.parser.UnicodeBOMInputStream;
import lombok.Getter;
import lombok.SneakyThrows;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс-враппер для лексера, предоставляющий набор дополнительных методов для удобства тестирования
 *
 * @param <T> класс реализации лексера
 */
public class TestLexer<T extends Lexer> {
  private final List<String> ruleNames;

  /**
   * Созданный объект лексера
   */
  @Getter
  private final Lexer lexer;

  @SneakyThrows
  public TestLexer(Class<T> clazz) {
    this.lexer = createLexer(clazz);

    var methods = clazz.getDeclaredMethods();
    var result = Arrays.stream(methods).filter(method -> "getTokenNames".equals(method.getName())).findFirst();
    if (result.isPresent()) {
      ruleNames = Arrays.asList((String[]) result.get().invoke(lexer));
    } else {
      ruleNames = Collections.emptyList();
    }
  }

  /**
   * Возвращает список токенов в переданной строке
   *
   * @param mode
   * @param inputString
   * @return
   */
  public List<Token> getTokens(int mode, String inputString) {
    return getTokensStream(mode, inputString).getTokens();
  }

  /**
   * Возвращает токены переданной строки
   *
   * @param mode
   * @param inputString
   * @return
   */
  public CommonTokenStream getTokensStream(int mode, String inputString) {
    CharStream input;

    try (
      var inputStream = IOUtils.toInputStream(inputString, StandardCharsets.UTF_8);
      var ubis = new UnicodeBOMInputStream(inputStream);
      var inputStreamReader = new InputStreamReader(ubis, StandardCharsets.UTF_8)
    ) {
      ubis.skipBOM();
      var inputTemp = CharStreams.fromReader(inputStreamReader);
      input = new CaseChangingCharStream(inputTemp);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    lexer.setInputStream(input);
    lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
    lexer.pushMode(mode);

    var tempTokenStream = new CommonTokenStream(lexer);
    tempTokenStream.fill();

    return tempTokenStream;
  }

  /**
   * Формирует строку с именами токенов на основании переданного массива их идентификаторов
   *
   * @param tokenTypes массив идентификаторов (типов) токенов
   * @return
   */
  public String tokenName(Integer[] tokenTypes) {
    return Arrays.stream(tokenTypes).map(this::tokenName).collect(Collectors.joining(", "));
  }

  /**
   * Возвращает имя токена по его идентификатору (типу)
   *
   * @param type
   * @return
   */
  public String tokenName(Integer type) {
    if (!ruleNames.isEmpty() && type < ruleNames.size()) {
      return ruleNames.get(type);
    } else {
      return type.toString();
    }
  }

  /**
   * Настраивает и запоминает тестируемый контекст. Использует дефолтные значения mode и channel
   *
   * @param inputString анализируемая строка
   * @return служебный класс для замыкания
   */
  public LexerAsserts assertThat(String inputString) {
    return new LexerAsserts(this, inputString, T.DEFAULT_MODE, T.DEFAULT_TOKEN_CHANNEL);
  }

  /**
   * Настраивает и запоминает тестируемый контекст. Использует дефолтное channel
   *
   * @param mode
   * @param inputString анализируемая строка
   * @return служебный класс для замыкания
   */
  public LexerAsserts assertThat(int mode, String inputString) {
    return new LexerAsserts(this, inputString, mode, T.DEFAULT_TOKEN_CHANNEL);
  }

  /**
   * Настраивает и запоминает тестируемый контекст
   *
   * @param mode
   * @param inputString анализируемая строка
   * @param channel     анализируемая строка
   * @return служебный класс для замыкания
   */
  public LexerAsserts assertThat(int mode, String inputString, int channel) {
    return new LexerAsserts(this, inputString, mode, channel);
  }

  /**
   * Настраивает и запоминает тестируемый контекст. Использует дефолтное mode
   *
   * @param inputString анализируемая строка
   * @param channel
   * @return служебный класс для замыкания
   */
  public LexerAsserts assertThat(String inputString, int channel) {
    return new LexerAsserts(this, inputString, T.DEFAULT_MODE, channel);
  }

  private T createLexer(Class<T> lexerClass) {
    try {
      return lexerClass.getDeclaredConstructor(CharStream.class, boolean.class)
        .newInstance(CharStreams.fromString(""), true);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
}
