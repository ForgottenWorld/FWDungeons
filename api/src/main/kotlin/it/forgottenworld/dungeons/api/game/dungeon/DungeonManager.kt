package it.forgottenworld.dungeons.api.game.dungeon

import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import org.bukkit.entity.Player
import java.util.*

interface DungeonManager {

    val finalDungeonCount: Int

    fun getFirstAvailableFinalDungeonId(): Int

    fun clearFinalDungeons()

    fun enableDungeon(id: Int)

    fun disableDungeon(id: Int): Boolean

    fun getAllFinalDungeons(): Collection<FinalDungeon>

    fun getAllActiveFinalDungeons(): List<FinalDungeon>

    fun showDungeonListToPlayer(player: Player, page: Int)

    fun registerFinalDungeon(dungeon: FinalDungeon)

    fun getFinalDungeonById(id: Int): FinalDungeon?

    fun getAllBusyInstances(): List<DungeonInstance>

    fun getDungeonInstances(dungeon: Dungeon): Map<Int, DungeonInstance>

    fun registerDungeonInstance(instance: DungeonInstance)

    fun clearDungeonInstances(dungeon: Dungeon)

    fun setDungeonInstances(dungeon: Dungeon, instances: Map<Int, DungeonInstance>)

    fun getPlayerEditableDungeon(uuid: UUID): EditableDungeon?

    fun setPlayerEditableDungeon(uuid: UUID, editableDungeon: EditableDungeon?)

    fun getPlayerInstance(uuid: UUID): DungeonInstance?

    fun setPlayerInstance(uuid: UUID, dungeonInstance: DungeonInstance?)

    fun loadDungeonsFromStorage()

    fun loadInstancesFromStorage()

    fun saveInstancesToStorage()

    fun saveDungeonToStorage(dungeon: FinalDungeon)
}