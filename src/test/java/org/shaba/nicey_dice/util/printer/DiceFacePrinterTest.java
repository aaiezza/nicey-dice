package org.shaba.nicey_dice.util.printer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.shaba.nicey_dice.DiceFace.dF;
import static org.shaba.nicey_dice.util.printer.DiceFacePrinter.diceFacePrinterWithEmojis;
import static org.shaba.nicey_dice.util.printer.DiceFacePrinter.diceFacePrinterWithoutEmojis;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class DiceFacePrinterTest {
    private DiceFacePrinter subject;

    @Test
    public void shouldPrintAsExpectedWithEmojis() {
        subject = diceFacePrinterWithEmojis();
        assertThat(subject.print(dF(4), dF(5), dF(3), dF(6), dF(2), dF(1)))
            .isEqualTo("4️⃣ 5️⃣ 3️⃣ 6️⃣ 2️⃣ 1️⃣");
    }

    @Test
    public void shouldPrintAsExpectedWithoutEmojis() {
        subject = diceFacePrinterWithoutEmojis();
        assertThat(subject.print(dF(4), dF(5), dF(3), dF(6), dF(2), dF(1)))
            .isEqualTo("⚃ ⚄ ⚂ ⚅ ⚁ ⚀");
    }
}
