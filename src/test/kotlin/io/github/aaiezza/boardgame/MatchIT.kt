package io.github.aaiezza.boardgame

import io.github.aaiezza.boardgame.RollerGameState.Companion.toPlayerMustRollState
import io.github.aaiezza.boardgame.RollerPlayerState.Companion.asFinished
import io.github.aaiezza.boardgame.RollerPlayerState.Companion.asRollerPlayerState
import io.github.aaiezza.boardgame.RollerPlayerState.Companion.ifFinished
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.math.max

class MatchIT {
    private lateinit var subject: Match

    @BeforeEach
    fun setUp() {
        subject = Match(
            Game(
                players = Players.createPlayersFromList(
                    listOf(
                        Player(Player.Username("A"), RollerPlayerState.StillPlaying(0, 0)),
                        Player(Player.Username("B"), RollerPlayerState.StillPlaying(0, 0)),
                        Player(Player.Username("C"), RollerPlayerState.StillPlaying(0, 0)),
                        Player(Player.Username("D"), RollerPlayerState.StillPlaying(0, 0)),
                        Player(Player.Username("E"), RollerPlayerState.StillPlaying(0, 0)),
                        Player(Player.Username("F"), RollerPlayerState.StillPlaying(0, 0)),
                        Player(Player.Username("G"), RollerPlayerState.StillPlaying(0, 0)),
                        Player(Player.Username("H"), RollerPlayerState.StillPlaying(0, 0)),
                        Player(Player.Username("I"), RollerPlayerState.StillPlaying(0, 0)),
                        Player(Player.Username("J"), RollerPlayerState.StillPlaying(0, 0)),
                        Player(Player.Username("K"), RollerPlayerState.StillPlaying(0, 0)),
                        Player(Player.Username("L"), RollerPlayerState.StillPlaying(0, 0)),
                        Player(Player.Username("M"), RollerPlayerState.StillPlaying(0, 0)),
                        Player(Player.Username("N"), RollerPlayerState.StillPlaying(0, 0)),
                        Player(Player.Username("O"), RollerPlayerState.StillPlaying(0, 0)),
                        Player(Player.Username("P"), RollerPlayerState.StillPlaying(0, 0)),
                    )
                ),
                state = RollerGameState.PlayerMustRoll(resetTurnsUntilObstacle = 10, goal = 30),
                gameMover = RollerGameMover(),
                playerMoveCalculator = RollerPlayerMoveCalculator()
            )
        )
    }

    @Test
    fun `should work`() {
        val result = subject.play()

        generateSequence(result) { game ->
            game.previousGame
        }.map { game ->
            val output = StringBuilder()
            with(game.state) {
                if (this is RollerGameState.PlayerWon) {
                    output.append("Winner(s): ${this.winningPlayers.joinToString(", ") { it.username.value }}\n")
                }
            }
            output.append("%02d - ${game.state::class.simpleName}")
            with(game.state) {
                when (this) {
                    is RollerGameState.PlayerHasChosenMove -> output.append(" - ${this.chosenMove::class.simpleName}")
                    is RollerGameState.PushBackPlayersObstacleChosen -> output.append(" - Obstacle: ${this.obstacle}")
                    else -> {}
                }
            }
            output.append("\n")
            game.players.forEach {
                val playerState = it.state.asRollerPlayerState()
                output.append(
                    " ${
                        it.state.ifFinished(
                            then = { p -> "✓ ${p.place}" },
                            orElse = { "   " })
                    } ${it.username.value} | distance: ${playerState.totalDistance} rolls: ${playerState.numbersOfRolls}\n"
                )
            }
            output.toString()
        }.toList().reversed()
            .forEachIndexed { i, gameString ->
                println(gameString.format(i))
            }
    }
}


/* Silly game where players take turns rolling a dice until her cumulative rolls reach 20 with the fewest moves */

sealed interface RollerPlayerState {
    val numbersOfRolls: Int
    val totalDistance: Int
    val lastRoll: Int?

    data class StillPlaying(
        override val numbersOfRolls: Int,
        override val totalDistance: Int,
        override val lastRoll: Int? = null
    ) :
        RollerPlayerState, Player.State.StillPlaying {
        override fun chooseMove(
            exposableGameState: Game.State.Exposable,
            availableMoves: List<Move.PlayerMove>,
        ): Pair<Move.PlayerMove, (Move.PlayerMove) -> Game.State> {
            exposableGameState as RollerExposableGameState
            val chosenMove = availableMoves.first {
                if (it is RollerPlayerMove) {
                    val delta = exposableGameState.goal - totalDistance
                    when (it) {
                        is RollerPlayerMove.RollOneDie -> delta <= 6
                        is RollerPlayerMove.RollTwoDice -> delta > 6
                    }
                } else false
            }
            return chosenMove to { move ->
                move as RollerPlayerMove
                RollerGameState.PlayerHasChosenMove(
                    chosenMove = move,
                    placeCounter = exposableGameState.placeCounter,
                    resetTurnsUntilObstacle = exposableGameState.resetTurnsUntilObstacle,
                    turnsUntilObstacle = exposableGameState.turnsUntilObstacle,
                    goal = exposableGameState.goal
                )
            }
        }
    }

