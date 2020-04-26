package org.shaba.nicey_dice;

import org.shaba.nicey_dice.player.Move;
import org.shaba.nicey_dice.player.Player;

import lombok.AccessLevel;

import java.util.List;
import java.util.Map;

import io.vavr.control.Try;
import one.util.streamex.StreamEx;

@lombok.Data
public class PlayerMovePrompt
{
    @lombok.Getter ( AccessLevel.PRIVATE )
    private final NiceyDiceGame game;
    @lombok.Getter ( AccessLevel.PRIVATE )
    private final Player        player;
    private final Class<? extends Move> moveType;

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
    PlayerMovePrompt(
            final NiceyDiceGame game,
            final Player player,
            final Class<? extends Move> moveType)
    {
        if ( game.getPlayers().currentPlayer() != player )
            throw new IllegalStateException(
                    "This player cannot make a move since it is not the turn of this player." );

        this.game = game;
        this.player = player;
        this.moveType = moveType;
    }

    public Try<Move> promptPlayerMove()
    {
        return getPlayer().obtainMove( this );
    }

    public Board getBoard()
    {
        return getGame().getBoard();
    }

    public RolledDice getRolledDice()
    {
        return getGame().getCurrentPlayerRolledDice();
    }

    public Map<Player.Name, List<ScoredCard>> getScoreBoard()
    {
        return StreamEx.of( getGame().getPlayers().iterator() )
                .mapToEntry( Player::getName, Player::getScoredCards ).toMap();
    }

    public static PlayerMovePrompt currentPlayerMovePrompt(
            @lombok.NonNull final NiceyDiceGame game)
    {
        return new PlayerMovePrompt(
            game,
            game.getCurrentPlayer(),
            game.getNextMoveTypeNeededForCurrentPlayer());
    }
}
