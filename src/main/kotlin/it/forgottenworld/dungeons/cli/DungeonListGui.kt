package it.forgottenworld.dungeons.cli

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.dungeon.Dungeon
import it.forgottenworld.dungeons.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.game.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.utils.append
import it.forgottenworld.dungeons.utils.chatComponent
import it.forgottenworld.dungeons.utils.clickEvent
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import kotlin.math.floor

object DungeonListGui {

    private fun joinClickable(instance: DungeonFinalInstance) = chatComponent {
        val leader = instance.leader == null
        val locked = instance.isLocked
        val full = instance.isFull
        val inGame = instance.inGame

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
        if (!full && !locked && !inGame)
            clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons join ${instance.dungeon.id} ${instance.id}")
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

        if (page < FinalDungeon.dungeons.count() - 1) {
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

        if (page < 0 || page > FinalDungeon.dungeons.count() - 1) return@chatComponent
        val dng = FinalDungeon.dungeons.values
            .filter { it.isActive }
            .getOrNull(page) ?: return@chatComponent

        append("====================[ ", ChatColor.DARK_GRAY)
        append(Strings.CHAT_PREFIX.dropLast(1))
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

        val minPl = dng.numberOfPlayers.first
        val maxPl = dng.numberOfPlayers.last
        append("$minPl${if (maxPl != minPl) "-$maxPl" else ""}\n\n", ChatColor.WHITE)

        dng.instances.values.forEachIndexed { ii, inst ->
            append(chevrons(1))
            append("${Strings.ROOM} ${ii + 1} ")
            append("| ", ChatColor.DARK_GRAY)
            append("Leader: ", ChatColor.GRAY)
            inst.leader?.name
                ?.let { append(it, ChatColor.LIGHT_PURPLE) }
                ?: append("none", ChatColor.DARK_GRAY)
            append(joinClickable(inst))
            append(inst.leader?.let { "  [ ${inst.playerCount}/${inst.maxPlayers} ]" } ?: "")
        }

        append("\n".repeat(13 - dng.instances.size - floor((dng.description.length + 6 + Strings.DESCRIPTION.length) / 55.0).toInt()))
        append(paginator(page))
    }
}