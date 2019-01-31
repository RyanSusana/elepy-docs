package com.elepy.docs

import com.elepy.Elepy
import com.elepy.admin.ElepyAdminPanel
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
            .addExtension(ElepyAdminPanel().addPlugin(ElepyGallery()))
            .connectDB(elepyDB)
            .registerDependency(TemplateCompiler::class.java)
            .addRouting(MainRoutes::class.java)
            .addModels(Section::class.java, Guide::class.java, News::class.java)


    //SparkJava stuff
    elepy.http().staticFiles.location("/public")

    elepy.start()


}


