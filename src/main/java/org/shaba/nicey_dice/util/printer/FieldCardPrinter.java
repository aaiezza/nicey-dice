package org.shaba.nicey_dice.util.printer;

import static io.vavr.API.Try;
import static java.lang.Math.max;
import static java.lang.String.format;
import static one.util.streamex.StreamEx.generate;

import org.shaba.nicey_dice.FieldCard;
import org.shaba.nicey_dice.FieldCards;

import java.util.List;
import java.util.function.UnaryOperator;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

@lombok.Data
public class FieldCardPrinter implements Printer<FieldCard> {
    private static final String CARD_TOP_FOR_EMOJI = "┏━━━━━━┓%n";
    private static final String CARD_0_FOR_EMOJI   = "┃      ┃%n";
    private static final String CARD_1_FOR_EMOJI   = "┃%s     ┃%n";
    private static final String CARD_2_FOR_EMOJI   = "┃  %s   ┃%n";
    private static final String CARD_3_FOR_EMOJI   = "┃    %s ┃%n";
    private static final String CARD_13_FOR_EMOJI  = "┃%s   %s ┃%n";
    private static final String CARD_BOT_FOR_EMOJI = "┗━━━━━━┛";

    private static final String CARD_TOP = "┏━━━━━┓%n";
    private static final String CARD_0   = "┃     ┃%n";
    private static final String CARD_1   = "┃%s    ┃%n";
    private static final String CARD_2   = "┃  %s  ┃%n";
    private static final String CARD_3   = "┃    %s┃%n";
    private static final String CARD_13  = "┃%s   %s┃%n";
    private static final String CARD_BOT = "┗━━━━━┛";

    private static final String[] CARD_FORMATS_FOR_EMOJIS = {
            CARD_TOP_FOR_EMOJI +
            CARD_0_FOR_EMOJI +
            CARD_2_FOR_EMOJI +
            CARD_0_FOR_EMOJI +
            CARD_BOT_FOR_EMOJI,
            CARD_TOP_FOR_EMOJI +
            CARD_1_FOR_EMOJI +
            CARD_0_FOR_EMOJI +
            CARD_3_FOR_EMOJI +
            CARD_BOT_FOR_EMOJI,
            CARD_TOP_FOR_EMOJI +
            CARD_1_FOR_EMOJI +
            CARD_2_FOR_EMOJI +
            CARD_3_FOR_EMOJI +
            CARD_BOT_FOR_EMOJI,
            CARD_TOP_FOR_EMOJI +
            CARD_13_FOR_EMOJI +
            CARD_0_FOR_EMOJI +
            CARD_13_FOR_EMOJI +
            CARD_BOT_FOR_EMOJI,
            CARD_TOP_FOR_EMOJI +
            CARD_13_FOR_EMOJI +
            CARD_2_FOR_EMOJI +
            CARD_13_FOR_EMOJI +
            CARD_BOT_FOR_EMOJI,
            CARD_TOP_FOR_EMOJI +
            CARD_13_FOR_EMOJI +
            CARD_13_FOR_EMOJI +
            CARD_13_FOR_EMOJI +
            CARD_BOT_FOR_EMOJI
    };

    private static final String[] CARD_FORMATS = {
            CARD_TOP +
            CARD_0 +
            CARD_2 +
            CARD_0 +
            CARD_BOT,
            CARD_TOP +
            CARD_1 +
            CARD_0 +
            CARD_3 +
            CARD_BOT,
            CARD_TOP +
            CARD_1 +
            CARD_2 +
            CARD_3 +
            CARD_BOT,
            CARD_TOP +
            CARD_13 +
            CARD_0 +
            CARD_13 +
            CARD_BOT,
            CARD_TOP +
            CARD_13 +
            CARD_2 +
            CARD_13 +
            CARD_BOT,
            CARD_TOP +
            CARD_13 +
            CARD_13 +
            CARD_13 +
            CARD_BOT
    };

    private final DiceFacePrinter diceFacePrinter;
    private final PlayerPrinter   playerPrinter;

    public FieldCardPrinter(final DiceFacePrinter diceFacePrinter) {
        this.diceFacePrinter = diceFacePrinter;
        this.playerPrinter = new PlayerPrinter(false);
    }

    @Override
    public String print(final FieldCard fieldCard) {
        final int size = fieldCard.getClaimCriteria().size();
        final Object[] claimCriteria = StreamEx.of(fieldCard.getClaimCriteria())
                .sorted()
                .map(diceFacePrinter::print)
                .toArray(Object []::new);
        return Try(
            () -> format(diceFacePrinter.isUseEmojis() ? CARD_FORMATS_FOR_EMOJIS[size - 1]
                                                       : CARD_FORMATS[size - 1],
                claimCriteria))
            .map(addPlayerClaims(fieldCard))
            .getOrElse(() -> format(
                "Too many dice to show: %d",
                fieldCard.getClaimCriteria().size()));
    }

    private UnaryOperator<String> addPlayerClaims(final FieldCard fieldCard) {
        return card -> {
            final long length = max(fieldCard.getPlayerClaims().size() + 1, card.lines().count());
            final StreamEx<String> cardStream = StreamEx.of(card.lines())
                .append(generate(() -> diceFacePrinter.isUseEmojis()? "        " : "       "))
                .skip(1);

            final StreamEx<String> claimsStream = StreamEx.of(printPlayerClaims(fieldCard))
                    .map(" "::concat)
                    .append(generate(String::new));

            return cardStream
                .zipWith(claimsStream, String::concat)
                .prepend(card.lines().findFirst().get())
                .limit(length)
                .joining("\n");
        };
    }

    private List<String> printPlayerClaims(final FieldCard fieldCard) {
        return EntryStream.of(fieldCard.getPlayerClaims())
            .mapKeyValue((name, dice) -> 
                    format("%s: %s", playerPrinter.print(name), diceFacePrinter.print(dice)))
            .toList();
    }

    public String print(final FieldCards fieldCards) {
        return fieldCards.cardStream()
            .map(this::print)
            .joining("\n");
    }
}
