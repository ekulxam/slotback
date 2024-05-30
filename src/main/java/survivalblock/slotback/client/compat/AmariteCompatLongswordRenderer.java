package survivalblock.slotback.client.compat;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import survivalblock.slotback.client.feature.BackToolFeatureRenderer;
import xyz.amymialee.amarite.items.AmariteLongswordItem;

public class AmariteCompatLongswordRenderer {

    @SuppressWarnings("unused")
    public static boolean isALongsword(ItemStack stack) {
        return isALongsword(stack.getItem());
    }

    public static boolean isALongsword(Item item) {
        return item instanceof AmariteLongswordItem;
    }

    public static void doTransforms(MatrixStack matrixStack, PlayerEntity player) {
        matrixStack.translate(-0.1, 0.25, 0.15);
        matrixStack.scale(1.8F, 1.8F, 1.8F);

        if (BackToolFeatureRenderer.isWearingSomethingNotElytra(player)) matrixStack.translate(0.0F, 0.0F, 0.06F);

    }
}
