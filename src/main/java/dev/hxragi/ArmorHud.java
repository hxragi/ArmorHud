package dev.hxragi;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class ArmorHud implements ClientModInitializer {

	private ArmorHudRenderer renderer;

	@Override
	public void onInitializeClient() {
		renderer = new ArmorHudRenderer();

		HudRenderCallback.EVENT.register((graphics, tickDelta) -> renderer.render(graphics));
	}
}