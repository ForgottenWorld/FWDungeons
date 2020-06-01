package it.forgottenworld.dungeons.db

import it.forgottenworld.dungeons.pluginInstance
import org.bukkit.Bukkit.getLogger
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

import java.sql.Connection
import java.sql.Date
import java.sql.ResultSet

private class DBTask(
        private val conn: Connection,
        private val isQuery: Boolean,
        private val sql: String,
        private val parameters: Array<out Any>,
        private val callbackRes: ((ResultSet) -> Unit)?,
        private val callbackCount: ((Int) -> Unit)?) : BukkitRunnable() {

    override fun run() {
        try {
            conn.prepareStatement(sql)?.let { s ->
                parameters.forEachIndexed { i, it ->
                    when (it) {
                        is String -> s.setString(i, it)
                        is Int -> s.setInt(i, it)
                        is Double -> s.setDouble(i, it)
                        is Float -> s.setFloat(i, it)
                        is Date -> s.setDate(i, it)
                        is Boolean -> s.setBoolean(i, it)
                        else -> throw Exception("ERROR: DBTask.run() - Invalid parameter type")
                    }
                }

                if (isQuery) {
                    s.executeQuery()
                    callbackRes?.invoke(s.resultSet)
                } else {
                    s.executeUpdate()
                    callbackCount?.invoke(s.updateCount)
                }
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }
}

fun executeQuery(sql: String,
                 vararg parameters: Any,
                 callback: (ResultSet) -> Unit) {
    pluginInstance ?: return
    DBHandler.connect()?.let {
        DBTask(it,true, sql, parameters, callback, null).runTaskAsynchronously(pluginInstance!!)
    } ?: getLogger().warning("ERROR: Couldn't connect to DB.")
}

fun executeUpdate(sql: String,
                 vararg parameters: Any,
                 callback: (Int) -> Unit) {

    pluginInstance ?: return
    DBHandler.connect()?.let {
        DBTask(it,false, sql, parameters, null, callback).runTaskAsynchronously(pluginInstance!!)
    } ?: getLogger().warning("ERROR: Couldn't connect to DB.")
}

fun executeUpdate(sql: String,
                  vararg parameters: Any) {
    pluginInstance ?: return
    DBHandler.connect()?.let {
        DBTask(it,false, sql, parameters, null, { }).runTaskAsynchronously(pluginInstance!!)
    } ?: getLogger().warning("ERROR: Couldn't connect to DB.")
}