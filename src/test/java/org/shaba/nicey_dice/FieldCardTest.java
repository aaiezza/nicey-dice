package org.shaba.nicey_dice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.shaba.nicey_dice.player.Player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.shaba.nicey_dice.Card.card;
import static org.shaba.nicey_dice.DiceFace.dF;
import static org.shaba.nicey_dice.FieldCard.fieldCard;
import static org.shaba.nicey_dice.player.Player.player;
import static org.shaba.nicey_dice.player.Player.Name.name;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class FieldCardTest {
    private FieldCard subject;
    private Player player;

    @Before
    public void setUp() {
        player = player( name("Giacomo") );
    }

    @Test
    public void shouldReturnExpectedUnclaimedCriteria() {
        subject = fieldCard( card( dF(2), dF(4), dF(4) ) )
                .addPlayerClaim( player, dF(4) )
                .addPlayerClaim( player, dF(4) );

        assertThat(subject.getUnclaimedCrieriaForPlayer(player))
            .containsExactly( dF(2) );
     }
}
