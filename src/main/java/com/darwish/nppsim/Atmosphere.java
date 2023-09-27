package com.darwish.nppsim;

public class Atmosphere extends WaterSteamComponent implements Connectable {

    public Atmosphere(double waterTemp) {
        waterTemperature = waterTemp;
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
    public double getWaterMass() {
        return 100000000000.0;
    }
}