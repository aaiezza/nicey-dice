package org.shaba.nicey_dice;

import static io.vavr.API.Success;

import org.shaba.nicey_dice.player.Move;
import org.shaba.nicey_dice.player.Player;
import org.shaba.nicey_dice.player.move.PostRollMove;
import org.shaba.nicey_dice.player.move.PreRollMove;

import java.util.function.Function;

import static org.shaba.nicey_dice.PlayerMovePrompt.currentPlayerMovePrompt;
import static org.shaba.nicey_dice.factories.DiceRoller.ROLLER;

import io.vavr.control.Try;

@lombok.Data
public class NiceyDiceGame
{
    private final Board      board;
    private final Players    players;
    private final RolledDice currentPlayerRolledDice;

    public static NiceyDiceGame niceyDiceGame( final Board board, final Players players )
    {
        return new NiceyDiceGame( board, players, RolledDice.UNROLLED );
    }

    public int getNumberOfPlayers()
    {
        return getPlayers().getNumberOfPlayers();
    }

    public Player getCurrentPlayer() {
        return getPlayers().currentPlayer();
    }

    public NiceyDiceGame fillField()
    {
        return new NiceyDiceGame( getBoard().fillField(), getPlayers(), getCurrentPlayerRolledDice() );
    }

    public Class<? extends Move> getNextMoveTypeNeededForCurrentPlayer() {
        return getCurrentPlayerRolledDice().isUnrolled() ? PreRollMove.class : PostRollMove.class;
    }

    /**
     * The current player is prompted to either roll the dice or take back all
     * of his PlacedDice given the current (immutable) state of the game.
     * 
     * @return The game as it exists after the player has made a valid decision
     *         to either roll, or take back all of his dice, and it was applied
     *         to the this current game state.
     */
    public Try<NiceyDiceGame> promptCurrentPlayerToProposeMove()
    {
        return Success(currentPlayerMovePrompt( this ))
                .flatMap( PlayerMovePrompt::promptPlayerMove )
                // TODO?: .map( MoveValidator::validateMove )
                .map( applyMoveToGame() );
    }

    public NiceyDiceGame rollDiceForCurrentPlayer()
    {
        return getCurrentPlayerRolledDice().isUnrolled() ?
                new NiceyDiceGame(
                    board,
                    players,
                    ROLLER.rollDice( getCurrentPlayer()
                        .getRollableDice( board.getFieldCards() ) ) )
                : this;
    }

    private Function<Move, NiceyDiceGame> applyMoveToGame()
    {
        return move -> move.apply(this).apply(getCurrentPlayer());
    }
}
