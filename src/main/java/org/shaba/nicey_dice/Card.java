package org.shaba.nicey_dice;

import static java.lang.String.format;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;

import com.google.common.collect.Lists;

import org.shaba.nicey_dice.player.Player;

import java.util.*;

import static org.shaba.nicey_dice.Points.points;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

@lombok.Data
public class Card
{
    // @formatter:off
    static final Map<Integer, Points> DICE_FACE_NUMBER_TO_POINTS = EntryStream.of(
                2, points(  2 ),
                3, points(  5 ),
                4, points( 10 ),
                6, points( 15 ) ).toMapAndThen( Collections::unmodifiableMap );
    // @formatter:on

    private final List<DiceFace>             claimCriteria;
    private final Points                     points;

    private Card( final Points points, final DiceFace... diceFaces )
    {
        if ( diceFaces.length > Player.NUMBER_OF_TOTAL_DICE )
        {
            throw new IllegalArgumentException(
                    format( "A card cannot have more than %d dice face criteria.",
                        Player.NUMBER_OF_TOTAL_DICE ) );
        }
        final List<DiceFace> claimCriteria = Lists.newArrayList();
        claimCriteria.addAll( Arrays.asList( diceFaces ) );
        this.claimCriteria = Collections.unmodifiableList( claimCriteria );

        this.points = points;
    }

    protected Card( final Card card )
    {
        this.claimCriteria = card.claimCriteria;
        this.points = card.points;
    }

    public Map<DiceFace, Long> getClaimCriteriaMap()
    {
        return getDiceFaceCountMap( getClaimCriteria() );
    }

    protected static Map<DiceFace, Long> getDiceFaceCountMap( final List<DiceFace> diceFaces )
    {
        return StreamEx.of( diceFaces ).groupingBy( identity(), counting() );
    }

    public static Card cardWithCustomPoints( final Points points, final DiceFace... diceFaces )
    {
        return new Card( points, diceFaces );
    }

    public static Card card( final DiceFace... diceFaces )
    {
        if ( !DICE_FACE_NUMBER_TO_POINTS.containsKey( diceFaces.length ) )
            throw new IllegalArgumentException(
                    format( "Must provide a number of dice faces within the set: [%s]",
                        StreamEx.of( DICE_FACE_NUMBER_TO_POINTS.keySet() ).joining( ", " ) ) );
        return new Card( DICE_FACE_NUMBER_TO_POINTS.get( diceFaces.length ), diceFaces );
    }
}
