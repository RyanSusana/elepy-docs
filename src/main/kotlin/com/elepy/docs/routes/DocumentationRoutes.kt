package com.elepy.docs.routes

import com.elepy.annotations.Inject
import com.elepy.annotations.Route
import com.elepy.dao.Crud
import com.elepy.docs.MarkdownPage
import com.elepy.http.AccessLevel
import com.elepy.http.HttpMethod
import com.elepy.http.Request
import com.elepy.http.Response


class DocumentationRoutes {

    @Inject
    lateinit var documentationCrud: Crud<MarkdownPage>


    @Route(accessLevel = AccessLevel.PUBLIC, path = "/api/pages-without-markdown", requestMethod = HttpMethod.GET)
    fun getPages(request: Request, response: Response) {

        documentationCrud.all


    }
}