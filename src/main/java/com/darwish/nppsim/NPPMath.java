package com.darwish.nppsim;

import static com.darwish.nppsim.Loader.tables;

public class NPPMath {
    /**
     * @return specific heat in kJ/kg
    */
    public static double calculateSpecificHeatWater(double tempC) {
        double vA = tempC;
        double result = 0.0;
        if (vA < 0) {
            return result;
        } else if (vA > 370) {
            return result;
        } else if (vA < 320) {
            result = (0.00000000000003537165 * vA * vA * vA * vA * vA * vA
                    - 0.00000000002853687405 * vA * vA * vA * vA * vA + 0.00000000900625896115 * vA * vA * vA * vA
                    - 0.00000133933025300616 * vA * vA * vA + 0.00010443179606629200 * vA * vA
                    - 0.00362516252242907000 * vA + 4.22234973344988000000);
        } else {
            result = (0.0000000000350336 * vA * vA * vA * vA * vA * vA - 0.0000000582545122 * vA * vA * vA * vA * vA
                    + 0.0000402152677305 * vA * vA * vA * vA - 0.0147498312009770 * vA * vA * vA
                    + 3.0309573484588600 * vA * vA - 330.8198996666820000 * vA + 14986.3041165481);
        }
        return result;
    }

    /**
     * @return dynamic viscosity pa*s
    */
    public static double calculateDynamicviscosity(double tempC) {
        double vA = tempC;
        double result = 0.0;

        if (vA < 0){
            return result;
        } else if  (vA > 370){
            return result;
        } else if (vA < 95){
            result = (0.00000000000277388442*vA*vA*vA*vA*vA*vA-0.00000000124359703683*vA*vA*vA*vA*vA+0.00000022981389243372*vA*vA*vA*vA-0.00002310372106867350*vA*vA*vA+0.00143393546700877000*vA*vA-0.06064140920049450000*vA+1.79157254681817000000) / 1000;  
        } else {
            result = (-0.00000000000045460686*vA*vA*vA*vA*vA+0.00000000059247759433*vA*vA*vA*vA-0.00000031530650243330*vA*vA*vA+0.00008686885936364020*vA*vA-0.01293386197882230000*vA+0.96667934078564300000) / 1000;
        }		
        return result;
    }
    /**
     * @return array of {newMass, newTemp}
     */
    public static double[] mixWater(double destinationMass, double destinationTemp, double addedMass, double addedTemp) {
        double[] result = {0.0, 0.0};
        if (addedMass <= 0) {
            result[0] = destinationMass;
            result[1] = destinationTemp;
            return result;
        } 
        final double totalMass = destinationMass + addedMass;
        final double totalEnthalpy = tables.getWaterEnthalpyByTemperature(addedTemp) * addedMass + tables.getWaterEnthalpyByTemperature(destinationTemp) * destinationMass;
        result[0] = totalMass;
        result[1] = tables.getWaterTemperatureByEnthalpy(totalEnthalpy / totalMass);
        return result;
    }

    /**
     * @return array of {newMass, newTemp}
     */
     public static Double[] mixSteam(double destinationMass, double destinationTemp, double addedMass, double addedTemp) {
        Double[] result = {0.0, 0.0};
        if (addedMass <= 0) {
            result[0] = destinationMass;
            result[1] = destinationTemp;
            return result;
        } 
        //TODO simplified for now
        /* final double totalMass = destinationMass + addedMass;
        final double totalEnthalpy = tables.getSteamEnthalpyByTemperature(addedTemp) * addedMass + tables.getSteamEnthalpyByTemperature(destinationTemp) * destinationMass;
        result[0] = totalMass;
        result[1] = tables.getSteamTemperatureByEnthalpy(totalEnthalpy / totalMass); */ 
        result[0] = addedMass + destinationMass;
        result[1] = (destinationTemp * destinationMass + addedMass * addedTemp) / result[0];
        return result;
    }

    public static double round(double number) {
        return Math.floor(number * 100) / 100;
    }

    public static double round(double number, int precision) {
        double precisionNum = Math.pow(10.0, precision);
        return Math.floor(number * precisionNum) / precisionNum;
    }

    /**
    * @param density density of water in kg/m3
    * @param depth depth of water column in m
    * @return pressure in pa
    */
    public static double calculateHydrostaticPressure(double density, double depth) {
        return density * depth * 9.81;
    }

    /**
    * @param density1 density of the colder/higher water in kg/m3
    * @param density2 density of the warmer/lower water in kg/m3
    * @param depth depth of water column in m
    * @return pressure in pa
    */
    public static double calculateThermalDrivingHead(double density1, double density2, double depth) {
        return (density1 - density2) * depth * 9.81;
    }

    /**
    * @param pressureIn MPa
    * @param pressureOut MPa
    * @param radius pipe radius m
    * @param length pipe length m
    * @param dynamicViscosity Pa.s
    * @return  flow in m3/s
    */
    public static double calculateVolumeFlowRate(double pressureIn, double pressureOut, double radius, double length, double dynamicViscosity) {
        return Math.PI * Math.pow(radius, 4) * (pressureIn - pressureOut) * 1000000 / (8 * dynamicViscosity * length);
    }
    
    public static String formatChannelNumber(int x, int y) {
        if (x < 4) {
            x += 51;
        } else if (x > 51) {
            x += 3;
        } else {
            x -= 3;
        } 
        if (y < 4) {
            y = 58 - y;
        } else if (y > 51) {
            y = 54 - (y - 52);
        } else {
            y = 52 - y;
        }
        return (y >= 10 ? Integer.toString(y) : ("0" + y)) + "-" + (x >= 10 ? Integer.toString(x) : ("0" + x));
    }
    
    public static float updatePositionFromState(int state, int autoState, float position,  float speed) {
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
        return position;
    }
    
    public static String formatSecondsToDaysAndTime(long timeSeconds, boolean longFormat) {
        long days = timeSeconds / 86400;
        long hours = timeSeconds % 86400 / 3600;
        long minutes = timeSeconds % 3600 / 60;
        long seconds = timeSeconds % 60;
        String hoursZero = "";
        String minutesZero = "";
        String secondsZero = "";
        String longFormatString = " ";
        if (hours < 10) {
            hoursZero = "0";
        }
        if (minutes < 10) {
            minutesZero = "0";
        }
        if (seconds < 10) {
            secondsZero = "0";
        }
        if (longFormat) {
            longFormatString = "            ";
        }
        return "Day " + (days + 1) + longFormatString + hoursZero + hours + ":" + minutesZero + minutes + ":" + secondsZero + seconds;
    }
    
    /**
     * @param halfLife half-life in hours
     * @return decay multiplier per element for each update cycle of 50ms
     */
    public static double calculateDecayMultiplierPerUpdate(double halfLife) {
        return Math.pow(0.5, 1 / (72000 * halfLife));
    }
    
    /**
     * @param depth in m
     * @param density in kg/m3
     * @return hydrostatic pressure in MPa
     */
    public static double calculateFluidColumn(double depth, double density) {
        return depth * density * 9.80665 / 1e6;
    }
}
