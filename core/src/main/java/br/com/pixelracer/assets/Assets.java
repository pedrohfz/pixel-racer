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

    public Texture menuBg;

    public Sound buttonSound;
    private Sound sfxPowerup;
    private Sound sfxHitCone;
    private Sound sfxSkid;

    public Music musicMenu;
    public Music musicPlay;
    public Music musicOver;

    public void load() {
        if (Gdx.files.internal("fonts/font.ttf").exists()) {
            FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/font.ttf"));

            FreeTypeFontParameter pSmall = new FreeTypeFontParameter();
            pSmall.size = 16;
            pSmall.color = Color.WHITE;
            pSmall.borderWidth = 2;
            pSmall.borderColor = Color.BLACK;

            FreeTypeFontParameter pBig = new FreeTypeFontParameter();
            pBig.size = 24;
            pBig.color = Color.WHITE;
            pBig.borderWidth = 3;
            pBig.borderColor = Color.BLACK;

            fontSmall = gen.generateFont(pSmall);
            fontBig   = gen.generateFont(pBig);
            gen.dispose();
        }

        if (Gdx.files.internal("gfx/menu.png").exists()) {
            menuBg = new Texture(Gdx.files.internal("gfx/menu.png"));
        }

        if (Gdx.files.internal("sfx/music_menu.mp3").exists()) {
            musicMenu = Gdx.audio.newMusic(Gdx.files.internal("sfx/music_menu.mp3"));
            musicMenu.setLooping(true);
            musicMenu.setVolume(0.35f);
        }

        if (Gdx.files.internal("sfx/music_play.mp3").exists()) {
            musicPlay = Gdx.audio.newMusic(Gdx.files.internal("sfx/music_play.mp3"));
            musicPlay.setLooping(true);
            musicPlay.setVolume(0.30f);
        }

        if (Gdx.files.internal("sfx/music_over.mp3").exists()) {
            musicOver = Gdx.audio.newMusic(Gdx.files.internal("sfx/music_over.mp3"));
            musicOver.setLooping(true);
            musicOver.setVolume(0.30f);
        }

        if (Gdx.files.internal("sfx/button.mp3").exists()) {
            buttonSound = Gdx.audio.newSound(Gdx.files.internal("sfx/button.mp3"));
        }

        if (Gdx.files.internal("sfx/powerup.mp3").exists()) {
            sfxPowerup = Gdx.audio.newSound(Gdx.files.internal("sfx/powerup.mp3"));
        }

        if (Gdx.files.internal("sfx/hit.mp3").exists()) {
            sfxHitCone = Gdx.audio.newSound(Gdx.files.internal("sfx/hit.mp3"));
        }

        if (Gdx.files.internal("sfx/skid.mp3").exists()) {
            sfxSkid = Gdx.audio.newSound(Gdx.files.internal("sfx/skid.mp3"));
        }
    }

    public void playMenuMusic() { stopAllMusic(); if (musicMenu != null) musicMenu.play(); }
    public void playPlayMusic() { stopAllMusic(); if (musicPlay != null) musicPlay.play(); }
    public void playOverMusic() { stopAllMusic(); if (musicOver != null) musicOver.play(); }

    public void stopAllMusic() {
        if (musicMenu != null) musicMenu.stop();
        if (musicPlay != null) musicPlay.stop();
        if (musicOver != null) musicOver.stop();
    }

    private void playLoop(Music m) {
        stopAllMusic();
        if (m != null) {
            m.setLooping(true);
            m.setVolume(1f);
            m.play();
        }
    }

    public void playButtonSound() { if (buttonSound != null) buttonSound.play(0.5f); }
    public void playPowerUpSound() { if (sfxPowerup != null) sfxPowerup.play(0.5f); }
    public void playHitSound() { if (sfxHitCone != null) sfxHitCone.play(1.0f); }
    public void playSkidSound() { if (sfxSkid != null) sfxSkid.play(0.5f); }

    public void dispose() {
        if (fontSmall != null) fontSmall.dispose();
        if (fontBig   != null) fontBig.dispose();
        if (menuBg != null) menuBg.dispose();
        if (buttonSound != null) buttonSound.dispose();
        if (musicMenu != null) musicMenu.dispose();
        if (musicPlay != null) musicPlay.dispose();
        if (musicOver != null) musicOver.dispose();
        if (sfxPowerup != null) sfxPowerup.dispose();
        if (sfxHitCone != null) sfxHitCone.dispose();
        if (sfxSkid != null) sfxSkid.dispose();
    }
}
