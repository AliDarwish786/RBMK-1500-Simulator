package com.darwish.nppsim;

//classes which have a connectable as a constructor parameter go here

import static com.darwish.nppsim.NPPSim.atmosphere;
import static com.darwish.nppsim.NPPSim.mcc;

interface Connectable {
    // methods required for connectors
    double getPressure();
    double getSteamDensity();
    double getWaterDensity();
    double getSteamMass();
    double getWaterMass();
    double getSteamVolume();
    double getWaterTemperature();
    double getSteamTemperature();
    double getWaterLevel();

    // handle result
    void updateSteamOutflow(double flow, double tempC);
    void updateSteamInflow(double flow, double tempC);
    void updateWaterOutflow(double flow, double tempC);
    void updateWaterInflow(double flow, double tempC);
}

class WaterValve extends Component {
    private final double cV; // Cv cumber of valve
    private final double FL = 0.9;
    final Connectable source, drain;
    protected final float speed; // valve position travel per tick (50 ms)
    protected float position = 0.0f; // valve position, 0.0 is closed, 1.0 is open;
    protected int state = 1; // valve state, 0 is closing, 1 neutral, 2 opening
    protected int autoState = 1; //autoControl signal
    private double flow = 0.0;
    protected double timestepFlow = 0.0, waterTemp = 20.0; // current flow in kg/s. timeStepFlow = flow * 0.05

    /**
     * @param cV Cv number of valve
     * @param fullTravelSpeed opening/closure time in seconds
     */
    public WaterValve(double cV, float fullTravelSpeed, Connectable source, Connectable drain) {
        this.cV = cV;
        this.source = source;
        this.drain = drain;
        this.speed = 0.05f / fullTravelSpeed; // 0.05 = 50ms per tick
    }

    public void update() {
        position = NPPMath.updatePositionFromState(state, autoState, position, speed);
        waterTemp = source.getWaterTemperature();
        final double pVapor = Loader.tables.getSteamPressureByTemp(waterTemp);
        final double sG = 1.0 / Loader.tables.getWaterDensityByTemp(waterTemp) / 1000; //converts density in m3/kg to specific gravity
        final double p1 = source.getPressure() * 1000;
        final double p2 = drain.getPressure() * 1000;
        final double FF = 0.96 - 0.28 * Math.sqrt(pVapor / 22120000.0); //calculates cricital pressure ratio factor
        
        if (p1 - p2 < Math.pow(FL, 2) * (p1 - FF * pVapor)) {
            flow = 0.0865 * cV * Math.sqrt((p1 - p2) / sG);
        } else {
            flow = 0.0865 * cV * FL  * Math.sqrt((p1 - FF * pVapor) / sG);
        }

        if (Double.isNaN(flow)) {
            flow = 0.0;
        }
        flow *= sG * 1000; //specific gravity to kg/m3 to get kg/h from m3/h
        flow /= 3600; // from kg/h to kg/s
        timestepFlow = flow * position * 0.05; // flow per time step: flow in kg/h / 3600 * 0.05
        if (source.getWaterMass() < timestepFlow) {
            timestepFlow = source.getWaterMass();
        }
        source.updateWaterOutflow(timestepFlow, waterTemp);
        drain.updateWaterInflow(timestepFlow, waterTemp);
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setAutoState(int state) {
        this.autoState = state;
    }

    public void setPosition(float position) {
        this.position = position;
    }
    
    public float getPosition() {
        return position;
    }
    
    public int getState() {
        return state;
    }
}

class SteamValve extends Component {
    private final double cV; // Cv cumber of valve
    final Connectable source, drain;
    private final float speed; // valve position travel per tick (50 ms)
    private float position = 0.0f; // valve position, 0.0 is closed, 1.0 is open;
    private int state = 1; // valve state, 0 is closing, 1 neutral, 2 opening
    private int autoState = 1; //autoControl signal
    protected double flow = 0.0, flowRate = 0.0, steamTemp = 20.0; // current flow in kg/s. timeStepFlow = flow * 0.05
    private boolean locked = false;
    
