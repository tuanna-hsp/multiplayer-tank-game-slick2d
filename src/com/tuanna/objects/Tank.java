package com.tuanna.objects;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.particles.effects.FireEmitter;

public class Tank extends DynamicObject {

    private static final float DEFAULT_HANDLING = 4;
    private static final float MAX_VELOCITY = 0.3f;
    private static final float DEFAULT_ACCELERATION = 0.015f;
    private static final float BRAKE_MODIFIER = 0.03f;
    private static final long RELOAD_INTERVAL = 2000;  // 2 sec

    private ParticleSystem particleSystem_;
    private Image bulletImage_;
    private int id_;
    private long lastShootTime_;
    private float lastDrawX_;
    private float lastDrawY_;

    public Tank(String carImage, String smokeImage, float centerX, float centerY) throws SlickException {
        super(carImage, centerX, centerY);

        friction_ = 0.0003f;
        particleSystem_ = new ParticleSystem(new Image(smokeImage));
        particleSystem_.addEmitter(new FireEmitter(0, 0, 10));
        id_ = (int) System.currentTimeMillis();
    }

    public void accelerate() {
        float newVelocity = velocity_ + DEFAULT_ACCELERATION;
        velocity_ = newVelocity < MAX_VELOCITY ? newVelocity : MAX_VELOCITY;
    }

    public void brake() {
        float newVelocity = velocity_ - BRAKE_MODIFIER;
        velocity_ = newVelocity > -MAX_VELOCITY ? newVelocity : -MAX_VELOCITY;
    }

    public void turnLeft() {
        rotation_ += DEFAULT_HANDLING * (velocity_ / MAX_VELOCITY);
    }

    public void turnRight() {
        rotation_ -= DEFAULT_HANDLING * (velocity_ / MAX_VELOCITY);
    }

    @Override
    public void update(int deltaT) {
        super.update(deltaT);
        particleSystem_.update(deltaT);
    }

    @Override
    public void draw(float xPos, float yPos) {
        lastDrawX_ = xPos;
        lastDrawY_ = yPos;
        super.draw(xPos, yPos);
    }

    public int getId() {
        return id_;
    }

    public Bullet fire() throws SlickException {
        long interval = liveTime_ - lastShootTime_;
        if (interval < RELOAD_INTERVAL || bulletImage_ == null) {
            // Can't fire now, wait reloading
            return null;
        } else {
            lastShootTime_ = liveTime_;
            Bullet bullet = new Bullet(
                    bulletImage_.copy(),
                    lastDrawX_,
                    lastDrawY_,
                    rotation_,
                    id_
            );
            bullet.setBoundDrawEnable(false);
            return bullet;
        }
    }

    public void setBulletImage(Image bulletImage) {
        bulletImage_ = bulletImage;
    }
}