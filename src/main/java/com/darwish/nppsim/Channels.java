package com.darwish.nppsim;

import static com.darwish.nppsim.NPPSim.autoControl;
import java.awt.Color;
import java.io.Serializable;

interface ChannelUIUpdatable {
    void updateTableValues();
}

class ChannelUIData implements Serializable {
    protected String positionString = "00-00";
    protected String name = "Channel";
    protected java.awt.Color UIBackgroundColor = new Color(235, 235, 160), UISelectedColor = Color.WHITE;
    protected java.awt.Color UISelsynColor = new Color(245, 245, 160);
    protected Object tableData[][];
    protected static java.text.DecimalFormat sf = new java.text.DecimalFormat("0.####E0");

    public void setTableData(Object[][] tableData) {
        this.tableData = tableData;
    }

    public void setCoreMapColors(Color background, Color selected) {
        this.UIBackgroundColor = background;
        this.UISelectedColor = selected;
    }
    
    public void setSelsynColors(Color selsyn) {
        this.UISelsynColor = selsyn;
    }

    public void setName(String name) {
        this.name = name;
    }
}

abstract class Channel implements ChannelUIUpdatable, Serializable {
    protected ChannelUIData uiData = new ChannelUIData();
    private final Double  initialNeutronCount = 1.0;
    private Double fastNeutronCount = 1.0, thermalNeutronCount = 0.0; //thermalNeutronCount is not used for anything yet
    protected double thermalFissionFactor = 1, thermalUtilizationFactor = 1, resonanceEscapeProb = 0.87, fastFissionFactor = 1, fastNonLeakageProb = 0.97, thermalNonLeakageProb = 0.99;

    public void update() {
        
    }
    
    public void updateNeutronPopulation() {
        fastNeutronCount *= thermalFissionFactor * fastNonLeakageProb * resonanceEscapeProb * thermalNonLeakageProb * thermalUtilizationFactor;
    }

    public void resetFlowRates() {
    };

    public ChannelUIData getUIData() {
        return uiData;
    }
    
    public void setPositionString(String positionString) {
        this.uiData.positionString = positionString;
    }

    /**
     * 
     * @return array of {fastNeutronCount, thermalNeutronCount}
     */
    public Double[] getNeutronPopulation() {
        return new Double[] {fastNeutronCount, thermalNeutronCount};
    }

    public void setNeutronPopulation(Double[] neutronPopulation) {
        fastNeutronCount = neutronPopulation[0];
        thermalNeutronCount = neutronPopulation[1];
        if (fastNeutronCount < initialNeutronCount) {
            fastNeutronCount = initialNeutronCount;
        }
    }
}

abstract class CPSChannel extends Channel {
    protected double waterTemperature = 0.0;
    protected double waterInflowTemperature = 0.0;
    protected double waterInflowRate = 0.0;
    protected double pressure = 0.0;
    
    @Override
    public void updateTableValues() {
        uiData.setTableData(new Object[][] {
                { "Type:", uiData.name, "" },
                { "Count:", ChannelUIData.sf.format(getNeutronPopulation()[0]), "" },
//                { "Pressure:", NPPMath.round(pressure), "Mpa" },
//                { "Water Inflow:", NPPMath.round(waterInflowRate), "kg/s" },
//                { "Inlet Temperature:", NPPMath.round(waterInflowTemperature), "C" },
//                { "Outlet Temperature:", NPPMath.round(waterTemperature), "C" }
        });
    }
}

abstract class ControlRodChannel extends CPSChannel {
    private float position = 1.0f;
    protected float speed = 0.0028571429f;
    protected float scramSpeed = 0.0038461538f;
    protected float fastScramSpeed = scramSpeed;
    private int state = 1, autoState = 1;
    private boolean scram = false, fastScram = false;
    
    @Override
    public void updateTableValues() {
        uiData.setTableData(new Object[][] {
                { "Type:", uiData.name, "" },
                { "Position: ", NPPMath.round(position * 7), "m" },
//                { "Pressure:", NPPMath.round(pressure), "Mpa" },
//                { "Water Inflow:", NPPMath.round(waterInflowRate), "kg/s" },
//                { "Inlet Temperature:", NPPMath.round(waterInflowTemperature), "C" },
//                { "Outlet Temperature:", NPPMath.round(waterTemperature), "C" }
        });
    }
    
