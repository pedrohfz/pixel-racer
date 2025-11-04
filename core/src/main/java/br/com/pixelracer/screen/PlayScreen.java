package br.com.pixelracer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import br.com.pixelracer.PixelRacerGame;
import br.com.pixelracer.config.Config;
import br.com.pixelracer.entity.Player;
import com.badlogic.gdx.math.MathUtils;

public class PlayScreen extends ScreenAdapter {
    private final PixelRacerGame game;
    private Player player;

    private Texture roadTile;
    private float scrollY = 0f;
    private float tileH;

    private float dashOffset = 0f;
    private float camBaseX, camBaseY;

    private float timeElapsed;

    public PlayScreen(PixelRacerGame game) {
        this.game = game;
        this.player = new Player();

        camBaseX = game.camera.position.x;
        camBaseY = game.camera.position.y;

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
        timeElapsed = 0f;
    }

    @Override
    public void render(float delta) {
        float dt = Math.min(delta, 1f / 30f);

        player.update(dt);

        float pxPerSecond = Math.max(Config.SCROLL_MIN, player.getSpeed() * Config.SCROLL_GAIN);
        scrollY += pxPerSecond * dt;

        dashOffset += (pxPerSecond * Config.DASH_SPEED_MULT) * dt;

        applyCameraShake(dt);

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

        timeElapsed += dt;
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

    private void applyCameraShake(float dt) {
        game.camera.position.set(camBaseX, camBaseY, 0);

        float spd = player.getSpeed();
        if (spd < Config.SHAKE_MIN_SPEED) {
            game.camera.update();
            return;
        }

        float t = MathUtils.clamp((spd - Config.SHAKE_MIN_SPEED) / 80f, 0f, 1f);
        float amp = Config.SHAKE_MAX_PIXELS * t;

        float offX = MathUtils.random(-amp, amp);
        float offY = MathUtils.random(-amp, amp * 0.6f);

        game.camera.position.add(offX, offY, 0);
        game.camera.update();
    }

    @Override
    public void show() { game.assets.playPlayMusic();}

    @Override
    public void hide() { game.assets.stopAllMusic(); }

    @Override
    public void dispose() {
        player.dispose();
        if (roadTile != null) roadTile.dispose();
    }
}
