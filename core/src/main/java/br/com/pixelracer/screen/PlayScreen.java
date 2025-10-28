package br.com.pixelracer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import br.com.pixelracer.PixelRacerGame;
import br.com.pixelracer.config.Config;

public class PlayScreen extends ScreenAdapter {
    private final PixelRacerGame game;
    private float elapsed = 0f;

    public PlayScreen(PixelRacerGame game) { this.game = game; }

    @Override
    public void render(float delta) {
        elapsed += delta;

        Gdx.gl.glClearColor(0.09f, 0.11f, 0.13f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();
        game.assets.fontSmall.draw(game.batch, "PLAY", 10, Config.WORLD_H - 10);
        game.assets.fontSmall.draw(game.batch, "Tempo: " + String.format("%.1fs", elapsed), 10, Config.WORLD_H - 30);
        game.assets.fontSmall.draw(game.batch, "[M] menu | [K] game over", 10, 30);
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) game.setScreen(new MenuScreen(game));
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) game.setScreen(new GameOverScreen(game, Math.round(elapsed)));
    }
}
