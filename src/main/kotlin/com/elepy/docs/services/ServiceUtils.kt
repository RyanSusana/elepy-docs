package com.elepy.docs.services

import org.kohsuke.github.GHContent
import org.kohsuke.github.GHFileNotFoundException
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub

fun GitHub.elepy(): GHRepository {
    return this.getRepository(if (System.getenv("testing") == null) "RyanSusana/elepy-wiki" else "RyanSusana/elepy-wiki")
}

fun getContent(directory: String, name: String): String? {

    return try {

        val gitHub: GitHub = GitHub.connectUsingPassword(System.getenv("GITHUB_USERNAME"), System.getenv("GITHUB_PASSWORD"))


        val fileContent = gitHub.elepy().getFileContent("$directory/${name.gitHubSafeName()}")

        fileContent.content
    } catch (e: GHFileNotFoundException) {
        null
    }
}

fun updateGithub(directory: String, name: String, content: String, showOnGithub: Boolean) {

    val gitHub: GitHub = GitHub.connectUsingPassword(System.getenv("GITHUB_USERNAME"), System.getenv("GITHUB_PASSWORD"))
    val directoryContent: MutableList<GHContent> = try {
        gitHub.elepy().getDirectoryContent(directory)

    } catch (e: GHFileNotFoundException) {
        mutableListOf()
    }
    if (showOnGithub) {
        if (directoryContent.stream().noneMatch { ghcontent -> ghcontent.name == name.gitHubSafeName() }) {
            try {
                println("")
                gitHub.elepy()
                        .createContent()
                        .message("created: ${name.gitHubSafeName()}")
                        .content(content)
                        .path("$directory/${name.gitHubSafeName()}")
                        .commit()
            } catch (e: Exception) {
                //Bug with the Library. It does an update instead of a create.
                e.printStackTrace()
            }

        } else {
            gitHub.elepy()
                    .getDirectoryContent(directory)
                    .stream()
                    .filter { githubFile ->
                        name.gitHubSafeName() == githubFile.name
                    }
                    .findAny()
                    .ifPresent { foundContent ->
                        gitHub.elepy()
                                .createContent()
                                .message("updated: ${name.gitHubSafeName()}")
                                .content(content).path("$directory/${name.gitHubSafeName()}")
                                .sha(foundContent.sha)
                                .commit()
                    }
        }
    } else {
        if (directoryContent.stream().anyMatch { ghcontent -> ghcontent.name == name.gitHubSafeName() }) {
            gitHub.elepy().getFileContent("$directory/${name.gitHubSafeName()}").delete("removed: ${name.gitHubSafeName()}")
        }
    }
}

fun String.gitHubSafeName(): String {
    return this.replace(".", "-").replace(" ", "-").toUpperCase() + ".md"
}