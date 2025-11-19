package com.basketbandit;

import com.basketbandit.io.Keyboard;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Map;

public class Renderer {
    private static final Map<RenderingHints.Key, Object> quality = Map.ofEntries(
        Map.entry(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON),
        Map.entry(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE),
        Map.entry(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY),
        Map.entry(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR),
        Map.entry(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY),
        Map.entry(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON),
        Map.entry(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON),
        Map.entry(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY),
        Map.entry(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE)
    );
    private static final Map<RenderingHints.Key, Object> speed = Map.ofEntries(
        Map.entry(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON),
        Map.entry(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE),
        Map.entry(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED),
        Map.entry(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR),
        Map.entry(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED),
        Map.entry(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON),
        Map.entry(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON),
        Map.entry(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED),
        Map.entry(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE)
    );
    private static final Map<String, Map<RenderingHints.Key, Object>> renderQualities = Map.ofEntries(
        Map.entry("quality", quality),
        Map.entry("speed", speed)
    );
    private static Map<RenderingHints.Key, Object> selectedQuality = quality;
    private static final GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    private static BufferStrategy bufferStrategy;
    private static final Canvas canvas = new Canvas(graphicsConfiguration);
    static {
        canvas.setSize(new Dimension(1280, 720));
        canvas.setBounds(0, 0,1280, 720);
        canvas.setBackground(Color.BLACK);
        canvas.setIgnoreRepaint(true);
        canvas.addKeyListener(new Keyboard());
        canvas.setVisible(true);
    }
    private static Graphics2D graphics;

    public static Canvas canvas() {
        return canvas;
    }

    public static int windowWidth() {
        return width();
    }

    public static int windowHeight() {
        return height();
    }

    // width of the unscaled base game
    public static int width() {
        return 1280;
    }

    // height of the unscaled base game
    public static int height() {
        return 720;
    }

    public static double widthRatio() {
        return (double) windowWidth() / width();
    }

    public static double heightRatio() {
        return (double) windowHeight() / height();
    }

    public static void setHints(String setting) {
        selectedQuality = renderQualities.getOrDefault(setting, speed);
    }

    public static Graphics2D graphics() {
        if(graphics == null) {
            canvas.createBufferStrategy(3);
            bufferStrategy = canvas.getBufferStrategy();
            graphics = (Graphics2D) bufferStrategy.getDrawGraphics();
            graphics.setRenderingHints(selectedQuality);
            graphics.scale(widthRatio(), heightRatio());
        }
        return graphics;
    }

    public static void render() {
        if(!bufferStrategy.contentsLost()) {
            bufferStrategy.show();
        }
    }
}
