package org.shaba.nicey_dice.util.printer;

import static java.lang.String.format;

import org.shaba.nicey_dice.Players;
import org.shaba.nicey_dice.RolledDice;

import java.util.function.Function;

import one.util.streamex.StreamEx;

@lombok.Data
public class PlayersPrinter implements Printer<Players> {
    private static final String FIRST_PLAYER = "☆ %s";
    private static final String FIRST_PLAYER_WITH_DICE = "%s: %s";
    private static final String PLAYER = "  %s";

    private final PlayerPrinter playerPrinter;
    private final DiceFacePrinter diceFacePrinter;

    @Override
    public String print(final Players players) {
        return  StreamEx.of(players.iterator())
            .map(playerPrinter::print)
            .mapFirstOrElse(
                p -> format(FIRST_PLAYER, p),
                p -> format(PLAYER, p))
            .joining("\n");
    }

    public Function<Players, String> printWithCurrentPlayersDice(final RolledDice rolledDice) {
        return players -> StreamEx.of(print(players).lines())
                .mapFirst(p -> rolledDice.isUnrolled()? p : format(
                    FIRST_PLAYER_WITH_DICE,
                    p,
                    diceFacePrinter
                        .print(StreamEx.of(rolledDice.iterator()).toList())))
                .joining("\n");
    }
}