    /**
     * @param cV Cv number of valve
     * @param fullTravelSpeed opening/closure time in seconds
     */
    public SteamValve(double cV, float fullTravelSpeed, Connectable source, Connectable drain) {
        this.cV = cV;
        this.source = source;
        this.drain = drain;
        this.speed = 0.05f / fullTravelSpeed; // 0.05 = 50ms per tick
    }

    public void update() {
        if (locked) {
            state = 1;
            autoState = 0;
        }
        position = NPPMath.updatePositionFromState(state, autoState, position, speed);

        double p1 = source.getPressure() * 1000;
        double p2 = drain.getPressure() * 1000;
        double fY = 1.3 / 1.4;
        double pC = (p1 - p2) / p1;
        double p = 1.0 / source.getSteamDensity();
        double X = 0.72;

        if (pC < fY * X) {
            flow = 2.73 * cV * (1 - (pC / (3 * fY * X))) * Math.sqrt((p1 - p2) * p);
        } else {
            flow = 0.66 * 2.73 * cV * Math.sqrt(fY * X * p1 * p);
        }

        if (Double.isNaN(flow)) {
            flow = 0.0;
        }
        flow /= 3600; // from kg/h to kg/s
        flow *= position * 0.05; // flow per time step: flow in kg/h / 3600 * 0.05
        flowRate = flow;
        steamTemp = source.getSteamTemperature();
        source.updateSteamOutflow(flow, steamTemp);
        drain.updateSteamInflow(flow, steamTemp);
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setAutoState(int state) {
        this.autoState = state;
    }

    public void setPosition(float position) {
        this.position = position;
    }

    public float getPosition() {
        return position;
    }

    public double getFlowRate() {
        return flowRate * 20;
    }
    
    public int getState() {
        return state;
    }

    public int getAutoState() {
        return autoState;
    }

    public void precisionAdjustment(boolean opening) {
        if (locked) {
            return;
        }
        if (opening) {
            position += 0.001f;
        } else {
            position -= 0.001f;
        }
        if (position > 1) {
            position = 1;
        } else if (position < 0) {
            position = 0;
        }
    }
    
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    public boolean isLocked() {
        return locked;
    }
}

class Pump extends Component { //TODO will need refactoring after water flow gets more realistic
    WaterValve dischargeValve = new WaterValve(200, 20, atmosphere, atmosphere); //dummy valve 
    protected final float ratedRPM; // max rated rpm
    protected final float ratedFlow; // flow at max rpm m3/s
    protected final float maxPowerUsage; // at full rpm, A
    protected final float accelerationSpeed; // rpm increase per tick
    protected final float decelerationSpeed; // rpm decrease per tick
    protected final double head, npshr;
    protected float rpm = 0.0f, rpmSetting = 1.0f; // current rpm in percentage of max rmp, setting as fraction of max
    private float oilPressure = 0.0f;
    protected float powerUsage = 0.0f; // A
    protected double currentHead = 0; //current head as determined by rpm
    protected double npsha = 0;
    protected double flow = 0.0, flowRate = 0.0;// flow in kg/s NOTE converted from m3/s, flow per timestep = flow * 0.05
    protected double timestepFlow = 0.0;
    protected double waterTemp = 20.0;
    protected boolean isCavitating = false;
    protected boolean active = false;
    protected Connectable source;
    protected Connectable drain;

    /**
     * @param ratedRPM         max rated rpm
     * @param ratedFlow        flow at max rpm m3/s
     * @param head             pump head
     * @param npshr            net positive suction head required
     * @param accelerationTime time for reach max rpm in s
     * @param decelerationTime time to full stop from max rpm in s
     * @param maxPowerUsage    rated power usage in kW
     */
    public Pump(float ratedRPM, float ratedFlow, double head, double npshr, int accelerationTime, int decelerationTime, float maxPowerUsage, Connectable source, Connectable drain) {
        this.ratedRPM = ratedRPM;
        this.ratedFlow = ratedFlow;
        accelerationSpeed = (0.05f / accelerationTime) * ratedRPM;
        decelerationSpeed = (0.05f / decelerationTime) * ratedRPM;
        this.maxPowerUsage = maxPowerUsage;
        this.source = source;
        this.drain = drain;
        this.head = head;
        this.npshr = npshr;
        //this.dischargeValve.setPosition(1.0f);
    }

