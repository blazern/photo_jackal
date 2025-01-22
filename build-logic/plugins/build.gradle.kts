plugins {
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin.api)
    implementation(gradleKotlinDsl())
}

gradlePlugin {
    plugins {
        create("appVersionPlugin") {
            id = "blazern.photo_jackal.appVersionPlugin"
            implementationClass = "AppVersionPlugin"
        }
    }
}
