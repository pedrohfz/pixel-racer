package br.com.pixelracer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

public class Oil extends Obstacle {

    private Texture texture;
    private final Rectangle bounds = new Rectangle();

    public Oil(int lane, float startY) {
        super("gfx/oil.png", lane, startY);

        if (Gdx.files.internal("gfx/oil.png").exists()) {
            texture = new Texture(Gdx.files.internal("gfx/oil.png"));
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        updateBounds();
    }

    public void update(float dy) {
        y -= dy;
        updateBounds();
    }

    public void render(Batch batch) {
        if (texture == null) return;
        batch.draw(texture, x - texture.getWidth() / 2f, y, texture.getWidth(), texture.getHeight());
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getY() {
        return y;
    }

    public float getHeight() {
        return texture == null ? 0f : texture.getHeight();
    }

    public void dispose() {
        if (texture != null) texture.dispose();
    }

    private void updateBounds() {
        if (texture == null) return;
        float w = texture.getWidth();
        float h = texture.getHeight();
        bounds.set(x - w / 2f + 6f, y + 6f, w - 12f, h - 12f);
    }
}
