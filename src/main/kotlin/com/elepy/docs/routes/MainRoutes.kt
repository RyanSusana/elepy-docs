package com.elepy.docs.routes

import com.elepy.annotations.Inject
import com.elepy.annotations.Route
import com.elepy.dao.Crud
import com.elepy.docs.Guide
import com.elepy.docs.TemplateCompiler
import com.elepy.models.AccessLevel
import spark.Request
import spark.Response
import spark.route.HttpMethod
import java.util.stream.Collectors

class MainRoutes {
    @Inject
    lateinit var templateCompiler: TemplateCompiler

    @Inject
    lateinit var guideDao: Crud<Guide>


    @Route(requestMethod = HttpMethod.get, path = "/")
    fun home(request: Request, response: Response): String {
        return templateCompiler.compile("templates/index.peb")
    }

    @Route(path = "/sitemap.txt", requestMethod = HttpMethod.get, accessLevel = AccessLevel.PUBLIC)
    fun sitemap(request: Request, response: Response): String {
        response.type("text/plain")
        return templateCompiler
                .compile("templates/sitemap.peb",
                        mapOf("guides" to
                                guideDao
                                        .all
                                        .stream()
                                        .filter { guide -> guide.showOnSite }
                                        .collect(Collectors.toList())
                        )
                )
    }
}