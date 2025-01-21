package com.beatcraft.data.types;

import org.joml.Vector3f;

import java.util.ArrayList;

public class PiecewiseCurve implements ISplinePath {

    public static class Curve {

        public float localStart;
        public float localEnd;
        public float globalStart;
        public float globalEnd;
        public ISplinePath spline;

        public Curve(float globalStart, float globalEnd, float localStart, float localEnd, ISplinePath curve) {
            this.globalStart = globalStart;
            this.globalEnd = globalEnd;
            this.localStart = localStart;
            this.localEnd = localEnd;
            this.spline = curve;
        }

    }

    private final ArrayList<Curve> curves = new ArrayList<>();

    public void addCurve(float globalStart, float globalEnd, float localStart, float localEnd, ISplinePath curve) {
        Curve spline = new Curve(globalStart, globalEnd, localStart, localEnd, curve);
        curves.add(spline);
    }

    public Vector3f evaluate(float t) {
        for (Curve curve : curves) {
            if (curve.globalStart <= t && t <= curve.globalEnd) {
                float local = (t - curve.globalStart) / (curve.globalEnd - curve.globalStart);
                float mapped = curve.localStart + (local * (curve.localEnd - curve.localStart));
                return curve.spline.evaluate(mapped);
            }
        }
        return new Vector3f(0, 0, 0);
    }

    public Vector3f getTangent(float t) {
        for (Curve curve : curves) {
            if (curve.globalStart <= t && t <= curve.globalEnd) {
                float local = (t - curve.globalStart) / (curve.globalEnd - curve.globalStart);
                float mapped = curve.localStart + (local * (curve.localEnd - curve.localStart));
                return curve.spline.getTangent(mapped);
            }
        }
        return new Vector3f(0, 0, 0);
    }
}
