package com.darwish.nppsim;

public class Dearator extends WaterSteamComponent implements Connectable, UIReadable {
    final SteamValve steamInlet, steamOutlet;
    private final double volume = 193.0;
    private final double nominalWaterVolume;
    private double initialSteamMass; // this would be air in reality
    private double steamMass = 0.0; // kg
    private double waterMass = 124000.0; // at 200C
    private double thermalLoss = 0.0; // TODO conductive energy loss Mwt

    // calculated results from the update thread stored here for use in other
    // functions:
    private double steamDensity;
    private double specificHeatWater = NPPMath.calculateSpecificHeatWater(waterTemperature);
    private double specificVaporEnthalpy = NPPSim.tables.getSpecificVaporEnthalpyByTemperature(waterTemperature);// NPPSim.calculateSpecificVaporEnthalpy(waterTemperature);
    private double specificDensityWater = NPPSim.tables.getWaterDensityByTemp(waterTemperature);
    private double waterVolume;
    private double steamVolume;
    private double deltaEnergy = 0.0, deltaHeatingEnergy = 0.0; // surplus/deficit energy from last time ste
    private double waterInflowTemperature = 20.0; // c

    public double deltaSteamMass; // debug

    public Dearator() {
        steamInlet = new SteamValve(244.03, 24, NPPSim.steamPiping, this); //48.61 for 10 kg/s
        steamOutlet = new SteamValve(1336.89, 48, this, NPPSim.atmosphere); //267.38 for 10 kg/s
        waterVolume = waterMass * specificDensityWater;
        steamVolume = volume - waterVolume;
        nominalWaterVolume = waterVolume;
        initialSteamMass = steamVolume / 1.6718; // constant density for 20c
        steamDensity = steamVolume / (steamMass + initialSteamMass);
        specificDensityWater = NPPSim.tables.getWaterDensityByTemp(20);
    }

