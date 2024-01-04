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

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Вспомогательный класс для чтения файлов-фикстур
 */
@UtilityClass
public class ResourceUtils {
  /**
   * Читает файл фикстуры из тестовых ресурсов проекта
   *
   * @param resourceName
   * @return
   */
  public String byName(@NonNull String resourceName) {
    if (resourceName.isBlank()) {
      throw new IllegalArgumentException("Resource name is blank");
    }

    String content;
    var classLoader = Thread.currentThread().getContextClassLoader();
    try (var inputStream = classLoader.getResourceAsStream(resourceName)) {
      assert inputStream != null;
      content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Read error: ", e);
    }
    return content;
  }
}
