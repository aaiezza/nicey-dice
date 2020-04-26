package org.shaba.nicey_dice.player;

import static io.vavr.API.Success;
import static java.lang.String.format;

import org.shaba.nicey_dice.*;
import org.shaba.nicey_dice.player.move.PostRollMove;
import org.shaba.nicey_dice.player.move.PostRollMove.WorkOnAFieldCard;
import org.shaba.nicey_dice.player.move.PreRollMove;
import org.shaba.nicey_dice.util.MoveGeneratorUtil;

import java.util.List;

import io.vavr.API;
import io.vavr.control.Option;
import io.vavr.control.Try;
import one.util.streamex.StreamEx;

public class SimpleTestPlayer extends Player {
    private final MoveGeneratorUtil moveGenerator;

    public SimpleTestPlayer(final Name name) {
        super(name);
        this.moveGenerator = new MoveGeneratorUtil();
    }

    private SimpleTestPlayer(final Name name, final List<ScoredCard> scoredCards) {
        super(name, scoredCards);
        this.moveGenerator = new MoveGeneratorUtil();
    }

    private SimpleTestPlayer(final Name name, final List<ScoredCard> scoredCards, final Stats stats) {
        super(name, scoredCards, stats);
        this.moveGenerator = new MoveGeneratorUtil();
    }

    @Override
    public Player withScoredCard(final ScoredCard scoredCard) {
        return new SimpleTestPlayer(
            getName(),
            StreamEx.of(getScoredCards()).append( scoredCard ).toImmutableList(),
            getStats() );
    }

    @Override
    public Player afterMove(final Move move) {
        return new SimpleTestPlayer(
            getName(),
            getScoredCards(),
            getStats().afterMove(move) );
    }

    @Override
    public Try<Move> proposeMove(final PlayerMovePrompt movePrompt) {
        if(PreRollMove.class.isAssignableFrom(movePrompt.getMoveType())) {
            if(getRollableDice(movePrompt.getBoard().getFieldCards()) > 0)
                return Success(new PreRollMove.RollDice());
            else {
                return Success(new PreRollMove.TakeBackDiceInField());
            }
        } else if (PostRollMove.class.isAssignableFrom(movePrompt.getMoveType())) {
            final List<WorkOnAFieldCard> moves = moveGenerator.generate(movePrompt, this);
            final Option<FieldCard> investedCard = Option.ofOptional(movePrompt.getBoard().getFieldCards().cardStream()
                    .findFirst(card -> !card.getClaimsForPlayer(this).isEmpty()));
            if(investedCard.isDefined()) {
                return investedCard
                        .flatMap(card -> Option.ofOptional(StreamEx.of(moves)
                            .findFirst(move -> move.getFieldCard() == card)))
                        .fold(() -> Success(new PostRollMove.EndTurn()), Try::success);
            } else {
                return Option.ofOptional(moves
                    .stream()
                    .findFirst())
                    .fold(() -> Success(new PostRollMove.EndTurn()), Try::success);
            }
        } else {
            return API.Failure(
                new IllegalArgumentException(
                        format("Unknown Move Type: %s", movePrompt.getMoveType().getSimpleName())));
        }
    }
}