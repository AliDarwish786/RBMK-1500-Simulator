package com.darwish.nppsim;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Core extends Component {
        final ArrayList<ArrayList<Channel>> coreArray = new ArrayList<>();
        private final ArrayList<ArrayList<Double[]>> nextGenerationCount = new ArrayList<>();
        private double neutronCount = 0, previousNeutronCount, kEff, reactivity, period, thermalPower;
        private double fcNeutronCount;
        
        public Core() {
            ArrayList<List<String>> coreData = getCoreData();
            for (int y = 0; y < 56; y++) {
                coreArray.add(new ArrayList<>());
                nextGenerationCount.add(new ArrayList<>());
                for (int x = 0; x < 56; x++) {
                    Channel channel = initializeChannelByIdentifier(coreData.get(y).get(x));
                    channel.setPositionString(NPPMath.formatChannelNumber(x, y));
                    coreArray.get(y).add(channel);
                    nextGenerationCount.get(y).add(channel.getNeutronPopulation());
                }
            }
        }

        public void update() {
            thermalPower = fcNeutronCount / 4.9808177502e13 * 4800;
            kEff = Math.pow(neutronCount / previousNeutronCount, 2.0); //to the power of 2 because 2 simulation cycles in 1 neuton lifetime cycle
            reactivity = (kEff - 1) / kEff;
            period = 0.1 / (kEff - 1); //simulation cycle time is 0.05 but 0.1 is more realistic
            previousNeutronCount = neutronCount;
            neutronCount = 0;
            fcNeutronCount = 0;
            nextGenerationCount.forEach(row -> { // clear map for next neutron generation
                row.forEach(numberArray -> {
                    numberArray[0] = 0.0;
                    numberArray[1] = 0.0;
                });
            });
            for (short x = 0; x < coreArray.size(); x++) {
                for (short y = 0; y < coreArray.get(x).size(); y++) {
                    Channel channel = coreArray.get(x).get(y);
                    
                    Double fastNeutronCount = channel.getNeutronPopulation()[0];
                    Double thermalNeutronCount = channel.getNeutronPopulation()[1];
                    neutronCount += fastNeutronCount;
                    if (channel instanceof FuelChannel) {
                        fcNeutronCount += fastNeutronCount;
                    }
                    Double propagatedFastNeutrons = fastNeutronCount;
                    Double propagatedThermalNeutrons = thermalNeutronCount;
                    Double[][] neighborChannels = new Double[8][2]; // an array of neighboring positions in a 9x9 grid omitting the 'self' position at the center starting clockwise from upper left
                    short neighborCount = 0;
                    if (coreArray.get(x).get(y) instanceof VoidChannel) {
                        continue;
                    }
                    for (short c = 0; c < 8; c++) {
                        short[] neighborXY = getNeighborCoordinates(x, y, c);
                        if (neighborXY[0] < 0 || neighborXY[0] > 55 || neighborXY[1] < 0 || neighborXY[1] > 55) { //continue if a neighboring channel would be outside of the core array or is VoidChannel
                            continue;
                        }
                        if (coreArray.get(neighborXY[0]).get(neighborXY[1]) instanceof VoidChannel) {
                            continue;
                        }
                        neighborCount++;
                        neighborChannels[c] = nextGenerationCount.get(neighborXY[0]).get(neighborXY[1]); //finally assign address of neighbor's neutron count array for next gen
                    }
                    propagatedFastNeutrons /= neighborCount;
                    propagatedThermalNeutrons /= neighborCount;
                    for (int i = 0; i < 8; i++) {   //send neutrons to adjecent channels if it was assigned an address
                        if (neighborChannels[i][0] == null) {
                            continue;
                        }
                        neighborChannels[i][0] += propagatedFastNeutrons;
                        neighborChannels[i][1] += propagatedThermalNeutrons;
                    }
                }
            }
            for (int x = 0; x < coreArray.size(); x++) { //update neutron population to new generation after propagation
                for (int y = 0; y < coreArray.get(x).size(); y++) {
                    if (coreArray.get(x).get(y) instanceof VoidChannel) {
                        continue;
                    }
                    coreArray.get(x).get(y).setNeutronPopulation(nextGenerationCount.get(x).get(y));
                }
            }
            for(int x = 0; x < coreArray.size(); x++) {
                for (int y = 0; y < coreArray.get(x).size(); y++) {
                    coreArray.get(x).get(y).updateNeutronPopulation();
                }
            }
        }
        
        /**
         * 
         * @param x
         * @param y
         * @param c relative position
         * @return coordinates {x, y} for neighboring channel depending on relative position
         */
        private short[] getNeighborCoordinates (short x, short y, short c) {
            short neighborX = x;
            short neighborY = y;
            switch (c) {
                case 0:
                    neighborX--;
                    neighborY--;
                    break;
                case 1:
                    neighborX--;
                    break;
                case 2:
                    neighborX--;
                    neighborY++;
                    break;
                case 3:
                    neighborY--;
                    break;
                case 4:
                    neighborY++;
                    break;
                case 5:
                    neighborX++;
                    neighborY--;
                    break;
                case 6:
                    neighborX++;
                    break;
                case 7:
                    neighborX++;
                    neighborY++;
                    break;
            }
            return new short[] {neighborX, neighborY};
        }
        
        private Channel initializeChannelByIdentifier(String channelIdentifier) {
            switch (channelIdentifier) { 
                case "\\": //PDMS-A Channel
                    return new PDMSChannel();
                case "^": //fission chamber
                    return new FissionChamber();
                case "_": //MCR
                    return new MCRChannel();
                case "E": //LEP
                    return new LEPChannel();
                case "o": //MCRM
                    return new MCRMChannel();
                case "L": //LAC
                    return new LACChannel();
                case "A": //AR
                    return new ACRChannel();
                case "x": //SAR
                    return new SARChannel();
                case "R": //axial control sar
                    return new SACRChannel();
                case "%": //FASR
                    return new FASRChannel(); 
                case "#": //fuel channel
                    return new FuelChannel();
                case "*": //reflector cooling channel
                    return new ReflectorCoolingChannel();
                case ".": //reflector
                    return new ReflectorChannel();
                case "v": //void
                    return new VoidChannel();
                default:
                    return null;
            }
            
        }
        
        private ArrayList<List<String>> getCoreData() {
            ArrayList<List<String>> result = new ArrayList<>();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader((getClass().getResourceAsStream("/res/coreMap.csv"))));
                ArrayList<String> dataStringArray = new ArrayList<>();
                String line;
                while ((line=reader.readLine())!=null) {
                    dataStringArray.add(line);
                }
                for (int i = 1; i < dataStringArray.size(); i++) {
                    String[] dataArray = dataStringArray.get(i).split(",");
                    ArrayList<String> data = new ArrayList<>(Arrays.asList(dataArray));
                    data.remove(data.size() - 1);
                    result.add(data);
                }
                if (result.size() != 56) {
                    throw new Exception("CorruptedCoreDataException");
                }
                for (List<String> i: result) {
                    if (i.size() != 56) {
                        throw new Exception("CorruptedCoreDataException");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }       

        public double getThermalPower() {
            return thermalPower;
        }

        public double getReactivity() {
            return reactivity;
        }
        
        public double getNeutronCount() {
            return previousNeutronCount;
        }
        
        public double getPeriod() {
            return period;
        }
    }