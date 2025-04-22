package io.github.thebettertruth.hyenamod.client.render.entity.feature;

import io.github.thebettertruth.hyenamod.client.render.entity.model.HyenaEntityModel;
import io.github.thebettertruth.hyenamod.client.render.entity.state.HyenaEntityRenderState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class HyenaHeldItemFeatureRenderer extends FeatureRenderer<HyenaEntityRenderState, HyenaEntityModel> {
	public HyenaHeldItemFeatureRenderer(FeatureRendererContext<HyenaEntityRenderState, HyenaEntityModel> featureRendererContext){
		super(featureRendererContext);
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, HyenaEntityRenderState hyenaEntityRenderState, float limbAngle, float limbDistance) {
		ItemRenderState itemRenderState = hyenaEntityRenderState.itemRenderState;

		if (!itemRenderState.isEmpty()) {
			matrixStack.push();
			matrixStack.translate(this.getContextModel().getHead().originX / 16.0F, this.getContextModel().getHead().originY / 16.0F, this.getContextModel().getHead().originZ / 16.0F);

			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(this.getContextModel().getHead().yaw));
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotation(this.getContextModel().getHead().pitch));

			matrixStack.translate(0.0F, 0.19F, -0.7F);

			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-75.0F));

			itemRenderState.render(matrixStack, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV);
			matrixStack.pop();
		}
	}
}
