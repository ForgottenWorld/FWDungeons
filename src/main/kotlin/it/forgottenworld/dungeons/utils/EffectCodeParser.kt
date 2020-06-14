package it.forgottenworld.dungeons.utils

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.controller.MobTracker
import it.forgottenworld.dungeons.cui.StringConst
import it.forgottenworld.dungeons.cui.getString
import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.scheduler.BukkitRunnable

const val CODE_FILL_ACTIVE_AREA = "fill"
const val CODE_SPAWN_TO_BE_KILLED_COMMAND = "spawntobekilled"
const val CODE_WHEN_DONE = "whendone"
const val CODE_FINISH = "exit"
const val PREFIX_MYTHIC_MOB = "mm"
const val PREFIX_MYTHIC_MOB_LENGTH = 2
const val PREFIX_VANILLA_MOB = "v"
const val PREFIX_VANILLA_MOB_LENGTH = 1
const val PREFIX_ACTIVE_AREA = "aa"
const val PREFIX_ACTIVE_AREA_LENGTH = 2

private data class TypeWrapper<T>(var value: T)

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
        when (val code = codeIterator.next()) {
            CODE_SPAWN_TO_BE_KILLED_COMMAND -> {
                val mobs = mutableListOf<String>()
                val mythicMobs = mutableListOf<String>()
                val activeArea = TypeWrapper(-1)
                val whenDone = parseSpawnToBeKilled(instance, codeIterator, mobs, mythicMobs, activeArea)

                return { MobTracker.attachNewObjectiveToInstance(
                            instance.id,
                            mobs,
                            mythicMobs,
                            instance.getActiveAreaById(activeArea.value)!!,
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
        mobs: MutableList<String>,
        mythicMobs: MutableList<String>,
        activeArea: TypeWrapper<Int>): () -> Unit {
    while(codeIterator.hasNext()) {
        val code = codeIterator.next()
        when {
            code.startsWith(PREFIX_MYTHIC_MOB) -> {
                mythicMobs.add(code.drop(PREFIX_MYTHIC_MOB_LENGTH))
            }
            code.startsWith(PREFIX_VANILLA_MOB) -> {
                mobs.add(code.drop(PREFIX_VANILLA_MOB_LENGTH))
            }
            code.startsWith(PREFIX_ACTIVE_AREA) -> {
                activeArea.value = code.drop(PREFIX_ACTIVE_AREA_LENGTH).toInt()
            }
            code == CODE_WHEN_DONE -> {
                return parseCode(instance, codeIterator)
            }
        }
    }
    return {}
}