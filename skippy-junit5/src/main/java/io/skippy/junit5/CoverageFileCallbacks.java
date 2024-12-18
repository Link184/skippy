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

package io.skippy.junit5;

import io.skippy.core.SkippyTestApi;
import org.junit.jupiter.api.extension.*;

/**
 * Callbacks that trigger the capture of coverage data for a test class.
 *
 * @author Florian McKee
 */
public final class CoverageFileCallbacks implements BeforeAllCallback, AfterAllCallback {

    private final SkippyTestApi skippyTestApi = SkippyTestApi.INSTANCE;

    @Override
    public void beforeAll(ExtensionContext context) {
        context.getTestClass().ifPresent(skippyTestApi::beforeAll);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        context.getTestClass().ifPresent(skippyTestApi::afterAll);
    }

}