    void update() {
        float setRPM = ratedRPM * rpmSetting;
        dischargeValve.update();
        waterTemp = source.getWaterTemperature();
        if (active) {
            float setPowerUsage = maxPowerUsage / 1.5f * rpmSetting; 
            powerUsage = setPowerUsage + ((1 - (rpm / setRPM)) * setPowerUsage * 0.5f); 
        } else {
            powerUsage = 0;
        }
        npsha = source.getPressure() - Loader.tables.getSteamPressureByTemp(waterTemp) + NPPMath.calculateFluidColumn(10, 1 / source.getWaterDensity());
        if (active) {
            if (rpm < setRPM - accelerationSpeed) {
                rpm += accelerationSpeed;
            } else if (rpm > setRPM + decelerationSpeed) {
                rpm -= decelerationSpeed;
            } else {
                rpm = setRPM;
            }
            isCavitating = npsha < npshr * (rpm / ratedRPM);
        } else {
            if (rpm != 0) {
                rpm -= decelerationSpeed;
                if (rpm < 0) {
                    rpm = 0;
                }
            }
            isCavitating = false;
        }
        currentHead = (Math.pow(rpm, 2) * (head / Math.pow(ratedRPM, 2)) + (rpm == 0 ? 0 : source.getPressure())) * dischargeValve.position;
        flow = ((rpm / ratedRPM) * ratedFlow) / source.getWaterDensity() * 0.05 * dischargeValve.position;
        if (Double.isNaN(flow)) {
            flow = 0.0;
        }
        if (Double.isNaN(timestepFlow)) {
            timestepFlow = 0.0;
        }
        if (source.getWaterMass() < timestepFlow) {
            timestepFlow = source.getWaterMass();
        }
        if (source.getWaterMass() < flow) {
            flow = source.getWaterMass();
        }
        source.updateWaterOutflow(timestepFlow, waterTemp);
        drain.updateWaterInflow(timestepFlow, waterTemp);
    }

    public void setActive(boolean active) {
        if (this.active && !active) {
            this.dischargeValve.setAutoState(0);
        } else if (!this.active && active) {
            this.dischargeValve.setAutoState(2);
        } else {
            this.dischargeValve.setAutoState(1);
        }
        this.active = active;
    }
    
    public void setRPM(float rpm) {
        this.rpm = rpm;
    };

    public boolean isActive() {
        return active;
    }

    public double getFlow() {
        return flow;
    }
    
    public double getFlowRate() {
        return timestepFlow * 20;
    }

    public float getRPM() {
        return rpm;
    }

    public float getPowerUsage() {
        return powerUsage;
    }

    public double getHead() {
        return currentHead;
    }

    public void updateFlow(double flow) {
        timestepFlow = flow;
    }
    
    public void setSetPoint(float setPoint) {
        if (setPoint > 1.0f) {
            rpmSetting = 1.0f;
            return;
        } else if (setPoint < 0.0f) {
            rpmSetting = 0.0f;
            return;
        }
        rpmSetting = setPoint;
    }
    
    public float getSetPoint() {
        return rpmSetting;
    }
}

class MCCPump extends Pump { //TODO will need refactoring after MCC water flow gets more realistic
    MCC.MCPPressureHeader drain;
    double bypassFlow = 0.0;

    public MCCPump(float ratedRPM, float ratedFlow, double npshr, int accelerationTime, int decelerationTime, float maxPowerUsage, Connectable source, MCC.MCPPressureHeader drain) {
        super(ratedRPM, ratedFlow, 0, npshr, accelerationTime, decelerationTime, maxPowerUsage, source, drain);
        this.drain = drain;
        this.rpmSetting = 0.0f;
    }

