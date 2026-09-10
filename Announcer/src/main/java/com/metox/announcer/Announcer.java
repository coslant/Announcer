package com.metox.announcer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Announcer extends JavaPlugin implements CommandExecutor {

    private final Random random = new Random();

    private BukkitTask task;
    private int index = 0;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Compat.LOG = getLogger();
        if (getCommand("announcer") != null) {
            getCommand("announcer").setExecutor(this);
        }
        start();
        getLogger().info("Announcer aktif - algilanan surum 1." + minor());
        // coslant
    }

    @Override
    public void onDisable() {
        stop();
    }

    private void start() {
        stop();

        int interval = getConfig().getInt("announcer.interval", 300);
        if (interval < 1) interval = 300;

        long period = interval * 20L;

        task = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                broadcastNext();
            }
        }, period, period);
    }

    private void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    // coslant
    private void broadcastNext() {
        List<String> lines = pickNext();
        if (lines.isEmpty()) return;

        String prefix = getConfig().getString("announcer.prefix", "");
        for (String line : lines) {
            Bukkit.broadcastMessage(color(prefix + line));
        }
    }

    private List<String> pickNext() {
        List<String> out = new ArrayList<String>();

        List<?> raw = getConfig().getList("announcer.messages");
        if (raw == null || raw.isEmpty()) return out;

        int pick;
        if (getConfig().getBoolean("announcer.random", false)) {
            pick = random.nextInt(raw.size());
        } else {
            if (index >= raw.size()) index = 0;
            pick = index;
            index++;
        }

        Object entry = raw.get(pick);
        if (entry instanceof List) {
            for (Object o : (List<?>) entry) out.add(String.valueOf(o));
        } else {
            out.add(String.valueOf(entry));
        }
        return out;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender.hasPermission("announcer.say") || sender.hasPermission("announcer.admin")) {
                sender.sendMessage(color("&7/" + label + " <mesaj>   &8|   &7/" + label + " reload   &8|   &7/" + label + " next"));
            } else {
                sender.sendMessage(color("&cBunun icin yetkin yok."));
            }
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("reload")) {
            if (!sender.hasPermission("announcer.admin")) {
                sender.sendMessage(color("&cBunun icin yetkin yok."));
                return true;
            }
            reloadConfig();
            start();
            sender.sendMessage(color("&aAnnouncer yeniden yuklendi."));
            return true;
        }

        if (sub.equals("next")) {
            if (!sender.hasPermission("announcer.admin")) {
                sender.sendMessage(color("&cBunun icin yetkin yok."));
                return true;
            }
            broadcastNext();
            return true;
        }

        if (!sender.hasPermission("announcer.say")) {
            sender.sendMessage(color("&cBunun icin yetkin yok."));
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(' ');
            sb.append(args[i]);
        }

        broadcastSay(sender.getName(), sb.toString());
        sender.sendMessage(color("&aDuyuru gonderildi."));
        return true;
    }

    private void broadcastSay(String who, String message) {
        String tFmt = getConfig().getString("announcer.say.title", "&cYetkili");
        String sFmt = getConfig().getString("announcer.say.subtitle", "&f%message%");
        int in = getConfig().getInt("announcer.say.fade-in", 10);
        int stay = getConfig().getInt("announcer.say.stay", 60);
        int out = getConfig().getInt("announcer.say.fade-out", 10);

        String titleLine = tFmt.replace("%player%", who).replace("%message%", message);
        String subLine = sFmt.replace("%player%", who).replace("%message%", message);

        for (Player p : Compat.online()) {
            Compat.title(p, titleLine, subLine, in, stay, out);
        }
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s == null ? "" : s);
    }

    private int minor() {
        try {
            return Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]);
        } catch (Throwable t) {
            return 8;
        }
    }
}
