package survivalblock.slotback.common.init;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import survivalblock.slotback.common.Slotback;

public class SlotbackTags {

    public static class Items {
        public static final TagKey<Item> SHOULD_RENDER_UPSIDE_DOWN = TagKey.of(Registry.ITEM_KEY, Slotback.id("should_render_upside_down"));
    }
}
