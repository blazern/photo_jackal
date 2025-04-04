plugins {
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
    google()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin.api)
    implementation(gradleKotlinDsl())
    testImplementation("junit:junit:4.13.2")
}

gradlePlugin {
    plugins {
        create("appVersionPlugin") {
            id = "blazern.photo_jackal.appVersionPlugin"
            implementationClass = "AppVersionPlugin"
        }
    }
}
