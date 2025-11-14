package br.com.pixelracer.screen;

import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import br.com.pixelracer.PixelRacerGame;
import br.com.pixelracer.config.Config;
import br.com.pixelracer.entity.Obstacle;
import br.com.pixelracer.entity.Oil;
import br.com.pixelracer.entity.Player;
import br.com.pixelracer.entity.PowerUp;

public class PlayScreen extends ScreenAdapter {

    private final PixelRacerGame game;
    private Player player;

    private Texture roadTile;
    private Texture white1x1;
    private Texture playerStarTex;
    private float tileH;
    private float scrollY = 0f;

    private final Array<Obstacle> obstacles = new Array<>();
    private float spawnTimer = 0f;
    private final float[] laneCooldown = new float[Config.LANE_COUNT];

    private float camBaseX, camBaseY;

    private float timeElapsed = 0f;
    private float scoreSeconds = 0f;
    private float nextBurstIn = 0f;
    private float burstLeft = 0f;

    private float oilTimer = 0f;
    private float invincibleTimer = 0f;

    private float fadeInAlpha = 1f;
    private float impactFlashAlpha = 0f;
    private float powerupBlinkAlpha = 0f;
    private float impactDelay = 0f;
    private boolean pendingGameOver = false;
    private boolean hitSfxPlayed = false;

    private final Array<Particle> particles = new Array<>(64);

    private int bestTime = 0;
    private boolean paused = false;

    private int phase = 1;
    private float phaseBannerTimer = 0f;

    private float consistency = 100f;
    private float lastSpeed = 0f;

    private final GlyphLayout layout = new GlyphLayout();

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

        if (Gdx.files.internal("gfx/player_star.png").exists()) {
            playerStarTex = new Texture(Gdx.files.internal("gfx/player_star.png"));
            playerStarTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1, 1, 1, 1);
        pm.fill();
        white1x1 = new Texture(pm);
        pm.dispose();

        nextBurstIn = MathUtils.random(Config.BURST_MIN_INTERVAL_S, Config.BURST_MAX_INTERVAL_S);

