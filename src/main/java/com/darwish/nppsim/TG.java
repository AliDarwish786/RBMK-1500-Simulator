package com.darwish.nppsim;

import static com.darwish.nppsim.NPPSim.TG1InletValves;
import static com.darwish.nppsim.NPPSim.TG2InletValves;
import static com.darwish.nppsim.NPPSim.atmosphere;
import static com.darwish.nppsim.NPPSim.autoControl;
import static com.darwish.nppsim.NPPSim.core;
import static com.darwish.nppsim.NPPSim.mcc;
import static com.darwish.nppsim.NPPSim.tg1;
import static com.darwish.nppsim.NPPSim.tg2;
import java.io.Serializable;

class TG extends WaterSteamComponent implements Connectable {
    final Condenser condenser;
    private double lastStepSteamInflow = 0.0;
    private double casingTemp, turbineTemp, load;
    private double steamInletPressure = 0.0, steamOutLetPressure = 0.0;
    private double steamInflowTemperature = 20.0, steamOutflowTemperature = 20.0;
    private float rpm = 0.0f;
    private boolean synced = false, tripped = true, reversePower = false;
    private static float gridPhase = 180.0f;
    private float genPhase = 0.0f, genAlignment;
    private boolean isTG1 = false, reversePowerRecorded = false;

    public TG() {
        this.condenser = null;
    }

    public TG(Condenser condenser) {
        this.condenser = condenser;
    }

    protected static void updateGridPhase() {
        gridPhase += 900.0f;
        gridPhase %= 360.0f;
    }
    
    protected void sync() {
        if (synced) {
            return;
        }
        synced = genAlignment > 48.5 && genAlignment < 51.5 && rpm >= 2997.5 && rpm <= 3002.5;
        if (synced) {
            if (isTG1) {
                autoControl.recordEvent("TG-1 Sync");
            } else {
                autoControl.recordEvent("TG-2 Sync");
            }
        }
    }
    
    protected void deSync() {
        synced = false;
    }
    
    protected void trip(String reason) {
        if (tripped) {
            return;
        }
        if (isTG1) {
            autoControl.recordEvent("TG-1 Trip: " + reason);
        } else {
            autoControl.recordEvent("TG-2 Trip: " + reason);
        }
        synced = false;
        TG1InletValves.forEach(valve -> {
            if (valve.drain.equals(this)) {
                valve.setPosition(0);
                valve.setState(0);
            }
        });
        TG2InletValves.forEach(valve -> {
            if (valve.drain.equals(this)) {
                valve.setPosition(0);
                valve.setState(0);
            }
        });
        if (!this.isTripped() && (tg1.isTripped() || tg2.isTripped()) && core.getThermalPower() > 2400) {
            autoControl.az1Control.trip("Turbine Trip");
        }
        tripped = true;
    }
    
    protected void reset() {
        if (rpm < 3250 && condenser.getPressure() < 0.030 && mcc.drum1.getPressure() > 5 && mcc.drum2.getPressure() > 5) {
            tripped = false;
            if (isTG1) {
                autoControl.recordEvent("TG-1 Reset");
            } else {
                autoControl.recordEvent("TG-2 Reset");
            }
        }
    }

