package com.alexdl.picasso;

public class ColorMixer {
    private static final int REFLECTANCE_SAMPLES = 38;
    private static final double EPSILON = 0.00000001;

    public static int mixColors(int colorA, int colorB, int weightA, int weightB) {
        return mixColors(new int[]{colorA, colorB}, new int[]{weightA, weightB});
    }

    public static int mixColors(int[] colors, int[] weights) {
        assert colors.length == weights.length;
        int count = colors.length;

        double[][] reflectances = new double[count][REFLECTANCE_SAMPLES];
        double[] dotProducts = new double[count];
        for (int i = 0; i < count; i++) {
            reflectances[i] = linearToReflectance(unpack(colors[i]));
            dotProducts[i] = dotProduct(reflectances[i], CIE_CMF_Y);
        }

        int biggestWeight = 0;
        for (int i = 0; i < count; i++) {
            if (weights[i] > biggestWeight) {
                biggestWeight = weights[i];
            }
        }
        double[] proportions = new double[count];
        for (int i = 0; i < count; i++) {
            proportions[i] = Math.clamp(weights[i] / (double) biggestWeight, 0, 1);
        }

        double[] concentrations = linearToConcentrations(dotProducts, proportions);

        double[] resultReflectance = new double[REFLECTANCE_SAMPLES];
        for (int i = 0; i < REFLECTANCE_SAMPLES; i++) {
            double KS = 0.0;
            for (int j = 0; j < count; j++) {
                double[] reflectance = reflectances[j];
                double concentration = concentrations[j];
                KS += concentration * (Math.pow(1 - reflectance[i], 2) / (2 * reflectance[i]));
            }
            double KM = 1 + KS - Math.sqrt(Math.pow(KS, 2) + 2 * KS);
            resultReflectance[i] = KM;
        }

        RGB resultLinear = reflectanceToLinear(resultReflectance);

        return pack(resultLinear);
    }

    private static RGB unpack(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return new RGB(r / 255.0, g / 255.0, b / 255.0);
    }

    private static int pack(RGB linear) {
        int r = Math.clamp(Math.round(linear.r * 255), 0, 255);
        int g = Math.clamp(Math.round(linear.g * 255), 0, 255);
        int b = Math.clamp(Math.round(linear.b * 255), 0, 255);
        return (0xFF << 24) + (r << 16) + (g << 8) + b;
    }

    private static double[] linearToReflectance(RGB linear) {
        WCMYRGB weights = spectralUpsampling(linear);

        double[] result = new double[REFLECTANCE_SAMPLES];
        for (int i = 0; i < REFLECTANCE_SAMPLES; i++) {
            result[i] = Math.max(
                    EPSILON,
                    weights.w +
                    weights.c * SPD_C[i] +
                    weights.m * SPD_M[i] +
                    weights.y * SPD_Y[i] +
                    weights.r * SPD_R[i] +
                    weights.g * SPD_G[i] +
                    weights.b * SPD_B[i]
            );
        }

        return result;
    }

    private static WCMYRGB spectralUpsampling(RGB linear) {
        double whiteAmount = Math.min(Math.min(linear.r, linear.g), linear.b);
        // At least one of the components should be 0 to make the relations clearer
        RGB linearFloored = new RGB(
                linear.r - whiteAmount,
                linear.g - whiteAmount,
                linear.b - whiteAmount);

        return new WCMYRGB(
                whiteAmount,
                Math.min(linearFloored.g, linearFloored.b),
                Math.min(linearFloored.r, linearFloored.b),
                Math.min(linearFloored.r, linearFloored.g),
                Math.max(0, Math.min(linearFloored.r - linearFloored.b, linearFloored.r - linearFloored.g)),
                Math.max(0, Math.min(linearFloored.g - linearFloored.b, linearFloored.g - linearFloored.r)),
                Math.max(0, Math.min(linearFloored.b - linearFloored.g, linearFloored.b - linearFloored.r))
        );
    }

    private static double dotProduct(double[] a, double[] b) {
        double[] product = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            product[i] = a[i] * b[i];
        }
        double dot = 0.0;
        for (double v : product) {
            dot += v;
        }

