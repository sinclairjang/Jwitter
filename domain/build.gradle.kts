version = "0.0.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.projectlombok:lombok")
    implementation("mysql:mysql-connector-java")
    implementation("io.dropwizard.metrics:metrics-core")
    implementation("net.ttddyy:datasource-proxy:1.10")
    runtimeOnly("com.h2database:h2")
}


