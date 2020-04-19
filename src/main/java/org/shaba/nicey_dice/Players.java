package org.shaba.nicey_dice;

import static java.lang.String.format;

import org.shaba.nicey_dice.player.Player;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.IntFunction;

import lombok.AccessLevel;
import one.util.streamex.StreamEx;

@lombok.Data
@lombok.RequiredArgsConstructor ( access = AccessLevel.PRIVATE )
public class Players implements Iterable<Player>
{
    public static final int                                    MINIMUM_NUMBER_OF_PLAYERS           = 2;
    public static final int                                    MAXIMUM_NUMBER_OF_PLAYERS           = 4;
    private static final IntFunction<IllegalArgumentException> ILLEGAL_NUMBER_OF_PLAYERS_EXCEPTION = value -> new IllegalArgumentException(
            format( "`%d` is invalid. Number of playerQueue must be between %d-%d.", value,
                MINIMUM_NUMBER_OF_PLAYERS, MAXIMUM_NUMBER_OF_PLAYERS ) );

    @lombok.Getter ( AccessLevel.NONE )
    private final Queue<Player>                                playerQueue;

    private Players( final Player... players )
    {
        if ( players.length < MINIMUM_NUMBER_OF_PLAYERS ||
                players.length > MAXIMUM_NUMBER_OF_PLAYERS )
            throw ILLEGAL_NUMBER_OF_PLAYERS_EXCEPTION.apply( players.length );

        if ( StreamEx.of( players ).distinct( Player::getName ).count() != players.length )
            throw new IllegalArgumentException( "Names of given players must be distinct." );

        this.playerQueue = new LinkedList<>( Arrays.asList( players ) );
    }

    public int getNumberOfPlayers()
    {
        return playerQueue.size();
    }

    Player currentPlayer()
    {
        return playerQueue.peek();
    }

    public Players nextPlayer()
    {
        final Queue<Player> players = new LinkedList<>( this.playerQueue );
        players.offer( players.poll() );
        return new Players( players );
    }

    public static Players players( final Player.Name... playerNames )
    {
        return new Players(
            StreamEx.of( Arrays.stream( playerNames ) )
                .nonNull()
                .map( Player::player )
                .toArray( Player []::new ) );
    }

    @Override
    public Iterator<Player> iterator()
    {
        return StreamEx.of( playerQueue ).iterator();
    }
}
