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

    public Music musicMenu;
    public Music musicPlay;
    public Music musicOver;

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

        if (Gdx.files.internal("assets/sfx/Joshua McLean - Mountain Trials.mp3").exists()) {
            musicMenu = Gdx.audio.newMusic(Gdx.files.internal("assets/sfx/Joshua McLean - Mountain Trials.mp3"));
            musicMenu.setLooping(true);
            musicMenu.setVolume(0.35f);
        }

        if (Gdx.files.internal("assets/sfx/Jeremy Blake - Powerup!.mp3").exists()) {
            musicPlay = Gdx.audio.newMusic(Gdx.files.internal("assets/sfx/Jeremy Blake - Powerup!.mp3"));
            musicPlay.setLooping(true);
            musicPlay.setVolume(0.30f);
        }

        if (Gdx.files.internal("assets/sfx/GameOver8bits.mp3").exists()) {
            musicOver = Gdx.audio.newMusic(Gdx.files.internal("assets/sfx/GameOver8bits.mp3"));
            musicOver.setLooping(true);
            musicOver.setVolume(0.30f);
        }

        if (Gdx.files.internal("assets/gfx/menu.png").exists()) {
            menuBg = new Texture(Gdx.files.internal("gfx/menu.png"));
        }
    }

    public void playButtonSound() {
        if (buttonSound != null) buttonSound.play(0.3f);
    }

    public void playMenuMusic() { stopAllMusic(); if (musicMenu != null) musicMenu.play(); }
    public void playPlayMusic() { stopAllMusic(); if (musicPlay != null) musicPlay.play(); }
    public void playOverMusic() { stopAllMusic(); if (musicOver != null) musicOver.play(); }

    public void stopAllMusic() {
        if (musicMenu != null) musicMenu.stop();
        if (musicPlay != null) musicPlay.stop();
        if (musicOver != null) musicOver.stop();
    }

    public void dispose() {
        if (fontSmall != null) fontSmall.dispose();
        if (fontBig   != null) fontBig.dispose();
        if (buttonSound != null) buttonSound.dispose();
        if (musicMenu != null) musicMenu.dispose();
        if (musicPlay != null) musicPlay.dispose();
        if (musicOver != null) musicOver.dispose();
        if (menuBg != null) menuBg.dispose();
    }
}
