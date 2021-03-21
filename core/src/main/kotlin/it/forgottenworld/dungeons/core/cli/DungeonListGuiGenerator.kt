package it.forgottenworld.dungeons.core.cli

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.jsonMessage
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.Bukkit

@Singleton
class DungeonListGuiGenerator @Inject constructor(
    private val dungeonManager: DungeonManager
) {

    private fun joinClickable(instance: DungeonInstance) = jsonMessage {
        val leader = instance.leader == null
        val locked = instance.isLocked
        val full = instance.players.size == instance.dungeon.maxPlayers
        val inGame = instance.isInGame

        +"  §f[ "
        +when {
            leader -> Strings.CREATE
            locked -> Strings.PRIVATE
            full -> Strings.FULL
            inGame -> Strings.IN_DUNGEON
            else -> Strings.JOIN
        }
        +when {
            locked -> ChatColor.GOLD
            full || inGame -> ChatColor.RED
            else -> ChatColor.GREEN
        }
        if (!full && !locked && !inGame) {
            +ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/fwdungeons join ${instance.dungeon.id} ${instance.id}"
            )
        }
        +" §f]"
    }

    private fun pageClickable(text: String, page: Int) = jsonMessage {
        +"§8=[ "
        +text
        +ChatColor.AQUA
        +ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons list $page")
        +" §8]="
    }

    private fun paginator(page: Int) = jsonMessage {
        if (page > 0) {
            +pageClickable("<<<<", page - 1)
        } else {
            +"§8======="
        }
        +"§8====================================="
        if (page < dungeonManager.finalDungeonCount - 1) {
            +pageClickable(">>>>", page + 1)
        } else {
            +"§8======="
        }
    }

    private fun chevrons(count: Int) = "§7${">".repeat(count)} "

    private fun coloredDifficulty(difficulty: Dungeon.Difficulty) = jsonMessage {
        +difficulty.toString().toUpperCase()
        +when (difficulty) {
            Dungeon.Difficulty.EASY -> ChatColor.DARK_GREEN
            Dungeon.Difficulty.MEDIUM -> ChatColor.GOLD
            Dungeon.Difficulty.HARD -> ChatColor.DARK_RED
        }
    }

    private fun dungeonDetails(dungeon: Dungeon) = jsonMessage {
        +chevrons(3)
        +"§3DUNGEON: §f${dungeon.name}\n"

        +chevrons(3)
        +"§3${Strings.DESCRIPTION}: §f${dungeon.description}\n"

        +chevrons(3)
        +"§3${Strings.DIFFICULTY}: "
        +coloredDifficulty(dungeon.difficulty)
        +"\n"

        +chevrons(3)
        +"§3${Strings.PLAYERS}: "
        +"§f${dungeon.minPlayers}${
            if (dungeon.maxPlayers != dungeon.minPlayers) "-${dungeon.maxPlayers}" else ""
        }\n\n"

        +instanceDetails(dungeon)
    }

    private fun instanceDetails(dungeon: Dungeon) = jsonMessage {
        val instances = dungeonManager.getDungeonInstances(dungeon)
        for ((i, inst) in instances.values.withIndex()) {
            val leader = inst.leader?.let(Bukkit::getPlayer)
            val leaderName = leader?.let { "§d${it.name}" } ?: "§8none"
            +chevrons(1)
            +"${Strings.ROOM} ${i + 1} §8| §7Leader: $leaderName"
            +joinClickable(inst)
            +(inst.leader?.let { "  [ ${inst.players.size}/${dungeon.maxPlayers} ]" } ?: "")
        }
        val paddingLines = 13 - instances.size - (dungeon.description.length + 6 + Strings.DESCRIPTION.length) / 55
        +"\n".repeat(paddingLines)
    }

    fun showPage(page: Int) = jsonMessage {
        if (page < 0 || page > dungeonManager.finalDungeonCount - 1) return@jsonMessage
        val dungeon = dungeonManager
            .getAllActiveFinalDungeons()
            .getOrNull(page) ?: return@jsonMessage
        +Strings.CHAT_HEADER
        +"\n"
        +dungeonDetails(dungeon)
        +paginator(page)
    }
}