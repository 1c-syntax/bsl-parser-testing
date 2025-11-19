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

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.antlr.v4.runtime.IncrementalParser;
import org.antlr.v4.runtime.IncrementalTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestParser<PARSER extends Parser, LEXER extends Lexer> {

  private final List<String> ruleNames;

  @Getter
  private final TestLexer<LEXER> lexer;

  @Getter
  private final Class<PARSER> parserClazz;

  @Getter
  private final Class<LEXER> lexerClazz;

  @Getter
  @Accessors(fluent = true)
  private PARSER parser;

  private final boolean supportRebuild;

  @SneakyThrows
  public TestParser(Class<PARSER> parserClazz, Class<LEXER> lexerClazz) {
    this.lexer = new TestLexer<>(lexerClazz);
    this.parserClazz = parserClazz;
    this.lexerClazz = lexerClazz;

    var fields = parserClazz.getDeclaredFields();
    var result = Arrays.stream(fields).filter(field -> "ruleNames".equals(field.getName())).findFirst();

    if (result.isPresent()) {
      ruleNames = Arrays.asList((String[]) result.get().get(parserClazz));
    } else {
      ruleNames = Collections.emptyList();
    }

    this.supportRebuild = IncrementalParser.class.isAssignableFrom(parserClazz);
  }

  /**
   * Возвращает имя rule по его идентификатору (типу)
   *
   * @param type Тип рула
   * @return Имя рула
   */
  public String ruleName(Integer type) {
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
  public ParserAsserts assertThat(String inputString) {
    parser = createParser(LEXER.DEFAULT_MODE, inputString);
    return new ParserAsserts(this, parser);
  }

  /**
   * Настраивает и запоминает тестируемый контекст. Использует дефолтное channel
   *
   * @param mode        Режим
   * @param inputString анализируемая строка
   * @return служебный класс для замыкания
   */
  public ParserAsserts assertThat(int mode, String inputString) {
    parser = createParser(mode, inputString);
    return new ParserAsserts(this, parser);
  }

  /**
   * Настраивает и запоминает тестируемый контекст по имени фикстуры
   *
   * @param sourcePath Имя файла-фикстуры (ресурса)
   * @return служебный класс для замыкания
   */
  public ParserAsserts assertThatFile(String sourcePath) {
    parser = createParser(LEXER.DEFAULT_MODE, ResourceUtils.byName(sourcePath));
    return new ParserAsserts(this, parser);
  }

  private PARSER createParser(int mode, String inputString) {
    var tokenStream = getLexer().getTokensStream(mode, inputString);
    try {
      if (supportRebuild) {
        return parserClazz.getDeclaredConstructor(IncrementalTokenStream.class)
          .newInstance(tokenStream);
      } else {
        return parserClazz.getDeclaredConstructor(TokenStream.class)
          .newInstance(tokenStream);
      }
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
}
