package org.shaba.nicey_dice.player;

import static io.vavr.API.Success;
import static java.lang.String.format;

import com.google.common.collect.Lists;

import org.shaba.nicey_dice.*;
import org.shaba.nicey_dice.player.move.PostRollMove;
import org.shaba.nicey_dice.player.move.PostRollMove.WorkOnAFieldCard;
import org.shaba.nicey_dice.player.move.PreRollMove;
import org.shaba.nicey_dice.player.move.PreRollMove.RollDice;
import org.shaba.nicey_dice.player.move.PreRollMove.TakeBackDiceInField;

import java.util.Collections;
import java.util.List;

import static org.shaba.nicey_dice.Points.sum;

import io.vavr.API;
import io.vavr.control.Try;
import one.util.streamex.StreamEx;

@lombok.Data
@lombok.ToString(callSuper = true)
public abstract class Player
{
    public static final int        NUMBER_OF_TOTAL_DICE = 6;
    private final Name             name;
    private final List<ScoredCard> scoredCards;
    private final Stats            stats;

    public Player( final Name name )
    {
        this( name, Lists.newArrayList() );
    }

    protected Player( final Name name, final List<ScoredCard> scoredCards )
    {
        this.name = name;
        this.scoredCards = Collections.unmodifiableList( scoredCards );
        this.stats = new Stats();
    }

    protected Player( final Name name, final List<ScoredCard> scoredCards, final Stats stats )
    {
        this.name = name;
        this.scoredCards = Collections.unmodifiableList( scoredCards );
        this.stats = stats;
    }

    public final int getRollableDice( final FieldCards field )
    {
        return NUMBER_OF_TOTAL_DICE - (int) field.cardStream()
                .flatCollection( fieldCard -> fieldCard.getClaimsForPlayer( this ) ).count();
    }

    public Points getPoints()
    {
        return sumPoints( this );
    }

    public abstract Player withScoredCard( final ScoredCard scoredCard );

    public abstract Player afterMove(final Move move);

    public static Points sumPoints( final Player player )
    {
        return player.scoredCards.stream().map( ScoredCard::getPoints ).collect( sum() );
    }

    // This hides the Move subclass. But is effective at obtaining statistics.
    public final Try<Move> obtainMove(final PlayerMovePrompt movePrompt) {
        return proposeMove(movePrompt).map(move -> {
//            return move;
            return move.andThen(applier -> applier.andThen(game -> new NiceyDiceGame(game.getBoard(),
                    game.getPlayers().replacePlayer(game.getCurrentPlayer().afterMove(move)),
                    game.getCurrentPlayerRolledDice())))::apply;
        });
        
    }

    /**
     * @param movePrompt
     *            the current game state that the player will have to work with.
     *            The implementation of this player will only be allowed
     *            visibility, however, to the board instance, and the rolled
     *            dice.
     * @return A {@link Move} that can be applied to the current game. Should it
     *         be valid, the game should utilize the information in the returned
     *         move and apply it to its state to produce a new legal game state,
     *         where play may then continue.
     */
    public abstract Try<Move> proposeMove( final PlayerMovePrompt movePrompt );

    public static Player player( final Name name )
    {
        @lombok.Value
        @lombok.EqualsAndHashCode(callSuper = true)
        class DefaultPlayer extends Player {
            public DefaultPlayer(final Name name) {
                super(name);
            }

            public DefaultPlayer(final Name name, final List<ScoredCard> scoredCards) {
                super(name, scoredCards);
            }

            public DefaultPlayer(final Name name, final List<ScoredCard> scoredCards, final Stats stats) {
                super(name, scoredCards, stats);
            }

            @Override
            public Try<Move> proposeMove(final PlayerMovePrompt movePrompt) {
                if(PreRollMove.class.isAssignableFrom(movePrompt.getMoveType())) {
                    return Success(new PreRollMove.RollDice());
                } else if (PostRollMove.class.isAssignableFrom(movePrompt.getMoveType())) {
                    return Success(new PostRollMove.EndTurn());
                } else {
                    return API.Failure(
                        new IllegalArgumentException(
                                format("Unknown Move Type: %s", movePrompt.getMoveType().getSimpleName())));
                }
            }

            @Override
            public Player withScoredCard(final ScoredCard scoredCard) {
                return new DefaultPlayer(
                    getName(),
                    StreamEx.of(getScoredCards()).append( scoredCard ).toImmutableList(),
                    getStats());
            }

            @Override
            public Player afterMove(final Move move) {
                return new DefaultPlayer(
                    getName(),
                    getScoredCards(),
                    getStats().afterMove(move) );
            }
        }

        return new DefaultPlayer(name);
    }

    @lombok.Value ( staticConstructor = "name" )
    public static class Name
    {
        @lombok.NonNull
        @lombok.EqualsAndHashCode.Exclude
        private final String value;

        @Override
        public boolean equals( final Object obj )
        {
            return this == obj;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( ( value == null ) ? 0 : value.hashCode() );
            return result;
        }
    }

    @lombok.Data
    public class Stats {
        private final int rolls;
        private final int takeBacks;
        private final int scores;

        @lombok.experimental.Tolerate
        public Stats() {
            this(0, 0, 0);
        }

        public Stats afterMove(final Move move) {
            if(move instanceof TakeBackDiceInField) {
                return new Stats(rolls, takeBacks + 1, scores);
            } else if (move instanceof RollDice) {
                return new Stats(rolls + 1, takeBacks, scores);
            } else if (move instanceof WorkOnAFieldCard) {
                final List<DiceFace> left = ((WorkOnAFieldCard) move).getFieldCard().getUnclaimedCrieriaForPlayer(Player.this);
                if(left.size() == 1 && left.contains(((WorkOnAFieldCard) move).getDiceFace())) {
                    return new Stats(rolls, takeBacks, scores + 1);
                } else {
                    return this;
                }
            } else {
                return this;
            }
        }

        @Override
        public String toString() {
            return format("rolls: %2d, takeBacks: %2d, scores: %2d", rolls, takeBacks, scores);
        }
    }
}
