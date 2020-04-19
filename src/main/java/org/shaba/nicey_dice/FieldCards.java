package org.shaba.nicey_dice;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.shaba.nicey_dice.FieldCard.fieldCard;

import com.google.common.collect.Lists;

import org.shaba.nicey_dice.player.Player;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.AccessLevel;
import one.util.streamex.StreamEx;

/**
 * Score-able cards drawn from the stock
 */
@lombok.Data
@lombok.RequiredArgsConstructor ( access = AccessLevel.PRIVATE )
public class FieldCards implements Iterable<FieldCard>
{
    public static final int       MAXIMUM_NUMBER_OF_CARDS_IN_FIELD = 3;
    @lombok.Getter ( AccessLevel.NONE )
    private final List<FieldCard> cards;

    private FieldCards()
    {
        this.cards = Collections.emptyList();
    }

    public FieldCards addCardFromStock( final Card card )
    {
        if ( cards.size() >= MAXIMUM_NUMBER_OF_CARDS_IN_FIELD )
        {
            throw new IllegalStateException( format( "Field cannot exceed %d number of cards",
                MAXIMUM_NUMBER_OF_CARDS_IN_FIELD ) );
        }
        final List<FieldCard> fieldCards = new ArrayList<>();
        fieldCards.addAll( cards );
        fieldCards.add( fieldCard( card ) );
        return new FieldCards( Collections.unmodifiableList( fieldCards ) );
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

        final List<FieldCard> fieldCards = cardStream().remove( fieldCard::equals ).toList();
        fieldCards.add( fieldCard.addPlayerClaim( player, diceFaces ) );

        return new FieldCards( Collections.unmodifiableList( fieldCards ) );
    }

    public Tuple2<FieldCards, FieldCard> scoreCard( final FieldCard fieldCard )
    {
        final int cardIndex = cards.indexOf( fieldCard );
        if ( !cards.contains( fieldCard ) || cardIndex < 0 )
            throw new IllegalArgumentException(
                    format( "Specified card (%s) is not in this field.", fieldCard ) );

        final List<FieldCard> fieldCards = Lists.newArrayList();
        fieldCards.addAll( cards );
        final FieldCard scoredFieldCard = fieldCards.get( cardIndex );
        fieldCards.remove( cardIndex );
        return Tuple.of( new FieldCards( Collections.unmodifiableList( fieldCards ) ),
            scoredFieldCard );
    }

    public boolean contains( final FieldCard fieldCard )
    {
        return cardStream().filter( fieldCard::equals ).findFirst().isPresent();
    }

    public static FieldCards fieldCards()
    {
        return new FieldCards();
    }

    @Override
    public Iterator<FieldCard> iterator()
    {
        return cardStream().iterator();
    }
}
