package br.com.pixelracer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import br.com.pixelracer.assets.Assets;
import br.com.pixelracer.config.Config;
import br.com.pixelracer.screen.MenuScreen;

public class PixelRacerGame extends Game {
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public Assets assets;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Config.WORLD_W, Config.WORLD_H);
        camera.setToOrtho(false, Config.WORLD_W, Config.WORLD_H);

        assets = new Assets();
        assets.load();

        setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (assets != null) assets.dispose();
        if (batch != null) batch.dispose();
    }
}
