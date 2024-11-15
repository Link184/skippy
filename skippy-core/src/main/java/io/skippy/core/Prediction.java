/*
 * Copyright 2023-2024 the original author or authors.
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

package io.skippy.core;

/**
 * Skip-or-execute prediction.
 *
 * @author Florian McKee
 */
public enum Prediction {

    /**
     * Execute the test.
     */
    EXECUTE,

    /**
     * Execute (don't use Skippy to try to predict whether the test needs to run or not).
     */
    ALWAYS_EXECUTE,

    /**
     * Skip the test.
     */
    SKIP
}
