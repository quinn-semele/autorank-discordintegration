package dev.compasses.multiloader

import org.gradle.jvm.toolchain.JavaLanguageVersion

object Constants {
    const val GROUP = "dev.compasses.autorank_discordintegration"
    const val MOD_ID = "autorank_discordintegration"
    const val MOD_NAME = "Auto Rank for Discord Integration"
    const val MOD_VERSION = "1.0.3"
    const val LICENSE = "PolyForm-Shield-1.0.0"
    const val DESCRIPTION = """Provides and takes away ranks when joining a Minecraft server based on discord ranks."""

    const val HOMEPAGE = "https://github.com/quinn-semele/autorank-discordintegration"
    const val ISSUE_TRACKER = "https://github.com/quinn-semele/autorank-discordintegration/issues"
    const val SOURCES_URL = "https://github.com/quinn-semele/autorank-discordintegration"

    @Suppress("RedundantNullableReturnType")
    val curseforgeProperties: CurseForgeProperties? = null

    @Suppress("RedundantNullableReturnType")
    val modrinthProperties: ModrinthProperties? = null

    const val PUBLISH_WEBHOOK_VARIABLE = "PUBLISH_WEBHOOK"

    const val COMPARE_URL = "https://github.com/quinn-semele/autorank-discordintegration/compare/"

    val CONTRIBUTORS = linkedMapOf(
        "Quinn Semele" to "Project Owner"
    )

    val CREDITS = linkedMapOf<String, String>(

    )

    val EXTRA_MOD_INFO_REPLACEMENTS = mapOf<String, String>(

    )

    val JAVA_VERSION = JavaLanguageVersion.of(21)
    const val JETBRAIN_ANNOTATIONS_VERSION = "24.1.0"
    const val FINDBUGS_VERSION = "3.0.2"

    const val MIXIN_VERSION = "0.8.5"
    const val MIXIN_EXTRAS_VERSION = "0.3.5"

    const val MINECRAFT_VERSION = "1.21"
    const val NF_MINECRAFT_CONSTRAINT = "[1.21, 1.22)"
    val SUPPORTED_MINECRAFT_VERSIONS = listOf(MINECRAFT_VERSION, "1.21.1")

    // https://parchmentmc.org/docs/getting-started#choose-a-version/
    const val PARCHMENT_MINECRAFT = "1.21"
    const val PARCHMENT_RELEASE = "2024.07.07"

    const val NEOFORM_VERSION = "1.21-20240613.152323" // // https://projects.neoforged.net/neoforged/neoform/
    const val NEOFORGE_VERSION = "21.0.143" // https://projects.neoforged.net/neoforged/neoforge/
    const val FML_CONSTRAINT = "[4,)" // https://projects.neoforged.net/neoforged/fancymodloader/
}