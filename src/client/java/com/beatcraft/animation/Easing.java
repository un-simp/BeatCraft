package com.beatcraft.animation;

import java.util.function.Function;

public class Easing {
    // https://easings.net/

    public static Function<float, float> getEasing(String easing) {
        return switch (easing) {
            case "easeStep" -> Easing::easeStep;
            case "easeOutQuad" -> Easing::easeOutQuad;
            case "easeInQuad" -> Easing::easeInQuad;
            case "easeInOutQuad" -> Easing::easeInOutQuad;
            case "easeInCubic" -> Easing::easeInCubic;
            case "easeOutCubic" -> Easing::easeOutCubic;
            case "easeInOutCubic" -> Easing::easeInOutCubic;
            case "easeInQuart" -> Easing::easeInQuart;
            case "easeOutQuart" -> Easing::easeOutQuart;
            case "easeInOutQuart" -> Easing::easeInOutQuart;
            case "easeInQuint" -> Easing::easeInQuint;
            case "easeOutQuint" -> Easing::easeOutQuint;
            case "easeInOutQuint" -> Easing::easeInOutQuint;
            case "easeInSine" -> Easing::easeInSine;
            case "easeOutSine" -> Easing::easeOutSine;
            case "easeInOutSine" -> Easing::easeInOutSine;
            case "easeInCirc" -> Easing::easeInCirc;
            case "easeOutCirc" -> Easing::easeOutCirc;
            case "easeInOutCirc" -> Easing::easeInOutCirc;
            case "easeInExpo" -> Easing::easeInExpo;
            case "easeOutExpo" -> Easing::easeOutExpo;
            case "easeInOutExpo" -> Easing::easeInOutExpo;
            case "easeInElastic" -> Easing::easeInElastic;
            case "easeOutElastic" -> Easing::easeOutElastic;
            case "easeInOutElastic" -> Easing::easeInOutElastic;
            case "easeInBack" -> Easing::easeInBack;
            case "easeOutBack" -> Easing::easeOutBack;
            case "easeInOutBack" -> Easing::easeInOutBack;
            case "easeInBounce" -> Easing::easeInBounce;
            case "easeOutBounce" -> Easing::easeOutBounce;
            case "easeInOutBounce" -> Easing::easeInOutBounce;
            default -> Easing::easeLinear;
        };
    }

    public static float easeStep(float x) {
        return x >= 1 ? 1 : 0;
    }

    public static float easeLinear(float x) {
        return x;
    }

    public static float easeInSine(float x) {
        return 1 - (float) Math.cos((x * (float) Math.PI) / 2);
    }

    public static float easeOutSine(float x) {
        return (float) Math.sin((x * (float) Math.PI) / 2);
    }

    public static float easeInOutSine(float x) {
        return -((float) Math.cos((float) Math.PI * x) - 1) / 2;
    }

    public static float easeInQuad(float x) {
        return x * x;
    }

    public static float easeOutQuad(float x) {
        return 1 - (1 - x) * (1 - x);
    }

    public static float easeInOutQuad(float x) {
        return x < 0.5 ? 2 * x * x : 1 - (float) Math.pow(-2 * x + 2, 2) / 2;
    }

    public static float easeInCubic(float x) {
        return x * x * x;
    }

    public static float easeOutCubic(float x) {
        return 1 - (float) Math.pow(1 - x, 3);
    }

    public static float easeInOutCubic(float x) {
        return x < 0.5 ? 4 * x * x * x : 1 - (float) Math.pow(-2 * x + 2, 3) / 2;
    }

    public static float easeInQuart(float x) {
        return x * x * x * x;
    }

    public static float easeOutQuart(float x) {
        return 1 - (float) Math.pow(1 - x, 4);
    }

    public static float easeInOutQuart(float x) {
        return x < 0.5 ? 8 * x * x * x * x : 1 - (float) Math.pow(-2 * x + 2, 4) / 2;
    }

    public static float easeInQuint(float x) {
        return x * x * x * x * x;
    }

