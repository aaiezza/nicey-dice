package org.shaba.nicey_dice;

import static io.vavr.API.Success;
import static java.lang.String.format;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.shaba.nicey_dice.player.Move;
import org.shaba.nicey_dice.player.Player;
import org.shaba.nicey_dice.player.move.PostRollMove;
import org.shaba.nicey_dice.player.move.PreRollMove;
import org.shaba.nicey_dice.util.MoveGeneratorUtil;

import java.util.List;

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

import io.vavr.API;
import io.vavr.control.Option;
import io.vavr.control.Try;
import one.util.streamex.StreamEx;

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
        subject = niceyDiceGame( board( cardStock(
            card( dF( 1 ), dF( 2 ), dF( 3 ) ),
            card( dF( 4 ), dF( 4 ), dF( 4 ) ),
            card( dF( 5 ), dF( 5 ), dF( 6 ) ),
            card( dF( 2 ), dF( 4 ), dF( 6 ) ) ) ),
                new Players(
                    new FocusedPlayer( name( "Player 1" ) ),
                    new FocusedPlayer( name( "Player 2" ) ) ) )
                .fillField();

        while(!subject.isOutOfCards()) {
            assertThatCode(() ->
                    subject = subject.promptCurrentPlayerToProposeMove().get())
                .doesNotThrowAnyException();
        }

        subject.getPlayers()
                .forEach(p -> System.out.printf("%10s : %3d points%n",
                    p.getName().getValue(), p.getPoints().getValue()));

    }

    private static class FocusedPlayer extends Player {
        private final MoveGeneratorUtil moveGenerator;

        protected FocusedPlayer(final Name name) {
            super(name);
            this.moveGenerator = new MoveGeneratorUtil();
        }

        protected FocusedPlayer(final Name name, final List<ScoredCard> scoredCards) {
            super(name, scoredCards);
            this.moveGenerator = new MoveGeneratorUtil();
        }

        @Override
        public Player withScoredCard(final ScoredCard scoredCard) {
            return new FocusedPlayer(
                getName(),
                StreamEx.of(getScoredCards()).append( scoredCard ).toImmutableList() );
        }

        @Override
        public Try<Move> proposeMove(final PlayerMovePrompt movePrompt) {
            if(PreRollMove.class.isAssignableFrom(movePrompt.getMoveType())) {
                if(getRollableDice(movePrompt.getBoard().getFieldCards()) > 0)
                    return Success(new PreRollMove.RollDice());
                else return Success(new PreRollMove.TakeBackDiceInField());
            } else if (PostRollMove.class.isAssignableFrom(movePrompt.getMoveType())) {
                return Option.ofOptional(moveGenerator.generate(movePrompt, this)
                    .stream()
                    .findFirst())
                    .fold(() -> Success(new PostRollMove.EndTurn()), Try::success);
            } else {
                return API.Failure(
                    new IllegalArgumentException(
                            format("Unknown Move Type: %s", movePrompt.getMoveType().getSimpleName())));
            }
        }
    }
}
