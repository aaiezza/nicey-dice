package io.github.aaiezza.dicerace

import io.github.aaiezza.boardgame.Game
import io.github.aaiezza.boardgame.Move
import io.github.aaiezza.boardgame.Player
import io.github.aaiezza.boardgame.Players
import io.github.aaiezza.dicerace.DiceRacePlayerState.Companion.asFinished
import kotlin.math.max

class PlaceCounter private constructor(val value: Int) {
    constructor() : this(1)

    fun inc(): PlaceCounter = PlaceCounter(value = value + 1)

    fun toPlace() = Place(value)
}

data class Place(val value: Int) {
    init {
        require(value > 1)
    }
}

data class ResetTurnsUntilObstacle(val value: Int) {
    init {
        require(value > 0)
    }

    fun reset() = TurnsUntilObstacle(value)
}

data class TurnsUntilObstacle(val value: Int) {
    init {
        require(value >= 0)
    }

    fun dec() = if (readyForObstacle()) error("Not able to decrement further") else TurnsUntilObstacle(this.value - 1)
    fun readyForObstacle() = value <= 0

}

data class Distance(val value: Int = 0) {
    init {
        require(value > 0)
    }

    operator fun plus(distance: Distance) = copy(value = value + distance.value)

    operator fun compareTo(goalDistance: GoalDistance) = this.value - goalDistance.value
    operator fun minus(obstacle: Obstacle) = Distance(max(0, value - obstacle.value))
}

data class GoalDistance(val distance: Distance) {
    constructor(value: Int) : this(Distance(value))

    val value
        get() = distance.value

    init {
        require(value > 0)
    }

    operator fun compareTo(distance: Distance) = this.value - distance.value
}

data class Obstacle(val value: Int) {
    init {
        require(value > 0)
    }
}

data class NumberOfRolls(val value: Int = 0) {
    init {
        require(value >= 0)
    }

    fun inc() = copy(value = value + 1)
}

data class Roll(val value: Int) {
    init {
        require(value > 0)
    }

    fun asDistance() = Distance(value)

    operator fun plus(other: Roll) = copy(value = value + other.value)

    companion object {
        fun random(): Roll = Roll((1..6).random())
    }
}

