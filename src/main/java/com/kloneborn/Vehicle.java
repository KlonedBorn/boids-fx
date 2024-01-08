package com.kloneborn;

import java.util.Random;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Vehicle {
    protected Point2D location;
    protected Point2D velocity;
    protected Point2D acceleration;
    protected double radius;
    protected double maxForce;
    protected double maxSpeed;
    protected double angle;

    public Vehicle(double x, double y) {
        this.location = new Point2D(x, y);
        this.velocity = new Point2D(0, 0);
        this.acceleration = new Point2D(0, 0);
        this.radius = 3.0;
        this.maxForce = 0.1;
        this.maxSpeed = 4.0;
        this.angle = 0;
    }

    public Vehicle(Point2D location, Point2D velocity, Point2D acceleration, double radius, double maxForce,
            double maxSpeed) {
        this.location = location;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.radius = radius;
        this.maxForce = maxForce;
        this.maxSpeed = maxSpeed;
    }

    public void seek(Point2D target) {
        Point2D desired = target.subtract(location);
        desired = desired.normalize();
        desired = desired.multiply(maxSpeed);
        Point2D steer = desired.subtract(velocity);
        applyForce(steer);
    }

    private double wanderRadius = 50.0;
    private double wanderDistance = 100.0;
    private double wanderAngleChange = 0.1;

    private double wanderAngle = 45.0;

    public Point2D wander() {
        // Update the wander angle with some randomness
        wanderAngle += (Math.random() * wanderAngleChange - wanderAngleChange * 0.5);

        // Calculate the new wander position
        double x = location.getX() + wanderRadius * Math.cos(wanderAngle);
        double y = location.getY() + wanderRadius * Math.sin(wanderAngle);

        return new Point2D(x, y);
    }

    void update() {
        Point2D wanderingForce = wander();
        wanderingForce = wanderingForce.multiply(0.5);
        wanderingForce = limit(wanderingForce, maxForce);
        applyForce(wanderingForce);
        this.velocity = velocity.add(acceleration);
        this.velocity = limit(velocity, maxSpeed);
        this.location = location.add(velocity);
        this.acceleration = acceleration.multiply(0);
        this.angle = Math.atan2(velocity.getY(), velocity.getX()) + Math.PI / 2;
    }

    void draw(GraphicsContext gc) {
        double drawX = location.getX() - radius; // Adjusting for the center of the circle
        double drawY = location.getY() - radius; // Adjusting for the center of the circle
        double diameter = 2 * radius;

        gc.setFill(Color.BLUE); // Set the color of the block dot

        // Draw a block dot at the vehicle's location
        gc.fillRect(drawX, drawY, diameter, diameter);
    };

    public void applyForce(Point2D force) {
        acceleration = acceleration.add(force);
    }

    public Point2D getLocation() {
        return location;
    }

    public void setLocation(Point2D location) {
        this.location = location;
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    public Point2D getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Point2D acceleration) {
        this.acceleration = acceleration;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getMaxForce() {
        return maxForce;
    }

    public void setMaxForce(double maxForce) {
        this.maxForce = maxForce;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public static Point2D limit(Point2D vector, double mag) {
        double currentMagnitude = Math.hypot(vector.getX(), vector.getY());

        if (currentMagnitude > mag) {
            double scaleFactor = mag / currentMagnitude;
            return new Point2D(vector.getX() * scaleFactor, vector.getY() * scaleFactor);
        }
        return vector;
    }
}