package org.shaba.nicey_dice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.shaba.nicey_dice.Players.players;
import static org.shaba.nicey_dice.Points.points;
import static org.shaba.nicey_dice.player.Player.player;
import static org.shaba.nicey_dice.player.Player.Name.name;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.shaba.nicey_dice.player.Player;

@RunWith ( MockitoJUnitRunner.StrictStubs.class )
public class PlayersTest
{
    private Players subject;

    @Test
    public void givenSameNames_throwException()
    {
        final Player.Name playerName = name( "Al" );
        assertThatThrownBy( () -> subject = players( playerName, playerName ) )
                .isExactlyInstanceOf( IllegalArgumentException.class )
                .hasMessageContaining( "Names of given players must be distinct." );
    }

    @Test
    public void given2Names_createsPlayers()
    {
        final Player.Name al = name( "Al" );
        final Player.Name bob = name( "Bob" );

        subject = players( al, bob );

        assertThat( subject.getNumberOfPlayers() ).isEqualTo( 2 );
        assertThat( subject.currentPlayer() )
            .isEqualTo( player( al ) );
        assertThat( subject )
            .extracting( Player::getName, Player::getPoints )
            .containsExactly(
                tuple( al, points( 0 ) ),
                tuple( bob, points( 0 ) ) );
    }
}