    @Override
    public void update() {
        if (scram) {
            if (fastScram) {
                position = NPPMath.updatePositionFromState(2, 2, position, fastScramSpeed);
            } else {
                position = NPPMath.updatePositionFromState(2, 2, position, scramSpeed);
            }
        } else {
            position = NPPMath.updatePositionFromState(state, autoState, position, speed);
        }
        thermalUtilizationFactor = 1.0 - position;
    }
    
    public float getPosition() {
        return position;
    }

    public void setPosition(float position) {
        this.position = position;
    }
    
    public void setState(int state) {
        this.state = state;
    }
    
    public void setAutoState(int autoState) {
        this.autoState = autoState;
    }
    
    public void setScram(boolean scram) {
        this.scram = scram;
    }
    
    public void setFastScram(boolean scram) {
        this.fastScram = scram;
    }
}

class FissionChamber extends CPSChannel {
    public FissionChamber() {
        uiData.setName("Fission Chamber");
    }

}

class PDMSChannel extends CPSChannel {
    public PDMSChannel() {
        uiData.setName("PDMS-A Channel");
    }
}

class MCRChannel extends ControlRodChannel {
    public MCRChannel() {
        uiData.setCoreMapColors(Color.GRAY.brighter(), Color.GRAY.brighter().brighter());
        uiData.setName("Manual Control Rod");

    }
}

class ACRChannel extends MCRChannel {
    public ACRChannel() {
        speed = 0.0014285714f;
        uiData.setName("Automatic Control Rod Group 1");
        uiData.setSelsynColors(Color.GREEN.darker().darker());
        uiData.setCoreMapColors(Color.GREEN.darker().darker(), Color.GREEN.darker().brighter());
    }
}
class LEPChannel extends MCRChannel {
    public LEPChannel() {
        uiData.setName("Local Emergency Protection Rod");
        uiData.setCoreMapColors(new Color(200, 73, 73), Color.PINK);
    }
}

class MCRMChannel extends ControlRodChannel {
    public MCRMChannel() {
        uiData.setName("Manual Control Rod Modernized");
        uiData.setCoreMapColors(Color.GRAY.brighter(), Color.GRAY.brighter().brighter());
    }
}

class LACChannel extends MCRMChannel {
    public LACChannel() {
        speed = 0.0014285714f;
        uiData.setName("Local Automatic Control Rod");
        uiData.setSelsynColors(Color.BLUE);
        uiData.setCoreMapColors(new Color(30, 30, 255), Color.CYAN);
    }
}

class FASRChannel extends ControlRodChannel {
    public FASRChannel() {
        speed = 0.0041071429f;
        scramSpeed = 0.0082142857f;
        fastScramSpeed = 0.02f;
        uiData.setName("Fast-Acting Scram Rod");
        uiData.setSelsynColors(Color.RED);
        uiData.setCoreMapColors(Color.RED, Color.PINK);
    }
}

class SARChannel extends ControlRodChannel {
    public SARChannel() {
        uiData.setName("Shortened Absorber Rod");
        uiData.setSelsynColors(Color.ORANGE);
        uiData.setCoreMapColors(Color.ORANGE, Color.ORANGE.brighter());
    }
}

class SACRChannel extends SARChannel {
    public SACRChannel() {
        uiData.setName("Automatic Control Rod Group 2");
        uiData.setSelsynColors(Color.GREEN.darker());
        uiData.setCoreMapColors(Color.GREEN.darker().darker(), Color.GREEN.darker().brighter());
    }
}

class VoidChannel extends Channel {

    @Override
    public void updateTableValues() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'upateTableValues'");
    }

}

class ReflectorChannel extends Channel {
    
    public ReflectorChannel() {
        resonanceEscapeProb = 0.95;
    }
    @Override
    public void updateTableValues() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'upateTableValues'");
    }

}

class ReflectorCoolingChannel extends ReflectorChannel {

