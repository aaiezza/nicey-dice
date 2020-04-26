package org.shaba.nicey_dice.util.printer;

import static java.lang.String.format;

import org.shaba.nicey_dice.NiceyDiceGame;

@lombok.Data
public class GamePrinter implements Printer<NiceyDiceGame> {
    private static final String GAME = "%s%n%s%n---------";

    private final PlayersPrinter playerPrinter;
    private final FieldCardPrinter fieldCardPrinter;

    /*
     * ☆ Player 1 [10 points]
     *   Player 2 [ 5 points]
     *
     * ☆ Player 1 [10 points]: ⚀ ⚃ ⚄
     *   Player 2 [ 5 points]
     *
     * ┏━━━━━┓
     * ┃⚀    ┃   Player 1: ⚀ ⚃
     * ┃  ⚃  ┃   Player 2: ⚃ ⚄
     * ┃    ⚄┃
     * ┗━━━━━┛
     * ┏━━━━━┓
     * ┃⚀    ┃   Player 1: ⚀
     * ┃     ┃
     * ┃    ⚀┃
     * ┗━━━━━┛
     * --------
     *
     */
    @Override
    public String print(final NiceyDiceGame game) {
        return format(GAME,
            playerPrinter.printWithCurrentPlayersDice(game.getCurrentPlayerRolledDice()).apply(game.getPlayers()),
            fieldCardPrinter.print(game.getBoard().getFieldCards()));
    }
}
