package com.darwish.nppsim;

import static com.darwish.nppsim.NPPSim.atmosphere;
import static com.darwish.nppsim.NPPSim.tg1;
import static com.darwish.nppsim.NPPSim.tg2;
import java.util.ArrayList;

/**
 * This is a pressure header where one or multiple pumps supply one or multiple components
 **/
class PressureHeader extends Component implements Connectable, UIReadable { //TODO will need refactoring after water flow gets more realistic
    private double pressure = 0.10142, zeroHeadFlow = 0, zeroFlowHead = 0; 
    private double waterOutflow = 0, waterOutflowRate = 0;
    protected double waterTemperature = 20.0, waterMass = 0.0;
    private double waterDensity = Loader.tables.getWaterDensityByTemp(waterTemperature);
    Pump[] sources;

    public PressureHeader(Pump[] sources) {
        this.sources = sources;
    }

    public PressureHeader() {};
    
    public void update() {
        double highestPressure = 0;
        double pressureSum = 0;
        double zeroHeadFlowSum = 0;
        for (Pump thisSource : sources) {
            double sourcePressure = thisSource.getHead();
            pressureSum += sourcePressure;
            zeroHeadFlowSum += thisSource.getFlow();
            highestPressure = sourcePressure > highestPressure ? sourcePressure : highestPressure;
        }
        zeroFlowHead = highestPressure;
        zeroHeadFlow = zeroHeadFlowSum * 5; //make sure the pumps have some excess flow this will be changed later
        for (Pump thisSource : sources) {
            double sourceOutflow = thisSource.getHead() / pressureSum * waterOutflow;
            if (pressureSum == 0) {
                sourceOutflow = 0;
            }
            thisSource.updateFlow(sourceOutflow);
        }
        waterDensity = Loader.tables.getWaterDensityByTemp(waterTemperature);
        if (waterOutflow > zeroHeadFlow) {
            waterOutflow = zeroHeadFlow;
        }
        Double calculatedPressure = (1 - (waterOutflow / zeroHeadFlow)) * zeroFlowHead + 0.10142;
        if (calculatedPressure.isNaN()) {
            calculatedPressure = 0.10142;
        }
        pressure = (pressure * 50 + calculatedPressure) / 51; //gradually change pressure to dampen fluctuations
        waterOutflowRate = waterOutflow;
        waterOutflow = 0;
        waterMass = 0;
    }

    public void setSources(Pump[] sources) {
        this.sources = sources;
    }

    @Override
    public double getWaterLevel() {
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
    public double getWaterOutflowRate() {
        return waterOutflowRate * 20;
    }

    @Override
    public double getWaterInflowRate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getWaterInflowRate'");
    }

    @Override
    public void resetFlowRates() {
        waterOutflowRate = 0;
    }

    @Override
    public double getPressure() {
        return pressure;
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
    public double getWaterTemperature() {
        return waterTemperature;
    }

    @Override
    public double getSteamTemperature() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSteamTemperature'");
    }

    @Override
    public void updateSteamOutflow(double flow, double tempC) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateSteamOutFlow'");
    }

    @Override
    public void updateSteamInflow(double flow, double tempC) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateSteamInFlow'");
    }

    @Override
    public void updateWaterOutflow(double flow, double tempC) {
        waterOutflow += flow;
        waterOutflowRate += flow;
    }

    @Override
    public void updateWaterInflow(double flow, double tempC) {
        double[] inflowData = NPPMath.mixWater(waterMass, waterTemperature, flow, tempC);
            waterMass = inflowData[0];
            waterTemperature = inflowData[1];
    }
}

class SimplifiedCondensateHeader extends PressureHeader { //greatly simplified feedwater heater for the sake of the early alpha release
    private double thermalPower = 0.0;
    private double heatedWaterTemp = 20.0;
    
    @Override
    public void update() {
        double oldWaterTemp = waterTemperature;
        double tg1SteamTemp = tg1.getSteamTemperature() / 1.45;
        double tg2SteamTemp = tg2.getSteamTemperature() / 1.45;
        double highestSteamTemp = tg1SteamTemp < tg2SteamTemp ? tg2SteamTemp : tg1SteamTemp;
        thermalPower += (tg1.getSteamInflow() / 600.0) * 10000 * (tg1SteamTemp / 196.82) * ((tg1SteamTemp - waterTemperature) / 170.0);
        thermalPower += (tg2.getSteamInflow() / 600.0) * 10000 * (tg2SteamTemp / 196.82) * ((tg2SteamTemp - waterTemperature) / 170.0);
        double deltaWaterTemp = thermalPower * 50 / (NPPMath.calculateSpecificHeatWater(waterTemperature) * waterMass);
        waterTemperature += Double.isNaN(deltaWaterTemp) || Double.isInfinite(deltaWaterTemp) ? 0.0 : deltaWaterTemp;
        if (waterTemperature > highestSteamTemp && waterTemperature > oldWaterTemp) {
            waterTemperature = highestSteamTemp * 0.9;
        }
        heatedWaterTemp = waterTemperature;
        thermalPower = 0.0;
        super.update();
    }

