package com.darwish.nppsim;

import static com.darwish.nppsim.NPPSim.TG1InletValves;
import static com.darwish.nppsim.NPPSim.TG2InletValves;
import static com.darwish.nppsim.NPPSim.auxFeederValves;
import static com.darwish.nppsim.NPPSim.core;
import static com.darwish.nppsim.NPPSim.dearatorValves;
import static com.darwish.nppsim.NPPSim.mainFeederValves;
import static com.darwish.nppsim.NPPSim.msvLoop1;
import static com.darwish.nppsim.NPPSim.msvLoop2;
import static com.darwish.nppsim.NPPSim.pcs;
import static com.darwish.nppsim.NPPSim.sdv_a;
import static com.darwish.nppsim.NPPSim.sdv_c;
import java.io.Serializable;
import java.util.ArrayList;
import static com.darwish.nppsim.NPPSim.mcc;
import java.util.Arrays;

public class AutoControl extends Component {
    ArrayList<WaterLevelControl> dearatorWaterControl = new ArrayList<>();
    ArrayList<WaterLevelControl> dearatorMakeupControl = new ArrayList<>();
    ArrayList<InletSteamPressureControl> dearatorPressureControl = new ArrayList<>();
    ArrayList<WaterLevelControl> auxFeederControl = new ArrayList<>();
    ArrayList<WaterLevelControl> mainFeederControl = new ArrayList<>();
    ArrayList<OutletSteamPressureControl> sdv_cControl = new ArrayList<>();
    ArrayList<OutletSteamPressureControl> sdv_aControl = new ArrayList<>();
    ArrayList<OutletSteamPressureControl> tgValveControl = new ArrayList<>();
    ArrayList<OutletSteamPressureControl> msv1Control = new ArrayList<>();
    ArrayList<OutletSteamPressureControl> msv2Control = new ArrayList<>();
    AZ1Control az1Control;
    FASRControl fasrControl;
    AutomaticRodController ar1Control, ar2Control, ar12Control, larControl;

