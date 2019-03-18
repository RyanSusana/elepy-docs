package com.elepy.docs

import com.elepy.admin.annotations.View
import com.elepy.annotations.*
import com.elepy.annotations.Number
import com.elepy.dao.SortOption
import com.elepy.docs.routes.GuidesRoutes
import com.elepy.docs.services.*
import com.elepy.docs.views.MarkdownPageView
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
@Update(handler = SectionUpdate::class)
@Create(handler = SectionCreate::class)
data class Section @JsonCreator constructor(
        @JsonProperty("id") @Importance(-20) @PrettyName("Section ID") @Identifier val id: String?,
        @JsonProperty("title") @PrettyName("Title") val title: String?,
        @JsonProperty("cssClasses") @Importance(-20) @PrettyName("Extra CSS Classes") val cssClasses: String?,
        @JsonProperty("content") @PrettyName("Section Content") @Required @Importance(-1) @Text(TextType.MARKDOWN) val content: String,
        @JsonProperty("cssId") @PrettyName("CSS ID") @Required @Uneditable @Unique @Text(TextType.TEXTFIELD) val cssId: String,
        @JsonProperty("order") @PrettyName("Order Nr.") @Number(minimum = 0f, maximum = 200f) val order: Int?,
        @JsonProperty("language") @Required @PrettyName("Programming Language") val language: SectionType,
        @JsonProperty("visibility") @Required @PrettyName("Section Visibility") val visibility: SectionVisibility,
        @TrueFalse(trueValue = "Show link in Navigation Bar", falseValue = "Don't show link in Navigation Bar") @JsonProperty("showOnSite") @PrettyName("Show Link") val showLink: Boolean
)

enum class SectionType(val css: String) {
    @JsonEnumDefaultValue
    @PrettyName("Java")
    JAVA("language-java"),

    @PrettyName("Kotlin")
    KOTLIN("language-kotlin"),

    XML("language-xml");
}

enum class SectionVisibility(val showOnSite: Boolean, val showOnGitHub: Boolean) {
    @PrettyName("Show Everywhere")
    SHOW_EVERYWHERE(true, true),

    @PrettyName("Show only on GitHub")
    SHOW_ON_GITHUB(false, true),

    @PrettyName("Show only on Elepy.com")
    @JsonEnumDefaultValue
    SHOW_ON_SITE(true, false)
}

//Guides
@RestModel(name = "Guides", slug = "/api/guides", defaultSortField = "order")
@ExtraRoutes(GuidesRoutes::class)
@IdProvider(SlugIdentityProvider::class)
data class Guide @JsonCreator constructor(
        @JsonProperty("id") @PrettyName("Section ID") @Identifier val id: String?,
        @JsonProperty("title") @PrettyName("Title") val title: String?,
        @JsonProperty("caption") @PrettyName("Caption Image") @Text(TextType.IMAGE_LINK) @Importance(-2) val image: String?,
        @JsonProperty("previewContent") @PrettyName("Guide Introduction") @Required @Importance(0) @Text(TextType.TEXTAREA, maximumLength = 300) val previewContent: String,
        @JsonProperty("content") @PrettyName("Guide Content") @Required @Importance(-3) @Text(TextType.MARKDOWN) val content: String,
        @JsonProperty("order") @PrettyName("Order Nr.") @Number(minimum = 0f, maximum = 200f) val order: Int?,
        @TrueFalse(trueValue = "Is live on site", falseValue = "Is not live on site") @JsonProperty("showOnSite") @PrettyName("Live") val showOnSite: Boolean
)

//News
@RestModel(name = "News", slug = "/api/news", defaultSortField = "date")
@IdProvider(SlugIdentityProvider::class)
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

@RestModel(name = "Pages", slug = "/api/pages", defaultSortField = "title", defaultSortDirection = SortOption.ASCENDING)
@View(MarkdownPageView::class)
@Find(findManyHandler = MarkdownPageFindMany::class)
@Update(handler = MarkdownPageUpdate::class)
@Create(handler = MarkdownPageCreate::class)
@Delete(handler = MarkdownPageDelete::class)
@ExtraRoutes(MarkdownPageRoutes::class)
data class MarkdownPage @JsonCreator constructor(
        @JsonProperty("id") val id: String?,
        @JsonProperty("title") @Searchable @Unique val title: String,
        @JsonProperty("slug") @Searchable @Unique val slug: String,
        @JsonProperty("type") @Searchable val type: MarkdownPageType,
        @JsonProperty("live") @TrueFalse(trueValue = "Live", falseValue = "Draft") val live: Boolean?,
        @JsonProperty("content") var content: String?
)

enum class MarkdownPageType(val pageTypeName: String, val directory: String) {
    DOCUMENTATION_PAGE("Documentation", "pages"), GUIDE("Guides", "guides"), NEWS("News", "news")
}