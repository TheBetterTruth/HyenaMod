package io.github.thebettertruth.hyenamod.client.render.entity.feature;

import io.github.thebettertruth.hyenamod.client.render.entity.model.HyenaEntityModel;
import io.github.thebettertruth.hyenamod.entity.passive.HyenaEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class HyenaHeldItemFeatureRenderer extends FeatureRenderer<HyenaEntity, HyenaEntityModel<HyenaEntity>> {
	private final HeldItemRenderer heldItemRenderer;
	public HyenaHeldItemFeatureRenderer(FeatureRendererContext<HyenaEntity, HyenaEntityModel<HyenaEntity>> featureRendererContext, HeldItemRenderer heldItemRenderer){
		super(featureRendererContext);
		this.heldItemRenderer = heldItemRenderer;
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, HyenaEntity hyenaEntity, float f, float g, float h, float i, float j, float k) {
		matrixStack.push();

		// Place the item somewhere around the hyenas head
		matrixStack.translate(this.getContextModel().getHead().pivotX / 16.0F, this.getContextModel().getHead().pivotY / 16.0F, this.getContextModel().getHead().pivotZ / 16.0F);

		// Rotate it with the hyenas head
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(this.getContextModel().getHead().yaw));
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotation(this.getContextModel().getHead().pitch));

		// Move it towards the snouth/mouth
		matrixStack.translate(0.0F, 0.19F, -0.7F);

		// Adjust its rotation so that it looks more like it is held by the hyena
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-75.0F));

		this.heldItemRenderer.renderItem(hyenaEntity, hyenaEntity.getMainHandStack(), ModelTransformationMode.GROUND, false, matrixStack, vertexConsumerProvider, light);
		matrixStack.pop();
	}
}
