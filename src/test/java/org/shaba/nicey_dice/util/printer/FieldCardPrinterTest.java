package org.shaba.nicey_dice.util.printer;

import static java.lang.String.format;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.shaba.nicey_dice.Card;
import org.shaba.nicey_dice.FieldCard;
import org.shaba.nicey_dice.player.Player;
import org.shaba.nicey_dice.player.Player.Name;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.shaba.nicey_dice.Card.cardWithCustomPoints;
import static org.shaba.nicey_dice.DiceFace.dF;
import static org.shaba.nicey_dice.FieldCard.fieldCard;
import static org.shaba.nicey_dice.Points.points;
import static org.shaba.nicey_dice.player.Player.Name.name;
import static org.shaba.nicey_dice.util.printer.DiceFacePrinter.diceFacePrinterWithEmojis;
import static org.shaba.nicey_dice.util.printer.DiceFacePrinter.diceFacePrinterWithoutEmojis;

import one.util.streamex.IntStreamEx;

public class FieldCardPrinterTest {
    private FieldCardPrinter subject;

    @Rule public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldPrintAsExpectedWithEmojis() {
        subject = new FieldCardPrinter(diceFacePrinterWithEmojis());
        assertFieldCard(0, "┏━━━━━━┓\n" + 
                "┃      ┃\n" + 
                "┃  4️⃣   ┃\n" + 
                "┃      ┃\n" + 
                "┗━━━━━━┛",
                       cardWithCustomPoints(points(12), dF(4)));
        assertFieldCard(1, "┏━━━━━━┓\n" + 
                "┃2️⃣     ┃   Player 1: 4️⃣\n" + 
                "┃      ┃\n" + 
                "┃    4️⃣ ┃\n" + 
                "┗━━━━━━┛",
                       cardWithCustomPoints(points(12), dF(4), dF(2)));
        assertFieldCard(2, "┏━━━━━━┓\n" + 
                "┃1️⃣     ┃   Player 1: 4️⃣\n" + 
                "┃  4️⃣   ┃   Player 2: 4️⃣\n" + 
                "┃    5️⃣ ┃\n" + 
                "┗━━━━━━┛",
                       cardWithCustomPoints(points(12), dF(1), dF(4), dF(5)));
        assertFieldCard(3, "┏━━━━━━┓\n" + 
                "┃4️⃣   4️⃣ ┃   Player 1: 4️⃣\n" + 
                "┃      ┃   Player 2: 4️⃣\n" + 
                "┃4️⃣   5️⃣ ┃   Player 3: 4️⃣\n" + 
                "┗━━━━━━┛",
                       cardWithCustomPoints(points(12), dF(4), dF(4), dF(5), dF(4)));
        assertFieldCard(4, "┏━━━━━━┓\n" + 
                "┃1️⃣   2️⃣ ┃   Player 1: 4️⃣\n" + 
                "┃  3️⃣   ┃   Player 2: 4️⃣\n" + 
                "┃4️⃣   5️⃣ ┃   Player 3: 4️⃣\n" + 
                "┗━━━━━━┛   Player 4: 4️⃣",
                       cardWithCustomPoints(points(12), dF(1), dF(3), dF(4), dF(5), dF(2)));
        assertFieldCard(5, "┏━━━━━━┓\n" + 
                "┃4️⃣   4️⃣ ┃   Player 1: 4️⃣\n" + 
                "┃4️⃣   4️⃣ ┃   Player 2: 4️⃣\n" + 
                "┃4️⃣   4️⃣ ┃   Player 3: 4️⃣\n" + 
                "┗━━━━━━┛   Player 4: 4️⃣\n" + 
                "           Player 5: 4️⃣",
                       cardWithCustomPoints(points(12), dF(4), dF(4), dF(4), dF(4), dF(4), dF(4)));
        assertFieldCard(6, "┏━━━━━━┓\n" + 
                "┃1️⃣   2️⃣ ┃   Player 1: 4️⃣\n" + 
                "┃3️⃣   4️⃣ ┃   Player 2: 4️⃣\n" + 
                "┃5️⃣   6️⃣ ┃   Player 3: 4️⃣\n" + 
                "┗━━━━━━┛   Player 4: 4️⃣\n" + 
                "           Player 5: 4️⃣\n" + 
                "           Player 6: 4️⃣",
                       cardWithCustomPoints(points(12), dF(1), dF(3), dF(4), dF(5), dF(2), dF(6)));
    }

