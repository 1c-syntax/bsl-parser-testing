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
package com.github._1c_syntax.bsl.parser.testing;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResourceUtilsTest {

  @Test
  void byName() {
    var string = ResourceUtils.byName("testFixture.txt");
    assertThat(string).isEqualTo("// some text");

    string = ResourceUtils.byName("folder/testFixtureInFolder.txt");
    assertThat(string).isEqualTo("// some text");
  }

  @Test
  void byNameError() {
    assertThrows(IllegalArgumentException.class, () -> ResourceUtils.byName(""));
    assertThrows(AssertionError.class, () -> ResourceUtils.byName("unknown"));
  }
}
