package it.forgottenworld.dungeons.core.integrations

import com.google.inject.Inject
import it.forgottenworld.dungeons.core.config.Configuration
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.utils.sendConsoleMessage
import me.kaotich00.easyranking.service.ERBoardService
import org.bukkit.Bukkit
import java.util.*

class EasyRankingUtils @Inject constructor(
    private val configuration: Configuration
) {

    fun checkEasyRankingIntegration() {
        sendConsoleMessage("${Strings.CONSOLE_PREFIX}Checking for EasyRanking integration...")
        if (!configuration.easyRankingIntegration) {
            sendConsoleMessage(" -- EasyRanking integration is §4not enabled")
            return
        }

        sendConsoleMessage(" -- EasyRanking integration §2is enabled")
        if (Bukkit.getPluginManager().getPlugin("Easyranking") == null) {
            sendConsoleMessage(" -- EasyRanking is §4not present")
            return
        }

        sendConsoleMessage(" -- EasyRanking §2is present")
        configuration.useEasyRanking = true
    }

    fun addScoreToPlayer(uuid: UUID, score: Float) {
        val board = ERBoardService
            .getInstance()
            .getBoardById("dungeons")
            .orElse(
                ERBoardService.getInstance().createBoard(
                    "dungeons",
                    Strings.LEADERBOARD_TITLE,
                    Strings.LEADERBOARD_DESCR,
                    100,
                    Strings.LEADERBOARD_POINTS,
                    false
                )
            )
        ERBoardService
            .getInstance()
            .addScoreToPlayer(board, uuid, score)
    }
}