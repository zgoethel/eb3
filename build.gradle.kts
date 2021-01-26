import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.4.20"

	`java-library`
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

	// JUnit 4 test implementation library
	testImplementation("junit", "junit", "4.12")
}

tasks.withType<JacocoReport> {
	classDirectories.setFrom(
		sourceSets.main.get().output.asFileTree
	)
}

coveralls.sourceDirs.addAll(sourceSets.main.get().allSource.srcDirs.map { it.path })
coveralls.jacocoReportPath = "${buildDir}/reports/jacoco/jacocoRootReport/jacocoRootReport.xml"

tasks.withType<org.kt3k.gradle.plugin.coveralls.CoverallsTask>
{
	dependsOn.add(tasks.getByName("jacocoTestReport"))
	onlyIf { System.getenv()["CI"] != null }
}