package br.com.pixelracer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import br.com.pixelracer.config.Config;

public class Player {
    private final Texture texture;

    private int lane = 1;
    private float x, y;

    private float speed = 0f;

    public Player() {
        texture = new Texture(Gdx.files.internal("gfx/player.png"));
        y = Config.PLAYER_Y;
        updateX();
    }

    private void updateX() {
        x = Config.laneCenterX(lane);
    }

    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && lane > 0) {
            lane--; updateX();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && lane < Config.LANE_COUNT - 1) {
            lane++; updateX();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            speed += delta * 50f;
        } else {
            speed -= delta * 20f;
        }
        if (speed < 0)   speed = 0;
        if (speed > 200) speed = 200;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x - texture.getWidth()/2f, y);
    }

    public Rectangle getBounds() {
        float pad = Config.HITBOX_SHRINK;
        return new Rectangle(
            x - texture.getWidth()/2f + pad,
            y + pad,
            texture.getWidth() - pad*2f,
            texture.getHeight() - pad*2f
        );
    }

    public float getSpeed() { return speed; }

    public void dispose() { texture.dispose(); }
}
