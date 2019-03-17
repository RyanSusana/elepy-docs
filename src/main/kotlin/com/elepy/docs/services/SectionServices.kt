package com.elepy.docs.services

import com.elepy.dao.Crud
import com.elepy.di.ElepyContext
import com.elepy.docs.Section
import com.elepy.docs.SectionVisibility
import com.elepy.evaluators.ObjectEvaluator
import com.elepy.exceptions.ErrorMessageBuilder
import com.elepy.routes.SimpleCreate
import com.elepy.routes.SimpleUpdate
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import kotlin.concurrent.thread

//Used to determine the validity of Sections. Their CSS id's can't contain spaces
class SectionEvaluator : ObjectEvaluator<Section> {
    override fun evaluate(section: Section, cls: Class<Section>) {
        if (section.cssId.trim().contains(" "))
            throw ErrorMessageBuilder.anElepyErrorMessage().withMessage("CSS ID's can't contain spaces").withStatus(400).build()!!
    }
}

//Update GitHub whenever you create a section
class SectionCreate : SimpleCreate<Section>() {
    override fun beforeCreate(objectForCreation: Section?, crud: Crud<Section>?) {
        //Do nothing
    }

    override fun afterCreate(createdObject: Section, crud: Crud<Section>?) {
        thread {
            updateGithub("${createdObject.cssId}.md", createdObject.content, createdObject.visibility)
        }
    }
}

//Update GitHub whenever you update a section
class SectionUpdate : SimpleUpdate<Section>() {
    override fun beforeUpdate(beforeVersion: Section?, crud: Crud<Section>?) {
        //Do nothing
    }

    override fun afterUpdate(beforeVersion: Section, updatedVersion: Section, crud: Crud<Section>?) {

        thread {
            updateGithub("${updatedVersion.cssId}.md", updatedVersion.content, updatedVersion.visibility)
        }
    }
}

fun GitHub.elepy(): GHRepository {
    return this.getRepository(if (System.getenv("testing") == null) "RyanSusana/elepy" else "RyanSusana/elepy-docs")
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
                    .filter { githubFile ->
                        name == githubFile.name
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