    @Test
    public void shouldPrintAsExpectedWithoutEmojis() {
        subject = new FieldCardPrinter(diceFacePrinterWithoutEmojis());
        assertFieldCard(0, "┏━━━━━┓\n" + 
                           "┃     ┃\n" + 
                           "┃  ⚃  ┃\n" + 
                           "┃     ┃\n" + 
                           "┗━━━━━┛",
                       cardWithCustomPoints(points(12), dF(4)));
        assertFieldCard(1, "┏━━━━━┓\n" + 
                           "┃⚁    ┃   Player 1: ⚃\n" + 
                           "┃     ┃\n" + 
                           "┃    ⚃┃\n" + 
                           "┗━━━━━┛",
                       cardWithCustomPoints(points(12), dF(4), dF(2)));
        assertFieldCard(2, "┏━━━━━┓\n" + 
                           "┃⚀    ┃   Player 1: ⚃\n" + 
                           "┃  ⚃  ┃   Player 2: ⚃\n" + 
                           "┃    ⚄┃\n" + 
                           "┗━━━━━┛",
                       cardWithCustomPoints(points(12), dF(1), dF(4), dF(5)));
        assertFieldCard(3, "┏━━━━━┓\n" + 
                           "┃⚃   ⚃┃   Player 1: ⚃\n" + 
                           "┃     ┃   Player 2: ⚃\n" + 
                           "┃⚃   ⚄┃   Player 3: ⚃\n" + 
                           "┗━━━━━┛",
                       cardWithCustomPoints(points(12), dF(4), dF(4), dF(5), dF(4)));
        assertFieldCard(4, "┏━━━━━┓\n" + 
                           "┃⚀   ⚁┃   Player 1: ⚃\n" + 
                           "┃  ⚂  ┃   Player 2: ⚃\n" + 
                           "┃⚃   ⚄┃   Player 3: ⚃\n" + 
                           "┗━━━━━┛   Player 4: ⚃",
                       cardWithCustomPoints(points(12), dF(1), dF(3), dF(4), dF(5), dF(2)));
        assertFieldCard(5, "┏━━━━━┓\n" + 
                           "┃⚃   ⚃┃   Player 1: ⚃\n" + 
                           "┃⚃   ⚃┃   Player 2: ⚃\n" + 
                           "┃⚃   ⚃┃   Player 3: ⚃\n" + 
                           "┗━━━━━┛   Player 4: ⚃\n" + 
                           "          Player 5: ⚃",
                       cardWithCustomPoints(points(12), dF(4), dF(4), dF(4), dF(4), dF(4), dF(4)));
        assertFieldCard(6, "┏━━━━━┓\n" + 
                           "┃⚀   ⚁┃   Player 1: ⚃\n" + 
                           "┃⚂   ⚃┃   Player 2: ⚃\n" + 
                           "┃⚄   ⚅┃   Player 3: ⚃\n" + 
                           "┗━━━━━┛   Player 4: ⚃\n" + 
                           "          Player 5: ⚃\n" + 
                           "          Player 6: ⚃",
                       cardWithCustomPoints(points(12), dF(1), dF(3), dF(4), dF(5), dF(2), dF(6)));
    }
    private void assertFieldCard(final int numberOfPlayers, final String expected, final Card card) {
        final FieldCard fieldCard = IntStreamEx.range(numberOfPlayers)
            .sorted()
            .mapToObj(i -> name(format("Player %d", i+1)))
            .map(this::mockPlayer)
            .reduce(
                fieldCard(card),
                (fc, player) ->
                    fc.addPlayerClaim(player, dF(4)),
                (l, r) -> l);

        System.out.printf("%nFieldCard:%n%s%n--------%n", subject.print(fieldCard));

        softly.assertThat(subject.print(fieldCard))
            .isEqualTo(expected);
    }

    private Player mockPlayer(final Name name) {
        final Player player = mock(Player.class);
        when(player.getName()).thenReturn(name);
        return player;
    }
}
