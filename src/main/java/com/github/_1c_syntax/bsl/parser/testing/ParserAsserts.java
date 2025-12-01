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

import lombok.SneakyThrows;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Реализация утверждений для лексера
 */
public class ParserAsserts {
  private final Parser parser;
  private final TestParser<?, ?> testParser;

  private final ParserRuleContext currentAST;

  @SneakyThrows
  public ParserAsserts(TestParser<?, ?> testParser, Parser parser) {
    this.testParser = testParser;
    this.parser = parser;
    var fields = testParser.getParserClazz().getDeclaredMethods();
    var result = Arrays.stream(fields).filter(field -> testParser.ruleName(0).equals(field.getName())).findFirst();
    if (result.isPresent()) {
      this.currentAST = (ParserRuleContext) result.get().invoke(parser);
      this.parser.reset();
    } else {
      this.currentAST = null;
    }
  }

  /**
   * Выполняет проверку совпадения прочитанного узла на предмет ошибок парсинга
   *
   * @param tree Узел дерева
   * @return Ассерт (для текучести)
   * @throws RecognitionException Ошибка парсинга
   */
  public ParserAsserts matches(ParseTree tree) throws RecognitionException {
    if (parser.getNumberOfSyntaxErrors() != 0) {
      throw new RecognitionException(
        "Syntax error while parsing:\n" + parser.getInputStream().getText(),
        parser,
        parser.getInputStream(),
        parser.getContext()
      );
    }

    if (tree instanceof ParserRuleContext ctx) {
      if (ctx.exception != null) {
        throw ctx.exception;
      }

      if (ctx.parent == null) {
        boolean parseSuccess = testParser.getLexerClazz().cast(parser.getInputStream().getTokenSource())._hitEOF;
        if (!parseSuccess) {
          throw new RecognitionException(
            "Parse error EOF don't hit\n" + parser.getInputStream().getText(),
            parser,
            parser.getInputStream(),
            parser.getContext()
          );
        }
      }
    }

    for (int i = 0; i < tree.getChildCount(); i++) {
      matches(tree.getChild(i));
    }
    return this;
  }

  /**
   * Убеждается, что прочитанного узла нет либо есть ошибка его чтения
   *
   * @param tree Узел дерева
   * @return Ассерт (для текучести)
   */
  public ParserAsserts noMatches(ParseTree tree) {
    assertThat(tree).satisfiesAnyOf(
      elem -> assertThat(elem.getChildCount()).isEqualTo(0), // нет дочерних
      elem -> assertThrows(RecognitionException.class, () -> matches(tree)) // ошибка парсинга
    );
    return this;
  }

  /**
   * Выполняет анализ наличия в дереве узлов с нужным идентификатором в нужном количестве
   *
   * @param ruleId идентификатор рула
   * @param count количество узлов
   * @return Ассерт (для текучести)
   */
  public ParserAsserts containsRule(int ruleId, int count) {
    assertThat(currentAST).isNotNull();
    var ruleNodes = Trees.findAllRuleNodes(currentAST, ruleId).stream().map(ParseTree::getText).toList();
    assertThat(ruleNodes).as(testParser.ruleName(ruleId) + " (" + ruleId + ")")
      .isNotNull().hasSize(count);
    return this;
  }
}