    public ReflectorCoolingChannel() {
        super();
    }
    @Override
    public void updateTableValues() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'upateTableValues'");
    }

}

class FuelChannel extends Channel implements Connectable, UIReadable {

    private Connectable drum;
    private final double volume = 0.2194;
    private double waterMass;
    private double waterTemperature = 20.0, steamTemperature = 20.0; // temperature in c
    private double thermalPower = 0.0; // thermal power in MWt
    private double pressure = 0.10142; // pressure in Mpa
    private double steamMass = 0.0; // kg
    private double waterLevel = 0.0; // percentage above or below nominal
    private double thermalLoss = 0.0; // conductive energy loss for the primary circuit Mwt
    private double voidFraction = 0.0; // % of mass steam in coolant loop

    // calculated results from the update thread stored here for use in other
    // functions:
    private double specificHeatWater = NPPMath.calculateSpecificHeatWater(waterTemperature);
    private double specificVaporEnthalpy = Loader.tables.getSpecificVaporEnthalpyByTemperature(waterTemperature);// NPPMath.calculateSpecificVaporEnthalpy(feedwaterTemperature);
    private double boilingPoint = Loader.tables.getSteamTemperatureByPressure(pressure);
    private double specificDensityWater = Loader.tables.getWaterDensityByTemp(waterTemperature);
    private double waterVolume;
    private double steamVolume, steamDensity;
    private double waterInflow = 0.0, waterInflowRate = 0.0; // kg kg/s
    private double waterInflowTemperature = 20.0; // C
    private double waterOutflow = 0.0, waterOutflowRate = 0.0; // kg, kg/s
    private double steamOutFlow = 0.0, steamOutflowRate = 0.0; // kg, kg/s
    private double steamProduction = 0.0; // kg/s
    private double averagePower = 0.0; //average power history for simplified decay heat calculation
    private double tElapsed = 0.0; //elapsed time since reaction stopped
    private double xeThermalUtilizationModifier = 0.0;
    private long tPower = 0; //total time for average power
    private double i135Count = 0, xe135Count = 0; //atoms per channel
    private final double nominalFeedWaterVolume;
    private final double resonanceEscapeProbInitial, thermalUtilizationFactorInitial;
    
    private final double I135DECAY_MULTIPLIER = NPPMath.calculateDecayMultiplierPerUpdate(6.57);
    private final double XE135DECAY_MULTIPLIER = NPPMath.calculateDecayMultiplierPerUpdate(9.14);

    public FuelChannel() {
        uiData.setName("Fuel Channel");
        waterMass = volume / Loader.tables.getWaterDensityByTemp(waterTemperature);
        waterVolume = waterMass * specificDensityWater;
        steamVolume = 0.0;
        nominalFeedWaterVolume = waterVolume;
        steamDensity = 0.0;
        thermalFissionFactor = 1.735;
        resonanceEscapeProbInitial = resonanceEscapeProb;
        thermalUtilizationFactor = 0.75;
        thermalUtilizationFactorInitial = thermalUtilizationFactor;
    }

