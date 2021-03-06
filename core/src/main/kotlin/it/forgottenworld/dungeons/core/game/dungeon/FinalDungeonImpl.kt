package it.forgottenworld.dungeons.core.game.dungeon

import com.google.inject.assistedinject.Assisted
import com.google.inject.assistedinject.AssistedInject
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.api.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.api.game.dungeon.subelement.chest.Chest
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.SpawnArea
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.core.game.detection.TriggerGridFactory
import it.forgottenworld.dungeons.core.game.dungeon.instance.DungeonInstanceFactory
import it.forgottenworld.dungeons.core.scripting.ScriptParser
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player
import javax.annotation.Nullable

class FinalDungeonImpl @AssistedInject constructor(
    @Assisted("id") override val id: Int,
    @Assisted("name") override val name: String,
    @Assisted("description") override val description: String,
    @Assisted override val difficulty: Dungeon.Difficulty,
    @Assisted("points") override val points: Int,
    @Assisted("minPlayers") override val minPlayers: Int,
    @Assisted("maxPlayers") override val maxPlayers: Int,
    @Assisted override val box: Box,
    @Assisted override val startingLocation: Vector3i,
    @Assisted override val triggers: Map<Int, Trigger>,
    @Assisted override val activeAreas: Map<Int, ActiveArea>,
    @Assisted override val spawnAreas: Map<Int, SpawnArea>,
    @Assisted override val chests: Map<Int, Chest>,
    @Nullable @Assisted("unlockableSeriesId") override val unlockableSeriesId: Int? = null,
    @Nullable @Assisted("unlockableId") override val unlockableId: Int? = null,
    private val dungeonFactory: DungeonFactory,
    private val dungeonInstanceFactory: DungeonInstanceFactory,
    triggerGridFactory: TriggerGridFactory,
    private val storage: Storage,
    private val dungeonManager: DungeonManager,
    scriptParser: ScriptParser
) : Storage.Storable, FinalDungeon {

    @AssistedInject
    constructor(
        @Assisted dungeon: Dungeon,
        dungeonFactory: DungeonFactory,
        dungeonInstanceFactory: DungeonInstanceFactory,
        triggerGridFactory: TriggerGridFactory,
        storage: Storage,
        dungeonManager: DungeonManager,
        scriptParser: ScriptParser
    ) : this(
        dungeon.id,
        dungeon.name,
        dungeon.description,
        dungeon.difficulty,
        dungeon.points,
        dungeon.minPlayers,
        dungeon.maxPlayers,
        dungeon.box!!.copy(),
        dungeon.startingLocation!!.copy(),
        dungeon.triggers,
        dungeon.activeAreas,
        dungeon.spawnAreas,
        dungeon.chests,
        null,
        null,
        dungeonFactory,
        dungeonInstanceFactory,
        triggerGridFactory,
        storage,
        dungeonManager,
        scriptParser
    )

    override var isActive = true
    override var isBeingEdited = false
    override val triggerGrid = triggerGridFactory.createFinalDungeonGrid(this@FinalDungeonImpl)

    init {
        val scriptContents = storage
            .getScriptFilesForDungeon(this)
            .joinToString("") { it.readText() }

        if (scriptContents.isNotEmpty()) {
            scriptParser.parseScript(this, scriptContents).forEach { (k, v) ->
                triggers[k]?.effect = v
            }
        }
    }

    override fun putInEditMode(player: Player): EditableDungeon? {
        if (isActive) {
            player.sendPrefixedMessage(Strings.DUNGEON_WITH_ID_NOT_DISABLED, id)
            return null
        }

        if (isBeingEdited) {
            player.sendPrefixedMessage(Strings.DUNGEON_ALREADY_BEING_EDITED)
            return null
        }

        isBeingEdited = true

        player.sendPrefixedMessage(Strings.NOW_EDITING_DUNGEON_WITH_ID, id)

        return dungeonFactory.createEditable(player, this).also {
            dungeonManager.setPlayerEditableDungeon(player.uniqueId, it)
            dungeonManager.clearDungeonInstances(this)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun import(at: Vector3i): Boolean {
        if (dungeonManager.getDungeonInstances(this).isNotEmpty()) return false
        dungeonInstanceFactory.create(this, at)
        dungeonManager.saveInstancesToStorage()
        return true
    }
}