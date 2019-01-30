package com.elepy.docs.routes

import com.elepy.annotations.Inject
import com.elepy.annotations.Route
import com.elepy.docs.TemplateCompiler
import spark.Request
import spark.Response
import spark.route.HttpMethod

class MainRoutes {
    @Inject
    lateinit var templateCompiler: TemplateCompiler


    @Route(requestMethod = HttpMethod.get, path = "/")
    fun home(request: Request, response: Response): String {
        return templateCompiler.compile("templates/index.peb")
    }
}