        Preferences prefs = Gdx.app.getPreferences("pixelracer");
        bestTime = prefs.getInteger("bestTime", 0);
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) paused = !paused;

        final float dt = Math.min(delta, 1f / 30f);

        if (!pendingGameOver && !paused) {
            player.update(dt);

            for (int i = 0; i < laneCooldown.length; i++) laneCooldown[i] -= dt;

            float speedMul = (oilTimer > 0f && invincibleTimer <= 0f) ? Config.OIL_SLOW_FACTOR : 1f;
            float pxPerSecond = Math.max(Config.SCROLL_MIN, player.getSpeed() * Config.SCROLL_GAIN) * speedMul;
            scrollY += pxPerSecond * dt;

            timeElapsed += dt;

            float scoreMul = 0.8f + 0.7f * (consistency / 100f);
            scoreSeconds += dt * scoreMul;

            if (oilTimer > 0f) oilTimer -= dt;
            if (invincibleTimer > 0f) invincibleTimer -= dt;

            updatePhase();
            updateConsistency(dt);

            if (burstLeft > 0f) {
                burstLeft -= dt;
                if (burstLeft <= 0f) {
                    float baseMin = Config.BURST_MIN_INTERVAL_S;
                    float baseMax = Config.BURST_MAX_INTERVAL_S;
                    float phaseMul = (phase == 3) ? 0.7f : 1f;
                    nextBurstIn = MathUtils.random(baseMin, baseMax) * phaseMul;
                    burstLeft = 0f;
                }
            } else {
                nextBurstIn -= dt;
                if (nextBurstIn <= 0f) {
                    float dur = Config.BURST_DURATION_S * ((phase == 3) ? 1.4f : 1f);
                    burstLeft = dur;
                }
            }

            spawnTimer -= dt;

            float intervalBase = MathUtils.lerp(
                Config.SPAWN_MIN_SEC,
                Config.SPAWN_BASE_SEC,
                MathUtils.clamp(1f - (player.getSpeed() / 200f), 0f, 1f)
            );

            float timeMul = difficultyTimeMul(timeElapsed);
            float jitterFactor = 1f + MathUtils.random(-Config.SPAWN_JITTER, Config.SPAWN_JITTER);

            float phaseSpawnMul = (phase == 2) ? 0.9f : 1f;
            float intervalThis = intervalBase * timeMul * jitterFactor * phaseSpawnMul;
            if (burstLeft > 0f) intervalThis *= Config.BURST_MULT;
            intervalThis = Math.max(0.25f, intervalThis);

            if (spawnTimer <= 0f) {
                boolean spawned = false;
                int attempts = 5;
                float laneFactor = laneCooldownFactor(timeElapsed);

                while (!spawned && attempts-- > 0) {
                    int lane = MathUtils.random(0, Config.LANE_COUNT - 1);
                    if (laneCooldown[lane] <= 0f) {
                        spawnVariedOnLane(lane, Config.WORLD_H + 40f);
                        laneCooldown[lane] = intervalThis * laneFactor;
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

            boolean invincible = invincibleTimer > 0f;
            for (int i = obstacles.size - 1; i >= 0; i--) {
                Obstacle o = obstacles.get(i);
                if (!o.getBounds().overlaps(player.getBounds())) continue;

                if (o instanceof PowerUp) {
                    invincibleTimer = Math.max(invincibleTimer, Config.POWERUP_DURATION_S);
                    powerupBlinkAlpha = 0.35f;
                    emitStarburst(player.getBounds().x + player.getBounds().width * 0.5f, player.getBounds().y + player.getBounds().height * 0.6f, 16);
                    game.assets.playPowerUpSound();
                    o.dispose();
                    obstacles.removeIndex(i);
                    continue;
                }

                if (o instanceof Oil) {
                    if (!invincible) {
                        boolean wasOff = oilTimer <= 0f;
                        oilTimer = Config.OIL_EFFECT_S;
                        if (wasOff) game.assets.playSkidSound();
                        emitSkid(player.getBounds().x + player.getBounds().width * 0.5f, player.getBounds().y + 6f, 10);
                        consistency = Math.max(0f, consistency - 12f);
                    }
                    o.dispose();
                    obstacles.removeIndex(i);
                    continue;
                }

                if (invincible) {
                    emitStarburst(player.getBounds().x + player.getBounds().width * 0.5f, player.getBounds().y + player.getBounds().height * 0.5f, 10);
                    o.dispose();
                    obstacles.removeIndex(i);
                } else {
                    if (!hitSfxPlayed) {
                        game.assets.playHitSound();
                        hitSfxPlayed = true;
                    }
                    impactFlashAlpha = 1f;
                    impactDelay = 0.15f;
                    pendingGameOver = true;
                    break;
                }
            }
        } else {
            if (pendingGameOver) {
                if (impactDelay > 0f) {
                    impactDelay -= dt;
                    if (impactDelay <= 0f) {
                        if (Math.round(scoreSeconds) > bestTime) {
                            Preferences prefs = Gdx.app.getPreferences("pixelracer");
                            prefs.putInteger("bestTime", Math.round(scoreSeconds));
                            prefs.flush();
                        }
                        game.setScreen(new GameOverScreen(game, Math.round(scoreSeconds)));
                        return;
                    }
                }
            }
        }

        if (!pendingGameOver && !paused) {
            for (int i = particles.size - 1; i >= 0; i--) {
                Particle p = particles.get(i);
                p.life -= dt;
                if (p.life <= 0f) {
                    particles.removeIndex(i);
                    continue;
                }
                p.x += p.vx * dt;
                p.y += p.vy * dt;
                p.vy += p.ay * dt;
            }
        }

        if (fadeInAlpha > 0f) fadeInAlpha = Math.max(0f, fadeInAlpha - dt * 3f);
        if (powerupBlinkAlpha > 0f) powerupBlinkAlpha = Math.max(0f, powerupBlinkAlpha - dt * 3f);
        if (impactFlashAlpha > 0f) impactFlashAlpha = Math.max(0f, impactFlashAlpha - dt * 5f);
        if (phaseBannerTimer > 0f) phaseBannerTimer = Math.max(0f, phaseBannerTimer - dt);

        if (!pendingGameOver && !paused) applyCameraShake(); else { game.camera.position.set(camBaseX, camBaseY, 0); game.camera.update(); }

        Gdx.gl.glClearColor(Config.BG_R, Config.BG_G, Config.BG_B, Config.BG_A);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();

        if (roadTile != null) {
            float yStart = -(scrollY % tileH);
            game.batch.draw(roadTile, 0, yStart, Config.WORLD_W, tileH);
            game.batch.draw(roadTile, 0, yStart + tileH, Config.WORLD_W, tileH);
            game.batch.draw(roadTile, 0, yStart + tileH * 2f, Config.WORLD_W, tileH);
        }

        for (Obstacle o : obstacles) o.render(game.batch);

        boolean inv = invincibleTimer > 0f && playerStarTex != null;
        if (inv) {
            float x = player.getBounds().x;
            float y = player.getBounds().y;
            float w = player.getBounds().width;
            float h = player.getBounds().height;
            game.batch.draw(playerStarTex, x, y, w, h);
        } else {
            player.render(game.batch);
        }

        for (int i = 0; i < particles.size; i++) {
            Particle p = particles.get(i);
            float a = p.life / p.lifeMax;
            game.batch.setColor(p.r, p.g, p.b, a * p.alpha);
            game.batch.draw(white1x1, p.x - p.size * 0.5f, p.y - p.size * 0.5f, p.size, p.size);
        }
        game.batch.setColor(1, 1, 1, 1);

        float leftX = 12f;
        float topY = Config.WORLD_H - 12f;

        game.assets.fontSmall.draw(game.batch, String.format(Locale.US, "Speed: %.0f", player.getSpeed()), leftX, topY);
        game.assets.fontSmall.draw(game.batch, String.format(Locale.US, "Time: %.1f", timeElapsed), leftX, topY - 18f);
        game.assets.fontSmall.draw(game.batch, String.format(Locale.US, "Best: %ds", bestTime), leftX, topY - 36f);

        String faseTxt = "Fase: " + phase;
        layout.setText(game.assets.fontSmall, faseTxt);
        float faseX = Config.WORLD_W - 12f - layout.width;
        game.assets.fontSmall.draw(game.batch, faseTxt, faseX, topY);

        String scoreTxt = "Score: " + Math.round(scoreSeconds);
        layout.setText(game.assets.fontSmall, scoreTxt);
        float scoreX = Config.WORLD_W - 12f - layout.width;
        game.assets.fontSmall.draw(game.batch, scoreTxt, scoreX, topY - 18f);

        if (paused) {
            game.batch.setColor(0, 0, 0, 0.45f);
            game.batch.draw(white1x1, 0, 0, Config.WORLD_W, Config.WORLD_H);
            game.batch.setColor(1, 1, 1, 1);
            String t = "PAUSED - P";
            layout.setText(game.assets.fontSmall, t);
            game.assets.fontSmall.draw(game.batch, t, (Config.WORLD_W - layout.width) * 0.5f, Config.WORLD_H * 0.55f);
        }

        if (powerupBlinkAlpha > 0f) {
            game.batch.setColor(1, 1, 1, powerupBlinkAlpha);
            game.batch.draw(white1x1, 0, 0, Config.WORLD_W, Config.WORLD_H);
            game.batch.setColor(1, 1, 1, 1);
        }

        if (impactFlashAlpha > 0f) {
            game.batch.setColor(1, 1, 1, impactFlashAlpha);
            game.batch.draw(white1x1, 0, 0, Config.WORLD_W, Config.WORLD_H);
            game.batch.setColor(1, 1, 1, 1);
        }

        if (phaseBannerTimer > 0f) {
            float a = MathUtils.clamp(phaseBannerTimer / 1.0f, 0f, 1f);
            game.batch.setColor(0, 0, 0, 0.35f * a);
            game.batch.draw(white1x1, 0, 0, Config.WORLD_W, Config.WORLD_H);
            game.batch.setColor(1, 1, 1, 1);
            String banner = "FASE " + phase;
            layout.setText(game.assets.fontSmall, banner);
            game.assets.fontSmall.draw(game.batch, banner, (Config.WORLD_W - layout.width) * 0.5f, Config.WORLD_H * 0.62f);
        }

        if (fadeInAlpha > 0f) {
            game.batch.setColor(0, 0, 0, fadeInAlpha);
            game.batch.draw(white1x1, 0, 0, Config.WORLD_W, Config.WORLD_H);
            game.batch.setColor(1, 1, 1, 1);
        }

        game.batch.end();
    }

    private void updatePhase() {
        int target = timeElapsed < 60f ? 1 : (timeElapsed < 120f ? 2 : 3);
        if (target != phase) {
            phase = target;
            phaseBannerTimer = 1.0f;
            game.assets.playPhaseChangeSound();
        }
    }

    private void updateConsistency(float dt) {
        float spd = player.getSpeed();
        float rate = (Math.abs(spd - lastSpeed)) / Math.max(0.0001f, dt);
        if (rate <= 30f) {
            consistency = Math.min(100f, consistency + 20f * dt);
        } else {
            consistency = Math.max(0f, consistency - 35f * dt);
        }
        if (oilTimer > 0f) {
            consistency = Math.max(0f, consistency - 20f * dt);
        }
        lastSpeed = spd;
    }

    private float difficultyTimeMul(float elapsed) {
        float t = MathUtils.clamp(elapsed / Config.TIME_FULL_DIFFICULTY_S, 0f, 1f);
        return MathUtils.lerp(1f, Config.DIFF_TIME_MIN_MUL, t);
    }

    private float laneCooldownFactor(float elapsed) {
        float t = MathUtils.clamp(elapsed / Config.TIME_FULL_DIFFICULTY_S, 0f, 1f);
        return MathUtils.lerp(Config.LANE_COOLDOWN_BASE, Config.LANE_COOLDOWN_MIN, t);
    }

    private void spawnVariedOnLane(int lane, float startY) {
        float r = MathUtils.random(1f);
        if (r < Config.WEIGHT_CONE) {
            obstacles.add(new Obstacle("gfx/cone.png", lane, startY));
        } else if (r < Config.WEIGHT_CONE + Config.WEIGHT_OIL) {
            obstacles.add(new Oil(lane, startY));
        } else {
            obstacles.add(new PowerUp(lane, startY));
        }
    }

    private void emitStarburst(float cx, float cy, int count) {
        for (int i = 0; i < count; i++) {
            float a = MathUtils.random(0f, 360f);
            float sp = MathUtils.random(50f, 110f);
            float vx = MathUtils.cosDeg(a) * sp;
            float vy = MathUtils.sinDeg(a) * sp;
            Particle p = new Particle();
            p.x = cx;
            p.y = cy;
            p.vx = vx;
            p.vy = vy;
            p.ay = -90f;
            p.size = MathUtils.random(3f, 5f);
            p.life = p.lifeMax = MathUtils.random(0.35f, 0.55f);
            p.alpha = 0.9f;
            p.r = 1f;
            p.g = MathUtils.random(0.9f, 1f);
            p.b = MathUtils.random(0.2f, 0.5f);
            particles.add(p);
        }
    }

    private void emitSkid(float cx, float cy, int count) {
        for (int i = 0; i < count; i++) {
            float a = MathUtils.random(-60f, 60f);
            float sp = MathUtils.random(30f, 80f);
            Particle p = new Particle();
            p.x = cx + MathUtils.random(-6f, 6f);
            p.y = cy + MathUtils.random(-2f, 2f);
            p.vx = MathUtils.cosDeg(a) * sp;
            p.vy = MathUtils.sinDeg(a) * sp * 0.4f;
            p.ay = -120f;
            p.size = MathUtils.random(2f, 4f);
            p.life = p.lifeMax = MathUtils.random(0.25f, 0.45f);
            p.alpha = 0.7f;
            p.r = 0f;
            p.g = 0f;
            p.b = 0f;
            particles.add(p);
        }
    }

    private void applyCameraShake() {
        game.camera.position.set(camBaseX, camBaseY, 0);
        float spd = player.getSpeed();
        if (spd < Config.SHAKE_MIN_SPEED) { game.camera.update(); return; }
        float t = MathUtils.clamp((spd - Config.SHAKE_MIN_SPEED) / 80f, 0f, 1f);
        t = t * t;
        float amp = Config.SHAKE_MAX_PIXELS * t;
        game.camera.position.add(MathUtils.random(-amp, amp), MathUtils.random(-amp, amp * 0.6f), 0);
        game.camera.update();
    }

    @Override
    public void dispose() {
        player.dispose();
        if (roadTile != null) roadTile.dispose();
        if (white1x1 != null) white1x1.dispose();
        if (playerStarTex != null) playerStarTex.dispose();
        for (Obstacle o : obstacles) o.dispose();
        game.assets.stopAllMusic();
    }

    private static class Particle {
        float x, y, vx, vy, ay, size, life, lifeMax, alpha, r, g, b;
    }
}
