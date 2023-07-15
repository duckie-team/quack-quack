/*
 * Designed and developed by Duckie Team 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/quack-quack-android/blob/main/LICENSE
 */

@file:Suppress("RedundantVisibilityModifier", "KDocUnresolvedReference")
@file:OptIn(ExperimentalCompilerApi::class)

package team.duckie.quackquack.sugar.test

import team.duckie.quackquack.sugar.hosted.PluginId as SugarCorePluginId
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.PluginOption
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import team.duckie.quackquack.sugar.hosted.OPTION_SUGAR_PATH
import team.duckie.quackquack.sugar.hosted.SugarCoreCommandLineProcessor
import team.duckie.quackquack.sugar.hosted.SugarHostedRegistrar
import team.duckie.quackquack.util.backend.test.removePackageLine

// TODO: @Imports 테스트 작성
// TODO: nullable한 인자 테스트 작성
class SugarCoreTest : StringSpec() {
  private val testCompilation =
    TestCompilation(tempdir()).apply {
      prepareSetting { tempDir ->
        compilerPluginRegistrars = listOf(SugarHostedRegistrar.asPluginRegistrar())
        pluginOptions = listOf(
          PluginOption(
            pluginId = SugarCorePluginId,
            optionName = OPTION_SUGAR_PATH.optionName,
            optionValue = tempDir.path,
          ),
        )
        commandLineProcessors = listOf(SugarCoreCommandLineProcessor())
      }
    }