    @Override 
    public double getWaterTemperature() {
        return heatedWaterTemp;
    }
}

/**
 * a pressure header where drain.waterInflow is determined by this.waterInflow
 * drains can be isolated by their respective valve in isolationValveArray
 */
class SimplePressureHeader extends Component implements Connectable, UIReadable {
    protected double pressure = 0.10142, waterInflow= 0, waterInflowRate = 0, waterTemperature = 20, waterInflowTemperature = 20, waterDensity = Loader.tables.getWaterDensityByTemp(waterTemperature);
    private float totalValvePositions = 0.0f;
    double initialWaterMass;
    Connectable[] drains;
    ArrayList<WaterValve> isolationValveArray = new ArrayList<>();

    public SimplePressureHeader(Connectable[] drains, double volume) {
        this.drains = drains;
        for (Connectable drain: drains) {
            isolationValveArray.add(new WaterValve(100, 10, atmosphere, atmosphere)); //dummy valves just used for their position property
        }
        initialWaterMass = volume / Loader.tables.getWaterDensityByTemp(20);
    }

    public void update() {
        double waterMass = initialWaterMass;
        double[] inflowData = NPPMath.mixWater(waterMass, waterTemperature, waterInflow, waterInflowTemperature);
        waterMass = inflowData[0];
        waterTemperature = inflowData[1];
        
        isolationValveArray.forEach(valve -> {
            valve.update();
            totalValvePositions += valve.getPosition();
        });
        double highestPressure = 0;
        for (Connectable drain : drains) {
            double drainPressure = drain.getPressure();
            highestPressure = drainPressure > highestPressure ? drainPressure : highestPressure;
        }
        pressure = highestPressure;
        for (int i = 0; i < drains.length; i++) {
            Connectable thisDrain = drains[i];
            double drainInflow = (double)(isolationValveArray.get(i).getPosition() / totalValvePositions) * waterInflow; //waterInflow / sources.length;
            thisDrain.updateWaterInflow(drainInflow, waterTemperature);
        }
        waterInflow = 0;
        totalValvePositions = 0.0f;
    }

    @Override
    public double getWaterLevel() {
        throw new UnsupportedOperationException("Unimplemented method 'getWaterLevel'");
    }

    @Override
    public double getPressure() {
        return pressure;
    }

    @Override
    public double getSteamDensity() {
        throw new UnsupportedOperationException("Unimplemented method 'getSteamDensity'");
    }

    @Override
    public double getWaterDensity() {
        return waterDensity;
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
    public double getWaterTemperature() {
        return waterTemperature;
    }

    @Override
    public double getSteamTemperature() {
        throw new UnsupportedOperationException("Unimplemented method 'getSteamTemperature'");
    }

    @Override
    public void updateSteamOutflow(double flow, double tempC) {
        throw new UnsupportedOperationException("Unimplemented method 'updateSteamOutflow'");
    }

    @Override
    public void updateSteamInflow(double flow, double tempC) {
        throw new UnsupportedOperationException("Unimplemented method 'updateSteamInFlow'");
    }

    @Override
    public void updateWaterOutflow(double flow, double tempC) {
        throw new UnsupportedOperationException("Unimplemented method 'updateWaterOutflow'");
    }

    @Override
    public void updateWaterInflow(double flow, double tempC) {
        double[] inflowData = NPPMath.mixWater(waterInflow, waterInflowTemperature, flow, tempC);
        waterInflow = inflowData[0];
        waterInflowTemperature = inflowData[1];
        waterInflowRate += waterInflow;
    }

    @Override
    public double getSteamInflowRate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSteamInflowRate'");
    }

    @Override
    public double getSteamOutflowRate() {
        throw new UnsupportedOperationException("Unimplemented method 'getSteamOutflowRate'");
    }

