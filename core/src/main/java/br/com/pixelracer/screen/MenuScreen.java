package br.com.pixelracer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import br.com.pixelracer.PixelRacerGame;
import br.com.pixelracer.config.Config;

public class MenuScreen extends ScreenAdapter {
    private final PixelRacerGame game;

    public MenuScreen(PixelRacerGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Config.BG_R, Config.BG_G, Config.BG_B, Config.BG_A);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();

        if (game.assets.menuBg != null) {
            game.batch.draw(game.assets.menuBg, 0, 0, Config.WORLD_W, Config.WORLD_H);
        }

        game.assets.fontBig.draw(game.batch, "PIXEL RACER", 60, Config.WORLD_H - 80);
        game.assets.fontSmall.draw(game.batch, "ENTER: JOGAR", 60, Config.WORLD_H - 150);
        game.assets.fontSmall.draw(game.batch, "ESC: SAIR", 60, Config.WORLD_H - 175);

        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.assets.playButtonSound();
            game.setScreen(new PlayScreen(game));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.assets.playButtonSound();
            Gdx.app.exit();
        }
    }

    @Override
    public void show() { game.assets.playMenuMusic(); }

    @Override
    public void hide() { game.assets.stopAllMusic(); }
}
