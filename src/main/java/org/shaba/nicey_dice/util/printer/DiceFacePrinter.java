package org.shaba.nicey_dice.util.printer;

import org.shaba.nicey_dice.DiceFace;

import lombok.AccessLevel;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;

import one.util.streamex.StreamEx;

@lombok.Data
@lombok.RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DiceFacePrinter implements Printer<DiceFace> {
    private final boolean useEmojis;

    @Override
    public String print(final DiceFace diceFace) {
        switch(diceFace.getValue()) {
        case 1: return useEmojis? "1️⃣" : "⚀";
        case 2: return useEmojis? "2️⃣" : "⚁";
        case 3: return useEmojis? "3️⃣" : "⚂";
        case 4: return useEmojis? "4️⃣" : "⚃";
        case 5: return useEmojis? "5️⃣" : "⚄";
        case 6: return useEmojis? "6️⃣" : "⚅";
        default: return diceFace.getValue() + "";
        }
    }

    public String print(final DiceFace... diceFaces) {
        return print(newArrayList(diceFaces));
    }

    public String print(final Collection<DiceFace> diceFaces) {
        return StreamEx.of(diceFaces)
            .map(this::print)
            .joining(" ");
    }

    public static DiceFacePrinter diceFacePrinterWithEmojis() {
        return new DiceFacePrinter(true);
    }

    public static DiceFacePrinter diceFacePrinterWithoutEmojis() {
        return new DiceFacePrinter(false);
    }
}
