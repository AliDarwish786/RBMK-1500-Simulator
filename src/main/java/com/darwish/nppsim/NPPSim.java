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
    static ArrayList<Pump> mainFeedWaterPumps;
    static ArrayList<Pump> auxFeedWaterPumps;
    static ArrayList<WaterValve> dearatorValves;
    static ArrayList<WaterValve> mainFeederValves;
    static ArrayList<WaterValve> auxFeederValves;
    static ArrayList<Dearator> dearators;
    static ArrayList<Ejector> ejectors;
    static TG tg1, tg2;
    static SteamTables tables;
    static OneWaySteamHeader steamPiping;
    static PressureHeader auxiliaryFWPressureHeader, mainFWPressureHeader;
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
        mainFeedWaterPumps = new ArrayList<>();
        auxFeedWaterPumps = new ArrayList<>();
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
        for (int i = 0; i < 3; i++) {
            condensate1A.add(new Pump(740, 0.417f, 2.766, 30, 40, 1600, tg1.condenser, condensateHeader)); //1.766 head is increased to emulate both stages in 1 pump
        }
        for (int i = 0; i < 3; i++) {
            condensate2A.add(new Pump(740, 0.417f, 2.766, 30, 40, 1600, tg2.condenser, condensateHeader)); //1.766
        }
        condensateHeader.setSources(new Pump[] {condensate1A.get(0), condensate1A.get(1), condensate1A.get(2), condensate2A.get(0), condensate2A.get(1), condensate2A.get(2)});
        for (int i = 0; i < 4; i++) {
            dearators.add(new Dearator());
            dearatorValves.add(new WaterValve(1667.52, 12, condensateHeader, dearators.get(i))); //1206.3
        }
        
        //initialize feedwater pumps and piping/valves
        feedwaterMixer1 = new WaterMixer(mcc.drum1);
        feedwaterMixer2 = new WaterMixer(mcc.drum2);
        
        //auxiliaryFWSuctionHeader = new SimpleSuctionHeader(new Connectable[] {dearators.get(0), dearators.get(1), dearators.get(2), dearators.get(3)});
        fwSuctionHeader = new SimpleSuctionHeader(new Connectable[] {dearators.get(0), dearators.get(1), dearators.get(2), dearators.get(3)}, 1);
        fwSuctionHeader.isolationValveArray.get(0).setPosition(1.0f); //open valve for the first dearator
        auxiliaryFWPressureHeader = new PressureHeader();
        mainFWPressureHeader = new PressureHeader();
        for (int i = 0; i < 7; i++) {
            mainFeedWaterPumps.add(new Pump(2982, 0.458f, 8.9, 30, 40, 5000, fwSuctionHeader, mainFWPressureHeader));
        }
        for (int i = 0; i < 6; i++) {
            auxFeedWaterPumps.add(new Pump(2970, 0.069f, 8.6, 30, 40, 800, fwSuctionHeader, auxiliaryFWPressureHeader));    
        } 
        mainFWPressureHeader.setSources(new Pump[] {mainFeedWaterPumps.get(0), mainFeedWaterPumps.get(1), mainFeedWaterPumps.get(2), mainFeedWaterPumps.get(3), mainFeedWaterPumps.get(4), mainFeedWaterPumps.get(5), mainFeedWaterPumps.get(6)});
        auxiliaryFWPressureHeader.setSources(new Pump[] {auxFeedWaterPumps.get(0), auxFeedWaterPumps.get(1), auxFeedWaterPumps.get(2), auxFeedWaterPumps.get(3), auxFeedWaterPumps.get(4), auxFeedWaterPumps.get(5)});
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
        
        stateArray = (ArrayList)state;
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
        mainFeedWaterPumps = new ArrayList<>();
        auxFeedWaterPumps = new ArrayList<>();
        dearatorValves = new ArrayList<>();
        mainFeederValves = new ArrayList<>();
        auxFeederValves = new ArrayList<>();
        dearators = new ArrayList<>();
        ejectors = new ArrayList<>();
        
        tables = Loader.tables;
        
        atmosphere = (Atmosphere)state.get(0);
        core = (Core)state.get(1);
        mcc = (MCC)state.get(2);
        steamPiping = (OneWaySteamHeader)state.get(11);
        tg1 = (TG)state.get(13);
        tg2 = (TG)state.get(15);
        for (int i = 16; i < 24; i++) {
            ejectors.add((Ejector)state.get(i));
        }
        TG1InletValves.add((SteamValve)state.get(24));
        TG1InletValves.add((SteamValve)state.get(25));
        TG2InletValves.add((SteamValve)state.get(26));
        TG2InletValves.add((SteamValve)state.get(27));
        for (int i = 28; i < 36; i++) {
            sdv_c.add((SteamValve)state.get(i));
        }
        sdv_a.add((SteamValve)state.get(36));
        sdv_a.add((SteamValve)state.get(37));
        msvLoop1.add((SteamValve)state.get(38));
        msvLoop2.add((SteamValve)state.get(39));
        msvLoop1.add((SteamValve)state.get(40));
        msvLoop2.add((SteamValve)state.get(41));
        msvLoop1.add((SteamValve)state.get(42));
        msvLoop2.add((SteamValve)state.get(43));
        msvLoop1.add((SteamValve)state.get(44));
        msvLoop2.add((SteamValve)state.get(45));
        msvLoop1.add((SteamValve)state.get(46));
        msvLoop2.add((SteamValve)state.get(47));
        msvLoop1.add((SteamValve)state.get(48));
        msvLoop2.add((SteamValve)state.get(49));
        condensateHeader = (SimplifiedCondensateHeader)state.get(50);
        condensate1A.add((Pump)state.get(51));
        condensate1A.add((Pump)state.get(52));
        condensate1A.add((Pump)state.get(53));
        condensate2A.add((Pump)state.get(54));
        condensate2A.add((Pump)state.get(55));
        condensate2A.add((Pump)state.get(56));
        dearators.add((Dearator)state.get(57));
        dearators.add((Dearator)state.get(61));
        dearators.add((Dearator)state.get(65));
        dearators.add((Dearator)state.get(69));
        dearatorValves.add((WaterValve)state.get(60));
        dearatorValves.add((WaterValve)state.get(64));
        dearatorValves.add((WaterValve)state.get(68));
        dearatorValves.add((WaterValve)state.get(72));
        feedwaterMixer1 = (WaterMixer)state.get(73);
        feedwaterMixer2 = (WaterMixer)state.get(74);
        fwSuctionHeader = (SimpleSuctionHeader)state.get(75);
        auxiliaryFWPressureHeader = (PressureHeader)state.get(80);
        mainFWPressureHeader = (PressureHeader)state.get(81);
        mainFeedWaterPumps.add((Pump)state.get(82));
        mainFeedWaterPumps.add((Pump)state.get(83));
        mainFeedWaterPumps.add((Pump)state.get(84));
        mainFeedWaterPumps.add((Pump)state.get(85));
        mainFeedWaterPumps.add((Pump)state.get(86));
        mainFeedWaterPumps.add((Pump)state.get(87));
        mainFeedWaterPumps.add((Pump)state.get(88));
        auxFeedWaterPumps.add((Pump)state.get(89));
        auxFeedWaterPumps.add((Pump)state.get(90));
        auxFeedWaterPumps.add((Pump)state.get(91));
        auxFeedWaterPumps.add((Pump)state.get(92));
        auxFeedWaterPumps.add((Pump)state.get(93));
        auxFeedWaterPumps.add((Pump)state.get(94));
        mainFeederValves.add((WaterValve)state.get(95));
        mainFeederValves.add((WaterValve)state.get(96));
        mainFeederValves.add((WaterValve)state.get(97));
        mainFeederValves.add((WaterValve)state.get(98));
        mainFeederValves.add((WaterValve)state.get(99));
        mainFeederValves.add((WaterValve)state.get(100));
        auxFeederValves.add((WaterValve)state.get(101));
        auxFeederValves.add((WaterValve)state.get(102));
        pcs = (PCS)state.get(103);
        autoControl = (AutoControl)state.get(148);
        autoControl.sdv_cControl.forEach(controller -> {
            controller.setSetpoint(7);
            controller.setActivationTreshold(7);
        });

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
                    mainFeedWaterPumps.forEach(pump -> {
                        pump.update();
                    });
                    auxFeedWaterPumps.forEach(pump -> {
                        pump.update();
                    });
                    
                    fwSuctionHeader.update();
                    
                    dearators.forEach(dearator -> {
                        dearator.update();
                    });
                    
                    pcs.update();
                    NPPSim.core1 = true;
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
        long start[] =  {0, 0};
        printThread = new Thread(() -> {
            try {
                while(run) {
                    
                    Thread.sleep(timeToSleep[0]);
                    while (updateCount < 20 ) {
                        Thread.sleep(10);
                    }
                    //System.out.println("Average time step: " + timeStepLengthCumulative / updateCount + " " + updateCount); //used sometimes for debugging
                    if (updateCount > 20) {
                        if (timeToSleep[0] > 0) {
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
        var wasPaused = isPaused();
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