    public void update() {
        genPhase += rpm / 1200.0f * 360.0f;
        genPhase %= 360.0f;
        genAlignment = (genPhase - gridPhase);
        genAlignment = genAlignment < 180 ? genAlignment + 180 : genAlignment - 180;
        genAlignment = genAlignment / 360 * 100; 

        if (synced) {
            rpm = 3000;
            genPhase = gridPhase;
            load = ((steamInflow - 7.0) / (61.1 - 7.0)) * 750.0;
        } else {
            load = 0;
            double benchmark = steamInflow / 7.0 * 3000;
            if (rpm < benchmark) {
                rpm += (benchmark - rpm) / 1000;
            } else {
                rpm += (benchmark - rpm) / 3000; 
            }
            if (rpm < 0) {
                rpm = 0;
            }
        }
        if (rpm > 3250) {
            trip("High RPM");
        }
        if (condenser.getPressure() > 0.030) {
            trip("Condenser Vacuum Low");
        }
        if (mcc.drum1.getPressure() < 5 || mcc.drum2.getPressure() < 5) {
            trip("Low Drum Pressure");
        }
        if (load > 825) {
            trip("High Generator Load");
        }
        
        reversePower = load < 0;
        if (reversePower) {
            reversePowerTrip();
        }
        lastStepSteamInflow = steamInflow;
        steamOutflow = steamInflow; //TODO
        steamOutflowTemperature = steamInflowTemperature * 0.92; //TODO
        condenser.updateSteamInflow(steamOutflow, steamOutflowTemperature);
        resetFlows();
    }

    @Override
    public double getSteamDensity() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSteamDensity'");
    }

    @Override
    public double getWaterDensity() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getWaterDensity'");
    }

    @Override
    public double getSteamMass() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSteamMass'");
    }

    @Override
    public double getSteamVolume() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSteamVolume'");
    }

    @Override
    public double getSteamTemperature() {
        return steamInflowTemperature;
    }
    
    @Override
    public void updateSteamOutflow(double flow, double tempC) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateSteamOutFlow'");
    }

    @Override
    public void updateSteamInflow(double flow, double tempC) {
        Double[] flowData = NPPMath.mixSteam(steamInflow, steamInflowTemperature, flow, tempC);
        steamInflow = flowData[0];
        steamInflowTemperature = flowData[1];
    }

    @Override
    public void updateWaterOutflow(double flow, double tempC) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateWaterOutFlow'");
    }

    @Override
    public void updateWaterInflow(double flow, double tempC) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateWaterInFlow'");
    }
    
    public float getRpm() {
        return rpm;
    }
    
    public float getGenAligmnent() {
        return genAlignment;
    }
    
    public boolean isTripped() {
        return tripped;
    }
    
    public double getSteamInflow() {
        return lastStepSteamInflow;
    }
    
    public double getGeneratorLoad() {
        return load;
    }

    public void forceSync() {
        rpm = 3000;
        synced = true;
    }
    
    public boolean isReversePower() {
        return reversePower;
    }

    private void reversePowerTrip() {
        if(!reversePowerRecorded) {
            if (isTG1) {
                autoControl.recordEvent("TG-1 Reverse Power");
            } else {
                autoControl.recordEvent("TG-2 Reverse Power");
            }
            reversePowerRecorded = true;
        }
        new Thread(() -> {
            try {
                Thread.sleep(7000); // if reverse power for 7 seconds trip turbine;
                if (reversePower) {
                    trip("Reverse Power");
                }
                reversePowerRecorded = false;
            } catch (InterruptedException e) {
                reversePowerRecorded = false;
            }
        }).start();
    }
    
    public void setTG1() {
        isTG1 = true;
    }

    @Override
    public double getWaterMass() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

class Condenser extends WaterSteamSubComponent implements Connectable, UIReadable, Serializable {
    protected final Pump condenserPump;
    private final double volume;
    private double feedwaterOutflow = 0.0, feedwaterOutflowRate = 0.0; // kg kg/s
    private double feedwaterMass = 366600, feedwaterTemperature = 20, feedwaterLevel, feedwaterVolume, nominalFeedwaterVolume;
    private double specificDensityFeedwater = Loader.tables.getWaterDensityByTemp(feedwaterTemperature);

    private final double volumeWaterSide = 116;
    private double waterMass = 0.0; 
    private double steamMass = 0.0; //initialSteamMass = mass equivalent of non-steam gasses
    double initialSteamMass;

