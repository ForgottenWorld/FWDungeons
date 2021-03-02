package it.forgottenworld.dungeons.core.game.dungeon

import com.google.inject.assistedinject.Assisted
import com.google.inject.assistedinject.AssistedInject
import it.forgottenworld.dungeons.api.game.chest.Chest
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.api.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.DungeonManager
import it.forgottenworld.dungeons.core.game.detection.TriggerGridFactory
import it.forgottenworld.dungeons.core.game.instance.DungeonInstanceFactory
import it.forgottenworld.dungeons.core.utils.launchAsync
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

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
    @Assisted override val chests: Map<Int, Chest>,
    private val plugin: FWDungeonsPlugin,
    private val dungeonFactory: DungeonFactory,
    private val dungeonInstanceFactory: DungeonInstanceFactory,
    triggerGridFactory: TriggerGridFactory,
    private val storage: Storage,
    private val dungeonManager: DungeonManager
) : Storage.Storable, FinalDungeon {

    @AssistedInject
    constructor(
        @Assisted dungeon: Dungeon,
        plugin: FWDungeonsPlugin,
        dungeonFactory: DungeonFactory,
        dungeonInstanceFactory: DungeonInstanceFactory,
        triggerGridFactory: TriggerGridFactory,
        storage: Storage,
        dungeonManager: DungeonManager
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
        dungeon.chests,
        plugin,
        dungeonFactory,
        dungeonInstanceFactory,
        triggerGridFactory,
        storage,
        dungeonManager
    )

    override var isActive = true
    override var isBeingEdited = false
    override val triggerGrid = triggerGridFactory.createFinalDungeonGrid(this@FinalDungeonImpl)

    override fun putInEditMode(player: Player): EditableDungeon? {
        if (isActive) {
            player.sendFWDMessage(Strings.DUNGEON_WITH_ID_NOT_DISABLED.format(id))
            return null
        }

        if (isBeingEdited) {
            player.sendFWDMessage(Strings.DUNGEON_ALREADY_BEING_EDITED)
            return null
        }
        isBeingEdited = true

        player.sendFWDMessage(Strings.NOW_EDITING_DUNGEON_WITH_ID.format(id))

        return dungeonFactory.createEditable(player, this).also {
            dungeonManager.setPlayerEditableDungeon(player.uniqueId, it)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun import(at: Vector3i): Boolean {
        if (dungeonManager.getDungeonInstances(this).isNotEmpty()) return false
        val config = YamlConfiguration()
        val file = File(plugin.dataFolder, "instances.yml")
        if (file.exists()) config.load(file)
        val inst = dungeonInstanceFactory.create(this, at)
        storage.save(inst, config.createSection("$id"))
        launchAsync { config.save(file) }
        return true
    }
}