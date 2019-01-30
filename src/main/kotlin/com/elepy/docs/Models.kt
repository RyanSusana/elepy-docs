package com.elepy.docs

import com.elepy.annotations.*
import com.elepy.annotations.Number
import com.elepy.docs.routes.GuidesRoutes
import com.elepy.docs.routes.MainRoutes
import com.elepy.docs.services.SectionCreate
import com.elepy.docs.services.SectionEvaluator
import com.elepy.docs.services.SectionUpdate
import com.elepy.id.SlugIdentityProvider
import com.elepy.models.TextType
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import com.fasterxml.jackson.annotation.JsonProperty
import java.text.SimpleDateFormat
import java.util.*

//Sections
@RestModel(name = "Sections", slug = "/sections", defaultSortField = "order")
@Evaluators(value = [SectionEvaluator::class])
@ExtraRoutes(MainRoutes::class)
@Update(handler = SectionUpdate::class)
@Create(handler = SectionCreate::class)
data class Section @JsonCreator constructor(
        @JsonProperty("id") @PrettyName("Section ID") @Identifier val id: String?,
        @JsonProperty("title") @PrettyName("Title") val title: String?,
        @JsonProperty("cssClasses") @PrettyName("Extra CSS Classes") val cssClasses: String?,
        @JsonProperty("content") @PrettyName("Section Content") @Required @Importance(-1) @Text(TextType.MARKDOWN) val content: String,
        @JsonProperty("cssId") @PrettyName("CSS ID") @Required @Uneditable @Unique @Text(TextType.TEXTFIELD) val cssId: String,
        @JsonProperty("order") @PrettyName("Order Nr.") @Number(minimum = 0f, maximum = 200f) val order: Int?,
        @JsonProperty("language") @Required @PrettyName("Programming Language") val language: SectionType,
        @JsonProperty("visibility") @Required @PrettyName("Section Visibility") val visibility: SectionVisibility,
        @com.elepy.annotations.Boolean(trueValue = "Show link in Navigation Bar", falseValue = "Don't show link in Navigation Bar") @JsonProperty("showOnSite") @PrettyName("Show Link") val showLink: Boolean
)

enum class SectionType(val css: String) {
    @JsonEnumDefaultValue
    JAVA("language-java"),
    KOTLIN("language-kotlin"), XML("language-xml");
}

enum class SectionVisibility(val showOnSite: Boolean, val showOnGitHub: Boolean) {
    SHOW_EVERYWHERE(true, true), SHOW_ON_GITHUB(false, true), @JsonEnumDefaultValue
    SHOW_ON_SITE(true, false)
}

//SEO friendly slugs
class EasyIdentityProvider : SlugIdentityProvider<Any>(0, 70, "title", "name")

//Guides
@RestModel(name = "Guides", slug = "/api/guides", defaultSortField = "order")
@ExtraRoutes(GuidesRoutes::class)
@IdProvider(EasyIdentityProvider::class)
data class Guide @JsonCreator constructor(
        @JsonProperty("id") @PrettyName("Section ID") @Identifier val id: String?,
        @JsonProperty("title") @PrettyName("Title") val title: String?,
        @JsonProperty("caption") @PrettyName("Caption Image") @Text(TextType.IMAGE_LINK)  @Importance(-2) val image: String?,
        @JsonProperty("previewContent") @PrettyName("Guide Introduction") @Required @Importance(0) @Text(TextType.TEXTAREA, maximumLength = 300) val previewContent: String,
        @JsonProperty("content") @PrettyName("Guide Content") @Required @Importance(-3) @Text(TextType.MARKDOWN) val content: String,
        @JsonProperty("order") @PrettyName("Order Nr.") @Number(minimum = 0f, maximum = 200f) val order: Int?,
        @com.elepy.annotations.Boolean(trueValue = "Is live on site", falseValue = "Is not live on site") @JsonProperty("showOnSite") @PrettyName("Live") val showOnSite: Boolean
)

//News
@RestModel(name = "News", slug = "/api/news", defaultSortField = "date")
@IdProvider(EasyIdentityProvider::class)
data class News @JsonCreator constructor(
        @JsonProperty("id") @PrettyName("Section ID") @Identifier val id: String?,
        @JsonProperty("title") @PrettyName("Title") val title: String?,
        @JsonProperty("caption") @PrettyName("Caption Image") @Text(TextType.IMAGE_LINK) @Importance(-5) val image: String?,
        @JsonProperty("content") @PrettyName("Section Content") @Required @Importance(-2) @Text(TextType.MARKDOWN) val content: String,
        @JsonProperty("previewContent") @PrettyName("Section Introduction") @Required @Importance(0) @Text(TextType.TEXTAREA, maximumLength = 120) val previewContent: String,
        @JsonProperty("date") @PrettyName("Date") @Importance(-10) val date: Date
) {

    @Generated
    @PrettyName("Formatted Date")
    @JsonProperty("prettyDate")
    fun prettyDate(): String {
        return SimpleDateFormat("yyyy/MM/dd").format(date)
    }
}