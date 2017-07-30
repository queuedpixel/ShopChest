package de.epiceric.shopchest.threading;

import de.epiceric.shopchest.ShopChest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ShopUpdater extends AsyncUpdater {

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

    public ShopUpdater(ShopChest plugin) {
        super(plugin);
    }

    @Override
    public void start() {
        if (!isRunning()) {
            long interval = plugin.getShopChestConfig().update_quality.getInterval();
            task = new ShopUpdaterTask().runTaskTimerAsynchronously(plugin, interval, interval);
        }
    }

    private class ShopUpdaterTask extends AsyncUpdaterTask {

        @Override
        public void run() {
            super.run();

            for (Player p : Bukkit.getOnlinePlayers()) {
                plugin.getShopUtils().updateShops(p);
            }
        }
    }
}
