package com.fmatos.samples.hud.service;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

import javax.inject.Inject;

/**
 * Created by fmatos on 8/07/2017.
 */

public class FontCache {

    final private Hashtable<String, Typeface> fontCache;

    @Inject
    public FontCache() {
        fontCache = new Hashtable<String, Typeface>();
    }

    public Typeface get(String name, Context context) {
        Typeface tf = fontCache.get(name);
        if(tf == null) {
            try {
                tf = Typeface.createFromAsset(context.getAssets(), name);
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        return tf;
    }
}