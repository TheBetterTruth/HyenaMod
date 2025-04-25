package io.github.thebettertruth.hyenamod.client;

import io.github.thebettertruth.hyenamod.HyenaMod;
import io.github.thebettertruth.hyenamod.client.render.entity.HyenaEntityRenderer;
import io.github.thebettertruth.hyenamod.client.render.entity.model.HyenaEntityModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HyenaModClient implements ClientModInitializer {
	public static final EntityModelLayer HYENA_MODEL_LAYER = new EntityModelLayer(Identifier.of(HyenaMod.MODID, "hyena"), "main");

	public void onInitializeClient() {
		EntityRendererRegistry.register(HyenaMod.HYENA, HyenaEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(HYENA_MODEL_LAYER, HyenaEntityModel::getTexturedModelData);
	}
}
