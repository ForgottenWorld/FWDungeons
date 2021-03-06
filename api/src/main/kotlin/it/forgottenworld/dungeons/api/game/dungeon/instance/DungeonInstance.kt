package it.forgottenworld.dungeons.api.game.dungeon.instance

import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.objective.CombatObjective
import it.forgottenworld.dungeons.api.game.objective.MobSpawnData
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage
import net.kyori.adventure.audience.Audience
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

interface DungeonInstance : Storage.Storable {

    val id: Int

    val dungeon: Dungeon

    val origin: Vector3i

    var leader: UUID?

    var partyKey: String

    val isLocked: Boolean

    val isInGame: Boolean

    val players: MutableList<UUID>

    val isTpSafe: Boolean

    val instanceObjectives: MutableList<CombatObjective>

    val audience: Audience get() = Audience.audience(players.mapNotNull { Bukkit.getPlayer(it) })

    fun attachNewObjective(mobs: List<MobSpawnData>, onAllKilled: (DungeonInstance) -> Unit)

    fun onInstanceFinish(givePoints: Boolean)

    fun onFinishTriggered()

    fun evacuate(): Boolean

    fun onPlayerJoin(player: Player)

    fun onStart()

    fun onPlayerLeave(player: Player)

    fun onPlayerDeath(player: Player)

    fun rescuePlayer(player: Player)

    fun onPlayerMove(player: Player)

    fun lock()

    fun unlock()
}