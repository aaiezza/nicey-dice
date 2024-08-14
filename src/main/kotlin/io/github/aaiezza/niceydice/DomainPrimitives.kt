package io.github.aaiezza.niceydice

data class DiceFace(val value: Byte) : Comparable<DiceFace> {
    init {
        require(value in MINIMUM_DICE_FACE_VALUE..MAXIMUM_DICE_FACE_VALUE) { "`$value` is invalid. Dice face must be between 1 and 6." }
    }

    companion object {
        const val MINIMUM_DICE_FACE_VALUE: Byte = 1
        const val MAXIMUM_DICE_FACE_VALUE: Byte = 6
    }

    override fun compareTo(other: DiceFace): Int = value - other.value
}

data class Card(val claimCriteria: ClaimCriteria) {
    data class ClaimCriteria(val value: List<DiceFace>) : List<DiceFace> by value {
        init {
            require(NUMBER_OF_DICE_TO_POINTS.containsKey(size)) { "It is unknown how to point this ClaimCriteria holding $size dice faces." }
        }
    }

    val points: Points by lazy {
        NUMBER_OF_DICE_TO_POINTS.getValue(claimCriteria.size)
    }

    companion object {
        private val NUMBER_OF_DICE_TO_POINTS =
            mapOf(
                2 to Points(2),
                3 to Points(5),
                4 to Points(10),
                6 to Points(15),
            )
    }
}

data class Points(val value: Int)

