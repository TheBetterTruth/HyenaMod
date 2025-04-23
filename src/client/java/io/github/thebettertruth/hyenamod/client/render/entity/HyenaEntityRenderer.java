package io.github.thebettertruth.hyenamod.client.render.entity;

import io.github.thebettertruth.hyenamod.HyenaMod;
import io.github.thebettertruth.hyenamod.client.HyenaModClient;
import io.github.thebettertruth.hyenamod.client.render.entity.feature.HyenaHeldItemFeatureRenderer;
import io.github.thebettertruth.hyenamod.client.render.entity.model.HyenaEntityModel;
import io.github.thebettertruth.hyenamod.client.render.entity.state.HyenaEntityRenderState;
import io.github.thebettertruth.hyenamod.entity.passive.HyenaEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.util.Identifier;

@SuppressWarnings("deprecation")
@Environment(EnvType.CLIENT)
public class HyenaEntityRenderer extends AgeableMobEntityRenderer<HyenaEntity, HyenaEntityRenderState, HyenaEntityModel> {

	private static final Identifier DEFAULT_TEXTURE = Identifier.of(HyenaMod.MODID, "textures/entity/hyena/hyena_spotted.png");

	public HyenaEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new HyenaEntityModel(context.getPart(HyenaModClient.HYENA_MODEL_LAYER)), new HyenaEntityModel(context.getPart(HyenaModClient.HYENA_BABY_MODEL_LAYER)), 0.5F);
		// Allow us to render the item the hyena's holding
		this.addFeature(new HyenaHeldItemFeatureRenderer(this));
	}

	@Override
	public HyenaEntityRenderState createRenderState() {
		return new HyenaEntityRenderState();
	}

	@Override
	public Identifier getTexture(HyenaEntityRenderState hyenaEntityRenderState) {
		return DEFAULT_TEXTURE;
	}

	@Override
	public void updateRenderState(HyenaEntity hyenaEntity, HyenaEntityRenderState hyenaEntityRenderState, float f) {
		super.updateRenderState(hyenaEntity, hyenaEntityRenderState, f);
		// Do the item-rendering stuff
		ItemHolderEntityRenderState.update(hyenaEntity, hyenaEntityRenderState, this.itemModelResolver);
		hyenaEntityRenderState.tailAngle = hyenaEntity.getTailAngle();
	}
}
