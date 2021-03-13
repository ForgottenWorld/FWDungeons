package it.forgottenworld.dungeons.core.cli

import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.utils.jsonMessage
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import kotlin.math.floor

@Singleton
class InteractiveRegionListGuiGenerator {

    private fun clickables(
        interactiveEl: InteractiveRegion,
        type: String
    ) = jsonMessage {
        +"§f  ["
        +"§a HL "
        +ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fwde $type hl ${interactiveEl.id}")
        +"§f] ["
        +"§c X "
        +ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fwde $type unmake ${interactiveEl.id}")
        +"§f]"
        if (interactiveEl is Trigger) {
            +" §f["
            if (interactiveEl.effectCode.isEmpty()) {
                +"§7 NO CODE "
            } else {
                +"§d SHOW CODE "
                +ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fwde trigger code ${interactiveEl.id}")
            }
            +"§f]"
        }
        +"\n"
    }

    private fun pageClickable(
        text: String,
        page: Int,
        type: String
    ) = jsonMessage {
        +text
        +ChatColor.AQUA
        +ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeonsedit $type list $page")
    }

    private fun paginator(
        page: Int,
        maxPage: Int,
        type: String
    ) = jsonMessage {
        if (page > 0) {
            +"§8=[ "
            +pageClickable("<<<<", page - 1, type)
            +"§8 ]="
        } else {
            +"§8======="
        }

        +"====================================="
        +ChatColor.DARK_GRAY

        if (page < maxPage) {
            +"§8=[ "
            +pageClickable(">>>>", page + 1, type)
            +"§8 ]="
        } else {
            +"======="
            +ChatColor.DARK_GRAY
        }
    }

    fun showActiveAreas(
        dungeon: EditableDungeon,
        page: Int
    ) = jsonMessage {
        +Strings.CHAT_HEADER
        +"§8===================[ §aActive Areas §8]===================\n\n"

        val from = page * ITEMS_PER_PAGE
        val to = (page + 1) * ITEMS_PER_PAGE - 1
        val indices = from..to.coerceAtMost(dungeon.activeAreas.size - 1)

        val activeAreas = dungeon
            .activeAreas
            .toList()
            .slice(indices)

        for ((k, v) in activeAreas) {
            +"§7>>> §3#$k: §f${v.label ?: "NO LABEL"}"
            +ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/fwde aa label id:${v.id} ")
            +clickables(v, "aa")
        }

        +"\n".repeat(ITEMS_PER_PAGE - dungeon.activeAreas.size)
        +paginator(page, floor(dungeon.activeAreas.size / ITEMS_PER_PAGE.toDouble()).toInt(), "aa")
    }

    fun showTriggers(
        dungeon: EditableDungeon,
        page: Int
    ) = jsonMessage {
        +Strings.CHAT_HEADER
        +"§8=====================[ §9Triggers §8]=====================\n\n"

        val from = page * ITEMS_PER_PAGE
        val to = (page + 1) * ITEMS_PER_PAGE - 1
        val indices = from..to.coerceAtMost(dungeon.triggers.size - 1)

        val triggers = dungeon
            .triggers
            .toList()
            .slice(indices)

        for ((k, v) in triggers) {
            +"§7>>> §3#$k: §f${v.label ?: "NO LABEL"}"
            +ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/fwde t label id:${v.id} ")
            +clickables(v, "t")
        }

        +"\n".repeat(ITEMS_PER_PAGE - dungeon.triggers.size)
        +paginator(page, dungeon.triggers.size / ITEMS_PER_PAGE, "t")
    }

    companion object {
        private const val ITEMS_PER_PAGE = 16
    }
}