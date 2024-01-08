package com.kloneborn;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.kloneborn.BoidUtils.BoidGenerator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class SceneController {

    @FXML
    private Label alignment_out;

    @FXML
    private Slider alignment_sld;

    @FXML
    private TextField boids_count;

    @FXML
    private Label cohesion_out;

    @FXML
    private Slider cohesion_sld;

    @FXML
    private ToggleGroup edgePolicy;

    @FXML
    private ColorPicker fill_picker;

    @FXML
    private Label fps_out;

    @FXML
    private Slider fps_slid;

    @FXML
    private Label max_force_out;

    @FXML
    private Slider max_force_sld;

    @FXML
    private Label max_speed_out;

    @FXML
    private Slider max_speed_sld;

    @FXML
    private Label seperation_out;

    @FXML
    private Slider seperation_sld;

    @FXML
    private ToggleGroup shapePolicy;

    @FXML
    private Label ups_out;

    @FXML
    private Slider ups_sld;

    @FXML
    private Canvas world;

    @FXML
    private CheckBox is_align;

    @FXML
    private CheckBox is_cohere;

    @FXML
    private CheckBox is_seperate;

    private BoidSimulator simulator;

    @FXML
    void setSimulationToPause(ActionEvent event) {

    }

    @FXML
    void setSimulationToPlay(ActionEvent event) {

    }

    @FXML
    void startSimulationWithBoidCount(ActionEvent event) {
        Integer var = Integer.valueOf(boids_count.getText());
        simulator.refresh(var);
    }

    @FXML
    void initialize() {
        ResourceBundle bundle = ResourceBundle.getBundle("com.kloneborn.system");

        // Initialize sliders
        init_slider(fps_slid, Double.parseDouble(bundle.getString("default.slider.fps")), "%.0f", fps_out);
        init_slider(ups_sld, Double.parseDouble(bundle.getString("default.slider.ups")), "%.0f", ups_out);
        init_slider(max_speed_sld, Double.parseDouble(bundle.getString("default.slider.max_speed")), "%.2f",
                max_speed_out);
        init_slider(max_force_sld, Double.parseDouble(bundle.getString("default.slider.max_force")), "%.2f",
                max_force_out);
        init_slider(alignment_sld, Double.parseDouble(bundle.getString("default.slider.alignment_force")), "%.2f",
                alignment_out);
        init_slider(cohesion_sld, Double.parseDouble(bundle.getString("default.slider.cohesion_force")), "%.2f",
                cohesion_out);
        init_slider(seperation_sld, Double.parseDouble(bundle.getString("default.slider.seperation_force")), "%.2f",
                seperation_out);

        // Initialize radio buttons for edge and shape policy
        selectRadioButton(edgePolicy, bundle.getString("default.radio_buttons.edge_policy"));
        selectRadioButton(shapePolicy, bundle.getString("default.radio_buttons.shape_policy"));

        // Initialize color picker with default color
        String defaultColor = bundle.getString("default.color_picker.fill");
        fill_picker.setValue(javafx.scene.paint.Color.web(defaultColor));

        String numberOfBoids = bundle.getString("default.property.boids_count");
        boids_count.setText(numberOfBoids);

        world.setWidth(Double.parseDouble(bundle.getString("default.property.world_width")));
        world.setHeight(Double.parseDouble(bundle.getString("default.property.world_height")));

        simulator = new BoidSimulator(Integer.parseInt(numberOfBoids));
        simulator.framesPerSecondProperty().bindBidirectional(fps_slid.valueProperty());
        simulator.updatesPerSecondProperty().bindBidirectional(ups_sld.valueProperty());
        simulator.start();
    }

    private class BoidSimulator extends SimulatorBase {
        private final List<Boid> boids = new ArrayList<>();
        private double width;
        private double height;
        private GraphicsContext graphics;
        private double randVelocityMagnitude = 10;

        // Initalizer
        public BoidSimulator(int count) {
            this.width = world.getWidth();
            this.height = world.getHeight();
            this.graphics = world.getGraphicsContext2D();
            refresh(count);
            Launcher.getStage().setOnCloseRequest(evt -> setRunning(false));
        }

        private void refresh(int count) {
            boids.clear();
            boids.addAll(BoidGenerator.generateBoids(342521, count, width, height, randVelocityMagnitude));
            // Update velocities for all boids
            for (Boid boid : boids) {
                boid.setVelocity(BoidUtils.randVelocity(randVelocityMagnitude));
                boid.properties.maxSpeedProperty().bindBidirectional(max_speed_sld.valueProperty());
                boid.properties.maxForceProperty().bindBidirectional(max_force_sld.valueProperty());
                boid.properties.alignmentForceProperty().bindBidirectional(alignment_sld.valueProperty());
                boid.properties.cohesionForceProperty().bindBidirectional(cohesion_sld.valueProperty());
                boid.properties.separationForceProperty().bindBidirectional(seperation_sld.valueProperty());
                boid.properties.fillProperty().bind(fill_picker.valueProperty());
            }
        }

        @Override
        protected void update(double dt) {
            for (Boid boid : boids) {
                if (is_align.isSelected())
                    boid.align(boids);
                if (is_cohere.isSelected())
                    boid.cohere(boids);
                if (is_seperate.isSelected())
                    boid.seperate(boids);
                boid.update();
            }
        }

        @Override
        protected void render(double dt) {
            graphics.clearRect(0, 0, width, height);
            for (Boid boid : boids) {
                boid.draw(graphics);
            }
        }
    }

    private static void selectRadioButton(ToggleGroup group, String buttonText) {
        group.getToggles().forEach(toggle -> {
            if (((RadioButton) toggle).getText().equalsIgnoreCase(buttonText)) {
                group.selectToggle(toggle);
            }
        });
    }

    private static final void init_slider(Slider s, double value, String format, Label out) {
        out.textProperty().bind(s.valueProperty().asString(format));
        s.setValue(value);
    }
}