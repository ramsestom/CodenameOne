package com.codename1.impl.android;

import android.graphics.DashPathEffect;

/**
 * A wrapper arround DashPathEffect allowing to access its attributes
 */
public class AndroidDashPathEffect extends DashPathEffect {
 
    float[] dashPattern;
    float dashPhase;

    public AndroidDashPathEffect(float[] intervals, float phase) {
        super(intervals, phase);
        this.dashPattern = intervals;
        this.dashPhase = phase;
    }
    
    public float[] getPattern(){
        return dashPattern;
    }
    
    public float getPhase(){
        return dashPhase;
    }
}
