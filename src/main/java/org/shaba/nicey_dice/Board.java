package org.shaba.nicey_dice;

import org.shaba.nicey_dice.player.Player;

import java.util.concurrent.atomic.AtomicReference;

import static org.shaba.nicey_dice.FieldCards.fieldCards;

import one.util.streamex.StreamEx;

@lombok.Data
public class Board
{
    private final CardStock  cardStock;
    private final FieldCards fieldCards;

    public Board fillField()
    {
        final AtomicReference<Board> board = new AtomicReference<>( this );
        while ( board.get().drawFromStockCanOccur() )
            board.updateAndGet( Board::drawFromStock );
        return board.get();
    }

    public Board placeDiceForPlayer(
            final FieldCard fieldCard,
            final Player player,
            final DiceFace... diceFaces )
    {
        return fieldCards.contains( fieldCard ) ? new Board( cardStock,
                fieldCards.placeDiceForPlayer( fieldCard, player, diceFaces ) ) : this;
    }

    public Board scoreCard( final FieldCard fieldCard )
    {
        return fieldCards.contains( fieldCard ) ? new Board( cardStock,
                fieldCards.scoreCard( fieldCard ) ) : this;
    }

    public Board removeDiceForPlayer(final Player player) {
        return board(getCardStock(),
            StreamEx.of(fieldCards.iterator())
                .map(fieldCard -> fieldCard.removePlayerClaims(player))
                .toListAndThen(FieldCards::new));
    }

    public Board drawFromStock()
    {
        return cardStock.draw().map2( fieldCards::addCardFromStock ).apply( Board::new );
    }

    public boolean drawFromStockCanOccur()
    {
        return !fieldCards.fieldIsFull() && !cardStock.isEmpty();
    }

    public static Board board( final CardStock cardStock, final FieldCards fieldCards )
    {
        return new Board( cardStock, fieldCards );
    }

    public static Board board( final CardStock cardStock )
    {
        return board( cardStock, fieldCards() );
    }
}
