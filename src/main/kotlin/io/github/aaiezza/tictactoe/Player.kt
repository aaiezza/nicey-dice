package io.github.aaiezza.tictactoe

interface Player {
    val symbol: Char
    fun makeMove(board: Board): Int
}

class HumanPlayer(override val symbol: Char) : Player {
    override fun makeMove(board: Board): Int {
        while (true) {
            println("Enter a position (0-8) for $symbol: ")
            val input = readLine()?.toIntOrNull()
            if (input != null && input in 0..8 && board.isValidMove(input)) {
                return input
            }
            println("Invalid input, try again.")
        }
    }
}

class RandomPlayer(override val symbol: Char) : Player {
    override fun makeMove(board: Board): Int {
        val availableMoves = board.cells.indices.filter { board.isValidMove(it) }
        return availableMoves.random()
    }
}

class BlockPlayer(override val symbol: Char, val opponentSymbol: Char) : Player {
    override fun makeMove(board: Board): Int {
        // First check if the player can win
        val winningMove = findWinningMove(board, symbol)
        if (winningMove != -1) return winningMove

        // Block opponent's winning move
        val blockingMove = findWinningMove(board, opponentSymbol)
        if (blockingMove != -1) return blockingMove

        // Otherwise, pick a random valid move
        return RandomPlayer(symbol).makeMove(board)
    }

    private fun findWinningMove(board: Board, player: Char): Int {
        return board.cells.indices.firstOrNull {
            board.isValidMove(it) && board.makeMove(it, player).checkWin(player)
        } ?: -1
    }
}
