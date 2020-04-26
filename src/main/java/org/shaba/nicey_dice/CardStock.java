package org.shaba.nicey_dice;

import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.AccessLevel;
import one.util.streamex.StreamEx;

@lombok.Data
@lombok.RequiredArgsConstructor ( access = AccessLevel.PRIVATE )
public class CardStock
{
    @lombok.Getter ( AccessLevel.NONE )
    private final Deque<Card> cards;

    public static CardStock cardStock( final Card... cards )
    {
        final Deque<Card> cardStack = new LinkedList<>();
        cardStack.addAll( Arrays.asList( cards ) );
        return new CardStock( cardStack );
    }

    public boolean isEmpty()
    {
        return cards.isEmpty();
    }

    public int cardsRemaining()
    {
        return cards.size();
    }

    /**
     * Drawing a card from the CardStock causes there to be one less card on
     * this CardStock. CardStock's are immutable, so the new CardStock is also
     * produced from this transaction.
     */
    public Tuple2<CardStock, Card> draw()
    {
        final Deque<Card> cardStack = new LinkedList<>();
        cardStack.addAll( cards );
        final Card drawnCard = cardStack.pop();
        return Tuple.of( new CardStock( cardStack ), drawnCard );
    }

    public CardStock shuffle()
    {
        final List<Card> cardsToShuffle = StreamEx.of( this.cards ).toCollection( LinkedList::new );
        Collections.shuffle( cardsToShuffle );
        return cardStock( cardsToShuffle.toArray( Card []::new ) ).new ShuffledCardStock();
    }

    @lombok.Value
    @lombok.EqualsAndHashCode ( callSuper = true )
    private class ShuffledCardStock extends CardStock
    {
        private ShuffledCardStock()
        {
            super( CardStock.this.cards );
        }

        @Override
        public CardStock shuffle()
        {
            return this;
        }
    }
}
