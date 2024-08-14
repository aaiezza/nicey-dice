package io.github.aaiezza.boardgame

sealed interface Player {
    val username: Username
    val state: State

    data class Username(val value: String)

    data class Active(override val username: Username, override val state: State) : Player
    data class Inactive(override val username: Username, override val state: State) : Player

    fun makeActive() = if (this !is Active) Active(username, state) else this
    fun makeInactive() = if (this !is Inactive) Inactive(username, state) else this

    interface State {
        fun chooseMove(
            exposableGameState: Game.State.Exposable,
            availableMoves: List<Move.PlayerMove>
        ): Pair<Move.PlayerMove, (Move.PlayerMove) -> Game.State>
    }

    interface MoveCalculator {
        operator fun invoke(game: Game): List<Move.PlayerMove>
    }
}

fun Player(username: Player.Username, state: Player.State) = Player.Inactive(username, state)

data class Players internal constructor(
    private val value: List<Player>
) : List<Player> by value {

    init {
        require(filterIsInstance<Player.Active>().size == 1) { "Only one player can be active at a time" }
        require(first() is Player.Active) { "First player must be the active player" }
    }

    companion object {
        private fun List<Player>.markStartingPlayerAsActive(playerToMakeActive: Player) =
            map { if (it == playerToMakeActive) it.makeActive() else it.makeInactive() }

        private fun List<Player>.findActivePlayerIndex(): Int =
            mapIndexedNotNull { i, player -> if (player is Player.Active) i else null }.single()

        private fun List<Player>.shiftPlayersSoActiveIsFirst(): List<Player> {
            val activePlayerIndex = this.findActivePlayerIndex()
            return Players(((activePlayerIndex..this.toList().indices.last) + (0 until activePlayerIndex)).map {
                this.toList()[it]
            })
        }

        fun createPlayersFromList(players: List<Player>, startingPlayerIndex: Int = 0) =
            Players(createPlayers(players.markStartingPlayerAsActive(players[startingPlayerIndex])))

        private fun createPlayers(value: List<Player>) = Players(value.shiftPlayersSoActiveIsFirst())
    }

    fun progressPlayers(): Players =
        if (size == 1) {
            Players(value.map { it.makeActive() })
        } else {
            Players(markStartingPlayerAsActive(mapIndexedNotNull { i, player -> if (i == 1) player else null }.single()).shiftPlayersSoActiveIsFirst())
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Players) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "Players(value=$value)"
    }
}
