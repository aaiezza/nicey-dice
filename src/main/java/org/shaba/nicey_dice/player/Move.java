package org.shaba.nicey_dice.player;

import org.shaba.nicey_dice.DiceFace;
import org.shaba.nicey_dice.FieldCard;

@lombok.Data
// TODO: Make interface; Sealed types:
//  - EndTurn
//  - Play a die to a field card
//  - Pull back all dice
public class Move
{
    // FIXME
//    public static final Move END_TURN = null;

    private final DiceFace diceFace;
    private final FieldCard fieldCard;

    public Move(
            final DiceFace diceFace,
            final FieldCard fieldCard) {
        // TODO Validate Move
        if ( !fieldCard.getClaimCriteria().contains( diceFace ) )
            throw new IllegalArgumentException( "Field card does NOT contain that dice face." );

        this.diceFace = diceFace;
        this.fieldCard = fieldCard;
    }
}
