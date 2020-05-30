package it.forgottenworld.dungeons.db

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
                    }
                }
                s.executeQuery()
                if (isQuery)
                    callbackRes?.invoke(s.resultSet)
                else
                    callbackCount?.invoke(s.updateCount)
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }
}

fun executeQuery(context: Plugin,
                 conn: Connection,
                 sql: String,
                 vararg parameters: Any,
                 callback: (ResultSet) -> Unit) {
    DBTask(conn,true, sql, parameters, callback, null).runTaskAsynchronously(context)
}

fun executeUpdate(context: Plugin,
                 conn: Connection,
                 sql: String,
                 vararg parameters: Any,
                 callback: (Int) -> Unit) {
    DBTask(conn,false, sql, parameters, null, callback).runTaskAsynchronously(context)
}