    public AutoControl() {
        dearatorValves.forEach(valve -> {
            dearatorWaterControl.add(new WaterLevelControl(valve.drain, new WaterValve[] {valve}));
            dearatorPressureControl.add(new InletSteamPressureControl(valve.drain, new SteamValve[] {((Dearator)valve.drain).steamInlet}));
        });
        pcs.dearatorMakeupValves.forEach(valve -> {
            dearatorMakeupControl.add(new WaterLevelControl(valve.drain, new WaterValve[] {valve}));
        });

        auxFeederValves.forEach(valve -> {
            auxFeederControl.add(new WaterLevelControl(((WaterMixer)valve.drain).drain, new WaterValve[] {valve}));
        });
        mainFeederControl.add(new WaterLevelControl(
            mcc.drum1,
            new WaterValve[] {mainFeederValves.get(0), mainFeederValves.get(1), mainFeederValves.get(2)})
        );
        mainFeederControl.add(new WaterLevelControl(
            mcc.drum2,
            new WaterValve[] {mainFeederValves.get(3), mainFeederValves.get(4), mainFeederValves.get(5)})
        );
        sdv_cControl.add(new OutletSteamPressureControl(mcc.drum1, new SteamValve[] {sdv_c.get(0), sdv_c.get(1), sdv_c.get(2), sdv_c.get(3), sdv_c.get(4), sdv_c.get(5), sdv_c.get(6), sdv_c.get(7)}));
        sdv_cControl.add(new OutletSteamPressureControl(mcc.drum2, new SteamValve[] {sdv_c.get(0), sdv_c.get(1), sdv_c.get(2),sdv_c.get(3), sdv_c.get(4), sdv_c.get(5), sdv_c.get(6), sdv_c.get(7)}));
        sdv_aControl.add(new OutletSteamPressureControl(mcc.drum1, new SteamValve[] {sdv_a.get(0)}));
        sdv_aControl.add(new OutletSteamPressureControl(mcc.drum2, new SteamValve[] {sdv_a.get(1)}));
        msv1Control.add(new OutletSteamPressureControl(mcc.drum1, new SteamValve[] {msvLoop1.get(0)}));
        msv1Control.add(new OutletSteamPressureControl(mcc.drum1, new SteamValve[] {msvLoop1.get(1), msvLoop1.get(2)}));
        msv1Control.add(new OutletSteamPressureControl(mcc.drum1, new SteamValve[] {msvLoop1.get(3), msvLoop1.get(4), msvLoop1.get(5)}));
        msv2Control.add(new OutletSteamPressureControl(mcc.drum2, new SteamValve[] {msvLoop2.get(0)}));
        msv2Control.add(new OutletSteamPressureControl(mcc.drum2, new SteamValve[] {msvLoop2.get(1), msvLoop2.get(2)}));
        msv2Control.add(new OutletSteamPressureControl(mcc.drum2, new SteamValve[] {msvLoop2.get(3), msvLoop2.get(4), msvLoop2.get(5)}));
        tgValveControl.add(new OutletSteamPressureControl(mcc.drum1, new SteamValve[] {TG1InletValves.get(0), TG1InletValves.get(1)}));
        tgValveControl.add(new OutletSteamPressureControl(mcc.drum2, new SteamValve[] {TG1InletValves.get(0), TG1InletValves.get(1)}));
        tgValveControl.add(new OutletSteamPressureControl(mcc.drum1, new SteamValve[] {TG2InletValves.get(0), TG2InletValves.get(1)}));
        tgValveControl.add(new OutletSteamPressureControl(mcc.drum2, new SteamValve[] {TG2InletValves.get(0), TG2InletValves.get(1)}));
        tgValveControl.forEach(controller -> {
            controller.setpoint = 6.90;
        });
        dearatorPressureControl.forEach(controller -> {
            controller.setpoint = 1.0;
        });
        sdv_cControl.forEach(controller -> {
            controller.setEnabled(true);
            controller.setpoint = 6.96; 
            controller.activationTreshold = 6.96;
        });
        sdv_aControl.forEach(controller -> {
        controller.setEnabled(true);
        controller.setpoint = 7.06; 
        controller.activationTreshold = 7.06;
        });
        //activate msv controllers and set setpoints for group 1(0), 2(1-2), 3(3-5)
        msv1Control.forEach(controller -> {
        controller.setEnabled(true);
        });
        msv1Control.get(0).setpoint = 7.36; 
        msv1Control.get(0).activationTreshold = 7.36; 
        msv1Control.get(1).setpoint = 7.45; 
        msv1Control.get(1).activationTreshold = 7.45; 
        msv1Control.get(2).setpoint = 7.55; 
        msv1Control.get(2).activationTreshold = 7.55; 
        
        msv2Control.forEach(controller -> {
        controller.setEnabled(true);
        });
        msv2Control.get(0).setpoint = 7.36;
        msv2Control.get(0).activationTreshold = 7.36; 
        msv2Control.get(1).setpoint = 7.45;
        msv2Control.get(1).activationTreshold = 7.45; 
        msv2Control.get(2).setpoint = 7.55;
        msv2Control.get(2).activationTreshold = 7.55; 
        
        az1Control = new AZ1Control();
        fasrControl = new FASRControl();
        
        ArrayList<ControlRodChannel> ar1 = new ArrayList<>();
        ArrayList<ControlRodChannel> ar2 = new ArrayList<>();
        ArrayList<ControlRodChannel> lar = new ArrayList<>();
        core.coreArray.forEach(row -> {
            row.forEach(channel -> {
                if (channel instanceof ACRChannel) {
                    ar1.add((ControlRodChannel)channel);
                } else if (channel instanceof SACRChannel) {
                    ar2.add((ControlRodChannel)channel);
                } else if (channel instanceof LACChannel) {
                    lar.add((ControlRodChannel)channel);
                }
            });
        });
        ar1Control = new AutomaticRodController(new ControlRodChannel[] {ar1.get(0), ar1.get(1), ar1.get(2), ar1.get(3)});
        ar2Control = new AutomaticRodController(new ControlRodChannel[] {ar2.get(0), ar2.get(1), ar2.get(2), ar2.get(3)});
        ar12Control = new AutomaticRodController(new ControlRodChannel[] {ar1.get(0), ar1.get(1), ar1.get(2), ar1.get(3), ar2.get(0), ar2.get(1), ar2.get(2), ar2.get(3)});
        larControl = new AutomaticRodController(new ControlRodChannel[] {lar.get(0), lar.get(1), lar.get(2), lar.get(3), lar.get(4), lar.get(5), lar.get(6), lar.get(7), lar.get(8), lar.get(9), lar.get(10), lar.get(11)});
    }

