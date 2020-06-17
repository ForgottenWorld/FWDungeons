package it.forgottenworld.dungeons.utils

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.state.MobSpawnData
import it.forgottenworld.dungeons.state.MobTracker
import it.forgottenworld.dungeons.state.MobType
import it.forgottenworld.dungeons.cui.StringConst
import it.forgottenworld.dungeons.cui.getString
import it.forgottenworld.dungeons.model.activearea.ActiveArea
import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.scheduler.BukkitRunnable

const val CODE_FILL_ACTIVE_AREA = "fill"
const val CODE_SPAWN_TO_BE_KILLED_COMMAND = "combatobjective"
const val CODE_WHEN_DONE = "whendone"
const val CODE_FINISH = "exit"
const val PREFIX_MYTHIC_MOB = "mm"
const val PREFIX_MYTHIC_MOB_LENGTH = 2
const val PREFIX_VANILLA_MOB = "v"
const val PREFIX_VANILLA_MOB_LENGTH = 1
const val PREFIX_ACTIVE_AREA = "aa"
const val PREFIX_ACTIVE_AREA_LENGTH = 2

data class TypeWrapper<T>(var value: T)

fun parseEffectCode(instance: DungeonInstance, lines: List<String>): () -> Unit {
    val parsedCodeLines =
            lines.map {
                parseCode(instance, it.split(" ").iterator())
            }
    return {
        parsedCodeLines.forEach { it() }
    }
}

private fun parseCode(instance: DungeonInstance, codeIterator: Iterator<String>): () -> Unit {
    while(codeIterator.hasNext()) {
        when (codeIterator.next()) {
            CODE_SPAWN_TO_BE_KILLED_COMMAND -> {
                val mobs = mutableSetOf<MobSpawnData>()
                val whenDone = parseSpawnToBeKilled(instance, codeIterator, mobs)

                return { MobTracker.attachNewObjectiveToInstance(
                            instance.id,
                            mobs,
                            whenDone) }
            }
            CODE_FILL_ACTIVE_AREA -> {
                val activeArea = instance.getActiveAreaById(
                        codeIterator.next().toInt())!!
                val material = Material.getMaterial(codeIterator.next(), false)!!
                return { activeArea.fillWithMaterial(material) }
            }
            CODE_FINISH ->
                return {
                    instance.party?.players?.forEach {
                        it.sendMessage("${getString(StringConst.CHAT_PREFIX)}${ChatColor.GREEN}You will exit the dungeon in 5 seconds...") }
                    object : BukkitRunnable() {
                        override fun run() {
                            instance.onInstanceFinish(true)
                    }}.runTaskLater(FWDungeonsPlugin.instance, 100)
                }
            CODE_WHEN_DONE ->
                throw Exception("ERROR: whendone used outside of spawntobekilled statement")
        }
    }
    return {}
}

private fun parseSpawnToBeKilled(
        instance: DungeonInstance,
        codeIterator: Iterator<String>,
        mobs: MutableSet<MobSpawnData>): () -> Unit {
    var currentActiveArea: ActiveArea? = null
    while(codeIterator.hasNext()) {
        val code = codeIterator.next()
        when {
            code.startsWith(PREFIX_MYTHIC_MOB) -> {
                mobs.add(MobSpawnData(currentActiveArea!!, code.drop(PREFIX_MYTHIC_MOB_LENGTH), MobType.MYTHIC))
            }
            code.startsWith(PREFIX_VANILLA_MOB) -> {
                mobs.add(MobSpawnData(currentActiveArea!!, code.drop(PREFIX_VANILLA_MOB_LENGTH), MobType.VANILLA))
            }
            code.startsWith(PREFIX_ACTIVE_AREA) -> {
                currentActiveArea = instance.getActiveAreaById(code.drop(PREFIX_ACTIVE_AREA_LENGTH).toInt())
            }
            code == CODE_WHEN_DONE -> {
                return parseCode(instance, codeIterator)
            }
        }
    }
    return {}
}