package br.com.pixelracer.config;

public class Config {
    public static final int WORLD_W = 420;
    public static final int WORLD_H = 680;

    public static final float ROAD_MARGIN_X = 66f;
    public static final float ROAD_X_MIN = ROAD_MARGIN_X;
    public static final float ROAD_X_MAX = WORLD_W - ROAD_MARGIN_X;

    public static final float BG_R = 0.07f;
    public static final float BG_G = 0.07f;
    public static final float BG_B = 0.09f;
    public static final float BG_A = 1f;

    public static final int   LANE_COUNT = 3;
    public static final float LANE_WIDTH = (ROAD_X_MAX - ROAD_X_MIN) / LANE_COUNT;
    public static final float PLAYER_Y   = 100f;

    public static float laneCenterX(int lane) { return ROAD_X_MIN + LANE_WIDTH * (lane + 0.5f); }

    public static final float SCROLL_GAIN = 1.2f;
    public static final float SCROLL_MIN  = 40f;

    public static final float SHAKE_MIN_SPEED  = 120f;
    public static final float SHAKE_MAX_PIXELS = 2.0f;

    public static final float SPAWN_BASE_SEC = 2.2f;
    public static final float SPAWN_MIN_SEC  = 1.0f;
    public static final float LANE_COOLDOWN_FACTOR = 0.8f;
    public static final float SPAWN_JITTER = 0.25f;

    public static final float HITBOX_SHRINK = 6f;
}
