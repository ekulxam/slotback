package survivalblock.slotback.common.slot;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import survivalblock.slotback.client.sprite.SlotbackSprites;
import survivalblock.slotback.common.component.HoldingBackToolComponent;

public class Backslot extends Slot {

    public static final int DEFAULT_X = 77;
    public static final int DEFAULT_Y = 44;

    private final PlayerEntity player;
    private int ticksEmpty = 0;

    public Backslot(Inventory inventory, int index, PlayerEntity player, int xOffset, int yOffset) {
        super(inventory, index, DEFAULT_X + xOffset, DEFAULT_Y + yOffset);
        this.player = player;
    }

    public void tick() {
        if (this.getStack() == null || this.getStack().isEmpty()) {
            HoldingBackToolComponent holdingBackToolComponent = HoldingBackToolComponent.get(player);
            if (holdingBackToolComponent.isHoldingBackWeapon()) {
                ticksEmpty++;
                if (ticksEmpty > 2) {
                    stopHoldingBackWeapon();
                }
            }
        }
    }

    @Override
    public Pair<Identifier, Identifier> getBackgroundSprite() {
        return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, SlotbackSprites.EMPTY_BACK_SLOT_TEXTURE);
    }

    private void stopHoldingBackWeapon() {
        HoldingBackToolComponent holdingBackToolComponent = HoldingBackToolComponent.get(this.player);
        if (holdingBackToolComponent.isHoldingBackWeapon()) holdingBackToolComponent.setHoldingBackWeapon(false, true);
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        super.onTakeItem(player, stack);
        stopHoldingBackWeapon();
    }

    @SuppressWarnings("RedundantMethodOverride")
    @Override
    public boolean canInsert(ItemStack stack) {
        return true;
    }

    @SuppressWarnings("RedundantMethodOverride")
    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return true;
    }
}
