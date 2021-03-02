package it.forgottenworld.dungeons.core.game.interactiveregion

import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import it.forgottenworld.dungeons.api.game.instance.DungeonInstance
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.scripting.CodeParser
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class TriggerImpl @Inject constructor(
    @Assisted override val id: Int,
    @Assisted override val box: Box,
    @Assisted override val effectCode: List<String>,
    @Assisted override val requiresWholeParty: Boolean,
    @Assisted override var label: String?,
    codeParser: CodeParser,
    private val triggerFactory: TriggerFactory
) : Trigger, Storage.Storable {

    private val effect = codeParser.parseScript(effectCode)

    override val origin: Vector3i
        get() = box.origin

    override fun containsXYZ(x: Int, y: Int, z: Int) = box.containsXYZ(x, y, z)

    override fun debugLogEnter(player: Player) {
        player.sendFWDMessage(
            Strings.DEBUG_ENTERED_TRIGGER
                .format(label?.plus(" ") ?: "", id)
        )
    }

    override fun debugLogExit(player: Player) {
        player.sendFWDMessage(
            Strings.DEBUG_EXITED_TRIGGER
                .format(label?.plus(" ") ?: "", id)
        )
    }

    override fun withContainerOrigin(oldOrigin: Vector3i, newOrigin: Vector3i) = triggerFactory.create(
        id,
        box.withContainerOrigin(oldOrigin, newOrigin),
        effectCode,
        requiresWholeParty,
        label,
    )

    override fun withContainerOriginZero(oldOrigin: Vector3i) = withContainerOrigin(oldOrigin, Vector3i.ZERO)

    override fun executeEffect(instance: DungeonInstance) {
        effect.invoke(instance)
    }
}