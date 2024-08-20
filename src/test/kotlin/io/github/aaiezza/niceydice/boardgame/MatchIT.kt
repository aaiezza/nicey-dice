package io.github.aaiezza.niceydice.boardgame

import io.github.aaiezza.boardgame.Game
import io.github.aaiezza.boardgame.Match
import io.github.aaiezza.boardgame.Players
import org.junit.jupiter.api.BeforeEach

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
}
