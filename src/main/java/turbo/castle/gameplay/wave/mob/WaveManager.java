package turbo.castle.gameplay.wave.mob;

import org.springframework.stereotype.Component;

@Component
public class WaveManager {

    private int currentWave = 0;
    private boolean woodPVP = false;
    private boolean stonePVP = false;

    public void setWoodPVP(boolean woodPVP) {
        this.woodPVP = woodPVP;
    }

    public void setStonePVP(boolean stonePVP) {
        this.stonePVP = stonePVP;
    }

    public boolean isWoodPVP() {
        return woodPVP;
    }

    public boolean isStonePVP() {
        return stonePVP;
    }

    public void nextWave() {
        currentWave++;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public void setCurrentWave(int currentWave) {
        this.currentWave = currentWave;
    }

    public double calculateHealth(double baseHealth) {
        return baseHealth + (currentWave * 5);
    }

    public double calculateDamage(double baseDamage) {
        return baseDamage + (currentWave * 1.5);
    }

    public double calculateSpeed(double baseSpeed) {
        return baseSpeed + (currentWave * 0.02);
    }
}