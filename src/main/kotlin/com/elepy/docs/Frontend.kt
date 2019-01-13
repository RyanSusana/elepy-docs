package com.elepy.docs

import com.elepy.Elepy
import com.elepy.ElepyModule
import com.elepy.dao.jongo.MongoDao
import com.mitchellbosecke.pebble.PebbleEngine
import com.mongodb.DB
import spark.Service
import java.io.StringWriter


class Frontend : ElepyModule {


    override fun setup(http: Service, elepy: Elepy) {
        http.staticFiles.location("public")
    }

    override fun routes(http: Service, elepy: Elepy) {
        val sectionCrud = MongoDao<Section>(elepy.getSingleton(DB::class.java), "sections", Section::class.java)

        http.get("/") { req, res ->
            compile("templates/index.peb", mapOf("sections" to sectionCrud.all))
        }
    }
    companion object {
        private val engine: PebbleEngine = PebbleEngine.Builder().build()

        fun  compile(templ: String, context: Map<String, Any>): String {
            val compiledTemplate = engine.getTemplate(templ)

            val writer = StringWriter()
            compiledTemplate.evaluate(writer, context)
            return writer.toString()
        }
    }
}