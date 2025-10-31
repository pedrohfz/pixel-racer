package br.com.pixelracer.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;


public class Assets {
    public BitmapFont fontSmall;
    public BitmapFont fontBig;
    public Sound buttonSound;

    
    public void load() {
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/PressStart2P.ttf"));

        FreeTypeFontParameter ps = new FreeTypeFontParameter();
        ps.size = 16;
        ps.color = Color.WHITE;
        ps.minFilter = Texture.TextureFilter.Nearest;
        ps.magFilter = Texture.TextureFilter.Nearest;

        FreeTypeFontParameter pb = new FreeTypeFontParameter();
        pb.size = 24;
        pb.color = Color.WHITE;
        pb.minFilter = Texture.TextureFilter.Nearest;
        pb.magFilter = Texture.TextureFilter.Nearest;

        fontSmall = gen.generateFont(ps);
        fontBig   = gen.generateFont(pb);
        gen.dispose();

        if (Gdx.files.internal("sfx/button.mp3").exists()) {
            buttonSound = Gdx.audio.newSound(Gdx.files.internal("sfx/button.mp3"));
        }

    }

    public void playButtonSound() {
        if (buttonSound != null) buttonSound.play(0.5f); // 50% de volume
    }

    public void dispose() {
        if (fontSmall != null) fontSmall.dispose();
        if (fontBig   != null) fontBig.dispose();
    }
}
