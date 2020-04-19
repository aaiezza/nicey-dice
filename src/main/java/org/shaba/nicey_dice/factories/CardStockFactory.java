package org.shaba.nicey_dice.factories;

import org.shaba.nicey_dice.*;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.shaba.nicey_dice.CardStock.cardStock;

import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

public class CardStockFactory
{
    // @formatter:off
    private static final int [] [] ORIGINAL_CARD_STOCK = {
        { 1, 1 }, { 2, 4 }, { 3, 3 }, { 4, 4 }, { 5, 5 }, { 6, 6 },

        { 2, 3, 6 }, { 1, 2 ,3 }, { 4, 4, 4 }, { 4, 5, 6 }, { 2, 4, 6 },
        { 1, 1, 1 }, { 2, 2, 2 }, { 3, 5, 5 }, { 3, 6, 6 }, { 3, 3, 3 },
        { 2, 2, 4 }, { 1, 4, 5 },

        { 1, 2, 3, 4 }, { 3, 4, 5, 6 }, { 3, 3, 5, 5 }, { 1, 1, 2, 2 },
        { 6, 6, 6, 6 }, { 4, 4, 4, 4 }, { 2, 2, 2, 2 }, { 2, 2, 6, 6 },

        { 1, 1, 1, 1, 1, 1 }, { 1, 2, 3, 4, 5, 6 },
        { 5, 5, 5, 5, 5, 5 }, { 6, 6, 6, 6, 6, 6 } };
    // @formatter:on

    private final DiceFaceFactory diceFaceFactory = new DiceFaceFactory();

    public CardStock originalCardStock()
    {
        return StreamEx.of( ORIGINAL_CARD_STOCK )
                .map( toDiceFaceArray() )
                .map( Card::card )
                .chain( toCardStock() );
    }

    private Function<int [], DiceFace []> toDiceFaceArray()
    {
        return diceFaces -> IntStreamEx.of( diceFaces )
                .mapToObj( DiceFace::diceFace )
                .toArray( DiceFace []::new );
    }

    private Function<StreamEx<Card>, CardStock> toCardStock()
    {
        return cards -> cardStock( cards.toArray( Card []::new ) );
    }

    public CardStock randomCardStock()
    {
        return EntryStream.of(
                numberOfDiceFaceArrays(  6 ), numberOfDiceFaces( 2 ),
                numberOfDiceFaceArrays( 12 ), numberOfDiceFaces( 3 ),
                numberOfDiceFaceArrays(  8 ), numberOfDiceFaces( 4 ),
                numberOfDiceFaceArrays(  4 ), numberOfDiceFaces( 6 ) )
            .flatMapKeyValue( randomDiceFaceArray() )
            .map( Card::card )
            .chain( toCardStock() );
    }

    @lombok.Value private static class NumberOfDiceFaceArrays { final int value; }
    private static NumberOfDiceFaceArrays numberOfDiceFaceArrays(final int value) {
        return new NumberOfDiceFaceArrays( value ); }
    @lombok.Value private static class NumberOfDiceFaces { final int value; }
    private static NumberOfDiceFaces numberOfDiceFaces(final int value) {
        return new NumberOfDiceFaces( value ); }

    private BiFunction<NumberOfDiceFaceArrays, NumberOfDiceFaces, StreamEx<DiceFace []>> randomDiceFaceArray()
    {
        return ( numberOfArrays, numberOfDiceFaces ) -> StreamEx
                .generate( randomDiceFaceArray( numberOfDiceFaces ) )
                .limit( numberOfArrays.getValue() );
    }

    private Supplier<DiceFace []> randomDiceFaceArray( final NumberOfDiceFaces numberOfDiceFaces )
    {
        return () -> StreamEx.generate( diceFaceFactory::createRandomDiceFace )
                .limit( numberOfDiceFaces.getValue() )
                .toArray(DiceFace []::new);
    }
}
