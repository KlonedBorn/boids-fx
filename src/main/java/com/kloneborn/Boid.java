package com.kloneborn;

import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Boid extends Vehicle {

    public final FXProperties properties = new FXProperties();

    public Boid(double x, double y) {
        super(new Point2D(x, y), new Point2D(0, 0), new Point2D(0, 0), 10, 0.1, 4.0);
    }

    @Override
    void update() {
        applyToroid(properties.getWorldWidth(), properties.getWorldHeight());
        super.update();
    }

    @Override
    void draw(GraphicsContext gc) {

        gc.setFill(Color.rgb(175, 175, 175));
        gc.setStroke(Color.BLACK);
        gc.save();
        gc.translate(location.getX(), location.getY());
        gc.rotate(Math.toDegrees(angle));
        double halfR = getRadius() / 2.0;
        gc.beginPath();
        gc.moveTo(0, -2 * halfR);
        gc.lineTo(-halfR, 2 * halfR);
        gc.lineTo(halfR, 2 * halfR);
        gc.closePath();
        gc.fill();
        gc.stroke();
        gc.restore();
    }

    public void applyToroid(double width, double height) {
        if (location.getX() > width)
            this.location = new Point2D(0, location.getY());
        else if (location.getX() < 0)
            this.location = new Point2D(width, location.getY());
        if (location.getY() > height)
            this.location = new Point2D(location.getX(), 0);
        else if (location.getY() < 0)
            this.location = new Point2D(location.getX(), height);
    }

    public void align(List<Boid> flock) {
        double preceptRad = 100.0; // px
        Point2D steering = new Point2D(0, 0);
        int total = 0;
        for (Boid boid : flock) {
            double d = location.distance(boid.location);
            if (boid != this && d <= preceptRad) {
                steering = steering.add(boid.velocity);
                total++;
            }
        }
        if (total > 0) {
            steering = steering.multiply(1.0 / total);
            steering = steering.normalize().multiply(maxSpeed);
            steering = steering.subtract(velocity);
            steering = limit(steering, properties.getAlignmentForce());
        }
        acceleration = acceleration.add(steering);
    }

    public void cohere(List<Boid> flock) {
        double neighborDist = 50.0;
        Point2D sum = new Point2D(0, 0);
        int count = 0;

        for (Boid other : flock) {
            double distance = location.distance(other.location);

            if (other != this && distance > 0 && distance < neighborDist) {
                sum = sum.add(other.location);
                count++;
            }
        }

        if (count > 0) {
            sum = sum.multiply(1.0 / count);
            seek(sum);
        }
    }

    public void seperate(List<Boid> flock) {
        float desiredSeparation = (float) (properties.getSize() * 2);
        Point2D sum = new Point2D(0, 0);
        int count = 0;

        for (Boid other : flock) {
            double distance = location.distance(other.location);
            if (other != this && distance > 0 && distance < desiredSeparation) {
                Point2D diff = location.subtract(other.location);
                diff = diff.normalize();
                sum = sum.add(diff);
                count++;
            }
        }

        if (count > 0) {
            sum = sum.multiply(1.0 / count);
            sum = sum.normalize().multiply(maxSpeed);
            Point2D steer = sum.subtract(velocity);
            steer = limit(steer, maxForce);
            acceleration = acceleration.add(steer.multiply(properties.getSeparationForce()));
        }
    }

    public void flock(List<Boid> neighbors) {
        align(neighbors);
        cohere(neighbors);
        seperate(neighbors);
    }

    // Enum for Edge Policy
    public enum EdgePolicy {
        TOROID(), ISLAND(), VOID;

        public static EdgePolicy fromString(String value) {
            switch (value.toLowerCase()) {
                case "toroid":
                    return TOROID;
                case "island":
                    return ISLAND;
                case "void":
                    return VOID;
                default:
                    throw new IllegalArgumentException("Invalid Edge Policy value: " + value);
            }
        }
    }

    // Enum for Shape Policy
    public enum ShapePolicy {
        DOT, ARROW;

        public static ShapePolicy fromString(String value) {
            switch (value.toLowerCase()) {
                case "dot":
                    return DOT;
                case "arrow":
                    return ARROW;
                default:
                    throw new IllegalArgumentException("Invalid Shape Policy value: " + value);
            }
        }
    }

    public class FXProperties {
        // JavaFX properties for sliders
        private final DoubleProperty maxSpeedProperty = new SimpleDoubleProperty();
        private final DoubleProperty maxForceProperty = new SimpleDoubleProperty();
        private final DoubleProperty alignmentForceProperty = new SimpleDoubleProperty();
        private final DoubleProperty cohesionForceProperty = new SimpleDoubleProperty();
        private final DoubleProperty separationForceProperty = new SimpleDoubleProperty();
        private final DoubleProperty worldWidthProperty = new SimpleDoubleProperty();
        private final DoubleProperty worldHeightProperty = new SimpleDoubleProperty();
        private final DoubleProperty sizeProperty = new SimpleDoubleProperty();
        private final ObjectProperty<GraphicsContext> graphicsProperty = new SimpleObjectProperty<>();

        // JavaFX property for color picker
        private final ObjectProperty<Color> fillProperty = new SimpleObjectProperty<>();

        // JavaFX property for edge policy (as an enum)
        private final ObjectProperty<EdgePolicy> edgePolicyProperty = new SimpleObjectProperty<>();

        // JavaFX property for shape policy (as an enum)
        private final ObjectProperty<ShapePolicy> shapePolicyProperty = new SimpleObjectProperty<>();

        // Constructor
        public FXProperties() {
            ResourceBundle bundle = ResourceBundle.getBundle("com.kloneborn.system");

            // Set values for sliders
            maxSpeedProperty.set(Double.parseDouble(bundle.getString("default.slider.max_speed")));
            maxForceProperty.set(Double.parseDouble(bundle.getString("default.slider.max_force")));
            alignmentForceProperty.set(Double.parseDouble(bundle.getString("default.slider.alignment_force")));
            cohesionForceProperty.set(Double.parseDouble(bundle.getString("default.slider.cohesion_force")));
            separationForceProperty.set(Double.parseDouble(bundle.getString("default.slider.seperation_force")));

            // Set values for radio buttons (edge and shape policy)
            edgePolicyProperty.set(EdgePolicy.fromString(bundle.getString("default.radio_buttons.edge_policy")));
            shapePolicyProperty.set(ShapePolicy.fromString(bundle.getString("default.radio_buttons.shape_policy")));

            // Set value for color picker
            String defaultColor = bundle.getString("default.color_picker.fill");
            fillProperty.set(javafx.scene.paint.Color.web(defaultColor));

            // Set properties
            setWorldWidth(Double.parseDouble(bundle.getString("default.property.world_width")));
            setWorldHeight(Double.parseDouble(bundle.getString("default.property.world_height")));

            setSize(10.0);
            maxSpeedProperty.addListener((obv, old, nvw) -> Boid.this.setMaxSpeed(nvw.doubleValue()));
            maxForceProperty.addListener((obv, old, nvw) -> Boid.this.setMaxForce(nvw.doubleValue()));
        }

        public DoubleProperty sizeProperty() {
            return sizeProperty;
        }

        public void setSize(double size) {
            this.sizeProperty.set(size);
        }

        public double getSize() {
            return this.sizeProperty.get();
        }

        // Getter and setter for maxSpeedProperty
        public DoubleProperty maxSpeedProperty() {
            return maxSpeedProperty;
        }

        public double getMaxSpeed() {
            return maxSpeedProperty.get();
        }

        public void setMaxSpeed(double maxSpeed) {
            maxSpeedProperty.set(maxSpeed);
        }

        // Getter and setter for maxForceProperty
        public DoubleProperty maxForceProperty() {
            return maxForceProperty;
        }

        public double getMaxForce() {
            return maxForceProperty.get();
        }

        public void setMaxForce(double maxForce) {
            maxForceProperty.set(maxForce);
        }

        // Getter and setter for alignmentForceProperty
        public DoubleProperty alignmentForceProperty() {
            return alignmentForceProperty;
        }

        public double getAlignmentForce() {
            return alignmentForceProperty.get();
        }

        public void setAlignmentForce(double alignmentForce) {
            alignmentForceProperty.set(alignmentForce);
        }

        // Getter and setter for cohesionForceProperty
        public DoubleProperty cohesionForceProperty() {
            return cohesionForceProperty;
        }

        public double getCohesionForce() {
            return cohesionForceProperty.get();
        }

        public void setCohesionForce(double cohesionForce) {
            cohesionForceProperty.set(cohesionForce);
        }

        // Getter and setter for separationForceProperty
        public DoubleProperty separationForceProperty() {
            return separationForceProperty;
        }

        public double getSeparationForce() {
            return separationForceProperty.get();
        }

        public void setSeparationForce(double separationForce) {
            separationForceProperty.set(separationForce);
        }

        // Getter and setter for fillProperty
        public ObjectProperty<Color> fillProperty() {
            return fillProperty;
        }

        public Color getFill() {
            return fillProperty.get();
        }

        public void setFill(Color fill) {
            fillProperty.set(fill);
        }

        // Getter and setter for edgePolicyProperty
        public ObjectProperty<EdgePolicy> edgePolicyProperty() {
            return edgePolicyProperty;
        }

        public EdgePolicy getEdgePolicy() {
            return edgePolicyProperty.get();
        }

        public void setEdgePolicy(EdgePolicy edgePolicy) {
            edgePolicyProperty.set(edgePolicy);
        }

        // Getter and setter for shapePolicyProperty
        public ObjectProperty<ShapePolicy> shapePolicyProperty() {
            return shapePolicyProperty;
        }

        public ShapePolicy getShapePolicy() {
            return shapePolicyProperty.get();
        }

        public void setShapePolicy(ShapePolicy shapePolicy) {
            shapePolicyProperty.set(shapePolicy);
        }

        // Getter for worldWidthProperty
        public double getWorldWidth() {
            return worldWidthProperty.get();
        }

        // Setter for worldWidthProperty
        public void setWorldWidth(double worldWidth) {
            this.worldWidthProperty.set(worldWidth);
        }

        // Accessor for worldWidthProperty
        public DoubleProperty worldWidthProperty() {
            return worldWidthProperty;
        }

        // Getter for worldHeightProperty
        public double getWorldHeight() {
            return worldHeightProperty.get();
        }

        // Setter for worldHeightProperty
        public void setWorldHeight(double worldHeight) {
            this.worldHeightProperty.set(worldHeight);
        }

        // Accessor for worldHeightProperty
        public DoubleProperty worldHeightProperty() {
            return worldHeightProperty;
        }

        // Getter for graphicsProperty
        public GraphicsContext getGraphics() {
            return graphicsProperty.get();
        }

        // Setter for graphicsProperty
        public void setGraphics(GraphicsContext graphics) {
            this.graphicsProperty.set(graphics);
        }

        // Accessor for graphicsProperty
        public ObjectProperty<GraphicsContext> graphicsProperty() {
            return graphicsProperty;
        }
    }
}