package org.shaba.nicey_dice.factories;

import static java.lang.String.format;
import static java.util.Arrays.copyOfRange;

import org.shaba.nicey_dice.NiceyDiceGame;
import org.shaba.nicey_dice.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.shaba.nicey_dice.Board.board;
import static org.shaba.nicey_dice.FieldCards.fieldCards;
import static org.shaba.nicey_dice.NiceyDiceGame.niceyDiceGame;
import static org.shaba.nicey_dice.Players.players;

@lombok.Value
public class NiceyDiceGameFactory
{
    private static final Logger logger = LoggerFactory.getLogger(NiceyDiceGameFactory.class);

    static final Player.Name [] DEFAULT_PLAYER_NAMES = {
            Player.Name.name( "Player 1" ),
            Player.Name.name( "Player 2" ),
            Player.Name.name( "Player 3" ),
            Player.Name.name( "Player 4" ) };

    private final CardStockFactory        cardStockFactory     = new CardStockFactory();

    /**
     * @param numberOfPlayers
     *            Sets the game up with this many players and chooses names from
     *            the default list in this factory.
     * @return A fresh game ready for play, with {@code numberOfPlayers}
     *         player(s) and the game's board fills the field with scorable
     *         cards.
     */
    public NiceyDiceGame createForNumberOfPlayers( final int numberOfPlayers )
    {
        logger.debug(format("Creating a game for %d players", numberOfPlayers));
        return niceyDiceGame(
            board( cardStockFactory.originalCardStock().shuffle(), fieldCards() ).fillField(),
            players( copyOfRange( DEFAULT_PLAYER_NAMES, 0, numberOfPlayers ) ) );
    }
}
