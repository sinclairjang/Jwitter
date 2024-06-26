version = "0.0.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.7.6")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    runtimeOnly("com.h2database:h2")
    implementation("mysql:mysql-connector-java")
    implementation("io.dropwizard.metrics:metrics-core")
    implementation("net.ttddyy:datasource-proxy:1.10")
}


