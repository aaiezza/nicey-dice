package org.shaba.nicey_dice.util;

import org.shaba.nicey_dice.PlayerMovePrompt;
import org.shaba.nicey_dice.player.Player;
import org.shaba.nicey_dice.player.move.PostRollMove;

import java.util.List;

import one.util.streamex.StreamEx;

public class MoveGeneratorUtil {
    public List<PostRollMove.WorkOnAFieldCard> generate(
            final PlayerMovePrompt prompt,
            final Player player) {
        return prompt
            .getBoard()
            .getFieldCards()
            .cardStream()
            .flatMap(fieldCard -> StreamEx.of(fieldCard.getUnclaimedCrieriaForPlayer(player))
                    .filter(prompt.getRolledDice().getDiceFaceCountMap().keySet()::contains)
                    .map(eligibleDiceFace ->
                        new PostRollMove.WorkOnAFieldCard(player, eligibleDiceFace, fieldCard)))
            .distinct()
            .toList();
    }
}
