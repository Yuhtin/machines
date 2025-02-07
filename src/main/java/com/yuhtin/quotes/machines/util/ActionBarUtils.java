package com.yuhtin.quotes.machines.util;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Objects;

import static me.lucko.helper.text3.Text.colorize;

/**
 * A reflection API for action bars in Minecraft.
 * Fully optimized - Supports 1.8.8+ and above.
 * Requires ReflectionUtils.
 * Messages are not colorized by default.
 * <p>
 * Action bars are text messages that appear above
 * the player's <a href="https://minecraft.gamepedia.com/Heads-up_display">hotbar</a>
 * Note that this is different than the text appeared when switching between items.
 * Those messages show the item's name and are different from action bars.
 * The only natural way of displaying action bars is when mounting.
 * <p>
 * Action bars cannot fade or stay like titles.
 * For static Action bars you'll need to send the packet every
 * 2 seconds (40 ticks) for it to stay on the screen without fading.
 * <p>
 * PacketPlayOutTitle: https://wiki.vg/Protocol#Title
 *
 * @author Crypto Morin
 * @version 3.1.0
 * @see ReflectionUtils
 */
public final class ActionBarUtils {
    /**
     * ChatComponentText JSON message builder.
     */
    private static final MethodHandle CHAT_COMPONENT_TEXT;
    /**
     * PacketPlayOutChat
     */
    private static final MethodHandle PACKET_PLAY_OUT_CHAT;
    /**
     * GAME_INFO enum constant.
     */
    private static final Object CHAT_MESSAGE_TYPE;

    static {
        MethodHandle packet = null;
        MethodHandle chatComp = null;
        Object chatMsgType = null;

        // Supporting 1.17 is not necessary, the package guards are just for readability.
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        Class<?> packetPlayOutChatClass = ReflectionUtils.getNMSClass("network.protocol.game", "PacketPlayOutChat");
        Class<?> iChatBaseComponentClass = ReflectionUtils.getNMSClass("network.chat", "IChatBaseComponent");

        try {
            // Game Info Message Type
            Class<?> chatMessageTypeClass = Class.forName(
                    ReflectionUtils.NMS + (ReflectionUtils.supports(17) ? "network.chat" : "") + "ChatMessageType"
            );

            // Packet Constructor
            MethodType type = MethodType.methodType(void.class, iChatBaseComponentClass, chatMessageTypeClass);

            for (Object obj : chatMessageTypeClass.getEnumConstants()) {
                String name = obj.toString();
                if (name.equals("GAME_INFO") || name.equalsIgnoreCase("ACTION_BAR")) {
                    chatMsgType = obj;
                    break;
                }
            }

            // JSON Message Builder
            Class<?> chatComponentTextClass = ReflectionUtils.getNMSClass("network.chat", "ChatComponentText");
            chatComp = lookup.findConstructor(chatComponentTextClass, MethodType.methodType(void.class, String.class));

            packet = lookup.findConstructor(packetPlayOutChatClass, type);
        } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException ignored) {
            try {
                // Game Info Message Type
                chatMsgType = (byte) 2;

                // JSON Message Builder
                Class<?> chatComponentTextClass = ReflectionUtils.getNMSClass("ChatComponentText");
                chatComp = lookup.findConstructor(chatComponentTextClass, MethodType.methodType(void.class, String.class));

                // Packet Constructor
                packet = lookup.findConstructor(packetPlayOutChatClass, MethodType.methodType(void.class, iChatBaseComponentClass, byte.class));
            } catch (NoSuchMethodException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }

        CHAT_MESSAGE_TYPE = chatMsgType;
        CHAT_COMPONENT_TEXT = chatComp;
        PACKET_PLAY_OUT_CHAT = packet;
    }

    private ActionBarUtils() {
    }

    /**
     * Sends an action bar to a player.
     *
     * @param player  the player to send the action bar to.
     * @param message the message to send.
     */
    public static void sendActionBar(@Nonnull Player player, @Nullable String message) {
        Objects.requireNonNull(player, "Cannot send action bar to null player");
        Objects.requireNonNull(message, "Cannot send a null actionbar message");

        String colored = colorize(message);
        try {
            Object component = CHAT_COMPONENT_TEXT.invoke(colored);
            Object packet = PACKET_PLAY_OUT_CHAT.invoke(component, CHAT_MESSAGE_TYPE);
            ReflectionUtils.sendPacket(player, packet);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}