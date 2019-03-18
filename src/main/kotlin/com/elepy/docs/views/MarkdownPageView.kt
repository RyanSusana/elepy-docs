package com.elepy.docs.views

import com.elepy.admin.concepts.RestModelView
import com.elepy.annotations.Inject
import com.elepy.describers.ModelDescription
import com.elepy.docs.TemplateCompiler

class MarkdownPageView : RestModelView {

    @Inject
    private lateinit var templateCompiler: TemplateCompiler


    override fun renderView(descriptor: ModelDescription<*>): String {
        return templateCompiler.compile("custom-models/markdown-page/markdown-edit.peb")
    }
}
