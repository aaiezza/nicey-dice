package io.github.aaiezza.boardgame

sealed interface Move {
    operator fun invoke(game: Game): Game

    fun interface GameMove: Move
    fun interface PlayerMove: Move
}