sealed class DiceRaceState(
    val placeCounter: PlaceCounter,
    val resetTurnsUntilObstacle: ResetTurnsUntilObstacle,
    val turnsUntilObstacle: TurnsUntilObstacle,
    val goalDistance: GoalDistance
) : Game.State {
    companion object {
        fun createInitialState(
            resetTurnsUntilObstacle: ResetTurnsUntilObstacle,
            goalDistance: GoalDistance
        ) = InitialState(resetTurnsUntilObstacle = resetTurnsUntilObstacle, goalDistance = goalDistance)
    }

    class InitialState internal constructor(
        resetTurnsUntilObstacle: ResetTurnsUntilObstacle,
        goalDistance: GoalDistance
    ) : DiceRaceState(
        placeCounter = PlaceCounter(),
        resetTurnsUntilObstacle = resetTurnsUntilObstacle,
        turnsUntilObstacle = resetTurnsUntilObstacle.reset(),
        goalDistance = goalDistance
    ), Game.State.GameDoesNextThing {
        fun moveToPromptUserForMove(): PromptUserForMove =
            PromptUserForMove(
                placeCounter = placeCounter,
                resetTurnsUntilObstacle = resetTurnsUntilObstacle,
                turnsUntilObstacle = turnsUntilObstacle,
                goalDistance = goalDistance
            )
    }

    class PromptUserForMove internal constructor(
        placeCounter: PlaceCounter,
        resetTurnsUntilObstacle: ResetTurnsUntilObstacle,
        turnsUntilObstacle: TurnsUntilObstacle,
        goalDistance: GoalDistance
    ) : DiceRaceState(
        placeCounter = placeCounter,
        resetTurnsUntilObstacle = resetTurnsUntilObstacle,
        turnsUntilObstacle = turnsUntilObstacle,
        goalDistance = goalDistance
    ), PlayerDoesNextThing {
        fun moveToPlayerHasChosenMove(chosenMove: DiceRacePlayerMove): PlayerHasChosenMove = PlayerHasChosenMove(
            chosenMove = chosenMove,
            resetTurnsUntilObstacle = resetTurnsUntilObstacle,
            placeCounter = placeCounter,
            turnsUntilObstacle = turnsUntilObstacle,
            goalDistance = goalDistance
        )
    }

    class PlayerHasChosenMove internal constructor(
        override val chosenMove: DiceRacePlayerMove,
        placeCounter: PlaceCounter,
        resetTurnsUntilObstacle: ResetTurnsUntilObstacle,
        turnsUntilObstacle: TurnsUntilObstacle,
        goalDistance: GoalDistance
    ) : DiceRaceState(
        placeCounter = placeCounter,
        resetTurnsUntilObstacle = resetTurnsUntilObstacle,
        turnsUntilObstacle = turnsUntilObstacle,
        goalDistance = goalDistance
    ), Game.State.PlayerHasChosenNextThing {
        fun moveToPromptGameToPushBackPlayers(): PromptGameToPushBackPlayers =
            PromptGameToPushBackPlayers(
                placeCounter = placeCounter,
                resetTurnsUntilObstacle = resetTurnsUntilObstacle,
                goalDistance = goalDistance
            )

        fun moveToPromptGameToPushBackPlayersAfterThisPlayerFinished(): PromptGameToPushBackPlayers =
            PromptGameToPushBackPlayers(
                placeCounter = placeCounter.inc(),
                resetTurnsUntilObstacle = resetTurnsUntilObstacle,
                goalDistance = goalDistance
            )

        fun moveToPromptUserForMove(): PromptUserForMove =
            PromptUserForMove(
                placeCounter = placeCounter,
                resetTurnsUntilObstacle = resetTurnsUntilObstacle,
                turnsUntilObstacle = turnsUntilObstacle.dec(),
                goalDistance = goalDistance
            )

        fun moveToPromptUserForMoveAfterThisPlayerFinished(): PromptUserForMove =
            PromptUserForMove(
                placeCounter = placeCounter.inc(),
                resetTurnsUntilObstacle = resetTurnsUntilObstacle,
                turnsUntilObstacle = turnsUntilObstacle.dec(),
                goalDistance = goalDistance
            )

        fun moveToCalculateWinners(winningPlayers: List<Player>): GameOver {
            require(winningPlayers.all { it.state is DiceRacePlayerState.Finished })
            return GameOver(
                winningPlayers = winningPlayers,
                placeCounter = placeCounter.inc(),
                resetTurnsUntilObstacle = resetTurnsUntilObstacle,
                turnsUntilObstacle = turnsUntilObstacle,
                goalDistance = goalDistance
            )
        }
    }

    class PromptGameToPushBackPlayers internal constructor(
        placeCounter: PlaceCounter,
        resetTurnsUntilObstacle: ResetTurnsUntilObstacle,
        goalDistance: GoalDistance
    ) : DiceRaceState(
        placeCounter = placeCounter,
        resetTurnsUntilObstacle = resetTurnsUntilObstacle,
        turnsUntilObstacle = TurnsUntilObstacle(0),
        goalDistance = goalDistance
    ), Game.State.GameDoesNextThing {
        fun moveToPushBackPlayersObstacleChosen(obstacleSupplier: () -> Obstacle = { Obstacle((1..4).random()) }): PushBackPlayersObstacleChosen =
            PushBackPlayersObstacleChosen(
                obstacle = obstacleSupplier(),
                placeCounter = placeCounter,
                resetTurnsUntilObstacle = resetTurnsUntilObstacle,
                goalDistance = goalDistance
            )
    }

    class PushBackPlayersObstacleChosen internal constructor(
        val obstacle: Obstacle,
        placeCounter: PlaceCounter,
        resetTurnsUntilObstacle: ResetTurnsUntilObstacle,
        goalDistance: GoalDistance
    ) : DiceRaceState(
        placeCounter = placeCounter,
        resetTurnsUntilObstacle = resetTurnsUntilObstacle,
        turnsUntilObstacle = TurnsUntilObstacle(0),
        goalDistance = goalDistance
    ), Game.State.GameDoesNextThing {
        fun moveToPromptUserForMove(): PromptUserForMove =
            PromptUserForMove(
                placeCounter = placeCounter,
                resetTurnsUntilObstacle = resetTurnsUntilObstacle,
                turnsUntilObstacle = resetTurnsUntilObstacle.reset(),
                goalDistance = goalDistance
            )
    }

    class GameOver internal constructor(
        val winningPlayers: List<Player>,
        placeCounter: PlaceCounter,
        resetTurnsUntilObstacle: ResetTurnsUntilObstacle,
        turnsUntilObstacle: TurnsUntilObstacle,
        goalDistance: GoalDistance
    ) : DiceRaceState(
        placeCounter = placeCounter,
        resetTurnsUntilObstacle = resetTurnsUntilObstacle,
        turnsUntilObstacle = turnsUntilObstacle,
        goalDistance = goalDistance
    ), Game.State.Terminal

    interface PlayerDoesNextThing : Game.State.PlayerDoesNextThing {
        override fun getExposedState(game: Game): Game.State.Exposable =
            DiceRaceExposableGameState(currentState = game.state as PromptUserForMove)
    }

}

data class DiceRaceExposableGameState(
    private val currentState: DiceRaceState.PromptUserForMove,
    val goalDistance: GoalDistance = (currentState as DiceRaceState).goalDistance,
) : Game.State.Exposable {
    val nextState: (Move.PlayerMove) -> Game.State
        get() = {
            currentState.moveToPlayerHasChosenMove(it as DiceRacePlayerMove)
        }
}

