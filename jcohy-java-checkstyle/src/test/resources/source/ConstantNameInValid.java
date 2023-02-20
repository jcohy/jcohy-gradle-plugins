/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.jcohy.checkstyle;

/**
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p>
 * Description:
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/30:15:57
 * @since 0.0.5.1
 */
public class ConstantNameInValid {


    final static int log = 10; // OK
    final static int logger = 50; // OK
    final static int logMYSELF = 10; // violation, name 'logMYSELF' must match
    // pattern '^log(ger)?$|^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    final static int loggerMYSELF = 5; // violation, name 'loggerMYSELF' must match
    // pattern '^log(ger)?$|^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    final static int MYSELF = 100; // OK
    final static int myselfConstant = 1; // violation, name 'myselfConstant' must match pattern
}
