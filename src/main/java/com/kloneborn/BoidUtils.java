package com.kloneborn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.geometry.Point2D;

public class BoidUtils {
    /**
     * Limits the magnitude of a Point2D to a specified maximum value.
     *
     * @param vector   The original Point2D vector.
     * @param maxValue The maximum magnitude allowed.
     * @return A new Point2D with a limited magnitude.
     */
    public static Point2D limit(Point2D vector, double maxValue) {
        double currentMagnitude = vector.magnitude();

        if (currentMagnitude > maxValue) {
            double scaleFactor = maxValue / currentMagnitude;
            return new Point2D(vector.getX() * scaleFactor, vector.getY() * scaleFactor);
        }

        return vector; // No need to limit if within the range
    }

    public static class BoidGenerator {

        public static List<Boid> generateBoids(int seed, int numberOfBoids, double worldWidth, double worldHeight,
                double randomVelocityMagnitude) {
            List<Boid> boids = new ArrayList<>();
            Random random = new Random(seed);

            for (int i = 0; i < numberOfBoids; i++) {
                double x = random.nextDouble() * worldWidth;
                double y = random.nextDouble() * worldHeight;
                Point2D position = new Point2D(x, y);

                // Generate random velocity with the specified magnitude
                Point2D velocity = generateRandomVelocity(randomVelocityMagnitude);

                // Create and add a new Boid to the list
                Boid boid = new Boid(position.getX(),position.getY());
                boid.setVelocity(velocity);
                boids.add(boid);
            }

            return boids;
        }

        private static Point2D generateRandomVelocity(double magnitude) {
            Random random = new Random();
            double angle = random.nextDouble() * 2 * Math.PI; // Random angle in radians
            double x = magnitude * Math.cos(angle);
            double y = magnitude * Math.sin(angle);
            return new Point2D(x, y);
        }
    }

    public static Point2D randVelocity(double magnitude) {
        Random random = new Random();
        double angle = random.nextDouble() * 2 * Math.PI; // Random angle in radians
        double x = magnitude * Math.cos(angle);
        double y = magnitude * Math.sin(angle);
        return new Point2D(x, y);
    }
}
