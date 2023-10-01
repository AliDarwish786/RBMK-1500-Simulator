package com.darwish.nppsim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class NPPSim {
    static UI ui;
    //list of all npp components
    static Atmosphere atmosphere;
    static MCC mcc;
    static Core core;
    static ArrayList<SteamValve> sdv_c;
    static ArrayList<SteamValve> sdv_a;
    static ArrayList<SteamValve> msvLoop1;
    static ArrayList<SteamValve> msvLoop2;
    static ArrayList<SteamValve> TG1InletValves;
    static ArrayList<SteamValve> TG2InletValves;
    static ArrayList<Pump> condensate1A; 
    static ArrayList<Pump> condensate2A;
    static ArrayList<Pump> condensate1B;
    static ArrayList<Pump> condensate2B;
    static ArrayList<Pump> mainFeedwaterPumps;
    static ArrayList<Pump> auxFeedwaterPumps;
    static ArrayList<WaterValve> dearatorValves;
    static ArrayList<WaterValve> mainFeederValves;
    static ArrayList<WaterValve> auxFeederValves;
    static ArrayList<Dearator> dearators;
    static ArrayList<Ejector> ejectors;
    static TG tg1, tg2;
    static SteamTables tables;
    static OneWaySteamHeader steamPiping;
    static PressureHeader auxiliaryFWPressureHeader, mainFWPressureHeader, condensateHeader1, condensateHeader2;
    static SimplifiedCondensateHeader condensateHeader;
    static SimpleSuctionHeader fwSuctionHeader;
    static WaterMixer feedwaterMixer1, feedwaterMixer2;
    static AutoControl autoControl;
    static PCS pcs;
    static ArrayList<Serializable> stateArray;
    
    private static Thread updateThread, printThread;
    private static boolean simPaused = false, updating = false, run = true;
    public static boolean core1=false;

    //debug parts here

    //variables for update thread
    private static int updateCount = 0;
    //private static boolean updated = false;
    private static int deltaTimeStepLength = 0;
    private static long timeStepLengthCumulative; //IT IS USED DONT BELIEVE THE IDE

    
    /**
     * new simulation instance with default values
     */
    public NPPSim() {
        stateArray = new ArrayList<>();
        sdv_c = new ArrayList<>(); 
        sdv_a = new ArrayList<>();
        msvLoop1 = new ArrayList<>(); 
        msvLoop2 = new ArrayList<>();
        TG1InletValves = new ArrayList<>(); 
        TG2InletValves = new ArrayList<>(); 
        condensate1A = new ArrayList<>(); 
        condensate2A = new ArrayList<>();
        condensate1B = new ArrayList<>();
        condensate2B = new ArrayList<>();
        mainFeedwaterPumps = new ArrayList<>();
        auxFeedwaterPumps = new ArrayList<>();
        dearatorValves = new ArrayList<>();
        mainFeederValves = new ArrayList<>();
        auxFeederValves = new ArrayList<>();
        dearators = new ArrayList<>();
        ejectors = new ArrayList<>();

        //parse the steam tables
        tables = Loader.tables;
        
        //initialize all components
        atmosphere = new Atmosphere(15);
        core = new Core();
        mcc = new MCC();

        //initialize the steam piping as a shared header
        steamPiping = new OneWaySteamHeader(new Connectable[] {mcc.drum1, mcc.drum2});
        
        //TG and Condenser Components
        tg1 = new TG(new Condenser());
        tg2 = new TG(new Condenser());
        tg1.setTG1();

        for (int i = 0; i < 4; i++) {
            ejectors.add(new Ejector(5.14, 10, steamPiping, atmosphere, tg1.condenser, atmosphere));
        }
        for (int i = 0; i < 4; i++) {
            ejectors.add(new Ejector(5.14, 10, steamPiping, atmosphere, tg2.condenser, atmosphere));
        }

        //add TG inlet valves
        TG1InletValves.add(new SteamValve(3044.72, 20, steamPiping, tg1));
        TG1InletValves.add(new SteamValve(3044.72, 20, steamPiping, tg1));
        TG2InletValves.add(new SteamValve(3044.72, 20, steamPiping, tg2));
        TG2InletValves.add(new SteamValve(3044.72, 20, steamPiping, tg2));

        //add the eight SDV-C valves, 4 per loop
        for (int i = 0; i < 4; i++) {   
            sdv_c.add(new SteamValve(745.76, 8, steamPiping, tg1.condenser));
        }
        for (int i = 0; i < 4; i++) {   
            sdv_c.add(new SteamValve(745.76, 8, steamPiping, tg2.condenser));
        }
        
        //SDV-A
        sdv_a.add(new SteamValve(471.03, 8, mcc.drum1, atmosphere));
        sdv_a.add(new SteamValve(471.03, 8, mcc.drum2, atmosphere));

        //MSV
        msvLoop1.add(new SteamValve(461.33, 8, mcc.drum1, atmosphere)); //group 1
        msvLoop2.add(new SteamValve(461.33, 8, mcc.drum2, atmosphere));
        for (int i = 0; i < 2; i++) {
            msvLoop1.add(new SteamValve(458.53, 8, mcc.drum1, atmosphere)); //group 2
            msvLoop2.add(new SteamValve(458.53, 8, mcc.drum2, atmosphere));
        }
        for (int i = 0; i < 3; i++) {
            msvLoop1.add(new SteamValve(455.48, 8, mcc.drum1, atmosphere)); //group 3
            msvLoop2.add(new SteamValve(455.48, 8, mcc.drum2, atmosphere));
        }
  
        //initialize condensate pumps and dearators  TODO this is greatly simplified needs reworking
        condensateHeader = new SimplifiedCondensateHeader();
        condensateHeader1 = new PressureHeader();
        condensateHeader2 = new PressureHeader();
        for (int i = 0; i < 3; i++) {
            condensate1A.add(new Pump(740, 0.417f, 1.117, 0.022555295, 30, 40, 167, tg1.condenser, condensateHeader1));
        }
        for (int i = 0; i < 3; i++) {
            condensate2A.add(new Pump(740, 0.417f, 1.117, 0.022555295, 30, 40, 167, tg2.condenser, condensateHeader2));
        }
        for (int i = 0; i < 3; i++) {
            condensate1B.add(new Pump(2975, 0.417f, 1.766, 0.2157463, 30, 40, 267, condensateHeader1, condensateHeader));
        }
        for (int i = 0; i < 3; i++) {
            condensate2B.add(new Pump(2975, 0.417f, 1.766, 0.2157463, 30, 40, 267, condensateHeader2, condensateHeader));
        }
        condensateHeader1.setSources(new Pump[] {condensate1A.get(0), condensate1A.get(1), condensate1A.get(2)});
        condensateHeader2.setSources(new Pump[] {condensate2A.get(0), condensate2A.get(1), condensate2A.get(2)});
        condensateHeader.setSources(new Pump[] {condensate1B.get(0), condensate1B.get(1), condensate1B.get(2), condensate2B.get(0), condensate2B.get(1), condensate2B.get(2)});
        for (int i = 0; i < 4; i++) {
            dearators.add(new Dearator());
            dearatorValves.add(new WaterValve(1667.52, 12, condensateHeader, dearators.get(i))); //1206.3 
        }
        
        //initialize feedwater pumps and piping/valves
        feedwaterMixer1 = new WaterMixer(mcc.sHeader1, 2);
        feedwaterMixer2 = new WaterMixer(mcc.sHeader2, 2);
        
        //auxiliaryFWSuctionHeader = new SimpleSuctionHeader(new Connectable[] {dearators.get(0), dearators.get(1), dearators.get(2), dearators.get(3)});
        fwSuctionHeader = new SimpleSuctionHeader(new Connectable[] {dearators.get(0), dearators.get(1), dearators.get(2), dearators.get(3)}, 1);
        fwSuctionHeader.isolationValveArray.get(0).setPosition(1.0f); //open valve for the first dearator
        auxiliaryFWPressureHeader = new PressureHeader();
        mainFWPressureHeader = new PressureHeader();
        for (int i = 0; i < 7; i++) {
            mainFeedwaterPumps.add(new Pump(2982, 0.458f, 8.9, 0.14709975, 30, 40, 833, fwSuctionHeader, mainFWPressureHeader)); //9.5
        }
        for (int i = 0; i < 6; i++) {
            auxFeedwaterPumps.add(new Pump(2970, 0.069f, 8.6, 0.08825985, 30, 40, 133, fwSuctionHeader, auxiliaryFWPressureHeader)); //9.1
        } 
        mainFWPressureHeader.setSources(new Pump[] {mainFeedwaterPumps.get(0), mainFeedwaterPumps.get(1), mainFeedwaterPumps.get(2), mainFeedwaterPumps.get(3), mainFeedwaterPumps.get(4), mainFeedwaterPumps.get(5), mainFeedwaterPumps.get(6)});
        auxiliaryFWPressureHeader.setSources(new Pump[] {auxFeedwaterPumps.get(0), auxFeedwaterPumps.get(1), auxFeedwaterPumps.get(2), auxFeedwaterPumps.get(3), auxFeedwaterPumps.get(4), auxFeedwaterPumps.get(5)});
        for (int i = 0; i < 3; i++) {
            mainFeederValves.add(new WaterValve(498.01, 40, mainFWPressureHeader, feedwaterMixer1));
        }
        for (int i = 0; i < 3; i++) {
            mainFeederValves.add(new WaterValve(498.01, 40, mainFWPressureHeader, feedwaterMixer2));
        }
        auxFeederValves.add(new WaterValve(197.44, 40, auxiliaryFWPressureHeader, feedwaterMixer1));
        auxFeederValves.add(new WaterValve(197.44, 40, auxiliaryFWPressureHeader, feedwaterMixer2));
        
        //PCS
        pcs = new PCS();
        
        //initialize the automatic control system
        autoControl = new AutoControl();

        //set variables if needed
//        core.coreArray.forEach(row -> {
//            row.forEach(channel -> {
//                channel.setNeutronCount(new Double[] {1e9, 0.0});
//                if(channel instanceof ControlRodChannel) {
//                    if (channel instanceof FASRChannel) {
//                        ((ControlRodChannel) channel).setPosition(0.0f);
//                    } else {
//                        ((ControlRodChannel) channel).setPosition(0.65f);
//                    }
//                }
//            });
//        });
//        autoControl.az1Control.setEnabled(false);
//        mcc.setWaterTemp(280);
//        mcc.mcp.get(0).setActive(true);
//        mcc.mcp.get(1).setActive(true);
//        mcc.mcp.get(4).setActive(true);
//        mcc.mcp.get(5).setActive(true);
//        mcc.mcp.get(0).setRPM(1000);
//        mcc.mcp.get(1).setRPM(1000);
//        mcc.mcp.get(4).setRPM(1000);
//        mcc.mcp.get(5).setRPM(1000);
//        autoControl.automaticRodController.setSetpoint(500);
//        dearators.forEach(dearator -> {
//            dearator.setWaterTemp(190);
//        });
        

        //initialize UI
        simPaused = false;
        ui = new UI();

        //start simulation
        run = true;
        updateTimer();
        printTimer();
    }
    
    /**
     * This creates a new simulation instance from a saved state
     * @param state the saved instance
     */
    public NPPSim(List<Serializable> state) {
        
        stateArray = (ArrayList<Serializable>)state;
        sdv_c = new ArrayList<>(); 
        sdv_a = new ArrayList<>();
        msvLoop1 = new ArrayList<>(); 
        msvLoop2 = new ArrayList<>();
        TG1InletValves = new ArrayList<>(); 
        TG2InletValves = new ArrayList<>(); 
        condensate1A = new ArrayList<>(); 
        condensate2A = new ArrayList<>();
        condensate1B = new ArrayList<>();
        condensate2B = new ArrayList<>();
        mainFeedwaterPumps = new ArrayList<>();
        auxFeedwaterPumps = new ArrayList<>();
        dearatorValves = new ArrayList<>();
        mainFeederValves = new ArrayList<>();
        auxFeederValves = new ArrayList<>();
        dearators = new ArrayList<>();
        ejectors = new ArrayList<>();
        
        tables = Loader.tables;
        
        atmosphere = (Atmosphere)state.get(0);
        core = (Core)state.get(1);
        mcc = (MCC)state.get(2);
        steamPiping = (OneWaySteamHeader)state.get(23);
        tg1 = (TG)state.get(26);
        tg2 = (TG)state.get(29);
        for (int i = 30; i < 38; i++) {
            ejectors.add((Ejector)state.get(i));
        }
        TG1InletValves.add((SteamValve)state.get(38));
        TG1InletValves.add((SteamValve)state.get(39));
        TG2InletValves.add((SteamValve)state.get(40));
        TG2InletValves.add((SteamValve)state.get(41));
        for (int i = 42; i < 50; i++) {
            sdv_c.add((SteamValve)state.get(i));
        }
        sdv_a.add((SteamValve)state.get(50));
        sdv_a.add((SteamValve)state.get(51));
        msvLoop1.add((SteamValve)state.get(52));
        msvLoop2.add((SteamValve)state.get(53));
        msvLoop1.add((SteamValve)state.get(54));
        msvLoop2.add((SteamValve)state.get(55));
        msvLoop1.add((SteamValve)state.get(56));
        msvLoop2.add((SteamValve)state.get(57));
        msvLoop1.add((SteamValve)state.get(58));
        msvLoop2.add((SteamValve)state.get(59));
        msvLoop1.add((SteamValve)state.get(60));
        msvLoop2.add((SteamValve)state.get(61));
        msvLoop1.add((SteamValve)state.get(62));
        msvLoop2.add((SteamValve)state.get(63));
        condensateHeader = (SimplifiedCondensateHeader)state.get(64);
        condensateHeader1 = (PressureHeader)state.get(65);
        condensateHeader2 = (PressureHeader)state.get(66);
        condensate1A.add((Pump)state.get(67));
        condensate1A.add((Pump)state.get(69));
        condensate1A.add((Pump)state.get(71));
        condensate2A.add((Pump)state.get(73));
        condensate2A.add((Pump)state.get(75));
        condensate2A.add((Pump)state.get(77));
        condensate1B.add((Pump)state.get(79));
        condensate1B.add((Pump)state.get(81));
        condensate1B.add((Pump)state.get(83));
        condensate2B.add((Pump)state.get(85));
        condensate2B.add((Pump)state.get(87));
        condensate2B.add((Pump)state.get(89));
        dearators.add((Dearator)state.get(91));
        dearators.add((Dearator)state.get(95));
        dearators.add((Dearator)state.get(99));
        dearators.add((Dearator)state.get(103));
        dearatorValves.add((WaterValve)state.get(94));
        dearatorValves.add((WaterValve)state.get(98));
        dearatorValves.add((WaterValve)state.get(102));
        dearatorValves.add((WaterValve)state.get(106));
        feedwaterMixer1 = (WaterMixer)state.get(107);
        feedwaterMixer2 = (WaterMixer)state.get(108);
        fwSuctionHeader = (SimpleSuctionHeader)state.get(109);
        auxiliaryFWPressureHeader = (PressureHeader)state.get(114);
        mainFWPressureHeader = (PressureHeader)state.get(115);
        mainFeedwaterPumps.add((Pump)state.get(116));
        mainFeedwaterPumps.add((Pump)state.get(118));
        mainFeedwaterPumps.add((Pump)state.get(120));
        mainFeedwaterPumps.add((Pump)state.get(122));
        mainFeedwaterPumps.add((Pump)state.get(124));
        mainFeedwaterPumps.add((Pump)state.get(126));
        mainFeedwaterPumps.add((Pump)state.get(128));
        auxFeedwaterPumps.add((Pump)state.get(130));
        auxFeedwaterPumps.add((Pump)state.get(132));
        auxFeedwaterPumps.add((Pump)state.get(134));
        auxFeedwaterPumps.add((Pump)state.get(136));
        auxFeedwaterPumps.add((Pump)state.get(138));
        auxFeedwaterPumps.add((Pump)state.get(140));
        mainFeederValves.add((WaterValve)state.get(142));
        mainFeederValves.add((WaterValve)state.get(143));
        mainFeederValves.add((WaterValve)state.get(144));
        mainFeederValves.add((WaterValve)state.get(145));
        mainFeederValves.add((WaterValve)state.get(146));
        mainFeederValves.add((WaterValve)state.get(147));
        auxFeederValves.add((WaterValve)state.get(148));
        auxFeederValves.add((WaterValve)state.get(149));
        pcs = (PCS)state.get(150);
        autoControl = (AutoControl)state.get(205);

        simPaused = false;
        ui = new UI();
        run = true;
        updateTimer();
        printTimer();
    }

    private void updateTimer() {
        updateThread = new Thread(() -> {
            try {
                while(run) {
                    while (simPaused) {
                        updating = false;
                        Thread.sleep(100);
                    }
                    updating = true;
                    long start = System.nanoTime();
                    //all simulation components's update methods go here. 
                    //RULE OF THUMB: objects from "Components" file should be updated before their sources/drains if possible
                    sdv_c.forEach(valve -> {
                        valve.update();
                    });
                    sdv_a.forEach(valve -> {
                        valve.update();
                    });
                    msvLoop1.forEach(valve -> {
                        valve.update();
                    });
                    msvLoop2.forEach(valve -> {
                        valve.update();
                    });
                    
                    TG1InletValves.forEach(valve -> {
                        valve.update();
                    });
                    TG2InletValves.forEach(valve -> {
                        valve.update();
                    });
                    NPPSim.dearators.forEach(dearator -> {
                        dearator.steamInlet.update();
                        dearator.steamOutlet.update();
                    });
                    steamPiping.update();
                    TG.updateGridPhase(); //static variable of class TG updated
                    tg1.update();
                    tg2.update();
                    ejectors.forEach(ejector -> {
                        ejector.update();
                    });
                    tg1.condenser.update();
                    tg2.condenser.update();
                    
                    dearatorValves.forEach(valve -> {
                        valve.update();
                    });

                    condensateHeader.update();
                    
                    condensate1B.forEach(pump -> {
                        pump.update();
                    });
                    condensate2B.forEach(pump -> {
                        pump.update();
                    });
                    
                    condensateHeader1.update();
                    condensateHeader2.update();
                    //System.out.println(condensateHeader.pressure + " " + condensateHeader1.pressure);
                    
                    condensate1A.forEach(pump -> {
                        pump.update();
                    });
                    condensate2A.forEach(pump -> {
                        pump.update();
                    });
                    
                    feedwaterMixer1.update();
                    feedwaterMixer2.update();
                    mainFeederValves.forEach(valve -> {
                        valve.update();
                    });
                    auxFeederValves.forEach(valve -> {
                        valve.update();
                    });
                    mainFWPressureHeader.update();
                    auxiliaryFWPressureHeader.update();
                    mainFeedwaterPumps.forEach(pump -> {
                        pump.update();
                    });
                    auxFeedwaterPumps.forEach(pump -> {
                        pump.update();
                    });
                    
                    fwSuctionHeader.update();
                    
                    dearators.forEach(dearator -> {
                        dearator.update();
                    });
                    
                    pcs.update();
                    NPPSim.core1 = true; //this value is read by the MTK so it won't update while the core is updating
                    core.update();
                    NPPSim.core1 = false;
                    mcc.update();
                    
                    autoControl.update();
                    //end of components to update
                    updateCount++;
                    long end = System.nanoTime();
                    int timestep = (int)(end - start);
                    deltaTimeStepLength = (50000000 - timestep);
                    int deltaMillis = deltaTimeStepLength / 1000000;
                    int deltaNanos = deltaTimeStepLength - deltaMillis * 1000000;
                    if (deltaTimeStepLength > 0) {
                        updating = false;
                        Thread.sleep(deltaMillis, deltaNanos);
                        updating = true;
                        timeStepLengthCumulative += deltaTimeStepLength;
                    }
                    timeStepLengthCumulative += timestep;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                new ErrorWindow("A fatal error occured", ExceptionUtils.getStackTrace(e), false).setVisible(true);
            }
        });
        updateThread.start();
    }

    private void printTimer() {
        final long timeToSleep[] = {900};
        printThread = new Thread(() -> {
            try {
                while(run) {
                    Thread.sleep(timeToSleep[0]);
                    while (updateCount < 20 ) {
                        Thread.sleep(10);
                    }
                    if (timeStepLengthCumulative / updateCount > 50000000) {
                        System.out.println("WARNING: Average timeStep = " + timeStepLengthCumulative / updateCount);
                    }
                    //System.out.println("Average time step: " + timeStepLengthCumulative / updateCount + " " + updateCount); //used sometimes for debugging
                    if (updateCount > 20) {
                        if (timeToSleep[0] > 500) {
                            timeToSleep[0] -= 50;
                        }
                        System.out.println("WARNING: Timesteps = " + updateCount + ". UI Wait time will be " + timeToSleep[0]);
                    }
                    updateCount = 0;
                    timeStepLengthCumulative = 0;
                    autoControl.updateSimulationTime();
                    ui.update();
                    while (simPaused) {
                        Thread.sleep(100);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        printThread.start();
    }
    
    public static void endSimulation() {
        try {
            for (int i = 0; i < UI.uiThreads.size(); i++) {
                UI.uiThreads.get(i).interrupt();
            }
            UI.uiThreads.clear();
            ui.setVisible(false);
            updateThread.interrupt();
            printThread.interrupt();
            run = false;
            ui.dispose();
            Loader.soundProvider.stopAll();
        } catch (Exception e) {
            //void
        }
    }
    
    public static void setPaused(boolean paused) {
        simPaused = paused;
    }
    
    public static boolean isPaused() {
        return simPaused;
    }
    
    public static boolean isRunning() {
        return run;
    }
    
    public ArrayList<Serializable> getState() {
        return stateArray;
    }

    public static void save(File file) {
        boolean wasPaused = isPaused();
        try {
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            setPaused(true);
            while(updating) {
                Thread.sleep(5);
            }
            oos.writeObject(stateArray);
            setPaused(wasPaused);
            oos.flush();
            fout.flush();
            oos.close();
            fout.close();
        } catch (Exception e) {
            new ErrorWindow("Error Saving IC", ExceptionUtils.getStackTrace(e), true).setVisible(true);
            setPaused(wasPaused);
        }
    }
    
    public static void load(File file) {
        Loader.loader.setLoading(false);
        try (
         FileInputStream fin = new FileInputStream(file);
         ObjectInputStream ois = new ObjectInputStream(fin);          
        ) {
            List<Serializable> read = (List<Serializable>) ois.readObject();
            ois.close();
            fin.close();
            endSimulation();
            Loader.simulation = new NPPSim(read);
        } catch (Exception e) {
            new ErrorWindow("Error Loading IC", ExceptionUtils.getStackTrace(e), true).setVisible(true);
        }
    }
}