    @Override
    public void update() {
        //waterTemperature -= (0.5 * waterTemperature - 10) * 0.0000001; //TODO thermal loss
        double[] waterInflowData = NPPMath.mixWater(waterMass, waterTemperature, waterInflow, waterInflowTemperature);
        waterTemperature = waterInflowData[1];
        waterMass = waterInflowData[0];

        specificHeatWater = NPPMath.calculateSpecificHeatWater(waterTemperature);
        specificVaporEnthalpy = Loader.tables.getSpecificVaporEnthalpyByTemperature(waterTemperature);
        specificDensityWater = Loader.tables.getWaterDensityByTemp(waterTemperature);
        pressure = drum.getPressure();
        boilingPoint = Loader.tables.getSteamTemperatureByPressure(pressure);

        thermalLoss = 0;

        double energy = thermalPower * 50 - thermalLoss * 50;
        double deltaSteamMass;
        waterTemperature += (energy / (specificHeatWater * waterMass));
        specificHeatWater = NPPMath.calculateSpecificHeatWater(waterTemperature);
        specificVaporEnthalpy = Loader.tables.getSpecificVaporEnthalpyByTemperature(waterTemperature);
        if (waterTemperature > boilingPoint) {
            deltaSteamMass = ((waterTemperature - boilingPoint) * specificHeatWater * waterMass) / specificVaporEnthalpy;
            waterTemperature = boilingPoint;
            steamTemperature = boilingPoint;

            steamProduction += deltaSteamMass;
            steamMass += deltaSteamMass;
            waterMass -= deltaSteamMass;
        }
        
        specificDensityWater = Loader.tables.getWaterDensityByTemp(waterTemperature);
        waterVolume = specificDensityWater * waterMass;
        waterOutflow = (waterVolume - volume) / specificDensityWater;
        if (waterOutflow < 0) {
            // TODO
            waterOutflow /= thermalPower == 0 ? 1 : thermalPower * 1000;
        }

        if (waterOutflow < 0) {
            double[] reverseFlowData = NPPMath.mixWater(waterMass, waterTemperature, 0 - waterOutflow,
                    drum.getWaterTemperature());
            waterMass = reverseFlowData[0];
            waterTemperature = reverseFlowData[1];
        } else {
            waterMass -= waterOutflow;
        }

        specificDensityWater = Loader.tables.getWaterDensityByTemp(waterTemperature);
        waterVolume = specificDensityWater * waterMass;
        double flow = waterOutflow < 0 ? 0 - waterOutflow : waterOutflow;
        voidFraction = steamMass / (flow + steamMass + 0.00000000000001); //crude way of preventing divide by 0
        steamVolume = volume - waterVolume < 0 ? 0 : volume - waterVolume;
        steamDensity = steamVolume / steamMass;
        waterLevel = (waterVolume / nominalFeedWaterVolume - 1) * 100;
        waterInflowRate = waterInflow;
        waterOutflowRate = waterOutflow;
        steamOutflowRate = steamOutFlow;
        steamOutFlow = 0; // reset lostSteam for next timeStep
        waterInflow = 0;

        resonanceEscapeProb = resonanceEscapeProbInitial - thermalPower / 4.25 * 0.03 - (waterTemperature / 300 * 0.005); //greatly simplified for simple core model
        resonanceEscapeProb -= (voidFraction * 0.04);
        thermalUtilizationFactor = (thermalUtilizationFactorInitial - xeThermalUtilizationModifier) + voidFraction * 0.077;
        thermalUtilizationFactor += 0.025 - Loader.tables.getWaterDensityByTemp(20) / Loader.tables.getWaterDensityByTemp(waterTemperature) * 0.025;
        thermalPower = (this.getNeutronPopulation()[0] / 29986861831.1868724665) * 2.8898254064; // simple mapping of neutron count to thermal power per channel for 4800 MWt
        
        if (thermalPower > 5) {
            autoControl.az1Control.trip("High Fuel Channel Power");
        }
        
        //decay heat
        
//        if (thermalPower < 0.000001) {
//            thermalPower = 0;
//            tElapsed += 0.05;
//        } 
//        else if (tElapsed > 0) {
//            tElapsed -= 0.05;
//        }
        if (thermalPower < 0.000001) {
            thermalPower = 0;
        } 
        if (thermalPower < averagePower) {
            tElapsed += 1;
        } else {
            tElapsed = 1;
        }
        
        if (autoControl.getTimeUpdatedFlag()) {     //update average power every second
            long simTime = autoControl.getSimulationTime() > 432000 ? 432000 : autoControl.getSimulationTime(); //consider last 5 days only
            averagePower = ((simTime - 1) * averagePower + thermalPower) / simTime;
            tPower = simTime;
        }
        //double multiplicationFactor = tElapsed <= 1 ? 1 : (Math.pow(tElapsed, -0.2) - Math.pow(tPower + tElapsed, -0.2));
        //thermalPower += 0.066 * averagePower * multiplicationFactor; //add decay heat
        double baseDecay = 0.066 * averagePower * (Math.pow(1, -0.2) - Math.pow(tPower + 1, -0.2));
        double realDecay = 0.066 * (averagePower - thermalPower) * (Math.pow(tElapsed, -0.2) - Math.pow(tPower + tElapsed, -0.2));
        thermalPower += baseDecay + realDecay;

        //xenon
        //macroscopic constants for simple core model:
        //3001050 fission  235
        //507870 capture   235
        //495929 capture   238
        i135Count += 3001050 * this.getNeutronPopulation()[0] * 0.063 * 0.05;//* 0.05; //generate 0.063 atoms of I135 per fission event
        xe135Count += i135Count * (1 - I135DECAY_MULTIPLIER);
        i135Count *= I135DECAY_MULTIPLIER;
        xe135Count *= XE135DECAY_MULTIPLIER;
        xe135Count -= 2000000e-24 * xe135Count * 1661 * this.getNeutronPopulation()[0] * 0.05;// * 0.05;
        if (xe135Count < 0) {
            xe135Count = 0;
        }
        if (i135Count < 0) {
            i135Count = 0;
        }
        //xe135Count = 4.6979e19;//1.0521e20; nominal, max xenon pit from 4800 to 0
        xeThermalUtilizationModifier = (0.876167866 - (3508920.0 / (495929.0 + 3508920.0 + (xe135Count * 1661 * 2000000e-24)))) * 0.9;
    }

