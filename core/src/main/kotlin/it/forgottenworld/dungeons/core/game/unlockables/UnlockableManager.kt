package it.forgottenworld.dungeons.core.game.unlockables

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.unlockables.Unlockable
import it.forgottenworld.dungeons.api.game.unlockables.UnlockableSeries
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.load
import it.forgottenworld.dungeons.api.storage.Storage.Companion.save
import it.forgottenworld.dungeons.api.storage.edit
import it.forgottenworld.dungeons.api.storage.read
import it.forgottenworld.dungeons.api.storage.yaml
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*

@Singleton
class UnlockableManager @Inject constructor(
    private val storage: Storage,
    private val unlockableFactory: UnlockableFactory
) {

    private val unlockableSeries = mutableMapOf<Int, UnlockableSeries>()
    private val playerUnlockProgress = mutableMapOf<Pair<UUID, Int>, Int>()
    private val unlockablePositions = mutableMapOf<Pair<UUID, Vector3i>, Pair<Int, Int>>()

    private val dummySeries get() = UnlockableSeriesImpl(
        0,
        "EXAMPLE SERIES",
        "description",
        listOf(
            unlockableFactory.create(
                0,
                0,
                "This is EXAMPLE UNLOCKABLE 0",
                "This appears when EXAMPLE UNLOCKABLE 0 is unlocked",
                listOf(Unlockable.Requirement.Item(Material.COBBLESTONE, 10))
            ),
            unlockableFactory.create(
                0,
                1,
                "This is EXAMPLE UNLOCKABLE 1",
                "This appears when EXAMPLE UNLOCKABLE 1 is unlocked",
                listOf(Unlockable.Requirement.Economy(1000.0))
            )
        )
    )

    fun loadUnlockablesFromStorage() {
        yaml {
            load(storage.unlockablesFile)
            read {
                forEachSection { _, sec ->
                    val series = storage.load<UnlockableSeries>(sec)
                    unlockableSeries[series.id] = series
                }
            }
            if (unlockableSeries.isEmpty()) {
                edit {
                    storage.save<UnlockableSeries>(dummySeries, section("0"))
                }
                save(storage.unlockablesFile)
            }
        }
    }

    fun lookUpPlate(
        worldId: UUID,
        position: Vector3i
    ) = unlockablePositions[worldId to position]

    fun bindPlateToUnlockable(
        seriesId: Int,
        unlockableId: Int,
        worldId: UUID,
        position: Vector3i
    ) {
        unlockablePositions[worldId to position] = seriesId to unlockableId
    }

    fun unbindPlate(
        worldId: UUID,
        position: Vector3i
    ) = unlockablePositions.remove(worldId to position) != null

    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action != Action.PHYSICAL) return
        val block = event.clickedBlock ?: return
        if (isBlockRecognizedPlate(block)) {
            onPlayerStepOnPlate(event.player, block.location)
        }
    }

    fun hasPlayerUnlocked(player: Player, seriesId: Int, unlockableOrder: Int) =
        playerUnlockProgress[player.uniqueId to seriesId] ?: 0 >= unlockableOrder

    private fun onPlayerStepOnPlate(player: Player, location: Location) {
        val (seriesId, unlockableOrder) = lookUpPlate(
            location.world.uid,
            Vector3i.ofLocation(location)
        ) ?: return

        tryPlayerUnlock(player, seriesId, unlockableOrder)
    }

    private fun tryPlayerUnlock(player: Player, seriesId: Int, unlockableOrder: Int) {
        val key = player.uniqueId to seriesId
        val progress = playerUnlockProgress[key] ?: run {
            playerUnlockProgress[key] = 0
            0
        }

        val unlockable = unlockableSeries[seriesId]!!.unlockables[unlockableOrder]
        player.sendPrefixedMessage(unlockable.message)

        if (progress > unlockableOrder) return

        if (progress < unlockableOrder) {
            player.sendPrefixedMessage(Strings.YOU_CANT_UNLOCK_YET, seriesId, unlockableOrder)
            return
        }

        player.sendPrefixedMessage(unlockable.printRequirements())
        if (unlockable.executeRequirements(player)) {
            player.sendPrefixedMessage(unlockable.unlockedMessage)
            playerUnlockProgress[key] = progress + 1
        } else {
            player.sendPrefixedMessage(Strings.REQUIREMENTS_NOT_MET)
        }
    }

    fun isBlockRecognizedPlate(block: Block) = RECOGNIZED_PRESSURE_PLATE_TYPES.contains(block.type)

    companion object {
        private val RECOGNIZED_PRESSURE_PLATE_TYPES = setOf(
            Material.ACACIA_PRESSURE_PLATE,
            Material.BIRCH_PRESSURE_PLATE,
            Material.CRIMSON_PRESSURE_PLATE,
            Material.DARK_OAK_PRESSURE_PLATE,
            Material.JUNGLE_PRESSURE_PLATE,
            Material.OAK_PRESSURE_PLATE,
            Material.POLISHED_BLACKSTONE_PRESSURE_PLATE,
            Material.SPRUCE_PRESSURE_PLATE,
            Material.STONE_PRESSURE_PLATE,
            Material.WARPED_PRESSURE_PLATE
        )
    }
}