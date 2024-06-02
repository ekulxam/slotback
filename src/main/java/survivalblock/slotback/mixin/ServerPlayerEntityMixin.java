package survivalblock.slotback.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.slotback.common.Slotback;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void saveBackslotOnDeath(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if (!Slotback.keepBackslotOnDeath) {
            return;
        }
        ItemStack stack = oldPlayer.getInventory().getStack(Slotback.SLOT_ID);
        if (stack != null && !stack.isEmpty()) {
            this.getInventory().setStack(Slotback.SLOT_ID, stack);
        }
    }
}
