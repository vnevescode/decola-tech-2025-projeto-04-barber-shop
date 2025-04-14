import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import org.gradle.testing.jacoco.tasks.JacocoReport


plugins {
	java
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.7"
	jacoco
}

group = "br.com.dio"
version = "0.0.1-SNAPSHOT"

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

val mapStructVersion = "1.6.0.Beta1"
val flywayVersion = "9.22.3"

repositories {
	mavenCentral()
	maven { url = uri("https://maven.google.com") }
}

dependencies {

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("com.bucket4j:bucket4j-core:8.2.0")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("com.googlecode.owasp-java-html-sanitizer:owasp-java-html-sanitizer:20220608.1")
	implementation("io.github.cdimascio:dotenv-java:3.0.0")

	implementation("org.mapstruct:mapstruct:$mapStructVersion")
	implementation("org.flywaydb:flyway-core:$flywayVersion")


	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor("org.mapstruct:mapstruct-processor:$mapStructVersion")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()

	// Exibe o resultado de cada teste no console
	testLogging {
		events("PASSED", "FAILED", "SKIPPED")
		exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
		showStandardStreams = true
	}

	val RED = "\u001B[31m"
	val GREEN = "\u001B[32m"
	val BRIGHT_GREEN = "\u001B[92m"
	val YELLOW = "\u001B[33m"
	val BLUE = "\u001B[36m"
	val GRAY = "\u001B[37m"
	val RESET = "\u001B[0m"

	addTestListener(object : TestListener {
		override fun afterSuite(desc: TestDescriptor, result: TestResult) {
			if (desc.parent == null) {
				val durationMillis = result.endTime - result.startTime
				val durationSeconds = durationMillis / 1000
				val hours = durationSeconds / 3600
				val minutes = (durationSeconds % 3600) / 60
				val seconds = durationSeconds % 60

				println("\n${BLUE}Resumo dos Testes:${RESET}")
				println(" -- ${GREEN}SUCCESSFUL:${RESET} ${result.successfulTestCount}")
				println(" -- ${RED}FAILED:${RESET}     ${result.failedTestCount}")
				println(" -- ${GRAY}SKIPPED:${RESET}    ${result.skippedTestCount}")
				println(" -- ${YELLOW}Duracao:${RESET} ${"%02d".format(hours)}h ${"%02d".format(minutes)}m ${"%02d".format(seconds)}s")

				if (result.failedTestCount > 0) {
					println("\n${RED}Algum teste falhou! Verifique os detalhes acima.${RESET}")
				} else {
					println("\n${BRIGHT_GREEN}Todos os testes passaram com sucesso!${RESET}")
				}
			}
		}

		override fun beforeSuite(desc: TestDescriptor) {}
		override fun beforeTest(desc: TestDescriptor) {}
		override fun afterTest(desc: TestDescriptor, result: TestResult) {}
	})
}

// Gera arquivo trigger.txt
tasks.named("build") {
	doLast {
		val trigger = file("src/main/resources/trigger.txt")
		if (!trigger.exists()) {
			trigger.createNewFile()
		}
		trigger.writeText(Date().time.toString())
	}
}

// Task customizada para gerar arquivos de migration Flyway
tasks.register("generateFlywayMigrationFile") {
	description = "Generate flyway migration"
	group = "Flyway"

	doLast {
		val migrationsDir = file("src/main/resources/db/migration")
		if (!migrationsDir.exists()) {
			migrationsDir.mkdirs()
		}

		val migrationNameFromConsole = project.findProperty("migrationName") as String?
			?: throw IllegalArgumentException("Você deve fornecer um nome para a migração com -PmigrationName=nome")

		val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
		val migrationName = "V${timestamp}__${migrationNameFromConsole}.sql"
		val migrationFile = file("${migrationsDir.path}/$migrationName")

		migrationFile.writeText("-- $migrationName generated in ${migrationsDir.path}")
		println("Migration file created: ${migrationFile.path}")
	}
}

tasks.named<JacocoReport>("jacocoTestReport") {
	dependsOn(tasks.test)

	reports {
		xml.required.set(true)
		html.required.set(true)
	}

	/*finalizedBy("jacocoCoverageSummary")*/
}

/*
tasks.register("jacocoCoverageSummary") {
	group = "verification"
	description = "📈 Imprime cobertura no terminal com emojis"

	dependsOn("jacocoTestReport")

	doLast {
		val reportFile = layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml").get().asFile
		if (!reportFile.exists()) {
			println("❌ Arquivo de cobertura não encontrado.")
			return@doLast
		}

		val content = reportFile.readText()
		val instructionRegex = Regex("""<counter type="INSTRUCTION" missed="(\d+)" covered="(\d+)" />""")

		val match = instructionRegex.find(content)

		if (match != null) {
			val missed = match.groupValues[1].toDouble()
			val covered = match.groupValues[2].toDouble()
			val total = missed + covered
			val percentage = if (total > 0) (covered / total) * 100 else 0.0

			val RED = "\u001B[31m"
			val GREEN = "\u001B[32m"
			val YELLOW = "\u001B[33m"
			val BLUE = "\u001B[36m"
			val RESET = "\u001B[0m"

			val cor = when {
				percentage >= 80 -> GREEN
				percentage >= 50 -> YELLOW
				else -> RED
			}

			println()
			println("$BLUE📊 Resumo da Cobertura de Código:$RESET")
			println("   ✔️ Instruções cobertas : ${covered.toInt()}")
			println("   ❌ Instruções perdidas : ${missed.toInt()}")
			println("   📈 Total               : ${total.toInt()}")
			println("   🔍 Cobertura total     : $cor${"%.2f".format(percentage)}%$RESET")
			println()
		} else {
			println("⚠️ Não foi possível extrair os dados de cobertura do XML.")
		}
	}
}

tasks.register("coverageFullReport") {
	group = "verification"
	description = "✅ Executa testes, gera cobertura e mostra resumo com emojis"

	dependsOn("test", "jacocoTestReport", "jacocoCoverageSummary")
}*/
