package api.peridot.periapi.inventories;

import org.bukkit.scheduler.BukkitTask;

public class PersonalInventoryData {

    private BukkitTask updateTask;
    private int openedPage;

    public BukkitTask getUpdateTask() {
        return updateTask;
    }

    public void setUpdateTask(BukkitTask updateTask) {
        cancelUpdateTask();
        this.updateTask = updateTask;
    }

    public void cancelUpdateTask() {
        if (updateTask == null) return;
        updateTask.cancel();
    }

    public int getOpenedPage() {
        return openedPage;
    }

    public void setOpenedPage(int openedPage) {
        this.openedPage = openedPage;
    }

}
