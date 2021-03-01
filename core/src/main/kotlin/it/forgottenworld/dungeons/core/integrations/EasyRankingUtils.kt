package it.forgottenworld.dungeons.core.integrations

import com.google.inject.Inject
import it.forgottenworld.dungeons.core.config.Configuration
import it.forgottenworld.dungeons.core.config.Strings
import me.kaotich00.easyranking.api.board.Board
import me.kaotich00.easyranking.service.ERBoardService
import org.bukkit.Bukkit
import java.util.*

class EasyRankingUtils @Inject constructor(
    private val configuration: Configuration
) {

    private val erBoardService get() = ERBoardService.getInstance()

    fun checkEasyRankingIntegration() {
        val logger = Bukkit.getLogger()
        logger.info("Checking for EasyRanking integration...")
        if (!configuration.easyRankingIntegration) {
            logger.info("EasyRanking integration is not enabled")
            return
        }

        logger.info("EasyRanking integration is enabled")
        if (Bukkit.getPluginManager().getPlugin("Easyranking") == null) {
            logger.info("EasyRanking is not present")
            return
        }

        logger.info("EasyRanking is present")
        configuration.useEasyRanking = true
    }

    private fun getBoard(): Board = erBoardService
        .getBoardById("dungeons")
        .orElse(
            erBoardService.createBoard(
                "dungeons",
                Strings.LEADERBOARD_TITLE,
                Strings.LEADERBOARD_DESCR,
                100,
                Strings.LEADERBOARD_POINTS,
                false
            )
        )

    fun addScoreToPlayer(uuid: UUID, score: Float) {
        erBoardService.addScoreToPlayer(getBoard(), uuid, score)
    }
}