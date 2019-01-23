package com.elepy.docs.routes

import com.elepy.annotations.Inject
import com.elepy.annotations.Route
import com.elepy.dao.jongo.MongoDao
import com.elepy.di.ElepyContext
import com.elepy.docs.Guide
import com.elepy.docs.TemplateCompiler
import spark.Request
import spark.Response
import spark.route.HttpMethod
import java.util.stream.Collectors

class GuidesRoutes : TemplateCompiler() {

    lateinit var elepy: ElepyContext

    @Inject
    lateinit var guideDao: MongoDao<Guide>

    @Route(path = "/guides", requestMethod = HttpMethod.get)
    fun guidesPage(request: Request, response: Response): String {
        return compile("templates/guides.peb", baseContext(elepy).plus(Pair("guides", guideDao.all.stream().filter { guide -> guide.showOnSite }.collect(Collectors.toList()))))
    }

    @Route(path = "/guides/:id", requestMethod = HttpMethod.get)
    fun guidePage(request: Request, response: Response): String {
        val guide = guideDao.getById(request.params("id"))

        return if (guide.isPresent && guide.get().showOnSite) {
            compile("templates/guide.peb", baseContext(elepy).plus(Pair("guide", guide.get())))
        } else {
            response.redirect("/")
            ""
        }
    }
}