    data class Finished(
        val place: Int,
        override val numbersOfRolls: Int,
        override val totalDistance: Int,
        override val lastRoll: Int = -1
    ) :
        RollerPlayerState, Player.State.Finished {
    }

    companion object {
        fun Player.State.asFinished() = this as Finished

        inline fun <reified T> Player.State.ifFinished(then: (Finished) -> T, orElse: () -> T) =
            if (this is Finished) then(this) else orElse()

        fun Player.State.asRollerPlayerState(): RollerPlayerState = this as RollerPlayerState
    }
}

sealed interface RollerPlayerMove : Move.PlayerMove {
    companion object {
        private fun checkForGameMove(game: Game): Game {
            val gameState = game.state as RollerGameState
            return if (gameState.turnsUntilObstacle <= 0) {
                return game.proceed(
                    previousGame = game.previousGame!!,
                    state = RollerGameState.PushBackPlayers(
                        placeCounter = gameState.placeCounter,
                        resetTurnsUntilObstacle = gameState.resetTurnsUntilObstacle,
                        goal = gameState.goal
                    )
                )
            } else game
        }

        private fun checkForWinner(game: Game): Game {
            val gameState = game.state as RollerGameState
            val allPlayersDone = game.players.all { it.state is RollerPlayerState.Finished }

            return if (allPlayersDone) {
                val comparator = Comparator<Player> { l, r ->
                    (l.state.asFinished().totalDistance - gameState.goal) -
                            (r.state.asFinished().totalDistance - gameState.goal)
                }.thenComparing { l, r ->
                    l.state.asFinished().numbersOfRolls - r.state.asFinished().numbersOfRolls
                }.thenComparing { l, r ->
                    l.state.asFinished().place - r.state.asFinished().place
                }

                game.proceed(
                    previousGame = game.previousGame!!,
                    state = RollerGameState.PlayerWon(
                        winningPlayers = game.players.sortedWith(comparator),
                        gameState.placeCounter,
                        gameState.resetTurnsUntilObstacle,
                        gameState.turnsUntilObstacle,
                        gameState.goal
                    )
                )
            } else game
        }

        fun checkForWinnerOrGameMove(game: Game): Game {
            with(checkForWinner(game)) { if (this != game) return this }
            with(checkForGameMove(game)) { if (this != game) return this }
            return game
        }

        fun executeRoll(game: Game, roll: () -> Int): Game {
            with(checkForWinnerOrGameMove(game)) { if (this != game) return this }

            val gameState = game.state as RollerGameState
            var gameStatePlace = gameState.placeCounter

            var players = game.players.map {
                if (it !is Player.Active) it else {
                    val playerState = it.state.asRollerPlayerState()

                    val roll = roll()
                    if (playerState.totalDistance + roll >= gameState.goal) {
                        gameStatePlace = gameState.placeCounter + 1
                        it.copy(
                            state = RollerPlayerState.Finished(
                                place = gameState.placeCounter,
                                numbersOfRolls = playerState.numbersOfRolls + 1,
                                lastRoll = roll,
                                totalDistance = playerState.totalDistance + roll
                            )
                        )
                    } else {
                        it.copy(
                            state = RollerPlayerState.StillPlaying(
                                numbersOfRolls = playerState.numbersOfRolls + 1,
                                lastRoll = roll,
                                totalDistance = playerState.totalDistance + roll
                            )
                        )
                    }
                }
            }.let { Players(it) }.progressPlayers()

            var nextGame = game.proceed(
                players = players,
                state = gameState.toPlayerMustRollState(
                    placeCounter = gameStatePlace,
                    turnsUntilObstacle = gameState.turnsUntilObstacle - 1,
                )
            )
            with(checkForWinner(nextGame)) { if (this != nextGame) return this }

            while (players.first().state is RollerPlayerState.Finished) {
                players = players.progressPlayers()
            }
            nextGame = nextGame.proceed(nextGame.previousGame, players = players)

            return with(checkForGameMove(nextGame)) {
                if (this != nextGame) this else nextGame
            }
        }
    }

    data object RollOneDie : RollerPlayerMove {
        override fun invoke(game: Game): Game = executeRoll(game) { (1..6).random() }
    }

