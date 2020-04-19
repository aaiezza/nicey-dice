package org.shaba.nicey_dice.factories;

import java.util.concurrent.ThreadLocalRandom;

import static org.shaba.nicey_dice.DiceFace.MAXIMUM_DICE_FACE_VALUE;
import static org.shaba.nicey_dice.DiceFace.MINIMUM_DICE_FACE_VALUE;
import static org.shaba.nicey_dice.RolledDice.rolledDice;

import org.shaba.nicey_dice.DiceFace;
import org.shaba.nicey_dice.RolledDice;

import lombok.AccessLevel;
import one.util.streamex.StreamEx;

@lombok.Value
@lombok.RequiredArgsConstructor ( access = AccessLevel.PRIVATE )
public class DiceRoller
{
    @lombok.Getter ( AccessLevel.NONE )
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    public static final DiceRoller  ROLLER = new DiceRoller();

    public RolledDice rollDice( final int numberOfDice )
    {
        return rolledDice( randomDiceStream().limit( numberOfDice ).toArray( DiceFace []::new ) );
    }

    public synchronized StreamEx<DiceFace> randomDiceStream()
    {
        return StreamEx
                .generate(
                    () -> random.nextInt( MINIMUM_DICE_FACE_VALUE, MAXIMUM_DICE_FACE_VALUE + 1 ) )
                .map( DiceFace::diceFace );
    }
}
