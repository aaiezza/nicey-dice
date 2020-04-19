package org.shaba.nicey_dice.player;

import org.shaba.nicey_dice.NiceyDiceGame;

import java.util.function.Function;

@FunctionalInterface
public interface Move extends Function<NiceyDiceGame, Function<Player, NiceyDiceGame>> {}