    data object RollTwoDice : RollerPlayerMove {
        override fun invoke(game: Game): Game = executeRoll(game) { (1..6).random() + (1..6).random() }
    }
}

data class RollerExposableGameState(
    val players: Players,
    val placeCounter: Int,
    val resetTurnsUntilObstacle: Int,
    val turnsUntilObstacle: Int,
    val goal: Int
) : Game.State.Exposable

sealed class RollerGameState(
    val placeCounter: Int = 1,
    val resetTurnsUntilObstacle: Int,
    val turnsUntilObstacle: Int,
    val goal: Int
) {
    fun getExposedState(game: Game): Game.State.Exposable {
        return RollerExposableGameState(game.players, placeCounter, resetTurnsUntilObstacle, turnsUntilObstacle, goal)
    }

    companion object {
        fun RollerGameState.toPlayerMustRollState(
            placeCounter: Int = this.placeCounter,
            resetTurnsUntilObstacle: Int = this.resetTurnsUntilObstacle,
            turnsUntilObstacle: Int = this.turnsUntilObstacle,
            goal: Int = this.goal
        ) = PlayerMustRoll(
            placeCounter = placeCounter,
            resetTurnsUntilObstacle = resetTurnsUntilObstacle,
            turnsUntilObstacle = turnsUntilObstacle,
            goal = goal
        )
    }

    class PushBackPlayers(placeCounter: Int, resetTurnsUntilObstacle: Int, goal: Int) :
        RollerGameState(placeCounter, resetTurnsUntilObstacle, 0, goal),
        Game.State.GameDoesNextThing

    class PushBackPlayersObstacleChosen(val obstacle: Int, placeCounter: Int, resetTurnsUntilObstacle: Int, goal: Int) :
        RollerGameState(placeCounter, resetTurnsUntilObstacle, 0, goal),
        Game.State.GameDoesNextThing

    class PlayerMustRoll(
        placeCounter: Int = 1,
        resetTurnsUntilObstacle: Int,
        turnsUntilObstacle: Int = resetTurnsUntilObstacle,
        goal: Int
    ) :
        RollerGameState(placeCounter, resetTurnsUntilObstacle, turnsUntilObstacle, goal), Game.State.PlayerDoesNextThing

    class PlayerHasChosenMove(
        override val chosenMove: RollerPlayerMove,
        placeCounter: Int,
        resetTurnsUntilObstacle: Int,
        turnsUntilObstacle: Int,
        goal: Int
    ) :
        RollerGameState(placeCounter, resetTurnsUntilObstacle, turnsUntilObstacle, goal),
        Game.State.PlayerHasChosenNextThing

    class PlayerWon(
        val winningPlayers: List<Player>,
        placeCounter: Int,
        resetTurnsUntilObstacle: Int,
        turnsUntilObstacle: Int,
        goal: Int
    ) :
        RollerGameState(placeCounter, resetTurnsUntilObstacle, turnsUntilObstacle, goal), Game.State.Terminal
}

class RollerGameMover : Game.Mover {
    override fun invoke(game: Game): Move.GameMove {
        return with(game.state) {
            when (this) {
                is RollerGameState.PushBackPlayers -> RollerGameMove.ChooseObstacleSize()
                is RollerGameState.PushBackPlayersObstacleChosen -> RollerGameMove.PushBackPlayers(this.obstacle)
                else -> error("Not a thing")
            }
        }
    }

    sealed interface RollerGameMove {
        class ChooseObstacleSize : Move.GameMove {
            override fun invoke(game: Game): Game {
                return game.proceed(
                    state = (game.state as RollerGameState.PushBackPlayers)
                        .let {
                            RollerGameState.PushBackPlayersObstacleChosen(
                                obstacle = (1..4).random(),
                                placeCounter = it.placeCounter,
                                resetTurnsUntilObstacle = it.resetTurnsUntilObstacle,
                                goal = it.goal
                            )
                        }
                )
            }
        }

        class PushBackPlayers(val obstacle: Int) : Move.GameMove {
            override fun invoke(game: Game): Game {
                val gameState = game.state as RollerGameState
                return game.proceed(
                    players = game.players.map {
                        Player(it.username, with(it.state) {
                            if (this is RollerPlayerState.StillPlaying) {
                                copy(totalDistance = max(0, totalDistance - obstacle))
                            } else this
                        })
                    }.let { Players.createPlayersFromList(it) },
                    state = gameState.toPlayerMustRollState(turnsUntilObstacle = gameState.resetTurnsUntilObstacle),
                )
            }
        }
    }
}

class RollerPlayerMoveCalculator : Player.MoveCalculator {
    override fun invoke(game: Game): List<Move.PlayerMove> {
        return listOf(RollerPlayerMove.RollOneDie, RollerPlayerMove.RollTwoDice)
    }
}
