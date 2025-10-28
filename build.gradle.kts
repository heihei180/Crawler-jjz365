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
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

// 👇 指定主类，这是 run 任务必需的
application {
    mainClass.set("CameraDataKt") // ⚠️ 根据你的主文件名调整
}