    public static float easeOutQuint(float x) {
        return 1 - (float) Math.pow(1 - x, 5);
    }

    public static float easeInOutQuint(float x) {
        return x < 0.5 ? 16 * x * x * x * x * x : 1 - (float) Math.pow(-2 * x + 2, 5) / 2;
    }

    public static float easeInExpo(float x) {
        return x == 0 ? 0 : (float) Math.pow(2, 10 * x - 10);
    }

    public static float easeOutExpo(float x) {
        return x == 1 ? 1 : 1 - (float) Math.pow(2, -10 * x);
    }

    public static float easeInOutExpo(float x) {
        return x == 0
                ? 0
                : x == 1
                ? 1
                : x < 0.5
                ? (float) Math.pow(2, 20 * x - 10) / 2
                : (2 - (float) Math.pow(2, -20 * x + 10)) / 2;
    }

    public static float easeInCirc(float x) {
        return 1 - (float) Math.sqrt(1 - (float) Math.pow(x, 2));
    }

    public static float easeOutCirc(float x) {
        return (float) Math.sqrt(1 - (float) Math.pow(x - 1, 2));
    }

    public static float easeInOutCirc(float x) {
        return x < 0.5
                ? (1 - (float) Math.sqrt(1 - (float) Math.pow(2 * x, 2))) / 2
                : ((float) Math.sqrt(1 - (float) Math.pow(-2 * x + 2, 2)) + 1) / 2;
    }

    public static float easeInBack(float x) {
        float c1 = 1.70158f;
        float c3 = c1 + 1;

        return c3 * x * x * x - c1 * x * x;
    }

    public static float easeOutBack(float x) {
        float c1 = 1.70158f;
        float c3 = c1 + 1f;

        return 1 + c3 * (float) Math.pow(x - 1, 3) + c1 * (float) Math.pow(x - 1, 2);
    }

    public static float easeInOutBack(float x) {
        float c1 = 1.70158f;
        float c2 = c1 * 1.525f;

        return x < 0.5
                ? ((float) Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
                : ((float) Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
    }

    public static float easeInElastic(float x) {
        float c4 = (2 * (float) Math.PI) / 3;

        return x == 0
                ? 0
                : x == 1
                ? 1
                : -(float) Math.pow(2, 10 * x - 10) * (float) Math.sin((x * 10 - 10.75) * c4);
    }

    public static float easeOutElastic(float x) {
        float c4 = (2 * (float) Math.PI) / 3;

        return x == 0
                ? 0
                : x == 1
                ? 1
                : (float) Math.pow(2, -10 * x) * (float) Math.sin((x * 10 - 0.75) * c4) + 1;
    }

    public static float easeInOutElastic(float x) {
        float c5 = (2 * (float) Math.PI) / 4.5f;

        float s = (float) Math.sin((20 * x - 11.125) * c5);
        return x == 0
                ? 0
                : x == 1
                ? 1
                : x < 0.5
                ? -((float) Math.pow(2, 20 * x - 10) * s) / 2
                : ((float) Math.pow(2, -20 * x + 10) * s) / 2 + 1;
    }

    public static float easeInBounce(float x) {
        return 1 - easeOutBounce(1 - x);
    }

    public static float easeOutBounce(float x) {
        float n1 = 7.5625f;
        float d1 = 2.75f;

        if (x < 1 / d1) {
            return n1 * x * x;
        } else if (x < 2 / d1) {
            return n1 * (x -= 1.5 / d1) * x + 0.75f;
        } else if (x < 2.5 / d1) {
            return n1 * (x -= 2.25 / d1) * x + 0.9375f;
        } else {
            return n1 * (x -= 2.625 / d1) * x + 0.984375f;
        }
    }

    public static float easeInOutBounce(float x) {
        return x < 0.5
                ? (1 - easeOutBounce(1 - 2 * x)) / 2
                : (1 + easeOutBounce(2 * x - 1)) / 2;
    }
}
