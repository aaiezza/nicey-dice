package org.shaba.nicey_dice.factories;

import org.shaba.nicey_dice.DiceFace;

import java.util.Random;

import static org.shaba.nicey_dice.DiceFace.*;

public class DiceFaceFactory
{
    private static final int RANGE = MAXIMUM_DICE_FACE_VALUE - MINIMUM_DICE_FACE_VALUE;

    private final Random     rand  = new Random();

    public DiceFace createRandomDiceFace()
    {
        return diceFace( rand.nextInt( RANGE ) - MINIMUM_DICE_FACE_VALUE );
    }

    /**
     * Alias for {@link DiceFaceFactory#createRandomDiceFace()}.
     */
    public DiceFace randDF() {
        return createRandomDiceFace();
    }
}
