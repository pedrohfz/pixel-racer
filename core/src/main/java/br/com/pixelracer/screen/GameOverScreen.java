package br.com.pixelracer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import br.com.pixelracer.PixelRacerGame;
import br.com.pixelracer.config.Config;

public class GameOverScreen extends ScreenAdapter {
    private final PixelRacerGame game;
    private final int scoreSeconds;

    public GameOverScreen(PixelRacerGame game, int scoreSeconds) {
        this.game = game;
        this.scoreSeconds = scoreSeconds;
    }

    @Override public void show() { game.assets.playOverMusic(); }
    @Override public void hide() { game.assets.stopAllMusic(); }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.12f, 0.06f, 0.06f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();

        GlyphLayout title = new GlyphLayout(game.assets.fontBig, "GAME OVER");
        float tx = (Config.WORLD_W - title.width) / 2f;
        game.assets.fontBig.draw(game.batch, title, tx, Config.WORLD_H - 100);

        GlyphLayout sc = new GlyphLayout(game.assets.fontSmall, "SCORE: " + scoreSeconds);
        game.assets.fontSmall.draw(game.batch, sc, 80, Config.WORLD_H - 170);

        GlyphLayout retry = new GlyphLayout(game.assets.fontSmall, "[R] JOGAR NOVAMENTE");
        GlyphLayout menu  = new GlyphLayout(game.assets.fontSmall, "[M] MENU");
        float cx1 = (Config.WORLD_W - retry.width) / 2f;
        float cx2 = (Config.WORLD_W - menu.width)  / 2f;

        game.assets.fontSmall.draw(game.batch, retry, cx1, 60);
        game.assets.fontSmall.draw(game.batch, menu,  cx2, 35);

        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            game.assets.playButtonSound();
            game.setScreen(new PlayScreen(game));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            game.assets.playButtonSound();
            game.setScreen(new MenuScreen(game));
        }
    }
}
