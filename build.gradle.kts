plugins {
	`kotlin-dsl`
	id("org.springframework.boot") version "2.7.3"
	id("io.spring.dependency-management") version "1.0.14.RELEASE"
	id("java")
}

repositories {
	mavenCentral()
}

allprojects {
	group = "com.gateway.bugreport"
	version = "0.0.1-SNAPSHOT"

	tasks.withType<JavaCompile> {
		sourceCompatibility = "17"
		targetCompatibility = "17"
	}
}

subprojects {
	repositories {
		mavenCentral()
	}

	apply {
		plugin("io.spring.dependency-management")
	}
}

buildscript {
	extra["springCloudVersion"] = "2021.0.4"
}

dependencies {
	implementation("org.springframework.cloud:spring-cloud-starter-gateway") {
		exclude(module = "logback-classic")
	}

	testImplementation("org.springframework.boot:spring-boot-starter-test"){
		exclude(module = "logback-classic")
	}
	testImplementation("io.projectreactor:reactor-test")
}

dependencyManagement {
	val springCloudVersion: String by extra

	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

sourceSets.configureEach {
	resources.srcDirs(
		layout.projectDirectory.dir("certificate")
	)
}

tasks.register("bootRunWithPatchedFilter") {
	doFirst {
		tasks.bootRun.configure {
			systemProperty("spring.profiles.active", "patchedFilter")
		}
	}
	finalizedBy("bootRun")
}