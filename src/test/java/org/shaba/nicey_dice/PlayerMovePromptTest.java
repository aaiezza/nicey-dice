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
import static org.shaba.nicey_dice.RolledDice.UNROLLED;

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

    @Test
    public void givenPlayersTurnAndUnrolledDice_PromptPlayerForDicePlacementShouldFail()
    {
        mockThatItIsTheTurnOfThisPlayer();
        mockDiceUnrolled();
        assertThatThrownBy( this::instantiatePlayerMovePrompt )
                .isInstanceOf( IllegalStateException.class )
                .hasMessageContaining( "player has not rolled dice" );
    }

    @Test
    public void givenPlayersTurnAndRolledDice_PromptPlayerForDicePlacementShouldWork()
    {
        mockThatItIsTheTurnOfThisPlayer();
        mockDiceRolled();
        assertThatCode( this::instantiatePlayerMovePrompt ).doesNotThrowAnyException();
    }

    @Test
    public void whenPlayerProposesNullMove_CountsAsEndTurn()
    {
        mockThatItIsTheTurnOfThisPlayer();
        mockDiceRolled();
        instantiatePlayerMovePrompt();
        move = new EndTurn();
        when( player.proposeMove( same( subject ) ) ).thenReturn( Success( move ) );
        assertThat( subject.promptPlayerMove() ).isEqualTo( Success( move ) );
    }

    @Test
    public void whenPlayerProposesValidMove_MoveIsReturned()
    {
        mockThatItIsTheTurnOfThisPlayer();
        mockDiceRolled();
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

    private void mockDiceRolled()
    {
        when( game.getCurrentPlayerRolledDice() ).thenReturn( rolledDice );
    }

    private void mockDiceUnrolled()
    {
        when( game.getCurrentPlayerRolledDice() ).thenReturn( UNROLLED );
    }
}
