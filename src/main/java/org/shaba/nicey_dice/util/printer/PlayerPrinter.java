package org.shaba.nicey_dice.util.printer;

import static java.lang.String.format;

import org.shaba.nicey_dice.player.Player;

@lombok.Data
public class PlayerPrinter implements Printer<Player> {
    private static final String NAME           = "%15s";
    private static final String NAME_AND_SCORE = NAME + " [%3d §]";

    private final boolean withScore;

    @Override
    public String print(final Player player) {
        if (isWithScore()) {
            return format(NAME_AND_SCORE, player.getName().getValue(), player.getPoints().getValue());
        } else {
            return format(NAME, player.getName().getValue());
        }
    }

    public String print(final Player.Name name) {
        return format(NAME, name.getValue());
    }
}
