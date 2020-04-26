package org.shaba.nicey_dice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.shaba.nicey_dice.player.*;
import org.shaba.nicey_dice.util.printer.*;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.shaba.nicey_dice.Board.board;
import static org.shaba.nicey_dice.Card.card;
import static org.shaba.nicey_dice.CardStock.cardStock;
import static org.shaba.nicey_dice.DiceFace.dF;
import static org.shaba.nicey_dice.FieldCard.fieldCard;
import static org.shaba.nicey_dice.NiceyDiceGame.niceyDiceGame;
import static org.shaba.nicey_dice.Players.players;
import static org.shaba.nicey_dice.player.Player.Name.name;

@RunWith ( MockitoJUnitRunner.StrictStubs.class )
public class NiceyDiceGameTest
{
    private NiceyDiceGame subject;
    
    private final Player.Name playerNameA = name( "Player A" );
    private final Player.Name playerNameB = name( "Player B" );

    @Test
    public void givenInstantiation_boardIsReadyToPlay()
    {
        subject = niceyDiceGame( board( cardStock( card( dF( 1 ), dF( 2 ), dF( 3 ) ) ) ),
            players( playerNameA, playerNameB ) );

        assertThat( subject ).as( "Test is ready to play" )
            .returns(playerNameA, g -> g.getCurrentPlayer().getName())
            .satisfies(g -> {
                assertThat(g.getBoard().getFieldCards())
                    .isEmpty();
                assertThat(g.getBoard().getCardStock().cardsRemaining())
                    .isOne();
                assertThat(g.getBoard().getCardStock().draw()._2())
                    .isEqualTo( card( dF( 1 ), dF( 2 ), dF( 3 ) ) );
                assertThat(g.fillField().getBoard().getFieldCards())
                    .containsExactly( fieldCard( card( dF( 1 ), dF( 2 ), dF( 3 ) ) ) );
            });
    }

    @Test
    public void givenDiceRolledAndPlayersTurn_PromptPlayerForDicePlacementShouldWork()
    {
        subject = niceyDiceGame( board( cardStock( card( dF( 1 ), dF( 2 ), dF( 3 ) ) ) ),
                players( playerNameA, playerNameB ) )
            .rollDiceForCurrentPlayer();

        final Player playerB = subject.getPlayers().nextPlayer().currentPlayer();

        assertThat( subject.promptCurrentPlayerToProposeMove() )
            .first()
            .returns(playerB, NiceyDiceGame::getCurrentPlayer);
    }

    @Test
    public void shouldPlayOutGame()
    {
        final GamePrinter gamePrinter = new GamePrinter(
            new PlayersPrinter(
                new PlayerPrinter(true),
                DiceFacePrinter.diceFacePrinterWithEmojis()),
            new FieldCardPrinter(DiceFacePrinter.diceFacePrinterWithEmojis()));

        subject = niceyDiceGame( board( cardStock(
            card( dF( 1 ), dF( 2 ), dF( 3 ) ),
            card( dF( 4 ), dF( 4 ), dF( 4 ) ),
            card( dF( 4 ), dF( 4 ), dF( 4 ) ),
            card( dF( 5 ), dF( 5 ), dF( 6 ) ),
            card( dF( 2 ), dF( 6 ) ) ) ),
//            card( dF( 1 ), dF( 2 ), dF( 3 ), dF( 4 ), dF( 4 ), dF( 4 ) ),
//            card( dF( 4 ), dF( 4 ), dF( 4 ), dF( 4 ), dF( 4 ), dF( 4 ) ),
//            card( dF( 4 ), dF( 4 ), dF( 4 ) ),
//            card( dF( 5 ), dF( 5 ), dF( 6 ) ),
//            card( dF( 2 ), dF( 6 ) ) ) ),
                new Players(
                    new FocusedTestPlayer( name( "Player 1" ) ),
                    new SimpleTestPlayer(  name( "Simpleton 2" ) ) ) )
                .fillField();

        final AtomicLong moves = new AtomicLong();
        final Scanner sc = new Scanner(System.in);
        while(!subject.isOutOfCards()) {
            System.out.println(gamePrinter.print(subject));
//            sc.nextLine();
            moves.incrementAndGet();
            assertThatCode(() ->
                    subject = subject.promptCurrentPlayerToProposeMove().get())
                .doesNotThrowAnyException();
        }
        sc.close();

        subject.getPlayers()
                .forEach(p -> System.out.printf("%15s : %3d points%n",
                    p.getName().getValue(), p.getPoints().getValue()));
        System.out.printf("Game took %d moves%n", moves.get());

    }
}
