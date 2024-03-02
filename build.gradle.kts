plugins {
    `java-library`
    `java-library-distribution`
    jacoco
}

group = "de.lyca.cloudevents"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.cloudevents:cloudevents-json-jackson:2.5.0")
    constraints {
        implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1") {
            because("CVE-2022-42003 and CVE-2022-42004")
        }
    }
    implementation("org.springframework.amqp:spring-amqp:3.1.2")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.assertj:assertj-core:3.25.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        csv.required = true
    }
}