    @Override
    void update() {
        waterTemp = source.getWaterTemperature();
        float setRPM = ratedRPM * rpmSetting;
        dischargeValve.update();
        if (active) {
            float setPowerUsage = maxPowerUsage / 1.5f * rpmSetting; 
            powerUsage = setPowerUsage + ((1 - (rpm / setRPM)) * setPowerUsage * 0.5f); 
        } else {
            powerUsage = 0;
        }
        npsha = source.getPressure() - Loader.tables.getSteamPressureByTemp(waterTemp) + NPPMath.calculateFluidColumn(25, 1 / source.getWaterDensity());
        if (active) {
            if (rpm < setRPM - accelerationSpeed) {
                rpm += accelerationSpeed;
            } else if (rpm > setRPM + decelerationSpeed) {
                rpm -= decelerationSpeed;
            } else {
                rpm = setRPM;
            }
            isCavitating = npsha < npshr * (rpm / ratedRPM);
        } else {
            if (rpm != 0) {
                rpm -= decelerationSpeed;
                if (rpm < 0) {
                    rpm = 0;
                }
            }
            isCavitating = false;
        }
        flow = ((rpm / ratedRPM) * ratedFlow) / source.getWaterDensity() * dischargeValve.position;

        if (drain.getBypassState()) {   //if bypasses are open calculate thermal driving head and derive natural ciculation flow
            drain.drains.forEach(channel -> {
                double tHead = NPPMath.calculateThermalDrivingHead(1 / source.getWaterDensity(), (1 / channel.getWaterDensity()), 14.1) / 1000; 
                double dVisc = NPPMath.calculateDynamicviscosity(source.getWaterTemperature());
                bypassFlow += source.getWaterDensity() * NPPMath.calculateVolumeFlowRate(1 + tHead, 1, 0.025, 7, dVisc) * dischargeValve.position;
            });
            if (bypassFlow > flow) {
                flow = bypassFlow;
            }
        }
        timestepFlow = flow * 0.05;
        source.updateWaterOutflow(timestepFlow, waterTemp);
        drain.updateWaterInflow(timestepFlow, waterTemp);
        bypassFlow = 0;
    }
    
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
}

/**
 * This is a special type of header where drains can be supplied by multiple sources without generating flow between sources
 **/
class OneWaySteamHeader extends WaterSteamComponent implements Connectable, UIReadable {
    private double steamDensity = Loader.tables.getSteamDensityByPressure(pressure);
    Connectable[] sources;

    public OneWaySteamHeader(Connectable[] sources) {
        this.sources = sources;
    }

    public void update() {
        double highestPressure = 0;
        double pressureSum = 0;
        for (int i = 0; i < sources.length; i++) {
            double sourcePressure = sources[i].getPressure();
            pressureSum += sourcePressure;
            highestPressure = sourcePressure > highestPressure ? sourcePressure : highestPressure;
        }
        pressure = highestPressure;
        for (int i = 0; i < sources.length; i++) {
            Connectable thisSource = sources[i];
            double steamMass = 0;
            double sourceOutFlow = thisSource.getPressure() / pressureSum * steamOutflow;
            double sourceSteamTemp = thisSource.getSteamTemperature();
            thisSource.updateSteamOutflow(sourceOutFlow, sourceSteamTemp);
            Double[] inflowData = NPPMath.mixSteam(steamMass, steamTemperature, sourceOutFlow, sourceSteamTemp);
            steamMass = inflowData[0];
            steamTemperature = inflowData[1];
            steamDensity = Loader.tables.getSteamDensityByPressure(pressure);
        }
        resetFlows();
    }

    @Override
    public double getWaterLevel() {
        throw new UnsupportedOperationException("Unimplemented method 'getWaterLevel'");
    }

