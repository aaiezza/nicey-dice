package io.github.aaiezza.tictactoe

class Game(private var board: Board, private val players: List<Player>) {
    private val history = mutableListOf<Board>()
    private var currentPlayerIndex = 0

    fun play() {
        while (true) {
            val currentPlayer = players[currentPlayerIndex % players.size]
            board.printBoard()
            println()

            val move = currentPlayer.makeMove(board)
            board = board.makeMove(move, currentPlayer.symbol)
            history.add(board)

            if (board.checkWin(currentPlayer.symbol)) {
                board.printBoard()
                println("${currentPlayer.symbol} wins!")
                break
            } else if (board.isFull()) {
                board.printBoard()
                println("It's a tie!")
                break
            }

            currentPlayerIndex++
        }
    }

    fun getHistory(): List<Board> = history
}
