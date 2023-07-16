package com.darwish.nppsim;

import static com.darwish.nppsim.NPPSim.stateArray;
import java.io.Serializable;

abstract class Component implements Serializable {
    public Component() {
        if (stateArray != null) {
            stateArray.add(this);
        }
    }
}

abstract class WaterSteamComponent extends Component {
    protected double waterInflow, waterInflowRate;
    protected double waterOutflow, waterOutflowRate;
    protected double steamInflow, steamInflowRate;
    protected double steamOutflow, steamOutflowRate;
    protected double waterTemperature = 20.0, steamTemperature = 20.0;
    protected double waterLevel = 0.0;
    protected double pressure = 0.10142;
    
    protected void resetFlows() {
        waterInflowRate = waterInflow;
        waterOutflowRate = waterOutflow;
        steamInflowRate = steamInflow;
        steamOutflowRate = steamOutflow;
        waterInflow = 0;
        waterOutflow = 0;
        steamInflow = 0;
        steamOutflow = 0;
    }

    public double getSteamInflowRate() {
        return steamInflowRate * 20;
    }

    public double getSteamOutflowRate() {
        return steamOutflowRate * 20;
    }

    public double getWaterInflowRate() {
        return waterInflowRate * 20;
    }

    public double getWaterOutflowRate() {
        return waterOutflowRate * 20;
    }
    
    public double getWaterLevel() {
        return waterLevel;
    }
    
    public double getWaterTemperature() {
        return waterTemperature;
    }
    
    public double getSteamTemperature() {
        return steamTemperature;
    }
    
    public double getPressure() {
        return pressure;
    }
}

abstract class WaterSteamSubComponent implements Serializable {
    protected double waterInflow, waterInflowRate;
    protected double waterOutflow, waterOutflowRate;
    protected double steamInflow, steamInflowRate;
    protected double steamOutflow, steamOutflowRate;
    protected double waterTemperature = 20.0, steamTemperature = 20.0;
    protected double waterLevel = 0.0;
    protected double pressure = 0.10142;
    
    protected void resetFlows() {
        waterInflowRate = waterInflow;
        waterInflowRate = waterOutflow;
        steamInflowRate = steamInflow;
        steamOutflowRate = steamOutflow;
        waterInflow = 0;
        waterOutflow = 0;
        steamInflow = 0;
        steamOutflow = 0;
    }

    public double getSteamInflowRate() {
        return steamInflowRate * 20;
    }

    public double getSteamOutflowRate() {
        return steamOutflowRate * 20;
    }

    public double getWaterInflowRate() {
        return waterInflowRate * 20;
    }

    public double getWaterOutflowRate() {
        return waterOutflowRate * 20;
    }
    
    public double getWaterLevel() {
        return waterLevel;
    }
    
    public double getWaterTemperature() {
        return waterTemperature;
    }
    
    public double getSteamTemperature() {
        return steamTemperature;
    }
    
    public double getPressure(){
        return pressure;
    }
}
