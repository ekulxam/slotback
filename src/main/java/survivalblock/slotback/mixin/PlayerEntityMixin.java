package survivalblock.slotback.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.slotback.common.Slotback;
import survivalblock.slotback.common.component.HoldingBackToolComponent;

@SuppressWarnings("UnreachableCode")
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

	@Shadow public abstract PlayerInventory getInventory();

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "getEquippedStack", at = @At("HEAD"), cancellable = true)
	private void getEquippedBack(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
		if (!slot.equals(EquipmentSlot.MAINHAND)) {
			return;
		}
		if (HoldingBackToolComponent.get((PlayerEntity) (Object) this).isHoldingBackWeapon()) {
			cir.setReturnValue(this.getInventory().getStack(Slotback.SLOT_ID));
		}
	}
}