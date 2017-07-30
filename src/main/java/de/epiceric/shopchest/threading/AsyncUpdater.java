package de.epiceric.shopchest.threading;

import de.epiceric.shopchest.ShopChest;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AsyncUpdater {

    protected final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();
    protected final ShopChest plugin;

    protected volatile BukkitTask task;

    public AsyncUpdater(ShopChest plugin) {
        this.plugin = plugin;
    }

    /**
     * Start the task if it is not already running
     */
    public void start() {
        if (!isRunning()) {
            long interval = plugin.getShopChestConfig().update_quality.getInterval();
            task = new AsyncUpdaterTask().runTaskTimerAsynchronously(plugin, interval, interval);
        }
    }

    /**
     * Stop the task if it is running
     */
    public void stop() {
        if (isRunning()) {
            task.cancel();
            task = null;
        }
    }

    /**
     * Stop the task and restart it
     */
    public void restart() {
        stop();
        start();
    }

    /**
     * @return Whether the task is currently running
     */
    public boolean isRunning() {
        return task != null;
    }

    /**
     * Add a runnable to the queue
     * @param runnable Runnable to add
     */
    public void add(Runnable runnable) {
        queue.offer(runnable);
    }

    public class AsyncUpdaterTask extends BukkitRunnable {

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
