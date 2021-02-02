package it.forgottenworld.dungeons.cli

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElement
import it.forgottenworld.dungeons.model.interactiveelement.Trigger
import it.forgottenworld.dungeons.utils.append
import it.forgottenworld.dungeons.utils.clickEvent
import it.forgottenworld.dungeons.utils.chatComponent
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import kotlin.math.floor

private fun getClickables(interactiveEl: InteractiveElement, type: String) = chatComponent {
    append("  [", ChatColor.WHITE)
    append(" HL ", ChatColor.GREEN)
    clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwde $type hl ${interactiveEl.id}")
    append("] [", ChatColor.WHITE)
    append(" X ", ChatColor.RED)
    clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwde $type unmake ${interactiveEl.id}")
    append("]", ChatColor.WHITE)
    if (interactiveEl is Trigger) {
        if (interactiveEl.effectCode.isEmpty()) {
            append(" [", ChatColor.WHITE)
            append(" NO CODE ", ChatColor.GRAY)
            append("]", ChatColor.WHITE)
        } else {
            append(" [", ChatColor.WHITE)
            append(" SHOW CODE ", ChatColor.LIGHT_PURPLE)
            clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwde trigger code ${interactiveEl.id}")
            append("]", ChatColor.WHITE)
        }
    }
    append("\n")
}

private fun getPageClickable(text: String, page: Int, type: String) = chatComponent {
    append(text, ChatColor.AQUA)
    clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeonsedit $type list $page")
}

private fun getPaginator(page: Int, maxPage: Int, type: String) = chatComponent {

    if (page > 0) {
        append("=[ ", ChatColor.DARK_GRAY)
        append(getPageClickable("<<<<", page - 1, type))
        append(" ]=", ChatColor.DARK_GRAY)
    } else {
        append("=======", ChatColor.DARK_GRAY)
    }

    append("=====================================", ChatColor.DARK_GRAY)

    if (page < maxPage) {
        append("=[ ")
        color(ChatColor.DARK_GRAY)
        append(getPageClickable(">>>>", page + 1, type))
        append(" ]=")
        color(ChatColor.DARK_GRAY)
    } else {
        append("=======")
        color(ChatColor.DARK_GRAY)
    }
}

const val ITEMS_PER_PAGE = 16
fun getInteractiveActiveAreaList(dungeon: EditableDungeon, page: Int) = chatComponent {

    append("====================[ ", ChatColor.DARK_GRAY)
    append(Strings.CHAT_PREFIX.dropLast(1))
    append("ungeons ", ChatColor.GRAY)
    append("]====================\n", ChatColor.DARK_GRAY)

    append("====================[ ", ChatColor.DARK_GRAY)
    append("Active Areas", ChatColor.GREEN)
    append(" ]===================\n\n", ChatColor.DARK_GRAY)

    for ((k, v) in dungeon
        .activeAreas
        .entries
        .toList()
        .slice(page * ITEMS_PER_PAGE..((page + 1) * ITEMS_PER_PAGE - 1)
            .coerceAtMost(dungeon.activeAreas.size - 1))) {
        append(">>> ", ChatColor.GRAY)
        append("#$k: ", ChatColor.DARK_AQUA)
        append(v.label ?: "NO LABEL", ChatColor.WHITE)
        append(getClickables(v, "aa"))
    }

    append("\n".repeat(ITEMS_PER_PAGE - dungeon.activeAreas.size))
    append(getPaginator(page, floor(dungeon.activeAreas.size / ITEMS_PER_PAGE.toDouble()).toInt(), "aa"))
}

fun getInteractiveTriggerList(dungeon: EditableDungeon, page: Int) = chatComponent {

    append("====================[ ", ChatColor.DARK_GRAY)
    append(Strings.CHAT_PREFIX.dropLast(1))
    append("ungeons ", ChatColor.GRAY)
    append("]====================\n", ChatColor.DARK_GRAY)

    append("=====================[ ", ChatColor.DARK_GRAY)
    append("Triggers", ChatColor.BLUE)
    append(" ]=====================\n\n", ChatColor.DARK_GRAY)

    for ((k, v) in dungeon
        .triggers
        .entries
        .toList()
        .slice(page * ITEMS_PER_PAGE..((page + 1) * ITEMS_PER_PAGE - 1)
            .coerceAtMost(dungeon.triggers.size - 1))) {
        append(">>> ", ChatColor.GRAY)
        append("#$k: ", ChatColor.DARK_AQUA)
        append(v.label ?: "NO LABEL", ChatColor.WHITE)
        append(getClickables(v, "t"))
    }

    append("\n".repeat(ITEMS_PER_PAGE - dungeon.triggers.size))
    append(getPaginator(page, floor(dungeon.triggers.size / ITEMS_PER_PAGE.toDouble()).toInt(), "t"))
}