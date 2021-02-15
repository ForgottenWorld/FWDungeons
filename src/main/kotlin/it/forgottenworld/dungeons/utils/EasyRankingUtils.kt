package it.forgottenworld.dungeons.utils

import it.forgottenworld.dungeons.config.Strings
import me.kaotich00.easyranking.api.board.Board
import me.kaotich00.easyranking.service.ERBoardService
import java.util.*

object EasyRankingUtils {

    private val erBoardService get() = ERBoardService.getInstance()

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