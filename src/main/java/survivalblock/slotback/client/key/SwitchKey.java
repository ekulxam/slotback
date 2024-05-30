package survivalblock.slotback.client.key;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import survivalblock.slotback.common.Slotback;
import survivalblock.slotback.common.packet.SwitchPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

public class SwitchKey {

    /*
     * BEGIN CREDIT
     *
     * Code simplified from BackSlot
     * Backslot is licensed under the GNU General Public License v3.0
     */

    public static KeyBinding keybind;
    public static boolean wasPressed;

    public static void init() {

        keybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Switch backslot", //the keybinding's name
                InputUtil.Type.KEYSYM, //KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_G, // The keycode of the key
                "BackSlot Config" //the keybinding's category.
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (keybind.wasPressed()) {
                if (!wasPressed) {
                    switchItem();
                }
                wasPressed = true;
            } else if (wasPressed) {
                wasPressed = false;
            }
        });
    }

    public static void switchItem() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(Slotback.SLOT_ID);
        ClientPlayNetworking.send(SwitchPacket.ID, buf);
    }

    /*
     * END CREDIT
     *
     * A copy of the GNU General Public License v3.0 can be found at:
     * https://github.com/Globox1997/BackSlot/blob/1.20/LICENSE
     * (It is too long to put here)
     */
}
