package com.elepy.docs

import com.elepy.annotations.Inject
import com.elepy.dao.Crud
import com.mitchellbosecke.pebble.PebbleEngine
import java.io.StringWriter

class TemplateCompiler {

    private val engine: PebbleEngine = PebbleEngine.Builder().build()

    @Inject
    private lateinit var sectionCrud: Crud<Section>

    fun compile(templ: String): String {
        return compile(templ, emptyMap())
    }

    fun compile(templ: String, context: Map<String, Any>): String {
        val compiledTemplate = engine.getTemplate(templ)
        val writer = StringWriter()
        val baseContext = baseContext()

        baseContext.putAll(context)
        compiledTemplate.evaluate(writer, baseContext)

        return writer.toString()
    }

    fun baseContext(): MutableMap<String, Any> {
        return mutableMapOf("sections" to sectionCrud.all)
    }
}