package com.elepy.docs.services

import com.elepy.dao.Crud
import com.elepy.docs.Section
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
            updateGithub("home", "${createdObject.cssId}.md", createdObject.content, createdObject.visibility.showOnGitHub)
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
            updateGithub("home", "${updatedVersion.cssId}.md", updatedVersion.content, updatedVersion.visibility.showOnGitHub)
        }
    }
}

fun GitHub.elepy(): GHRepository {
    return this.getRepository(if (System.getenv("testing") == null) "RyanSusana/elepy-wiki" else "RyanSusana/elepy-wiki")
}



