package survivalblock.slotback.client.key;

import survivalblock.slotback.common.component.HoldingBackToolComponent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class HoldBackToolKey {

    public static KeyBinding keybind;
    public static boolean wasPressed;


    public static void init() {
        keybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Hold back tool", //the keybinding's name
                InputUtil.Type.KEYSYM, //KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_R, // The keycode of the key
                "BackSlot Config" //the keybinding's category.
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (keybind.wasPressed()) {
                if (!wasPressed) {
                    ClientPlayerEntity player = client.player;
                    if (player != null) {
                        HoldingBackToolComponent holdingBackToolComponent = HoldingBackToolComponent.get(player);
                        holdingBackToolComponent.setHoldingBackWeapon(!holdingBackToolComponent.isHoldingBackWeapon(), true);
                    }
                }
                wasPressed = true;
            } else if (wasPressed) {
                wasPressed = false;
            }
        });
    }
}
