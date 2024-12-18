package io.skippy.gradle.android;

import org.gradle.api.Project;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.skippy.core.SkippyBuildApi;
import io.skippy.core.SkippyRepository;

/**
 * A sub-set of relevant {@link Project} properties that are compatible with Gradle's Configuration Cache.
 *
 * @author Florian McKee
 * @author Eugeniu Tufar
 */
class ProjectSettings implements Serializable {

    final List<File> classesDirs;
    final SkippyPluginExtension skippyPluginExtension;
    final Path projectDir;
    final Path buildDir;

    /**
     * C'tor.
     *
     * @param classesDirs the folders that contain compiled class files
     * @param skippyExtension the {@link SkippyPluginExtension}
     * @param projectDir the project directory (e.g., /repos/my-project)
     * @param buildDir the build directory (e.g., /repos/my-project/build)
     */
    private ProjectSettings(List<File> classesDirs, SkippyPluginExtension skippyExtension, Path projectDir, Path buildDir) {
        this.classesDirs = classesDirs;
        this.skippyPluginExtension = skippyExtension;
        this.projectDir = projectDir;
        this.buildDir = buildDir;
    }

    static ProjectSettings from(Project project) {
        var androidClassesDirs = AndroidDestinationDirectoryCollector.collect(project);
        var kotlinClassesDirs = KotlinDestinationDirectoryCollector.collect(project);
        var allClassesDirs = Stream.concat(kotlinClassesDirs, androidClassesDirs).toList();

        Path projectDir = project.getProjectDir().toPath();
        Path buildDir = project.getLayout().getBuildDirectory().getAsFile().get().toPath();
        var skippyExtension = project.getExtensions().getByType(SkippyPluginExtension.class);
        return new ProjectSettings(allClassesDirs, skippyExtension, projectDir, buildDir);
    }

    void ifBuildSupportsSkippy(Consumer<SkippyBuildApi> action) {
        if (classesDirs != null && !classesDirs.isEmpty()) {
            var skippyConfiguration = skippyPluginExtension.toSkippyConfiguration();
            var skippyBuildApi = new SkippyBuildApi(
                    skippyConfiguration,
                    new GradleClassFileCollector(projectDir, classesDirs),
                    SkippyRepository.getInstance(
                            skippyConfiguration,
                            projectDir,
                            buildDir
                    )
            );
            action.accept(skippyBuildApi);
        }
    }
}