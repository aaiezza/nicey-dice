package org.shaba.nicey_dice;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.vavr.control.Option;
import lombok.AccessLevel;
import one.util.streamex.StreamEx;

@lombok.Data
public class RolledDice implements Iterable<DiceFace>
{
    public static final RolledDice UNROLLED = new RolledDice( Collections.emptyList() );

    @lombok.Getter ( AccessLevel.NONE )
    private final List<DiceFace>   dice;

    private RolledDice( final List<DiceFace> dice )
    {
        this.dice = Collections.unmodifiableList( dice );
    }

    public static RolledDice rolledDice( final DiceFace... diceFaces )
    {
        return Option.of( diceFaces ).map( Arrays::asList ).map( RolledDice::new ).get();
    }

    public Map<DiceFace, Long> getDiceFaceCountMap()
    {
        return StreamEx.of( dice ).groupingBy( identity(), counting() );
    }

    @Override
    public Iterator<DiceFace> iterator()
    {
        return dice.iterator();
    }

    @Override
    public String toString()
    {
        final StringBuilder out = new StringBuilder( "RolledDice [ " );

        StreamEx.of( dice ).map( DiceFace::getValue ).sorted()
                .mapLastOrElse( d -> out.append( d ).append( ", " ), out::append ).count();

        return out.append( "]" ).toString();
    }
}
