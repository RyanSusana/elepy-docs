package com.elepy.docs.routes

import com.elepy.annotations.Inject
import com.elepy.annotations.Route
import com.elepy.dao.Crud
import com.elepy.docs.MarkdownPage
import com.elepy.docs.MarkdownPageType
import com.elepy.docs.TemplateCompiler
import com.elepy.http.HttpMethod
import com.elepy.http.Request
import com.elepy.http.Response


class DocumentationRoutes {

    private val notFoundContent: String =
            """
                # 404 Page Not Found
                Try another one!
            """.trimIndent()

    private val introContent: String =
            """
                # Welcome to the Elepy docs!
                You can find a list of links in the sidebar
            """.trimIndent()

    val intro = MarkdownPage("", "Introduction", "", MarkdownPageType.DOCUMENTATION_PAGE, true, introContent)
    val notFound = MarkdownPage("", "404 Not Found", "", MarkdownPageType.DOCUMENTATION_PAGE, true, notFoundContent)
    @Inject
    lateinit var documentationCrud: Crud<MarkdownPage>


    @Inject
    lateinit var pageDao: Crud<MarkdownPage>


    @Inject
    lateinit var templateCompiler: TemplateCompiler

    @Route(requestMethod = HttpMethod.GET, path = "/docs/:slug")
    fun documentationPage(request: Request, response: Response): String {

        val all = pageDao.all.filter { it.live!! }

        val types = MarkdownPageType.values().map { type ->
            mapOf("name" to type.pageTypeName, "value" to type.name)
        }

        var page = pageDao.searchInField("slug", request.params("slug")).firstOrNull()
                ?: notFound

        if (!page.live!!) {
            page = notFound
        }

        return templateCompiler.compile("templates/documentation.peb", mapOf("types" to types, "allPages" to all, "currentPage" to page))
    }

    @Route(requestMethod = HttpMethod.GET, path = "/docs/")
    fun documentationIntroRedir(request: Request, response: Response) {
        response.redirect("/docs")
    }

    @Route(requestMethod = HttpMethod.GET, path = "/docs")
    fun documentationIntro(request: Request, response: Response): String {

        val all = pageDao.all.filter { it.live!! }

        val types = MarkdownPageType.values().map { type ->
            mapOf("name" to type.pageTypeName, "value" to type.name)
        }

        var page = pageDao.searchInField("slug", request.params("intro")).firstOrNull()
                ?: intro

        if (!page.live!!) {
            page = intro
        }

        return templateCompiler.compile("templates/documentation.peb", mapOf("types" to types, "allPages" to all, "currentPage" to page))
    }

    //LEGACY

    @Route(path = "/guides", requestMethod = HttpMethod.GET)
    fun guidesPage(request: Request, response: Response) {
        response.redirect("/docs")
    }

    @Route(path = "/guides/:id", requestMethod = HttpMethod.GET)
    fun guidePage(request: Request, response: Response) {
        response.redirect("/docs/" + request.params("id"))
    }
}