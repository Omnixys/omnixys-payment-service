val javaLanguageVersion = project.properties["javaLanguageVersion"] as String? ?: JavaVersion.VERSION_23.majorVersion
val javaVersion = project.properties["javaVersion"] ?: libs.versions.javaVersion.get()

val enablePreview = if (project.properties["enablePreview"] == false) null else "--enable-preview"
val imagePath = project.properties["imagePath"] ?: "omnixys"
val imageTag = project.properties["imageTag"] ?: project.version.toString()

val tracePinnedThreads = project.properties["tracePinnedThreads"] == "true" || project.properties["tracePinnedThreads"] == "TRUE"
val alternativeBuildpack = project.properties["buildpack"]

val mapStructVerbose = project.properties["mapStructVerbose"] == "true" || project.properties["mapStructVerbose"] == "TRUE"
val useTracing = project.properties["tracing"] != "false" && project.properties["tracing"] != "FALSE"
val useDevTools = project.properties["devTools"] != "false" && project.properties["devTools"] != "FALSE"
val activeProfilesProtocol = if (project.properties["https"] != "false" && project.properties["https"] != "FALSE") "dev" else "dev,http"
val activeProfiles = if (System.getenv("ACTIVE_PROFILE") == "test") {
	"test"
} else {
	activeProfilesProtocol
}

plugins {
	java
	jacoco
	idea
	`project-report`
	id("org.springframework.boot") version libs.versions.springBootPlugin.get()
	id("io.spring.dependency-management") version libs.versions.dependencyManagement.get()
	// Aufruf: gradle versions
	id("com.github.nwillc.vplugin") version libs.versions.nwillcVPlugin.get()

	// https://github.com/ben-manes/gradle-versions-plugin
	// Aufruf: gradle dependencyUpdates
	id("com.github.ben-manes.versions") version libs.versions.benManesVersions.get()

	// https://github.com/markelliot/gradle-versions
	// Aufruf: gradle checkNewVersions
	id("com.markelliot.versions") version libs.versions.markelliotVersions.get()

	id("org.asciidoctor.jvm.convert") version libs.versions.asciidoctor.get()
	id("org.asciidoctor.jvm.pdf") version libs.versions.asciidoctor.get()
}

group = "com.omnixys"
version = "25.05.02"
extra["snippetsDir"] = file("build/generated-snippets")

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(libs.versions.javaVersion.get())
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
dependencyManagement {
	imports {
		mavenBom("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:2.15.0")
	}
}

extra["springCloudVersion"] = "2024.0.1"

dependencies {
	/**--------------------------------------------------------------------------------------------------------------------
	 * SECURITY
	 * --------------------------------------------------------------------------------------------------------------------*/
	runtimeOnly("org.bouncycastle:bcpkix-jdk18on:${libs.versions.bouncycastle.get()}") // Argon2
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-security")

	/**--------------------------------------------------------------------------------------------------------------------
	 * für MAPPER
	 * --------------------------------------------------------------------------------------------------------------------*/
	annotationProcessor("org.mapstruct:mapstruct-processor:${libs.versions.mapstruct.get()}")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:${libs.versions.lombokMapstructBinding.get()}")
	implementation("org.mapstruct:mapstruct:${libs.versions.mapstruct.get()}")

	/**-------------------------------------------------------------------------------------------------------------------
	 * GATEWAY
	 ***********************************************************************************************************************************/
	implementation("com.apollographql.federation:federation-graphql-java-support:5.3.0") // Apollo Federation Support

	/**------------------------------------------------------------------------------------------------------------------------
	 * TEST
	 * --------------------------------------------------------------------------------------------------------------------*/
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.springframework.graphql:spring-graphql-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:kafka")
	testImplementation("org.testcontainers:mysql")
	testImplementation("org.testcontainers:postgresql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.springframework:spring-webflux")

	/**----------------------------------------------------------------
	 * SPRING BOOT STARTER
	 **-------------------------------------------------------------*/
	implementation("org.springframework.boot:spring-boot-starter-actuator")//bei SecurityConfig
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-graphql")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	/**--------------------------------------------------------------------------------------------------------------------
	 * DATENBANK
	 * --------------------------------------------------------------------------------------------------------------------*/
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("org.postgresql:postgresql")

	/**------------------------------------------------------------------------------------------------------------------------
	 * WICHTIGE EXTRAS
	 * --------------------------------------------------------------------------------------------------------------------*/
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok:${libs.versions.lombok.get()}")
	annotationProcessor("org.hibernate:hibernate-jpamodelgen:${libs.versions.hibernateJpamodelgen.get()}")
	implementation("io.github.cdimascio:dotenv-java:${libs.versions.dotenv.get()}") // Bibliothek für .env-Datei

	/**------------------------------------------------------------------------------------------------------------------------
	 * MESSANGER
	 * --------------------------------------------------------------------------------------------------------------------*/
	implementation("org.springframework.kafka:spring-kafka")
	implementation("com.fasterxml.jackson.core:jackson-databind")
	implementation("com.fasterxml.jackson.module:jackson-module-parameter-names")

	/**------------------------------------------------------------------------------------------------------------------------
	 * WEITERE EXTRAS
	 * --------------------------------------------------------------------------------------------------------------------*/
	implementation("com.google.guava:guava:30.1-jre") //für Splitt-operation in FlightRepository
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")

	compileOnly("com.github.spotbugs:spotbugs-annotations:${libs.versions.spotbugs.get()}")
	testCompileOnly("com.github.spotbugs:spotbugs-annotations:${libs.versions.spotbugs.get()}")
	testImplementation("org.gaul:modernizer-maven-annotations:${libs.versions.modernizer.get()}")
//  implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
//  implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	/**------------------------------------------------------------------------------------------------------------------------
	 * OBSERVABILITY
	 * --------------------------------------------------------------------------------------------------------------------*/
		implementation("io.opentelemetry:opentelemetry-exporter-otlp")

		// --- Micrometer Basis ---
		implementation("io.micrometer:micrometer-observation")
		implementation("io.micrometer:micrometer-tracing")

		// --- Bridge zu OpenTelemetry ---
		implementation("io.micrometer:micrometer-tracing-bridge-otel")

		// --- Micrometer Prometheus Export ---
		runtimeOnly("io.micrometer:micrometer-registry-prometheus")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport) // Nach Tests Coverage Report erstellen
}

