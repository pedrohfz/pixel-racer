package br.com.pixelracer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import br.com.pixelracer.PixelRacerGame;
import br.com.pixelracer.config.Config;
import br.com.pixelracer.entity.Player;

public class PlayScreen extends ScreenAdapter {
    private final PixelRacerGame game;
    private Player player;

    private Texture roadTile;
    private float scrollY = 0f;
    private float tileH;

    private float timeElapsed;

    public PlayScreen(PixelRacerGame game) {
        this.game = game;
        this.player = new Player();

        game.assets.playMusic();

        if (Gdx.files.internal("gfx/road_tile.png").exists()) {
            roadTile = new Texture(Gdx.files.internal("gfx/road_tile.png"));
            roadTile.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            float srcW = roadTile.getWidth();
            float srcH = roadTile.getHeight();
            float scale = Config.WORLD_W / srcW;
            tileH = srcH * scale;
        } else if (Gdx.files.internal("assets/gfx/road.png").exists()) {
            roadTile = new Texture(Gdx.files.internal("assets/gfx/road.png"));
            roadTile.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            tileH = Config.WORLD_H;
        }
    }

    @Override
    public void render(float delta) {
        float dt = Math.min(delta, 1f / 30f);

        // update do player
        player.update(dt);

        float pxPerSecond = Math.max(Config.SCROLL_MIN, player.getSpeed() * Config.SCROLL_GAIN);
        scrollY += pxPerSecond * dt;

        Gdx.gl.glClearColor(Config.BG_R, Config.BG_G, Config.BG_B, Config.BG_A);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();

        if (roadTile != null) {
            float yStart = - (scrollY % tileH);

            game.batch.draw(roadTile, 0, yStart, Config.WORLD_W, tileH);
            game.batch.draw(roadTile, 0, yStart + tileH, Config.WORLD_W, tileH);
            game.batch.draw(roadTile, 0, yStart + tileH * 2f, Config.WORLD_W, tileH);
        }

        player.render(game.batch);

        game.assets.fontSmall.draw(game.batch, String.format("Speed: %.0f", player.getSpeed()), 20, Config.WORLD_H - 20);
        game.assets.fontSmall.draw(game.batch, String.format("Time: %.1f", timeElapsed += dt), 20, Config.WORLD_H - 40);
        game.assets.fontSmall.draw(game.batch, "M: MENU | K: GAME OVER", 20, 30);

        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            game.assets.playButtonSound();
            game.setScreen(new MenuScreen(game));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            game.assets.playButtonSound();
            game.setScreen(new GameOverScreen(game, Math.round(timeElapsed)));
        }
    }

    @Override
    public void dispose() {
        player.dispose();
        if (roadTile != null) roadTile.dispose();
        game.assets.stopMusic();
    }
}
