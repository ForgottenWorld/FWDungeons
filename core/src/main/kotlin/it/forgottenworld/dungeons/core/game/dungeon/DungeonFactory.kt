package it.forgottenworld.dungeons.core.game.dungeon

import com.google.inject.assistedinject.Assisted
import it.forgottenworld.dungeons.api.game.chest.Chest
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.api.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.game.interactiveregion.SpawnArea
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import org.bukkit.entity.Player

interface DungeonFactory {
    
    fun createEditable(
        @Assisted("editor") editor: Player,
        @Assisted("id") id: Int = EditableDungeon.NEW_DUNGEON_TEMP_ID,
        @Assisted("name") name: String = "NEW DUNGEON",
        @Assisted("description") description: String = "",
        difficulty: Dungeon.Difficulty = Dungeon.Difficulty.MEDIUM,
        @Assisted("minPlayers") minPlayers: Int = 1,
        @Assisted("maxPlayers") maxPlayers: Int = 2,
        box: Box? = null,
        startingLocation: Vector3i? = null,
        @Assisted("points") points: Int = 0,
        finalInstanceLocations: MutableList<Vector3i> = mutableListOf(),
        triggers: Map<Int, Trigger> = mutableMapOf(),
        activeAreas: Map<Int, ActiveArea> = mutableMapOf(),
        spawnAreas: Map<Int, SpawnArea> = mutableMapOf(),
        chests: MutableMap<Int, Chest> = mutableMapOf()
    ): EditableDungeon
    
    fun createEditable(
        @Assisted("editor") editor: Player,
        dungeon: Dungeon
    ): EditableDungeon
    
    fun createFinal(
        @Assisted("id") id: Int,
        @Assisted("name") name: String,
        @Assisted("description") description: String,
        difficulty: Dungeon.Difficulty,
        @Assisted("points") points: Int,
        @Assisted("minPlayers") minPlayers: Int,
        @Assisted("maxPlayers") maxPlayers: Int,
        box: Box,
        startingLocation: Vector3i,
        triggers: Map<Int, Trigger>,
        activeAreas: Map<Int, ActiveArea>,
        spawnAreas: Map<Int, SpawnArea>,
        chests: Map<Int, Chest>,
        @Assisted("unlockableSeriesId") unlockableSeriesId: Int? = null,
        @Assisted("unlockableId") unlockableId: Int? = null
    ): FinalDungeon

    fun createFinal(
        dungeon: Dungeon
    ): FinalDungeon
}