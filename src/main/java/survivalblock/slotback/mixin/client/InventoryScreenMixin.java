package survivalblock.slotback.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.slotback.client.SlotbackClient;
import survivalblock.slotback.common.Slotback;
import survivalblock.slotback.common.compat.TrinketsCharmCompat;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {

    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "drawBackground", at = @At(value = "RETURN"))
    public void drawBlankSlot(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo info) {
        RenderSystem.setShaderTexture(0, SlotbackClient.BACK_TEXTURE);
        int xOff = 0;
        int yOff = 0;
        if (FabricLoader.getInstance().isModLoaded("trinkets") && TrinketsCharmCompat.isCharmLoaded(this.client.player)) {
            xOff += 4 * 19 - 1;
            yOff += 19;
        }
        DrawableHelper.drawTexture(matrices, this.x + 76 + xOff, this.y + 43 + yOff, 0.0F, 0.0F, 18, 18, 18, 18);
    }

}