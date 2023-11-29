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

package io.skippy.gradle;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Smoke test for the Skippy plugin.
 *
 * @author Florian McKee
 */
public class SkippyPluginSmokeTest {

    @Test
    public void smokeTestSkippyPlugin() throws Exception {

        var buildFile = new File(getClass().getResource("skippyplugin/build.gradle").toURI());
        var projectDir = buildFile.getParentFile();

        BuildResult result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments("-b", "build-with-plugin.gradle", "clean", "build", "skippyAnalyze")
                .forwardOutput()
                .build();

        var output = result.getOutput();

        assertThat(output).contains("""             
                > Task :skippyAnalyze
                Capturing coverage data for com.example.LeftPadderTest in skippy/com.example.LeftPadderTest.csv
                Capturing coverage data for com.example.RightPadderTest in skippy/com.example.RightPadderTest.csv
                Creating the Skippy analysis file skippy/analyzedFiles.txt.""");
    }

}