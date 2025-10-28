package br.com.pixelracer.assets;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Assets {
    public BitmapFont fontSmall;
    public BitmapFont fontBig;

    public void load() {
        fontSmall = new BitmapFont();
        fontBig   = new BitmapFont();
        fontBig.getData().setScale(1.4f);
    }

    public void dispose() {
        if (fontSmall != null) fontSmall.dispose();
        if (fontBig   != null) fontBig.dispose();
    }
}