    @Override
    public double getWaterOutflowRate() {
        throw new UnsupportedOperationException("Unimplemented method 'getWaterOutflowRate'");
    }

    @Override
    public double getWaterInflowRate() {
        return waterInflowRate;
    }

    @Override
    public void resetFlowRates() {
        waterInflowRate = 0;
    }

    public void setIsolationValveState(int valveIndex, int state) {
        isolationValveArray.get(valveIndex).setState(state);
    }
}

/**
 * a suction header where source.waterOutflow is determined by this.waterOutflow
 * sources can be isolated by their respective valve in isolationValveArray
 */
class SimpleSuctionHeader extends Component implements Connectable, UIReadable {
    protected double pressure = 0.10142, waterOutflow = 0, waterOutflowRate = 0, waterTemperature = 20, waterDensity = Loader.tables.getWaterDensityByTemp(waterTemperature);
    private float totalValvePositions = 0.0f;
    double initialWaterMass;
    Connectable[] sources;
    ArrayList<WaterValve> isolationValveArray = new ArrayList<>();

    public SimpleSuctionHeader(Connectable[] sources, double volume) {
        this.sources = sources;
        for (Connectable source: sources) {
            isolationValveArray.add(new WaterValve(100, 10, atmosphere, atmosphere)); //dummy valves just used for their position property
        }
        initialWaterMass = volume / Loader.tables.getWaterDensityByTemp(20);
    }

    public void update() {
        double waterMass = initialWaterMass;
        isolationValveArray.forEach(valve -> {
            valve.update();
            totalValvePositions += valve.getPosition();
        });
        double highestPressure = 0;
        for (Connectable source : sources) {
            double sourcePressure = source.getPressure();
            highestPressure = sourcePressure > highestPressure ? sourcePressure : highestPressure;
        }
        pressure = highestPressure;
        for (int i = 0; i < sources.length; i++) {
            Connectable thisSource = sources[i];
            Double sourceOutFlow = (double)(isolationValveArray.get(i).getPosition() / totalValvePositions) * waterOutflow; //waterOutflow / sources.length;
            if (sourceOutFlow.isNaN()) {
                sourceOutFlow = 0.0;
            }
            double sourceWaterTemp = thisSource.getWaterTemperature();
            thisSource.updateWaterOutflow(sourceOutFlow, sourceWaterTemp);
            double[] inflowData = NPPMath.mixWater(waterMass, waterTemperature, sourceOutFlow, sourceWaterTemp);
            waterMass = inflowData[0];
            waterTemperature = inflowData[1];
            waterDensity = Loader.tables.getWaterDensityByTemp(waterTemperature);
        }
        waterOutflow = 0;
        totalValvePositions = 0.0f;
    }

    @Override
    public double getWaterLevel() {
        throw new UnsupportedOperationException("Unimplemented method 'getWaterLevel'");
    }

    @Override
    public double getPressure() {
        return pressure;
    }

    @Override
    public double getSteamDensity() {
        throw new UnsupportedOperationException("Unimplemented method 'getSteamDensity'");
    }

    @Override
    public double getWaterDensity() {
        return waterDensity;
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
    public double getWaterTemperature() {
        return waterTemperature;
    }

    @Override
    public double getSteamTemperature() {
        throw new UnsupportedOperationException("Unimplemented method 'getSteamTemperature'");
    }

    @Override
    public void updateSteamOutflow(double flow, double tempC) {
        throw new UnsupportedOperationException("Unimplemented method 'updateSteamOutflow'");
    }

    @Override
    public void updateSteamInflow(double flow, double tempC) {
        throw new UnsupportedOperationException("Unimplemented method 'updateSteamInFlow'");
    }

    @Override
    public void updateWaterOutflow(double flow, double tempC) {
        waterOutflow += flow;
        waterOutflowRate += flow;
    }

    @Override
    public void updateWaterInflow(double flow, double tempC) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateWaterInFlow'");
    }

    @Override
    public double getSteamInflowRate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSteamInflowRate'");
    }

    @Override
    public double getSteamOutflowRate() {
        throw new UnsupportedOperationException("Unimplemented method 'getSteamOutflowRate'");
    }

    @Override
    public double getWaterOutflowRate() {
        return waterOutflowRate;
    }

    @Override
    public double getWaterInflowRate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getWaterInflowRate'");
    }

    @Override
    public void resetFlowRates() {
        waterOutflowRate = 0;
    }

    public void setIsolationValveState(int valveIndex, int state) {
        isolationValveArray.get(valveIndex).setState(state);
    }
}

