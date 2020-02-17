package com.ifmo.hexxlet

import com.ifmo.hexxlet.repository.AccountRepository
import com.ifmo.hexxlet.repository.OperationRepository
import org.apache.ibatis.datasource.pooled.PooledDataSource
import org.apache.ibatis.jdbc.ScriptRunner
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import spark.Request
import spark.Response
import spark.Spark
import java.io.BufferedReader
import java.io.InputStreamReader
import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import javax.sql.DataSource

fun initDatabase(settings: Properties): Connection {
    val url = settings.getProperty("url")
    val connection = DriverManager.getConnection(url, settings.getProperty("user"), settings.getProperty("password"))
    val sr = ScriptRunner(connection)
    Class.forName("com.ifmo.hexxlet.MainKt").getResourceAsStream("/populate.sql").use {
        val reader = BufferedReader(InputStreamReader(it))
        sr.runScript(reader)
    }
    return connection
}

fun configureMyBatis(settings: Properties): SqlSessionFactory {
    val dataSource: DataSource = PooledDataSource(settings.getProperty("driver"), settings.getProperty("url2"),
        settings.getProperty("user"), settings.getProperty("password"))
    val environment = Environment("Development", JdbcTransactionFactory(), dataSource)
    val configuration = Configuration(environment)
    configuration.addMapper(AccountRepository::class.java)
    configuration.addMapper(OperationRepository::class.java)
    val builder = SqlSessionFactoryBuilder()
    return builder.build(configuration)
}

fun configureEmbeddedServer() {
    Spark.port(8080)
    Spark.exception(Exception::class.java) { exception: Exception, _: Request, _: Response -> exception.printStackTrace() }
}

fun getProperties(path: String): Properties {
    val properties = Properties()
    Class.forName("com.ifmo.hexxlet.MainKt").getResourceAsStream(path).use {
        val reader = BufferedReader(InputStreamReader(it))
        properties.load(reader)
    }
    return properties
}