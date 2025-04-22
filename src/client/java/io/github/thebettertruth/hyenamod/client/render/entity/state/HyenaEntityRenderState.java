package io.github.thebettertruth.hyenamod.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;

@Environment(EnvType.CLIENT)
public class HyenaEntityRenderState extends ItemHolderEntityRenderState {
	public float tailAngle = (float) (Math.PI / 5.0F);
}
