package com.elepy.docs.routes

import com.elepy.annotations.Route
import com.elepy.di.ElepyContext
import com.elepy.docs.TemplateCompiler
import spark.Request
import spark.Response
import spark.route.HttpMethod

class MainRoutes : TemplateCompiler() {

    lateinit var elepy : ElepyContext

    @Route(requestMethod = HttpMethod.get, path = "/")
    fun home(request: Request, response: Response): String {
        return compile("templates/index.peb", baseContext(elepy))
    }
}