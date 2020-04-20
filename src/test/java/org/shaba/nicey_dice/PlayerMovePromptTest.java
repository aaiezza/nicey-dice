package org.shaba.nicey_dice;

import static io.vavr.API.Success;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.shaba.nicey_dice.player.Move;
import org.shaba.nicey_dice.player.Player;
import org.shaba.nicey_dice.player.move.PostRollMove.EndTurn;
import org.shaba.nicey_dice.player.move.PreRollMove;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith ( MockitoJUnitRunner.StrictStubs.class )
public class PlayerMovePromptTest
{
    private PlayerMovePrompt subject;
    @Mock
    private NiceyDiceGame    game;
    @Mock
    private Players          players;
    @Mock
    private RolledDice       rolledDice;
    @Mock
    private Player           player;
    @Mock
    private Move             move;

    @Before
    public void setup()
    {
        when( game.getPlayers() ).thenReturn( players );
    }

    @Test
    public void givenNotPlayersTurn_PromptPlayerForDicePlacementShouldFail()
    {
        mockThatItIsNotTheTurnOfThisPlayer();
        assertThatThrownBy( this::instantiatePlayerMovePrompt )
                .isInstanceOf( IllegalStateException.class )
                .hasMessageContaining( "not the turn of this player" );
    }

    public void givenPlayersTurn_PromptPlayerForDicePlacementShouldWork()
    {
        mockThatItIsTheTurnOfThisPlayer();
        assertThatCode( this::instantiatePlayerMovePrompt ).doesNotThrowAnyException();
    }

    @Test
    public void whenPlayerProposesNullMove_CountsAsEndTurn()
    {
        mockThatItIsTheTurnOfThisPlayer();
        instantiatePlayerMovePrompt();
        move = new EndTurn();
        when( player.proposeMove( same( subject ) ) ).thenReturn( Success( move ) );
        assertThat( subject.promptPlayerMove() ).isEqualTo( Success( move ) );
    }

    @Test
    public void whenPlayerProposesValidMove_MoveIsReturned()
    {
        mockThatItIsTheTurnOfThisPlayer();
        instantiatePlayerMovePrompt();
        when( player.proposeMove( same( subject ) ) ).thenReturn( Success( move ) );
        assertThat( subject.promptPlayerMove().get() ).isSameAs( move );
    }

    private void instantiatePlayerMovePrompt()
    {
        subject = new PlayerMovePrompt( game, player, PreRollMove.class );
    }

    private void mockThatItIsTheTurnOfThisPlayer()
    {
        when( players.currentPlayer() ).thenReturn( player );
    }

    private void mockThatItIsNotTheTurnOfThisPlayer()
    {
        when( players.currentPlayer() ).thenReturn( mock( Player.class ) );
    }
}
