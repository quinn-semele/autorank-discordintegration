plugins {
    id("multiloader-neoforge")
}

multiloader {
    mods {
        create("cloth-config") {
            requiresRepo("Shedaniel Maven", "https://maven.architectury.dev/", setOf("me.shedaniel.cloth", "dev.architectury"))
            required()

            artifacts {
                api("dev.architectury:architectury-neoforge:13.0.6") {
                    exclude(group="net.fabricmc")
                }
                api("me.shedaniel.cloth:cloth-config-neoforge:15.0.140") {
                    exclude(group="net.fabricmc")
                }
            }
        }

        create("ftbranks") {
            requiresRepo("Saps Maven", "https://maven.saps.dev/minecraft", setOf("dev.ftb.mods", "dev.latvian.mods"))
            required()

            artifacts {
                api("dev.ftb.mods:ftb-ranks-neoforge:2100.1.0")
            }
        }

        create("discordintegration") {
            required()
            requiresRepo("ErdbeerbaerLp's Maven", "https://repo.erdbeerbaerlp.de/repository/maven-public/", setOf("de.erdbeerbaerlp"))

            artifacts {
                api("de.erdbeerbaerlp:dcintegration.common:3.0.7")
            }
        }
    }
}