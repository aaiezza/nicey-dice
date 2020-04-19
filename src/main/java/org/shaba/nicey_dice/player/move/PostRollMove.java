package org.shaba.nicey_dice.player.move;

import org.shaba.nicey_dice.*;
import org.shaba.nicey_dice.RolledDice.DetachedDie;
import org.shaba.nicey_dice.player.Move;
import org.shaba.nicey_dice.player.Player;

import java.util.function.Function;

import static org.shaba.nicey_dice.NiceyDiceGame.niceyDiceGame;

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
            // TODO Score cards!
            return player -> {
                final DetachedDie detachedDie = game
                        .getCurrentPlayerRolledDice()
                        .select(diceFace);
                final Board board = game
                        .getBoard()
                        .placeDiceForPlayer(fieldCard, player, detachedDie.getChosenDie());
                return new NiceyDiceGame(board, game.getPlayers(), detachedDie.getRolledDice());
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
