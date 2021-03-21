package it.forgottenworld.dungeons.core.scripting

import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import org.bukkit.Material

class GenericScope(val dungeon: FinalDungeon) {

    private object Keywords {
        const val CODE_FILL_ACTIVE_AREA = "fill"
        const val CODE_COMBAT_OBJECTIVE = "combatObjective"
        const val CODE_BREAK = "break"
        const val CODE_FINISH = "exitDungeon"
    }

    fun parse(code: CharIterator): (DungeonInstance) -> Unit {
        val parsed = mutableListOf<(DungeonInstance) -> Unit>()
        while (code.hasNext()) {
            when (ScriptingUtils.findKeyword(
                code,
                Keywords.CODE_FILL_ACTIVE_AREA,
                Keywords.CODE_COMBAT_OBJECTIVE,
                Keywords.CODE_BREAK,
                Keywords.CODE_FINISH
            )) {
                Keywords.CODE_COMBAT_OBJECTIVE -> {
                    val block = ScriptingUtils.parseBlock(code)
                    val combatObjective = CombatObjectiveScope(dungeon).parse(block)
                    parsed.add { combatObjective(it) }
                }
                Keywords.CODE_FILL_ACTIVE_AREA -> {
                    val args = ScriptingUtils.parseArguments(code)
                    ScriptingUtils.eatSemicolon(code)
                    if (args.size != 1 || args.size != 2) {
                        throw ScriptingException("Expected active area ID (or label) and optional material")
                    }
                    val aaId = if (args[0].startsWith('"')) {
                        val label = args[0].trim('"')
                        dungeon
                            .activeAreas
                            .values
                            .find { it.label == label }
                            ?.id
                            ?: throw ScriptingException("Invalid active area label")
                    } else {
                        args[0].toIntOrNull() ?: throw ScriptingException("Invalid active area ID")
                    }
                    val material = args.getOrNull(1)?.let {
                        if (!it.startsWith('"')) {
                            throw ScriptingException("Material name should be between double quotes")
                        }
                        Material.getMaterial(it.trim('"'), false)
                            ?: throw ScriptingException("Material not recognized: $it")
                    } ?: Material.AIR
                    parsed.add {
                        val aa = it.dungeon.activeAreas[aaId]
                            ?: throw ScriptingException("Active area with id $aaId not found")
                        aa.fillWithMaterial(material, it)
                    }
                }
                Keywords.CODE_FINISH -> {
                    parsed.add { it.onFinishTriggered() }
                    ScriptingUtils.eatSemicolon(code)
                }
                Keywords.CODE_BREAK -> {
                    return { for (f in parsed) f(it) }
                }
                else -> throw ScriptingException("Unrecognized code $code")
            }
        }
        return { for (f in parsed) f(it) }
    }
}