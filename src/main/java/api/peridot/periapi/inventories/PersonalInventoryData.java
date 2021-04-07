package api.peridot.periapi.inventories;

import org.bukkit.scheduler.BukkitTask;

public class PersonalInventoryData {

    private BukkitTask updateTask;
    private int openedPage;

    public BukkitTask getUpdateTask() {
        return this.updateTask;
    }

    public void setUpdateTask(BukkitTask updateTask) {
        this.cancelUpdateTask();
        this.updateTask = updateTask;
    }

    public void cancelUpdateTask() {
        if (this.updateTask == null) return;
        this.updateTask.cancel();
    }

    public int getOpenedPage() {
        return this.openedPage;
    }

    public void setOpenedPage(int openedPage) {
        this.openedPage = openedPage;
    }

}