    public void update() {
        sdv_c.forEach(valve -> {
            valve.setLocked(valve.drain.getPressure() > 0.023 || valve.drain.getSteamTemperature() > 100.0);
        });
        if(sdv_cControl.get(0).isEnabled()) {
            Runnable controller = mcc.drum1.getPressure() > mcc.drum2.getPressure() ? sdv_cControl.get(0)::update: sdv_cControl.get(1)::update;
            controller.run();
        }
        if(tgValveControl.get(0).isEnabled()) {
            Runnable controller = mcc.drum1.getPressure() > mcc.drum2.getPressure() ? tgValveControl.get(0)::update: tgValveControl.get(1)::update;
            controller.run();
        }
        if(tgValveControl.get(2).isEnabled()) {
            Runnable controller = mcc.drum1.getPressure() > mcc.drum2.getPressure() ? tgValveControl.get(2)::update: tgValveControl.get(3)::update;
            controller.run();
        }
        dearatorWaterControl.forEach(controller -> {
            if (controller.isEnabled()) {
                controller.update();
            }
        });
        dearatorMakeupControl.forEach(controller -> {
            if (controller.isEnabled()) {
                controller.update();
            }
        });
        dearatorPressureControl.forEach(controller -> {
            if (controller.isEnabled()) {
                controller.update();
            }
        });
        auxFeederControl.forEach(controller -> {
            if (controller.isEnabled()) {
                controller.update();
            }
        });
        mainFeederControl.forEach(controller -> {
            if(controller.isEnabled()) {
                controller.update();
            }
        });
        sdv_aControl.forEach(controller -> {
            if(controller.isEnabled()) {
                controller.update();
            }
        });
        msv1Control.forEach(controller -> {
            if(controller.isEnabled()) {
                controller.update();
            }
        });
        msv2Control.forEach(controller -> {
            if(controller.isEnabled()) {
                controller.update();
            }
        });
        az1Control.update();
        fasrControl.update();
        if (ar1Control.isEnabled()) {   //only one of these should be enabled at a time
            ar1Control.update();
        } else if (ar2Control.isEnabled()) {
            ar2Control.update();
        } else if (ar12Control.isEnabled()) {
            ar12Control.update();
        }
        if (larControl.isEnabled()) {
            larControl.update();
        }
    }

    class WaterLevelControl implements Serializable {
        WaterValve[] valveArray;
        Connectable target;
        private boolean enabled = false;
        private double previousLevel, currentLevel, deltaLevel, deltaLevelSetpoint, setpoint = 0.0;
    
        public WaterLevelControl(Connectable target, WaterValve[] valveArray) {
            this.valveArray = valveArray;
            this.target = target;
        }
        
        public void setWaterValveStateByLevel(double level, double setPoint) {
            
        }
        
        public void setWaterValveStateByLevel(double level) {
            setWaterValveStateByLevel(level, 0.0);
        }
    
        public void update() {
            currentLevel = target.getWaterLevel();
            deltaLevel = currentLevel - previousLevel;
            deltaLevelSetpoint = (setpoint - currentLevel) / 200;
            if (deltaLevel > deltaLevelSetpoint + 0.001) {
                for (WaterValve valve: valveArray) {
                    valve.setAutoState(0);
                }
            } else if (deltaLevel < deltaLevelSetpoint - 0.001) {
                for (WaterValve valve: valveArray) {
                    valve.setAutoState(2);
                }
            } else {
                for (WaterValve valve: valveArray) {
                    valve.setAutoState(1);
                }
            }
            previousLevel = currentLevel;
            //System.out.println(valveArray[0].getState() + " DL " + deltaLevel + " DLS " + deltaLevelSetpoint + " WL " + target.getWaterLevel());
        }
    
        public void setSetpoint(double setpoint) {
            this.setpoint = setpoint;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            if (!enabled) {
                for (WaterValve valve: valveArray) {
                    valve.setAutoState(1);
                }
            }
        }
    }
    
    class InletSteamPressureControl implements Serializable {
        SteamValve[] valveArray;
        Connectable target;
        private boolean enabled = false;
        protected double previousPressure, currentPressure, deltaPressure, deltaPressureSetpoint, setpoint = 0.0;

        public InletSteamPressureControl(Connectable target, SteamValve[] valveArray) {
            this.valveArray = valveArray;
            this.target = target;
        }

