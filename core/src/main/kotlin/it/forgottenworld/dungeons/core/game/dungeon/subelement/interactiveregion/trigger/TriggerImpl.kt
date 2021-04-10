package it.forgottenworld.dungeons.core.game.dungeon.subelement.interactiveregion.trigger

import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player
import javax.annotation.Nullable

class TriggerImpl @Inject constructor(
    @Assisted override val id: Int,
    @Assisted override val box: Box,
    @Assisted override val requiresWholeParty: Boolean,
    @Nullable @Assisted override var label: String?
) : Trigger, Storage.Storable {

    override var effect: Trigger.Effect? = null

    override fun containsXYZ(x: Int, y: Int, z: Int) = box.containsXYZ(x, y, z)

    override fun debugLogEnter(player: Player) {
        player.sendPrefixedMessage(
            Strings.DEBUG_ENTERED_TRIGGER,
            label?.plus(" ") ?: "",
            id
        )
    }

    override fun debugLogExit(player: Player) {
        player.sendPrefixedMessage(
            Strings.DEBUG_EXITED_TRIGGER,
            label?.plus(" ") ?: "",
            id
        )
    }

    override fun executeEffect(instance: DungeonInstance) {
        effect?.execute(instance)
    }
}