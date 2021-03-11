package it.forgottenworld.dungeons.core.cli

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.utils.clickEvent
import it.forgottenworld.dungeons.core.utils.color
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

        append("  [ ") color ChatColor.WHITE
        append(
            when {
                leader -> Strings.CREATE
                locked -> Strings.PRIVATE
                full -> Strings.FULL
                inGame -> Strings.IN_DUNGEON
                else -> Strings.JOIN
            }
        )
        color(
            when {
                locked -> ChatColor.GOLD
                full || inGame -> ChatColor.RED
                else -> ChatColor.GREEN
            }
        )
        if (!full && !locked && !inGame) {
            clickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/fwdungeons join ${instance.dungeon.id} ${instance.id}"
            )
        }
        append(" ]") color ChatColor.WHITE
    }

    private fun pageClickable(text: String, page: Int) = jsonMessage {
        append(text) color ChatColor.AQUA
        clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons list $page")
    }

    private fun paginator(page: Int) = jsonMessage {
        if (page > 0) {
            append("=[ ") color ChatColor.DARK_GRAY
            append(pageClickable("<<<<", page - 1))
            append(" ]=") color ChatColor.DARK_GRAY
        } else {
            append("=======") color ChatColor.DARK_GRAY
        }

        append("=====================================") color ChatColor.DARK_GRAY

        if (page < dungeonManager.finalDungeonCount - 1) {
            append("=[ ") color(ChatColor.DARK_GRAY)
            append(pageClickable(">>>>", page + 1))
            append(" ]=") color(ChatColor.DARK_GRAY)
        } else {
            append("=======")
            color(ChatColor.DARK_GRAY)
        }
    }

    private fun chevrons(count: Int) = "${ChatColor.GRAY}${">".repeat(count)} "

    private fun coloredDifficulty(difficulty: Dungeon.Difficulty) = jsonMessage {
        append(difficulty.toString().toUpperCase())
        color(
            when (difficulty) {
                Dungeon.Difficulty.EASY -> ChatColor.DARK_GREEN
                Dungeon.Difficulty.MEDIUM -> ChatColor.GOLD
                Dungeon.Difficulty.HARD -> ChatColor.DARK_RED
            }
        )
    }

    private fun dungeonDetails(dungeon: Dungeon) = jsonMessage {
        append(chevrons(3))
        append("DUNGEON: ") color ChatColor.DARK_AQUA
        append("${dungeon.name}\n") color ChatColor.WHITE

        append(chevrons(3))
        append("${Strings.DESCRIPTION}: ") color ChatColor.DARK_AQUA
        append("${dungeon.description}\n") color ChatColor.WHITE

        append(chevrons(3))
        append("${Strings.DIFFICULTY}: ") color ChatColor.DARK_AQUA
        append(coloredDifficulty(dungeon.difficulty))
        append("\n")

        append(chevrons(3))
        append("${Strings.PLAYERS}: ") color ChatColor.DARK_AQUA
        val minPl = dungeon.minPlayers
        val maxPl = dungeon.maxPlayers
        append("$minPl${if (maxPl != minPl) "-$maxPl" else ""}\n\n") color ChatColor.WHITE

        instanceDetails(dungeon)
    }

    private fun instanceDetails(dungeon: Dungeon) = jsonMessage {
        val instances = dungeonManager.getDungeonInstances(dungeon)
        for((i, inst) in instances.values.withIndex()) {
            append(chevrons(1))
            append("${Strings.ROOM} ${i + 1} ")
            append("| ") color ChatColor.DARK_GRAY
            append("Leader: ") color ChatColor.GRAY
            inst.leader?.let {
                val pl = Bukkit.getPlayer(it) ?: return@let
                append(pl.name) color ChatColor.LIGHT_PURPLE
            } ?: append("none") color ChatColor.DARK_GRAY
            append(joinClickable(inst))
            append(inst.leader?.let { "  [ ${inst.players.size}/${dungeon.maxPlayers} ]" } ?: "")
        }
        val paddingLines = 13 - instances.size - (dungeon.description.length + 6 + Strings.DESCRIPTION.length) / 55
        append("\n".repeat(paddingLines))
    }

    fun showPage(page: Int) = jsonMessage {
        if (page < 0 || page > dungeonManager.finalDungeonCount - 1) return@jsonMessage
        val dungeon = dungeonManager
            .getAllActiveFinalDungeons()
            .getOrNull(page) ?: return@jsonMessage
        append("§8====================[ ${Strings.CHAT_PREFIX_NO_SPACE}§7ungeons §8]====================\n\n")
        append(dungeonDetails(dungeon))
        append(paginator(page))
    }
}