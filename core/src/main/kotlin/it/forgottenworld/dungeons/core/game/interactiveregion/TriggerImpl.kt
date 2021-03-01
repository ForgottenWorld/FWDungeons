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

data class TriggerImpl @Inject constructor(
    @Assisted override val id: Int,
    @Assisted override val box: Box,
    @Assisted override val effectCode: List<String>,
    @Assisted override val requiresWholeParty: Boolean,
    @Assisted override var label: String?
) : Trigger, Storage.Storable {

    private val effect = CodeParser.parseScript(effectCode)

    override val origin: Vector3i
        get() = box.origin

    override fun containsXYZ(x: Int, y: Int, z: Int) = box.containsXYZ(x, y, z)

    fun debugLogEnter(player: Player) {
        player.sendFWDMessage(
            Strings.DEBUG_ENTERED_TRIGGER
                .format(label?.plus(" ") ?: "", id)
        )
    }

    fun debugLogExit(player: Player) {
        player.sendFWDMessage(
            Strings.DEBUG_EXITED_TRIGGER
                .format(label?.plus(" ") ?: "", id)
        )
    }

    override fun withContainerOrigin(oldOrigin: Vector3i, newOrigin: Vector3i) = copy(
        box = box.withContainerOrigin(oldOrigin, newOrigin)
    )

    override fun withContainerOriginZero(oldOrigin: Vector3i) = copy(
        box = box.withContainerOriginZero(oldOrigin)
    )

    override fun executeEffect(instance: DungeonInstance) {
        effect.invoke(instance)
    }
}