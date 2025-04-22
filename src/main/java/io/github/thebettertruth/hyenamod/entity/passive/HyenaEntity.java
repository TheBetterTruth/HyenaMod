package io.github.thebettertruth.hyenamod.entity.passive;

import io.github.thebettertruth.hyenamod.HyenaMod;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HyenaEntity extends AnimalEntity implements Angerable {
	public static final int EATING_TIME = 20;
	public static final int SHOULD_EAT_TIMER_BASE = 600;

	private static final TrackedData<Integer> ANGER_TIME = DataTracker.registerData(HyenaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(30, 40);
	private static final List<Item> PREFERRED_ITEMS = Arrays.asList(Items.BEEF, Items.MUTTON, Items.PORKCHOP, Items.CHICKEN, Items.RABBIT, Items.COOKED_BEEF, Items.COOKED_MUTTON, Items.COOKED_PORKCHOP, Items.COOKED_RABBIT, Items.ROTTEN_FLESH);


	private float shouldEatTimer;
	private float eatingTime;

	@Nullable
	private UUID angryAt;

	public HyenaEntity(EntityType<? extends HyenaEntity> entityType, World world) {
		super(entityType, world);
		this.setCanPickUpLoot(true);
		this.setDropGuaranteed(EquipmentSlot.MAINHAND);
		this.getNavigation().setMaxFollowRange(32.0F);
		this.resetEatingTimer();
	}

	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		return super.initialize(world, difficulty, spawnReason, entityData);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(ANGER_TIME, 0);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(2, new FleeEntityGoal<>(this, LlamaEntity.class, 24.0F, 1.5D, 1.5D));
		this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.4F));
		this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.add(5, new PickupItemGoal());
		this.goalSelector.add(6, new EatItemGoal());
		this.goalSelector.add(7, new FollowParentGoal(this, 1.25D));
		this.goalSelector.add(8, new GoToVillageGoal(300));
		this.goalSelector.add(9, new WanderAroundFarGoal(this, 1.0D));
		this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(11, new LookAroundGoal(this));
		this.targetSelector.add(1, (new RevengeGoal(this)).setGroupRevenge());
		this.targetSelector.add(2, new ActiveTargetGoal<>(this, LivingEntity.class, false, (entity, world) -> {
			EntityType<?> t = entity.getType();

			if (this.getMainHandStack().isEmpty() || !this.getMainHandStack().contains(DataComponentTypes.FOOD)) {
				if (t == EntityType.SHEEP || t == EntityType.CHICKEN || t == EntityType.RABBIT || t == EntityType.CAT || t == EntityType.OCELOT) {
					return true;
				}
				else if (t != HyenaMod.HYENA && entity.getHealth() <= 8) {
					return true;
				}
			}
			return t == EntityType.WOLF && ((WolfEntity) entity).isTamed();
		}));
		this.targetSelector.add(3, new UniversalAngerGoal<>(this, true));
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.getMainHandStack().isEmpty()) {
			shouldEatTimer--;
		}
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.getItem() == Items.ROTTEN_FLESH;
	}

	@Override
	public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return HyenaMod.HYENA.create(world, SpawnReason.BREEDING);
	}

	public static DefaultAttributeContainer.Builder createHyenaAttributes() {
		return AnimalEntity.createAnimalAttributes()
				.add(EntityAttributes.MOVEMENT_SPEED, 0.3D)
				.add(EntityAttributes.MAX_HEALTH, 8.0D)
				.add(EntityAttributes.ATTACK_DAMAGE, 4.0D);
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(HyenaMod.HYENA_STEP_SOUND, 0.15F, 1.0F);
	}

	@Override
	protected void playHurtSound(DamageSource damageSource) {
		this.playSound(HyenaMod.HYENA_HURT_SOUND, 1.0F, 1.0F);
	}

	@Override
	public void playAmbientSound() {
		if (!this.hasAngerTime()) {
			this.playSound(HyenaMod.HYENA_LAUGH_SOUND, 1.0F, 1.0F);
		}
	}

	@Override
	protected void playEatSound() {
		this.playSound(SoundEvents.ENTITY_GENERIC_EAT.value(), 0.15F, 1.0F);
	}

	@Override
	protected @Nullable SoundEvent getDeathSound() {
		return HyenaMod.HYENA_DEATH_SOUND;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		this.writeAngerToNbt(nbt);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);

		if (!nbt.contains("CanPickupLoot")) {
			this.setCanPickUpLoot(true);
		}
		this.readAngerFromNbt(this.getWorld(), nbt);
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	public float getTailAngle() {
		if (this.hasAngerTime()) {
			return (float) (Math.PI / 2.0F);
		}
		else {
			return ((float) Math.PI / 5.0F);
		}
	}

	@Override
	public int getAngerTime() {
		return this.dataTracker.get(ANGER_TIME);
	}

	@Override
	public void setAngerTime(int angerTime) {
		this.dataTracker.set(ANGER_TIME, angerTime);
	}

	@Override
	public @Nullable UUID getAngryAt() {
		return this.angryAt;
	}

	@Override
	public void setAngryAt(@Nullable UUID angryAt) {
		this.angryAt = angryAt;
	}

	@Override
	public void chooseRandomAngerTime() {
		this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
	}

	private void dropEquippedItem() {
		ItemStack itemStack = this.getMainHandStack();
		this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);

		this.getWorld().spawnEntity(new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), itemStack));
	}

	@Override
	protected void drop(ServerWorld world, DamageSource damageSource) {
		ItemStack itemStack = this.getMainHandStack();

		if (this.dropStack(world, itemStack) != null) {
			this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
		}

		super.drop(world, damageSource);
	}

	@Override
	public boolean canPickupItem(ItemStack item) {
		if (this.getMainHandStack().isEmpty()) {
			return true;
		}
		else {
			return this.prefersItemOverEquipped(item);
		}
	}

	@Override
	public boolean canGather(ServerWorld world, ItemStack stack) {
		return this.canPickupItem(stack);
	}

	@Override
	protected void loot(ServerWorld world, ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.getStack();

		if (this.canPickupItem(itemStack)) {
			this.dropEquippedItem();
			this.triggerItemPickedUpByEntityCriteria(itemEntity);
			this.equipStack(EquipmentSlot.MAINHAND, itemStack.split(1));
			this.sendPickup(itemEntity, itemStack.getCount());
			this.resetEatingTimer();
		}
	}

	private boolean prefersItem(ItemStack itemStack) {
		return PREFERRED_ITEMS.contains(itemStack.getItem());
	}

	private boolean prefersItemOverEquipped(ItemStack itemStack) {
		ItemStack equippedStack = this.getMainHandStack();

		boolean prefersItemStack = this.prefersItem(itemStack);
		boolean prefersEquipped = this.prefersItem(this.getMainHandStack());

		if (prefersEquipped && !prefersItemStack) {
			return false;
		}
		else if (!prefersEquipped && prefersItemStack) {
			return true;
		}
		else {
			return PREFERRED_ITEMS.indexOf(itemStack.getItem()) < PREFERRED_ITEMS.indexOf(equippedStack.getItem());
		}
	}

	protected void resetEatingTimer() {
		this.eatingTime = HyenaEntity.EATING_TIME;

		ItemStack itemStack = this.getMainHandStack();

		if (itemStack.isEmpty()) return;

		if (itemStack.contains(DataComponentTypes.FOOD)) {
			this.shouldEatTimer = HyenaEntity.SHOULD_EAT_TIMER_BASE * (this.random.nextFloat() + this.random.nextFloat());
		}
		else {
			this.shouldEatTimer = HyenaEntity.SHOULD_EAT_TIMER_BASE * (this.random.nextFloat() + this.random.nextFloat()) * (1 + this.random.nextFloat() * 2);
		}
	}

	public static boolean canSpawn(EntityType<HyenaEntity> ignoredType, WorldAccess worldAccess, SpawnReason ignoredSpawnReason, BlockPos pos, Random ignoredRandom) {
		return worldAccess.getBlockState(pos.down()).isIn(BlockTags.VALID_SPAWN) && isLightLevelValidForNaturalSpawn(worldAccess, pos);
	}

	private class PickupItemGoal extends Goal {
		private static final float PICKUP_CHANCE = 0.7F;
		private final Random random;
		private ItemEntity targetedItem;

		public PickupItemGoal() {
			this.random = HyenaEntity.this.random;
			this.setControls(EnumSet.of(Control.MOVE));
		}

		@Override
		public boolean canStart() {
			if (HyenaEntity.this.hasAngerTime()) {
				return false;
			}
			else {
				List<ItemEntity> list = HyenaEntity.this.getWorld().getEntitiesByClass(ItemEntity.class, HyenaEntity.this.getBoundingBox().expand(8.0D, 8.0D, 8.0D), itemEntity -> {
					if (!HyenaEntity.this.getMainHandStack().isEmpty()) {
						return HyenaEntity.this.prefersItemOverEquipped(itemEntity.getStack());
					}
					else {
						return HyenaEntity.this.prefersItem(itemEntity.getStack()) || this.random.nextFloat() < PICKUP_CHANCE;
					}
				});

				return !list.isEmpty();
			}
		}

		@Override
		public void tick() {
			if (targetedItem != null && !targetedItem.isRemoved()) {
				HyenaEntity.this.getNavigation().startMovingTo(targetedItem, 1.2D);
			}
		}

		@Override
		public void start() {
			List<ItemEntity> list = HyenaEntity.this.getWorld().getEntitiesByClass(ItemEntity.class, HyenaEntity.this.getBoundingBox().expand(10.0D, 10.0D, 10.0D), itemEntity -> {
				if (!HyenaEntity.this.getMainHandStack().isEmpty()) {
					return HyenaEntity.this.prefersItemOverEquipped(itemEntity.getStack());
				}
				else {
					return true;
				}
			});

			if (!list.isEmpty()) {
				targetedItem = list.getFirst();
			}
			else {
				targetedItem = null;
			}
		}
	}

	private class EatItemGoal extends Goal {
		public EatItemGoal() {}

		@Override
		public void tick() {
			if (!HyenaEntity.this.getWorld().isClient()) {
				HyenaEntity.this.playEatSound();
				HyenaEntity.this.getWorld().sendEntityStatus(HyenaEntity.this, EntityStatuses.CREATE_EATING_PARTICLES);

				if (HyenaEntity.this.eatingTime-- < 0) {
					HyenaEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
					HyenaEntity.this.resetEatingTimer();
				}
			}
		}

		@Override
		public boolean canStart() {
			ItemStack itemStack = HyenaEntity.this.getMainHandStack();
			if (itemStack.isEmpty()) {
				return false;
			}
			else {
				return HyenaEntity.this.shouldEatTimer < 0;
			}
		}

		@Override
		public boolean shouldContinue() {
			return !HyenaEntity.this.hasAngerTime() && !HyenaEntity.this.getMainHandStack().isEmpty();
		}
	}

	public class GoToVillageGoal extends net.minecraft.entity.ai.goal.GoToVillageGoal {
		public GoToVillageGoal(int searchRange) {
			super(HyenaEntity.this, searchRange);
		}

		@Override
		public boolean canStart() {
			return super.canStart() && this.canGoToVillage();
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && this.canGoToVillage();
		}

		private boolean canGoToVillage() {
			return !HyenaEntity.this.hasAngerTime() && HyenaEntity.this.getTarget() == null;
		}
	}
}
