package com.elepy.docs.services

import com.elepy.concepts.ObjectEvaluator
import com.elepy.dao.Crud
import com.elepy.di.ElepyContext
import com.elepy.docs.Section
import com.elepy.docs.updateGithub
import com.elepy.exceptions.ErrorMessageBuilder
import com.elepy.routes.SimpleCreate
import com.elepy.routes.SimpleUpdate
import kotlin.concurrent.thread

class SectionEvaluator : ObjectEvaluator<Section> {
    override fun evaluate(section: Section, cls: Class<Section>) {
        if (section.cssId.trim().contains(" "))
            throw ErrorMessageBuilder.anElepyErrorMessage().withMessage("CSS ID's can't contain spaces").withStatus(400).build()!!
    }
}

class SectionCreate : SimpleCreate<Section>() {
    override fun beforeCreate(objectForCreation: Section?, crud: Crud<Section>?, elepy: ElepyContext?) {
        //Do nothing
    }

    override fun afterCreate(createdObject: Section, crud: Crud<Section>?, elepy: ElepyContext?) {
        thread {
            updateGithub("${createdObject.cssId}.md", createdObject.content, createdObject.visibility)
        }
    }
}

class SectionUpdate : SimpleUpdate<Section>() {
    override fun beforeUpdate(beforeVersion: Section?, crud: Crud<Section>?, elepy: ElepyContext?) {
        //Do nothing
    }

    override fun afterUpdate(beforeVersion: Section, updatedVersion: Section, crud: Crud<Section>?, elepy: ElepyContext?) {

        thread {
            updateGithub("${updatedVersion.cssId}.md", updatedVersion.content, updatedVersion.visibility)
        }

    }
}
