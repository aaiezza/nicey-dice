package org.shaba.nicey_dice;

import static java.lang.String.format;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.shaba.nicey_dice.player.Player;

import java.util.*;
import java.util.function.BiPredicate;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

@lombok.ToString ( callSuper = true )
@lombok.EqualsAndHashCode ( callSuper = true )
public class FieldCard extends Card
{
    private final Map<Player.Name, List<DiceFace>> playerClaims;

    private FieldCard( final Card card, final Map<Player.Name, List<DiceFace>> playerClaims )
    {
        super( card );
        this.playerClaims = Collections.unmodifiableMap( playerClaims );
    }

    public FieldCard addPlayerClaim( final Player player, final DiceFace... diceFaces )
    {
        return new FieldCard( this, combinePlayerClaims( player, diceFaces ) );
    }

    public List<DiceFace> getClaimsForPlayer( final Player player )
    {
        return Collections
                .unmodifiableList( playerClaims.getOrDefault( player, Lists.newArrayList() ) );
    }

    private Map<Player.Name, List<DiceFace>> combinePlayerClaims(
            final Player player,
            final DiceFace... diceFaces )
    {
        final Map<Player.Name, List<DiceFace>> claims = EntryStream.of( playerClaims ).toMap();

        final List<DiceFace> existingClaims = StreamEx
                .of( playerClaims.getOrDefault( player.getName(), Lists.newArrayList() ) ).toList();
        StreamEx.of( diceFaces ).forEach( existingClaims::add );
        final Optional<IllegalArgumentException> violation = EntryStream
                .of( getDiceFaceCountMap( existingClaims ) ).filterKeyValue( exceedsCriteria() )
                .toMapAndThen( this::processViolations );

        if ( violation.isPresent() )
            throw violation.get();

        claims.put( player.getName(), existingClaims );
        return Collections.unmodifiableMap( claims );
    }

    private BiPredicate<DiceFace, Long> exceedsCriteria()
    {
        return ( diceFace, quantity ) -> getClaimCriteriaMap().get( diceFace ) < quantity;
    }

    private Optional<IllegalArgumentException> processViolations(
            final Map<DiceFace, Long> violators )
    {
        if ( violators.isEmpty() )
            return Optional.empty();
        return Optional.of( new IllegalArgumentException( EntryStream.of( violators ).mapKeyValue(
            ( diceFace, quantity ) -> format( "[%d of face value %d]", quantity, diceFace.getValue() ) )
                .joining( ", ",
                    "These dice faces were found in excess to the actual criteria for scoring this card: ",
                    "" ) ) );
    }

    public static FieldCard fieldCard( final Card card )
    {
        return new FieldCard( card, Maps.newHashMap() );
    }
}
