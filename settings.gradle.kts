pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "AuraLauncher"
include(":app")
if (providers.gradleProperty("includeQaProbe").isPresent) include(":qa-probe")

include(":macrobenchmark", ":baselineprofile")
