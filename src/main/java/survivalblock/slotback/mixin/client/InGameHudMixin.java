package survivalblock.slotback.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.slotback.common.Slotback;
import survivalblock.slotback.common.component.HoldingBackToolComponent;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {

    @Shadow private int scaledWidth;

    @Shadow private int scaledHeight;

    @Shadow protected abstract void renderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed);

    @Shadow @Final private static Identifier WIDGETS_TEXTURE;

    @WrapWithCondition(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 1))
    private boolean doNotRenderSelectedSlotIfHoldingBackTool(InGameHud instance, MatrixStack matrixStack, int x, int y, int u, int v, int width, int height, @Local PlayerEntity player) {
        return !HoldingBackToolComponent.get(player).isHoldingBackWeapon();
    }

    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableBlend()V"))
    private void renderBackslot(float tickDelta, MatrixStack matrices, CallbackInfo ci, @Local PlayerEntity player) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        ItemStack itemStack = player.getInventory().getStack(Slotback.SLOT_ID);
        Arm arm = player.getMainArm().getOpposite();
        int i = this.scaledWidth / 2;
        int j = this.getZOffset();
        this.setZOffset(-90);

        if (!itemStack.isEmpty()) {
            int x = (arm == Arm.RIGHT ? i - 91 - 29 : i + 91);
            int hotbarY = this.scaledHeight - 23;
            this.drawTexture(matrices, x, hotbarY, arm == Arm.RIGHT ? 24 : 53, 22, 29,24);
            if (HoldingBackToolComponent.get(player).isHoldingBackWeapon()) {
                int offset = (arm == Arm.RIGHT ? -1 : 6);
                this.drawTexture(matrices, x + offset, hotbarY, 0, 22, 24, 22);
            }
            this.setZOffset(j);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            int n = this.scaledHeight - 16 - 3;
            try {
                int stuff = arm == Arm.RIGHT ? i - 91 - 26 : i + 91 + 10;
                this.renderHotbarItem(stuff, n, tickDelta, player, itemStack, 1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        RenderSystem.disableBlend();
    }
}
