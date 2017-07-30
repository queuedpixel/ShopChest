package de.epiceric.shopchest.threading;

import de.epiceric.shopchest.ShopChest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ShopUpdater {

    public enum UpdateQuality {
        SLOWEST(31L),
        SLOWER(24L),
        SLOW(17L),
        NORMAL(10L),
        FAST(7L),
        FASTER(4L),
        FASTEST(1L);

        private final long interval;

        UpdateQuality(long interval) {
            this.interval = interval;
        }

        public long getInterval() {
            return interval;
        }
    }

    private final ShopChest plugin;
    private final Queue<Runnable> beforeNext = new ConcurrentLinkedQueue<>();

    private volatile BukkitTask task;

    public ShopUpdater(ShopChest plugin) {
        this.plugin = plugin;
    }

    /**
     * Start task, except if it is already
     */
    public void start() {
        if (!isRunning()) {
            long interval = plugin.getShopChestConfig().update_quality.getInterval();
            task = new ShopUpdaterTask().runTaskTimerAsynchronously(plugin, interval, interval);
        }
    }

    /**
     * Stop any task task then start it again
     */
    public void restart() {
        stop();
        start();
    }

    /**
     * Stop task properly
     */
    public void stop() {
        if (isRunning()) {
            task.cancel();
            task = null;
        }
    }

    /**
     * @return whether task is task or not
     */
    public boolean isRunning() {
        return task != null;
    }

    /**
     * Register a task to run before next loop
     *
     * @param runnable task to run
     */
    public void beforeNext(Runnable runnable) {
        beforeNext.add(runnable);
    }

    private class ShopUpdaterTask extends BukkitRunnable {

        @Override
        public void run() {
            if (!beforeNext.isEmpty()) {
                Runnable runnable;

                while ((runnable = beforeNext.poll()) != null) {
                    runnable.run();
                }
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                plugin.getShopUtils().updateShops(p);
            }
        }
    }
}