    public void update() {
        //waterTemperature -= (0.5 * waterTemperature - 10) * 0.000005;       
        double[] waterInflowData = NPPMath.mixWater(waterMass, waterTemperature, waterInflow, waterInflowTemperature);
        waterTemperature = waterInflowData[1];
        waterMass = waterInflowData[0];
        waterMass -= waterOutflow;

        double vacuumBreakerFlow = (pressure - 0.10142) * 20;
        
        specificHeatWater = NPPMath.calculateSpecificHeatWater(waterTemperature);
        specificVaporEnthalpy = NPPSim.tables.getSpecificVaporEnthalpyByTemperature(waterTemperature);// NPPMath.calculateSpecificVaporEnthalpy(feedwaterTemperature);
        specificDensityWater = NPPSim.tables.getWaterDensityByTemp(waterTemperature);

        
        double oldWaterTemp = waterTemperature;
        waterTemperature += ((NPPSim.tables.getSteamEnthalpyByTemperature(steamTemperature) - NPPSim.tables.getSteamEnthalpyByTemperature(waterTemperature)) * steamMass) / (specificHeatWater * waterMass);
        specificHeatWater = NPPMath.calculateSpecificHeatWater(waterTemperature);
        deltaEnergy += (NPPSim.tables.getSteamEnthalpyByTemperature(waterTemperature) - NPPSim.tables.getSteamEnthalpyByTemperature(oldWaterTemp)) * steamMass;  //correct for difference in new steamTemp and new waterTemp after energy transfer
        
        steamTemperature = waterTemperature;
        if (initialSteamMass > 0) { //simulate the air being replaced by steam
            initialSteamMass -= steamOutflow;
        } else {
            steamMass -= steamOutflow;
        }
        if (pressure < 0.10142) {
            initialSteamMass -= vacuumBreakerFlow;
        }
        else if (pressure > 1.45) {
            steamMass -= (pressure - 1.45) * 50; //atmospheric valves opened;
        }

        //equation for heating the condensate with incoming steam 

        double energy = 0 - deltaHeatingEnergy / 3;
        deltaHeatingEnergy -= deltaHeatingEnergy / 3;

        waterTemperature += (energy / (specificHeatWater * waterMass));
        double potentialPressure = NPPSim.tables.getSteamPressureByTemp(waterTemperature);
        
        specificDensityWater = NPPSim.tables.getWaterDensityByTemp(waterTemperature);
        waterVolume = waterMass * specificDensityWater;
        steamVolume = volume - waterVolume;
        deltaSteamMass = steamVolume / NPPSim.tables.getSteamDensityByPressure(potentialPressure) - (steamMass + initialSteamMass);

        if (0.5 * steamInflow + deltaSteamMass < 0) {
            deltaSteamMass = 0 - 0.5 * steamInflow;    //0.5 to simulate onlu part of the steam condensing
        } else {
            deltaSteamMass *= 0.5;
        }

        specificVaporEnthalpy = NPPSim.tables.getSpecificVaporEnthalpyByTemperature(waterTemperature);
        deltaHeatingEnergy += deltaSteamMass * specificVaporEnthalpy;

        waterMass -= deltaSteamMass;  
        steamMass += deltaSteamMass;

        //equation for steam and water in the dearator moving toward saturation
        
        energy = 0 - deltaEnergy / 3;
        deltaEnergy -= deltaEnergy / 3;

        waterTemperature += (energy / (specificHeatWater * waterMass));
        potentialPressure = NPPSim.tables.getSteamPressureByTemp(waterTemperature);
        
        specificDensityWater = NPPSim.tables.getWaterDensityByTemp(waterTemperature);
        waterVolume = waterMass * specificDensityWater;
        steamVolume = volume - waterVolume;
        deltaSteamMass = steamVolume / NPPSim.tables.getSteamDensityByPressure(potentialPressure) - (steamMass + initialSteamMass);

        if (0.0001 * steamMass + deltaSteamMass < 0) {
            deltaSteamMass = 0 - 0.0001 * steamMass;    //0.0001 to simulate only part of the steam condensing
        } else {
            deltaSteamMass *= 0.0001;
        }

        specificVaporEnthalpy = NPPSim.tables.getSpecificVaporEnthalpyByTemperature(waterTemperature);
        deltaEnergy += deltaSteamMass * specificVaporEnthalpy;

        waterMass -= deltaSteamMass;  
        steamMass += deltaSteamMass;


        if (steamMass < 0) {
            steamMass = 0;
        }

        specificDensityWater = NPPSim.tables.getWaterDensityByTemp(waterTemperature);
        waterVolume = waterMass * specificDensityWater;
        waterLevel = (waterVolume / nominalWaterVolume - 1) * 100;
        steamVolume = volume - waterVolume;
        steamDensity = steamVolume / (steamMass + initialSteamMass);
        pressure = NPPSim.tables.getSteamPressureByDensity(steamDensity);
        resetFlows();
    }

    @Override
    public double getSteamDensity() {
        return steamDensity;
    }

    @Override
    public double getWaterDensity() {
        // TODO Auto-generated method stub
        return 0;
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
    public double getWaterMass() {
        return waterMass;
    }

    public void setWaterTemp(double tempC) {
        waterTemperature = tempC;
        waterMass = nominalWaterVolume / NPPSim.tables.getWaterDensityByTemp(tempC);
    }
    
    @Override
    public void updateSteamOutflow(double flow, double tempC) {
        steamOutflow = flow;
    }

    @Override
    public void updateSteamInflow(double flow, double tempC) {
        Double[] inflowData = NPPMath.mixSteam(steamMass, steamTemperature, flow, tempC);
        steamMass = inflowData[0];
        steamTemperature = inflowData[1];
        steamInflow = flow;
        
    }

    @Override
    public void updateWaterOutflow(double flow, double tempC) {
        waterOutflow = flow;
    }

    @Override
    public void updateWaterInflow(double flow, double tempC) {
        double[] waterInflowData = NPPMath.mixWater(waterInflow, waterInflowTemperature, flow, tempC);
        waterInflow = waterInflowData[0];
        waterInflowTemperature = waterInflowData[1];
    }
}
