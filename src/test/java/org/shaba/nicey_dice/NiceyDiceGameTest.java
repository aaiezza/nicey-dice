package org.shaba.nicey_dice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.shaba.nicey_dice.Board.board;
import static org.shaba.nicey_dice.Card.card;
import static org.shaba.nicey_dice.CardStock.cardStock;
import static org.shaba.nicey_dice.DiceFace.dF;
import static org.shaba.nicey_dice.NiceyDiceGame.niceyDiceGame;
import static org.shaba.nicey_dice.Players.players;
import static org.shaba.nicey_dice.player.Player.Name.name;

@RunWith ( MockitoJUnitRunner.StrictStubs.class )
public class NiceyDiceGameTest
{
    private NiceyDiceGame subject;

    @Test
    public void givenInstantiation_boardIsReadyToPlay()
    {
        subject = niceyDiceGame( board( cardStock( card( dF( 1 ), dF( 2 ), dF( 3 ) ) ) ),
            players( name( "Player A" ), name( "Player B" ) ) );

        assertThat( subject ).as( "Test" );
        // TODO: ...
    }

    @Test
    public void givenDiceRolledAndPlayersTurn_PromptPlayerForDicePlacementShouldWork()
    {
        subject = niceyDiceGame( board( cardStock( card( dF( 1 ), dF( 2 ), dF( 3 ) ) ) ),
                players( name( "Player A" ), name( "Player B" ) ) )
            .rollDiceForCurrentPlayer();

        assertThat( subject.promptCurrentPlayerForDicePlacement() );
        // player produces valid move | invalid move (IllegalStateException)
        // if valid move, that it was applied as expected.
    }
}
