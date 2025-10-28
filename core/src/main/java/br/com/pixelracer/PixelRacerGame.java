package br.com.pixelracer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import br.com.pixelracer.assets.Assets;
import br.com.pixelracer.config.Config;
import br.com.pixelracer.screen.MenuScreen;

public class PixelRacerGame extends Game {
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public FitViewport viewport;
    public Assets assets;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(Config.WORLD_W, Config.WORLD_H, camera);
        viewport.apply(true);

        assets = new Assets();
        assets.load();

        setScreen(new MenuScreen(this));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (getScreen() != null) getScreen().dispose();
        batch.dispose();
        assets.dispose();
    }
}
