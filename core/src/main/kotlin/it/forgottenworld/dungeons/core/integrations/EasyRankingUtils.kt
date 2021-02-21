package it.forgottenworld.dungeons.core.integrations

import it.forgottenworld.dungeons.core.config.ConfigManager
import it.forgottenworld.dungeons.core.config.Strings
import me.kaotich00.easyranking.api.board.Board
import me.kaotich00.easyranking.service.ERBoardService
import org.bukkit.Bukkit
import java.util.*

object EasyRankingUtils {

    private val erBoardService get() = ERBoardService.getInstance()

    fun checkEasyRankingIntegration() {
        val logger = Bukkit.getLogger()
        logger.info("Checking for EasyRanking integration...")
        if (!ConfigManager.easyRankingIntegration) {
            logger.info("EasyRanking integration is not enabled")
            return
        }

        logger.info("EasyRanking integration is enabled")
        if (Bukkit.getPluginManager().getPlugin("Easyranking") == null) {
            logger.info("EasyRanking is not present")
            return
        }

        logger.info("EasyRanking is present")
        ConfigManager.useEasyRanking = true
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