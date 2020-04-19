package org.shaba.nicey_dice.player.move;

import org.shaba.nicey_dice.NiceyDiceGame;
import org.shaba.nicey_dice.player.Move;
import org.shaba.nicey_dice.player.Player;

import java.util.function.Function;

import static org.shaba.nicey_dice.NiceyDiceGame.niceyDiceGame;

public interface PreRollMove extends Move {

    public class RollDice implements PreRollMove {
        @Override
        public Function<Player, NiceyDiceGame> apply(final NiceyDiceGame game) {
            return player -> {
                return game.rollDiceForCurrentPlayer();
            };
        }
    }

    public class TakeBackDiceInField implements PreRollMove {
        @Override
        public Function<Player, NiceyDiceGame> apply(final NiceyDiceGame game) {
            return player -> niceyDiceGame(
                game.getBoard().removeDiceForPlayer(player),
                game.getPlayers());
        }
    }
}
