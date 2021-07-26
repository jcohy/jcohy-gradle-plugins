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

import org.apache.commons.logging.Log;

/**
 * Allow this to be skipped items.
 *
 * @author jiac
 */
public class FiltersToSkipThis {

    private Log logger;

    private String inner;

    public void test() {
        logger.debug("test");
        inner = "test";
    }

    private class Nested {

        private String nested;

        Nested() {
            String x = inner;
            nested = "ok";
        }

    }

}
