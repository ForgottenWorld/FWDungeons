package it.forgottenworld.dungeons.cli

import it.forgottenworld.dungeons.model.dungeon.EditableDungeon
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElement
import it.forgottenworld.dungeons.utils.ktx.append
import it.forgottenworld.dungeons.utils.ktx.clickEvent
import it.forgottenworld.dungeons.utils.ktx.component
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import kotlin.math.floor

private fun getClickables(activeArea: InteractiveElement, type: String) = component {
    append("  [", ChatColor.WHITE)
    append(" HL ", ChatColor.GREEN)
    clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwde $type hl ${activeArea.id}")
    append("] [", ChatColor.WHITE)
    append(" X ", ChatColor.RED)
    clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwde $type unmake ${activeArea.id}")
    append("]\n", ChatColor.WHITE)
}

private fun getPageClickable(text: String, page: Int, type: String) = component {
    append(text, ChatColor.AQUA)
    clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeonsedit $type list $page")
}

private fun getPaginator(page: Int, maxPage: Int, type: String) = component {
    if (page > 0) {
        append("=[ ", ChatColor.DARK_GRAY)
        append(getPageClickable("PREVIOUS", page - 1, type))
        append(" ]", ChatColor.DARK_GRAY)
    } else {
        append("==========", ChatColor.DARK_GRAY)
    }

    append("================================", ChatColor.DARK_GRAY)

    if (page < maxPage) {
        append("===[ ")
        color(ChatColor.DARK_GRAY)
        append(getPageClickable("NEXT", page + 1, type))
        append(" ]=")
        color(ChatColor.DARK_GRAY)
    } else {
        append("=========")
        color(ChatColor.DARK_GRAY)
    }
}

const val ITEMS_PER_PAGE = 16
fun getInteractiveActiveAreaList(dungeon: EditableDungeon, page: Int) = component {

    append("====================[ ", ChatColor.DARK_GRAY)
    append(getString(Strings.CHAT_PREFIX).dropLast(1))
    append("ungeons ", ChatColor.GRAY)
    append("]====================\n", ChatColor.DARK_GRAY)

    append("====================[ ", ChatColor.DARK_GRAY)
    append("Active Areas", ChatColor.GREEN)
    append(" ]===================\n\n", ChatColor.DARK_GRAY)

    for ((k,v) in dungeon
            .activeAreas
            .entries
            .toList()
            .slice(page * ITEMS_PER_PAGE..((page + 1) * ITEMS_PER_PAGE - 1)
                    .coerceAtMost(dungeon.activeAreas.size - 1))) {
        append(">>> ",ChatColor.GRAY)
        append("#$k: ", ChatColor.DARK_AQUA)
        append(v.label ?: "NO LABEL", ChatColor.WHITE)
        append(getClickables(v, "aa"))
    }

    append("\n".repeat(ITEMS_PER_PAGE - dungeon.activeAreas.size))
    append(getPaginator(page, floor(dungeon.activeAreas.size / ITEMS_PER_PAGE.toDouble()).toInt(), "aa"))
}

fun getInteractiveTriggerList(dungeon: EditableDungeon, page: Int) = component {

    append("====================[ ", ChatColor.DARK_GRAY)
    append(getString(Strings.CHAT_PREFIX).dropLast(1))
    append("ungeons ", ChatColor.GRAY)
    append("]====================\n", ChatColor.DARK_GRAY)

    append("=====================[ ", ChatColor.DARK_GRAY)
    append("Triggers", ChatColor.BLUE)
    append(" ]=====================\n\n", ChatColor.DARK_GRAY)

    for ((k,v) in dungeon
            .triggers
            .entries
            .toList()
            .slice(page * ITEMS_PER_PAGE..((page + 1) * ITEMS_PER_PAGE - 1)
                    .coerceAtMost(dungeon.triggers.size - 1))) {
        append(">>> ",ChatColor.GRAY)
        append("#$k: ", ChatColor.DARK_AQUA)
        append(v.label ?: "NO LABEL", ChatColor.WHITE)
        append(getClickables(v, "t"))
    }

    append("\n".repeat(ITEMS_PER_PAGE - dungeon.triggers.size))
    append(getPaginator(page, floor(dungeon.triggers.size / ITEMS_PER_PAGE.toDouble()).toInt(), "t"))
}