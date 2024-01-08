package com.kloneborn;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * The {@code SimulationBase} class provides a framework for implementing a
 * Fixed Time Step with Interpolation Game Loop.
 * This game loop ensures a consistent experience across different systems by
 * updating and rendering the game at fixed time intervals.
 * The implementation uses elapsed time between updates to calculate the
 * progression of the game state, providing smoother gameplay.
 * <p>
 * <b>Usage:</b> Extend this class and implement the {@code init},
 * {@code update}, and {@code render} methods to customize the game logic.
 * The loop will automatically call these methods at fixed time intervals.
 * </p>
 * <p>
 * <b>Game Loop Type:</b> Fixed Time Step with Interpolation
 * </p>
 * <p>
 * <b>Pros:</b>
 * <ul>
 * <li>Consistent gameplay experience across different hardware and frame
 * rates.</li>
 * <li>Smooths out visual appearance through interpolation.</li>
 * </ul>
 * </p>
 * <p>
 * <b>Cons:</b>
 * <ul>
 * <li>Potential for wasted computation when the system can handle more updates
 * or renders per second than specified.</li>
 * </ul>
 * </p>
 * <p>
 * <b>Limitations:</b>
 * <ul>
 * <li>May introduce input lag due to fixed time steps.</li>
 * <li>Performance can suffer on systems unable to meet the specified update and
 * render intervals.</li>
 * </ul>
 * </p>
 * <p>
 * <b>Instructions:</b>
 * <ul>
 * <li>Extend this class and implement the {@code init}, {@code update}, and
 * {@code render} methods.</li>
 * <li>Override the {@code init} method to perform any necessary
 * initialization.</li>
 * <li>Override the {@code update} method to handle game logic and state
 * updates.</li>
 * <li>Override the {@code render} method to handle rendering.</li>
 * </ul>
 * </p>
 *
 * @author Kyle M. King
 * @version 1.0
 */
public abstract class SimulatorBase {
    private static final int DEFAULT_FPS = 60;
    private static final int DEFAULT_UPS = 60;
    private final BooleanProperty runningProperty = new SimpleBooleanProperty(this, "running", true);
    private final BooleanProperty pausedProperty = new SimpleBooleanProperty(this, "paused", false);
    private final DoubleProperty framesPerSecondProperty = new SimpleDoubleProperty(this, "frames-per-second",
            DEFAULT_FPS);
    private final DoubleProperty updatesPerSecondProperty = new SimpleDoubleProperty(this, "updates-per-second",
            DEFAULT_UPS);
    private DoubleProperty timePerFrameProperty = new SimpleDoubleProperty(this, "time-per-frame", 1000 / DEFAULT_FPS);
    private DoubleProperty timePerUpdateProperty = new SimpleDoubleProperty(this, "time-per-update",
            1000 / DEFAULT_UPS);
    private final Thread simulationLoopThread;

    public SimulatorBase() {
        this.simulationLoopThread = new Thread(new SimulationGameLoop());
        timePerFrameProperty.bind(Bindings.createDoubleBinding(
                () -> 1000 / framesPerSecondProperty.get(),
                framesPerSecondProperty));
        timePerUpdateProperty.bind(Bindings.createDoubleBinding(
                () -> 1000 / updatesPerSecondProperty.get(),
                updatesPerSecondProperty));
    }

    protected abstract void update(double dt);

    protected abstract void render(double dt);

    /**
     * Start the simulation loop.
     */
    public void start() {
        if (!isPaused()) {
            simulationLoopThread.start();
        } else {
            setPaused(false);
        }
    }

    /**
     * Pause the simulation loop.
     */
    public void pause() {
        setPaused(true);
    }

    /**
     * Stop the simulation loop.
     */
    public void stop() {
        setRunning(false);
        setPaused(false);
    }

    /**
     * Get the value of the running property.
     *
     * @return {@code true} if running, {@code false} otherwise.
     */
    public boolean isRunning() {
        return runningProperty.get();
    }

    /**
     * Set the value of the running property.
     *
     * @param running The new value for the running property.
     */
    public void setRunning(boolean running) {
        runningProperty.set(running);
    }

    /**
     * Get the value of the paused property.
     *
     * @return {@code true} if paused, {@code false} otherwise.
     */
    public boolean isPaused() {
        return pausedProperty.get();
    }

    /**
     * Set the value of the paused property.
     *
     * @param paused The new value for the paused property.
     */
    public void setPaused(boolean paused) {
        pausedProperty.set(paused);
    }

    /**
     * Get the value of the frames per second (FPS) property.
     *
     * @return The frames per second.
     */
    public double getFramesPerSecond() {
        return framesPerSecondProperty.get();
    }

    /**
     * Set the value of the frames per second (FPS) property.
     *
     * @param fps The new frames per second.
     */
    public void setFramesPerSecond(int fps) {
        framesPerSecondProperty.set(fps);
        timePerFrameProperty.set(1000.0 / fps);
    }

    /**
     * Get the value of the updates per second (UPS) property.
     *
     * @return The updates per second.
     */
    public double getUpdatesPerSecond() {
        return updatesPerSecondProperty.get();
    }

    /**
     * Set the value of the updates per second (UPS) property.
     *
     * @param ups The new updates per second.
     */
    public void setUpdatesPerSecond(int ups) {
        updatesPerSecondProperty.set(ups);
        timePerUpdateProperty.set(1000.0 / ups);
    }

    /**
     * Get the value of the time per frame property.
     *
     * @return The time per frame in milliseconds.
     */
    public double getTimePerFrame() {
        return timePerFrameProperty.get();
    }

    /**
     * Set the value of the time per frame property.
     *
     * @param timePerFrame The new time per frame in milliseconds.
     */
    public void setTimePerFrame(double timePerFrame) {
        timePerFrameProperty.set(timePerFrame);
    }

    /**
     * Get the value of the time per update property.
     *
     * @return The time per update in milliseconds.
     */
    public double getTimePerUpdate() {
        return timePerUpdateProperty.get();
    }

    /**
     * Set the value of the time per update property.
     *
     * @param timePerUpdate The new time per update in milliseconds.
     */
    public void setTimePerUpdate(double timePerUpdate) {
        timePerUpdateProperty.set(timePerUpdate);
    }

    public DoubleProperty framesPerSecondProperty() {
        return framesPerSecondProperty;
    }

    public DoubleProperty updatesPerSecondProperty() {
        return updatesPerSecondProperty;
    }

    private class SimulationGameLoop implements Runnable {
        public void run() {
            long lastUpdateTime = System.nanoTime();
            long lastRenderTime = System.nanoTime();
            double nsPerUpdate = 1_000_000_000.0 / updatesPerSecondProperty.get();
            double nsPerRender = 1_000_000_000.0 / framesPerSecondProperty.get();
            double deltaUpdate = 0;
            double deltaRender = 0;

            while (isRunning()) {
                long now = System.nanoTime();
                long updateTimeDiff = now - lastUpdateTime;
                long renderTimeDiff = now - lastRenderTime;

                deltaUpdate += updateTimeDiff / nsPerUpdate;
                deltaRender += renderTimeDiff / nsPerRender;

                lastUpdateTime = now;
                lastRenderTime = now;

                while (deltaUpdate >= 1) {
                    update(timePerUpdateProperty.get());
                    deltaUpdate -= 1;
                }

                while (deltaRender >= 1) {
                    render(timePerFrameProperty.get());
                    deltaRender -= 1;
                }

                // Optional: Add a sleep to control the loop speed
                // This helps in preventing high CPU usage
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}