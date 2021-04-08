package it.forgottenworld.dungeons.core.cli

import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.jsonMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import kotlin.math.floor

@Singleton
class DungeonElementGuiGenerator {

    private fun interactiveRegionClickables(
        interactiveEl: InteractiveRegion,
        type: String
    ) = jsonMessage {
        +Component.text("  [", NamedTextColor.WHITE)
        +Component
            .text(" HL ", NamedTextColor.GREEN)
            .clickEvent(ClickEvent.runCommand("/fwde $type hl ${interactiveEl.id}"))
        +Component.text("] [", NamedTextColor.WHITE)
        +Component
            .text(" X ", NamedTextColor.RED)
            .clickEvent(ClickEvent.runCommand("/fwde $type unmake ${interactiveEl.id}"))
        +Component.text("]\n", NamedTextColor.WHITE)
    }

    private fun pageClickable(
        text: String,
        page: Int,
        type: String
    ) = Component.text(text, NamedTextColor.AQUA)
        .clickEvent(ClickEvent.runCommand("/fwdungeonsedit $type list $page"))

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
            +"§8========"
        }

        +"§8====================================="

        if (page < maxPage) {
            +"§8=[ "
            +pageClickable(">>>>", page + 1, type)
            +"§8 ]="
        } else {
            +"§8========"
        }
    }

    fun showActiveAreas(
        dungeon: EditableDungeon,
        page: Int
    ) = jsonMessage {
        +Strings.CHAT_HEADER
        +"§8===================[ §9Active Areas §8]===================\n\n"

        val from = page * ITEMS_PER_PAGE
        val to = (page + 1) * ITEMS_PER_PAGE - 1
        val indices = from..to.coerceAtMost(dungeon.activeAreas.size - 1)

        val activeAreas = dungeon
            .activeAreas
            .toList()
            .slice(indices)

        for ((k, v) in activeAreas) {
            +"§7>>> §3#$k: §f${v.label ?: "NO LABEL"}"
            +ClickEvent.suggestCommand("/fwde aa label id:${v.id} ")
            +interactiveRegionClickables(v, "aa")
        }

        +"\n".repeat(ITEMS_PER_PAGE - dungeon.activeAreas.size)
        +paginator(page, floor(dungeon.activeAreas.size / ITEMS_PER_PAGE.toDouble()).toInt(), "aa")
    }

    fun showSpawnAreas(
        dungeon: EditableDungeon,
        page: Int
    ) = jsonMessage {
        +Strings.CHAT_HEADER
        +"§8====================[ §dSpawn Areas §8]===================\n\n"

        val from = page * ITEMS_PER_PAGE
        val to = (page + 1) * ITEMS_PER_PAGE - 1
        val indices = from..to.coerceAtMost(dungeon.spawnAreas.size - 1)

        val spawnAreas = dungeon
            .spawnAreas
            .toList()
            .slice(indices)

        for ((k, v) in spawnAreas) {
            +"§7>>> §3#$k: §f${v.label ?: "NO LABEL"}"
            +ClickEvent.suggestCommand("/fwde sa label id:${v.id} ")
            +interactiveRegionClickables(v, "sa")
        }

        +"\n".repeat(ITEMS_PER_PAGE - dungeon.spawnAreas.size)
        +paginator(page, floor(dungeon.spawnAreas.size / ITEMS_PER_PAGE.toDouble()).toInt(), "sa")
    }

    fun showChests(
        dungeon: EditableDungeon,
        page: Int
    ) = jsonMessage {
        +Strings.CHAT_HEADER
        +"§8======================[ §aChests §8]======================\n\n"

        val from = page * ITEMS_PER_PAGE
        val to = (page + 1) * ITEMS_PER_PAGE - 1
        val indices = from..to.coerceAtMost(dungeon.chests.size - 1)

        val chests = dungeon
            .chests
            .toList()
            .slice(indices)

        for ((k, v) in chests) {
            +"§7>>> §3#$k: §f${v.label ?: "NO LABEL"}"
            +ClickEvent.suggestCommand("/fwde c label id:${v.id} ")
            +"§f  ["
            +"§c X "
            +ClickEvent.runCommand("/fwde c remove ${v.id}")
            +"§f]\n"
        }

        +"\n".repeat(ITEMS_PER_PAGE - dungeon.activeAreas.size)
        +paginator(page, floor(dungeon.activeAreas.size / ITEMS_PER_PAGE.toDouble()).toInt(), "aa")
    }

    fun showTriggers(
        dungeon: EditableDungeon,
        page: Int
    ) = jsonMessage {
        +Strings.CHAT_HEADER
        +"§8=====================[ §6Triggers §8]=====================\n\n"

        val from = page * ITEMS_PER_PAGE
        val to = (page + 1) * ITEMS_PER_PAGE - 1
        val indices = from..to.coerceAtMost(dungeon.triggers.size - 1)

        val triggers = dungeon
            .triggers
            .toList()
            .slice(indices)

        for ((k, v) in triggers) {
            +"§7>>> §3#$k: §f${v.label ?: "NO LABEL"}"
            +ClickEvent.suggestCommand("/fwde t label id:${v.id} ")
            +interactiveRegionClickables(v, "t")
        }

        +"\n".repeat(ITEMS_PER_PAGE - dungeon.triggers.size)
        +paginator(page, dungeon.triggers.size / ITEMS_PER_PAGE, "t")
    }

    companion object {
        private const val ITEMS_PER_PAGE = 16
    }
}