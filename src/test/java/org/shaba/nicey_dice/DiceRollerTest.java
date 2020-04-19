package org.shaba.nicey_dice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.shaba.nicey_dice.factories.DiceRoller;

public class DiceRollerTest
{
    final DiceRoller subject = DiceRoller.ROLLER;

    @Test
    public void rolling6Dice() {
        final RolledDice rolledDice = subject.rollDice( 6 );
        assertThat( rolledDice ).hasSize( 6 );
    }
}
