package me.lede.level.objects;

public class ExperienceBuff {

    private double buff;
    private long remainSeconds;

    public ExperienceBuff() {
        this.buff = 1;
        this.remainSeconds = -1;
    }

    public boolean isEnabled() {
        return remainSeconds != -1;
    }

    public void decreaseSecond() {
        this.remainSeconds--;
    }

    public double getBuff() {
        return buff;
    }

    public void setBuff(double buff) {
        this.buff = buff;
    }

    public long getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(long remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

}
