package org.shaba.nicey_dice.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.when;
import static org.shaba.nicey_dice.Points.points;
import static org.shaba.nicey_dice.factories.DiceRoller.ROLLER;
import static org.shaba.nicey_dice.player.Player.player;
import static org.shaba.nicey_dice.player.Player.Name.name;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.shaba.nicey_dice.*;

import one.util.streamex.StreamEx;

@RunWith ( MockitoJUnitRunner.StrictStubs.class )
public class PlayerTest
{
    private Player      subject;
    private Player.Name name = name( "Test Player" );
    @Mock
    private FieldCards  fieldCards;
    @Mock
    private FieldCard   fieldCard;

    @Before
    public void setup()
    {
        subject = player( name );
    }

    @Test
    public void givenNewPlayer_hasExpectedNameAnd0points()
    {
        assertThat( subject )
            .returns( name, Player::getName )
            .returns( points( 0 ), Player::getPoints );
    }

    @Test
    public void givenUnclaimedFieldCards_playerHasAll_6_DiceToRoll()
    {
        mockFieldCards( 1 );
        when( fieldCard.getClaimsForPlayer( same( subject ) ) ).thenReturn( testDF( 0 ) );
        assertThat( subject.getRollableDice( fieldCards ) ).isEqualTo( 6 );
    }

    @Test
    public void given_1ClaimOnFieldCard_playerHas_5_DiceToRoll()
    {
        mockFieldCards( 1 );
        when( fieldCard.getClaimsForPlayer( same( subject ) ) ).thenReturn( testDF( 1 ) );
        assertThat( subject.getRollableDice( fieldCards ) ).isEqualTo( 5 );
    }

    @Test
    public void given_2ClaimsOn_1FieldCard_playerHas_4_DiceToRoll()
    {
        mockFieldCards( 1 );
        when( fieldCard.getClaimsForPlayer( same( subject ) ) ).thenReturn( testDF( 2 ) );
        assertThat( subject.getRollableDice( fieldCards ) ).isEqualTo( 4 );
    }

    @Test
    public void given_5ClaimsAcross_3FieldCard_playerHas_1_DiceToRoll()
    {
        mockFieldCards( 3 );
        when( fieldCard.getClaimsForPlayer( same( subject ) ) )
                .thenReturn( testDF( 2 ) )
                .thenReturn( testDF( 1 ) )
                .thenReturn( testDF( 2 ) );
        assertThat( subject.getRollableDice( fieldCards ) ).isEqualTo( 1 );
    }

    private void mockFieldCards( final int numOfCards )
    {
        when( fieldCards.cardStream() )
                .thenAnswer( i -> StreamEx.constant( fieldCard, numOfCards ) );
    }

    private static List<DiceFace> testDF( final long numOfDice )
    {
        return ROLLER.randomDiceStream().limit( numOfDice ).toList();
    }
}
