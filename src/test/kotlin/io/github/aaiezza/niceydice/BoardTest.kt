package io.github.aaiezza.niceydice

import assertk.assertThat
import assertk.assertions.containsExactly
import io.github.aaiezza.niceydice.DieFace.Companion.toSixSidedDieFace
import io.github.aaiezza.niceydice.SixSidedDieFace.Companion.to6D
import org.junit.jupiter.api.Test

class BoardTest {
    private lateinit var subject: Board

    @Test
    fun `should produce map of claims criteria`() {
        val map: Map<DieFace, List<PlayerState.Id>> = mapOf()
        val result = map.toMutableMap()

        result[DieFace(1)] = listOf(PlayerState.Id(1), PlayerState.Id(2))
        result[DieFace(1)] = listOf(PlayerState.Id(2))
        result[DieFace(1)] = listOf(PlayerState.Id(1))

        println(result)
    }

    @Test
    fun `should work as expected`() {
        subject = Board(
            playerData = mapOf(
                PlayerState.Id(1) to Board.PlayerData(),
                PlayerState.Id(2) to Board.PlayerData(),
            ),
            deck = Card.STANDARD_DECK,
            fieldCards = emptyList()
        )
        subject.print()

        subject = with(subject) {
            copy(
                deck = deck.subList(3, deck.size),
                fieldCards = deck.subList(0, 3).map { FieldCard(it) }
            )
        }
        subject.print()

        subject = with(subject) {
            val currentPlayerId = PlayerState.Id(1)
            copy(
                playerData = playerData.mapValues { (id, data) ->
                    if (currentPlayerId == id) {
                        data.copy(dice = data.dice.subList(1, data.dice.size), scoredCards = listOf(fieldCards[0].card))
                    } else data
                },
                deck = deck.subList(1, deck.size),
                fieldCards = (fieldCards.drop(1) + deck.subList(0, 1).map { FieldCard(it, emptyMap()) })
                    .mapIndexed { i, fc ->
                        if (i == 1) {
                            fc.copy(
                                claims = fc.claims.toMutableMap()
                                    .let {
                                        it[currentPlayerId] =
                                            fc.claims.getOrDefault(
                                                currentPlayerId,
                                                emptyList()
                                            ) + fc.claimCriteria[0].toSixSidedDieFace()
                                        it
                                    }.toMap()
                            )
                        } else fc
                    }
            )
        }
        subject.print()

        subject = with(subject) {
            val currentPlayerId = PlayerState.Id(2)
            copy(
                playerData = playerData.mapValues { (id, data) ->
                    if (currentPlayerId == id) {
                        data.copy(dice = data.dice.subList(2, data.dice.size), scoredCards = listOf(fieldCards[0].card))
                            .let { it.copy(rolledDice = it.dice.map { 4.to6D() }) }
                    } else data
                },
                deck = deck.subList(1, deck.size),
                fieldCards = (fieldCards.drop(1) + deck.subList(0, 1).map { FieldCard(it, emptyMap()) })
                    .mapIndexed { i, fc ->
                        if (i == 0) {
                            fc.copy(
                                claims = fc.claims.toMutableMap()
                                    .let {
                                        it[currentPlayerId] =
                                            fc.claims.getOrDefault(
                                                currentPlayerId,
                                                emptyList()
                                            ) + fc.claimCriteria[0].toSixSidedDieFace() + fc.claimCriteria[1].toSixSidedDieFace()
                                        it
                                    }.toMap()
                            )
                        } else fc
                    }
            )
        }
        subject.print()

        assertThat(subject.fieldCards[0].criteriaMetBy).containsExactly(PlayerState.Id(2))
    }
}
