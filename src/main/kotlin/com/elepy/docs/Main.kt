package com.elepy.docs

import com.elepy.Elepy
import com.elepy.admin.ElepyAdminPanel
import com.elepy.annotations.*
import com.elepy.annotations.Number
import com.elepy.concepts.ObjectEvaluator
import com.elepy.exceptions.ErrorMessageBuilder
import com.elepy.models.TextType
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.fakemongo.Fongo
import com.mongodb.DB
import org.apache.log4j.Level
import org.apache.log4j.Logger

fun main(args: Array<String>) {

    Logger.getRootLogger().level = Level.INFO
    org.apache.log4j.BasicConfigurator.configure()


    val fongo = Fongo("examples")
    val exampleDB = fongo.getDB("example1")

    val elepy = Elepy()
            .onPort(4242)
            .addExtension(Frontend())
            .addExtension(ElepyAdminPanel())
            .attachSingleton(DB::class.java, exampleDB)


    elepy.addModel(Section::class.java)
    elepy.start()
}

@RestModel(name = "Sections", slug = "/sections", defaultSortField = "order")
@Evaluators(value = [SectionEvaluator::class])
data class Section @JsonCreator constructor(
        @JsonProperty("id") @PrettyName("sectionId") @Identifier val id: String?,
        @JsonProperty("content") @PrettyName("Section Content") @Importance (-1) @Text(TextType.MARKDOWN) val content: String?,
        @JsonProperty("cssId") @PrettyName("CSS ID") @Required @Uneditable @Unique @Text(TextType.TEXTFIELD) val cssId: String,
        @JsonProperty("order") @PrettyName("Order Nr.") @Number(minimum = 0f, maximum = 200f) val order: Int?,
        @JsonProperty("language") @PrettyName("Programming Language") val language: SectionType
)

enum class SectionType(val css: String) {
    JAVA("language-java"), KOTLIN("language-kotlin"), XML("language-xml");
}

class SectionEvaluator : ObjectEvaluator<Section> {
    override fun evaluate(section: Section, cls: Class<Section>) {
        if (section.cssId.trim().contains(" "))
            throw ErrorMessageBuilder.anElepyErrorMessage().withMessage("CSS ID's can't contain spaces").withStatus(400).build()
    }
}