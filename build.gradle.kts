plugins {
	java
	id("org.springframework.boot") version "3.5.9"
	id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "6.25.0"
	id("jacoco")
}

group = "com.handgrow"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("io.github.cdimascio:java-dotenv:5.2.2")
    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    // Redis cache
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    // OpenApi
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
	// Junit + Mockito
	testImplementation ("org.springframework.boot:spring-boot-starter-test")
	testImplementation ("org.mockito:mockito-core")
	testImplementation ("org.mockito:mockito-junit-jupiter")
	// Test dependency
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}


spotless {
    java {
        target("src/*/java/**/*.java")
        // Chọn chuẩn format (Google hoặc Palantir). Palantir đang được ưa chuộng hơn vì ít xuống dòng vô lý.
        palantirJavaFormat()
        removeUnusedImports()
    }
}

// 👇 Cực kỳ quan trọng: Tạo task để tự động cài Git Hook
tasks.register("createPreCommitHook") {
    doLast {
        val gitHooksDir = File(rootProject.rootDir, ".git/hooks")
        if (!gitHooksDir.exists()) gitHooksDir.mkdirs()

        val preCommitFile = File(gitHooksDir, "pre-commit")

        // Nội dung script chặn commit
        preCommitFile.writeText("""
            #!/bin/bash
            echo "Checking code format with Spotless..."
            ./gradlew spotlessCheck
            
            if [ ${'$'}? -ne 0 ]; then
                echo "❌ CODE FORMAT ERROR! Commit failed."
                echo "💡 Please run: ./gradlew spotlessApply"
                exit 1
            fi
        """.trimIndent())

        preCommitFile.setExecutable(true)
        println("✅ Pre-commit hook installed successfully!")
    }
}

// Tự động chạy task tạo hook mỗi khi build
tasks.named("build") { dependsOn("createPreCommitHook") }


tasks.test {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}


tasks.jacocoTestReport {
	dependsOn(tasks.test)

	reports {
		xml.required.set(true)
		html.required.set(true)
	}
}