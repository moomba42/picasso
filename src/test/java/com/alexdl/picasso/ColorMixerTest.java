package com.alexdl.picasso;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColorMixerTest {
    @Test
    public void yellowAndCyanMakeOffGreen() {
        int yellow = 0xFFFFFF00;
        int cyan = 0xFF00FFFF;
        int result = ColorMixer.mixColors(yellow, cyan, 275, 725);
        Assertions.assertEquals(0xFF00FF30, result);
    }

    @Test
    public void twoSameColorsMakeSameColor() {
        assertMixWithSelfEqualsSelf(0xFFFF0000); // r
        assertMixWithSelfEqualsSelf(0xFF00FF00); // g
        assertMixWithSelfEqualsSelf(0xFF0000FF); // b
        assertMixWithSelfEqualsSelf(0xFF00FFFF); // c
        assertMixWithSelfEqualsSelf(0xFFFF00FF); // m
        assertMixWithSelfEqualsSelf(0xFFFFFF00); // y
        assertMixWithSelfEqualsSelf(0xFF348A63); // custom 1
        assertMixWithSelfEqualsSelf(0xFF361759); // custom 2
        assertMixWithSelfEqualsSelf(0xFF728A3E); // custom 3
    }

    private void assertMixWithSelfEqualsSelf(int color) {
        assertEquals(ColorMixer.mixColors(color, color, 1, 1), color);
    }

    @Test
    public void colorOrderDoesntMatterIfWeightIsTheSame() {
        assertMixIsOrderIndependent(0xFF00FFFF, 0xFFFF00FF, 0xFFFFFF00);
        assertMixIsOrderIndependent(0xFFC5F101, 0xFF728A3E, 0xFFF15F00);
        assertMixIsOrderIndependent(0xFF361759, 0xFFA100FF, 0xFF0000FF);
    }

    private void assertMixIsOrderIndependent(int colorA, int colorB, int colorC) {
        int result1 = ColorMixer.mixColors(new int[]{colorA, colorB, colorC}, new int[]{1, 1, 1});
        int result2 = ColorMixer.mixColors(new int[]{colorC, colorA, colorB}, new int[]{1, 1, 1});
        int result3 = ColorMixer.mixColors(new int[]{colorB, colorC, colorA}, new int[]{1, 1, 1});
        int result4 = ColorMixer.mixColors(new int[]{colorA, colorC, colorB}, new int[]{1, 1, 1});
        int result5 = ColorMixer.mixColors(new int[]{colorB, colorA, colorC}, new int[]{1, 1, 1});
        int result6 = ColorMixer.mixColors(new int[]{colorC, colorB, colorA}, new int[]{1, 1, 1});

        assertEquals(result1, result2);
        assertEquals(result1, result3);
        assertEquals(result1, result4);
        assertEquals(result1, result5);
        assertEquals(result1, result6);
    }
}
