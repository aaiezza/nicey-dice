package org.shaba.nicey_dice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.shaba.nicey_dice.player.Player;
import org.shaba.nicey_dice.player.move.PostRollMove;
import org.shaba.nicey_dice.util.MoveGeneratorUtil;

import static com.google.common.base.Predicates.alwaysFalse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.shaba.nicey_dice.DiceFace.dF;
import static org.shaba.nicey_dice.RolledDice.rolledDice;
import static org.shaba.nicey_dice.player.Player.player;
import static org.shaba.nicey_dice.player.Player.Name.name;

import one.util.streamex.StreamEx;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class MoveGeneratorUtilTest {
    private MoveGeneratorUtil subject;

    @Mock
    private NiceyDiceGame game;
    @Mock
    private PlayerMovePrompt prompt;
    @Mock
    private Board board;
    @Mock
    private FieldCards fieldCards;
    @Mock
    private FieldCard fieldCard_24, fieldCard_44, fieldCard_136;

    private Player player;

    @Before
    public void setUp() {
        player = player( name("Giacomo") );
        subject = new MoveGeneratorUtil();

        when(fieldCard_24.getClaimCriteria())
            .thenReturn(list(dF(2), dF(4)));
        when(fieldCard_44.getClaimCriteria())
            .thenReturn(list(dF(4), dF(4)));
        when(fieldCard_136.getClaimCriteria())
            .thenReturn(list(dF(1), dF(3), dF(6)));
    }

    @Test
    public void shouldReturnExpectedUnclaimedCriteria() {
        when(prompt.getBoard()).thenReturn(board);
        when(board.getFieldCards()).thenReturn(fieldCards);
        when(fieldCards.cardStream()).thenReturn(StreamEx.of(
                fieldCard_24,
                fieldCard_44));

        when(fieldCard_24.getUnclaimedCrieriaForPlayer(eq(player)))
            .thenReturn(list(dF(2)));
        when(fieldCard_24.claimForDiceFaceFulfilled(argThat(df -> !dF(4).equals(df)))).thenReturn(alwaysFalse());

        when(fieldCard_44.getUnclaimedCrieriaForPlayer(eq(player)))
            .thenReturn(list(dF(4), dF(4)));
        when(fieldCard_44.claimForDiceFaceFulfilled(any())).thenReturn(alwaysFalse());

        when(prompt.getRolledDice()).thenReturn(rolledDice(dF(4), dF(6), dF(2), dF(2), dF(1)));

        assertThat(subject.generate(prompt, player))
            .containsExactly(
                wm(dF(2), fieldCard_24),
                wm(dF(4), fieldCard_44));
    }

    @Test
    public void shouldReturnExpectedMoreUnclaimedCriteria() {
        when(prompt.getBoard()).thenReturn(board);
        when(board.getFieldCards()).thenReturn(fieldCards);
        when(fieldCards.cardStream()).thenReturn(StreamEx.of(
                fieldCard_24,
                fieldCard_44,
                fieldCard_136));

        when(fieldCard_24.getUnclaimedCrieriaForPlayer(eq(player)))
            .thenReturn(list(dF(2)));
        when(fieldCard_24.claimForDiceFaceFulfilled(argThat(df -> !dF(4).equals(df)))).thenReturn(alwaysFalse());

        when(fieldCard_44.getUnclaimedCrieriaForPlayer(eq(player)))
            .thenReturn(list(dF(4), dF(4)));
        when(fieldCard_44.claimForDiceFaceFulfilled(any())).thenReturn(alwaysFalse());

        when(fieldCard_136.getUnclaimedCrieriaForPlayer(eq(player)))
            .thenReturn(list(dF(1), dF(6)));
        when(fieldCard_136.claimForDiceFaceFulfilled(argThat(df -> !dF(3).equals(df)))).thenReturn(alwaysFalse());

        when(prompt.getRolledDice()).thenReturn(rolledDice(dF(4), dF(6), dF(5), dF(2)));

        assertThat(subject.generate(prompt, player))
            .containsExactly(
                wm(dF(2), fieldCard_24),
                wm(dF(4), fieldCard_44),
                wm(dF(6), fieldCard_136));
    }

    private PostRollMove.WorkOnAFieldCard wm(
            final DiceFace diceFace,
            final FieldCard fieldCard) {
        return new PostRollMove.WorkOnAFieldCard(player, diceFace, fieldCard);
    }
}
