package questwalk.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.github.javaparser.utils.Log
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.the

internal val Project.applicationExtension: CommonExtension<*, *, *, *, *, *>
    get() = extensions.getByType<ApplicationExtension>()

internal val Project.libraryExtension: CommonExtension<*, *, *, *, *, *>
    get() = extensions.getByType<LibraryExtension>()

internal val Project.androidExtension: CommonExtension<*, *, *, *, *, *>
    get() = kotlin.runCatching { libraryExtension }
        .recoverCatching { applicationExtension }
        .onFailure { Log.error("Could not find Library or Application extension from this project") }
        .getOrThrow()

internal val Project.libs get() = the<LibrariesForLibs>()

internal fun Project.implementation(dependency: Any) = dependencies.add("implementation", dependency)
internal fun Project.testImplementation(dependency: Any) = dependencies.add("testImplementation", dependency)
internal fun Project.androidTestImplementation(dependency: Any) = dependencies.add("androidTestImplementation", dependency)
internal fun Project.debugImplementation(dependency: Any) = dependencies.add("debugImplementation", dependency)