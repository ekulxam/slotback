package survivalblock.slotback.client.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.Vec3f;
import survivalblock.slotback.client.compat.AmariteCompatLongswordRenderer;
import survivalblock.slotback.common.Slotback;
import survivalblock.slotback.common.component.HoldingBackToolComponent;
import survivalblock.slotback.common.init.SlotbackTags;

@Environment(EnvType.CLIENT)
public class BackToolFeatureRenderer extends HeldItemFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    private final HeldItemRenderer heldItemRenderer;

    public BackToolFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context, HeldItemRenderer heldItemRenderer) {
        super(context, heldItemRenderer);
        this.heldItemRenderer = heldItemRenderer;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity player, float f, float g, float h, float j, float k, float l) {
        /*
         * BEGIN CREDIT
         *
         * Code simplified from BackSlot
         * Backslot is licensed under the GNU General Public License v3.0
         */
        ItemStack backslotStack = player.getInventory().getStack(Slotback.SLOT_ID);
        if (!backslotStack.isEmpty() && !HoldingBackToolComponent.get(player).isHoldingBackWeapon()) {
            matrixStack.push();
            ModelPart modelPart = this.getContextModel().body;
            modelPart.rotate(matrixStack);
            Item backslotItem = backslotStack.getItem();

            boolean isATrident = backslotItem instanceof TridentItem;
            boolean isLongsword = FabricLoader.getInstance().isModLoaded("amarite") && AmariteCompatLongswordRenderer.isALongsword(backslotItem);

            if (isATrident) {
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(52.0F));
                matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(40.0F));
                matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-25.0F));
                matrixStack.translate(-0.21D, 0.0D, 0.0D);

                if (isWearingSomethingNotElytra(player)) matrixStack.translate(-0.08F, 0.0F, 0.0F);

                matrixStack.scale(1.0F, -1.0F, -1.0F);
                /*
                 * END CREDIT
                 *
                 * A copy of the GNU General Public License v3.0 can be found at:
                 * https://github.com/Globox1997/BackSlot/blob/1.20/LICENSE
                 * (It is too long to put here)
                 */
            } else if (isLongsword) {
                AmariteCompatLongswordRenderer.doTransforms(matrixStack, player);
            } else if (isAShield(backslotItem)) {
                matrixStack.translate(0.0, 0.2, 0.27);

                if (isWearingSomethingNotElytra(player)) matrixStack.translate(0.0F, 0.0F, 0.15F);

                matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0F));
                matrixStack.scale(1.8F, 1.8F, 1.8F);
            } else {
                matrixStack.translate(0.0, 0.35, 0.15);
                if (isWearingSomethingNotElytra(player)) matrixStack.translate(0.0F, 0.0F, 0.06F);

                if (!(shouldRenderUpsideDown(backslotStack))) {
                    matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0F));
                    if(backslotItem instanceof BlockItem) matrixStack.translate(0.0, 0.12, 0.0);
                }
            }
            ModelTransformation.Mode mode;
            if (isATrident) {
                mode = ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND;
            } else {
                mode = ModelTransformation.Mode.FIXED;
            }

            heldItemRenderer.renderItem(player, backslotStack, mode, false, matrixStack, vertexConsumerProvider, i);
            matrixStack.pop();
        }
    }


    private static boolean isAShield(Item item) {
        return item instanceof ShieldItem || item.getRegistryEntry().isIn(ConventionalItemTags.SHIELDS);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isAnElytra(ItemStack stack) {
        return stack.getItem() instanceof ElytraItem || stack.getItem() instanceof FabricElytraItem;
    }

    public static boolean isWearingSomethingNotElytra(PlayerEntity player) {
        return player.hasStackEquipped(EquipmentSlot.CHEST) && !isAnElytra(player.getEquippedStack(EquipmentSlot.CHEST));
        }

    private static boolean shouldRenderUpsideDown(ItemStack stack) {
        return stack.isIn(SlotbackTags.Items.SHOULD_RENDER_UPSIDE_DOWN) || stack.getItem() instanceof ArmorItem;
    }
}