    @Override
    public double getSteamDensity() {
        return steamDensity;
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
    public void updateSteamOutflow(double flow, double tempC) {
        steamOutflow += flow;
    }

    @Override
    public void updateSteamInflow(double flow, double tempC) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateSteamInFlow'");
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

    @Override
    public double getWaterMass() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

class Ejector extends SteamValve {
    final Connectable ejectorSource, ejectorDrain;
    double ejectorFlow = 0.0, ejectorFlowRate = 0.0;

    public Ejector(double cV, float fullTravelSpeed, Connectable source, Connectable drain, Connectable ejectorSource, Connectable ejectorDrain) {
        super(cV, fullTravelSpeed, source, drain);
        this.ejectorDrain = ejectorDrain;
        this.ejectorSource = ejectorSource;
    }

    @Override
    public void update() {
        super.update();
        ejectorFlow = flow / 0.05275 * 0.003 * (ejectorSource.getPressure() / 0.00414); //0.06 / 20 = 0.003; 0.00414 = nominal vacuum in MPa
        this.ejectorSource.updateSteamOutflow(ejectorFlow, ejectorSource.getSteamTemperature());
        this.ejectorDrain.updateSteamInflow(ejectorFlow, source.getSteamTemperature());
        ejectorFlowRate = ejectorFlow;
    }
    
    public double getEjectorFlowRate() {
        return ejectorFlowRate * 20;
    }
    
}

/**
 * a heat exchanger which is at the pressure side for both inlets
 * NOTE: there are 2 inflow sources using the same Connectable Interface. 
 * Make sure the method UpdateWaterInflow is called by the sources in the following order:
 * source1 for shell side
 * source2 for tube side
 * a Boolean value is flipped each time the method is called to process the 2 inputs
 */
class WaterWaterHeatExchanger extends WaterSteamComponent implements Connectable {
    private final float efficiency;
    private final double ratedFlow1, ratedFlow2;
    private double waterInflowTemperature1 = 20.0, waterOutflowTemperature1 = 20.0, waterMass1 = 0.0;
    private double waterInflowTemperature2 = 20.0, waterOutflowTemperature2 = 20.0, waterMass2 = 0.0;
    private boolean source1 = true;
    Connectable drain1, drain2;
    
    /**
     * @param efficiency in %
     * @param ratedFlow1 rated flow in kg/s for shell side;
     * @param ratedFlow2 rated flow in kg/s for tube side;
     */
    public WaterWaterHeatExchanger(float efficiency, double ratedFlow1, double ratedFlow2, Connectable drain1, Connectable drain2) {
        this.drain1 = drain1;
        this.drain2 = drain2;
        this.efficiency = efficiency / 100;
        this.ratedFlow1 = ratedFlow1 / 20; //to flow per timestep
        this.ratedFlow2 = ratedFlow2 / 20;
    }
    
    public void update() {
        final float flowratio1 = (float)(waterMass1 / ratedFlow1);
        final float flowRatio2 = (float)(waterMass2 / ratedFlow2);
        float effectiveFlowRatio = flowratio1 <= flowRatio2 ? flowRatio2 / flowratio1 : flowratio1 / flowRatio2;
        if (Double.isNaN(effectiveFlowRatio)) {
            effectiveFlowRatio = 1;
        }
        final float realEfficiency = 1 - ((1 - efficiency) / effectiveFlowRatio);

        final double specificHeat = NPPMath.calculateSpecificHeatWater((waterInflowTemperature1 + waterInflowTemperature2) / 2);
        final double lowestWaterMass = waterMass1 < waterMass2 ? waterMass1 : waterMass2;
        final double transferredEnergy = (waterInflowTemperature1 - waterInflowTemperature2) * specificHeat * lowestWaterMass * realEfficiency;

        final double temperature1 = waterInflowTemperature1 - (transferredEnergy / (specificHeat * waterMass1));
        final double temperature2 = waterInflowTemperature2 + (transferredEnergy / (specificHeat * waterMass2));

        waterOutflowTemperature1 = Double.isNaN(temperature1) ? waterOutflowTemperature1 - ((waterOutflowTemperature1 - waterOutflowTemperature2) * 0.0001) : temperature1;
        waterOutflowTemperature2 = Double.isNaN(temperature2) ? waterOutflowTemperature2 - ((waterOutflowTemperature2 - waterOutflowTemperature1) * 0.0001) : temperature2;

        drain1.updateWaterInflow(waterMass1, waterOutflowTemperature1);
        drain2.updateWaterInflow(waterMass2, waterOutflowTemperature2);
        
    }

    public void updateWaterInFlow1(double flow, double tempC) {
        waterInflowTemperature1 = tempC;
        waterMass1 = flow;
    }

    public void updateWaterInFlow2(double flow, double tempC) {
        waterInflowTemperature2 = tempC;
        waterMass2 = flow;
    }
    
    public double getWaterInflow1Temp() {
        return waterInflowTemperature1;
    }
    
    public double getWaterInflow2Temp() {
        return waterInflowTemperature2;
    }
    
    public double getWaterOutflow1Temp() {
        return waterOutflowTemperature1;
    }
    
    public double getWaterOutflow2Temp() {
        return waterOutflowTemperature2;
    }
    
    public double getWaterFlowRate1() {
        return waterMass1 * 20;
    }
    
    public double getWaterFlowRate2() {
        return waterMass2 * 20;
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
    public void updateWaterInflow(double flow, double tempC) {
        if (source1) {
            updateWaterInFlow1(flow, tempC);
        } else {
            updateWaterInFlow2(flow, tempC);
        }
        source1 = !source1;
    }

    @Override
    public double getWaterMass() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

class pcsMockupValve extends WaterValve { //will be obsolete when mcp is reworked

    public pcsMockupValve(Connectable source, Connectable drain) {
        super(200, 10, source, drain);
        this.setPosition(1.0f);
    } 

    @Override
    public void update() {
        double flow1 = 0, flow2 = 0;
        if (state != 1) {
            switch (state) {
                case 0: {
                    if (position == 0.0) {
                        break;
                    }
                    position -= speed;
                    if (position < 0.0) {
                        position = 0.0f;
                    }
                    break;
                }
                case 2: {
                    if (position == 1.0) {
                        break;
                    }
                    position += speed;
                    if (position > 1.0) {
                        position = 1.0f;
                    }
                    break;
                }
            }
        } else {
            switch (autoState) {
                case 0: {
                    if (position == 0.0) {
                        break;
                    }
                    position -= speed;
                    if (position < 0.0) {
                        position = 0.0f;
                    }
                    break;
                }
                case 2: {
                    if (position == 1.0) {
                        break;
                    }
                    position += speed;
                    if (position > 1.0) {
                        position = 1.0f;
                    }
                    break;
                }
            }
        }
        
        for (int i = 0; i < 4; i++) {
            double flow = mcc.mcp.get(i).getFlowRate();
            if (flow > flow1) {
                flow1 = flow;
            }
        }
        for (int i = 4; i < 8; i++) {
            double flow = mcc.mcp.get(i).getFlowRate();
            if (flow > flow2) {
                flow2 = flow;
            }
        }
        timestepFlow = (flow1 + flow2) / 2 / 1750 * 5.55 * position;
        source.updateWaterOutflow(timestepFlow, source.getWaterTemperature());
        drain.updateWaterInflow(timestepFlow, source.getWaterTemperature());
    }
    
}

class SimplePump extends Pump { //will become obsolete soon
    WaterValve outletValve;
    public SimplePump(float ratedRPM, float ratedFlow, double head, double npshr, int accelerationTime, int decelerationTime,float maxPowerUsage, Connectable source, Connectable drain) {
        super(ratedRPM, ratedFlow, head, npshr, accelerationTime, decelerationTime, maxPowerUsage, source, drain);
        outletValve = new WaterValve(10, 10, atmosphere, atmosphere); //dummy valve
        outletValve.setPosition(1.0f);
    }

    @Override public void update() {
        outletValve.update();
        waterTemp = source.getWaterTemperature();
        float setRPM = ratedRPM * rpmSetting;
        if (active) {
            float setPowerUsage = maxPowerUsage / 1.5f * rpmSetting; 
            powerUsage = setPowerUsage + ((1 - (rpm / setRPM)) * setPowerUsage * 0.5f); 
        } else {
            powerUsage = 0;
        }
        npsha = source.getPressure() - Loader.tables.getSteamPressureByTemp(waterTemp);
        if (active) {
            if (rpm != ratedRPM) {
                rpm += accelerationSpeed;
                if (rpm > ratedRPM) {
                    rpm = ratedRPM;
                }
            }
            isCavitating = npsha < npshr * (rpm / ratedRPM);
        } else {
            if (rpm != 0) {
                rpm -= decelerationSpeed;
                if (rpm < 0) {
                    rpm = 0;
                }
            }
            isCavitating = false;
        }
        flow = ((rpm / ratedRPM) * ratedFlow) / source.getWaterDensity();
        timestepFlow = flow * 0.05 * outletValve.getPosition();
        if (source.getWaterMass() < timestepFlow) {
            timestepFlow = source.getWaterMass();
        }
        source.updateWaterOutflow(timestepFlow, waterTemp);
        drain.updateWaterInflow(timestepFlow, waterTemp);
    }

}

