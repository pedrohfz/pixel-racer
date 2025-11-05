package br.com.pixelracer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import br.com.pixelracer.PixelRacerGame;
import br.com.pixelracer.config.Config;
import br.com.pixelracer.entity.Player;
import br.com.pixelracer.entity.Obstacle;

public class PlayScreen extends ScreenAdapter {
    private final PixelRacerGame game;
    private Player player;

    private Texture roadTile;
    private float   tileH;
    private float   scrollY = 0f;

    private final Array<Obstacle> obstacles = new Array<>();
    private float spawnTimer = 0f;
    private final float[] laneCooldown = new float[Config.LANE_COUNT];

    private float camBaseX, camBaseY;

    private float timeElapsed = 0f;

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
        }
    }

    @Override
    public void show() {
        game.assets.playPlayMusic();
    }

    @Override
    public void hide() {
        game.assets.stopAllMusic();
    }

    @Override
    public void render(float delta) {
        final float dt = Math.min(delta, 1f / 30f);

        player.update(dt);

        for (int i = 0; i < laneCooldown.length; i++) {
            laneCooldown[i] -= dt;
        }

        float pxPerSecond = Math.max(Config.SCROLL_MIN, player.getSpeed() * Config.SCROLL_GAIN);
        scrollY += pxPerSecond * dt;

        spawnTimer -= dt;

        float intervalBase = MathUtils.lerp(
            Config.SPAWN_MIN_SEC,
            Config.SPAWN_BASE_SEC,
            MathUtils.clamp(1f - (player.getSpeed() / 200f), 0f, 1f)); // 200 ~ vmax

        float jitterFactor = 1f + MathUtils.random(-Config.SPAWN_JITTER, Config.SPAWN_JITTER);
        float intervalThis = Math.max(0.25f, intervalBase * jitterFactor);

        if (spawnTimer <= 0f) {
            boolean spawned = false;
            int attempts = 5; // evita loop infinito
            while (!spawned && attempts-- > 0) {
                int lane = MathUtils.random(0, Config.LANE_COUNT - 1);
                if (laneCooldown[lane] <= 0f) {
                    spawnObstacleOnLane(lane);
                    laneCooldown[lane] = intervalThis * Config.LANE_COOLDOWN_FACTOR;
                    spawned = true;
                }
            }
            spawnTimer = intervalThis;
        }

        float dy = pxPerSecond * dt;
        for (int i = obstacles.size - 1; i >= 0; i--) {
            Obstacle o = obstacles.get(i);
            o.update(dy);
            if (o.y < -o.getHeight()) {
                o.dispose();
                obstacles.removeIndex(i);
            }
        }

        for (Obstacle o : obstacles) {
            if (o.getBounds().overlaps(player.getBounds())) {
                game.assets.playButtonSound();
                game.setScreen(new GameOverScreen(game, Math.round(timeElapsed)));
                return;
            }
        }

        applyCameraShake();

        Gdx.gl.glClearColor(Config.BG_R, Config.BG_G, Config.BG_B, Config.BG_A);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();

        if (roadTile != null) {
            float yStart = - (scrollY % tileH);
            game.batch.draw(roadTile, 0, yStart,               Config.WORLD_W, tileH);
            game.batch.draw(roadTile, 0, yStart + tileH,       Config.WORLD_W, tileH);
            game.batch.draw(roadTile, 0, yStart + tileH * 2f,  Config.WORLD_W, tileH);
        }

        for (Obstacle o : obstacles) o.render(game.batch);

        player.render(game.batch);

        timeElapsed += dt;
        game.assets.fontSmall.draw(game.batch, String.format("Speed: %.0f", player.getSpeed()), 20, Config.WORLD_H - 20);
        game.assets.fontSmall.draw(game.batch, String.format("Time: %.1f",  timeElapsed),       20, Config.WORLD_H - 40);
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

    private void spawnObstacleOnLane(int lane) {
        float startY = Config.WORLD_H + 40f;
        obstacles.add(new Obstacle("gfx/cone.png", lane, startY));
    }

    private void applyCameraShake() {
        game.camera.position.set(camBaseX, camBaseY, 0);

        float spd = player.getSpeed();
        if (spd < Config.SHAKE_MIN_SPEED) { game.camera.update(); return; }

        float t   = MathUtils.clamp((spd - Config.SHAKE_MIN_SPEED) / 80f, 0f, 1f);
        float amp = Config.SHAKE_MAX_PIXELS * t;

        game.camera.position.add(MathUtils.random(-amp, amp),
            MathUtils.random(-amp, amp * 0.6f), 0);
        game.camera.update();
    }

    @Override
    public void dispose() {
        player.dispose();
        if (roadTile != null) roadTile.dispose();
        for (Obstacle o : obstacles) o.dispose();
        game.assets.stopAllMusic();
    }
}
