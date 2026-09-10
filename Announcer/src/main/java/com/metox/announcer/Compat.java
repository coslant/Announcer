package com.metox.announcer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// coslant
public final class Compat {

    public static final int MINOR;

    private static final String NMS_VER;

    public static java.util.logging.Logger LOG = null;

    static {
        int m = 8;
        try {
            String raw = Bukkit.getBukkitVersion().split("-")[0];
            String[] parts = raw.split("\\.");
            if (parts.length >= 2) m = Integer.parseInt(parts[1]);
        } catch (Throwable t) {
            m = 8;
        }
        MINOR = m;

        String ver = "";
        try {
            String cn = Bukkit.getServer().getClass().getName();
            String[] p = cn.split("\\.");
            if (p.length >= 4 && p[3].startsWith("v")) {
                ver = p[3];
            }
        } catch (Throwable t) {
            ver = "";
        }
        NMS_VER = ver;
    }

    private Compat() {}

    public static String color(String in) {
        if (in == null) return "";
        return ChatColor.translateAlternateColorCodes('&', in);
    }


    public static boolean title(Player p, String title, String sub, int in, int stay, int out) {
        String t = color(title);
        String s = color(sub);

        if (MINOR >= 11) {
            if (apiTitle(p, t, s, in, stay, out)) return true;
        }
        return nmsTitle(p, t, s, in, stay, out);
    }

    private static boolean apiTitle(Player p, String t, String s, int in, int stay, int out) {
        try {
            Method m = Player.class.getMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);
            m.invoke(p, t, s, in, stay, out);
            return true;
        } catch (NoSuchMethodException five) {
            try {
                Method m2 = Player.class.getMethod("sendTitle", String.class, String.class);
                m2.invoke(p, t, s);
                return true;
            } catch (Throwable ignored) {
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static boolean nmsTitle(Player p, String t, String s, int in, int stay, int out) {
        if (NMS_VER == null || NMS_VER.isEmpty()) return false;
        try {
            String nms = "net.minecraft.server." + NMS_VER + ".";
            String cb = "org.bukkit.craftbukkit." + NMS_VER + ".";

            Class<?> craftPlayer = Class.forName(cb + "entity.CraftPlayer");
            Object handle = craftPlayer.getMethod("getHandle").invoke(p);
            Field connField = handle.getClass().getField("playerConnection");
            Object conn = connField.get(handle);

            Class<?> packetClass = Class.forName(nms + "Packet");
            Method sendPacket = conn.getClass().getMethod("sendPacket", packetClass);

            Class<?> chatBase = Class.forName(nms + "IChatBaseComponent");
            Class<?> chatText = Class.forName(nms + "ChatComponentText");
            Class<?> packetTitle = Class.forName(nms + "PacketPlayOutTitle");

            Class<?> enumAction;
            try {
                enumAction = Class.forName(nms + "PacketPlayOutTitle$EnumTitleAction");
            } catch (ClassNotFoundException nested) {
                enumAction = Class.forName(nms + "EnumTitleAction");
            }

            Object aTitle = enumValue(enumAction, "TITLE");
            Object aSub = enumValue(enumAction, "SUBTITLE");
            Object aTimes = enumValue(enumAction, "TIMES");

            Constructor<?> timesCon = packetTitle.getConstructor(enumAction, chatBase, int.class, int.class, int.class);
            sendPacket.invoke(conn, timesCon.newInstance(aTimes, null, in, stay, out));

            Constructor<?> partCon = packetTitle.getConstructor(enumAction, chatBase);
            Constructor<?> textCon = chatText.getConstructor(String.class);

            if (s != null && !s.isEmpty()) {
                Object subComp = textCon.newInstance(s);
                sendPacket.invoke(conn, partCon.newInstance(aSub, subComp));
            }

            Object titleComp = textCon.newInstance(t == null ? "" : t);
            sendPacket.invoke(conn, partCon.newInstance(aTitle, titleComp));
            return true;
        } catch (Throwable ex) {
            if (LOG != null) LOG.warning("NMS title gonderilemedi (ver=" + NMS_VER + "): " + ex);
            return false;
        }
    }

    private static Object enumValue(Class<?> enumClass, String name) {
        for (Object o : enumClass.getEnumConstants()) {
            if (((Enum<?>) o).name().equals(name)) return o;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<Player> online() {
        List<Player> list = new ArrayList<Player>();
        try {
            Object res = Bukkit.class.getMethod("getOnlinePlayers").invoke(null);
            if (res instanceof Player[]) {
                for (Player p : (Player[]) res) list.add(p);
            } else if (res instanceof Collection) {
                for (Object o : (Collection<Object>) res) list.add((Player) o);
            }
        } catch (Throwable ignored) {
        }
        return list;
    }
}
