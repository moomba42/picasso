package com.alexdl.picasso;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ColorMixerTest {
    @Test
    public void yellowAndCyanMakeOffGreen() {
        int yellow = 0xFFFFFF00;
        int cyan = 0xFF00FFFF;
        int result = ColorMixer.mixColor(yellow, cyan, 0.725);
        Assertions.assertEquals(0xFF00FF30, result);
    }

    @Test
    public void twoSameColorsMakeSameColor() {
        assertTrue(mixWithSelfEqualsSelf(0xFFFF0000)); // r
        assertTrue(mixWithSelfEqualsSelf(0xFF00FF00)); // g
        assertTrue(mixWithSelfEqualsSelf(0xFF0000FF)); // b
        assertTrue(mixWithSelfEqualsSelf(0xFF00FFFF)); // c
        assertTrue(mixWithSelfEqualsSelf(0xFFFF00FF)); // m
        assertTrue(mixWithSelfEqualsSelf(0xFFFFFF00)); // y
        assertTrue(mixWithSelfEqualsSelf(0xFF348A63)); // custom 1
        assertTrue(mixWithSelfEqualsSelf(0xFF361759)); // custom 2
        assertTrue(mixWithSelfEqualsSelf(0xFF728A3E)); // custom 3
    }

    private boolean mixWithSelfEqualsSelf(int color) {
        return ColorMixer.mixColor(color, color, 0.5) == color;
    }
}
