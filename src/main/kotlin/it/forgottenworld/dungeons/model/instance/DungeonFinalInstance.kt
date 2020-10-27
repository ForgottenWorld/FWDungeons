package it.forgottenworld.dungeons.model.instance

import it.forgottenworld.dungeons.cli.Strings
import it.forgottenworld.dungeons.cli.getLockClickable
import it.forgottenworld.dungeons.cli.getString
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.event.DungeonCompletedEvent
import it.forgottenworld.dungeons.model.dungeon.FinalDungeon
import it.forgottenworld.dungeons.model.interactiveelement.ActiveArea
import it.forgottenworld.dungeons.model.interactiveelement.Trigger
import it.forgottenworld.dungeons.service.DungeonService
import it.forgottenworld.dungeons.service.DungeonService.collidingTrigger
import it.forgottenworld.dungeons.service.DungeonService.dungeonInstance
import it.forgottenworld.dungeons.service.DungeonService.returnGameMode
import it.forgottenworld.dungeons.service.DungeonService.returnPosition
import it.forgottenworld.dungeons.service.InstanceObjectiveService
import it.forgottenworld.dungeons.service.RespawnService.respawnGameMode
import it.forgottenworld.dungeons.service.RespawnService.respawnLocation
import it.forgottenworld.dungeons.task.TriggerChecker
import it.forgottenworld.dungeons.utils.*
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.util.BlockVector

class DungeonFinalInstance(
        override val id: Int,
        override val dungeon: FinalDungeon,
        override val origin: BlockVector,
        override val triggers: Map<Int, Trigger>,
        override val activeAreas: Map<Int, ActiveArea>) : DungeonInstance {

    companion object {
        fun fromConfig(config: ConfigurationSection): DungeonFinalInstance? {
            val dungeon = DungeonService.dungeons[config.getInt("dungeon_id")] ?: return null
            val instOriginBlock = ConfigManager.dungeonWorld.getBlockAt(
                    config.getDouble("x").toInt(),
                    config.getDouble("y").toInt(),
                    config.getDouble("z").toInt()
            )

            return dungeon.createInstance(instOriginBlock, config.getInt("instance_id"))
        }
    }

    var isTpSafe = true
    val players: MutableList<Player> = mutableListOf()
    var leader: Player? = null
    val minPlayers = dungeon.numberOfPlayers.first
    val maxPlayers = dungeon.numberOfPlayers.last
    var isLocked = false

    override val box = dungeon.box.withOrigin(origin)

    private val startingPostion = dungeon
            .startingLocation
            .withRefSystemOrigin(BlockVector(0,0,0), origin)

    fun resetInstance() {
        disbandParty()
        isTpSafe = true
        triggers.values.forEach {
            it.procced = false
            it.clearCurrentlyInsidePlayers()
        }
        this.activeAreas.values.forEach { it.fillWithMaterial(it.startingMaterial) }
        InstanceObjectiveService.instanceObjectives[dungeon.id to id]?.abort()
        InstanceObjectiveService.instanceIdForTrackedMobs.values.removeAll { it == id }
        InstanceObjectiveService.dungeonIdForTrackedMobs.values.removeAll { it == id }
    }

    var inGame = false
    var partyKey = ""

    val playerCount: Int
        get() = players.size

    val isFull: Boolean
        get() = playerCount == maxPlayers

    fun lock() {
        isLocked = true
        partyKey = getRandomString(10)
    }

    fun unlock() {
        isLocked = false
        partyKey = ""
    }

    private fun disbandParty() {
        players.forEach { it.run {
            returnPosition = null
            returnGameMode = null
            returnGameMode = null
            collidingTrigger = null
        } }
        players.clear()
    }

    private fun checkUpdateLeader(player: Player) {
        if (leader != player) return
        if (players.isEmpty()) resetInstance()
        else leader = players.first().apply { sendFWDMessage("You're now the party leader") }
    }

    fun onPlayerJoin(player: Player) {
        if (isFull) {
            player.sendFWDMessage("This dungeon party is full")
            return
        }

        if (inGame) {
            player.sendFWDMessage("This dungeon party has already entered the dungeon")
            return
        }

        if (players.isEmpty()) {
            leader = player
            player.spigot()
                    .sendMessage(textComponent("${getString(Strings.CHAT_PREFIX)}Dungeon party created. To make it private, click ") {
                        addExtra(getLockClickable())
                    })
        } else {
            player.sendFWDMessage("You joined the dungeon party")
            players.forEach { it.sendFWDMessage("${player.name} joined the dungeon party") }
        }

        players.add(player)
        player.dungeonInstance = this
        return
    }

    private fun onPlayerRemoved(player: Player) {
        players.remove(player.apply {
            returnPosition = null
            returnGameMode = null
            collidingTrigger?.onPlayerExit(player)
        })
        checkUpdateLeader(player)
    }

    fun onPlayerLeave(player: Player) {
        if (inGame) {
            player.health = 0.0
            return
        }
        players.forEach { it.sendFWDMessage("${player.name} left the dungeon party") }
        onPlayerRemoved(player)
    }

    fun onPlayerDeath(player: Player) {
        players.forEach { it.sendFWDMessage("${player.name} died in the dungeon") }
        player.respawnLocation = player.returnPosition
        player.respawnGameMode = player.returnGameMode
        onPlayerRemoved(player)
    }

    fun onInstanceStart() {
        triggers.values.forEach { it.applyMeta() }
        TriggerChecker.activeInstances.add(this)
        inGame = true
        isTpSafe = false
        players.runForEach {
            returnPosition = location.clone()
            returnGameMode = gameMode
            gameMode = GameMode.ADVENTURE
            val startingLocation = startingPostion.locationInWorld(ConfigManager.dungeonWorld)
            teleport(startingLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
            sendFWDMessage("Good luck out there!")
        }
    }
    
    fun onInstanceFinish(givePoints: Boolean) {
        TriggerChecker.activeInstances.remove(this)

        if (givePoints && dungeon.points != 0)
            players
                    .map { it.uniqueId }
                    .let { DungeonCompletedEvent(it, dungeon.points.toFloat()) }
                    .let { Bukkit.getPluginManager().callEvent(it) }

        isTpSafe = true

        players.forEach {
            it.sendFWDMessage("${ChatColor.GREEN}Congratulations, you made it out alive!")
            it.returnPosition?.let { pos -> it.teleport(pos, PlayerTeleportEvent.TeleportCause.PLUGIN) }
            it.returnGameMode?.let { gm -> it.gameMode = gm }
        }

        disbandParty()
        resetInstance()
    }

    fun evacuate(): Boolean {
        onInstanceFinish(false)
        return true
    }

    fun toConfig(config: ConfigurationSection) {
        config.run {
            set("dungeon_id", dungeon.id)
            set("instance_id", id)
            set("x", origin.x)
            set("y", origin.y)
            set("z", origin.z)
        }
    }
}