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
 * 2-tuple that contains a {@link Prediction} and the {@link Reason} why the prediction was made.
 *
 * @param prediction a {@link Prediction}
 * @param reason the reason the {@link Prediction} was made
 *
 * @author Florian McKee
 */
public record PredictionWithReason(Prediction prediction, Reason reason) {
    static PredictionWithReason execute(Reason reason) {
        return new PredictionWithReason(Prediction.EXECUTE, reason);
    }

    static PredictionWithReason skip(Reason reason) {
        return new PredictionWithReason(Prediction.SKIP, reason);
    }
}