        public void update() {
            currentPressure = target.getPressure() - 0.02;
            deltaPressure = currentPressure - previousPressure;
            deltaPressureSetpoint = (setpoint - currentPressure) / 200;
            if (deltaPressure > deltaPressureSetpoint + 0.0001) {
                for (SteamValve valve: valveArray) {
                    valve.setAutoState(0);
                }
            } else if (deltaPressure < deltaPressureSetpoint - 0.0001) {
                for (SteamValve valve: valveArray) {
                    valve.setAutoState(2);
                }
            } else {
                for (SteamValve valve: valveArray) {
                    valve.setAutoState(1);
                }
            }
            previousPressure = currentPressure;
        }

        public void setSetpoint(double setpoint) {
            this.setpoint = setpoint;
        }
        
        public double getSetpoint() {
            return setpoint;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            if (!enabled) {
                for (SteamValve valve: valveArray) {
                    valve.setAutoState(1);
                }
            }
        }
    }

    class OutletSteamPressureControl extends InletSteamPressureControl {
        private double activationTreshold = 0.0;

        public OutletSteamPressureControl(Connectable target, SteamValve[] valveArray) {
            super(target, valveArray);
        }

        @Override
        public void update() {
            currentPressure = target.getPressure() - 0.02;
            deltaPressure = currentPressure - previousPressure;
            deltaPressureSetpoint = (setpoint - currentPressure) / 200;
            if (deltaPressure < deltaPressureSetpoint - 0.0001) {
                for (SteamValve valve: valveArray) {
                    valve.setAutoState(0);
                }
            } else if (deltaPressure > deltaPressureSetpoint + 0.0001 && (target.getPressure() > activationTreshold || valveArray[0].getPosition() != 0.0)) {
                for (SteamValve valve: valveArray) {
                    valve.setAutoState(2);
                }
            } else {
                for (SteamValve valve: valveArray) {
                    valve.setAutoState(1);
                }
            }
            previousPressure = currentPressure;
            //System.out.println(valveArray[0].getAutoState() + " DP " + deltaPressure + " DPS " + deltaPressureSetpoint + " WL " + target.getPressure());
        } 

        public void setActivationTreshold(double treshold) {
            this.activationTreshold = treshold;
        }
    }
    
    class AZ1Control implements Serializable {
        private boolean tripped = false;
        boolean persistentReactivity = false;
        
        public void update() {
            if (mcc.drum1.getPressure() > 7.26 || mcc.drum2.getPressure() > 7.26) {
                trip();
            }
            if (mcc.drum1.getWaterLevel() > 30 || mcc.drum2.getWaterLevel() > 30 ) {
                trip();
            }
            if (mcc.drum1.getWaterLevel() < -40 || mcc.drum2.getWaterLevel() < -40) {
                trip();
            }
            if (core.getReactivity() > 0.02) {
                if (persistentReactivity) {
                    trip();
                }
                
                persistentReactivity = true;
            } else {
                persistentReactivity = false;
            }
            if (core.getThermalPower() > 5800) {
                trip();
            }
        }
        
        public void trip() {
            tripped = true;
            core.coreArray.forEach(row -> {
                row.forEach(channel -> {
                    if (channel instanceof ControlRodChannel) {
                        ((ControlRodChannel) channel).setAutoState(2);
                        ((ControlRodChannel) channel).setState(2);
                        ((ControlRodChannel) channel).setScram(tripped);
                    }
                });
            });
        }   
        
        public void reset() {
            tripped = false;
            core.coreArray.forEach(row -> {
                row.forEach(channel -> {
                    if (channel instanceof ControlRodChannel) {
                        ((ControlRodChannel) channel).setAutoState(1);
                        ((ControlRodChannel) channel).setState(1);
                        ((ControlRodChannel) channel).setScram(tripped);
                    }
                });
            });
        }
        
        public boolean isTripped() {
            return tripped;
        }
    }
    
     class FASRControl implements Serializable {
        private boolean tripped = false, sequenceLock = false;
        private ArrayList<FASRChannel> channels = new ArrayList<>();
        
        public FASRControl() {
            core.coreArray.forEach(row -> {
                row.forEach(channel -> {
                    if (channel instanceof FASRChannel) {
                        channels.add((FASRChannel)channel);
                    }
                });
            });
        }
        
        public void update() {
            for (FASRChannel channel: channels) {
                if (channel.getPosition() > 0) {
                    sequenceLock = true;
                    return;
                }
            }
            sequenceLock = false;
        }
        
