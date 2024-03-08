plugins {
    `java-library`
    signing
    `maven-publish`
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
    jacoco
}

group = "de.lyca.cloudevents"
version = "1.1.0"

sourceSets {
    create("structuredContentMode") {}
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
    registerFeature("structuredContentMode") {
        usingSourceSet(sourceSets["structuredContentMode"])
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api("io.cloudevents:cloudevents-core")
    api("org.springframework.amqp:spring-amqp")
    "structuredContentModeApi"("io.cloudevents:cloudevents-json-jackson")

    constraints {
        implementation("org.springframework.amqp:spring-amqp:[3.0,3.99]")
        implementation("io.cloudevents:cloudevents-core:[2.0,2.99]")
        implementation("io.cloudevents:cloudevents-json-jackson:[2.0,2.99]")
        implementation("com.fasterxml.jackson.core:jackson-databind:[2.16.1,2.99]") {
            because("CVE-2022-42003 and CVE-2022-42004")
        }
    }

    testImplementation("io.cloudevents:cloudevents-json-jackson")

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

nexusPublishing {
    repositories {
        sonatype()
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name = "cloudevents-spring-amqp"
                description = "CloudEvents Binding for Spring AMQP"
                url = "https://github.com/lyca/cloudevents-spring-amqp"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "lyca"
                        name = "Lars Michele"
                        email = "lars.michele@tu-dortmund.de"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/lyca/cloudevents-spring-amqp.git"
                    developerConnection = "scm:git:git://github.com/lyca/cloudevents-spring-amqp.git"
                    url = "https://github.com/lyca/cloudevents-spring-amqp"
                }
            }
        }
    }
}

signing {
    sign(publishing.publications.findByName("maven"))
}