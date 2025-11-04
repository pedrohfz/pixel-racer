package br.com.pixelracer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import br.com.pixelracer.PixelRacerGame;
import br.com.pixelracer.config.Config;

public class GameOverScreen extends ScreenAdapter {
    private final PixelRacerGame game;
    private final int score;

    public GameOverScreen(PixelRacerGame game, int score) {
        this.game = game;
        this.score = score;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.12f, 0.06f, 0.06f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();
        game.assets.fontBig.draw(game.batch, "GAME OVER", 80, Config.WORLD_H - 80);
        game.assets.fontSmall.draw(game.batch, "SCORE: " + score, 80, Config.WORLD_H - 130);
        game.assets.fontSmall.draw(game.batch, "[R] JOGAR NOVAMENTE", 30, 50);
        game.assets.fontSmall.draw(game.batch, "[M] MENU", 30, 30);

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