        public void trip() {
            tripped = true;
            core.coreArray.forEach(row -> {
                row.forEach(channel -> {
                    if (channel instanceof ControlRodChannel) {
                        ((ControlRodChannel) channel).setAutoState(2);
                        ((ControlRodChannel) channel).setState(2);
                        ((ControlRodChannel) channel).setScram(tripped);
                    }
                });
            });
            channels.forEach(channel -> {
                channel.setFastScram(true);
            });
        }   
        
        public void reset() {
            tripped = false;
            core.coreArray.forEach(row -> {
                row.forEach(channel -> {
                    if (channel instanceof ControlRodChannel) {
                        ((ControlRodChannel) channel).setAutoState(1);
                        ((ControlRodChannel) channel).setState(1);
                        ((ControlRodChannel) channel).setScram(tripped);
                    }
                });
            });
            channels.forEach(channel -> {
                channel.setFastScram(false);
            });
        }
        
        public boolean isTripped() {
            return tripped;
        }
        
        public boolean getSequenceBlock() {
            return sequenceLock;
        }
    }
    
    class AutomaticRodController implements Serializable {
        private double setpoint = 0.0;
        double ro, roSetpoint, thermalPower, averagePower;
        final ControlRodChannel[] linkedChannels;
        final ArrayList<ControlRodChannel> toControl = new ArrayList<>();
        private boolean enabled = false, error = false, limit = false, busy = false;
        public AutomaticRodController(ControlRodChannel[] linkedChannels) {
            this.linkedChannels = linkedChannels;
        }
        
        public void update() {
            thermalPower = core.getThermalPower();
            toControl.clear();
            toControl.addAll(Arrays.asList(linkedChannels));
            var upperLimit = false;
            var lowerLimit = false;
            var checkError = false;
            var checkBusy = false;
            for (ControlRodChannel channel: linkedChannels) {
                averagePower += channel.getNeutronPopulation()[0];
                if (channel.getPosition() == 0) {
                    upperLimit = true;
                } else if (channel.getPosition() == 1) {
                    lowerLimit = true;
                }
            }
            averagePower /= linkedChannels.length;
            ro = core.getReactivity();
            roSetpoint = 0 + ((setpoint - thermalPower) / 20000); 
            if (ro  > roSetpoint + 0.00005) {
                limit = lowerLimit;
                if (limit) {
                    checkError = true;
                }
                for (ControlRodChannel channel: linkedChannels) {
                    if (channel.getNeutronPopulation()[0] < averagePower * 0.95) {
                        toControl.remove(channel);
                    }
                }
                if (toControl.isEmpty()) {
                    toControl.addAll(Arrays.asList(linkedChannels));
                }
                for (ControlRodChannel rod: toControl) {
                    rod.setAutoState(2);
                    if (rod.getPosition() < 1) {
                        checkBusy = true;
                    }
                }
            } else if (ro < roSetpoint - 0.00005) {
                limit = upperLimit;
                if (fasrControl.getSequenceBlock()) {
                    error = true;
                    busy = false;
                    for (ControlRodChannel rod: linkedChannels) {
                        rod.setAutoState(1);
                    }
                    return;
                }
                for (ControlRodChannel channel: linkedChannels) {
                    if (channel.getNeutronPopulation()[0] > averagePower * 1.05) {
                        toControl.remove(channel);
                    }
                }
                if (toControl.isEmpty()) {
                    toControl.addAll(Arrays.asList(linkedChannels));
                }
                for (ControlRodChannel rod: toControl) {
                    rod.setAutoState(0);
                    if (rod.getPosition() > 0) {
                        checkBusy = true;
                    }
                }
            } else {
                limit = false;
                busy = false;
                for (ControlRodChannel rod: linkedChannels) {
                    rod.setAutoState(1);
                }
            }
            error = checkError;
            busy = checkBusy;
        }
        
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            if (!enabled) {
                for (ControlRodChannel channel: linkedChannels) {
                    channel.setAutoState(1);
                }
            }
        }
        
        public void setSetpoint(double setpoint) {
            this.setpoint = setpoint;
        }
        
        public double getSetpoint() {
            return setpoint;
        }
        
        public boolean hasError() {
            return error;
        }
        
        public boolean onLimit() {
            return limit;
        }
        
        public boolean isBusy() {
            return busy;
        }
    }
}
