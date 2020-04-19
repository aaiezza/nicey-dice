package org.shaba.nicey_dice;

import static java.lang.String.format;

import java.util.stream.Collector;
import java.util.stream.Collectors;

@lombok.Value
public class Points
{
    private static final Points NO_POINTS = new Points( 0 );
    private final int           value;

    public Points( final int value )
    {
        if ( value < 0 )
            throw new IllegalArgumentException(
                    format( "Points can only be positive numbers. %d is less than 0.", value ) );
        this.value = value;
    }

    public Points add( final Points points )
    {
        return new Points( value + points.getValue() );
    }

    public static Collector<Points, ?, Points> sum()
    {
        return Collectors.reducing( NO_POINTS, Points::add );
    }

    public static Points points( final int value )
    {
        return new Points( value );
    }
}
