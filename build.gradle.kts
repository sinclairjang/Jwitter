plugins {
    id("java")
    id("org.springframework.boot") version "2.6.3" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
}

allprojects {
    group = "org.zerobase.jwitter"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}
subprojects { apply(plugin = "java") }

subprojects {
    apply {
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    tasks.test {
        useJUnitPlatform()
    }
}