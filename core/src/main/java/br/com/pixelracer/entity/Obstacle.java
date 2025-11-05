package br.com.pixelracer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import br.com.pixelracer.config.Config;

public class Obstacle {
    private final Texture tex;
    public float x, y;
    public int lane;

    public Obstacle(String path, int lane, float startY) {
        this.tex  = new Texture(Gdx.files.internal(path));
        this.lane = lane;
        this.y    = startY;

        float center = Config.WORLD_W / 2f;
        float laneCenterX = center + (lane - 1) * Config.LANE_WIDTH;
        this.x = laneCenterX - tex.getWidth() / 2f;
    }

    public void update(float dy) { y -= dy; }

    public void render(SpriteBatch batch) { batch.draw(tex, x, y); }

    public Rectangle getBounds() {
        float pad = Config.HITBOX_SHRINK;
        return new Rectangle(x + pad, y + pad, tex.getWidth() - pad*2f, tex.getHeight() - pad*2f);
    }

    public float getHeight() { return tex.getHeight(); }

    public void dispose() { tex.dispose(); }
}
