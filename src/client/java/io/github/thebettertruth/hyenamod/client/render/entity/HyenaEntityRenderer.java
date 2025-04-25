package io.github.thebettertruth.hyenamod.client.render.entity;

import io.github.thebettertruth.hyenamod.HyenaMod;
import io.github.thebettertruth.hyenamod.client.HyenaModClient;
import io.github.thebettertruth.hyenamod.client.render.entity.feature.HyenaHeldItemFeatureRenderer;
import io.github.thebettertruth.hyenamod.client.render.entity.model.HyenaEntityModel;
import io.github.thebettertruth.hyenamod.entity.passive.HyenaEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

@SuppressWarnings("deprecation")
@Environment(EnvType.CLIENT)
public class HyenaEntityRenderer extends MobEntityRenderer<HyenaEntity, HyenaEntityModel<HyenaEntity>> {
	private static final Identifier DEFAULT_TEXTURE = Identifier.of(HyenaMod.MODID, "textures/entity/hyena/hyena_spotted.png");

	public HyenaEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new HyenaEntityModel<>(context.getPart(HyenaModClient.HYENA_MODEL_LAYER)), 0.5F);
		// Allow us to render the item the hyena's holding
		this.addFeature(new HyenaHeldItemFeatureRenderer(this, context.getHeldItemRenderer()));
	}

	@Override
	public Identifier getTexture(HyenaEntity hyenaEntity) {
		return DEFAULT_TEXTURE;
	}
}
