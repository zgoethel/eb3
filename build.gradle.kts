import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

import org.kt3k.gradle.plugin.coveralls.CoverallsTask

plugins {
	kotlin("jvm") version "1.4.20"

	java
	application

	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	id("org.springframework.boot") version "2.4.2"

	jacoco
	
	id("com.github.kt3k.coveralls") version "2.10.2"
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType(KotlinCompile::class).all {
	kotlinOptions {
		jvmTarget = "11"

		@Suppress("SuspiciousCollectionReassignment")
		freeCompilerArgs += "-include-runtime"
	}
}

application {
	mainClass.set("net.jibini.eb.EasyButton")
}

repositories {
	jcenter()
	mavenCentral()
}

dependencies {
	// Kotlin platform and reflection libraries
	api(kotlin("stdlib"))
	api(kotlin("reflect"))

	// Kotlin coroutine multithreading utilities
	api("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.4.2")

	// Spring Boot starter libraries
	implementation("org.springframework.boot", "spring-boot-starter-tomcat")
	implementation("org.springframework.boot", "spring-boot-starter-web")
	implementation("org.springframework.boot", "spring-boot-starter-webflux")

	implementation("org.projectreactor", "reactor-spring", "1.0.1.RELEASE")

	// Template and JSP utility libraries
	implementation("javax.servlet", "jstl", "1.2")
	implementation("org.apache.tomcat.embed", "tomcat-embed-jasper")

	// Serialization and classpath libraries
	implementation("com.google.code.gson", "gson", "2.8.6")
	implementation("io.github.classgraph", "classgraph", "4.8.90")

	// JSON API-level library
	api("org.json", "json", "20200518")

	// Vendor-specific dependencies
	implementation("org.apache.poi", "poi", "5.0.0")
	implementation("org.apache.poi", "poi-ooxml", "5.0.0")

	// JUnit 4 test implementation library
	testImplementation("junit", "junit", "4.12")
}

// Gather test result data into root report
tasks.register<JacocoReport>("jacocoRootReport") {
	executionData.setFrom(files((tasks.getByName("jacocoTestReport") as JacocoReport).executionData))

	reports {
		html.isEnabled = true
		xml.isEnabled = true
		csv.isEnabled = false
	}

	onlyIf { true }
}

// Set up the Jacoco initial testing reports
tasks.withType<JacocoReport> {
	dependsOn.add(tasks.test)

	classDirectories.setFrom(
		sourceSets.main.get().output.asFileTree
	)
}

// Include all source for Coveralls
coveralls.sourceDirs.addAll(sourceSets.main.get().allSource.srcDirs.map { it.path })
// Set the path of the root report
coveralls.jacocoReportPath = "${buildDir}/reports/jacoco/jacocoRootReport/jacocoRootReport.xml"

// Set up the Coveralls root report uploading
tasks.withType<CoverallsTask>
{
	dependsOn.add(tasks.getByName("jacocoRootReport"))

	onlyIf { true }
}