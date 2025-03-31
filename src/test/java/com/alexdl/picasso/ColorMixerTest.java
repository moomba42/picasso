package com.alexdl.picasso;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ColorMixerTest {
    @Test
    public void yellowAndCyanMakeOffGreen() {
        int yellow = 0xFFFFFF00;
        int cyan = 0xFF00FFFF;
        int result = ColorMixer.mixColor(yellow, cyan, 0.5);
        Assertions.assertEquals(0xFF16FF0D, result);
    }
}
