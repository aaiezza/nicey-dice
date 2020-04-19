package org.shaba.nicey_dice;

import static java.lang.String.format;

import java.util.Arrays;

@lombok.Value
public class DiceFace
{
    public static final int MINIMUM_DICE_FACE_VALUE = 1;
    public static final int MAXIMUM_DICE_FACE_VALUE = 6;
    private final byte      value;

    public DiceFace( final int value )
    {
        if ( value < MINIMUM_DICE_FACE_VALUE || value > MAXIMUM_DICE_FACE_VALUE )
        {
            throw new IllegalArgumentException(
                    format( "`%d` is invlad. Dice face must be between 1 and 6.", value ) );
        }
        this.value = (byte) value;
    }

    public int getValue()
    {
        return value;
    }

    public static DiceFace diceFace( final int value )
    {
        return new DiceFace( value );
    }

    public static DiceFace [] diceFaces( final int... values )
    {
        return Arrays.stream( values ).mapToObj( DiceFace::diceFace ).toArray( DiceFace []::new );
    }

    /**
     * Simple alias for less verbose construction
     */
    public static DiceFace dF( final int value )
    {
        return new DiceFace( value );
    }
}
