package net.lemon.planetearth.util;

import net.lemon.planetearth.entity.render.SnakeRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class PlanetEarthRenderInit {
    public static void init() {
        //put render initializers here
        EntityRenderers.register(ModEntities.OCELLATED_PAMPAS_SNAKE.get(), SnakeRenderer::new);
    }
}
