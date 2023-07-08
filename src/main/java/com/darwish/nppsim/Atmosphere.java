package com.darwish.nppsim;

public class Atmosphere extends Component implements Connectable {
    private final double waterTemp;

    public Atmosphere(double waterTemp) {
        this.waterTemp = waterTemp;
    }

    @Override
    public double getPressure() {
        return 0.10142;
    }

    @Override
    public double getSteamDensity() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void updateSteamOutflow(double flow, double tempC) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateWaterInflow(double flow, double tempC) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public double getWaterDensity() {
        return 0.00100094;
    }

    @Override
    public double getSteamMass() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getSteamVolume() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void updateSteamInflow(double flow, double tempC) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateWaterOutflow(double flow, double tempC) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public double getWaterTemperature() {
        return waterTemp;
    }

    @Override
    public double getSteamTemperature() {
        return 20;
    }

    @Override
    public double getWaterLevel() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getWaterLevel'");
    }
}