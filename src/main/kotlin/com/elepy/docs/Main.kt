package com.elepy.docs

import com.elepy.Elepy
import com.elepy.admin.ElepyAdminPanel
import com.elepy.plugins.gallery.ElepyGallery
import com.github.fakemongo.Fongo
import com.mongodb.DB
import com.mongodb.MongoClient
import com.mongodb.ServerAddress
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub

fun main(args: Array<String>) {

    Logger.getRootLogger().level = Level.INFO
    org.apache.log4j.BasicConfigurator.configure()
    val databaseServer: String = System.getenv("DATABASE_SERVER") ?: "localhost"
    val databasePort: String = System.getenv("DATABASE_PORT") ?: "27017"


    val mongoClient = MongoClient(ServerAddress(databaseServer, databasePort.toInt()))

    val elepyDB = if (System.getenv("testing") == null) mongoClient.getDB("elepy-docs") else Fongo("test").getDB("test")

    val elepy = Elepy()
            .onPort(4242)
            .addExtension(ElepyAdminPanel().addPlugin(ElepyGallery()))
            .attachSingleton(DB::class.java, elepyDB)

    elepy.http().staticFiles.location("/public")

    elepy.addModels(Section::class.java, Guide::class.java, News::class.java)
    elepy.start()
}

fun updateGithub(name: String, content: String, visibility: SectionVisibility) {

    val gitHub: GitHub = GitHub.connectUsingPassword(System.getenv("GITHUB_USERNAME"), System.getenv("GITHUB_PASSWORD"))

    val directoryContent = gitHub.elepy().getDirectoryContent("docs")

    if (visibility.showOnGitHub) {
        if (directoryContent.stream().noneMatch { ghcontent -> ghcontent.name == name }) {
            try {
                gitHub.elepy()
                        .createContent()
                        .message("AUTOMATIC DOCUMENTATION UPDATE: $name")
                        .content(content)
                        .path("docs/$name")
                        .commit()
            } catch (e: Exception) {
                //Bug with the Library. It does an update instead of a create.
                e.printStackTrace()
            }

        } else {
            gitHub.elepy()
                    .getDirectoryContent("docs")
                    .stream()
                    .filter { content ->
                        name == content.name
                    }
                    .findAny()
                    .ifPresent { foundContent ->
                        gitHub.elepy()
                                .createContent()
                                .message("AUTOMATIC DOCUMENTATION UPDATE: $name")
                                .content(content).path("docs/$name")
                                .sha(foundContent.sha)
                                .commit()
                    }
        }
    } else {
        if (directoryContent.stream().anyMatch { ghcontent -> ghcontent.name == name }) {
            gitHub.elepy().getFileContent("docs/$name").delete("REMOVED DOCUMENTATION: $name")
        }
    }
}

fun GitHub.elepy(): GHRepository {
    return this.getRepository(if (System.getenv("testing") == null) "RyanSusana/elepy" else "RyanSusana/elepy-docs")
}

