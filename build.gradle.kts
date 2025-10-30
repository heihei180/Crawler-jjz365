plugins {
    kotlin("jvm") version "2.0.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {

    mavenLocal()
    maven(
        url = uri("https://mvn.cloud.alipay.com/nexus/content/repositories/open/")
    )
    maven {
        url = uri("https://maven.tencent.com/repository/maven-public/")
    }
    maven {
//          url 'https://maven.aliyun.com/repository/central'
        url = uri("https://maven.aliyun.com/repository/central")
    }
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

//    <!-- JavaFX -->

    // JavaFX 依赖（注意：必须为你的操作系统选择正确的 classifier）
    val javafxVersion = "18.0.2"
    val osName = System.getProperty("os.name")
    val targetOs = when {
        osName == "Mac OS X" -> "mac"
        osName == "Linux" -> "linux"
        osName.startsWith("Windows") -> "win"
        else -> throw GradleException("OS $osName is not supported")
    }

    implementation("org.openjfx:javafx-base:$javafxVersion:$targetOs")
    implementation("org.openjfx:javafx-controls:$javafxVersion:$targetOs")
    implementation("org.openjfx:javafx-fxml:$javafxVersion:$targetOs")
    implementation("org.openjfx:javafx-graphics:$javafxVersion:$targetOs")
    implementation("org.openjfx:javafx-media:$javafxVersion:$targetOs")
    implementation("org.openjfx:javafx-web:$javafxVersion:$targetOs")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

// 👇 指定主类，这是 run 任务必需的
application {
    mainClass.set("org.example.MainKt") // ⚠️ 根据你的主文件名调整
}


// 🔥 关键：JVM 参数 - 添加模块路径
tasks.withType<JavaExec> {
    val osName = System.getProperty("os.name")
    val targetOs = when {
        osName == "Mac OS X" -> "mac"
        osName == "Linux" -> "linux"
        osName.startsWith("Windows") -> "win"
        else -> throw GradleException("OS $osName is not supported")
    }

    jvmArgs = listOf(
        "--module-path", "\$MODULE_PATH",
        "--add-modules", "javafx.controls,javafx.fxml,javafx.web,javafx.media"
    )

    // 动态替换 \$MODULE_PATH 为实际路径
    doFirst {
        val javafxModules = configurations.runtimeClasspath.get()
            .filter { it.name.contains("javafx") && it.name.contains(targetOs) }
            .joinToString(separator = File.pathSeparator) { it.absolutePath }

        jvmArgs = jvmArgs!!.map { arg ->
            if (arg == "\$MODULE_PATH") javafxModules else arg
        }
    }
}