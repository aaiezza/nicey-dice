package org.shaba.nicey_dice.player;

import static io.vavr.API.Success;
import static java.lang.String.format;

import org.shaba.nicey_dice.PlayerMovePrompt;
import org.shaba.nicey_dice.ScoredCard;
import org.shaba.nicey_dice.player.move.PostRollMove;
import org.shaba.nicey_dice.player.move.PreRollMove;
import org.shaba.nicey_dice.util.MoveGeneratorUtil;

import java.util.List;

import io.vavr.API;
import io.vavr.control.Option;
import io.vavr.control.Try;
import one.util.streamex.StreamEx;

public class FocusedTestPlayer extends Player {
    private final MoveGeneratorUtil moveGenerator;

    public FocusedTestPlayer(final Name name) {
        super(name);
        this.moveGenerator = new MoveGeneratorUtil();
    }

    private FocusedTestPlayer(final Name name, final List<ScoredCard> scoredCards) {
        super(name, scoredCards);
        this.moveGenerator = new MoveGeneratorUtil();
    }

    @Override
    public Player withScoredCard(final ScoredCard scoredCard) {
        return new FocusedTestPlayer(
            getName(),
            StreamEx.of(getScoredCards()).append( scoredCard ).toImmutableList() );
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