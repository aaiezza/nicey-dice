package io.github.aaiezza.dicerace

import io.github.aaiezza.boardgame.*
import io.github.aaiezza.dicerace.DiceRacePlayerState.Companion.ifFinished
import io.github.aaiezza.dicerace.DiceRacePlayerState.StillPlaying
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class MatchIT {
    private lateinit var subject: Match

    @BeforeEach
    fun setUp() {
        subject = Match(
            Game(
                players = Players.createPlayersFromList(
                    listOf(
                        "A".asUsername().asCarefulDiceRacePlayer(),
                        "B".asUsername().asCarefulDiceRacePlayer(),
                        "C".asUsername().asCarefulDiceRacePlayer(),
                        "D".asUsername().asCarefulDiceRacePlayer(),
                        "E".asUsername().asCarefulDiceRacePlayer(),
                        "F".asUsername().asCarefulDiceRacePlayer(),
                        "G".asUsername().asCarefulDiceRacePlayer(),
                        "H".asUsername().asCarefulDiceRacePlayer(),
                        "I".asUsername().asCarefulDiceRacePlayer(),
                        "J".asUsername().asCarefulDiceRacePlayer(),
                        "K".asUsername().asCarefulDiceRacePlayer(),
                        "L".asUsername().asCarefulDiceRacePlayer(),
                        "M".asUsername().asCarefulDiceRacePlayer(),
                        "N".asUsername().asCarefulDiceRacePlayer(),
                        "O".asUsername().asCarefulDiceRacePlayer(),
                        "P".asUsername().asCarefulDiceRacePlayer(),
                    )
                ),
                state = DiceRaceState.createInitialState(
                    resetTurnsUntilObstacle = ResetTurnsUntilObstacle(10),
                    goalDistance = GoalDistance(30)
                ),
                gameMover = DiceRaceGameMover(),
                playerMoveCalculator = DiceRacePlayerMoveCalculator()
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
                if (this is DiceRaceState.GameOver) {
                    output.append("Winner(s): ${this.winningPlayers.joinToString(", ") { it.username.value }}\n")
                }
            }
            output.append("%02d - ${game.state::class.simpleName}")
            with(game.state) {
                when (this) {
                    is DiceRaceState.PlayerHasChosenMove -> output.append(" - ${this.chosenMove::class.simpleName}")
                    is DiceRaceState.PushBackPlayersObstacleChosen -> output.append(" - Obstacle: ${this.obstacle}")
                    else -> {}
                }
            }
            output.append("\n")
            game.players.forEach {
                val playerState = it.state as DiceRacePlayerState
                output.append(
                    " ${
                        playerState.ifFinished(
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

fun String.asUsername() = Player.Username(this)
fun Player.Username.asCarefulDiceRacePlayer() = Player(username = this, state = CarefulDiceRacePlayerState())

data class CarefulDiceRacePlayerState(
    override val numbersOfRolls: NumberOfRolls = NumberOfRolls(),
    override val totalDistance: Distance = Distance(),
    override val lastRoll: Roll? = null
) : StillPlaying {
    override fun applyObstacle(obstacle: Obstacle): StillPlaying =
        copy(totalDistance = totalDistance - obstacle)

    override fun applyRoll(roll: Roll): StillPlaying =
        copy(totalDistance = totalDistance + roll.asDistance(), numbersOfRolls = numbersOfRolls.inc(), lastRoll = roll)

    override fun chooseMove(
        exposableGameState: Game.State.Exposable,
        availableMoves: List<Move.PlayerMove>
    ): Pair<Move.PlayerMove, (Move.PlayerMove) -> Game.State> {
        exposableGameState as DiceRaceExposableGameState
        val chosenMove = availableMoves.first {
            it as DiceRacePlayerMove
            val delta = exposableGameState.goalDistance.value - totalDistance.value
            when (it) {
                is DiceRacePlayerMove.RollOneDie -> delta <= 6
                is DiceRacePlayerMove.RollTwoDice -> delta > 6
                else -> false
            }
        }
        return chosenMove to exposableGameState.nextState
    }
}
