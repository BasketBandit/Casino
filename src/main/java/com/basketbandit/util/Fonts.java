package com.basketbandit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class Fonts {
    protected static final Logger log = LoggerFactory.getLogger(Fonts.class);
    public static Font default8;
    public static Font default12;
    public static Font default16;
    public static Font default24;
    public static Font default36;
    public static Font default72;

    static {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, Fonts.class.getResourceAsStream("/assets/font/balatro/balatro.ttf"));
            default8 = font.deriveFont(8.0f);
            default12 = font.deriveFont(12.0f);
            default16 = font.deriveFont(16.0f);
            default24 = font.deriveFont(24.0f);
            default36 = font.deriveFont(36.0f);
            default72 = font.deriveFont(72.0f);
        } catch(Exception ex) {
            log.error(ex.getMessage());
        }
    }

    public static int[] centered(Graphics graphics, String text, Rectangle rectangle, Font font) {
        FontMetrics metrics = graphics.getFontMetrics(font);
        int x = rectangle.x + (rectangle.width - metrics.stringWidth(text)) / 2;
        int y = rectangle.y + ((rectangle.height - metrics.getHeight()) / 2) + metrics.getAscent();
        return new int[]{x,y};
    }
}
