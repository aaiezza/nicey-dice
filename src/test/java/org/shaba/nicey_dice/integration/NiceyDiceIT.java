package org.shaba.nicey_dice.integration;

import org.junit.Test;
import org.shaba.nicey_dice.FieldCards;
import org.shaba.nicey_dice.NiceyDiceGame;
import org.shaba.nicey_dice.factories.NiceyDiceGameFactory;
import org.shaba.nicey_dice.player.Player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.shaba.nicey_dice.Points.points;

public class NiceyDiceIT
{
    private NiceyDiceGameFactory gameFactory = new NiceyDiceGameFactory();

    /**
     * Tests that:
     * <ul>
     * <li>There are {@code 2} players</li>
     * <li>{@link FieldCards#MAXIMUM_NUMBER_OF_CARDS_IN_FIELD 3} cards in
     * field</li>
     * <li>All players:
     * <ul>
     * <li>have no points yet</li>
     * <li>have {@link Player#NUMBER_OF_TOTAL_DICE 6} dice to roll with</li>
     * </ul>
     * </li>
     * </ul>
     */
    @Test
    public void givenNewGame_boardIsReadyToPlay()
    {
        final NiceyDiceGame game = gameFactory.createForNumberOfPlayers( 2 );

        assertThat( game.getNumberOfPlayers() ).isEqualTo( 2 );

        assertThat( game.getBoard().getFieldCards().size() )
            .as( "Three scorable cards in the field" )
            .isEqualTo( FieldCards.MAXIMUM_NUMBER_OF_CARDS_IN_FIELD );

        printFieldCards( game );

        assertThat( game.getPlayers() )
            .extracting(
                Player::getPoints,
                p -> p.getRollableDice( game.getBoard().getFieldCards() ) )
            .containsOnly(
                tuple( points( 0 ), Player.NUMBER_OF_TOTAL_DICE ),
                tuple( points( 0 ), Player.NUMBER_OF_TOTAL_DICE ) );

        printPlayers( game );
    }

    @Test
    public void givenGame_FirstPlayerRollDice()
    {
        final NiceyDiceGame game = gameFactory.createForNumberOfPlayers( 2 )
                .rollDiceForCurrentPlayer();

        assertThat( game.getCurrentPlayerRolledDice() ).hasSize( 6 );
    }

    private void printFieldCards( final NiceyDiceGame game )
    {
        final StringBuilder fieldCards = new StringBuilder();
        game.getBoard().getFieldCards().cardStream().forEach( c -> {
            fieldCards.append( "  " ).append( c ).append( "\n" );
        } );
        System.out.printf( "Field Cards:\n%s", fieldCards );
    }

    private void printPlayers( final NiceyDiceGame game )
    {
        final StringBuilder players = new StringBuilder();
        game.getPlayers().forEach( p -> {
            players.append( "  " ).append( p ).append( "\n" );
        } );
        System.out.printf( "Players:\n%s", players );
    }
}
