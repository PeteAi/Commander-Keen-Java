package commanderKeen.entitiy.mob;

import aagrueme.com.github.api.Animation;
import aagrueme.com.github.api.Spritesheet;
import commanderKeen.levels.Level;
import commanderKeen.util.IHasRenderer;
import commanderKeen.util.IHasUpdater;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public abstract class Keen implements IHasUpdater, IHasRenderer {
    protected static final int RIGHT = 0;
    protected static final int UP = 1;
    protected static final int LEFT = 2;
    protected static final int DOWN = 3;
    public double camToY;
    public double camToX;

    public int right = 0;
    public int left = 0;
    public int jump = 0;
    public boolean falling = false;
    protected final float GRAVITY = 0.2F;
    protected final float MAX_FALLING_SPEED = 4.5F;

    protected double x;
    protected double y;
    protected Animation animation;
    protected Spritesheet idleSprite;

    protected double dx;
    protected double dy;

    protected Level level;
    protected float speed = 0.75f;
    protected int idle = LEFT;

    protected BufferedImage texture;

    public Keen(Level level, double x, double y, Animation animation, Spritesheet idleSprite){
        this.level = level;
        this.x = x;
        this.y = y;
        this.animation = animation;
        this.idleSprite = idleSprite;
        this.animation.startAnimation();
        this.texture = animation.getImage();
    }

    @Override
    public void render(Graphics2D g2d) {
        g2d.setTransform(AffineTransform.getTranslateInstance(x, y));
        g2d.drawImage(texture, 0, 0, null);
        g2d.setTransform(AffineTransform.getTranslateInstance(0, 0));
    }

    protected void move() {
        x += dx;
        y += dy;
        dx = 0;
    }

    protected void calculateMovement() {
        if(left > 0) dx = -speed;
        if(right > 0) dx = speed;

        if(falling && jump == 0) {
            dy += GRAVITY;
            if(dy > MAX_FALLING_SPEED) dy = MAX_FALLING_SPEED;
        }

        if(jump > 0 && !falling) {
            dy = jump;
            jump = 0;
            falling = true;
        }
    }
    
    protected void calculateAnimation(){
        animation.update();
        if(dx > 0 && animation.getCurrentState() != LEFT) {
            animation.setState(LEFT);
            idle = LEFT;
            texture = animation.getImage();
        } else if(dx < 0 && animation.getCurrentState() != RIGHT) {
            animation.setState(RIGHT);
            idle = RIGHT;
            texture = animation.getImage();
        }else if(dx == 0) {
            texture = idleSprite.getImage(idle, 0);
        }
    }

    @Override
    public void update() {
        calculateMovement();
        collision();
        calculateAnimation();
        move();
    }

    protected void collision() {
        boolean accepted = false;
        if(dx > 0) {
            for (int i = 0; accepted; i++) {
                dy -= i;
                if (level.level[(int) (x + dx) / 16][(int) y / 16].getBlock().testCollision()) {
                    right = 0;
                    left = 0;
                    accepted = true;
                }
            }
        }else if(dx < 0) {
            for (int i = 0; accepted; i++) {
                dy += i;
                if (level.level[(int) (x + dx) / 16][(int) y / 16].getBlock().testCollision()) {
                    right = 0;
                    left = 0;
                    accepted = true;
                }
            }
        }
        accepted = false;
        if(dy > 0) {
            for (int i = 0; accepted; i++) {
                dy -= i;
                if (level.level[(int) x / 16][(int) (y + dy) / 16].getBlock().testCollision()) {
                    accepted = true;
                }
            }
        }else if(dy < 0) {
            for (int i = 0; accepted; i++) {
                dy += i;
                if (level.level[(int) x / 16][(int) (y + dy) / 16].getBlock().testCollision()) {
                    accepted = true;
                }
            }
        }
    }
}
