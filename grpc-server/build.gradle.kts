import com.google.protobuf.gradle.*

plugins {
    id("org.springframework.boot")
    id("com.google.protobuf") version "0.8.19"
    id("java")
}

dependencies {
    val versions = object {
        // NOTE: keep this in sync with Spring boot version, see: https://github.com/LogNet/grpc-spring-boot-starter/blob/master/ReleaseNotes.md
        val grpcStarter = "4.8.1"
        val reactorGrpc = "1.2.3"
    }

    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("io.github.lognet:grpc-spring-boot-starter:${versions.grpcStarter}")
    implementation("com.salesforce.servicelibs:reactor-grpc:${versions.reactorGrpc}")
    implementation("com.salesforce.servicelibs:reactor-grpc-stub:${versions.reactorGrpc}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

protobuf {
    val versions = object {
        val protobuf = "3.19.2"
        val grpc = "1.49.0"
        val reactorGrpc = "1.2.3"
    }
    protoc {
        artifact = "com.google.protobuf:protoc:${versions.protobuf}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${versions.grpc}"
        }
        id("reactor") {
            artifact = "com.salesforce.servicelibs:reactor-grpc:${versions.reactorGrpc}"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                // Apply the "grpc" plugin whose spec is defined above, without
                // options. Note the braces cannot be omitted, otherwise the
                // plugin will not be added. This is because of the implicit way
                // NamedDomainObjectContainer binds the methods.
                id("grpc") { }
                id("reactor") {}
            }
        }
    }

    generatedFilesBaseDir = "${project.buildDir}/protoGen"
}

sourceSets.configureEach {
    val protoGenDir = project.buildDir.toPath()
        .resolve("protoGen")
        .resolve(this.name)

    java.srcDirs(
        protoGenDir.resolve("java")
            .toFile(),
        protoGenDir.resolve("grpc")
            .toFile(),
        protoGenDir.resolve("reactor")
            .toFile()
    )
    resources.srcDirs(
        layout.projectDirectory.dir("../certificate").asFile
    )
}