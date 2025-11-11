package br.com.pixelracer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import br.com.pixelracer.PixelRacerGame;
import br.com.pixelracer.config.Config;

public class GameOverScreen extends ScreenAdapter {

    private final PixelRacerGame game;
    private final int finalScore;
    private int bestScore;

    private final GlyphLayout layout = new GlyphLayout();

    public GameOverScreen(PixelRacerGame game, int finalScore) {
        this.game = game;
        this.finalScore = finalScore;

        Preferences prefs = Gdx.app.getPreferences("pixelracer");
        bestScore = prefs.getInteger("bestTime", 0);
        if (finalScore > bestScore) {
            bestScore = finalScore;
            prefs.putInteger("bestTime", bestScore);
            prefs.flush();
        }
    }

    @Override
    public void show() {
        game.assets.playOverMusic();
    }

    @Override
    public void hide() {
        game.assets.stopAllMusic();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.18f, 0.02f, 0.02f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();

        String t1 = "GAME OVER";
        String t2 = "SCORE: " + finalScore + "s";
        String t3 = "BEST: " + bestScore + "s";
        String t4 = "ENTER - RESTART";
        String t5 = "M - MENU";

        float cx = Config.WORLD_W * 0.5f;
        float y = Config.WORLD_H * 0.60f;

        layout.setText(game.assets.fontBig != null ? game.assets.fontBig : game.assets.fontSmall, t1);
        if (game.assets.fontBig != null)
            game.assets.fontBig.draw(game.batch, t1, cx - layout.width * 0.5f, y);
        else
            game.assets.fontSmall.draw(game.batch, t1, cx - layout.width * 0.5f, y);

        y -= 40f;
        layout.setText(game.assets.fontSmall, t2);
        game.assets.fontSmall.draw(game.batch, t2, cx - layout.width * 0.5f, y);

        y -= 20f;
        layout.setText(game.assets.fontSmall, t3);
        game.assets.fontSmall.draw(game.batch, t3, cx - layout.width * 0.5f, y);

        y -= 60f;
        layout.setText(game.assets.fontSmall, t4);
        game.assets.fontSmall.draw(game.batch, t4, cx - layout.width * 0.5f, y);

        y -= 80f;
        layout.setText(game.assets.fontSmall, t5);
        game.assets.fontSmall.draw(game.batch, t5, cx - layout.width * 0.5f, y);

        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.assets.playButtonSound();
            game.setScreen(new PlayScreen(game));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            game.assets.playButtonSound();
            game.setScreen(new MenuScreen(game));
        }
    }

    @Override
    public void dispose() {
        game.assets.stopAllMusic();
    }
}
