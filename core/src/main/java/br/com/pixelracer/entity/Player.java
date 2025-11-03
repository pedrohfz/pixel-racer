package br.com.pixelracer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import br.com.pixelracer.config.Config;

public class Player {
    private Texture texture;
    private float x, y;
    private int lane;
    private float speed;

    private final float laneWidth = 100f;
    private final float baseY = 100f;

    public Player() {
        texture = new Texture(Gdx.files.internal("assets/gfx/player.png"));
        lane = 1;
        updatePosition();
    }

    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && lane > 0) {
            lane--;
            updatePosition();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && lane < 2) {
            lane++;
            updatePosition();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            speed += delta * 50;
        } else {
            speed -= delta * 20;
        }

        if (speed < 0) speed = 0;
        if (speed > 200) speed = 200;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x - texture.getWidth() / 2f, y);
    }

    private void updatePosition() {
        x = Config.WORLD_W / 2f + (lane - 1) * laneWidth;
        y = baseY;
    }

    public float getSpeed() {
        return speed;
    }

    public void dispose() {
        texture.dispose();
    }
}
