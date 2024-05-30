package survivalblock.slotback.common.init;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import survivalblock.slotback.common.Slotback;
import survivalblock.slotback.common.component.HoldingBackToolComponent;

public class SlotbackEntityComponents implements EntityComponentInitializer {

    public static final ComponentKey<HoldingBackToolComponent> HOLDING_BACK_TOOL_COMPONENT = ComponentRegistry.getOrCreate(Slotback.id("holding_back_tool"), HoldingBackToolComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(HOLDING_BACK_TOOL_COMPONENT, HoldingBackToolComponent::new, RespawnCopyStrategy.CHARACTER);
    }
}
