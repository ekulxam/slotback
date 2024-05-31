package survivalblock.slotback.common.slot;

import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.mob.MobEntity;
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

    public Backslot(Inventory inventory, int index, PlayerEntity player, int xOffset, int yOffset) {
        super(inventory, index, DEFAULT_X + xOffset, DEFAULT_Y + yOffset);
        this.player = player;
    }

    @Override
    protected void onCrafted(ItemStack stack) {
        super.onCrafted(stack);
    }

    public void tick() {
    }

    @Override
    public Pair<Identifier, Identifier> getBackgroundSprite() {
        return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, SlotbackSprites.EMPTY_BACK_SLOT_TEXTURE);
    }

    @Override
    protected void onTake(int amount) {
        super.onTake(amount);
    }

    @Override
    public void onQuickTransfer(ItemStack newItem, ItemStack original) {
        super.onQuickTransfer(newItem, original);
        stopHoldingBackWeapon();
    }

    private void stopHoldingBackWeapon() {
        HoldingBackToolComponent.get(this.player).setHoldingBackWeapon(false, true);
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        super.onTakeItem(player, stack);
        stopHoldingBackWeapon();
    }

    @Override
    public void setStack(ItemStack stack) {
        super.setStack(stack);
        stopHoldingBackWeapon();
    }

    @SuppressWarnings("RedundantMethodOverride")
    public boolean canInsert(ItemStack stack) {
        return true;
    }

    @SuppressWarnings("RedundantMethodOverride")
    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return true;
    }
}
