package io.github.aaiezza.tictactoe

data class Board(val cells: List<Char> = List(9) { ' ' }) {

    fun printBoard() {
        println("""
            ${cells[0]} | ${cells[1]} | ${cells[2]}
            ---------
            ${cells[3]} | ${cells[4]} | ${cells[5]}
            ---------
            ${cells[6]} | ${cells[7]} | ${cells[8]}
        """.trimIndent())
    }

    fun isValidMove(index: Int): Boolean = cells[index] == ' '

    fun makeMove(index: Int, player: Char): Board {
        return copy(cells = cells.toMutableList().apply { this[index] = player })
    }

    fun isFull(): Boolean = cells.none { it == ' ' }

    fun checkWin(player: Char): Boolean {
        val winPatterns = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Columns
            listOf(0, 4, 8), listOf(2, 4, 6)                   // Diagonals
        )
        return winPatterns.any { pattern -> pattern.all { cells[it] == player } }
    }
}
