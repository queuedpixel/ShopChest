package de.epiceric.shopchest.threading;

import de.epiceric.shopchest.ShopChest;
import de.epiceric.shopchest.nms.ArmorStandWrapper;
import de.epiceric.shopchest.nms.Hologram;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HologramUpdater {

    private final ShopChest plugin;
    private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();

    private volatile BukkitTask task;

    public HologramUpdater(ShopChest plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (!isRunning()) {
            long interval = plugin.getShopChestConfig().update_quality.getInterval();
            task = new HologramUpdaterTask().runTaskTimerAsynchronously(plugin, interval, interval);
        }
    }

    public void stop() {
        if (isRunning()) {
            task.cancel();
            task = null;
        }
    }

    public void restart() {
        stop();
        start();
    }

    public boolean isRunning() {
        return task != null;
    }

    public void addToQueue(final Hologram hologram, final Player player, final boolean show) {
        queue.add(new Runnable() {
            @Override
            public void run() {
                for (ArmorStandWrapper wrapper : hologram.getArmorStandWrappers()) {
                    wrapper.setVisible(player, show);
                }

                if (hologram.getInteractArmorStandWrapper() != null) {
                    hologram.getInteractArmorStandWrapper().setVisible(player, show);
                }
            }
        });
    }

    public class HologramUpdaterTask extends BukkitRunnable {

        @Override
        public void run() {
            if (!queue.isEmpty()) {
                Runnable runnable;

                while ((runnable = queue.poll()) != null) {
                    runnable.run();
                }
            }
        }

    }

}
