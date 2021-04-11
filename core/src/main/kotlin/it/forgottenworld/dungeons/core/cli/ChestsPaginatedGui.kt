package it.forgottenworld.dungeons.core.cli

import it.forgottenworld.dungeons.api.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.api.game.dungeon.subelement.chest.Chest
import it.forgottenworld.dungeons.core.storage.Strings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor

class ChestsPaginatedGui(private val editableDungeon: EditableDungeon) : DungeonSubElementPaginatedGui<Chest>(
    TextComponent.ofChildren(
        Component.text(Strings.CHAT_HEADER),
        Component.text("======================[ ", NamedTextColor.DARK_GRAY),
        Component.text("Chests", NamedTextColor.GREEN),
        Component.text(" ]======================\n\n", NamedTextColor.DARK_GRAY)
    ),
    "c"
) {

    override val elements: List<Chest>
        get() = editableDungeon.chests.values.toList()
}