tasks.jacocoTestReport {
	dependsOn(tasks.test) // Coverage-Report erst nach den Tests generieren
	reports {
		xml.required.set(true) // Codecov benötigt das XML-Format
		html.required.set(true)
		csv.required.set(false)
	}
}

tasks.named("bootRun", org.springframework.boot.gradle.tasks.run.BootRun::class.java) {
	if (enablePreview != null) {
		jvmArgs(enablePreview)
	}
	// systemProperty("spring.profiles.active", activeProfiles)
	systemProperty("logging.file.name", "./build/log/application.log")
	systemProperty("server.tomcat.basedir", "build/tomcat")
	systemProperty("keycloak.client-secret", project.properties["keycloak.client-secret"]!!)
	systemProperty("keycloak.issuer", project.properties["keycloak.issuer"]!!)
	//systemProperty("app.keycloak.host", project.properties["keycloak.host"]!!)

	if (tracePinnedThreads) {
		systemProperty("tracePinnedThreads", "full")
	}
}

tasks.named<JavaCompile>("compileJava") {
	with(options) {
		isDeprecation = true
		with(compilerArgs) {
			if (enablePreview != null) {
				add(enablePreview)
			}
			// javac --help-lint
			add("-Xlint:all,-serial,-processing,-preview")
			add("--add-opens")
			add("--add-exports")
		}
	}
}

tasks.named("bootBuildImage", org.springframework.boot.gradle.tasks.bundling.BootBuildImage::class.java) {
	// statt "created xx years ago": https://medium.com/buildpacks/time-travel-with-pack-e0efd8bf05db
	createdDate = "now"

	// default:   imageName = "docker.io/${project.name}:${project.version}"
	imageName = "$imagePath/${project.name}:$imageTag"

	@Suppress("StringLiteralDuplication")
	environment = mapOf(
		"BP_JVM_VERSION" to javaLanguageVersion, // default: 17
		"BPL_JVM_THREAD_COUNT" to "20", // default: 250 (reactive: 50)
		"BPE_DELIM_JAVA_TOOL_OPTIONS" to " ",
		"BPE_APPEND_JAVA_TOOL_OPTIONS" to enablePreview,
	)
	imageName = imageName.get()
	println("")
	println("Buildpacks: JVM durch   B e l l s o f t   L i b e r i c a   (default)")
	println("")
}

tasks.test {
	outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.named<Javadoc>("javadoc") {
	options {
		showFromPackage()
		// outputLevel = org.gradle.external.javadoc.JavadocOutputLevel.VERBOSE

		if (this is CoreJavadocOptions) {
			// Keine bzw. nur elementare Warnings anzeigen wegen Lombok
			// https://stackoverflow.com/questions/52205209/configure-gradle-build-to-suppress-javadoc-console-warnings
			addStringOption("Xdoclint:none", "-quiet")
			// https://stackoverflow.com/questions/59485464/javadoc-and-enable-preview
			addBooleanOption("-enable-preview", true)
			addStringOption("-release", javaLanguageVersion)
		}

		if (this is StandardJavadocDocletOptions) {
			author(true)
			bottom("Copyright &#169; 2016 - present J&uuml;rgen Zimmermann, Hochschule Karlsruhe. All rights reserved.")
		}
	}
}

tasks.named<Jar>("jar") {
	archiveFileName.set("kunde-2024.04.0.jar")
}

tasks.named("dependencyUpdates", com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask::class) {
	checkConstraints = true
}

idea {
	module {
		isDownloadSources = true
		isDownloadJavadoc = true
		sourceDirs.add(file("generated/"))
		generatedSourceDirs.add(file("generated/"))
	}
}