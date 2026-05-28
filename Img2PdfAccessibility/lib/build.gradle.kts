plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    id("application")
}

application {
    mainClass.set("PdfToolsImg2PdfAccessibility.PdfToolsImg2PdfAccessibility")
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