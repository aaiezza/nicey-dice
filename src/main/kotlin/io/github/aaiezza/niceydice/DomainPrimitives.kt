package io.github.aaiezza.niceydice

import io.github.aaiezza.niceydice.DieFace.Companion.toDieFace
import io.github.aaiezza.niceydice.ICard.Companion.NUMBER_OF_DICE_TO_POINTS
import io.github.aaiezza.niceydice.SixSidedDieFace.Companion.toDieFace

open class DieFace(open val value: Byte) : Comparable<DieFace> {
    override fun compareTo(other: DieFace): Int = value - other.value

    override fun toString(): String = when(value.toInt()) {
        1 -> "⚀"
        2 -> "⚁"
        3 -> "⚂"
        4 -> "⚃"
        5 -> "⚄"
        6 -> "⚅"
        else -> "DieFace($value)"
    }

    companion object {
        fun Int.toDieFace() = DieFace(this.toByte())
        fun Byte.toDieFace() = DieFace(this)
        fun DieFace.toSixSidedDieFace() = SixSidedDieFace(value)
    }
}

data class SixSidedDieFace(override val value: Byte) : DieFace(value) {
    init {
        require(value in MINIMUM_DICE_FACE_VALUE..MAXIMUM_DICE_FACE_VALUE) { "`$value` is invalid. Dice face must be between 1 and 6." }
    }

    override fun toString(): String = super.toString()

    companion object {
        const val MINIMUM_DICE_FACE_VALUE: Byte = 1
        const val MAXIMUM_DICE_FACE_VALUE: Byte = 6
        val RANGE = MINIMUM_DICE_FACE_VALUE..MAXIMUM_DICE_FACE_VALUE
        fun SixSidedDieFace.toDieFace() = DieFace(value)
        fun Int.to6D() = SixSidedDieFace(this.toByte())
    }
}

class SixSidedDie {
    fun roll(): SixSidedDieFace = SixSidedDieFace(SixSidedDieFace.RANGE.random().toByte())
}

data class Points(val value: Int)

interface ICard {
    val claimCriteria: ClaimCriteria
    val points: Points

    data class ClaimCriteria(val value: List<DieFace>) : List<DieFace> by value {
        init {
            require(NUMBER_OF_DICE_TO_POINTS.containsKey(size)) { "It is unknown how to point this ClaimCriteria holding $size dice faces." }
        }

        companion object {
            fun fromSixSidedDice(dice: List<SixSidedDieFace>) = ClaimCriteria(dice.map { DieFace(it.value) })
        }
    }

    companion object {
        val NUMBER_OF_DICE_TO_POINTS =
            mapOf(
                2 to Points(2),
                3 to Points(5),
                4 to Points(10),
                6 to Points(15),
            )
    }
}

data class Card(override val claimCriteria: ICard.ClaimCriteria) : ICard, List<DieFace> by claimCriteria {
    override val points: Points by lazy {
        NUMBER_OF_DICE_TO_POINTS.getValue(claimCriteria.size)
    }

    override fun toString(): String = claimCriteria.joinToString(prefix = "⦉", separator = "", postfix = "⦊")

    companion object {
        val STANDARD_DECK : List<Card> get() = listOf(
            intArrayOf(1, 1), intArrayOf(2, 4), intArrayOf(3, 3), intArrayOf(4, 4), intArrayOf(5, 5), intArrayOf(6, 6),

            intArrayOf(2, 3, 6), intArrayOf(1, 2, 3), intArrayOf(4, 4, 4), intArrayOf(4, 5, 6), intArrayOf(2, 4, 6),
            intArrayOf(1, 1, 1), intArrayOf(2, 2, 2), intArrayOf(3, 5, 5), intArrayOf(3, 6, 6), intArrayOf(3, 3, 3),
            intArrayOf(2, 2, 4), intArrayOf(1, 4, 5),

            intArrayOf(1, 2, 3, 4), intArrayOf(3, 4, 5, 6), intArrayOf(3, 3, 5, 5), intArrayOf(1, 1, 2, 2),
            intArrayOf(6, 6, 6, 6), intArrayOf(4, 4, 4, 4), intArrayOf(2, 2, 2, 2), intArrayOf(2, 2, 6, 6),

            intArrayOf(1, 1, 1, 1, 1, 1), intArrayOf(1, 2, 3, 4, 5, 6),
            intArrayOf(5, 5, 5, 5, 5, 5), intArrayOf(6, 6, 6, 6, 6, 6)
        ).map { claimCriteria -> claimCriteria.map { it.toDieFace() }.let { ICard.ClaimCriteria(it) } }
            .map { Card(it) }
    }
}

data class PlayerState(
    val id: Id
) {
    data class Id(val value: Int) {
        override fun toString(): String = "Id($value)"
    }
}

data class Board(
    val playerData: Map<PlayerState.Id, PlayerData>,
    val deck: List<Card> = Card.STANDARD_DECK,
    val fieldCards: List<FieldCard> = emptyList(),
) {
    data class PlayerData(
        val dice: List<SixSidedDie> = generateSequence(::SixSidedDie).take (6).toList(),
        val rolledDice: List<SixSidedDieFace> = emptyList(),
        val scoredCards: List<Card> = emptyList(),
    )
}

data class FieldCard(
    val card: Card,
    val claims: Map<PlayerState.Id, List<SixSidedDieFace>> = emptyMap()
) : ICard by card {
    init {
        require(claimsMap.size == claimCriteria.size)
    }

    val claimsMap : Map<DieFace, List<PlayerState.Id>>
        get() {
            var claimsToMatch = claims.mapValues { (_, dice) -> dice.map { it.toDieFace() }.toMutableList() }

            val map = claimCriteria.groupBy({it}) {
                claimsToMatch.mapNotNull { (id, dice) ->
                    val i = dice.mapIndexedNotNull { i, d -> if(d.value == it.value) i else null }.firstOrNull()
                    if(null != i) {
                        dice.removeAt(i)
                        id
                    } else null
                }
            }.mapValues { (_, ids) -> ids[0] }

            require(claimsToMatch.values.all { it.isEmpty() }) { "There are claims placed on this card that don't match the criteria." }

            return map
        }

    val criteriaMetBy: List<PlayerState.Id>
        get() = claims.mapNotNull { (id, dice) ->
            if(dice.size == claimCriteria.size) id else null
        }

    override fun toString(): String {
        val claimsString = claims.toList().joinToString("") { (id, dice) -> "${id.value}=$dice" }
        return "$card${if(claims.isNotEmpty()) claimsString else "" }"
    }
}