/**
*  A water component where multiple inputs are combined into a single output. pressure = drain.pressure
**/
class WaterMixer extends Component implements Connectable, UIReadable {
    private double waterInflowRate = 0.0;
    protected double waterInflow = 0.0;
    protected double waterTemperature = 20.0;
    Connectable drain;
        
    public WaterMixer(Connectable drain) {
        this.drain = drain;
    }
    
    public void update() {
        drain.updateWaterInflow(waterInflow, waterTemperature);
        waterInflow = 0;
    }
    
    @Override
    public void updateWaterInflow(double flow, double tempC) {
        double[] inflowData = NPPMath.mixWater(waterInflow, waterTemperature, flow, tempC);
        waterInflow = inflowData[0];
        waterTemperature = inflowData[1];
        waterInflowRate += flow;
    }
    
    @Override
    public void resetFlowRates() {
        waterInflowRate = 0;
    }
    
    @Override
    public double getWaterInflowRate() {
        return waterInflowRate;
    }

    @Override
    public double getWaterLevel() {
        throw new UnsupportedOperationException("Unimplemented method 'getWaterLevel'");
    }

    @Override
    public double getPressure() {
        return drain.getPressure();
    }

    @Override
    public double getSteamDensity() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double getWaterDensity() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double getSteamMass() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double getSteamVolume() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double getWaterTemperature() {
        return waterTemperature;
    }

    @Override
    public double getSteamTemperature() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateSteamOutflow(double flow, double tempC) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateSteamInflow(double flow, double tempC) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateWaterOutflow(double flow, double tempC) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
    public double getWaterOutflowRate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getWaterOutflowRate'");
    }
    
}

class Tank extends Component implements Connectable, UIReadable {
    private final double nominalWaterVolume;
    private double waterInflow = 0.0, waterInflowRate = 0.0; // kg, kg/s
    private double waterOutflow = 0.0, waterOutflowRate = 0.0; // kg, kg/s
    private double waterInflowTemperature = 20.0; // c
    private double waterLevel;
    private double waterVolume, waterMass;
    private double pressure = 0.10142; // pressure in Mpa
    private double waterTemperature = 20.0;
    
    public Tank(double nominalWaterVolume) {
        this.nominalWaterVolume = nominalWaterVolume;
        waterVolume = nominalWaterVolume; 
        waterMass = waterVolume / Loader.tables.getWaterDensityByTemp(waterTemperature);
        waterLevel = (waterVolume / nominalWaterVolume - 1) * 100;
    }

    public void update() {
        double[] waterInflowData = NPPMath.mixWater(waterMass, waterTemperature, waterInflow, waterInflowTemperature);
        waterTemperature = waterInflowData[1];
        waterMass = waterInflowData[0];
        waterMass -= waterOutflow;
        waterVolume = waterMass * Loader.tables.getWaterDensityByTemp(waterTemperature);
        waterLevel = (waterVolume / nominalWaterVolume - 1) * 100;

        waterInflow = 0;
        waterOutflow = 0;
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
    public double getWaterOutflowRate() {
        return waterOutflowRate;
    }

    @Override
    public double getWaterInflowRate() {
        return waterInflowRate;
    }

    @Override
    public void resetFlowRates() {
        waterInflowRate = 0;
        waterOutflowRate = 0;
    }

    @Override
    public double getPressure() {
        return pressure;
    }

    @Override
    public double getSteamDensity() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSteamDensity'");
    }

    @Override
    public double getWaterDensity() {
        return Loader.tables.getWaterDensityByTemp(waterTemperature);
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
    public double getWaterTemperature() {
        return waterTemperature;
    }

    @Override
    public double getSteamTemperature() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSteamTemperature'");
    }

    @Override
    public void updateSteamOutflow(double flow, double tempC) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateSteamOutflow'");
    }

    @Override
    public void updateSteamInflow(double flow, double tempC) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateSteamInflow'");
    }

    @Override
    public void updateWaterOutflow(double flow, double tempC) {
        waterOutflow += flow;
        waterOutflowRate += flow;
    }

    @Override
    public void updateWaterInflow(double flow, double tempC) {
        double[] waterInflowData = NPPMath.mixWater(waterInflow, waterTemperature, flow, tempC);
        waterInflowTemperature = waterInflowData[1];
        waterInflow = waterInflowData[0];
        waterInflowRate += flow;
    }

    @Override
    public double getWaterLevel() {
        return waterLevel;
    }
}