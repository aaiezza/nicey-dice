package io.github.aaiezza.niceydice

fun Board.print() {
    val out = StringBuilder()

    out.append("Players: ")
    playerData.asSequence().joinToString(" ") { (id, data) ->
        "${id.value}=[${if(data.rolledDice.isEmpty()) "dice" else "rolled dice"}: " +
                "${if(data.rolledDice.isEmpty()) data.dice.size else data.rolledDice}, " +
                "score: ${data.scoredCards.map { it.points.value }.toIntArray().sum()}]"
    }.let(out::append)
    out.append("\n   Deck: ${deck.size} $deck\nField: ")

    fieldCards.joinToString(" ").let(out::append)
    out.append("\n")

    println(out.toString())
}