        return dot;
    }

    private static double[] linearToConcentrations(double[] linearDotProducts, double[] proportions) {
        assert linearDotProducts.length == proportions.length;

        double[] amounts = new double[linearDotProducts.length];
        double total = 0.0;

        for (int i = 0; i < linearDotProducts.length; i++) {
            amounts[i] = Math.pow(linearDotProducts[i] * proportions[i], 2);
            total += amounts[i];
        }

        double[] result = new double[amounts.length];
        for (int i = 0; i < amounts.length; i++) {
            result[i] = amounts[i] / total;
        }

        return result;
    }

    private static RGB reflectanceToLinear(double[] reflectance) {
        double x = dotProduct(reflectance, CIE_CMF_X);
        double y = dotProduct(reflectance, CIE_CMF_Y);
        double z = dotProduct(reflectance, CIE_CMF_Z);
        double[] xyz = {x, y, z};

        double r = dotProduct(XYZ_RGB[0], xyz);
        double g = dotProduct(XYZ_RGB[1], xyz);
        double b = dotProduct(XYZ_RGB[2], xyz);

        return new RGB(r, g, b);
    }


    private record RGB(double r, double g, double b) {
    }

    private record WCMYRGB(double w, double c, double m, double y, double r, double g, double b) {
    }

    //  Below values are taken from https://github.com/rvanwijnen/spectral.js
    //  Here is the license for it:
    //
    //  MIT License
    //
    //  Copyright (c) 2023 Ronald van Wijnen
    //
    //  Permission is hereby granted, free of charge, to any person obtaining a
    //  copy of this software and associated documentation files (the "Software"),
    //  to deal in the Software without restriction, including without limitation
    //  the rights to use, copy, modify, merge, publish, distribute, sublicense,
    //  and/or sell copies of the Software, and to permit persons to whom the
    //  Software is furnished to do so, subject to the following conditions:
    //
    //  The above copyright notice and this permission notice shall be included in
    //  all copies or substantial portions of the Software.
    //
    //  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    //  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    //  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    //  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    //  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
    //  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
    //  DEALINGS IN THE SOFTWARE.

    private static final double[] SPD_C = {
            0.96853629, 0.96855103, 0.96859338, 0.96877345, 0.96942204, 0.97143709, 0.97541862, 0.98074186, 0.98580992, 0.98971194, 0.99238027, 0.99409844, 0.995172, 0.99576545,
            0.99593552, 0.99564041, 0.99464769, 0.99229579, 0.98638762, 0.96829712, 0.89228016, 0.53740239, 0.15360445, 0.05705719, 0.03126539, 0.02205445, 0.01802271, 0.0161346,
            0.01520947, 0.01475977, 0.01454263, 0.01444459, 0.01439897, 0.0143762, 0.01436343, 0.01435687, 0.0143537, 0.01435408,
    };

    private static final double[] SPD_M = {
            0.51567122, 0.5401552, 0.62645502, 0.75595012, 0.92826996, 0.97223624, 0.98616174, 0.98955255, 0.98676237, 0.97312575, 0.91944277, 0.32564851, 0.13820628, 0.05015143,
            0.02912336, 0.02421691, 0.02660696, 0.03407586, 0.04835936, 0.0001172, 0.00008554, 0.85267882, 0.93188793, 0.94810268, 0.94200977, 0.91478045, 0.87065445, 0.78827548,
            0.65738359, 0.59909403, 0.56817268, 0.54031997, 0.52110241, 0.51041094, 0.50526577, 0.5025508, 0.50126452, 0.50083021,
    };

    private static final double[] SPD_Y = {
            0.02055257, 0.02059936, 0.02062723, 0.02073387, 0.02114202, 0.02233154, 0.02556857, 0.03330189, 0.05185294, 0.10087639, 0.24000413, 0.53589066, 0.79874659, 0.91186529,
            0.95399623, 0.97137099, 0.97939505, 0.98345207, 0.98553736, 0.98648905, 0.98674535, 0.98657555, 0.98611877, 0.98559942, 0.98507063, 0.98460039, 0.98425301, 0.98403909,
            0.98388535, 0.98376116, 0.98368246, 0.98365023, 0.98361309, 0.98357259, 0.98353856, 0.98351247, 0.98350101, 0.98350852,
    };

    private static final double[] SPD_R = {
            0.03147571, 0.03146636, 0.03140624, 0.03119611, 0.03053888, 0.02856855, 0.02459485, 0.0192952, 0.01423112, 0.01033111, 0.00765876, 0.00593693, 0.00485616, 0.00426186,
            0.00409039, 0.00438375, 0.00537525, 0.00772962, 0.0136612, 0.03181352, 0.10791525, 0.46249516, 0.84604333, 0.94275572, 0.96860996, 0.97783966, 0.98187757, 0.98377315,
            0.98470202, 0.98515481, 0.98537114, 0.98546685, 0.98550011, 0.98551031, 0.98550741, 0.98551323, 0.98551563, 0.98551547,
    };

    private static final double[] SPD_G = {
            0.49108579, 0.46944057, 0.4016578, 0.2449042, 0.0682688, 0.02732883, 0.013606, 0.01000187, 0.01284127, 0.02636635, 0.07058713, 0.70421692, 0.85473994, 0.95081565, 0.9717037,
            0.97651888, 0.97429245, 0.97012917, 0.9425863, 0.99989207, 0.99989891, 0.13823139, 0.06968113, 0.05628787, 0.06111561, 0.08987709, 0.13656016, 0.22169624, 0.32176956,
            0.36157329, 0.4836192, 0.46488579, 0.47440306, 0.4857699, 0.49267971, 0.49625685, 0.49807754, 0.49889859,
    };

    private static final double[] SPD_B = {
            0.97901834, 0.97901649, 0.97901118, 0.97892146, 0.97858555, 0.97743705, 0.97428075, 0.96663223, 0.94822893, 0.89937713, 0.76070164, 0.4642044, 0.20123039, 0.08808402,
            0.04592894, 0.02860373, 0.02060067, 0.01656701, 0.01451549, 0.01357964, 0.01331243, 0.01347661, 0.01387181, 0.01435472, 0.01479836, 0.0151525, 0.01540513, 0.01557233,
            0.0156571, 0.01571025, 0.01571916, 0.01572133, 0.01572502, 0.01571717, 0.01571905, 0.01571059, 0.01569728, 0.0157002,
    };

    private static final double[] CIE_CMF_X = {
            0.00006469, 0.00021941, 0.00112057, 0.00376661, 0.01188055, 0.02328644, 0.03455942, 0.03722379, 0.03241838, 0.02123321, 0.01049099, 0.00329584, 0.00050704, 0.00094867,
            0.00627372, 0.01686462, 0.02868965, 0.04267481, 0.05625475, 0.0694704, 0.08305315, 0.0861261, 0.09046614, 0.08500387, 0.07090667, 0.05062889, 0.03547396, 0.02146821,
            0.01251646, 0.00680458, 0.00346457, 0.00149761, 0.0007697, 0.00040737, 0.00016901, 0.00009522, 0.00004903, 0.00002,
    };

    private static final double[] CIE_CMF_Y = {
            0.00000184, 0.00000621, 0.00003101, 0.00010475, 0.00035364, 0.00095147, 0.00228226, 0.00420733, 0.0066888, 0.0098884, 0.01524945, 0.02141831, 0.03342293, 0.05131001,
            0.07040208, 0.08783871, 0.09424905, 0.09795667, 0.09415219, 0.08678102, 0.07885653, 0.0635267, 0.05374142, 0.04264606, 0.03161735, 0.02088521, 0.01386011, 0.00810264,
            0.0046301, 0.00249138, 0.0012593, 0.00054165, 0.00027795, 0.00014711, 0.00006103, 0.00003439, 0.00001771, 0.00000722,
    };

    private static final double[] CIE_CMF_Z = {
            0.00030502, 0.00103681, 0.00531314, 0.01795439, 0.05707758, 0.11365162, 0.17335873, 0.19620658, 0.18608237, 0.13995048, 0.08917453, 0.04789621, 0.02814563, 0.01613766,
            0.0077591, 0.00429615, 0.00200551, 0.00086147, 0.00036904, 0.00019143, 0.00014956, 0.00009231, 0.00006813, 0.00002883, 0.00001577, 0.00000394, 0.00000158, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
    };

    private static final double[][] XYZ_RGB = {
            {3.24306333, -1.53837619, -0.49893282},
            {-0.96896309, 1.87542451, 0.04154303},
            {0.05568392, -0.20417438, 1.05799454}
    };
}
