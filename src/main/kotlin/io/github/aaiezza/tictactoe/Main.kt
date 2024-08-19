package io.github.aaiezza.tictactoe

fun main() {
    val initialBoard = Board()
    val players = listOf(
        HumanPlayer('X'),
//        RandomPlayer('X'),
        BlockPlayer('O', 'X')
    )
    val game = Game(initialBoard, players)
    game.play()

    // Optionally, print the history of the board states after the game ends
    println("\nGame History:")
    game.getHistory().forEachIndexed { index, boardState ->
        println("Move ${index + 1}:")
        boardState.printBoard()
    }
}
