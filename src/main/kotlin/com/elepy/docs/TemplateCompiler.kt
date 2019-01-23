package com.elepy.docs

import com.elepy.di.ElepyContext
import com.mitchellbosecke.pebble.PebbleEngine
import java.io.StringWriter

abstract class TemplateCompiler {
    private val engine: PebbleEngine = PebbleEngine.Builder().build()

    fun compile(templ: String, context: Map<String, Any>): String {
        val compiledTemplate = engine.getTemplate(templ)

        val writer = StringWriter()
        compiledTemplate.evaluate(writer, context)
        return writer.toString()
    }

    fun baseContext(elepyContext: ElepyContext): MutableMap<String, Any> {
        val sectionCrud = elepyContext.getCrudFor(Section::class.java)
        return mutableMapOf("sections" to sectionCrud.all)
    }
}