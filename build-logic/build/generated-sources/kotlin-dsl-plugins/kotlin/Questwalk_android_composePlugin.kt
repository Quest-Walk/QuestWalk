/**
 * Precompiled [questwalk.android.compose.gradle.kts][Questwalk_android_compose_gradle] script plugin.
 *
 * @see Questwalk_android_compose_gradle
 */
public
class Questwalk_android_composePlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Questwalk_android_compose_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
