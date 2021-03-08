package it.forgottenworld.dungeons.core.cli

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.utils.append
import it.forgottenworld.dungeons.core.utils.chatComponent
import it.forgottenworld.dungeons.core.utils.clickEvent
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.Bukkit

@Singleton
class DungeonListGuiGenerator @Inject constructor(
    private val dungeonManager: DungeonManager
) {

    private fun joinClickable(instance: DungeonInstance) = chatComponent {
        val leader = instance.leader == null
        val locked = instance.isLocked
        val full = instance.players.size == instance.dungeon.maxPlayers
        val inGame = instance.isInGame

        append("  [ ", ChatColor.WHITE)
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
        append(" ]", ChatColor.WHITE)
    }

    private fun pageClickable(text: String, page: Int) = chatComponent {
        append(text, ChatColor.AQUA)
        clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons list $page")
    }

    private fun paginator(page: Int) = chatComponent {
        if (page > 0) {
            append("=[ ", ChatColor.DARK_GRAY)
            append(pageClickable("<<<<", page - 1))
            append(" ]=", ChatColor.DARK_GRAY)
        } else {
            append("=======", ChatColor.DARK_GRAY)
        }

        append("=====================================", ChatColor.DARK_GRAY)

        if (page < dungeonManager.finalDungeonCount - 1) {
            append("=[ ")
            color(ChatColor.DARK_GRAY)
            append(pageClickable(">>>>", page + 1))
            append(" ]=")
            color(ChatColor.DARK_GRAY)
        } else {
            append("=======")
            color(ChatColor.DARK_GRAY)
        }
    }

    private fun chevrons(count: Int) = "${ChatColor.GRAY}${">".repeat(count)} "

    private fun coloredDifficulty(difficulty: Dungeon.Difficulty) = chatComponent {
        append(difficulty.toString().toUpperCase())
        color(
            when (difficulty) {
                Dungeon.Difficulty.EASY -> ChatColor.DARK_GREEN
                Dungeon.Difficulty.MEDIUM -> ChatColor.GOLD
                Dungeon.Difficulty.HARD -> ChatColor.DARK_RED
            }
        )
    }

    fun showPage(page: Int) = chatComponent {

        if (page < 0 || page > dungeonManager.finalDungeonCount - 1) return@chatComponent
        val dng = dungeonManager
            .getAllActiveFinalDungeons()
            .getOrNull(page) ?: return@chatComponent

        append("====================[ ", ChatColor.DARK_GRAY)
        append(Strings.CHAT_PREFIX_NO_SPACE)
        append("ungeons ", ChatColor.GRAY)
        append("]====================\n\n", ChatColor.DARK_GRAY)

        append(chevrons(3))
        append("DUNGEON: ", ChatColor.DARK_AQUA)
        append("${dng.name}\n", ChatColor.WHITE)

        append(chevrons(3))
        append("${Strings.DESCRIPTION}: ", ChatColor.DARK_AQUA)
        append("${dng.description}\n", ChatColor.WHITE)

        append(chevrons(3))
        append("${Strings.DIFFICULTY}: ", ChatColor.DARK_AQUA)
        append(coloredDifficulty(dng.difficulty))
        append("\n")

        append(chevrons(3))
        append("${Strings.PLAYERS}: ", ChatColor.DARK_AQUA)

        val minPl = dng.minPlayers
        val maxPl = dng.maxPlayers
        append("$minPl${if (maxPl != minPl) "-$maxPl" else ""}\n\n", ChatColor.WHITE)

        for((i, inst) in dungeonManager.getDungeonInstances(dng).values.withIndex()) {
            append(chevrons(1))
            append("${Strings.ROOM} ${i + 1} ")
            append("| ", ChatColor.DARK_GRAY)
            append("Leader: ", ChatColor.GRAY)
            inst.leader?.let {
                val pl = Bukkit.getPlayer(it) ?: return@let
                append(pl.name, ChatColor.LIGHT_PURPLE)
            } ?: append("none", ChatColor.DARK_GRAY)
            append(joinClickable(inst))
            append(inst.leader?.let { "  [ ${inst.players.size}/$maxPl ]" } ?: "")
        }

        val paddingLines = 13 -
            dungeonManager.getDungeonInstances(dng).size -
            ((dng.description.length + 6 + Strings.DESCRIPTION.length) / 55)

        append("\n".repeat(paddingLines))
        append(paginator(page))
    }
}