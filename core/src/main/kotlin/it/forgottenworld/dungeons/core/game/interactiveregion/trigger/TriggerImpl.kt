package it.forgottenworld.dungeons.core.game.interactiveregion.trigger

import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.scripting.CodeParser
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player
import javax.annotation.Nullable

class TriggerImpl @Inject constructor(
    @Assisted override val id: Int,
    @Assisted override val box: Box,
    @Assisted override val effectCode: List<String>,
    @Assisted override val requiresWholeParty: Boolean,
    @Nullable @Assisted override var label: String?,
    codeParser: CodeParser
) : Trigger, Storage.Storable {

    private val effect = codeParser.parseScript(effectCode)

    override val origin: Vector3i
        get() = box.origin

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
        effect.invoke(instance)
    }
}