package com.darwish.nppsim;

import static com.darwish.nppsim.NPPSim.core;
import static com.darwish.nppsim.NPPSim.mcc;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MCC extends Component {
    final SeparatorDrum drum1, drum2;
    final MCPPressureHeader pHeader1, pHeader2;
    final ArrayList<Pump> mcp = new ArrayList<>(); // 0-3 loop 1, 4-7 loop 2
    final ArrayList<FuelChannel> fuelChannels1 = new ArrayList<>();
    final ArrayList<FuelChannel> fuelChannels2 = new ArrayList<>();
    final ArrayList<SteamWaterPipe> steamWaterPipes1 = new ArrayList<>();
    final ArrayList<SteamWaterPipe> steamWaterPipes2 = new ArrayList<>();

    public MCC() {
        drum1 = new SeparatorDrum();
        drum2 = new SeparatorDrum();

        for (ArrayList<Channel> x: core.coreArray) {
            for (int i = 0; i < x.size(); i++) {
                if (x.get(i) instanceof FuelChannel) {
                    if (i < 28) {
                        fuelChannels1.add((FuelChannel)x.get(i));
                    } else {
                        fuelChannels2.add((FuelChannel)x.get(i));
                    }
                }
            }
        }
        fuelChannels1.forEach(channel -> {
            steamWaterPipes1.add(new SteamWaterPipe(channel, drum1));
        });
        fuelChannels2.forEach(channel -> {
            steamWaterPipes2.add(new SteamWaterPipe(channel, drum2));
        });

        pHeader1 = new MCPPressureHeader(fuelChannels1);
        pHeader2 = new MCPPressureHeader(fuelChannels2);
        // add the eight MCP's, 4 per loop
        for (int i = 0; i < 4; i++) {
            mcp.add(new MCCPump(1000, 2.2222f, 18, 200, 5600, drum1, pHeader1));
        }
        for (int i = 0; i < 4; i++) {
            mcp.add(new MCCPump(1000, 2.2222f, 18, 200, 5600, drum2, pHeader2));
        }
    }

    protected class SeparatorDrum implements Connectable, UIReadable, Serializable {
        private final double volume = 682.35;
        private final double nominalWaterVolume;
        private double initialSteamMass; // this would be air in reality
        private double steamMass = 0.0; // kg
        private double waterMass = 305837.26; // 306.4 m3 at 20C
        private double pressure = 0.10142; // pressure in Mpa
        private double waterTemperature = 20.0, steamTemperature = 20.0;
        private double waterLevel = 0.0;
        private double steamProduction = 0.0; // kg/s
        private double thermalLoss = 0.0; // conductive energy loss Mwt

        // calculated results from the update thread stored here for use in other
        // functions:
        private double steamDensity;
        private double specificHeatWater = NPPMath.calculateSpecificHeatWater(waterTemperature);
        private double specificVaporEnthalpy = Loader.tables.getSpecificVaporEnthalpyByTemperature(waterTemperature);// NPPMath.calculateSpecificVaporEnthalpy(waterTemperature);
        private double boilingPoint = Loader.tables.getSteamTemperatureByPressure(pressure);
        private double specificDensityWater = Loader.tables.getWaterDensityByTemp(waterTemperature);
        private double waterVolume;
        private double steamVolume;
        private double deltaEnergy = 0.0; // surplus/deficit energy from last time step in kJ
        private double waterInflow = 0.0, waterInflowRate = 0.0; // kg, kg/s
        private double waterInflowTemperature = 20.0; // c
        private double waterOutflow = 0.0, waterOutflowRate = 0.0; // kg, kg/s
        private double steamInflow = 0.0, steamInflowRate = 0.0; // kg, kg/s
        private double steamOutflow = 0.0, steamOutflowRate = 0.0; // kg, kg/s
        private double steamInflowTemperature = 20.0; // c

        public SeparatorDrum() {
            waterVolume = waterMass * specificDensityWater;
            steamVolume = volume - waterVolume;
            nominalWaterVolume = waterVolume;
            initialSteamMass = steamVolume / 1.6718; // constant density for 20c
            steamDensity = steamVolume / (steamMass + initialSteamMass);
        }

        void update() {
            double vacuumBreakerFlow = (pressure - 0.10142) * 20;
            waterMass -= waterOutflow;
            if (waterInflow < 0) {
                waterMass += waterInflow;
            } else {
                double[] inflowData = NPPMath.mixWater(waterMass, waterTemperature, waterInflow, waterInflowTemperature);
                waterMass = inflowData[0];
                waterTemperature = inflowData[1];
            }

            specificHeatWater = NPPMath.calculateSpecificHeatWater(waterTemperature);
            specificVaporEnthalpy = Loader.tables.getSpecificVaporEnthalpyByTemperature(waterTemperature);
            specificDensityWater = Loader.tables.getWaterDensityByTemp(waterTemperature);

            steamMass -= steamOutflow;
            if (steamInflow < 0) {
                steamMass += steamInflow;
            } else {
                steamMass += steamInflow;
                double oldWaterTemp = waterTemperature;
                waterTemperature += ((Loader.tables.getSteamEnthalpyByTemperature(steamInflowTemperature) - Loader.tables.getSteamEnthalpyByTemperature(waterTemperature)) * steamInflow) / (specificHeatWater * waterMass);
                specificHeatWater = NPPMath.calculateSpecificHeatWater(waterTemperature);
                deltaEnergy += (Loader.tables.getSteamEnthalpyByTemperature(waterTemperature) - Loader.tables.getSteamEnthalpyByTemperature(oldWaterTemp)) * steamInflow; //correct for difference in new steamTemp and new waterTemp after energy transfer
            }

            steamTemperature = waterTemperature;

            if (steamMass <= 0) {
                steamMass = 0;
                initialSteamMass -= steamOutflow;
            }
            if (pressure < 0.10142) {
                initialSteamMass -= vacuumBreakerFlow;
            }
            
            double energy = 0 - deltaEnergy / 3 - thermalLoss * 50;
            deltaEnergy -= deltaEnergy / 3;

            waterTemperature += (energy / (specificHeatWater * waterMass));
            final double potentialPressure = Loader.tables.getSteamPressureByTemp(waterTemperature);
            specificDensityWater = Loader.tables.getWaterDensityByTemp(waterTemperature);
            waterVolume = waterMass * specificDensityWater;
            steamVolume = volume - waterVolume;
            double deltaSteamMass = steamVolume / Loader.tables.getSteamDensityByPressure(potentialPressure) - (steamMass + initialSteamMass);
            if (steamMass + deltaSteamMass < 0) {
                deltaSteamMass = 0 - steamMass;
            }

            specificVaporEnthalpy = Loader.tables.getSpecificVaporEnthalpyByTemperature(waterTemperature);
            deltaEnergy += deltaSteamMass * specificVaporEnthalpy;

            steamProduction += deltaSteamMass + steamInflow;

            waterMass -= deltaSteamMass;
            steamMass += deltaSteamMass;

            if (steamMass < 0) {
                steamMass = 0;
            }

            specificDensityWater = Loader.tables.getWaterDensityByTemp(waterTemperature);
            waterVolume = waterMass * specificDensityWater;
            waterLevel = (waterVolume / nominalWaterVolume - 1) * 100;
            steamVolume = volume - waterVolume;
            steamDensity = steamVolume / (steamMass + initialSteamMass);
            pressure = Loader.tables.getSteamPressureByDensity(steamDensity);
            boilingPoint = Loader.tables.getSteamTemperatureByPressure(pressure);
            steamOutflow = 0; // reset lostSteam for next timeStep
            steamInflow = 0;
            waterInflow = 0;
            waterOutflow = 0;
        }

        @Override
        public double getPressure() {
            return pressure;
        }

        @Override
        public double getSteamDensity() {
            return steamDensity;
        }

        @Override
        public void updateSteamInflow(double flow, double tempC) {
            double[] inflowData = NPPMath.mixWater(steamInflow, steamInflowTemperature, flow, tempC);
            steamInflow = inflowData[0];
            steamInflowTemperature = inflowData[1];
        }

        @Override
        public void updateSteamOutflow(double flow, double tempC) {
            steamOutflow += flow;
            steamOutflowRate += flow;
        }

        @Override
        public void updateWaterInflow(double flow, double tempC) {
            if (flow < 0) {
                waterInflow += flow;
            } else {
                double[] inflowData = NPPMath.mixWater(waterInflow, waterInflowTemperature, flow, tempC);
                waterInflow = inflowData[0];
                waterInflowTemperature = inflowData[1];
            }
            waterInflowRate += flow;
        }

        @Override
        public void updateWaterOutflow(double flow, double tempC) {
            waterOutflow += flow;
            waterOutflowRate += flow;
        }

        @Override
        public void resetFlowRates() {
            steamProduction = 0;
            steamOutflowRate = 0;
            steamInflowRate = 0;
            waterInflowRate = 0;
            waterOutflowRate = 0;
        }

        @Override
        public double getWaterTemperature() {
            return waterTemperature;
        }

        public double getWaterMass() {
            return waterMass;
        }

        @Override
        public double getWaterLevel() {
            return waterLevel;
        }

        public double getBoilingPoint() {
            return boilingPoint;
        }

        public double getSteamProduction() {
            return steamProduction;
        }

        @Override
        public double getSteamOutflowRate() {
            return steamOutflowRate;
        }

        @Override
        public double getSteamMass() {
            return steamMass;
        }

        @Override
        public double getWaterDensity() {
            return specificDensityWater;
        }

        public double getWaterOutflow() {
            return waterOutflow;
        }

        @Override
        public double getSteamVolume() {
            return steamVolume;
        }

        @Override
        public double getSteamTemperature() {
            return steamTemperature;
        }

        @Override
        public double getWaterOutflowRate() {
            return waterOutflowRate;
        }

        @Override
        public double getWaterInflowRate() {
            return waterInflowRate;
        }

        @Override
        public double getSteamInflowRate() {
            // TODO Auto-generated method stub
            return 0;
        }
        
        public double getInitialSteamMass() {
            return initialSteamMass;
        }

        public void setSteamPressure(double press) {
            pressure = press;
        }
    }
    
    protected class SteamWaterPipe implements Serializable {
    private final FuelChannel source;
    private final Connectable drain;
    private double steamFlow = 0.0, steamTemp = 0.0;
    private double waterFlow = 0.0, waterTemp = 0.0;

    SteamWaterPipe(FuelChannel source, Connectable drain) {
        this.source = source;
        this.drain = drain;
        source.setDrain(drain);
    }

    public void update() {
        if (source.getSteamVolume() == 0) {
            steamFlow = source.getSteamMass();
        } else {
            double ratio = source.getSteamVolume() / (source.getSteamVolume() + drain.getSteamVolume());
            double totalMass = source.getSteamMass() + drain.getSteamMass();
            steamFlow = source.getSteamMass() - (ratio * totalMass);
        }
        if (steamFlow >= 0) {
            steamTemp = source.getSteamTemperature();
        } else {
            steamTemp = drain.getSteamTemperature();
        }
        waterFlow = source.getWaterOutflow();
        if (waterFlow < 0) { // reverse flow will need drain watertemp, otherwise source watertemp
            waterTemp = drain.getWaterTemperature();
        } else {
            waterTemp = source.getWaterTemperature();
        }
        // source waterOutFlow is not updated here as source does that by itself
        if (Double.isNaN(steamFlow)) {
            steamFlow = 0;
        }
        source.updateSteamOutflow(steamFlow, steamTemp);
        drain.updateSteamInflow(steamFlow, steamTemp);
        drain.updateWaterInflow(waterFlow, waterTemp);
    }
}

    protected class MCPPressureHeader implements Connectable, UIReadable, Serializable {
        final List<FuelChannel> drains;
        private final double volume = 121.5; 
        private double waterMass, waterVolume;
        private double waterTemperature = 20.0;
        private double waterInflow = 0.0, waterInflowRate = 0.0; // kg kg/s
        private double waterInflowTemperature = 0.0; // C
        private double waterOutflow = 0.0, waterOutflowRate = 0.0; // kg, kg/s
        private double specificDensityWater = Loader.tables.getWaterDensityByTemp(waterTemperature);
        private boolean bypassesOpen = true;

        MCPPressureHeader(List<FuelChannel> drains) {
            this.drains = drains;
            this.waterMass = volume / Loader.tables.getWaterDensityByTemp(waterTemperature);
            this.waterVolume = volume;
        }

        public void update() {
            waterMass -= waterOutflow;
            waterOutflowRate += waterOutflow;
            double[] waterInflowData = NPPMath.mixWater(waterMass, waterTemperature, waterInflow, waterInflowTemperature);
            waterTemperature = waterInflowData[1];
            waterMass = waterInflowData[0];
            //waterTemperature -= (0.5 * waterTemperature - 10) * 0.0000004; //TODO
            specificDensityWater = Loader.tables.getWaterDensityByTemp(waterTemperature);
            waterVolume = specificDensityWater * waterMass;
            waterOutflow = (waterVolume - volume) / specificDensityWater;
            waterOutflowRate += waterOutflow;
            if (waterOutflow < 0) {
                if (drains.size() == 835) {
                    double[] inflowData = NPPMath.mixWater(waterMass, waterTemperature, 0 - waterOutflow, mcc.drum1.getWaterTemperature());
                    waterMass = inflowData[0];
                    waterTemperature = inflowData[1];
                    mcc.drum1.updateWaterOutflow(0 - waterOutflow, mcc.drum1.getWaterTemperature());
                } else {
                    double[] inflowData = NPPMath.mixWater(waterMass, waterTemperature, 0 - waterOutflow, mcc.drum2.getWaterTemperature());
                    waterMass = inflowData[0];
                    waterTemperature = inflowData[1];
                    mcc.drum2.updateWaterOutflow(0 - waterOutflow, mcc.drum2.getWaterTemperature());
                }
            } else {
                waterMass -= waterOutflow;
                double flowPerDrain = waterOutflow / drains.size();
                for (Connectable i: drains) {
                    i.updateWaterInflow(flowPerDrain, waterTemperature);
                }
            }
            waterVolume = specificDensityWater * waterMass;
            waterInflow = 0; //reset for next timestep
            waterOutflow = 0;
        }

        @Override
        public void resetFlowRates() {
            waterOutflowRate = 0;
        }

        @Override
        public double getPressure() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public double getSteamDensity() {
            // TODO Auto-generated method stub
            return 0;
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
        public double getWaterTemperature() {
            return waterTemperature;
        }

        public double getWaterOutflow() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public double getWaterOutflowRate() {
            return waterOutflowRate;
        }

        @Override
        public double getSteamTemperature() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void updateSteamOutflow(double flow, double tempC) {
            // TODO Auto-generated method stub

        }

        @Override
        public void updateSteamInflow(double flow, double tempC) {
            // TODO Auto-generated method stub

        }

        @Override
        public void updateWaterOutflow(double flow, double tempC) {
            waterOutflow += flow;
        }

        @Override
        public void updateWaterInflow(double flow, double tempC) {
            waterInflow += flow;
            waterInflowTemperature = tempC; //tempC will be equal for all mcp's

        }

        @Override
        public double getWaterLevel() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getWaterLevel'");
        }

        @Override
        public double getSteamInflowRate() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getSteamInflowRate'");
        }

        @Override
        public double getSteamOutflowRate() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getSteamOutflowRate'");
        }

        @Override
        public double getWaterInflowRate() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getWaterInflowRate'");
        }

        public void setBypassState(boolean open) {
            bypassesOpen = open;
        }

        public boolean getBypassState() {
            return bypassesOpen;
        }
    }

    public void update() {
        
        core.coreArray.forEach(row -> {
            row.forEach(channel -> {
                channel.update();
            });
        });
        
        for (SteamWaterPipe i: steamWaterPipes1) {
            i.update();
        }
        for (SteamWaterPipe i: steamWaterPipes2) {
            i.update();
        }
        drum1.update();
        drum2.update();
        for (Pump i : mcp) {
            i.update();
        }
        pHeader1.update();
        pHeader2.update();
    }

    public void setThermalPower(double thermalPower) {
        final double perChannel = thermalPower / (fuelChannels1.size() + fuelChannels2.size());
        for (FuelChannel i: fuelChannels1) {
            i.setThermalPower(perChannel);
        }
        for (FuelChannel i: fuelChannels2) {
            i.setThermalPower(perChannel);
        }
    }

    public void setWaterTemp(double tempC) {
        double saturatedPress = Loader.tables.getSteamPressureByTemp(tempC);
        for (FuelChannel i: fuelChannels1) {
            i.setWaterTemp(tempC);
            i.setSteamPressure(saturatedPress);
        }
        for (FuelChannel i: fuelChannels2) {
            i.setWaterTemp(tempC);
            i.setSteamPressure(saturatedPress);
        }
        drum1.waterTemperature = tempC;
        drum1.setSteamPressure(saturatedPress);
        drum2.waterTemperature = tempC;
        drum2.setSteamPressure(saturatedPress);
        pHeader1.waterTemperature = tempC;
        pHeader2.waterTemperature = tempC;
    }

}
