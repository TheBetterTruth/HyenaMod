package io.github.thebettertruth.hyenamod.client.render.entity.model;// Made with Blockbench 4.12.4

import com.google.common.collect.ImmutableList;
import io.github.thebettertruth.hyenamod.entity.passive.HyenaEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class HyenaEntityModel<T extends HyenaEntity> extends AnimalModel<T> {
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart tail;

	public HyenaEntityModel(ModelPart root) {
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.rightHindLeg = root.getChild("right_hind_leg");
		this.leftHindLeg = root.getChild("left_hind_leg");
		this.rightFrontLeg = root.getChild("right_front_leg");
		this.leftFrontLeg = root.getChild("left_front_leg");
		this.tail = root.getChild("tail");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();
		root.addChild(EntityModelPartNames.HEAD,
				ModelPartBuilder.create()
						.uv(0, 0)
						.cuboid(-3.0F, -2.8F, -9.0F, 6.0F, 6.0F, 6.0F)

						.uv(20, 15)
						.cuboid(EntityModelPartNames.NECK, -3.5F, -2.8F, -3.0F, 7.0F, 6.0F, 2.0F)

						.uv(2, 12)
						.cuboid(EntityModelPartNames.NOSE, -1.5F, 0.1F, -12.0F, 3.0F, 3.0F, 4.0F)

						.uv(26, 8)
						.cuboid(EntityModelPartNames.RIGHT_EAR, -3.75F, -5.15F, -6.0F, 2.0F, 3.0F, 1.0F)

						.uv(26, 8)
						.cuboid(EntityModelPartNames.LEFT_EAR, 1.75F, -5.15F, -6.0F, 2.0F, 3.0F, 1.0F),
				ModelTransform.pivot(0.0F, 12.0F, -4.0F));

		root.addChild(EntityModelPartNames.BODY,
				ModelPartBuilder.create()
						.uv(34, 0)
						.cuboid(-4.0F, -2.75F, -0.5F, 8.0F, 7.0F, 7.0F)

						.uv(34, 18)
						.cuboid("upper_body", -4.5F, -3.75F, -6.5F, 9.0F, 8.0F, 6.0F),
				ModelTransform.pivot(0.0F, 13.75F, 0.5F));

		// All legs are equal
		ModelPartBuilder legBuilder = ModelPartBuilder.create()
				.uv(0, 22)
				.cuboid(-1.5F, 1.0F, -1.5F, 3.0F, 7.0F, 3.0F);

		root.addChild(EntityModelPartNames.RIGHT_HIND_LEG, legBuilder, ModelTransform.pivot(-2.0F, 16.0F, 4.0F));
		root.addChild(EntityModelPartNames.LEFT_HIND_LEG, legBuilder, ModelTransform.pivot(2.0F, 16.0F, 4.0F));
		root.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, legBuilder, ModelTransform.pivot(-2.0F, 16.0F, -4.0F));
		root.addChild(EntityModelPartNames.LEFT_FRONT_LEG, legBuilder, ModelTransform.pivot(2.0F, 16.0F, -4.0F));

		root.addChild(EntityModelPartNames.TAIL,
				ModelPartBuilder.create()
						.uv(13, 22)
						.cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 8.0F, 2.0F),
				ModelTransform.pivot(0.0F, 12.0F, 7.0F));

		return TexturedModelData.of(modelData, 64, 32);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.tail);
	}

	@Override
	public void animateModel(T entity, float animationProgress, float amplitude, float tickDelta) {
		this.rightHindLeg.pitch = MathHelper.cos(animationProgress * 0.6662F) * 1.4F * amplitude;
		this.leftHindLeg.pitch = MathHelper.cos(animationProgress * 0.6662F + (float) Math.PI) * 1.4F * amplitude;
		this.rightFrontLeg.pitch = MathHelper.cos(animationProgress * 0.6662F + (float) Math.PI) * 1.4F * amplitude;
		this.leftFrontLeg.pitch = MathHelper.cos(animationProgress * 0.6662F) * 1.4F * amplitude;
		this.tail.yaw = MathHelper.cos(animationProgress * 0.6662F) * 1.4F * amplitude;
	}

	@Override
	public void setAngles(T hyenaEntity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		// Mojangs way of calculating leg and tail motion when an entity is walking. Somewhat
		this.tail.pitch = hyenaEntity.getTailAngle();

		this.head.pitch = headPitch * (float) (Math.PI / 180.0F);
		this.head.yaw = headYaw * (float) (Math.PI / 180.0F);
	}

	public ModelPart getHead() {
		return head;
	}

	public ModelPart getBody() {
		return body;
	}
}