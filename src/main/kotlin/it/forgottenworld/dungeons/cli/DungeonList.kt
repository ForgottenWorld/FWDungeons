package it.forgottenworld.dungeons.cli

import it.forgottenworld.dungeons.model.dungeon.Difficulty
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.manager.DungeonManager
import it.forgottenworld.dungeons.utils.append
import it.forgottenworld.dungeons.utils.clickEvent
import it.forgottenworld.dungeons.utils.component
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import java.lang.Integer.max
import kotlin.math.ceil

private fun getJoinClickable(
        instance: DungeonFinalInstance,
        leader: Boolean,
        locked: Boolean,
        full: Boolean,
        inGame: Boolean) = component {

    append(when {
        leader -> "CREATE"
        locked -> "PRIVATE"
        full -> "FULL"
        inGame -> "IN DUNGEON"
        else -> "JOIN"
    })

    color(when {
        locked -> ChatColor.GOLD
        full || inGame -> ChatColor.RED
        else -> ChatColor.GREEN
    })

    if (!full && !locked && !inGame)
        clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons join ${instance.dungeon.id} ${instance.id}")
}

private fun getPageClickable(text: String, page: Int) = component {
    append(text, ChatColor.AQUA)
    clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons list $page")
}

private fun getPaginator(page: Int) = component {
    if (page > 0) {
        append("=[ ", ChatColor.DARK_GRAY)
        append(getPageClickable("PREVIOUS", page - 1))
        append(" ]", ChatColor.DARK_GRAY)
    } else {
        append("=============", ChatColor.DARK_GRAY)
    }

    append("===============================", ChatColor.DARK_GRAY)

    if (page < DungeonManager.dungeons.count() - 1) {
        append("[ ")
        color(ChatColor.DARK_GRAY)
        append(getPageClickable("NEXT", page + 1))
        append(" ]=")
        color(ChatColor.DARK_GRAY)
    } else {
        append("=========")
        color(ChatColor.DARK_GRAY)
    }
}

private fun getChevrons(count: Int) = "${ChatColor.GRAY}${">".repeat(count)} "

private fun getColoredDifficulty(difficulty: Difficulty) = component {
    append("${difficulty.toString().toUpperCase()}\n")
    color(when (difficulty) {
        Difficulty.EASY -> ChatColor.DARK_GREEN
        Difficulty.MEDIUM -> ChatColor.GOLD
        Difficulty.HARD -> ChatColor.DARK_RED
    })
}

fun getInteractiveDungeonList(page: Int) = component {

    if (page < 0 || page > DungeonManager.dungeons.count() - 1) return@component
    val d = DungeonManager.dungeons.values
            .filter { it.active }
            .toList()
            .getOrNull(page) ?: return@component

    append("====================[ ", ChatColor.DARK_GRAY)
    append(getString(Strings.CHAT_PREFIX).dropLast(1))
    append("ungeons ", ChatColor.GRAY)
    append("]====================\n\n", ChatColor.DARK_GRAY)

    append(getChevrons(3))
    append("DUNGEON: ", ChatColor.DARK_AQUA)
    append("${d.name}\n", ChatColor.WHITE)

    append(getChevrons(3))
    append("DESCRIPTION: ", ChatColor.DARK_AQUA)
    append("${d.description}\n", ChatColor.WHITE)

    append(getChevrons(3))
    append("DIFFICULTY: ", ChatColor.DARK_AQUA)
    append(getColoredDifficulty(d.difficulty))
    append("\n")

    append(getChevrons(3))
    append("PLAYERS: ", ChatColor.DARK_AQUA)

    val minPl = d.numberOfPlayers.first
    val maxPl = d.numberOfPlayers.last
    append("$minPl${if (maxPl != minPl) "-$maxPl" else ""}\n\n", ChatColor.WHITE)

    d.instances.values.forEachIndexed { ii, inst ->

        append(getChevrons(1))
        append("Room ${ii + 1} ")
        append("| ", ChatColor.DARK_GRAY)
        append("Leader: ", ChatColor.GRAY)
        inst.leader?.name?.let {
            append(it, ChatColor.LIGHT_PURPLE)
        } ?: run {
            append("none", ChatColor.DARK_GRAY)
        }

        append("  [ ", ChatColor.WHITE)
        append(getJoinClickable(
                    inst,
                    inst.leader == null,
                    inst.isLocked,
                    inst.isFull,
                    inst.inGame))
        append(" ]", ChatColor.WHITE)

        append(inst.leader?.let{ "  [ ${inst.playerCount}/${inst.maxPlayers} ]" } ?: "")
    }

    append("\n".repeat(12 - d.instances.count() - ceil(max((d.description.length - 48), 0) / 60.0).toInt()))
    append(getPaginator(page))
}