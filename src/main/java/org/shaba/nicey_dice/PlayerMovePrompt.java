package org.shaba.nicey_dice;

import org.shaba.nicey_dice.player.Move;
import org.shaba.nicey_dice.player.Player;

import lombok.AccessLevel;

import java.util.List;
import java.util.Map;

import io.vavr.control.Option;
import io.vavr.control.Try;
import one.util.streamex.StreamEx;

@lombok.Data
public class PlayerMovePrompt
{
    @lombok.Getter ( AccessLevel.NONE )
    private final NiceyDiceGame game;
    @lombok.Getter ( AccessLevel.NONE )
    private final Player        player;

    /**
     * Should be created when the gameplay dictates that it is time for the
     * given player to propose a move to make. <br/>
     * It must be the given player's turn, and the dice must have been rolled
     * already.<br/>
     * If the dice have not been rolled for this turn yet, this method throws an
     * {@link IllegalArgumentException}. <br/>
     * Also, if it is not this player's turn, the rolled dice are not those of
     * this player, and this condition will throw an
     * {@link IllegalArgumentException}.
     */
    public PlayerMovePrompt( final NiceyDiceGame game, final Player player )
    {
        if ( game.getPlayers().currentPlayer() != player )
            throw new IllegalStateException(
                    "This player cannot make a move since it is not the turn of this player." );
        if ( game.getCurrentPlayerRolledDice() == RolledDice.UNROLLED )
            throw new IllegalStateException(
                    "This player cannot make a move since player has not rolled dice." );

        this.game = game;
        this.player = player;
    }

    public Try<Move> promptPlayerMove()
    {
        return player.proposeMove( this );
    }

    public Board getBoard()
    {
        return game.getBoard();
    }

    public RolledDice getRolledDice()
    {
        return game.getCurrentPlayerRolledDice();
    }

    public Map<Player.Name, List<ScoredCard>> getScoreBoard()
    {
        return StreamEx.of( game.getPlayers().iterator() )
                .mapToEntry( Player::getName, Player::getScoredCards ).toMap();
    }

    public static Try<PlayerMovePrompt> currentPlayerMovePrompt( final NiceyDiceGame game )
    {
        return Option.of( game )
                .toTry()
                .map( g -> new PlayerMovePrompt( g, g.getPlayers().currentPlayer() ) );
    }
}