    // calculated results from the update thread stored here for use in other functions:
    private double steamVolume;
    private double deltaEnergy = 0.0, deltaSteamEnergy = 0.0;
    private double condensationRate = 0.0; //per tick, multiply by 20 for rate/s 
    private double steamInflowTemperature = 20.0; // c
    private double steamDensity;

    public Condenser() {
        condenserPump = new Pump(750.0f, 5.14f, 5.0, 0, 40, 55, 1880, atmosphere, atmosphere);
        feedwaterVolume = feedwaterMass * specificDensityFeedwater;
        nominalFeedwaterVolume = feedwaterVolume;
        volume = feedwaterVolume * 2;
        steamVolume = volume - feedwaterVolume;
        steamMass = 0;
        initialSteamMass = steamVolume / 1.64718; // constant density for 20c
        waterMass = volumeWaterSide / Loader.tables.getWaterDensityByTemp(condenserPump.source.getWaterTemperature());
        steamDensity = steamVolume / steamMass;
    }

    public void update() {
        condenserPump.update();
        double condensedWaterMass = 0, deltaCondensedSteamMass, deltaSteamMass, potentialPressure;
        feedwaterTemperature -= (0.5 * feedwaterTemperature - 10) * 0.000005;

        double[] waterInflowData = NPPMath.mixWater(waterMass, waterTemperature, condenserPump.flow * 25, condenserPump.source.getWaterTemperature());
        waterMass = waterInflowData[0];
        waterTemperature = waterInflowData[1];

        if (initialSteamMass <= 0) {
            steamMass += initialSteamMass;
            initialSteamMass = 0;
            steamMass -= steamOutflow;
        } else {
            initialSteamMass -= steamOutflow;
        }

        if (pressure > 0.10142) {
            steamMass -= (pressure - 0.10142) * 6000; //atmospheric valves opened;
        }

        if (steamMass <= 0) {
            steamMass = 0;
        }

        Double steamInFlowData[] = NPPMath.mixSteam(steamMass, feedwaterTemperature, steamInflow, steamInflowTemperature);
        steamMass = steamInFlowData[0];
        steamTemperature = steamInFlowData[1];

        initialSteamMass += (steamInflow / 60 * 0.06 + (0.10142 - pressure)) / 20; //non-steam gasses from feedwater and leakage into the condenser
        feedwaterMass -= feedwaterOutflow;
        
        double condensedSteamEnergy = Loader.tables.getSteamEnthalpyByTemperature(steamTemperature) * steamMass;
        steamTemperature = waterTemperature * (1 + steamTemperature / waterTemperature / 30 * (1 - waterTemperature / steamTemperature) * 1.084337735) ; //TODO make this more realistic this uses constants to tweak cooling efficiency
        condensedSteamEnergy -= Loader.tables.getSteamEnthalpyByTemperature(steamTemperature) * steamMass;

        double steamEnergy = 0 - deltaSteamEnergy / 3;
        deltaSteamEnergy -= deltaSteamEnergy / 3;
        waterTemperature += ((condensedSteamEnergy + steamEnergy) / (NPPMath.calculateSpecificHeatWater(waterTemperature) * waterMass)); //test?

        potentialPressure = Loader.tables.getSteamPressureByTemp(steamTemperature);
        specificDensityFeedwater = Loader.tables.getWaterDensityByTemp(feedwaterTemperature);
        feedwaterVolume = feedwaterMass * specificDensityFeedwater;
        steamVolume = volume - feedwaterVolume;
        deltaCondensedSteamMass = steamVolume / Loader.tables.getSteamDensityByPressure(potentialPressure) - (steamMass + initialSteamMass);
        if (steamMass + deltaCondensedSteamMass < 0) {
            deltaCondensedSteamMass = 0 - steamMass;
        }

        deltaSteamEnergy += deltaCondensedSteamMass * Loader.tables.getSpecificVaporEnthalpyByTemperature(steamTemperature);
        condensedWaterMass -= deltaCondensedSteamMass;
        steamMass += deltaCondensedSteamMass;

        double oldFeedwaterMass = feedwaterMass;

        double[] feedwaterInflowData = NPPMath.mixWater(feedwaterMass, feedwaterTemperature, condensedWaterMass , steamTemperature);
        feedwaterMass = feedwaterInflowData[0];
        feedwaterTemperature = feedwaterInflowData[1];
        condensationRate = oldFeedwaterMass < feedwaterMass ? feedwaterMass - oldFeedwaterMass : 0; 

        double energy = 0 - deltaEnergy / 3;
        deltaEnergy -= deltaEnergy / 3;
        feedwaterTemperature += energy / (NPPMath.calculateSpecificHeatWater(feedwaterTemperature) * feedwaterMass); //boiling condensate

        potentialPressure = Loader.tables.getSteamPressureByTemp(feedwaterTemperature);
        if (potentialPressure > pressure) { //only let condensate boil if pressure < saturated but dont let steam condense if pressure > saturated
            specificDensityFeedwater = Loader.tables.getWaterDensityByTemp(feedwaterTemperature);
            feedwaterVolume = feedwaterMass * specificDensityFeedwater;
            steamVolume = volume - feedwaterVolume;
            deltaSteamMass = steamVolume / Loader.tables.getSteamDensityByPressure(potentialPressure) - (steamMass + initialSteamMass);

            if (steamMass + deltaSteamMass < 0) {
                deltaSteamMass = 0 - steamMass;
            }
            deltaEnergy += deltaSteamMass * Loader.tables.getSpecificVaporEnthalpyByTemperature(feedwaterTemperature);
            feedwaterMass -= deltaSteamMass;
            steamMass += deltaSteamMass; 
        }

        waterOutflow = waterMass * Loader.tables.getWaterDensityByTemp(waterTemperature) - volumeWaterSide;
        waterMass -= waterOutflow;

        specificDensityFeedwater = Loader.tables.getWaterDensityByTemp(feedwaterTemperature);
        feedwaterVolume = feedwaterMass * specificDensityFeedwater;
        feedwaterLevel = (feedwaterVolume / nominalFeedwaterVolume - 1) * 100;
        steamVolume = volume - feedwaterVolume;
        steamDensity = steamVolume / (steamMass + initialSteamMass);
        pressure = Loader.tables.getSteamPressureByDensity(steamDensity);
        feedwaterOutflowRate = feedwaterOutflow;
        feedwaterOutflow = 0;
        resetFlows();
        waterLevel = feedwaterLevel;
    }

