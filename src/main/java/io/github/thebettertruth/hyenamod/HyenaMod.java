package io.github.thebettertruth.hyenamod;

import io.github.thebettertruth.hyenamod.entity.passive.HyenaEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import org.jetbrains.annotations.NotNull;

public class HyenaMod implements ModInitializer {
	public static final String MODID = "hyenamod";

	public static final EntityType<HyenaEntity> HYENA = Registry.register(
			Registries.ENTITY_TYPE, Identifier.of(MODID, "hyena"),
			FabricEntityType.Builder.createMob(HyenaEntity::new, SpawnGroup.CREATURE, builder -> builder.spawnRestriction(SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HyenaEntity::canSpawn))
					.eyeHeight(0.8F)
					.maxTrackingRange(32)
					.dimensions(0.6F, 0.9F)
					.build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MODID, "hyena"))));
	public static final SoundEvent HYENA_LAUGH_SOUND = Registry.register(Registries.SOUND_EVENT, Identifier.of(MODID, "hyena_laugh"),
			SoundEvent.of(Identifier.of(MODID, "hyena_laugh")));
	public static final SoundEvent HYENA_HURT_SOUND = Registry.register(Registries.SOUND_EVENT, Identifier.of(MODID, "hyena_hurt"),
			SoundEvent.of(Identifier.of(MODID, "hyena_hurt")));
	public static final SoundEvent HYENA_DEATH_SOUND = Registry.register(Registries.SOUND_EVENT, Identifier.of(MODID, "hyena_death"),
			SoundEvent.of(Identifier.of(MODID, "hyena_death")));
	public static final SoundEvent HYENA_STEP_SOUND = Registry.register(Registries.SOUND_EVENT, Identifier.of(MODID, "hyena_step"),
			SoundEvent.of(Identifier.of(MODID, "hyena_step")));

	@Override
	public void onInitialize() {
		FabricDefaultAttributeRegistry.register(HYENA, HyenaEntity.createHyenaAttributes());
		BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_SAVANNA),
				SpawnGroup.CREATURE,
				HYENA,
				53,
				4,
				8);
	}
}
