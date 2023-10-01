package com.darwish.nppsim;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

public class SteamTables {
    private JSONArray tables;
    private HashMap<Integer, Double> steamPressureByTempMap = new HashMap<>();
    private HashMap<Integer, Double> waterDensityByTempMap = new HashMap<>();
    private HashMap<Integer, Double> vaporizationEnthalpyByTempMap = new HashMap<>();
    private HashMap<Integer, Double> waterEnthalpyByTempMap = new HashMap<>();
    private HashMap<Integer, Double> steamEnthalpyByTempMap = new HashMap<>();
    private HashMap<Integer, Double> steamTemperatureByPressureMap = new HashMap<>();
    private HashMap<Integer, Double> steamPressureByDensityMap = new HashMap<>();
    private HashMap<Integer, Double> steamDensityByPressureMap = new HashMap<>();
    private HashMap<Integer, Double> waterTemperatureByEnthalpyMap = new HashMap<>();
    private boolean error = false;
    
    public SteamTables() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader((getClass().getResourceAsStream("/res/saturated_by_temperature_V1.4.json"))));
            String jsonString = "";
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString += line;
            }
            tables = new JSONObject(jsonString).getJSONArray("data");
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < tables.length(); i++) {
            steamPressureByTempMap.put(tables.getJSONArray(i).getInt(0), tables.getJSONArray(i).getDouble(1));
            waterDensityByTempMap.put(tables.getJSONArray(i).getInt(0), tables.getJSONArray(i).getDouble(2));
            vaporizationEnthalpyByTempMap.put(tables.getJSONArray(i).getInt(0), tables.getJSONArray(i).getDouble(9));
            waterEnthalpyByTempMap.put(tables.getJSONArray(i).getInt(0), tables.getJSONArray(i).getDouble(7));
            steamEnthalpyByTempMap.put(tables.getJSONArray(i).getInt(0), tables.getJSONArray(i).getDouble(8));
        }
        for (int y = 778750; y >= 32; y--) {
            for (int i = 0; i < tables.length(); i++) {
                if (tables.getJSONArray(i).getDouble(3) < y / 10000.0) {
                    double dDens = tables.getJSONArray(i).getDouble(3) - tables.getJSONArray(i - 1).getDouble(3);
                    double dPress = tables.getJSONArray(i).getDouble(1) - tables.getJSONArray(i - 1).getDouble(1);
                    double perDens = dPress / dDens;
                    steamPressureByDensityMap.put(y ,tables.getJSONArray(i).getDouble(1) + (y / 10000.0 - tables.getJSONArray(i).getDouble(3)) * perDens);
                    break;
                }
            }
        }
        for (int y = 17; y <= 220640; y++) {
            for (int i = 0; i < tables.length(); i++) {
                if (tables.getJSONArray(i).getDouble(1) > y / 10000.0) {
                    double dPress = tables.getJSONArray(i).getDouble(1) - tables.getJSONArray(i - 1).getDouble(1);
                    double dDens = tables.getJSONArray(i).getDouble(3) - tables.getJSONArray(i - 1).getDouble(3);
                    double perPress = dDens / dPress;
                    steamDensityByPressureMap.put(y ,tables.getJSONArray(i).getDouble(3) + (y / 10000.0 - tables.getJSONArray(i).getDouble(1)) * perPress);
                    break;
                }
            }
            for (int i = 0; i < tables.length(); i++) {
                if (tables.getJSONArray(i).getDouble(1) > y / 10000.0) {
                    double dPress = tables.getJSONArray(i).getDouble(1) - tables.getJSONArray(i - 1).getDouble(1);
                    double dTemp = tables.getJSONArray(i).getDouble(0) - tables.getJSONArray(i - 1).getDouble(0);
                    double perPress = dTemp / dPress;
                    steamTemperatureByPressureMap.put(y ,tables.getJSONArray(i).getDouble(0) + (y / 10000.0 - tables.getJSONArray(i).getDouble(1)) * perPress);
                    break;
                }
            }
        }
        for (int y = 629; y <= 20483; y++) {
            for (int i = 0; i < tables.length(); i++) {
                if (tables.getJSONArray(i).getDouble(7) > y / 10.0) {
                    double dEnthalpy = tables.getJSONArray(i).getDouble(7) - tables.getJSONArray(i - 1).getDouble(7);
                    double dTemp = tables.getJSONArray(i).getDouble(0) - tables.getJSONArray(i - 1).getDouble(0);
                    double perEnthalpy = dTemp / dEnthalpy;
                    waterTemperatureByEnthalpyMap.put(y ,tables.getJSONArray(i).getDouble(0) + (y / 10.0 - tables.getJSONArray(i).getDouble(7)) * perEnthalpy);
                    break;
                }
            }
        }
    }

    public double getSteamTemperatureByPressure(double pressure) { //Mpa
        if (pressure >= 0.0017 && pressure < 22.064) {
            int lower = (int)(pressure * 10000.0);
            int higher = (int)(pressure * 10000.0 + 1);
            double lowerValue = steamTemperatureByPressureMap.get(lower);
            double higherValue = steamTemperatureByPressureMap.get(higher);  
            double perDensity = higherValue - lowerValue;
            return lowerValue + ((pressure * 10000.0 - lower) * perDensity); 
        }
        if (!error) {
            error = !error;
            NPPSim.endSimulation();
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            StringBuilder strBuilder = new StringBuilder();
            for (StackTraceElement stack1 : stack) {
                strBuilder.append(stack1.toString());
                strBuilder.append("\n");
            }
            new ErrorWindow("A component has been damaged beyond repair", strBuilder.toString(), true).setVisible(true);
        }
        return 0;
    }

    public double getSteamPressureByTemp(double tempC) { //Mpa
        if (tempC >= 1 && tempC < 373) {
            int lower = (int)tempC;
            int higher = (int)tempC + 1;
            double lowerValue = steamPressureByTempMap.get(lower);
            double higherValue = steamPressureByTempMap.get(higher);  
            double perDegree = higherValue - lowerValue;
            return lowerValue + ((tempC - lower) * perDegree); 
        }
        if (!error) {
            error = !error;
            NPPSim.endSimulation();
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            StringBuilder strBuilder = new StringBuilder();
            for (StackTraceElement stack1 : stack) {
                strBuilder.append(stack1.toString());
                strBuilder.append("\n");
            }
            new ErrorWindow("A component has been damaged beyond repair", strBuilder.toString(), true).setVisible(true);
        }
        return 0;
    }

    public double getSteamPressureByDensity(double density) { //m3/kg
        if (density  >= 0.0032 && density < 77.875) {
            int lower = (int)(density * 10000.0);
            int higher = (int)(density * 10000.0 + 1);
            double lowerValue = steamPressureByDensityMap.get(lower);
            double higherValue = steamPressureByDensityMap.get(higher);  
            double perDensity = higherValue - lowerValue;
            return lowerValue + ((density * 10000.0 - lower) * perDensity); 
        }
        if (!error) {
            error = !error;
            NPPSim.endSimulation();
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            StringBuilder strBuilder = new StringBuilder();
            for (StackTraceElement stack1 : stack) {
                strBuilder.append(stack1.toString());
                strBuilder.append("\n");
            }
            new ErrorWindow("A component has been damaged beyond repair", strBuilder.toString(), true).setVisible(true);
        }
        return 0;
    }

    public double getSteamDensityByPressure(double pressure) { //m3/kg
        if (pressure >= 0.0017 && pressure < 22.064) {
            int lower = (int)(pressure * 10000.0);
            int higher = (int)(pressure * 10000.0 + 1);
            double lowerValue = steamDensityByPressureMap.get(lower);
            double higherValue = steamDensityByPressureMap.get(higher);  
            double perDensity = higherValue - lowerValue;
            return lowerValue + ((pressure * 10000.0 - lower) * perDensity); 
        }
        if (!error) {
            error = !error;
            NPPSim.endSimulation();
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            StringBuilder strBuilder = new StringBuilder();
            for (StackTraceElement stack1 : stack) {
                strBuilder.append(stack1.toString());
                strBuilder.append("\n");
            }
            new ErrorWindow("A component has been damaged beyond repair", strBuilder.toString(), true).setVisible(true);
        }
        return 0;
    }

    public double getWaterDensityByTemp(double tempC) { //m3/kg
        if (tempC >= 1 && tempC < 373) {
            int lower = (int)tempC;
            int higher = (int)tempC + 1;
            double lowerValue = waterDensityByTempMap.get(lower);
            double higherValue = waterDensityByTempMap.get(higher);  
            double perDegree = higherValue - lowerValue;
            return lowerValue + ((tempC - lower) * perDegree); 
        }
        if (!error) {
            error = !error;
            NPPSim.endSimulation();
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            StringBuilder strBuilder = new StringBuilder();
            for (StackTraceElement stack1 : stack) {
                strBuilder.append(stack1.toString());
                strBuilder.append("\n");
            }
            new ErrorWindow("A component has been damaged beyond repair", strBuilder.toString(), true).setVisible(true);
        }
        return 0;
    }

    public double getSpecificVaporEnthalpyByTemperature(double tempC) { //kJ/kg
        if (tempC >= 1 && tempC < 373) {
            int lower = (int)tempC;
            int higher = (int)tempC + 1;
            double lowerValue = vaporizationEnthalpyByTempMap.get(lower);
            double higherValue = vaporizationEnthalpyByTempMap.get(higher);  
            double perDegree = higherValue - lowerValue;
            return lowerValue + ((tempC - lower) * perDegree); 
        }
        if (!error) {
            error = !error;
            NPPSim.endSimulation();
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            StringBuilder strBuilder = new StringBuilder();
            for (StackTraceElement stack1 : stack) {
                strBuilder.append(stack1.toString());
                strBuilder.append("\n");
            }
            new ErrorWindow("A component has been damaged beyond repair", strBuilder.toString(), true).setVisible(true);
        }
        return 0;
    }

    public double getWaterEnthalpyByTemperature(double tempC) { //kJ/kg
        if (tempC >= 1 && tempC < 373) {
            int lower = (int)tempC;
            int higher = (int)tempC + 1;
            double lowerValue = waterEnthalpyByTempMap.get(lower);
            double higherValue = waterEnthalpyByTempMap.get(higher);  
            double perDegree = higherValue - lowerValue;
            return lowerValue + ((tempC - lower) * perDegree); 
        }
        if (!error) {
            error = !error;
            NPPSim.endSimulation();
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            StringBuilder strBuilder = new StringBuilder();
            for (StackTraceElement stack1 : stack) {
                strBuilder.append(stack1.toString());
                strBuilder.append("\n");
            }
            new ErrorWindow("A component has been damaged beyond repair", strBuilder.toString(), true).setVisible(true);
        }
        return 0;
    }

    public double getSteamEnthalpyByTemperature(double tempC) { //kJ/kg
        if (tempC >= 1 && tempC < 373) {
            int lower = (int)tempC;
            int higher = (int)tempC + 1;
            double lowerValue = steamEnthalpyByTempMap.get(lower);
            double higherValue = steamEnthalpyByTempMap.get(higher);  
            double perDegree = higherValue - lowerValue;
            return lowerValue + ((tempC - lower) * perDegree); 
        }
        if (!error) {
            error = !error;
            NPPSim.endSimulation();
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            StringBuilder strBuilder = new StringBuilder();
            for (StackTraceElement stack1 : stack) {
                strBuilder.append(stack1.toString());
                strBuilder.append("\n");
            }
            new ErrorWindow("A component has been damaged beyond repair", strBuilder.toString(), true).setVisible(true);
        }
        return 0;
    }

    public double getWaterTemperatureByEnthalpy(double enthalpy) { //C
        if (enthalpy >= 62.9 && enthalpy < 2048.3) {
            int lower = (int)(enthalpy * 10.0);
            int higher = (int)(enthalpy * 10.0 + 1);
            double lowerValue = waterTemperatureByEnthalpyMap.get(lower);
            double higherValue = waterTemperatureByEnthalpyMap.get(higher);  
            double perDegree = higherValue - lowerValue;
            return lowerValue + ((enthalpy * 10.0 - lower) * perDegree); 
        }
        if (!error) {
            error = !error;
            NPPSim.endSimulation();
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            StringBuilder strBuilder = new StringBuilder();
            for (StackTraceElement stack1 : stack) {
                strBuilder.append(stack1.toString());
                strBuilder.append("\n");
            }
            new ErrorWindow("A component has been damaged beyond repair", strBuilder.toString(), true).setVisible(true);
        }
        return 0;
    }

//TODO
/*     public double getSteamTemperatureByEnthalpy(double Enthalpy) { //C
        for (int i = 0; i < tables.length(); i++) {
            if (tables.getJSONArray(i).getDouble(8) > Enthalpy) {
                double dEnthalpy = tables.getJSONArray(i).getDouble(8) - tables.getJSONArray(i - 1).getDouble(8);
                double dTemp = tables.getJSONArray(i).getDouble(0) - tables.getJSONArray(i - 1).getDouble(0);
                double perEnthalpy = dTemp / dEnthalpy;
                return tables.getJSONArray(i).getDouble(0) + (Enthalpy - tables.getJSONArray(i).getDouble(8)) * perEnthalpy;
            }
        }
        return 0.0;
    } */

}