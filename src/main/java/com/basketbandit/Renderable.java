package com.basketbandit;

import java.awt.*;

public interface Renderable {
    String name();
    Graphics2D graphics = Renderer.graphics();
}
