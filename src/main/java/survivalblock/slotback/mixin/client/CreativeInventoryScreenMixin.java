package survivalblock.slotback.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.slotback.client.SlotbackClient;
import survivalblock.slotback.common.Slotback;
import survivalblock.slotback.common.compat.TrinketsCharmCompat;
import survivalblock.slotback.common.slot.Backslot;
import survivalblock.slotback.client.slot.CreativeBackslot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {

    @Shadow private static int selectedTab;

    public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }


    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/screen/slot/Slot.<init>(Lnet/minecraft/inventory/Inventory;III)V"), method = "setSelectedTab")
    private void addCreativeTrinketSlots(ItemGroup g, CallbackInfo info) {
        List<Slot> slots = new ArrayList<>(this.client.player.playerScreenHandler.slots);
        Collections.reverse(slots); // reverse the collection so that it finds the backslot earlier

        /*
         * BEGIN CREDIT:
         * From Trinkets' CreativeInventoryScreenMixin
         * Trinkets is licensed under the MIT License, Copyright (c) 2019 Emily Rose Ploszaj
         */
        for (Slot slot : slots) {
            if (slot instanceof Backslot backslot) {
                int xOff = 50;
                if (FabricLoader.getInstance().isModLoaded("trinkets") && TrinketsCharmCompat.isCharmLoaded(this.client.player)) {
                    xOff += 38;
                }
                int yOff = -24;
                // add the backslot after the delete item in the creative tab
                this.handler.slots.add(new CreativeBackslot(backslot, backslot.getIndex(), Backslot.DEFAULT_X + xOff, Backslot.DEFAULT_Y + yOff));
                /*
                 * END CREDIT
                 *
                 * The MIT License is stated as follows:
                 *
                 * Permission is hereby granted, free of charge, to any person obtaining a copy
                 * of this software and associated documentation files (the "Software"), to deal
                 * in the Software without restriction, including without limitation the rights
                 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
                 * copies of the Software, and to permit persons to whom the Software is
                 * furnished to do so, subject to the following conditions:
                 *
                 * The above copyright notice and this permission notice shall be included in all
                 * copies or substantial portions of the Software.
                 *
                 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
                 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
                 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
                 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
                 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
                 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
                 * SOFTWARE.
                 */
                break;
            }
        }
    }

    @Inject(method = "drawBackground", at = @At(value = "RETURN"))
    public void drawCreativeBlankSlot(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo info) {
        // manually render the empty slot background
        if (selectedTab != ItemGroup.INVENTORY.getIndex()) {
            return;
        }
        int xOff = 0;
        if (FabricLoader.getInstance().isModLoaded("trinkets") && TrinketsCharmCompat.isCharmLoaded(this.client.player)) {
            xOff += 38;
        }
        RenderSystem.setShaderTexture(0, SlotbackClient.BACK_TEXTURE);
        DrawableHelper.drawTexture(matrices, this.x + 126 + xOff, this.y + 19, 0.0F, 0.0F, 18, 18, 18, 18);
    }
}
