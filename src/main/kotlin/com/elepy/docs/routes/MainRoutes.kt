package com.elepy.docs.routes

import com.elepy.annotations.Inject
import com.elepy.annotations.Route
import com.elepy.dao.Crud
import com.elepy.docs.Guide
import com.elepy.docs.MarkdownPage
import com.elepy.docs.TemplateCompiler
import com.elepy.http.AccessLevel
import com.elepy.http.HttpMethod
import com.elepy.http.Request
import com.elepy.http.Response

class MainRoutes {
    @Inject
    lateinit var templateCompiler: TemplateCompiler

    @Inject
    lateinit var guideDao: Crud<Guide>
    @Inject
    lateinit var documentationCrud: Crud<MarkdownPage>


    @Route(requestMethod = HttpMethod.GET, path = "/")
    fun home(request: Request, response: Response): String {
        return templateCompiler.compile("templates/index.peb")
    }


    @Route(path = "/sitemap.txt", requestMethod = HttpMethod.GET, accessLevel = AccessLevel.PUBLIC)
    fun sitemap(request: Request, response: Response): String {
        response.type("text/plain")
        return templateCompiler
                .compile("templates/sitemap.peb",
                        mapOf("guides" to guideDao.all.filter { guide -> guide.showOnSite },
                                "docs" to documentationCrud.all.filter { it.live!! }
                        )
                )
    }
}