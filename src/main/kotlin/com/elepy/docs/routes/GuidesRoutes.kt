package com.elepy.docs.routes

import com.elepy.annotations.Inject
import com.elepy.annotations.Route
import com.elepy.dao.Crud
import com.elepy.docs.Guide
import com.elepy.docs.TemplateCompiler
import com.elepy.http.HttpMethod
import com.elepy.http.Request
import com.elepy.http.Response
import java.util.stream.Collectors

class GuidesRoutes {

    @Inject
    lateinit var templateCompiler: TemplateCompiler

    @Inject
    lateinit var guideDao: Crud<Guide>


    @Route(path = "/guides", requestMethod = HttpMethod.GET)
    fun guidesPage(request: Request, response: Response): String {
        return templateCompiler
                .compile("templates/guides.peb",
                        mapOf("guides" to
                                guideDao
                                        .all
                                        .stream()
                                        .filter { guide -> guide.showOnSite }
                                        .collect(Collectors.toList())
                        )
                )
    }

    @Route(path = "/guides/:id", requestMethod = HttpMethod.GET)
    fun guidePage(request: Request, response: Response) {
        val guide = guideDao.getById(request.params("id"))

        return if (guide.isPresent && guide.get().showOnSite) {
            response.result(templateCompiler.compile("templates/guide.peb", mapOf("guide" to guide.get())))
        } else {
            response.redirect("/")
        }
    }

}