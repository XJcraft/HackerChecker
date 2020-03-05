package org.xjcraft.hackerchecker;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jim.bukkit.audit.apply.PlayerApplyEvent;
import org.xjcraft.annotation.RCommand;
import org.xjcraft.api.CommonCommandExecutor;
import org.xjcraft.hackerchecker.bean.MinerLog;
import org.xjcraft.hackerchecker.config.Config;
import org.xjcraft.hackerchecker.config.LogConfig;
import org.xjcraft.hackerchecker.config.WarningConfig;

import java.util.*;

public class PlayerListener implements Listener, CommonCommandExecutor {
    private HackerChecker plugin;
    BukkitTask task;
    Set<String> names = new HashSet<>();

    public PlayerListener(HackerChecker plugin) {
        this.plugin = plugin;

        task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::timer, 100, 20 * 60 * 5);
    }

    private void timer() {
        synchronized (LogConfig.config) {
            plugin.saveConfig(LogConfig.class);
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("XJCraftAudit");
        if (plugin == null) {
            return;
        }
        org.jim.bukkit.audit.PlayerMeta playerMeta = ((org.jim.bukkit.audit.AuditPlugin) plugin).getHelper().getPlayerMeta(event.getPlayer());
        if (playerMeta == null || playerMeta.getStatus() == org.jim.bukkit.audit.Status.APPLIED_VILLAGE_BASE) {
            names.add(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        names.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void apply(PlayerApplyEvent event) {
        MinerLog minerLog = LogConfig.config.getLogs().get(event.getPlayer().getName());
        if (minerLog != null) {
            double rate = minerLog.getDiamonds().doubleValue() / minerLog.getTotal();
            if (rate > Config.config.getBanRate()) {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), String.format("ban %s 涉嫌作弊，如有异议请联系op进行复查！", event.getPlayer()));
            }
            synchronized (this) {
                if (rate > Config.config.getSuspectRate()) {
                    WarningConfig.config.getSuspects().put(event.getPlayer().getName(), (float) rate);
                    plugin.saveConfig(WarningConfig.class);
                } else {
                    LogConfig.config.getLogs().remove(event.getPlayer().getName());
                }
            }

        }
        names.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().getLocation().getBlockY() > Config.config.getHeight()) return;
        if (names.contains(player.getName())) {
            MinerLog minerLog = LogConfig.config.getLogs().computeIfAbsent(player.getName(), k -> new MinerLog());
            minerLog.setTotal(minerLog.getTotal() + 1);
            if (event.getBlock().getType() == Material.DIAMOND_ORE)
                minerLog.setDiamonds(minerLog.getDiamonds() + 1);
        }
    }


    @RCommand("check")
    public void check(CommandSender player) {
        Set<Map.Entry<String, Float>> entries = WarningConfig.config.getSuspects().entrySet();
        ArrayList<Map.Entry<String, Float>> list = new ArrayList<>(entries);
        list.sort(new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                float v = o1.getValue() - o2.getValue();
                if (v > 0) return 1;
                if (v < 0) return -1;
                return 0;
            }
        });
        for (Map.Entry<String, Float> entry : list) {
            player.sendMessage(String.format("玩家%s的钻石概率为%s", entry.getKey(), entry.getValue()));
        }

    }

    @RCommand("remove")
    public void remove(CommandSender player, String name) {
        LogConfig.config.getLogs().remove(name);
        WarningConfig.config.getSuspects().remove(name);
        player.sendMessage(name + "已移除！");
    }

}
