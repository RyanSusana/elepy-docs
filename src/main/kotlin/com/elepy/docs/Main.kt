package com.elepy.docs

import com.elepy.Elepy
import com.elepy.admin.ElepyAdminPanel
import com.elepy.admin.models.User
import com.elepy.admin.models.UserType
import com.elepy.docs.routes.DocumentationRoutes
import com.elepy.docs.routes.MainRoutes
import com.elepy.plugins.gallery.ElepyGallery
import com.github.fakemongo.Fongo
import com.mongodb.MongoClient
import com.mongodb.ServerAddress
import org.apache.log4j.Level
import org.apache.log4j.Logger

fun main(args: Array<String>) {

    Logger.getRootLogger().level = if (System.getenv("testing") == null) Level.ERROR else Level.INFO
    org.apache.log4j.BasicConfigurator.configure()
    val databaseServer: String = System.getenv("DATABASE_SERVER") ?: "localhost"
    val databasePort: String = System.getenv("DATABASE_PORT") ?: "27017"


    val mongoClient = MongoClient(ServerAddress(databaseServer, databasePort.toInt()))

    val elepyDB = if (System.getenv("testing") == null) mongoClient.getDB("elepy-docs") else Fongo("test").getDB("test")

    val elepy = Elepy()
            .onPort(4242)
            .addExtension(ElepyAdminPanel()
                    .attachSrcDirectory(TemplateCompiler::class.java.classLoader, "public")
                    .addPlugin(ElepyGallery())
                    .onNoUserFound { context, crud ->

                        val user = User()

                        user.username = "admin"
                        user.password = "admin"
                        user.userType = UserType.SUPER_ADMIN

                        crud.create(user.hashWord())
                    }
            )
            .connectDB(elepyDB)
            .registerDependency(TemplateCompiler::class.java)
            .addRouting(MainRoutes::class.java)
            .addRouting(DocumentationRoutes::class.java)
            .addModelPackage("com.elepy.docs")




    elepy.start()

    val markdownPage = MarkdownPage("id", "Title", "slug", MarkdownPageType.DOCUMENTATION_PAGE,true, "## her")
    elepy.getCrudFor(MarkdownPage::class.java).create(markdownPage)

}


