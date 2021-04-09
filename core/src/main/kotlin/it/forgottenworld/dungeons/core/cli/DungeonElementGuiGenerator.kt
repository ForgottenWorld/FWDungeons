package it.forgottenworld.dungeons.core.cli

import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.core.storage.Strings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor

@Singleton
class DungeonElementGuiGenerator {

    private fun interactiveRegionClickables(
        interactiveEl: InteractiveRegion,
        type: String
    ) = TextComponent.ofChildren(
        Component.text("  [", NamedTextColor.WHITE),
        Component.text(" HL ", NamedTextColor.GREEN)
            .clickEvent(ClickEvent.runCommand("/fwde $type hl ${interactiveEl.id}")),
        Component.text("] [", NamedTextColor.WHITE),
        Component.text(" X ", NamedTextColor.RED)
            .clickEvent(ClickEvent.runCommand("/fwde $type unmake ${interactiveEl.id}")),
        Component.text("]\n", NamedTextColor.WHITE)
    )

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
    ) = TextComponent.ofChildren(
        if (page > 0) {
            TextComponent.ofChildren(
                Component.text("=[ ", NamedTextColor.DARK_GRAY),
                pageClickable("<<<<", page - 1, type),
                Component.text(" ]=", NamedTextColor.DARK_GRAY),
            )
        } else {
            Component.text("========", NamedTextColor.DARK_GRAY)
        },
        Component.text("=====================================", NamedTextColor.DARK_GRAY),
        if (page < maxPage) {
            TextComponent.ofChildren(
                Component.text("=[ ", NamedTextColor.DARK_GRAY),
                pageClickable(">>>>", page + 1, type),
                Component.text(" ]=", NamedTextColor.DARK_GRAY),
            )
        } else {
            Component.text("========", NamedTextColor.DARK_GRAY)
        }
    )

    fun showActiveAreas(
        dungeon: EditableDungeon,
        page: Int
    ) = Component.text { builder ->
        builder.append(
            TextComponent.ofChildren(
                Component.text(Strings.CHAT_HEADER),
                Component.text("===================[ ", NamedTextColor.DARK_GRAY),
                Component.text("Active Areas", NamedTextColor.BLUE),
                Component.text(" ]===================\n\n", NamedTextColor.DARK_GRAY)
            )
        )

        val from = page * ITEMS_PER_PAGE
        val to = (page + 1) * ITEMS_PER_PAGE - 1
        val indices = from..to.coerceAtMost(dungeon.activeAreas.size - 1)

        val activeAreas = dungeon
            .activeAreas
            .toList()
            .slice(indices)

        for ((k, v) in activeAreas) {
            builder.append(
                TextComponent.ofChildren(
                    Component.text(">>> ", NamedTextColor.GRAY),
                    Component.text("#$k: ", NamedTextColor.DARK_AQUA),
                    Component.text(v.label ?: "NO LABEL", NamedTextColor.WHITE)
                        .clickEvent(ClickEvent.suggestCommand("/fwde aa label id:${v.id} ")),
                    interactiveRegionClickables(v, "aa")
                )
            )
        }

        builder.append(Component.text("\n".repeat(ITEMS_PER_PAGE - activeAreas.size)))
        builder.append(paginator(page, (dungeon.activeAreas.size - 1) / ITEMS_PER_PAGE, "aa"))
    }

    fun showSpawnAreas(
        dungeon: EditableDungeon,
        page: Int
    ) = Component.text { builder ->
        builder.append(
            TextComponent.ofChildren(
                Component.text(Strings.CHAT_HEADER),
                Component.text("====================[ ", NamedTextColor.DARK_GRAY),
                Component.text("Spawn Areas", NamedTextColor.LIGHT_PURPLE),
                Component.text(" ]===================\n\n", NamedTextColor.DARK_GRAY)
            )
        )

        val from = page * ITEMS_PER_PAGE
        val to = (page + 1) * ITEMS_PER_PAGE - 1
        val indices = from..to.coerceAtMost(dungeon.spawnAreas.size - 1)

        val spawnAreas = dungeon
            .spawnAreas
            .toList()
            .slice(indices)

        for ((k, v) in spawnAreas) {
            builder.append(
                TextComponent.ofChildren(
                    Component.text(">>> ", NamedTextColor.GRAY),
                    Component.text("#$k: ", NamedTextColor.DARK_AQUA),
                    Component.text(v.label ?: "NO LABEL", NamedTextColor.WHITE)
                        .clickEvent(ClickEvent.suggestCommand("/fwde sa label id:${v.id} ")),
                    interactiveRegionClickables(v, "sa")
                )
            )
        }

        builder.append(Component.text("\n".repeat(ITEMS_PER_PAGE - spawnAreas.size)))
        builder.append(paginator(page, (dungeon.spawnAreas.size - 1) / ITEMS_PER_PAGE, "sa"))
    }

    fun showChests(
        dungeon: EditableDungeon,
        page: Int
    ) = Component.text { builder ->
        builder.append(
            TextComponent.ofChildren(
                Component.text(Strings.CHAT_HEADER),
                Component.text("======================[ ", NamedTextColor.DARK_GRAY),
                Component.text("Chests", NamedTextColor.GREEN),
                Component.text(" ]======================\n\n", NamedTextColor.DARK_GRAY)
            )
        )

        val from = page * ITEMS_PER_PAGE
        val to = (page + 1) * ITEMS_PER_PAGE - 1
        val indices = from..to.coerceAtMost(dungeon.chests.size - 1)

        val chests = dungeon
            .chests
            .toList()
            .slice(indices)

        for ((k, v) in chests) {
            builder.append(
                TextComponent.ofChildren(
                    Component.text(">>> ", NamedTextColor.GRAY),
                    Component.text("#$k: ", NamedTextColor.DARK_AQUA),
                    Component.text(v.label ?: "NO LABEL", NamedTextColor.WHITE)
                        .clickEvent(ClickEvent.suggestCommand("/fwde c label id:${v.id} ")),
                    Component.text("  [", NamedTextColor.WHITE),
                    Component.text(" X ", NamedTextColor.RED)
                        .clickEvent(ClickEvent.runCommand("/fwde c remove ${v.id}")),
                    Component.text("]\n", NamedTextColor.WHITE)
                )
            )
        }

        builder.append(Component.text("\n".repeat(ITEMS_PER_PAGE - chests.size)))
        builder.append(paginator(page, (dungeon.chests.size - 1) / ITEMS_PER_PAGE, "c"))
    }

    fun showTriggers(
        dungeon: EditableDungeon,
        page: Int
    ) = Component.text { builder ->
        builder.append(
            TextComponent.ofChildren(
                Component.text(Strings.CHAT_HEADER),
                Component.text("=====================[ ", NamedTextColor.DARK_GRAY),
                Component.text("Triggers", NamedTextColor.GOLD),
                Component.text(" ]=====================\n\n", NamedTextColor.DARK_GRAY)
            )
        )

        val from = page * ITEMS_PER_PAGE
        val to = (page + 1) * ITEMS_PER_PAGE - 1
        val indices = from..to.coerceAtMost(dungeon.triggers.size - 1)

        val triggers = dungeon
            .triggers
            .toList()
            .slice(indices)

        for ((k, v) in triggers) {
            builder.append(
                TextComponent.ofChildren(
                    Component.text(">>> ", NamedTextColor.GRAY),
                    Component.text("#$k: ", NamedTextColor.DARK_AQUA),
                    Component.text(v.label ?: "NO LABEL", NamedTextColor.WHITE)
                        .clickEvent(ClickEvent.suggestCommand("/fwde t label id:${v.id} ")),
                    interactiveRegionClickables(v, "t")
                )
            )
        }

        builder.append(Component.text("\n".repeat(ITEMS_PER_PAGE - triggers.size)))
        builder.append(paginator(page, (dungeon.triggers.size - 1) / ITEMS_PER_PAGE, "t"))
    }

    companion object {

        private const val ITEMS_PER_PAGE = 16
    }
}