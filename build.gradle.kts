import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "com.masmovil"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "3.9.2"
val junitJupiterVersion = "5.6.0"
val assertjVersion = "3.8.0"
val mockitoVersion = "2.20.1"
val junitVersion   = "4.12"


val mainVerticleName = "com.masmovil.best_sellers.MainVerticle"
val watchForChange = "src/**/*"
val doOnChange = "./gradlew classes"
val launcherClassName = "io.vertx.core.Launcher"

application {
  mainClassName = launcherClassName
}

dependencies {
  implementation("io.vertx:vertx-rx-java2:$vertxVersion")
  implementation("io.vertx:vertx-web:$vertxVersion")
  implementation("io.vertx:vertx-pg-client:$vertxVersion")
  implementation("org.postgresql:postgresql:42.2.10")
  compile("io.vertx:vertx-pg-client:3.9.2")
  compile("com.zaxxer:HikariCP:2.2.5")
  testCompile("org.mockito:mockito-all:1.10.19")
  testCompile("junit:junit:$junitVersion")
  testCompile("org.assertj:assertj-core:$assertjVersion")
  testCompile("org.mockito:mockito-core:$mockitoVersion")
  testCompile("org.testcontainers:testcontainers:1.14.3")
  testCompile("org.testcontainers:postgresql:1.14.3")
  testImplementation("io.vertx:vertx-unit:$vertxVersion")
  testImplementation("junit:junit:4.13")
}

  java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles {
    include("META-INF/services/io.vertx.core.spi.VerticleFactory")
  }
}

tasks.withType<Test> {
  useJUnit()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}
