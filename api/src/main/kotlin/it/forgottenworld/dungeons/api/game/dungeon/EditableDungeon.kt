package it.forgottenworld.dungeons.api.game.dungeon

import it.forgottenworld.dungeons.api.game.chest.Chest
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import org.bukkit.event.player.PlayerInteractEvent

interface EditableDungeon : Dungeon {

    override var id: Int

    override var name: String

    override var description: String

    override var difficulty: Dungeon.Difficulty

    override var minPlayers: Int

    override var maxPlayers: Int

    override var box: Box?

    override var startingLocation: Vector3i?

    override var points: Int

    override val chests: MutableMap<Int, Chest>

    var finalInstanceLocations: MutableList<Vector3i>

    val hasTestOrigin: Boolean

    val dungeonBoxBuilder: Box.Builder

    val triggerBoxBuilder: Box.Builder

    val activeAreaBoxBuilder: Box.Builder

    val spawnAreaBoxBuilder: Box.Builder

    var testOrigin: Vector3i

    fun finalize(): FinalDungeon

    fun labelInteractiveRegion(type: InteractiveRegion.Type, label: String, id: Int = -1)

    fun unmakeInteractiveRegion(type: InteractiveRegion.Type, ieId: Int?): Int

    fun newInteractiveRegion(type: InteractiveRegion.Type, box: Box): Int

    fun setupTestBox()

    fun onDestroy(restoreFormer: Boolean = false)

    fun whatIsMissingForWriteout(): String

    fun toggleEditorHighlights()

    fun onPlayerInteract(event: PlayerInteractEvent)

    companion object {
        const val NEW_DUNGEON_TEMP_ID = -69420
    }
}