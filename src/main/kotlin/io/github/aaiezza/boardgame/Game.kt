package io.github.aaiezza.boardgame

class Game private constructor(
    val previousGame: Game?,
    val players: Players,
    val state: State,
    private val gameMover: Mover,
    private val playerMoveCalculator: Player.MoveCalculator
) {
    constructor(players: Players, state: State, gameMover: Mover, playerMoveCalculator: Player.MoveCalculator) : this(
        null,
        players,
        state,
        gameMover,
        playerMoveCalculator
    )

    fun proceed(
        previousGame: Game? = this,
        players: Players = this.players,
        state: State = this.state,
    ): Game = Game(previousGame, players, state, gameMover, playerMoveCalculator)

    fun progress(): Game {
        return when (state) {
            is State.Terminal -> this
            is State.PlayerHasChosenNextThing -> state.chosenMove.invoke(this)
            is State.PlayerDoesNextThing -> {
                val (chosenMove, nextState) = with(players[0].state as Player.State.StillPlaying) {
                    chooseMove(state.getExposedState(this@Game), playerMoveCalculator(this@Game))
                }
                proceed(state = nextState(chosenMove))
            }

            is State.GameDoesNextThing -> gameMover(this).invoke(this)
            is State.Undo -> previousGame?.previousGame ?: this
            else -> error("A subclass of state has not accounted for all game states (${state::class.qualifiedName})")
        }
    }

    interface State {
        interface Exposable

        interface GameDoesNextThing : State
        interface PlayerDoesNextThing : State {
            fun getExposedState(game: Game): Exposable
        }

        interface PlayerHasChosenNextThing : GameDoesNextThing {
            val chosenMove: Move.PlayerMove
        }

        interface Undo : State

        interface Terminal : State
    }

    interface Mover {
        operator fun invoke(game: Game): Move.GameMove
    }
}

class Match(private val initialGame: Game) {
    fun play(): Game =
        generateSequence(initialGame) { game ->
            if (game.state is Game.State.Terminal) null
            else game.progress()
        }.last()
}
