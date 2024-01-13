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

package io.skippy.build;

import io.skippy.core.SkippyUtils;

import java.nio.file.Path;

import static io.skippy.core.SkippyConstants.*;

/**
 * API with functionality that is used across build-tool specific libraries (e.g., skippy-gradle and skippy-maven).
 *
 * @author Florian McKee
 */
public final class SkippyBuildApi {

    private final Path projectDir;
    private final ClassesMd5Writer classesMd5Writer;
    private final CoverageFileCompactor coverageFileCompactor;

    /**
     * C'tor.
     *
     * @param projectDir the project folder
     * @param classFileCollector the {@link ClassFileCollector}
     */
    public SkippyBuildApi(Path projectDir, ClassFileCollector classFileCollector) {
        this.projectDir = projectDir;
        this.classesMd5Writer = new ClassesMd5Writer(projectDir, classFileCollector);
        this.coverageFileCompactor = new CoverageFileCompactor(projectDir, classFileCollector);
    }

    /**
     * Performs the following actions:
     * <ul>
     *     <li>Compacts the coverage files (see {@link CoverageFileCompactor})</li>
     *     <li>Writes the {@code classes.md5} file (see {@link ClassesMd5Writer})</li>
     * </ul>
     */
    public void writeClassesMd5FileAndCompactCoverageFiles() {
        classesMd5Writer.write();
        coverageFileCompactor.compact();
    }

    /**
     * Removes the skippy folder.
     */
    public void removeSkippyFolder() {
        var skippyFolder = SkippyUtils.getSkippyFolder(projectDir).toFile();
        if (skippyFolder.exists()) {
            for (var file : skippyFolder.listFiles()) {
                file.delete();
            }
            skippyFolder.delete();
        }
    }

    /**
     * Removes log files from the skippy folder.
     */
    public void deleteLogFiles() {
        var skippyFolder = SkippyUtils.getSkippyFolder(projectDir);

        var predictionsLog = skippyFolder.resolve(PREDICTIONS_LOG_FILE).toFile();
        if (predictionsLog.exists()) {
            predictionsLog.delete();
        }
        var profilingLog = skippyFolder.resolve(PROFILING_LOG_FILE).toFile();
        if (profilingLog.exists()) {
            profilingLog.delete();
        }
    }

}
