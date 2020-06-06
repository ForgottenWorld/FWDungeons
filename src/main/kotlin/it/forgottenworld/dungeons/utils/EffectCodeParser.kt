package it.forgottenworld.dungeons.utils

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.controller.FWDungeonsController
import it.forgottenworld.dungeons.model.dungeon.Dungeon
import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import it.forgottenworld.dungeons.model.trigger.Trigger
import org.bukkit.Bukkit.*
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

const val CODE_SPAWN_MOB = "spawn"
const val CODE_FILL_ACTIVE_AREA = "fill"
const val CODE_EXECUTE_COMMAND = "exec"

fun parseEffectCode(instance: DungeonInstance, lines: List<String>) {
    lines.forEach { line ->
        val sl = line.split(" ")
        when (sl.first()) {
            CODE_SPAWN_MOB ->
                getWorld(ConfigManager.dungeonWorld)!!.spawnEntity(
                    instance.getActiveAreaById(sl[1].toInt())!!.box.randomLocationOnFloor(),
                    EntityType.valueOf(sl[2])
            ).apply {
                entityId
            }
            CODE_FILL_ACTIVE_AREA ->
                instance.getActiveAreaById(
                        sl[1].toInt())!!.fillWithMaterial(Material.getMaterial(sl[2], false)!!)
            CODE_EXECUTE_COMMAND ->
                dispatchCommand(getConsoleSender(), sl.drop(1).joinToString(" "))
        }
    }
}