    @Override
    public double getSteamDensity() {
        return steamDensity;
    }
    
    @Override 
    public double getWaterTemperature() {
        return feedwaterTemperature;
    }

    @Override
    public double getWaterDensity() {
        return specificDensityFeedwater;
    }

    @Override
    public double getSteamMass() {
        return steamMass;
    }

    @Override
    public double getSteamVolume() {
        return steamVolume;
    }

    @Override
    public void updateSteamOutflow(double flow, double tempC) {
        steamOutflow += flow;
    }

    @Override
    public void updateSteamInflow(double flow, double tempC) {
        Double[] flowData = NPPMath.mixSteam(steamInflow, steamInflowTemperature, flow, tempC);
        steamInflow = flowData[0];
        steamInflowTemperature = flowData[1];
    }

    @Override
    public void updateWaterOutflow(double flow, double tempC) {
        feedwaterOutflow += flow;
    }

    @Override
    public void updateWaterInflow(double flow, double tempC) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getWaterInflowRate'");
    }

    @Override
    public double getWaterOutflowRate() {
        return feedwaterOutflowRate * 20;
    }
    
    public double getCondenserWaterTemperature() {
        return waterTemperature;
    }
    
    public double getCondensationRate() {
        return condensationRate * 20;
    }

    @Override
    public double getWaterMass() {
        return waterMass;
    }
}


