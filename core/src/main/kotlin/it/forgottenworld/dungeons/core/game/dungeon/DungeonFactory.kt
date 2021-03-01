package it.forgottenworld.dungeons.core.game.dungeon

import it.forgottenworld.dungeons.api.game.chest.Chest
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import org.bukkit.entity.Player

interface DungeonFactory {
    
    fun createEditable(
        editor: Player,
        id: Int = EditableDungeon.NEW_DUNGEON_TEMP_ID,
        name: String = "NEW DUNGEON",
        description: String = "",
        difficulty: Dungeon.Difficulty = Dungeon.Difficulty.MEDIUM,
        minPlayers: Int = 1,
        maxPlayers: Int = 2,
        box: Box? = null,
        startingLocation: Vector3i? = null,
        points: Int = 0,
        finalInstanceLocations: MutableList<Vector3i> = mutableListOf(),
        triggers: Map<Int, Trigger> = mutableMapOf(),
        activeAreas: Map<Int, ActiveArea> = mutableMapOf(),
        chests: MutableMap<Int, Chest> = mutableMapOf(),
    ): EditableDungeon
    
    fun createEditable(
        editor: Player,
        dungeon: Dungeon
    ): EditableDungeon
    
    fun createFinal(
        id: Int,
        name: String,
        description: String,
        difficulty: Dungeon.Difficulty,
        points: Int,
        minPlayers: Int,
        maxPlayers: Int,
        box: Box,
        startingLocation: Vector3i,
        triggers: Map<Int, Trigger>,
        activeAreas: Map<Int, ActiveArea>,
        chests: Map<Int, Chest>
    ): FinalDungeon

    fun createFinal(
        dungeon: Dungeon
    ): FinalDungeon
}