plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    id("application")
}

application {
    mainClass.set("PdfToolsEncrypt.PdfToolsEncrypt")
}
repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
} 

java {
    sourceSets {
        main {
            java {
                srcDir("lib/src/main/java")
            }
        }
    }
}

dependencies {
    implementation("com.pdftools:pdftools-sdk:1.18.0")
}