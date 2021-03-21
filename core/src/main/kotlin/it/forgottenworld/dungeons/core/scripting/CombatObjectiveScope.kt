package it.forgottenworld.dungeons.core.scripting

import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.game.objective.MobSpawnData

class CombatObjectiveScope(val dungeon: FinalDungeon) {

    private object Keywords {
        const val CODE_WHEN_DONE = "whenDone"
        const val CODE_MYTHIC_MOB = "mythicMob"
        const val CODE_VANILLA_MOB = "mob"
        const val CODE_SPAWN_AREA = "spawnArea"
    }

    inner class SpawnAreaScope(private var currentSpawnArea: Int) {

        fun parse(
            mobs: MutableList<MobSpawnData>,
            code: CharIterator
        ) {
            while (code.hasNext()) {
                val keyword = ScriptingUtils.findKeyword(
                    code,
                    Keywords.CODE_MYTHIC_MOB,
                    Keywords.CODE_VANILLA_MOB
                )
                when(keyword) {
                    Keywords.CODE_MYTHIC_MOB,
                    Keywords.CODE_VANILLA_MOB -> {
                        val args = ScriptingUtils.parseArguments(code)
                        ScriptingUtils.eatSemicolon(code)
                        if (args.size != 1 || args.size != 2) {
                            throw ScriptingException("Expected mob type and optional amount (default 1)")
                        }
                        if (!args[0].startsWith('"')) {
                            throw ScriptingException("Mob name should be between double quotes")
                        }
                        val mobType = args[0].trim('"')
                        val amount = if (args.size == 2) {
                            args[1].toIntOrNull() ?: throw ScriptingException("Invalid mob amount")
                        } else {
                            1
                        }
                        repeat(amount) {
                            mobs.add(
                                MobSpawnData(
                                    currentSpawnArea,
                                    mobType,
                                    keyword == Keywords.CODE_MYTHIC_MOB
                                )
                            )
                        }
                    }
                    else -> throw ScriptingException("Unexpected end of block")
                }
            }
        }
    }

    fun parse(
        code: CharIterator
    ): (DungeonInstance) -> Unit {
        val mobs = mutableListOf<MobSpawnData>()
        while (code.hasNext()) {
            when (ScriptingUtils.findKeyword(
                code,
                Keywords.CODE_WHEN_DONE,
                Keywords.CODE_SPAWN_AREA
            )) {
                Keywords.CODE_SPAWN_AREA -> {
                    val args = ScriptingUtils.parseArguments(code)
                    if (args.size != 1) {
                        throw ScriptingException("Expected Spawn Area ID or label")
                    }
                    val saId = if (args[0].startsWith('"')) {
                        val label = args[0].trim('"')
                        dungeon
                            .spawnAreas
                            .values
                            .find { it.label == label }
                            ?.id
                            ?: throw ScriptingException("Invalid spawn area label")
                    } else {
                        args[0].toIntOrNull() ?: throw ScriptingException("Invalid spawn area ID")
                    }
                    val block = ScriptingUtils.parseBlock(code)
                    SpawnAreaScope(saId).parse(mobs, block)
                }
                Keywords.CODE_WHEN_DONE -> {
                    val block = ScriptingUtils.parseBlock(code)
                    val whenDone = GenericScope(dungeon).parse(block)
                    return { it.attachNewObjective(mobs, whenDone) }
                }
                else -> throw ScriptingException("Unexpected end of block")
            }
        }
        throw ScriptingException("Unexpected end of block")
    }
}