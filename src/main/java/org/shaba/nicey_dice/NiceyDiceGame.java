package org.shaba.nicey_dice;

import org.shaba.nicey_dice.player.Move;

import java.util.function.Function;

import static org.shaba.nicey_dice.PlayerMovePrompt.currentPlayerMovePrompt;
import static org.shaba.nicey_dice.factories.DiceRoller.ROLLER;

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
        return players.getNumberOfPlayers();
    }

    public NiceyDiceGame fillField()
    {
        return new NiceyDiceGame( board.fillField(), players, currentPlayerRolledDice );
    }

    /**
     * The current player is prompted to either roll the dice or take back all
     * of his PlacedDice given the current (immutable) state of the game.
     * 
     * @return The game as it exists after the player has made a valid decision
     *         to either roll, or take back all of his dice, and it was applied
     *         to the this current game state.
     */
    public NiceyDiceGame promptCurrentPlayerToRollDice()
    {
        return currentPlayerMovePrompt( this )
                .flatMap( PlayerMovePrompt::promptPlayerMove )
                // TODO: .map( MoveValidator::validateMove )
                .map( applyMoveToGame() ).get();
    }

    public NiceyDiceGame rollDiceForCurrentPlayer()
    {
        return currentPlayerRolledDice == RolledDice.UNROLLED ? new NiceyDiceGame( board, players,
                ROLLER.rollDice(
                    players.currentPlayer().getRollableDice( board.getFieldCards() ) ) ) : this;
    }

    /**
     * The current player is prompted for a plan of play given the current
     * (immutable) state of the game.
     * 
     * @return The game as it exists after the player has made a valid decision
     *         to play and it was applied to the this current game state.
     */
    public NiceyDiceGame promptCurrentPlayerForDicePlacement()
    {
        return currentPlayerMovePrompt( this ).flatMap( PlayerMovePrompt::promptPlayerMove )
                // TODO: .map( MoveValidator::validateMove )
                .map( applyMoveToGame() ).get();
    }

    private Function<Move, NiceyDiceGame> applyMoveToGame()
    {
        return move -> {

            // TODO
            return this;
        };
    }
}