sealed interface DiceRacePlayerMove : Move.PlayerMove {
    companion object {
        private fun getWinnersComparator(game: Game): Comparator<Player> {
            val gameState = game.state as DiceRaceState

            return Comparator<Player> { l, r ->
                (l.state.asFinished().totalDistance.value - gameState.goalDistance.value) -
                        (r.state.asFinished().totalDistance.value - gameState.goalDistance.value)
            }.thenComparing { l, r ->
                l.state.asFinished().numbersOfRolls.value - r.state.asFinished().numbersOfRolls.value
            }.thenComparing { l, r ->
                l.state.asFinished().place.value - r.state.asFinished().place.value
            }
        }

        fun executeRoll(game: Game, roll: () -> Roll): Game {
            val gameState = game.state as DiceRaceState.PlayerHasChosenMove

            var players = game.players.map {
                if (it !is Player.Active) it else {
                    val playerState = (it.state as DiceRacePlayerState.StillPlaying).applyRoll(roll())
                    if (playerState.totalDistance >= gameState.goalDistance)
                        it.copy(state = playerState.asFinished(gameState.placeCounter.toPlace()))
                    else it.copy(state = playerState)
                }
            }.let { Players(it) }
            val isPlayerFinished = players[0].state is DiceRacePlayerState.Finished
            players.progressPlayers()

            val allPlayersDone = players.all { it.state is DiceRacePlayerState.Finished }
            fun proceed(state: DiceRaceState) = game.proceed(players = players, state = state)
            return if (allPlayersDone) {
                proceed(
                    gameState.moveToCalculateWinners(players.sortedWith(getWinnersComparator(game)))
                )
            } else {
                while (players.first().state is DiceRacePlayerState.Finished) {
                    players = players.progressPlayers()
                }
                if (gameState.turnsUntilObstacle.readyForObstacle()) {
                    proceed(
                        if (isPlayerFinished)
                            gameState.moveToPromptGameToPushBackPlayersAfterThisPlayerFinished()
                        else gameState.moveToPromptGameToPushBackPlayers()
                    )
                } else {
                    proceed(
                        if (isPlayerFinished)
                            gameState.moveToPromptUserForMoveAfterThisPlayerFinished()
                        else gameState.moveToPromptUserForMove()
                    )
                }
            }
        }
    }

    data object RollOneDie : DiceRacePlayerMove {
        override fun invoke(game: Game): Game = executeRoll(game) { Roll.random() }
    }

    data object RollTwoDice : DiceRacePlayerMove {
        override fun invoke(game: Game): Game = executeRoll(game) { Roll.random() + Roll.random() }
    }
}

sealed interface DiceRacePlayerState {
    val numbersOfRolls: NumberOfRolls
    val totalDistance: Distance
    val lastRoll: Roll?

    interface StillPlaying : DiceRacePlayerState, Player.State.StillPlaying {
        fun applyObstacle(obstacle: Obstacle): StillPlaying
        fun applyRoll(roll: Roll): StillPlaying
        fun asFinished(place: Place) = Finished(
            place = place, numbersOfRolls = numbersOfRolls,
            totalDistance = totalDistance, lastRoll = lastRoll
        )
    }

    data class Finished(
        val place: Place,
        override val numbersOfRolls: NumberOfRolls,
        override val totalDistance: Distance,
        override val lastRoll: Roll? = null
    ) : DiceRacePlayerState, Player.State.Finished

    companion object {
        fun Player.State.asFinished() = this as Finished

        inline fun <reified T> DiceRacePlayerState.ifFinished(then: (Finished) -> T, orElse: () -> T) =
            if (this is Finished) then(this) else orElse()
    }
}

class DiceRaceGameMover : Game.Mover {
    override fun invoke(game: Game): Move.GameMove {
        return with(game.state) {
            when (this as DiceRaceState) {
                is DiceRaceState.InitialState -> DiceRaceGameMove.InitiateGame()
                is DiceRaceState.PromptGameToPushBackPlayers -> DiceRaceGameMove.ChooseObstacleSize()
                is DiceRaceState.PushBackPlayersObstacleChosen -> DiceRaceGameMove.PushBackPlayers()
                else -> error("No other states to consider.")
            }
        }
    }

    sealed interface DiceRaceGameMove {
        class InitiateGame : Move.GameMove {
            override fun invoke(game: Game): Game = game.proceed(
                previousGame = game.previousGame,
                state = (game.state as DiceRaceState.InitialState)
                    .moveToPromptUserForMove()
            )
        }

        class ChooseObstacleSize : Move.GameMove {
            override fun invoke(game: Game): Game = game.proceed(
                state = (game.state as DiceRaceState.PromptGameToPushBackPlayers)
                    .moveToPushBackPlayersObstacleChosen()
            )
        }

        class PushBackPlayers : Move.GameMove {
            override fun invoke(game: Game): Game {
                val gameState = game.state as DiceRaceState.PushBackPlayersObstacleChosen
                return game.proceed(
                    players = game.players.map {
                        Player(it.username, with(it.state) {
                            if (this is DiceRacePlayerState.StillPlaying) {
                                applyObstacle(gameState.obstacle)
                            } else this
                        })
                    }.let { Players.createPlayersFromList(it) },
                    state = gameState.moveToPromptUserForMove()
                )
            }
        }
    }
}

class DiceRacePlayerMoveCalculator : Player.MoveCalculator {
    override fun invoke(game: Game): List<Move.PlayerMove> {
        return listOf(DiceRacePlayerMove.RollOneDie, DiceRacePlayerMove.RollTwoDice)
    }
}
