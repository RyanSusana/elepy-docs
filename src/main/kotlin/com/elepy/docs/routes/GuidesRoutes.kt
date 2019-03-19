package com.elepy.docs.routes

import com.elepy.annotations.Inject
import com.elepy.annotations.Route
import com.elepy.dao.Crud
import com.elepy.docs.Guide
import com.elepy.docs.TemplateCompiler
import com.elepy.http.HttpMethod
import com.elepy.http.Request
import com.elepy.http.Response

class GuidesRoutes {

    @Inject
    lateinit var templateCompiler: TemplateCompiler

    @Inject
    lateinit var guideDao: Crud<Guide>




}