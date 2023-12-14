/*
 * Copyright 2023 the original author or authors.
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

package io.skippy.gradle.coveragebuild;

import io.skippy.gradle.model.SkippifiedTest;
import org.gradle.api.Project;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.Logger;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.ResultHandler;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CountDownLatch;

import static io.skippy.gradle.util.StopWatch.measureInMs;
import static io.skippy.gradle.SkippyConstants.SKIPPY_DIRECTORY;
import static java.util.stream.Collectors.joining;

/**
 * Runs coverage builds for skippified tests.
 *
 * @author Florian McKee
 */
final class CoverageBuildRunner {

    /**
     * Runs a coverage build for the {@code skippifiedTest}.
     *
     * @param project
     * @param buildLauncher
     * @param skippifiedTest
     */
    static void run(Project project, BuildLauncher buildLauncher, SkippifiedTest skippifiedTest) {
        try {
            var args = CoverageBuildTasksAndArguments.forSkippifiedTest(skippifiedTest);
            var csvFile = project.getProjectDir().toPath().resolve(SKIPPY_DIRECTORY).resolve(skippifiedTest.classFile().getFullyQualifiedClassName() + ".csv");
            var fqn = skippifiedTest.classFile().getFullyQualifiedClassName();

            project.getLogger().lifecycle("\n%s > Capturing coverage data in %s".formatted(
                    fqn, project.getProjectDir().toPath().relativize(csvFile))
            );
            project.getLogger().lifecycle("%s > ./gradlew %s %s".formatted(
                    fqn, args.tasks().stream().collect(joining(" ")), args.arguments().stream().collect(joining(" "))
            ));
            project.getLogger().lifecycle("%s".formatted(skippifiedTest.classFile().getFullyQualifiedClassName()));

            long ms = measureInMs(() -> runInternal(project.getLogger(), buildLauncher, skippifiedTest));
            project.getLogger().lifecycle("%s".formatted(skippifiedTest.classFile().getFullyQualifiedClassName()));
            project.getLogger().lifecycle("%s Build executed in %sms.".formatted(skippifiedTest.classFile().getFullyQualifiedClassName(), ms));
        } catch (Exception e) {
            project.getLogger().error(e.getMessage(), e);
            throw e;
        }
    }

    private static void runInternal(Logger logger, BuildLauncher buildLauncher, SkippifiedTest skippifiedTest) {
        // configure tasks and arguments
        var args = CoverageBuildTasksAndArguments.forSkippifiedTest(skippifiedTest);
        buildLauncher.forTasks(args.tasks().toArray(new String[0]));
        buildLauncher.addArguments(args.arguments().toArray(new String[0]));

        // intercept logs
        var errorOutputStream = new ByteArrayOutputStream();
        var standardOutputStream = new ByteArrayOutputStream();
        buildLauncher.setStandardError(errorOutputStream);
        buildLauncher.setStandardOutput(standardOutputStream);

        // execute

        var latch = new CountDownLatch(1);
        buildLauncher.run(new ResultHandler<>() {
            @Override
            public void onComplete(Void unused) {
                latch.countDown();
            }

            @Override
            public void onFailure(GradleConnectionException e) {
                latch.countDown();
                logger.error("Coverage build failed", e);
            }
        });

        try {
             latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        // propagate logs to root build
        log(logger, errorOutputStream, LogLevel.ERROR, skippifiedTest);
        log(logger, standardOutputStream, LogLevel.LIFECYCLE, skippifiedTest);
    }

    private static void log(Logger logger, ByteArrayOutputStream outputStream, LogLevel logLevel, SkippifiedTest skippifiedTest) {
        var output = outputStream.toString();
        if ( ! output.isEmpty()) {
            for (var line : output.split(System.lineSeparator())) {
                logger.log(logLevel, "%s    %s".formatted(skippifiedTest.classFile().getFullyQualifiedClassName(), line));
            }
        }
    }

}