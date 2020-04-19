package org.shaba.nicey_dice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.shaba.nicey_dice.DiceFace.dF;

import org.junit.Test;

public class RolledDiceTest
{
    private RolledDice subject;

    @Test
    public void given3RolledDice_3DicePresent()
    {
        subject = RolledDice.rolledDice( dF( 6 ), dF( 5 ), dF( 1 ) );

        assertThat( subject ).hasSize( 3 );
        System.out.println( subject );
    }
}
