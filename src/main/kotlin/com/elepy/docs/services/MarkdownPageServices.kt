package com.elepy.docs.services

import com.elepy.annotations.Inject
import com.elepy.annotations.Route
import com.elepy.dao.Crud
import com.elepy.docs.MarkdownPage
import com.elepy.docs.MarkdownPageType
import com.elepy.exceptions.ElepyException
import com.elepy.http.AccessLevel
import com.elepy.http.HttpMethod
import com.elepy.http.Request
import com.elepy.http.Response
import com.elepy.routes.MappedFindMany
import com.elepy.routes.SimpleCreate
import com.elepy.routes.SimpleDelete
import com.elepy.routes.SimpleUpdate
import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.concurrent.thread


class MarkdownPageFindMany : MappedFindMany<MarkdownPage, MarkdownPage>() {
    override fun mapValues(objectsToMap: MutableList<MarkdownPage>, request: Request, crud: Crud<MarkdownPage>?): List<MarkdownPage> {
        val includeContent = request.queryParamOrDefault("includeContent", "true")!!.toBoolean()

        return if (includeContent) {
            objectsToMap
        } else {
            objectsToMap.map { o ->
                MarkdownPage(o.id, o.title, o.slug, o.type, o.live, null)
            }
        }
    }
}

class MarkdownPageCreate : SimpleCreate<MarkdownPage>() {
    override fun afterCreate(updatedVersion: MarkdownPage, crud: Crud<MarkdownPage>) {
        thread {
            updateGithub(updatedVersion.type.directory, "${updatedVersion.title}.md", updatedVersion.content!!, updatedVersion.live!!)
        }
    }

    override fun beforeCreate(objectForCreation: MarkdownPage, crud: Crud<MarkdownPage>) {

    }

}

class MarkdownPageUpdate : SimpleUpdate<MarkdownPage>() {
    override fun afterUpdate(beforeVersion: MarkdownPage, updatedVersion: MarkdownPage, crud: Crud<MarkdownPage>) {
        if (beforeVersion.content != updatedVersion.content || beforeVersion.title != updatedVersion.title || beforeVersion.live != updatedVersion.live) {
            thread {
                if (beforeVersion.title != updatedVersion.title || beforeVersion.type != updatedVersion.type) {
                    updateGithub(beforeVersion.type.directory, "${beforeVersion.title}.md", "", false)
                }
                updateGithub(updatedVersion.type.directory, "${updatedVersion.title}.md", updatedVersion.content!!, updatedVersion.live!!)
            }
        }
    }

    override fun beforeUpdate(beforeVersion: MarkdownPage?, crud: Crud<MarkdownPage>?) {
    }
}

class MarkdownPageDelete : SimpleDelete<MarkdownPage>() {
    override fun afterDelete(itemToDelete: MarkdownPage, dao: Crud<MarkdownPage>?) {
        thread {
            updateGithub(itemToDelete.type.directory, "${itemToDelete.title}.md", "", false)
        }
    }

    override fun beforeDelete(deletedItem: MarkdownPage?, dao: Crud<MarkdownPage>?) {
    }
}

class MarkdownPageRoutes {

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Inject
    lateinit var crud: Crud<MarkdownPage>

    @Route(requestMethod = HttpMethod.GET, path = "/page-types")
    fun getPageTypes(request: Request, response: Response) {
        val list = MarkdownPageType.values().map { type ->
            mapOf("name" to type.pageTypeName, "value" to type.name)
        }
        response.result(objectMapper.writeValueAsString(list))
    }

    @Route(accessLevel = AccessLevel.PUBLIC, path = "/api/sync-page/:id", requestMethod = HttpMethod.GET)
    fun syncPageWithGithub(request: Request, response: Response) {

        val page = crud.getById(request.params("id")).orElseThrow { ElepyException("No page found with this ID.", 404) }
        val githubContent = getContent(page.type.directory, page.title + ".md")

        if (githubContent != null) {
            val pageContentLen = page.content!!.length
            val githubContentLen = githubContent.length

            if (page.content == githubContent) {
                println("Already")
                throw ElepyException("Already in sync with GitHub", 200)
            }
            page.content = githubContent
            crud.update(page)
            println("Done")
            throw ElepyException("Successfully synced with GitHub", 200)
        } else {
            println("None")
            throw ElepyException("No GitHub page found titled: ${page.title}.md", 404)
        }


    }
}