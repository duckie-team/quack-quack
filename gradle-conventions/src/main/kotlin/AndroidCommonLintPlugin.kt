import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import team.duckie.quackquack.convention.PluginEnum
import team.duckie.quackquack.convention.applyPlugins
import team.duckie.quackquack.convention.compileOnlys
import team.duckie.quackquack.convention.libs
import team.duckie.quackquack.convention.setupJunit
import team.duckie.quackquack.convention.setupLint

/*
 * Designed and developed by 2022 SungbinLand, Team Duckie
 *
 * [AndroidCommonLintPlugin.kt] created by Ji Sungbin on 22. 8. 19. 오후 7:52
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/sungbinland/quack-quack/blob/main/LICENSE
 */

internal class AndroidCommonLintPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugins(
                PluginEnum.JavaLibrary,
                PluginEnum.PureKotlin,
                PluginEnum.AndroidLint,
            )

            extensions.configure<JavaPluginExtension> {
                val version = JavaVersion.VERSION_1_8
                sourceCompatibility = version
                targetCompatibility = version
            }

            dependencies {
                dependencies {
                    compileOnlys(libs.findLibrary("kotlin-stdlib").get())
                    setupJunit(
                        core = libs.findLibrary("test-junit-core").get(),
                        engine = libs.findLibrary("test-junit-engine").get(),
                    )
                    setupLint(
                        core = libs.findLibrary("lint-api").get(),
                        test = libs.findBundle("test-lint").get(),
                    )
                }
            }
        }
    }
}
