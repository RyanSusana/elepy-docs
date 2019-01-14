package com.elepy.docs

import com.elepy.Elepy
import com.elepy.admin.ElepyAdminPanel
import com.elepy.annotations.*
import com.elepy.annotations.Number
import com.elepy.concepts.ObjectEvaluator
import com.elepy.dao.Crud
import com.elepy.di.ElepyContext
import com.elepy.exceptions.ElepyException
import com.elepy.exceptions.ErrorMessageBuilder
import com.elepy.models.TextType
import com.elepy.plugins.gallery.ElepyGallery
import com.elepy.routes.SimpleCreate
import com.elepy.routes.SimpleUpdate
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonMappingException
import com.github.fakemongo.Fongo
import com.mongodb.DB
import com.mongodb.MongoClient
import com.mongodb.ServerAddress
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import spark.Request
import spark.Response
import kotlin.concurrent.thread

fun main(args: Array<String>) {

    Logger.getRootLogger().level = Level.ERROR
    org.apache.log4j.BasicConfigurator.configure()
    val databaseServer: String = System.getenv("DATABASE_SERVER") ?: "localhost"
    val databasePort: String = System.getenv("DATABASE_PORT") ?: "27017"


    val mongoClient = MongoClient(ServerAddress(databaseServer, databasePort.toInt()))

    val elepyDB = if (System.getenv("testing") == null) mongoClient.getDB("elepy-docs") else Fongo("test").getDB("test")

    val elepy = Elepy()
            .onPort(4242)
            .addExtension(Frontend())
            .addExtension(ElepyAdminPanel().addPlugin(ElepyGallery()))
            .attachSingleton(DB::class.java, elepyDB)


    elepy.addModel(Section::class.java)
    elepy.start()
}

@RestModel(name = "Sections", slug = "/sections", defaultSortField = "order")
@Evaluators(value = [SectionEvaluator::class])
@Update(handler = SectionUpdate::class)
@Create(handler = SectionCreate::class)
data class Section @JsonCreator constructor(
        @JsonProperty("id") @PrettyName("Section ID") @Identifier val id: String?,
        @JsonProperty("title") @PrettyName("Title") val title: String?,
        @JsonProperty("cssClasses") @PrettyName("Extra CSS Classes") val cssClasses: String?,
        @JsonProperty("content") @PrettyName("Section Content") @Required @Importance(-1) @Text(TextType.MARKDOWN) val content: String,
        @JsonProperty("cssId") @PrettyName("CSS ID") @Required @Uneditable @Unique @Text(TextType.TEXTFIELD) val cssId: String,
        @JsonProperty("order") @PrettyName("Order Nr.") @Number(minimum = 0f, maximum = 200f) val order: Int?,
        @JsonProperty("language") @PrettyName("Programming Language") val language: SectionType,
        @JsonProperty("visibility") @PrettyName("Section Visibility") val visibility: SectionVisibility,
        @com.elepy.annotations.Boolean(trueValue = "Show link in Navigation Bar", falseValue = "Don't show link in Navigation Bar") @JsonProperty("showLink") @PrettyName("Show Link") val showLink: Boolean
)


enum class SectionType(val css: String) {
    JAVA("language-java"), KOTLIN("language-kotlin"), XML("language-xml");
}

enum class SectionVisibility(val showOnSite: Boolean, val showOnGitHub: Boolean) {
    SHOW_EVERYWHERE(true, true), SHOW_ON_GITHUB(false, true), SHOW_ON_SITE(true, false)
}

class SectionCreate : SimpleCreate<Section>() {

    @Throws(Exception::class)
    override fun handleCreate(request: Request, response: Response, dao: Crud<Section>, elepy: ElepyContext, objectEvaluators: List<ObjectEvaluator<Section>>, clazz: Class<Section>) {

        try {

            val body = request.body()

            val objectMapper = elepy.objectMapper
            val item = objectMapper.readValue<Section>(body, dao.getType())

            beforeCreate(item, dao, elepy)

            super.handleCreate(request, response, dao, elepy, objectEvaluators, clazz)

            afterCreate(item, dao, elepy)
            response.status(200)
            response.body("OK")

        } catch (e: JsonMappingException) {
            e.printStackTrace()
            throw ElepyException("MultipleCreate not supported with SimpleCreate")
        }

    }
    override fun beforeCreate(objectForCreation: Section?, crud: Crud<Section>?, elepy: ElepyContext?) {
        //Do nothing
    }

    override fun afterCreate(createdObject: Section, crud: Crud<Section>?, elepy: ElepyContext?) {
        thread {
            println(createdObject.cssId)
            updateGithub("${createdObject.cssId}.md", createdObject.content, createdObject.visibility)
        }
    }
}

class SectionUpdate : SimpleUpdate<Section>() {
    override fun beforeUpdate(beforeVersion: Section?, crud: Crud<Section>?, elepy: ElepyContext?) {
        //Do nothing
    }

    override fun afterUpdate(beforeVersion: Section, updatedVersion: Section, crud: Crud<Section>?, elepy: ElepyContext?) {

        thread {
            updateGithub("${updatedVersion.cssId}.md", updatedVersion.content, updatedVersion.visibility)
        }

    }
}


class SectionEvaluator : ObjectEvaluator<Section> {
    override fun evaluate(section: Section, cls: Class<Section>) {
        if (section.cssId.trim().contains(" "))
            throw ErrorMessageBuilder.anElepyErrorMessage().withMessage("CSS ID's can't contain spaces").withStatus(400).build()
    }
}


fun updateGithub(name: String, content: String, visibility: SectionVisibility) {

    val gitHub: GitHub = GitHub.connectUsingPassword(System.getenv("GITHUB_USERNAME"), System.getenv("GITHUB_PASSWORD"))

    val directoryContent = gitHub.elepy().getDirectoryContent("docs")

    if (visibility.showOnGitHub) {
        if (directoryContent.stream().noneMatch { ghcontent -> ghcontent.name == name }) {
            try{
                gitHub.elepy()
                        .createContent()
                        .message("AUTOMATIC DOCUMENTATION UPDATE: $name")
                        .content(content)
                        .path("docs/$name")
                        .commit()
            }catch (e:Exception){
                //Bug with the Library. It does an update instead of a create.
                e.printStackTrace()
            }

        } else {
            gitHub.elepy()
                    .getDirectoryContent("docs")
                    .stream()
                    .filter { content ->
                        name == content.name
                    }
                    .findAny()
                    .ifPresent { foundContent ->
                        gitHub.elepy()
                                .createContent()
                                .message("AUTOMATIC DOCUMENTATION UPDATE: $name")
                                .content(content).path("docs/$name")
                                .sha(foundContent.sha)
                                .commit()
                    }
        }
    } else {
        if (directoryContent.stream().anyMatch { ghcontent -> ghcontent.name == name }) {
            gitHub.elepy().getFileContent("docs/$name").delete("REMOVED DOCUMENTATION: $name")
        }
    }
}

fun GitHub.elepy(): GHRepository {
    return this.getRepository(if (System.getenv("testing") == null) "RyanSusana/elepy" else "RyanSusana/elepy")
}