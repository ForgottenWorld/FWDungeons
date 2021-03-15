package it.forgottenworld.dungeons.core.scripting

import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.game.objective.MobSpawnData
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material

@Singleton
class CodeParser {

    private object Consts {
        const val CODE_FILL_ACTIVE_AREA = "fill"
        const val CODE_COMBAT_OBJECTIVE = "combatObjective"
        const val CODE_WHEN_DONE = "whenDone"
        const val CODE_FINISH = "exitDungeon"
        const val CODE_BREAK = "break"
        const val PREFIX_MYTHIC_MOB = "MM"
        const val PREFIX_VANILLA_MOB = "VNL"
        const val PREFIX_ACTIVE_AREA = "AA"
    }

    fun cleanupCode(rawCode: String) = rawCode
        .replace("\n", "")
        .split(";")
        .map { it.trim() }
        .flatMap { it.split(" ") }
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    private fun parseCombatObjective(
        codeIterator: Iterator<String>
    ): (DungeonInstance) -> Unit {
        var currentActiveArea: Int? = null
        val mobs = mutableListOf<MobSpawnData>()
        while (codeIterator.hasNext()) {
            val code = codeIterator.next()
            when {
                code.startsWith(Consts.PREFIX_MYTHIC_MOB) -> {
                    if (currentActiveArea == null) throw ScriptingException("Target active area not yet set")
                    mobs.add(
                        MobSpawnData(
                            currentActiveArea,
                            code.removePrefix(Consts.PREFIX_MYTHIC_MOB),
                            true
                        )
                    )
                }
                code.startsWith(Consts.PREFIX_VANILLA_MOB) -> {
                    if (currentActiveArea == null) throw ScriptingException("Target active area not yet set")
                    mobs.add(
                        MobSpawnData(
                            currentActiveArea,
                            code.removePrefix(Consts.PREFIX_VANILLA_MOB),
                            false
                        )
                    )
                }
                code.startsWith(Consts.PREFIX_ACTIVE_AREA) -> {
                    currentActiveArea = code.removePrefix(Consts.PREFIX_ACTIVE_AREA).toInt()
                }
                code == Consts.CODE_WHEN_DONE -> {
                    val whenDone = parseTokens(codeIterator)
                    return { it.attachNewObjective(mobs, whenDone) }
                }
                else -> code.toIntOrNull()?.let { for (i in 1 until it) mobs.add(mobs.last().copy()) }
            }
        }
        return {}
    }

    private fun parseTokens(codeIterator: Iterator<String>): (DungeonInstance) -> Unit {
        val parsed = mutableListOf<(DungeonInstance) -> Unit>()
        while (codeIterator.hasNext()) {
            when (val code = codeIterator.next()) {
                Consts.CODE_COMBAT_OBJECTIVE -> {
                    val combatObjective = parseCombatObjective(codeIterator)
                    parsed.add { combatObjective(it) }
                }
                Consts.CODE_FILL_ACTIVE_AREA -> {
                    val aaId = codeIterator.next().toInt()
                    val material = Material.getMaterial(codeIterator.next(), false)!!
                    parsed.add {
                        val aa = it.dungeon.activeAreas[aaId]
                            ?: throw ScriptingException("Active area with id $aaId not found")
                        aa.fillWithMaterial(material, it)
                    }
                }
                Consts.CODE_FINISH -> parsed.add {
                    it.onFinishTriggered()
                }
                Consts.CODE_BREAK -> return { for (f in parsed) f(it) }
                Consts.CODE_WHEN_DONE -> {
                    throw ScriptingException("whenDone used outside of combatObjective statement")
                }
                else -> throw ScriptingException("Unrecognized code $code")
            }
        }
        return { for (f in parsed) f(it) }
    }

    fun beautify(raw: List<String>) = raw.joinToString(";\n") {
        it.replace(
            Consts.CODE_FILL_ACTIVE_AREA,
            "${ChatColor.of("#bfff00")}${Consts.CODE_FILL_ACTIVE_AREA}${ChatColor.WHITE}"
        ).replace(
            Consts.CODE_COMBAT_OBJECTIVE,
            "${ChatColor.AQUA}${Consts.CODE_COMBAT_OBJECTIVE}${ChatColor.WHITE}"
        ).replace(
            Consts.CODE_WHEN_DONE,
            "${ChatColor.LIGHT_PURPLE}${Consts.CODE_WHEN_DONE}${ChatColor.WHITE}"
        ).replace(
            Consts.CODE_FINISH,
            "${ChatColor.RED}${Consts.CODE_FINISH}${ChatColor.WHITE}"
        ).replace(
            Consts.PREFIX_ACTIVE_AREA,
            "${ChatColor.GREEN}${Consts.PREFIX_ACTIVE_AREA}${ChatColor.WHITE}"
        ).replace(
            Consts.PREFIX_MYTHIC_MOB,
            "${ChatColor.of("#ffa500")}${Consts.PREFIX_MYTHIC_MOB}${ChatColor.WHITE}"
        ).replace(
            Consts.PREFIX_VANILLA_MOB,
            "${ChatColor.GRAY}${Consts.PREFIX_VANILLA_MOB}${ChatColor.WHITE}"
        ).trim()
    }

    fun parseScript(lines: List<String>) = parseTokens(lines.iterator())
}