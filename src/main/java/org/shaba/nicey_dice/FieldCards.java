package org.shaba.nicey_dice;

import static java.lang.String.format;

import com.google.common.collect.Lists;

import org.shaba.nicey_dice.player.Player;

import lombok.AccessLevel;

import java.util.*;

import static org.shaba.nicey_dice.FieldCard.fieldCard;

import one.util.streamex.StreamEx;

/**
 * Score-able cards drawn from the stock
 */
@lombok.Data
public class FieldCards implements Iterable<FieldCard>
{
    public static final int       MAXIMUM_NUMBER_OF_CARDS_IN_FIELD = 3;
    @lombok.Getter ( AccessLevel.NONE )
    private final List<FieldCard> cards;

    private FieldCards()
    {
        this.cards = Collections.emptyList();
    }

    FieldCards(final List<FieldCard> cards) {
        this.cards = Collections.unmodifiableList(cards);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public FieldCards addCardFromStock( final Card card )
    {
        if ( cards.size() >= MAXIMUM_NUMBER_OF_CARDS_IN_FIELD )
        {
            throw new IllegalStateException( format( "Field cannot exceed %d number of cards",
                MAXIMUM_NUMBER_OF_CARDS_IN_FIELD ) );
        }
        return new FieldCards(cardStream()
            .append(fieldCard( card ))
            .toListAndThen(Collections::unmodifiableList));
    }

    public boolean fieldIsFull()
    {
        return size() >= MAXIMUM_NUMBER_OF_CARDS_IN_FIELD;
    }

    public int size()
    {
        return cards.size();
    }

    public StreamEx<FieldCard> cardStream()
    {
        return StreamEx.of( cards );
    }

    public FieldCards placeDiceForPlayer(
            final FieldCard fieldCard,
            final Player player,
            final DiceFace... diceFaces )
    {
        if ( !contains( fieldCard ) )
            return this;

        return cardStream()
                .findFirst(fieldCard::equals)
                .map(card ->
                    cardStream()
                        .map(fCard -> fCard == card ? fCard.addPlayerClaim(player, diceFaces)
                                                    : fCard)
                        .toListAndThen(Collections::unmodifiableList) )
                .map(FieldCards::new)
                .orElse(this);
    }

    public FieldCards scoreCard( final FieldCard fieldCard )
    {
        final int cardIndex = cards.indexOf( fieldCard );
        if ( !cards.contains( fieldCard ) || cardIndex < 0 )
            throw new IllegalArgumentException(
                    format( "Specified card (%s) is not in this field.", fieldCard ) );

        final List<FieldCard> fieldCards = Lists.newArrayList();
        fieldCards.addAll( cards );
        fieldCards.remove( cardIndex );
        return new FieldCards( Collections.unmodifiableList( fieldCards ) );
    }

    public boolean contains( final FieldCard fieldCard )
    {
        return get(fieldCard).isPresent();
    }

    public Optional<FieldCard> get( final FieldCard fieldCard )
    {
        return cardStream().filter( fieldCard::equals ).findFirst();
    }

    public static FieldCards fieldCards()
    {
        return new FieldCards();
    }

    @Override
    public Iterator<FieldCard> iterator()
    {
        return cards.iterator();
    }
}