    public void setDrain(Connectable drain) {
        drum = drain;
    }

    @Override
    public double getPressure() {
        return pressure;
    }

    @Override
    public double getSteamDensity() {
        return steamDensity;
    }

    public double getWaterMass() {
        return waterMass;
    }

    @Override
    public double getWaterLevel() {
        return waterLevel;
    }

    @Override
    public double getSteamOutflowRate() {
        return steamOutflowRate * 20;
    }

    @Override
    public double getWaterTemperature() {
        return waterTemperature;
    }

    public double getBoilingPoint() {
        return boilingPoint;
    }

    @Override
    public double getWaterDensity() {
        return specificDensityWater;
    }

    public double getWaterOutflow() {
        return waterOutflow;
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
    public void updateSteamInflow(double flow, double tempC) {
        // if this happens we have bigger problems

    }

    @Override
    public void updateWaterOutflow(double flow, double tempC) {
        // unnecassary at this point
    }

    @Override
    public void updateSteamOutflow(double flow, double tempC) {
        steamOutFlow = flow;
        // exception to where usually this is done in the update method rather than
        // called by an external object
        steamMass -= steamOutFlow;
    }

    @Override
    public void updateWaterInflow(double flow, double tempC) {
        waterInflow = flow;
        waterInflowTemperature = tempC;
    }

    @Override
    public double getSteamTemperature() {
        return steamTemperature;
    }

    @Override
    public double getWaterOutflowRate() {
        return waterOutflowRate * 20;
    }

    @Override
    public double getWaterInflowRate() {
        return waterInflowRate * 20;
    }

    public double getSteamProduction() {
        return steamProduction * 20;
    }

    @Override
    public void updateTableValues() {
        uiData.setTableData(new Object[][] {
                { "Type:", uiData.name, "" },
                { "Thermal Power:", NPPMath.round(thermalPower * 1000), "kW" },
                { "Pressure:", NPPMath.round(pressure), "Mpa" },
                { "Water Inflow:", NPPMath.round(waterInflowRate * 20), "kg/s" },
                { "Inlet Temperature:", NPPMath.round(waterInflowTemperature), "C" },
                { "Outlet Temperature:", NPPMath.round(waterTemperature), "C" },
                { "Voiding:", NPPMath.round(voidFraction * 100), "%" },
                { "I135:", NPPMath.round(i135Count / 1.93458087e20 * 100), "%",},
                { "Xe135:", NPPMath.round(xe135Count / 4.6978565e19 * 100), "%" }
        });
    }

    public double getVoidFraction() {
        return voidFraction;
    }

    public void setThermalPower(double power) {
        thermalPower = power;
    }

    public void setWaterTemp(double tempC) {
        waterTemperature = tempC;
    }

    public void setSteamPressure(double press) {
        pressure = press;
    }

    @Override
    public double getSteamInflowRate() {
        return 0;
    }
    
    public double getThermalPower() {
        return thermalPower;
    }
}