  init {
    """
    - @SugarName이 없을 때는 기본 정책대로 sugar component를 생성함
    - KDoc이 없는 대상은 sugared kdoc만 생성함
    """ {
      val result = testCompilation.compile(
        kotlin(
          "text.kt",
          """
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import team.duckie.quackquack.sugar.material.SugarToken
import team.duckie.quackquack.sugar.material.Sugarable

@Sugarable
@Composable
fun QuackText(
  modifier: Modifier = Modifier,
  text: String,
  @SugarToken style: AwesomeType2,
  singleLine: Boolean = false,
  softWrap: Boolean = true,
) {}
          """,
        ),
      )

      @Language("kotlin")
      val expect = """
// This file was automatically generated by sugar-core.
// Do not modify it manually.
@file:OptIn(SugarCompilerApi::class, SugarGeneratorUsage::class)
@file:SugarGeneratedFile


import AwesomeType2
import QuackText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import kotlin.Boolean
import kotlin.OptIn
import kotlin.String
import team.duckie.quackquack.casa.`annotation`.Casa
import team.duckie.quackquack.casa.`annotation`.SugarGeneratorUsage
import team.duckie.quackquack.sugar.material.SugarCompilerApi
import team.duckie.quackquack.sugar.material.SugarGeneratedFile
import team.duckie.quackquack.sugar.material.SugarRefer
import team.duckie.quackquack.sugar.material.sugar

/**
 * This component uses [AwesomeType2.One] as the token value for `style`.
 *
 * This document was automatically generated by [QuackText].
 * If any contents are broken or wanna see the entire contents, please check the original document.
 */
@Casa
@Composable
@NonRestartableComposable
@SugarRefer("QuackText")
public fun QuackOneText(
  modifier: Modifier = sugar(),
  text: String,
  singleLine: Boolean = sugar(),
  softWrap: Boolean = sugar(),
) {
  QuackText(
    modifier = modifier,
    text = text,
    style = AwesomeType2.One,
    singleLine = singleLine,
    softWrap = softWrap,
  )
}

/**
 * This component uses [AwesomeType2.Two] as the token value for `style`.
 *
 * This document was automatically generated by [QuackText].
 * If any contents are broken or wanna see the entire contents, please check the original document.
 */
@Casa
@Composable
@NonRestartableComposable
@SugarRefer("QuackText")
public fun QuackTwoText(
  modifier: Modifier = sugar(),
  text: String,
  singleLine: Boolean = sugar(),
  softWrap: Boolean = sugar(),
) {
  QuackText(
    modifier = modifier,
    text = text,
    style = AwesomeType2.Two,
    singleLine = singleLine,
    softWrap = softWrap,
  )
}

/**
 * This component uses [AwesomeType2.Three] as the token value for `style`.
 *
 * This document was automatically generated by [QuackText].
 * If any contents are broken or wanna see the entire contents, please check the original document.
 */
@Casa
@Composable
@NonRestartableComposable
@SugarRefer("QuackText")
public fun QuackThreeText(
  modifier: Modifier = sugar(),
  text: String,
  singleLine: Boolean = sugar(),
  softWrap: Boolean = sugar(),
) {
  QuackText(
    modifier = modifier,
    text = text,
    style = AwesomeType2.Three,
    singleLine = singleLine,
    softWrap = softWrap,
  )
}

      """.trimIndent()

      result.exitCode shouldBe KotlinCompilation.ExitCode.OK
      testCompilation.findGeneratedFileOrNull("text.kt")?.readText()?.removePackageLine() shouldBe expect
    }

    """
    - PREFIX_NAME + Awesome + TOKEN_NAME 조합으로 sugar component를 생성함
    - KDoc이 있는 대상은 SugarToken이 제외된 sugared fully-kdoc을 생성함
    """ {
      val result = testCompilation.compile(
        kotlin(
          "text.kt",
          """
import team.duckie.quackquack.sugar.material.SugarToken
import team.duckie.quackquack.sugar.material.SugarName
import team.duckie.quackquack.sugar.material.Sugarable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * AWESOME! 
 *
 * @param modifier 적용할 Modifier
 * @param text 표시할 문자 
 */
@SugarName(SugarName.PREFIX_NAME + "Awesome" + SugarName.TOKEN_NAME)
@Sugarable
@Composable
fun QuackText(
  modifier: Modifier = Modifier,
  text: String,
  @SugarToken style: AwesomeType2,
  singleLine: Boolean = false,
  softWrap: Boolean = true,
) {}
          """,
        ),
      )

      @Language("kotlin")
      val expect = """
// This file was automatically generated by sugar-core.
// Do not modify it manually.
@file:OptIn(SugarCompilerApi::class, SugarGeneratorUsage::class)
@file:SugarGeneratedFile


import AwesomeType2
import QuackText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import kotlin.Boolean
import kotlin.OptIn
import kotlin.String
import team.duckie.quackquack.casa.`annotation`.Casa
import team.duckie.quackquack.casa.`annotation`.SugarGeneratorUsage
import team.duckie.quackquack.sugar.material.SugarCompilerApi
import team.duckie.quackquack.sugar.material.SugarGeneratedFile
import team.duckie.quackquack.sugar.material.SugarRefer
import team.duckie.quackquack.sugar.material.sugar

/**
 * AWESOME!
 *
 * This component uses [AwesomeType2.One] as the token value for `style`.
 *
 * This document was automatically generated by [QuackText].
 * If any contents are broken or wanna see the entire contents, please check the original document.
 *
 * @param modifier 적용할 Modifier
 * @param text 표시할 문자
 */
@Casa
@Composable
@NonRestartableComposable
@SugarRefer("QuackText")
public fun QuackAwesomeOne(
  modifier: Modifier = sugar(),
  text: String,
  singleLine: Boolean = sugar(),
  softWrap: Boolean = sugar(),
) {
  QuackText(
    modifier = modifier,
    text = text,
    style = AwesomeType2.One,
    singleLine = singleLine,
    softWrap = softWrap,
  )
}

/**
 * AWESOME!
 *
 * This component uses [AwesomeType2.Two] as the token value for `style`.
 *
 * This document was automatically generated by [QuackText].
 * If any contents are broken or wanna see the entire contents, please check the original document.
 *
 * @param modifier 적용할 Modifier
 * @param text 표시할 문자
 */
@Casa
@Composable
@NonRestartableComposable
@SugarRefer("QuackText")
public fun QuackAwesomeTwo(
  modifier: Modifier = sugar(),
  text: String,
  singleLine: Boolean = sugar(),
  softWrap: Boolean = sugar(),
) {
  QuackText(
    modifier = modifier,
    text = text,
    style = AwesomeType2.Two,
    singleLine = singleLine,
    softWrap = softWrap,
  )
}

/**
 * AWESOME!
 *
 * This component uses [AwesomeType2.Three] as the token value for `style`.
 *
 * This document was automatically generated by [QuackText].
 * If any contents are broken or wanna see the entire contents, please check the original document.
 *
 * @param modifier 적용할 Modifier
 * @param text 표시할 문자
 */
@Casa
@Composable
@NonRestartableComposable
@SugarRefer("QuackText")
public fun QuackAwesomeThree(
  modifier: Modifier = sugar(),
  text: String,
  singleLine: Boolean = sugar(),
  softWrap: Boolean = sugar(),
) {
  QuackText(
    modifier = modifier,
    text = text,
    style = AwesomeType2.Three,
    singleLine = singleLine,
    softWrap = softWrap,
  )
}

      """.trimIndent()

      result.exitCode shouldBe KotlinCompilation.ExitCode.OK
      testCompilation.findGeneratedFileOrNull("text.kt")?.readText()?.removePackageLine() shouldBe expect
    }

    """
    - DEFAULT_NAME을 사용하면 기본 정책대로 sugar component를 생성함
    - 여러줄의 KDoc default content가 적용됐다면 첫 번째 줄만 default section으로 사용함 
    """ {
      val result = testCompilation.compile(
        kotlin(
          "text.kt",
          """
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import team.duckie.quackquack.sugar.material.SugarToken
import team.duckie.quackquack.sugar.material.SugarName
import team.duckie.quackquack.sugar.material.Sugarable

/**
 * AWESOME!. AWESOME!!. AWESOME!!!.
 * AWESOME!!!!. AWESOME!!!!!. AWESOME!!!!!!.
 *
 * @param modifier 적용할 Modifier
 * @param text 표시할 문자
 */
@SugarName(SugarName.DEFAULT_NAME)
@Sugarable
@Composable
fun QuackText(
  modifier: Modifier = Modifier,
  text: String,
  @SugarToken style: AwesomeType,
  singleLine: Boolean = false,
  softWrap: Boolean = true,
) {}
          """,
        ),
      )

      @Language("kotlin")
      val expect = """
// This file was automatically generated by sugar-core.
// Do not modify it manually.
@file:OptIn(SugarCompilerApi::class, SugarGeneratorUsage::class)
@file:SugarGeneratedFile


import AwesomeType
import QuackText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import kotlin.Boolean
import kotlin.OptIn
import kotlin.String
import team.duckie.quackquack.casa.`annotation`.Casa
import team.duckie.quackquack.casa.`annotation`.SugarGeneratorUsage
import team.duckie.quackquack.sugar.material.SugarCompilerApi
import team.duckie.quackquack.sugar.material.SugarGeneratedFile
import team.duckie.quackquack.sugar.material.SugarRefer
import team.duckie.quackquack.sugar.material.sugar

/**
 * AWESOME!.
 *
 * This component uses [AwesomeType.One] as the token value for `style`.
 *
 * This document was automatically generated by [QuackText].
 * If any contents are broken or wanna see the entire contents, please check the original document.
 *
 * @param modifier 적용할 Modifier
 * @param text 표시할 문자
 */
@Casa
@Composable
@NonRestartableComposable
@SugarRefer("QuackText")
public fun QuackOneText(
  modifier: Modifier = sugar(),
  text: String,
  singleLine: Boolean = sugar(),
  softWrap: Boolean = sugar(),
) {
  QuackText(
    modifier = modifier,
    text = text,
    style = AwesomeType.One,
    singleLine = singleLine,
    softWrap = softWrap,
  )
}

      """.trimIndent()

      result.exitCode shouldBe KotlinCompilation.ExitCode.OK
      testCompilation.findGeneratedFileOrNull("text.kt")?.readText()?.removePackageLine() shouldBe expect
    }

    "람다 인자가 지원됨" {
      val result = testCompilation.compile(
        kotlin(
          "checkbox.kt",
          """
import androidx.compose.runtime.Composable
import team.duckie.quackquack.sugar.material.SugarToken
import team.duckie.quackquack.sugar.material.Sugarable

@Sugarable
@Composable
fun QuackCheckbox(
  @SugarToken style: AwesomeType,
  onCheckChanged: (checked: Boolean) -> Unit,
) {}

@Sugarable
@Composable
fun QuackCheckbox2(
  @SugarToken style: AwesomeType,
  onCheckChanged: suspend Boolean.(checked: Boolean) -> Boolean,
) {}
          """,
        ),
      )

      @Language("kotlin")
      val expect = """
// This file was automatically generated by sugar-core.
// Do not modify it manually.
@file:OptIn(SugarCompilerApi::class, SugarGeneratorUsage::class)
@file:SugarGeneratedFile


import AwesomeType
import QuackCheckbox
import QuackCheckbox2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import kotlin.Boolean
import kotlin.Function1
import kotlin.OptIn
import kotlin.Unit
import kotlin.coroutines.SuspendFunction2
import team.duckie.quackquack.casa.`annotation`.Casa
import team.duckie.quackquack.casa.`annotation`.SugarGeneratorUsage
import team.duckie.quackquack.sugar.material.SugarCompilerApi
import team.duckie.quackquack.sugar.material.SugarGeneratedFile
import team.duckie.quackquack.sugar.material.SugarRefer
import team.duckie.quackquack.sugar.material.sugar

/**
 * This component uses [AwesomeType.One] as the token value for `style`.
 *
 * This document was automatically generated by [QuackCheckbox].
 * If any contents are broken or wanna see the entire contents, please check the original document.
 */
@Casa
@Composable
@NonRestartableComposable
@SugarRefer("QuackCheckbox")
public fun QuackOneCheckbox(onCheckChanged: (P0: Boolean) -> Unit) {
  QuackCheckbox(
    style = AwesomeType.One,
    onCheckChanged = onCheckChanged,
  )
}

/**
 * This component uses [AwesomeType.One] as the token value for `style`.
 *
 * This document was automatically generated by [QuackCheckbox2].
 * If any contents are broken or wanna see the entire contents, please check the original document.
 */
@Casa
@Composable
@NonRestartableComposable
@SugarRefer("QuackCheckbox2")
public fun QuackOneCheckbox2(onCheckChanged: suspend (P0: Boolean, P1: Boolean) -> Boolean) {
  QuackCheckbox2(
    style = AwesomeType.One,
    onCheckChanged = onCheckChanged,
  )
}

      """.trimIndent()

      result.exitCode shouldBe KotlinCompilation.ExitCode.OK
      testCompilation.findGeneratedFileOrNull("checkbox.kt")?.readText()?.removePackageLine() shouldBe expect
    }
  }
}
