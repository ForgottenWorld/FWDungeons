package it.forgottenworld.dungeons.db

import org.bukkit.Bukkit.getLogger
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


object DBHandler {


    private var connection: Connection? = null
    private lateinit var host: String
    private lateinit var database: String
    private lateinit var username: String
    private lateinit var password: String
    private var port: Int = -1

    @Throws(SQLException::class, ClassNotFoundException::class)
    fun connect(host: String, database: String, username: String, password: String, port: Int) : Connection? {
        this.host = host
        this.database = database
        this.username = username
        this.password = password
        this.port = port

        if (connection != null && connection?.isClosed == false) {
            return connection
        }
        synchronized(this) {
            if (connection != null && connection?.isClosed == false) {
                return connection as Connection
            }
            Class.forName("com.mysql.jdbc.Driver")
            connection =
                    DriverManager.getConnection(
                            "jdbc:mysql://${DBHandler.host}:${DBHandler.port}/${DBHandler.database}",
                            DBHandler.username,
                            DBHandler.password)
        }

        return connection
    }

    fun connect(): Connection? {
        if (!isConfigured()) {
            getLogger().warning("ERROR: DBHandler.Connect() needs to be called with arguments at least once.")
            return null
        }

        return connect(host, database, username, password, port)
    }
    
    private fun isConfigured() = port != -1
}