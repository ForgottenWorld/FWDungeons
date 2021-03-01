package it.forgottenworld.dungeons.core.game.dungeon

import com.google.inject.assistedinject.Assisted
import com.google.inject.assistedinject.AssistedInject
import it.forgottenworld.dungeons.api.game.chest.Chest
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import it.forgottenworld.dungeons.core.config.Configuration
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.DungeonManager
import it.forgottenworld.dungeons.core.game.DungeonManager.editableDungeon
import it.forgottenworld.dungeons.core.game.detection.TriggerGridFactory
import it.forgottenworld.dungeons.core.game.instance.DungeonInstanceFactory
import it.forgottenworld.dungeons.core.utils.launchAsync
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

class FinalDungeon @AssistedInject constructor(
    @Assisted override val id: Int,
    @Assisted override val name: String,
    @Assisted override val description: String,
    @Assisted override val difficulty: Dungeon.Difficulty,
    @Assisted override val points: Int,
    @Assisted override val minPlayers: Int,
    @Assisted override val maxPlayers: Int,
    @Assisted override val box: Box,
    @Assisted override val startingLocation: Vector3i,
    @Assisted override val triggers: Map<Int, Trigger>,
    @Assisted override val activeAreas: Map<Int, ActiveArea>,
    @Assisted override val chests: Map<Int, Chest>,
    private val plugin: FWDungeonsPlugin,
    private val configuration: Configuration,
    private val dungeonFactory: DungeonFactory,
    private val dungeonInstanceFactory: DungeonInstanceFactory,
    private val storage: Storage
) : Dungeon, Storage.Storable {

    @AssistedInject
    constructor(
        @Assisted dungeon: Dungeon,
        plugin: FWDungeonsPlugin,
        configuration: Configuration,
        dungeonFactory: DungeonFactory,
        dungeonInstanceFactory: DungeonInstanceFactory,
        storage: Storage
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
        configuration,
        dungeonFactory,
        dungeonInstanceFactory,
        storage
    )

    var isActive = true
    var isBeingEdited = false
    val triggerGrid = TriggerGridFactory.createFinalDungeonGrid(this@FinalDungeon)

    fun putInEditMode(player: Player): EditableDungeon? {
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
            player.uniqueId.editableDungeon = it
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    fun import(at: Vector3i): Boolean {
        if (DungeonManager.getDungeonInstances(this).isNotEmpty()) return false
        val config = YamlConfiguration()
        val file = File(plugin.dataFolder, "instances.yml")
        if (file.exists()) config.load(file)
        val inst = dungeonInstanceFactory.create(this, at)
        storage.save(inst, config.createSection("$id"))
        launchAsync { config.save(file) }
        return true
    }
}