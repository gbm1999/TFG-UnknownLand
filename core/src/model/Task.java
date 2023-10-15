package model;

public class Task {
    private String name;
    private String description;
    private int countProgress;
    private ItemStack item;

    public Task(String name){
        this.name = name;
        countProgress = 0;
        try {
            item = new ItemStack(Material.PISTOL, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        description = "";
    }

    public boolean isCompleted(){
        return countProgress == 100;
    }
    public int getCountProgress() {
        return countProgress;
    }

    public void setCountProgress(int countProgress) {
        this.countProgress = countProgress;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
