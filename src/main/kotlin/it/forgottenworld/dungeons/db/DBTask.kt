package it.forgottenworld.dungeons.db

import it.forgottenworld.dungeons.FWDungeonsPlugin
import org.bukkit.Bukkit.getLogger
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
                        is String -> s.setString(i + 1, it)
                        is Int -> s.setInt(i + 1, it)
                        is Double -> s.setDouble(i + 1, it)
                        is Float -> s.setFloat(i + 1, it)
                        is Date -> s.setDate(i + 1, it)
                        is Boolean -> s.setBoolean(i + 1, it)
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
    DBHandler.connect()?.let {
        DBTask(it,true, sql, parameters, callback, null).runTask(FWDungeonsPlugin.instance)
    } ?: getLogger().warning("ERROR: Couldn't connect to DB.")
}

fun executeUpdate(sql: String,
                 vararg parameters: Any,
                 callback: (Int) -> Unit) {
    DBHandler.connect()?.let {
        DBTask(it,false, sql, parameters, null, callback).runTask(FWDungeonsPlugin.instance)
    } ?: getLogger().warning("ERROR: Couldn't connect to DB.")
}

fun executeUpdate(sql: String,
                  vararg parameters: Any) {
    DBHandler.connect()?.let {
        DBTask(it,false, sql, parameters, null, { }).runTask(FWDungeonsPlugin.instance)
    } ?: getLogger().warning("ERROR: Couldn't connect to DB.")
}

fun executeQueryAsync(sql: String,
                 vararg parameters: Any,
                 callback: (ResultSet) -> Unit) {
    DBHandler.connect()?.let {
        DBTask(it,true, sql, parameters, callback, null).runTaskAsynchronously(FWDungeonsPlugin.instance)
    } ?: getLogger().warning("ERROR: Couldn't connect to DB.")
}

fun executeUpdateAsync(sql: String,
                  vararg parameters: Any,
                  callback: (Int) -> Unit) {
    DBHandler.connect()?.let {
        DBTask(it,false, sql, parameters, null, callback).runTaskAsynchronously(FWDungeonsPlugin.instance)
    } ?: getLogger().warning("ERROR: Couldn't connect to DB.")
}

fun executeUpdateAsync(sql: String,
                  vararg parameters: Any) {
    DBHandler.connect()?.let {
        DBTask(it,false, sql, parameters, null, { }).runTaskAsynchronously(FWDungeonsPlugin.instance)
    } ?: getLogger().warning("ERROR: Couldn't connect to DB.")
}