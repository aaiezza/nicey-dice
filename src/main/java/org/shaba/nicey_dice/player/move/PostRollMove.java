package org.shaba.nicey_dice.player.move;

import org.shaba.nicey_dice.*;
import org.shaba.nicey_dice.RolledDice.DetachedDie;
import org.shaba.nicey_dice.player.Move;
import org.shaba.nicey_dice.player.Player;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.shaba.nicey_dice.NiceyDiceGame.niceyDiceGame;

import one.util.streamex.StreamEx;

public interface PostRollMove extends Move
{
    @lombok.Value
    @lombok.experimental.NonFinal
    public class WorkOnAFieldCard implements PostRollMove
    {
        private final DiceFace  diceFace;
        private final FieldCard fieldCard;

        public WorkOnAFieldCard(
            final Player player,
            final DiceFace diceFace,
            final FieldCard fieldCard )
        {
            if ( !fieldCard.getClaimCriteria().contains( diceFace ) )
                throw new IllegalArgumentException( "Field card does NOT contain that dice face." );
            if ( fieldCard.claimForDiceFaceFulfilled( diceFace ).test(player) )
                throw new IllegalArgumentException( String.format(
                    "Player %s already has the required number of %s placed on this card",
                    player,
                    diceFace ) );

            this.diceFace = diceFace;
            this.fieldCard = fieldCard;
        }

        @Override
        public Function<Player, NiceyDiceGame> apply(final NiceyDiceGame game) {
            return player -> {
                final DetachedDie detachedDie = game
                        .getCurrentPlayerRolledDice()
                        .select(getDiceFace());
                final AtomicReference<Board> board = new AtomicReference<>(game
                        .getBoard()
                        .placeDiceForPlayer(getFieldCard(), player, detachedDie.getChosenDie()));
                final Players players = StreamEx.of(board.get().getFieldCards().iterator())
                        .filter(fieldCard -> fieldCard.playerMeetsCriteria(player))
                        .reduce(
                            game.getPlayers(),
                            (plyrs, scoreableCard) -> scoreableCard.scoreCardFor(player)
                                    .map(player::withScoredCard)
                                    .map(plyrs::replacePlayer)
                                    .peek(__ ->
                                        board.set(new Board(board.get().getCardStock(),
                                            board.get()
                                            .getFieldCards()
                                            .scoreCard(scoreableCard))))
                                    .toJavaOptional()
                                    .orElse(plyrs),
                            (p1s, p2s) -> p1s);
                return new NiceyDiceGame(
                    board.get().fillField(),
                    players,
                    detachedDie.getRolledDice());
            };
        }
    }

    public class EndTurn implements PostRollMove {
        @Override
        public Function<Player, NiceyDiceGame> apply(final NiceyDiceGame game) {
            return player -> niceyDiceGame(
                game.getBoard(),
                game.getPlayers().nextPlayer());
        }
    }
}
