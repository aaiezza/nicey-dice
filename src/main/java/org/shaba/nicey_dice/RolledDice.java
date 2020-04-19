package org.shaba.nicey_dice;

import static java.lang.String.format;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;

import org.apache.commons.collections4.CollectionUtils;

import lombok.AccessLevel;

import java.util.*;

import static com.google.common.base.Predicates.not;

import io.vavr.control.Option;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

@lombok.Data
public class RolledDice implements Iterable<DiceFace>
{
    public static final RolledDice UNROLLED = new RolledDice( Collections.emptyList() );

    @lombok.Getter ( AccessLevel.NONE )
    private final List<DiceFace>   dice;

    private RolledDice( @lombok.NonNull final List<DiceFace> dice )
    {
        this.dice = Collections.unmodifiableList( dice );
    }

    public static RolledDice rolledDice( final DiceFace... diceFaces )
    {
        return Option.of( diceFaces )
                .map( Arrays::asList )
                .filter(CollectionUtils::isNotEmpty)
                .fold(() -> UNROLLED, RolledDice::new);
    }

    public Map<DiceFace, Long> getDiceFaceCountMap()
    {
        return StreamEx.of( dice ).groupingBy( identity(), counting() );
    }

    public DetachedDie select(@lombok.NonNull final DiceFace diceFace) {
        if(!dice.contains(diceFace))
            throw new IllegalStateException(format("Did not roll a %d", diceFace.getValue()));

        final Integer selectedDie = dice.indexOf(diceFace);
        return new DetachedDie(
            EntryStream.of(dice)
                .filterKeys(not(selectedDie::equals))
                .values()
                .toListAndThen(RolledDice::new),
            diceFace);
    }

    public boolean isUnrolled() {
        return this == UNROLLED;
    }

    @Override
    public Iterator<DiceFace> iterator()
    {
        return dice.iterator();
    }

    @Override
    public String toString()
    {
        return StreamEx.of( dice )
            .map( DiceFace::getValue )
            .sorted()
            .joining(", ", "RolledDice [", "]");
    }

    @lombok.Data
    public static class DetachedDie {
        private final RolledDice rolledDice;
        private final DiceFace chosenDie;
    }
}
