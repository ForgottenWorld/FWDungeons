package it.forgottenworld.dungeons.core.cli

import it.forgottenworld.dungeons.api.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.core.storage.Strings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor

class ActiveAreaPaginatedGui(private val editableDungeon: EditableDungeon) : DungeonSubElementPaginatedGui<ActiveArea>(
    TextComponent.ofChildren(
        Component.text(Strings.CHAT_HEADER),
        Component.text("===================[ ", NamedTextColor.DARK_GRAY),
        Component.text("Active Areas", NamedTextColor.BLUE),
        Component.text(" ]===================\n\n", NamedTextColor.DARK_GRAY)
    ),
    "aa"
) {

    override val elements: List<ActiveArea>
        get() = editableDungeon.activeAreas.values.toList()
}