package br.com.pixelracer.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
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
    public Music bgMusic;
    public Texture menuBg;

    public void load() {
        if (Gdx.files.internal("assets/fonts/PressStart2P.ttf").exists()) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/PressStart2P.ttf"));

            FreeTypeFontParameter smallParam = new FreeTypeFontParameter();
            smallParam.size = 16;
            smallParam.color = Color.WHITE;
            smallParam.borderWidth = 2;
            smallParam.borderColor = Color.BLACK;
            smallParam.minFilter = Texture.TextureFilter.Nearest;
            smallParam.magFilter = Texture.TextureFilter.Nearest;

            FreeTypeFontParameter bigParam = new FreeTypeFontParameter();
            bigParam.size = 24;
            bigParam.color = Color.WHITE;
            bigParam.borderWidth = 3;
            bigParam.borderColor = Color.BLACK;
            bigParam.minFilter = Texture.TextureFilter.Nearest;
            bigParam.magFilter = Texture.TextureFilter.Nearest;

            fontSmall = generator.generateFont(smallParam);
            fontBig   = generator.generateFont(bigParam);
            generator.dispose();
        }

        if (Gdx.files.internal("assets/sfx/button.mp3").exists()) {
            buttonSound = Gdx.audio.newSound(Gdx.files.internal("sfx/button.mp3"));
        }

        if (Gdx.files.internal("assets/sfx/Jeremy Blake - Powerup!.mp3").exists()) {
            bgMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/sfx/Jeremy Blake - Powerup!.mp3"));
            bgMusic.setLooping(true);
            bgMusic.setVolume(0.3f);
        }

        if (Gdx.files.internal("assets/gfx/menu.png").exists()) {
            menuBg = new Texture(Gdx.files.internal("gfx/menu.png"));
        }


    }

    public void playButtonSound() {
        if (buttonSound != null) buttonSound.play(0.3f);
    }

    public void playMusic() {
        if (bgMusic != null && !bgMusic.isPlaying()) bgMusic.play();
    }

    public void stopMusic() {
        if (bgMusic != null && bgMusic.isPlaying()) bgMusic.stop();
    }

    public void dispose() {
        if (fontSmall != null) fontSmall.dispose();
        if (fontBig   != null) fontBig.dispose();
        if (buttonSound != null) buttonSound.dispose();
        if (bgMusic != null) bgMusic.dispose();
        if (menuBg != null) menuBg.dispose();
    }
}
