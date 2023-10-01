package com.darwish.nppsim;

import static com.darwish.nppsim.NPPSim.TG1InletValves;
import static com.darwish.nppsim.NPPSim.TG2InletValves;
import static com.darwish.nppsim.NPPSim.autoControl;
import static com.darwish.nppsim.NPPSim.mcc;
import static com.darwish.nppsim.NPPSim.sdv_c;
import static com.darwish.nppsim.NPPSim.steamPiping;
import static com.darwish.nppsim.NPPSim.tg1;
import static com.darwish.nppsim.NPPSim.tg2;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;

public class TGUI extends javax.swing.JFrame implements UIUpdateable {
    private final Annunciator annunciator;
    private boolean debounce = false;
    /**
     * Creates new form TGUI
     */
    public TGUI() {
        initComponents();
        this.setTitle("Turbine-Generators");
        annunciator = new Annunciator(annunciatorPanel);
        
        ((JSpinner.DefaultEditor)sdvcSpinner1.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor)sdvcSpinner2.getEditor()).getTextField().setEditable(false);
        sdvcSpinner1.addChangeListener((ChangeEvent e) -> {
            SteamValve currentSelection = sdv_c.get((int)sdvcSpinner1.getValue() - 1);
            switch (currentSelection.getState()) {
                case 0:
                    sdvcClose1.setSelected(true);
                    break;
                case 1:
                    sdvcStop1.setSelected(true);
                    break;
                case 2:
                    sdvcOpen1.setSelected(true);
                    break;  
            }
        });
        sdvcSpinner2.addChangeListener((ChangeEvent e) -> {
            SteamValve currentSelection = sdv_c.get((int)sdvcSpinner2.getValue() - 1);
            switch (currentSelection.getState()) {
                case 0:
                    sdvcClose2.setSelected(true);
                    break;
                case 1:
                    sdvcStop2.setSelected(true);
                    break;
                case 2:
                    sdvcOpen2.setSelected(true);
                    break;  
            }
        });
        
        linear1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        linear1.setFrameVisible(false);
        linear1.setLcdVisible(false);
        linear1.setMaxValue(1000.0);
        linear1.setOrientation(eu.hansolo.steelseries.tools.Orientation.HORIZONTAL);
        linear1.setTitle("TG-1 Load");
        linear1.setTrackVisible(true);
        linear1.setTrackStop(1000);
        linear1.setTrackStart(800);
        linear1.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        linear1.setTrackStartColor(new java.awt.Color(255, 0, 0));
        linear1.setTrackStopColor(new java.awt.Color(255, 0, 0));
        linear1.setUnitString("MW");
        linear1.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        linear2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        linear2.setFrameVisible(false);
        linear2.setLcdVisible(false);
        linear2.setLedVisible(false);
        linear2.setMaxValue(1600.0);
        linear2.setOrientation(eu.hansolo.steelseries.tools.Orientation.HORIZONTAL);
        linear2.setTitle("TG-1 Steam Flow");
        linear2.setUnitString("kg/s");
        linear2.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        linear3.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        linear3.setFrameVisible(false);
        linear3.setLcdVisible(false);
        linear3.setMaxValue(1000.0);
        linear3.setOrientation(eu.hansolo.steelseries.tools.Orientation.HORIZONTAL);
        linear3.setTitle("TG-2 Load");
        linear3.setTrackVisible(true);
        linear3.setTrackStop(1000);
        linear3.setTrackStart(800);
        linear3.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        linear3.setTrackStartColor(new java.awt.Color(255, 0, 0));
        linear3.setTrackStopColor(new java.awt.Color(255, 0, 0));
        linear3.setUnitString("MW");
        linear3.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        linear4.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        linear4.setFrameVisible(false);
        linear4.setLcdVisible(false);
        linear4.setLedVisible(false);
        linear4.setMaxValue(1600.0);
        linear4.setTitle("TG-2 Steam Flow");
        linear4.setUnitString("kg/s");
        linear4.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);
        radial4.setValue(60);
        radial1.setValue(20);
        initializeDialUpdateThread();
        precisionController();
        
        //set initial values
        double setpoint = autoControl.tgValveControl.get(0).getSetpoint();
        rpm1B.setLcdValue(setpoint);
        rpm1B1.setLcdValue(setpoint);
        sdvcManual.setSelected(!autoControl.sdv_cControl.get(0).isEnabled());
        autoSteamPressure1On.setSelected(autoControl.tgValveControl.get(0).isEnabled());
        autoSteamPressure1On1.setSelected(autoControl.tgValveControl.get(2).isEnabled());
    }
    
    @Override
    public void update() {
        checkAlarms();
        if (this.isVisible()) {
            java.awt.EventQueue.invokeLater(() -> {
                double totalFlow1 = 0.0;
                double totalFlow2 = 0.0;
                for (int i = 0; i < 4; i++) {
                    totalFlow1 += sdv_c.get(i).getFlowRate();
                }
                for (int i = 4; i < 8; i++) {
                    totalFlow2 += sdv_c.get(i).getFlowRate();
                }
                interlockLed1.setLedOn(sdv_c.get(0).isLocked());
                interlockLed2.setLedOn(sdv_c.get(4).isLocked());
                sdvcFlow1.setLcdValue(sdv_c.get((int)sdvcSpinner1.getValue() - 1).getFlowRate());
                sdvcFlow2.setLcdValue(sdv_c.get((int)sdvcSpinner2.getValue() - 1).getFlowRate());
                sdvcTotal1.setLcdValue(totalFlow1);
                sdvcTotal2.setLcdValue(totalFlow2);
            });
        }
    }

    @Override
    public void initializeDialUpdateThread() {
        UI.uiThreads.add(
            new Thread(() -> {
                try {
                    while (true) {
                        annunciator.update();
                        if (this.isVisible()) {
                            java.awt.EventQueue.invokeLater(() -> {
                                float alignment1 = tg1.getGenAligmnent();
                                float rpm1 = tg1.getRpm();
                                float alignment2 = tg2.getGenAligmnent();
                                float rpm2 = tg2.getRpm();

                                if (rpm1 > 2800) {
                                    radial1.setValue(alignment1);
                                }
                                radial2.setValue(tg1.getRpm());
                                if (rpm2 > 2800) {
                                    radial4.setValue(alignment2);
                                }
                                radial1.setUserLedOn(alignment1 > 48.5 && alignment1 < 51.5 && rpm1 >= 2997.5 && rpm1 <= 3002.5);
                                radial4.setUserLedOn(alignment2 > 48.5 && alignment2 < 51.5 && rpm2 >= 2997.5 && rpm2 <= 3002.5);
                                radial3.setValue(tg2.getRpm());
                                radial2Top1.setValue(TG1InletValves.get(0).getPosition() * 100);
                                radial2Top2.setValue(TG2InletValves.get(0).getPosition() * 100);

                                linear1.setValue(tg1.getGeneratorLoad());
                                linear2.setValue(tg1.getSteamInflow() * 20);
                                linear3.setValue(tg2.getGeneratorLoad());
                                linear4.setValue(tg2.getSteamInflow() * 20);

                                pressure1.setValue(mcc.drum1.getPressure());
                                pressure2.setValue(mcc.drum2.getPressure());

                                valvePos1.setValue(sdv_c.get(0).getPosition() * 100);
                                valvePos2.setValue(sdv_c.get(1).getPosition() * 100);
                                valvePos3.setValue(sdv_c.get(2).getPosition() * 100);
                                valvePos4.setValue(sdv_c.get(3).getPosition() * 100);
                                valvePos5.setValue(sdv_c.get(4).getPosition() * 100);
                                valvePos6.setValue(sdv_c.get(5).getPosition() * 100);
                                valvePos7.setValue(sdv_c.get(6).getPosition() * 100);
                                valvePos8.setValue(sdv_c.get(7).getPosition() * 100);
                            });
                        }
                        if (this.isFocused()) {
                            Thread.sleep(UI.getUpdateRate());
                        } else {
                            Thread.sleep(200);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            })
        );
        UI.uiThreads.get(UI.uiThreads.size() - 1).start();
    }
    
    @Override
    public void setVisibility(boolean visible) {
        this.setVisible(visible);
    }
    
    @Override
    public void discard() {
        this.setVisible(false);
    }
    
    @Override
    public void acknowledge() {
        annunciator.acknowledge();
    }
    
    private void checkAlarms() { 
        if (tg1.getRpm() > 3100) {
            annunciator.trigger(tg1Speed);
        } else {
            annunciator.reset(tg1Speed);
        }
        if (tg2.getRpm() > 3100) {
            annunciator.trigger(tg2Speed);
        } else {
            annunciator.reset(tg2Speed);
        }
        if (tg1.isTripped()) {
            annunciator.trigger(tg1Trip);
        } else {
            annunciator.reset(tg1Trip);
        }
        if (tg2.isTripped()) {
            annunciator.trigger(tg2Trip);
        } else {
            annunciator.reset(tg2Trip);
        }
        if (tg1.isReversePower()) {
            annunciator.trigger(tg1Rev);
        } else {
            annunciator.reset(tg1Rev);
        }
        if (tg2.isReversePower()) {
            annunciator.trigger(tg2Rev);
        } else {
            annunciator.reset(tg2Rev);
        }
        if (steamPiping.getSteamTemperature() < 230) {
            annunciator.trigger(steamTemp);
        } else {
            annunciator.reset(steamTemp);
        }
        annunciator.setTrigger(steamPiping.getPressure() < 6.0, steamPress);
        annunciator.setTrigger(tg1.condenser.getPressure() > 0.015, lowVacuum1);
        annunciator.setTrigger(tg2.condenser.getPressure() > 0.015, lowVacuum2);
        annunciator.setTrigger(tg1.condenser.getPressure() > 0.025, noVacuum1);
        annunciator.setTrigger(tg2.condenser.getPressure() > 0.025, noVacuum2);
    }

    private void precisionController() {
        UI.uiThreads.add(
            new Thread(() -> {
                while(true) {
                    try {
                        if (debounce) {
                            Thread.sleep(50);
                            debounce = false;
                        }
                        if (precisionIncrement2.getModel().isPressed() || precisionIncrement3.getModel().isPressed()) {
                            precisionIncrement3ActionPerformed(null);
                        } else if (precisionDecrement2.getModel().isPressed() || precisionDecrement3.getModel().isPressed()) {
                            precisionDecrement3ActionPerformed(null);
                        } else if (precisionDecrement.getModel().isPressed()) {
                            precisionDecrementActionPerformed(null);
                        } else if (precisionDecrement1.getModel().isPressed()) {
                            precisionDecrement1ActionPerformed(null);
                        } else if (precisionIncrement.getModel().isPressed()) {
                            precisionIncrementActionPerformed(null);
                        } else if (precisionIncrement1.getModel().isPressed()) {
                            precisionIncrement1ActionPerformed(null);
                        }
                        Thread.sleep(50);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            })
        );
        UI.uiThreads.get(UI.uiThreads.size() - 1).start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        buttonGroup6 = new javax.swing.ButtonGroup();
        buttonGroup7 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        radial2 = new eu.hansolo.steelseries.gauges.Radial();
        radial1 = new eu.hansolo.steelseries.gauges.Radial();
        jPanel4 = new javax.swing.JPanel();
        radial2Top1 = new eu.hansolo.steelseries.gauges.Radial2Top();
        tgValvesOpen = new javax.swing.JRadioButton();
        tgValvesStop = new javax.swing.JRadioButton();
        tgValvesClose = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        precisionIncrement = new javax.swing.JButton();
        precisionDecrement = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        autoSteamPressure1On = new javax.swing.JRadioButton();
        autoSteamPressure1Off = new javax.swing.JRadioButton();
        jLabel11 = new javax.swing.JLabel();
        rpm1B = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        precisionDecrement2 = new javax.swing.JButton();
        precisionIncrement2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        radial3 = new eu.hansolo.steelseries.gauges.Radial();
        radial4 = new eu.hansolo.steelseries.gauges.Radial();
        jPanel6 = new javax.swing.JPanel();
        radial2Top2 = new eu.hansolo.steelseries.gauges.Radial2Top();
        tgValvesOpen1 = new javax.swing.JRadioButton();
        tgValvesStop1 = new javax.swing.JRadioButton();
        tgValvesClose1 = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        precisionIncrement1 = new javax.swing.JButton();
        precisionDecrement1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        autoSteamPressure1On1 = new javax.swing.JRadioButton();
        autoSteamPressure1Off1 = new javax.swing.JRadioButton();
        jLabel19 = new javax.swing.JLabel();
        rpm1B1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        precisionDecrement3 = new javax.swing.JButton();
        precisionIncrement3 = new javax.swing.JButton();
        linear1 = new eu.hansolo.steelseries.gauges.Linear();
        linear2 = new eu.hansolo.steelseries.gauges.Linear();
        linear3 = new eu.hansolo.steelseries.gauges.Linear();
        linear4 = new eu.hansolo.steelseries.gauges.Linear();
        annunciatorPanel = new javax.swing.JPanel();
        tg1Trip = new javax.swing.JTextField();
        tg2Trip = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        steamTemp = new javax.swing.JTextField();
        steamPress = new javax.swing.JTextField();
        tg1Rev = new javax.swing.JTextField();
        tg2Rev = new javax.swing.JTextField();
        jTextField16 = new javax.swing.JTextField();
        jTextField18 = new javax.swing.JTextField();
        lowVacuum1 = new javax.swing.JTextField();
        lowVacuum2 = new javax.swing.JTextField();
        tg1Speed = new javax.swing.JTextField();
        tg2Speed = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        jTextField14 = new javax.swing.JTextField();
        noVacuum1 = new javax.swing.JTextField();
        noVacuum2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        sdvcAuto = new javax.swing.JRadioButton();
        sdvcManual = new javax.swing.JRadioButton();
        jPanel20 = new javax.swing.JPanel();
        sdvcOpen1 = new javax.swing.JRadioButton();
        sdvcClose1 = new javax.swing.JRadioButton();
        jLabel38 = new javax.swing.JLabel();
        sdvcSpinner1 = new javax.swing.JSpinner();
        jLabel40 = new javax.swing.JLabel();
        sdvcStop1 = new javax.swing.JRadioButton();
        sdvcFlow1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        interlockLed1 = new eu.hansolo.steelseries.extras.Led();
        jLabel8 = new javax.swing.JLabel();
        sdvcTotal1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        sdvcOpen2 = new javax.swing.JRadioButton();
        sdvcClose2 = new javax.swing.JRadioButton();
        jLabel39 = new javax.swing.JLabel();
        sdvcSpinner2 = new javax.swing.JSpinner();
        jLabel41 = new javax.swing.JLabel();
        sdvcStop2 = new javax.swing.JRadioButton();
        sdvcFlow2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        interlockLed2 = new eu.hansolo.steelseries.extras.Led();
        jLabel13 = new javax.swing.JLabel();
        sdvcTotal2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        sdvcManualLed = new eu.hansolo.steelseries.extras.Led();
        jLabel16 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        valvePos5 = new eu.hansolo.steelseries.gauges.RadialBargraph();
        valvePos6 = new eu.hansolo.steelseries.gauges.RadialBargraph();
        valvePos7 = new eu.hansolo.steelseries.gauges.RadialBargraph();
        valvePos8 = new eu.hansolo.steelseries.gauges.RadialBargraph();
        jPanel13 = new javax.swing.JPanel();
        valvePos1 = new eu.hansolo.steelseries.gauges.RadialBargraph();
        valvePos2 = new eu.hansolo.steelseries.gauges.RadialBargraph();
        valvePos3 = new eu.hansolo.steelseries.gauges.RadialBargraph();
        valvePos4 = new eu.hansolo.steelseries.gauges.RadialBargraph();
        pressure1 = new eu.hansolo.steelseries.gauges.Linear();
        pressure2 = new eu.hansolo.steelseries.gauges.Linear();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();

        setBackground(new java.awt.Color(204, 0, 153));
        setSize(new java.awt.Dimension(1366, 768));

        jPanel3.setBackground(UI.BACKGROUND);
        jPanel3.setPreferredSize(new java.awt.Dimension(1366, 768));

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "Turbine-Generator 1");

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, "SYNC");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        radial2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        radial2.setFrameType(eu.hansolo.steelseries.tools.FrameType.SQUARE);
        radial2.setFrameVisible(false);
        radial2.setLcdVisible(false);
        radial2.setMaxNoOfMajorTicks(14);
        radial2.setMaxNoOfMinorTicks(14);
        radial2.setMaxValue(3500.0);
        radial2.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.GRAY);
        radial2.setPointerShadowVisible(false);
        radial2.setPostsVisible(false);
        radial2.setPreferredSize(new java.awt.Dimension(160, 160));
        radial2.setThreshold(3100.0);
        radial2.setTicklabelOrientation(eu.hansolo.steelseries.tools.TicklabelOrientation.HORIZONTAL);
        radial2.setTitle("RPM");
        radial2.setTrackSection(200.0);
        radial2.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        radial2.setTrackStart(3100.0);
        radial2.setTrackStartColor(new java.awt.Color(255, 0, 0));
        radial2.setTrackStop(3500.0);
        radial2.setUnitString("");

        radial1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        radial1.setFrameType(eu.hansolo.steelseries.tools.FrameType.SQUARE);
        radial1.setFrameVisible(false);
        radial1.setLcdVisible(false);
        radial1.setLedVisible(false);
        radial1.setPointerShadowVisible(false);
        radial1.setPreferredSize(new java.awt.Dimension(160, 160));
        radial1.setTicklabelsVisible(false);
        radial1.setTitle("Synchroscope");
        radial1.setTitleAndUnitFont(new java.awt.Font("Serif", 0, 12)); // NOI18N
        radial1.setTrackSectionColor(new java.awt.Color(0, 153, 51));
        radial1.setTrackStart(48.5);
        radial1.setTrackStartColor(new java.awt.Color(0, 153, 51));
        radial1.setTrackStop(51.5);
        radial1.setTrackStopColor(new java.awt.Color(0, 153, 51));
        radial1.setTrackVisible(true);
        radial1.setUnitString("Slow <--> Fast");
        radial1.setUserLedColor(eu.hansolo.steelseries.tools.LedColor.GREEN_LED);
        radial1.setUserLedOn(true);
        radial1.setUserLedVisible(true);

        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        radial2Top1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        radial2Top1.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        radial2Top1.setFrameVisible(false);
        radial2Top1.setLcdBackgroundVisible(false);
        radial2Top1.setLcdVisible(false);
        radial2Top1.setLedVisible(false);
        radial2Top1.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        radial2Top1.setPointerShadowVisible(false);
        radial2Top1.setPreferredSize(new java.awt.Dimension(100, 100));
        radial2Top1.setTitle("Position");
        radial2Top1.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        radial2Top1.setTitleAndUnitFontEnabled(true);
        radial2Top1.setUnitString("%");

        buttonGroup1.add(tgValvesOpen);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesOpen, "Open");
        tgValvesOpen.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesOpenItemStateChanged(evt);
            }
        });
        tgValvesOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesOpenActionPerformed(evt);
            }
        });

        buttonGroup1.add(tgValvesStop);
        tgValvesStop.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesStop, "Stop");
        tgValvesStop.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesStopItemStateChanged(evt);
            }
        });
        tgValvesStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesStopActionPerformed(evt);
            }
        });

        buttonGroup1.add(tgValvesClose);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesClose, "Close");
        tgValvesClose.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesCloseItemStateChanged(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Inlet Valves");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 10, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(112, 112, 112))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tgValvesOpen)
                            .addComponent(tgValvesStop)
                            .addComponent(tgValvesClose))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(radial2Top1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(7, 7, 7)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(tgValvesOpen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesStop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesClose))
                    .addComponent(radial2Top1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, "Precision Control");

        org.openide.awt.Mnemonics.setLocalizedText(precisionIncrement, "+");
        precisionIncrement.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionIncrement.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionIncrement.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionIncrement.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionIncrement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionIncrementActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(precisionDecrement, "-");
        precisionDecrement.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionDecrement.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionDecrement.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionDecrement.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionDecrement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionDecrementActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(precisionIncrement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(precisionDecrement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(precisionIncrement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precisionDecrement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(jButton5, "DESYNC");
        jButton5.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton9, "RESET");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setBackground(javax.swing.UIManager.getDefaults().getColor("nb.versioning.conflicted.color"));
        org.openide.awt.Mnemonics.setLocalizedText(jButton10, "TRIP");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jPanel9.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup6.add(autoSteamPressure1On);
        org.openide.awt.Mnemonics.setLocalizedText(autoSteamPressure1On, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.autoSteamPressure1On.text")); // NOI18N
        autoSteamPressure1On.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoSteamPressure1OnItemStateChanged(evt);
            }
        });
        autoSteamPressure1On.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoSteamPressure1OnActionPerformed(evt);
            }
        });

        buttonGroup6.add(autoSteamPressure1Off);
        autoSteamPressure1Off.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(autoSteamPressure1Off, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.autoSteamPressure1Off.text")); // NOI18N
        autoSteamPressure1Off.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoSteamPressure1OffItemStateChanged(evt);
            }
        });
        autoSteamPressure1Off.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoSteamPressure1OffActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jLabel11.text")); // NOI18N

        rpm1B.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        rpm1B.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        rpm1B.setLcdDecimals(2);
        rpm1B.setLcdUnitString(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.rpm1B.lcdUnitString")); // NOI18N
        rpm1B.setLcdUnitStringVisible(false);
        rpm1B.setLcdValue(6.96);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel17, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jLabel17.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel18, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jLabel18.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(precisionDecrement2, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.precisionDecrement2.text")); // NOI18N
        precisionDecrement2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionDecrement2.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionDecrement2.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionDecrement2.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionDecrement2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionDecrement2ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(precisionIncrement2, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.precisionIncrement2.text")); // NOI18N
        precisionIncrement2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionIncrement2.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionIncrement2.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionIncrement2.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionIncrement2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionIncrement2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel17)
                        .addGap(7, 7, 7)
                        .addComponent(rpm1B, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(autoSteamPressure1On)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(autoSteamPressure1Off)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(precisionIncrement2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(precisionDecrement2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel11)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rpm1B, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(precisionDecrement2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precisionIncrement2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(autoSteamPressure1Off)
                    .addComponent(autoSteamPressure1On))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton9))
                            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(radial2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(radial1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 1, Short.MAX_VALUE)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(radial2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(radial1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, "Turbine-Generator 2");

        org.openide.awt.Mnemonics.setLocalizedText(jButton3, "SYNC");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        radial3.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        radial3.setFrameType(eu.hansolo.steelseries.tools.FrameType.SQUARE);
        radial3.setFrameVisible(false);
        radial3.setLcdVisible(false);
        radial3.setMaxNoOfMajorTicks(14);
        radial3.setMaxNoOfMinorTicks(14);
        radial3.setMaxValue(3500.0);
        radial3.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.GRAY);
        radial3.setPointerShadowVisible(false);
        radial3.setPostsVisible(false);
        radial3.setPreferredSize(new java.awt.Dimension(160, 160));
        radial3.setThreshold(3100.0);
        radial3.setTicklabelOrientation(eu.hansolo.steelseries.tools.TicklabelOrientation.HORIZONTAL);
        radial3.setTitle("RPM");
        radial3.setTrackSection(200.0);
        radial3.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        radial3.setTrackStart(3100.0);
        radial3.setTrackStartColor(new java.awt.Color(255, 0, 0));
        radial3.setTrackStop(3500.0);
        radial3.setUnitString("");

        radial4.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        radial4.setFrameType(eu.hansolo.steelseries.tools.FrameType.SQUARE);
        radial4.setFrameVisible(false);
        radial4.setLcdVisible(false);
        radial4.setLedVisible(false);
        radial4.setPointerShadowVisible(false);
        radial4.setPreferredSize(new java.awt.Dimension(160, 160));
        radial4.setTicklabelsVisible(false);
        radial4.setTitle("Synchroscope");
        radial4.setTitleAndUnitFont(new java.awt.Font("Serif", 0, 12)); // NOI18N
        radial4.setTrackSectionColor(new java.awt.Color(0, 153, 51));
        radial4.setTrackStart(48.5);
        radial4.setTrackStartColor(new java.awt.Color(0, 153, 51));
        radial4.setTrackStop(51.5);
        radial4.setTrackStopColor(new java.awt.Color(0, 153, 51));
        radial4.setTrackVisible(true);
        radial4.setUnitString("Slow <--> Fast");
        radial4.setUserLedColor(eu.hansolo.steelseries.tools.LedColor.GREEN_LED);
        radial4.setUserLedOn(true);
        radial4.setUserLedVisible(true);

        jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        radial2Top2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        radial2Top2.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        radial2Top2.setFrameVisible(false);
        radial2Top2.setLcdBackgroundVisible(false);
        radial2Top2.setLcdVisible(false);
        radial2Top2.setLedVisible(false);
        radial2Top2.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        radial2Top2.setPointerShadowVisible(false);
        radial2Top2.setPreferredSize(new java.awt.Dimension(100, 100));
        radial2Top2.setTitle(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.radial2Top2.title")); // NOI18N
        radial2Top2.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        radial2Top2.setTitleAndUnitFontEnabled(true);
        radial2Top2.setUnitString(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.radial2Top2.unitString")); // NOI18N

        buttonGroup2.add(tgValvesOpen1);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesOpen1, "Open");
        tgValvesOpen1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesOpen1ItemStateChanged(evt);
            }
        });
        tgValvesOpen1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesOpen1ActionPerformed(evt);
            }
        });

        buttonGroup2.add(tgValvesStop1);
        tgValvesStop1.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesStop1, "Stop");
        tgValvesStop1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesStop1ItemStateChanged(evt);
            }
        });
        tgValvesStop1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesStop1ActionPerformed(evt);
            }
        });

        buttonGroup2.add(tgValvesClose1);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesClose1, "Close");
        tgValvesClose1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesClose1ItemStateChanged(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, "Inlet Valves");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 10, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(112, 112, 112))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tgValvesOpen1)
                            .addComponent(tgValvesStop1)
                            .addComponent(tgValvesClose1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(radial2Top2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(7, 7, 7)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(tgValvesOpen1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesStop1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesClose1))
                    .addComponent(radial2Top2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, "Precision Control");

        org.openide.awt.Mnemonics.setLocalizedText(precisionIncrement1, "+");
        precisionIncrement1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionIncrement1.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionIncrement1.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionIncrement1.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionIncrement1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionIncrement1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(precisionDecrement1, "-");
        precisionDecrement1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionDecrement1.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionDecrement1.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionDecrement1.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionDecrement1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionDecrement1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addContainerGap(25, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(precisionIncrement1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(precisionDecrement1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(precisionIncrement1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precisionDecrement1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(jButton4, "DESYNC");
        jButton4.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton7, "RESET");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setBackground(javax.swing.UIManager.getDefaults().getColor("nb.versioning.conflicted.color"));
        org.openide.awt.Mnemonics.setLocalizedText(jButton8, "TRIP");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jPanel10.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup7.add(autoSteamPressure1On1);
        org.openide.awt.Mnemonics.setLocalizedText(autoSteamPressure1On1, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.autoSteamPressure1On1.text")); // NOI18N
        autoSteamPressure1On1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoSteamPressure1On1ItemStateChanged(evt);
            }
        });
        autoSteamPressure1On1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoSteamPressure1On1ActionPerformed(evt);
            }
        });

        buttonGroup7.add(autoSteamPressure1Off1);
        autoSteamPressure1Off1.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(autoSteamPressure1Off1, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.autoSteamPressure1Off1.text")); // NOI18N
        autoSteamPressure1Off1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoSteamPressure1Off1ItemStateChanged(evt);
            }
        });
        autoSteamPressure1Off1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoSteamPressure1Off1ActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel19, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jLabel19.text")); // NOI18N

        rpm1B1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        rpm1B1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        rpm1B1.setLcdDecimals(2);
        rpm1B1.setLcdUnitString(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.rpm1B1.lcdUnitString")); // NOI18N
        rpm1B1.setLcdUnitStringVisible(false);
        rpm1B1.setLcdValue(6.96);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel20, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jLabel20.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel21, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jLabel21.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(precisionDecrement3, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.precisionDecrement3.text")); // NOI18N
        precisionDecrement3.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionDecrement3.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionDecrement3.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionDecrement3.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionDecrement3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionDecrement3ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(precisionIncrement3, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.precisionIncrement3.text")); // NOI18N
        precisionIncrement3.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionIncrement3.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionIncrement3.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionIncrement3.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionIncrement3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionIncrement3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel20)
                        .addGap(7, 7, 7)
                        .addComponent(rpm1B1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(autoSteamPressure1On1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(autoSteamPressure1Off1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(precisionIncrement3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(precisionDecrement3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel19)))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rpm1B1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(precisionDecrement3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precisionIncrement3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(autoSteamPressure1Off1)
                    .addComponent(autoSteamPressure1On1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton7))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(radial3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(radial4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(radial4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(radial3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(16, 16, 16)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        linear1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        linear1.setFrameVisible(false);
        linear1.setLcdVisible(false);
        linear1.setLedVisible(false);
        linear1.setMaxValue(1000.0);
        linear1.setOrientation(eu.hansolo.steelseries.tools.Orientation.HORIZONTAL);
        linear1.setThreshold(800.0);
        linear1.setTitle(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.linear1.title")); // NOI18N
        linear1.setTrackSection(200.0);
        linear1.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        linear1.setTrackStart(800.0);
        linear1.setTrackStartColor(new java.awt.Color(255, 0, 0));
        linear1.setTrackStop(1000.0);
        linear1.setTrackVisible(true);
        linear1.setUnitString(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.linear1.unitString")); // NOI18N

        linear2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        linear2.setFrameVisible(false);
        linear2.setLcdVisible(false);
        linear2.setLedVisible(false);
        linear2.setMaxValue(1000.0);
        linear2.setThreshold(800.0);
        linear2.setTitle("TG-1 Steam Flow");
        linear2.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        linear2.setTrackStart(800.0);
        linear2.setTrackStartColor(new java.awt.Color(255, 0, 0));
        linear2.setTrackStop(1000.0);
        linear2.setUnitString(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.linear2.unitString")); // NOI18N

        linear3.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        linear3.setFrameVisible(false);
        linear3.setLcdVisible(false);
        linear3.setLedVisible(false);
        linear3.setMaxValue(1000.0);
        linear3.setThreshold(800.0);
        linear3.setTitle("TG-1 Load");
        linear3.setTrackSection(0.0);
        linear3.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        linear3.setTrackStart(800.0);
        linear3.setTrackStartColor(new java.awt.Color(255, 0, 0));
        linear3.setTrackStop(1000.0);
        linear3.setTrackVisible(true);
        linear3.setUnitString("MW");

        linear4.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        linear4.setFrameVisible(false);
        linear4.setLcdVisible(false);
        linear4.setLedVisible(false);
        linear4.setThreshold(800.0);
        linear4.setTitle("TG-1 Steam Flow");
        linear4.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        linear4.setTrackStart(800.0);
        linear4.setTrackStartColor(new java.awt.Color(255, 0, 0));
        linear4.setTrackStop(1000.0);
        linear4.setUnitString("kg/s");
        linear4.setValue(100.0);
        linear4.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        annunciatorPanel.setLayout(new java.awt.GridLayout(3, 6));

        tg1Trip.setEditable(false);
        tg1Trip.setBackground(new java.awt.Color(142, 0, 0));
        tg1Trip.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        tg1Trip.setForeground(new java.awt.Color(0, 0, 0));
        tg1Trip.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tg1Trip.setText("TG-1 Trip");
        tg1Trip.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tg1Trip.setFocusable(false);
        tg1Trip.setPreferredSize(new java.awt.Dimension(100, 30));
        tg1Trip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tg1TripActionPerformed(evt);
            }
        });
        annunciatorPanel.add(tg1Trip);

        tg2Trip.setEditable(false);
        tg2Trip.setBackground(new java.awt.Color(142, 0, 0));
        tg2Trip.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        tg2Trip.setForeground(new java.awt.Color(0, 0, 0));
        tg2Trip.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tg2Trip.setText("TG-2 Trip");
        tg2Trip.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tg2Trip.setFocusable(false);
        tg2Trip.setPreferredSize(new java.awt.Dimension(100, 30));
        tg2Trip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tg2TripActionPerformed(evt);
            }
        });
        annunciatorPanel.add(tg2Trip);

        jTextField3.setEditable(false);
        jTextField3.setBackground(new java.awt.Color(142, 0, 0));
        jTextField3.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jTextField3.setForeground(new java.awt.Color(0, 0, 0));
        jTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField3.setText("TG-1 Vibration");
        jTextField3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField3.setFocusable(false);
        annunciatorPanel.add(jTextField3);

        jTextField8.setEditable(false);
        jTextField8.setBackground(new java.awt.Color(142, 0, 0));
        jTextField8.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jTextField8.setForeground(new java.awt.Color(0, 0, 0));
        jTextField8.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField8.setText("TG-2 Vibration");
        jTextField8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField8.setFocusable(false);
        annunciatorPanel.add(jTextField8);

        steamTemp.setEditable(false);
        steamTemp.setBackground(new java.awt.Color(107, 103, 0));
        steamTemp.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        steamTemp.setForeground(new java.awt.Color(0, 0, 0));
        steamTemp.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        steamTemp.setText("Low steam temp");
        steamTemp.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        steamTemp.setFocusable(false);
        annunciatorPanel.add(steamTemp);

        steamPress.setEditable(false);
        steamPress.setBackground(new java.awt.Color(107, 103, 0));
        steamPress.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        steamPress.setForeground(new java.awt.Color(0, 0, 0));
        steamPress.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        steamPress.setText("Low steam press.");
        steamPress.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        steamPress.setFocusable(false);
        annunciatorPanel.add(steamPress);

        tg1Rev.setEditable(false);
        tg1Rev.setBackground(new java.awt.Color(142, 0, 0));
        tg1Rev.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        tg1Rev.setForeground(new java.awt.Color(0, 0, 0));
        tg1Rev.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tg1Rev.setText("TG-1 Rev. Power");
        tg1Rev.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tg1Rev.setFocusable(false);
        annunciatorPanel.add(tg1Rev);

        tg2Rev.setEditable(false);
        tg2Rev.setBackground(new java.awt.Color(142, 0, 0));
        tg2Rev.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        tg2Rev.setForeground(new java.awt.Color(0, 0, 0));
        tg2Rev.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tg2Rev.setText("TG-2 Rev. Power");
        tg2Rev.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tg2Rev.setFocusable(false);
        tg2Rev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tg2RevActionPerformed(evt);
            }
        });
        annunciatorPanel.add(tg2Rev);

        jTextField16.setEditable(false);
        jTextField16.setBackground(new java.awt.Color(142, 0, 0));
        jTextField16.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jTextField16.setForeground(new java.awt.Color(0, 0, 0));
        jTextField16.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField16.setText("TG-1 Diff. Exp");
        jTextField16.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField16.setFocusable(false);
        annunciatorPanel.add(jTextField16);

        jTextField18.setEditable(false);
        jTextField18.setBackground(new java.awt.Color(142, 0, 0));
        jTextField18.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jTextField18.setForeground(new java.awt.Color(0, 0, 0));
        jTextField18.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField18.setText("TG-1 Diff. Exp");
        jTextField18.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField18.setFocusable(false);
        annunciatorPanel.add(jTextField18);

        lowVacuum1.setEditable(false);
        lowVacuum1.setBackground(new java.awt.Color(107, 103, 0));
        lowVacuum1.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        lowVacuum1.setForeground(new java.awt.Color(0, 0, 0));
        lowVacuum1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        lowVacuum1.setText("Vacuum 1 Low");
        lowVacuum1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lowVacuum1.setFocusable(false);
        lowVacuum1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lowVacuum1ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(lowVacuum1);

        lowVacuum2.setEditable(false);
        lowVacuum2.setBackground(new java.awt.Color(107, 103, 0));
        lowVacuum2.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        lowVacuum2.setForeground(new java.awt.Color(0, 0, 0));
        lowVacuum2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        lowVacuum2.setText("Vacuum 2 Low");
        lowVacuum2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lowVacuum2.setFocusable(false);
        annunciatorPanel.add(lowVacuum2);

        tg1Speed.setEditable(false);
        tg1Speed.setBackground(new java.awt.Color(142, 0, 0));
        tg1Speed.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        tg1Speed.setForeground(new java.awt.Color(0, 0, 0));
        tg1Speed.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tg1Speed.setText("TG-1 Speed");
        tg1Speed.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tg1Speed.setFocusable(false);
        tg1Speed.setPreferredSize(new java.awt.Dimension(100, 30));
        tg1Speed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tg1SpeedActionPerformed(evt);
            }
        });
        annunciatorPanel.add(tg1Speed);

        tg2Speed.setEditable(false);
        tg2Speed.setBackground(new java.awt.Color(142, 0, 0));
        tg2Speed.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        tg2Speed.setForeground(new java.awt.Color(0, 0, 0));
        tg2Speed.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tg2Speed.setText("TG-2 Speed");
        tg2Speed.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tg2Speed.setFocusable(false);
        tg2Speed.setPreferredSize(new java.awt.Dimension(100, 30));
        tg2Speed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tg2SpeedActionPerformed(evt);
            }
        });
        annunciatorPanel.add(tg2Speed);

        jTextField11.setEditable(false);
        jTextField11.setBackground(new java.awt.Color(142, 0, 0));
        jTextField11.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jTextField11.setForeground(new java.awt.Color(0, 0, 0));
        jTextField11.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField11.setText("TG-1 oil press.");
        jTextField11.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField11.setFocusable(false);
        jTextField11.setPreferredSize(new java.awt.Dimension(100, 30));
        jTextField11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField11ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(jTextField11);

        jTextField14.setEditable(false);
        jTextField14.setBackground(new java.awt.Color(142, 0, 0));
        jTextField14.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jTextField14.setForeground(new java.awt.Color(0, 0, 0));
        jTextField14.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField14.setText("TG-1 oil press.");
        jTextField14.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField14.setFocusable(false);
        jTextField14.setPreferredSize(new java.awt.Dimension(100, 30));
        jTextField14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField14ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(jTextField14);

        noVacuum1.setEditable(false);
        noVacuum1.setBackground(new java.awt.Color(142, 0, 0));
        noVacuum1.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        noVacuum1.setForeground(new java.awt.Color(0, 0, 0));
        noVacuum1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        noVacuum1.setText("Vacuum 1");
        noVacuum1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        noVacuum1.setFocusable(false);
        noVacuum1.setPreferredSize(new java.awt.Dimension(100, 30));
        noVacuum1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noVacuum1ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(noVacuum1);

        noVacuum2.setEditable(false);
        noVacuum2.setBackground(new java.awt.Color(142, 0, 0));
        noVacuum2.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        noVacuum2.setForeground(new java.awt.Color(0, 0, 0));
        noVacuum2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        noVacuum2.setText("Vacuum 2");
        noVacuum2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        noVacuum2.setFocusable(false);
        annunciatorPanel.add(noVacuum2);

        jButton1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton1, "ACK");
        jButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton1.setMaximumSize(new java.awt.Dimension(50, 50));
        jButton1.setMinimumSize(new java.awt.Dimension(50, 50));
        jButton1.setPreferredSize(new java.awt.Dimension(50, 50));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jLabel7.text")); // NOI18N

        jPanel11.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel10.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jLabel10.text")); // NOI18N

        buttonGroup3.add(sdvcAuto);
        sdvcAuto.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcAuto, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.sdvcAuto.text")); // NOI18N
        sdvcAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdvcAutoActionPerformed(evt);
            }
        });

        buttonGroup3.add(sdvcManual);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcManual, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.sdvcManual.text")); // NOI18N
        sdvcManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdvcManualActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(sdvcAuto)
                    .addComponent(sdvcManual))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sdvcAuto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sdvcManual)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel20.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup4.add(sdvcOpen1);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcOpen1, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.sdvcOpen1.text")); // NOI18N
        sdvcOpen1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sdvcOpen1ItemStateChanged(evt);
            }
        });
        sdvcOpen1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdvcOpen1ActionPerformed(evt);
            }
        });

        buttonGroup4.add(sdvcClose1);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcClose1, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.sdvcClose1.text")); // NOI18N
        sdvcClose1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sdvcClose1ItemStateChanged(evt);
            }
        });
        sdvcClose1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdvcClose1ActionPerformed(evt);
            }
        });

        jLabel38.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel38, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jLabel38.text")); // NOI18N

        sdvcSpinner1.setModel(new javax.swing.SpinnerNumberModel(1, 1, 4, 1));

        jLabel40.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel40, "Condenser 1");

        buttonGroup4.add(sdvcStop1);
        sdvcStop1.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcStop1, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.sdvcStop1.text")); // NOI18N
        sdvcStop1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sdvcStop1ItemStateChanged(evt);
            }
        });
        sdvcStop1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdvcStop1ActionPerformed(evt);
            }
        });

        sdvcFlow1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        sdvcFlow1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        sdvcFlow1.setLcdDecimals(2);
        sdvcFlow1.setLcdUnitString(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.sdvcFlow1.lcdUnitString")); // NOI18N

        interlockLed1.setPreferredSize(new java.awt.Dimension(24, 24));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jLabel8.text")); // NOI18N

        sdvcTotal1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        sdvcTotal1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        sdvcTotal1.setLcdDecimals(2);
        sdvcTotal1.setLcdUnitString(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.sdvcTotal1.lcdUnitString")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jLabel9.text")); // NOI18N

        jLabel12.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jLabel12.text")); // NOI18N

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addComponent(jLabel38)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sdvcSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(sdvcOpen1)
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addComponent(sdvcClose1)
                                .addGap(31, 31, 31)
                                .addComponent(interlockLed1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addComponent(sdvcStop1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel12)))
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addGap(39, 39, 39)
                                .addComponent(jLabel8))
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(sdvcTotal1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(sdvcFlow1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addGap(79, 79, 79)
                        .addComponent(jLabel9)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sdvcSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel38))
                        .addComponent(sdvcOpen1))
                    .addComponent(sdvcFlow1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sdvcStop1)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sdvcClose1)
                            .addComponent(interlockLed1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGap(0, 1, Short.MAX_VALUE)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sdvcTotal1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)))
                .addGap(11, 11, Short.MAX_VALUE))
        );

        jPanel21.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup5.add(sdvcOpen2);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcOpen2, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.sdvcOpen2.text")); // NOI18N
        sdvcOpen2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sdvcOpen2ItemStateChanged(evt);
            }
        });
        sdvcOpen2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdvcOpen2ActionPerformed(evt);
            }
        });

        buttonGroup5.add(sdvcClose2);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcClose2, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.sdvcClose2.text")); // NOI18N
        sdvcClose2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sdvcClose2ItemStateChanged(evt);
            }
        });
        sdvcClose2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdvcClose2ActionPerformed(evt);
            }
        });

        jLabel39.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel39, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jLabel39.text")); // NOI18N

        sdvcSpinner2.setModel(new javax.swing.SpinnerNumberModel(5, 5, 8, 1));

        jLabel41.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel41, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jLabel41.text")); // NOI18N

        buttonGroup5.add(sdvcStop2);
        sdvcStop2.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcStop2, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.sdvcStop2.text")); // NOI18N
        sdvcStop2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sdvcStop2ItemStateChanged(evt);
            }
        });
        sdvcStop2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdvcStop2ActionPerformed(evt);
            }
        });

        sdvcFlow2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        sdvcFlow2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        sdvcFlow2.setLcdDecimals(2);
        sdvcFlow2.setLcdUnitString(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.sdvcFlow2.lcdUnitString")); // NOI18N

        interlockLed2.setPreferredSize(new java.awt.Dimension(24, 24));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel13, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jLabel13.text")); // NOI18N

        sdvcTotal2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        sdvcTotal2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        sdvcTotal2.setLcdDecimals(2);
        sdvcTotal2.setLcdUnitString(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.sdvcTotal2.lcdUnitString")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel14, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jLabel14.text")); // NOI18N

        jLabel15.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel15, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jLabel15.text")); // NOI18N

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addComponent(jLabel39)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sdvcSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(sdvcOpen2)
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addComponent(sdvcClose2)
                                .addGap(31, 31, 31)
                                .addComponent(interlockLed2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addComponent(sdvcStop2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel15)))
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addGap(39, 39, 39)
                                .addComponent(jLabel13))
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(sdvcTotal2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(sdvcFlow2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jLabel41)
                        .addGap(79, 79, 79)
                        .addComponent(jLabel14)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sdvcSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39))
                        .addComponent(sdvcOpen2))
                    .addComponent(sdvcFlow2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sdvcStop2)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sdvcClose2)
                            .addComponent(interlockLed2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGap(0, 1, Short.MAX_VALUE)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sdvcTotal2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)))
                .addGap(11, 11, Short.MAX_VALUE))
        );

        sdvcManualLed.setPreferredSize(new java.awt.Dimension(24, 24));

        jLabel16.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel16, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jLabel16.text")); // NOI18N

        jPanel12.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        valvePos5.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.MUD);
        valvePos5.setBarGraphColor(eu.hansolo.steelseries.tools.ColorDef.WHITE);
        valvePos5.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.GLOSSY_METAL);
        valvePos5.setFrameVisible(false);
        valvePos5.setGaugeType(eu.hansolo.steelseries.tools.GaugeType.TYPE2);
        valvePos5.setLcdVisible(false);
        valvePos5.setLedVisible(false);
        valvePos5.setMajorTickmarkVisible(false);
        valvePos5.setMinimumSize(new java.awt.Dimension(45, 45));
        valvePos5.setMinorTickmarkVisible(false);
        valvePos5.setPreferredSize(new java.awt.Dimension(45, 45));
        valvePos5.setTicklabelsVisible(false);
        valvePos5.setTickmarkColorFromThemeEnabled(false);
        valvePos5.setTickmarksVisible(false);
        valvePos5.setTitle(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.valvePos5.title")); // NOI18N
        valvePos5.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        valvePos5.setUnitString(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.valvePos5.unitString")); // NOI18N

        valvePos6.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.MUD);
        valvePos6.setBarGraphColor(eu.hansolo.steelseries.tools.ColorDef.WHITE);
        valvePos6.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.GLOSSY_METAL);
        valvePos6.setFrameVisible(false);
        valvePos6.setGaugeType(eu.hansolo.steelseries.tools.GaugeType.TYPE2);
        valvePos6.setLcdVisible(false);
        valvePos6.setLedVisible(false);
        valvePos6.setMajorTickmarkVisible(false);
        valvePos6.setMinimumSize(new java.awt.Dimension(45, 45));
        valvePos6.setMinorTickmarkVisible(false);
        valvePos6.setPreferredSize(new java.awt.Dimension(45, 45));
        valvePos6.setTicklabelsVisible(false);
        valvePos6.setTickmarkColorFromThemeEnabled(false);
        valvePos6.setTickmarksVisible(false);
        valvePos6.setTitle(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.valvePos6.title")); // NOI18N
        valvePos6.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        valvePos6.setUnitString(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.valvePos6.unitString")); // NOI18N

        valvePos7.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.MUD);
        valvePos7.setBarGraphColor(eu.hansolo.steelseries.tools.ColorDef.WHITE);
        valvePos7.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.GLOSSY_METAL);
        valvePos7.setFrameVisible(false);
        valvePos7.setGaugeType(eu.hansolo.steelseries.tools.GaugeType.TYPE2);
        valvePos7.setLcdVisible(false);
        valvePos7.setLedVisible(false);
        valvePos7.setMajorTickmarkVisible(false);
        valvePos7.setMinimumSize(new java.awt.Dimension(45, 45));
        valvePos7.setMinorTickmarkVisible(false);
        valvePos7.setPreferredSize(new java.awt.Dimension(45, 45));
        valvePos7.setTicklabelsVisible(false);
        valvePos7.setTickmarkColorFromThemeEnabled(false);
        valvePos7.setTickmarksVisible(false);
        valvePos7.setTitle(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.valvePos7.title")); // NOI18N
        valvePos7.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        valvePos7.setUnitString(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.valvePos7.unitString")); // NOI18N

        valvePos8.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.MUD);
        valvePos8.setBarGraphColor(eu.hansolo.steelseries.tools.ColorDef.WHITE);
        valvePos8.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.GLOSSY_METAL);
        valvePos8.setFrameVisible(false);
        valvePos8.setGaugeType(eu.hansolo.steelseries.tools.GaugeType.TYPE2);
        valvePos8.setLcdVisible(false);
        valvePos8.setLedVisible(false);
        valvePos8.setMajorTickmarkVisible(false);
        valvePos8.setMinimumSize(new java.awt.Dimension(45, 45));
        valvePos8.setMinorTickmarkVisible(false);
        valvePos8.setPreferredSize(new java.awt.Dimension(45, 45));
        valvePos8.setTicklabelsVisible(false);
        valvePos8.setTickmarkColorFromThemeEnabled(false);
        valvePos8.setTickmarksVisible(false);
        valvePos8.setTitle(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.valvePos8.title")); // NOI18N
        valvePos8.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        valvePos8.setUnitString(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.valvePos8.unitString")); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(valvePos5, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(valvePos6, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(valvePos7, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(valvePos8, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(valvePos6, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(valvePos5, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(valvePos7, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(valvePos8, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        valvePos1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.MUD);
        valvePos1.setBarGraphColor(eu.hansolo.steelseries.tools.ColorDef.WHITE);
        valvePos1.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.GLOSSY_METAL);
        valvePos1.setFrameVisible(false);
        valvePos1.setGaugeType(eu.hansolo.steelseries.tools.GaugeType.TYPE2);
        valvePos1.setLcdVisible(false);
        valvePos1.setLedVisible(false);
        valvePos1.setMajorTickmarkVisible(false);
        valvePos1.setMinimumSize(new java.awt.Dimension(45, 45));
        valvePos1.setMinorTickmarkVisible(false);
        valvePos1.setPreferredSize(new java.awt.Dimension(45, 45));
        valvePos1.setTicklabelsVisible(false);
        valvePos1.setTickmarkColorFromThemeEnabled(false);
        valvePos1.setTickmarksVisible(false);
        valvePos1.setTitle(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.valvePos1.title")); // NOI18N
        valvePos1.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        valvePos1.setUnitString(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.valvePos1.unitString")); // NOI18N

        valvePos2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.MUD);
        valvePos2.setBarGraphColor(eu.hansolo.steelseries.tools.ColorDef.WHITE);
        valvePos2.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.GLOSSY_METAL);
        valvePos2.setFrameVisible(false);
        valvePos2.setGaugeType(eu.hansolo.steelseries.tools.GaugeType.TYPE2);
        valvePos2.setLcdVisible(false);
        valvePos2.setLedVisible(false);
        valvePos2.setMajorTickmarkVisible(false);
        valvePos2.setMinimumSize(new java.awt.Dimension(45, 45));
        valvePos2.setMinorTickmarkVisible(false);
        valvePos2.setPreferredSize(new java.awt.Dimension(45, 45));
        valvePos2.setTicklabelsVisible(false);
        valvePos2.setTickmarkColorFromThemeEnabled(false);
        valvePos2.setTickmarksVisible(false);
        valvePos2.setTitle(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.valvePos2.title")); // NOI18N
        valvePos2.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        valvePos2.setUnitString(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.valvePos2.unitString")); // NOI18N

        valvePos3.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.MUD);
        valvePos3.setBarGraphColor(eu.hansolo.steelseries.tools.ColorDef.WHITE);
        valvePos3.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.GLOSSY_METAL);
        valvePos3.setFrameVisible(false);
        valvePos3.setGaugeType(eu.hansolo.steelseries.tools.GaugeType.TYPE2);
        valvePos3.setLcdVisible(false);
        valvePos3.setLedVisible(false);
        valvePos3.setMajorTickmarkVisible(false);
        valvePos3.setMinimumSize(new java.awt.Dimension(45, 45));
        valvePos3.setMinorTickmarkVisible(false);
        valvePos3.setPreferredSize(new java.awt.Dimension(45, 45));
        valvePos3.setTicklabelsVisible(false);
        valvePos3.setTickmarkColorFromThemeEnabled(false);
        valvePos3.setTickmarksVisible(false);
        valvePos3.setTitle(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.valvePos3.title")); // NOI18N
        valvePos3.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        valvePos3.setUnitString(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.valvePos3.unitString")); // NOI18N

        valvePos4.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.MUD);
        valvePos4.setBarGraphColor(eu.hansolo.steelseries.tools.ColorDef.WHITE);
        valvePos4.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.GLOSSY_METAL);
        valvePos4.setFrameVisible(false);
        valvePos4.setGaugeType(eu.hansolo.steelseries.tools.GaugeType.TYPE2);
        valvePos4.setLcdVisible(false);
        valvePos4.setLedVisible(false);
        valvePos4.setMajorTickmarkVisible(false);
        valvePos4.setMinimumSize(new java.awt.Dimension(45, 45));
        valvePos4.setMinorTickmarkVisible(false);
        valvePos4.setPreferredSize(new java.awt.Dimension(45, 45));
        valvePos4.setTicklabelsVisible(false);
        valvePos4.setTickmarkColorFromThemeEnabled(false);
        valvePos4.setTickmarksVisible(false);
        valvePos4.setTitle(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.valvePos4.title")); // NOI18N
        valvePos4.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        valvePos4.setUnitString(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.valvePos4.unitString")); // NOI18N

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(valvePos1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(valvePos2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(valvePos3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(valvePos4, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(valvePos2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(valvePos1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(valvePos3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(valvePos4, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 159, Short.MAX_VALUE)
                                .addComponent(jLabel16)
                                .addGap(4, 4, 4)
                                .addComponent(sdvcManualLed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(15, Short.MAX_VALUE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(jLabel16))
                    .addComponent(sdvcManualLed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pressure1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        pressure1.setFrameVisible(false);
        pressure1.setLcdVisible(false);
        pressure1.setLedVisible(false);
        pressure1.setMaxValue(10.0);
        pressure1.setTitle(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.pressure1.title")); // NOI18N
        pressure1.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        pressure1.setTrackStart(800.0);
        pressure1.setTrackStartColor(new java.awt.Color(255, 0, 0));
        pressure1.setUnitString("MPa");
        pressure1.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        pressure2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        pressure2.setFrameVisible(false);
        pressure2.setLcdVisible(false);
        pressure2.setLedVisible(false);
        pressure2.setMaxValue(10.0);
        pressure2.setTitle(org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.pressure2.title")); // NOI18N
        pressure2.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        pressure2.setTrackStart(800.0);
        pressure2.setTrackStartColor(new java.awt.Color(255, 0, 0));
        pressure2.setUnitString("MPa");
        pressure2.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(linear2, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(linear4, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(linear1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(pressure1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(pressure2, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(linear3, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(annunciatorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(60, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(annunciatorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pressure1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pressure2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(linear3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(linear1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(linear2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(linear4, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(468, 468, 468))
        );

        jScrollPane1.setViewportView(jPanel3);

        org.openide.awt.Mnemonics.setLocalizedText(jMenu1, "Window");

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem5, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jMenuItem5.text")); // NOI18N
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem3, "Core Map");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem10, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jMenuItem10.text")); // NOI18N
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem10);

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem12, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jMenuItem12.text")); // NOI18N
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem12);

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem2, "Condensate");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem1, "Dearators");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem8, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jMenuItem8.text")); // NOI18N
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem8);

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem4, org.openide.util.NbBundle.getMessage(TGUI.class, "TGUI.jMenuItem4.text")); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1390, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 752, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        tg1.sync();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void tgValvesOpenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesOpenItemStateChanged
        if (!tg1.isTripped()) {
            if (evt.getStateChange() == 1) {
                TG1InletValves.forEach(valve -> {
                    valve.setState(2);
                });
            }
        }
    }//GEN-LAST:event_tgValvesOpenItemStateChanged

    private void tgValvesOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesOpenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesOpenActionPerformed

    private void tgValvesStopItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesStopItemStateChanged
        if (!tg1.isTripped()) {
            if (evt.getStateChange() == 1) {
                TG1InletValves.forEach(valve -> {
                    valve.setState(1);
                });
            }
        }
    }//GEN-LAST:event_tgValvesStopItemStateChanged

    private void tgValvesStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesStopActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesStopActionPerformed

    private void tgValvesCloseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesCloseItemStateChanged
        if (!tg1.isTripped()) {
            if (evt.getStateChange() == 1) {
                TG1InletValves.forEach(valve -> {
                    valve.setState(0);
                });
            }
        }
    }//GEN-LAST:event_tgValvesCloseItemStateChanged

    private void precisionIncrementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionIncrementActionPerformed
        if (!tg1.isTripped()) {
            TG1InletValves.forEach(valve -> {
                valve.precisionAdjustment(true);
            });
        }
    }//GEN-LAST:event_precisionIncrementActionPerformed

    private void precisionDecrementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionDecrementActionPerformed
        if (debounce) {
            return;
        }
        debounce = true;
        if (!tg1.isTripped()) {
            TG1InletValves.forEach(valve -> {
                valve.precisionAdjustment(false);
            });
        }
    }//GEN-LAST:event_precisionDecrementActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        tg2.sync();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void tgValvesOpen1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesOpen1ItemStateChanged
        if (debounce) {
            return;
        }
        debounce = true;
        if (!tg2.isTripped()) {
            if (evt.getStateChange() == 1) {
                TG2InletValves.forEach(valve -> {
                    valve.setState(2);
                });
            }
        }
    }//GEN-LAST:event_tgValvesOpen1ItemStateChanged

    private void tgValvesOpen1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesOpen1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesOpen1ActionPerformed

    private void tgValvesStop1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesStop1ItemStateChanged
        if (!tg2.isTripped()) {
            if (evt.getStateChange() == 1) {
                TG2InletValves.forEach(valve -> {
                    valve.setState(1);
                });
            }
        }
    }//GEN-LAST:event_tgValvesStop1ItemStateChanged

    private void tgValvesStop1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesStop1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesStop1ActionPerformed

    private void tgValvesClose1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesClose1ItemStateChanged
        if (!tg2.isTripped()) {
            if (evt.getStateChange() == 1) {
                TG2InletValves.forEach(valve -> {
                    valve.setState(0);
                });
            }
        }
    }//GEN-LAST:event_tgValvesClose1ItemStateChanged

    private void precisionIncrement1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionIncrement1ActionPerformed
        if (debounce) {
            return;
        }
        debounce = true;
        if (!tg2.isTripped()) {
            TG2InletValves.forEach(valve -> {
               valve.precisionAdjustment(true);
            });
        }
    }//GEN-LAST:event_precisionIncrement1ActionPerformed

    private void precisionDecrement1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionDecrement1ActionPerformed
        if (debounce) {
            return;
        }
        debounce = true;
        if (!tg2.isTripped()) {
            TG2InletValves.forEach(valve -> {
                valve.precisionAdjustment(false);
            });
        }
    }//GEN-LAST:event_precisionDecrement1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        tg1.deSync();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        tg2.deSync();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        tgValvesStop.setSelected(true);
        tg1.trip("Manual");
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        UI.createOrContinue(CoreMap.class, false, false);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        tgValvesStop1.setSelected(true);
        tg2.trip("Manual");
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        tgValvesStop.setSelected(true);
        tg1.reset();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        tgValvesStop1.setSelected(true);
        tg2.reset();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void tg1TripActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tg1TripActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tg1TripActionPerformed

    private void tg2TripActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tg2TripActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tg2TripActionPerformed

    private void tg2RevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tg2RevActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tg2RevActionPerformed

    private void tg1SpeedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tg1SpeedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tg1SpeedActionPerformed

    private void tg2SpeedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tg2SpeedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tg2SpeedActionPerformed

    private void jTextField11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField11ActionPerformed

    private void jTextField14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField14ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField14ActionPerformed

    private void noVacuum1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noVacuum1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_noVacuum1ActionPerformed

    private void lowVacuum1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lowVacuum1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lowVacuum1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        UI.createOrContinue(CondensateUI.class, true, false);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        UI.createOrContinue(DearatorUI.class, true, false);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        UI.createOrContinue(FeedwaterUI.class, true, false);
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        annunciator.acknowledge();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void sdvcOpen1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcOpen1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcOpen1ItemStateChanged

    private void sdvcOpen1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcOpen1ActionPerformed
        if (sdvcAuto.isSelected()) {
            return;
        }
        sdv_c.get((int)sdvcSpinner1.getValue() - 1).setState(2);
    }//GEN-LAST:event_sdvcOpen1ActionPerformed

    private void sdvcClose1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcClose1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcClose1ItemStateChanged

    private void sdvcClose1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcClose1ActionPerformed
        if (sdvcAuto.isSelected()) {
            return;
        }
        sdv_c.get((int)sdvcSpinner1.getValue() - 1).setState(0);
    }//GEN-LAST:event_sdvcClose1ActionPerformed

    private void sdvcStop1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcStop1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcStop1ItemStateChanged

    private void sdvcStop1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcStop1ActionPerformed
        if (sdvcAuto.isSelected()) {
            return;
        }
        sdv_c.get((int)sdvcSpinner1.getValue() - 1).setState(1);
    }//GEN-LAST:event_sdvcStop1ActionPerformed

    private void sdvcOpen2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcOpen2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcOpen2ItemStateChanged

    private void sdvcOpen2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcOpen2ActionPerformed
        if (sdvcAuto.isSelected()) {
            return;
        }
        sdv_c.get((int)sdvcSpinner2.getValue() - 1).setState(2);
    }//GEN-LAST:event_sdvcOpen2ActionPerformed

    private void sdvcClose2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcClose2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcClose2ItemStateChanged

    private void sdvcClose2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcClose2ActionPerformed
        if (sdvcAuto.isSelected()) {
            return;
        }
        sdv_c.get((int)sdvcSpinner2.getValue() - 1).setState(0);
    }//GEN-LAST:event_sdvcClose2ActionPerformed

    private void sdvcStop2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcStop2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcStop2ItemStateChanged

    private void sdvcStop2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcStop2ActionPerformed
        if (sdvcAuto.isSelected()) {
            return;
        }
        sdv_c.get((int)sdvcSpinner2.getValue() - 1).setState(1);
    }//GEN-LAST:event_sdvcStop2ActionPerformed

    private void autoSteamPressure1OnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoSteamPressure1OnItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoSteamPressure1OnItemStateChanged

    private void autoSteamPressure1OnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSteamPressure1OnActionPerformed
        autoControl.tgValveControl.get(0).setEnabled(true);
        autoControl.tgValveControl.get(1).setEnabled(true);
        autoControl.tgValveControl.get(0).setSetpoint(rpm1B1.getLcdValue());
        autoControl.tgValveControl.get(1).setSetpoint(rpm1B1.getLcdValue());
    }//GEN-LAST:event_autoSteamPressure1OnActionPerformed

    private void autoSteamPressure1OffItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoSteamPressure1OffItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoSteamPressure1OffItemStateChanged

    private void autoSteamPressure1OffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSteamPressure1OffActionPerformed
        autoControl.tgValveControl.get(0).setEnabled(false);
        autoControl.tgValveControl.get(1).setEnabled(false);
    }//GEN-LAST:event_autoSteamPressure1OffActionPerformed

    private void precisionDecrement2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionDecrement2ActionPerformed
        if (debounce) {
            return;
        }
        debounce = true;
        precisionDecrement3ActionPerformed(evt);
    }//GEN-LAST:event_precisionDecrement2ActionPerformed

    private void precisionIncrement2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionIncrement2ActionPerformed
        if (debounce) {
            return;
        }
        debounce = true;
        precisionIncrement3ActionPerformed(evt);
    }//GEN-LAST:event_precisionIncrement2ActionPerformed

    private void autoSteamPressure1On1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoSteamPressure1On1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoSteamPressure1On1ItemStateChanged

    private void autoSteamPressure1On1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSteamPressure1On1ActionPerformed
        autoControl.tgValveControl.get(2).setEnabled(true);
        autoControl.tgValveControl.get(3).setEnabled(true);
        autoControl.tgValveControl.get(2).setSetpoint(rpm1B1.getLcdValue());
        autoControl.tgValveControl.get(3).setSetpoint(rpm1B1.getLcdValue());
    }//GEN-LAST:event_autoSteamPressure1On1ActionPerformed

    private void autoSteamPressure1Off1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoSteamPressure1Off1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoSteamPressure1Off1ItemStateChanged

    private void autoSteamPressure1Off1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSteamPressure1Off1ActionPerformed
        autoControl.tgValveControl.get(2).setEnabled(false);
        autoControl.tgValveControl.get(3).setEnabled(false);
    }//GEN-LAST:event_autoSteamPressure1Off1ActionPerformed

    private void precisionDecrement3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionDecrement3ActionPerformed
        if (debounce) {
            return;
        }
        debounce = true;
        double currentValue = rpm1B.getLcdValue();
        if (currentValue < 5.89) {
            return;
        }
        rpm1B.setLcdValue(currentValue - 0.01);
        rpm1B1.setLcdValue(currentValue - 0.01);
        autoControl.tgValveControl.forEach(controller -> {
            controller.setSetpoint(currentValue - 0.01);
        });
    }//GEN-LAST:event_precisionDecrement3ActionPerformed

    private void precisionIncrement3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionIncrement3ActionPerformed
        if (debounce) {
            return;
        }
        debounce = true;
        double currentValue = rpm1B.getLcdValue();
        if (currentValue > 6.96) {
            return;
        }
        rpm1B.setLcdValue(currentValue + 0.01);
        rpm1B1.setLcdValue(currentValue + 0.01);
        autoControl.tgValveControl.forEach(controller -> {
            controller.setSetpoint(currentValue + 0.01);
        });
    }//GEN-LAST:event_precisionIncrement3ActionPerformed

    private void sdvcAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcAutoActionPerformed
        autoControl.sdv_cControl.forEach(controller -> {
            controller.setEnabled(true);
        });
        sdvcManualLed.setLedOn(false);
        sdvcStop1.setSelected(true);
        sdvcStop2.setSelected(true);
        NPPSim.sdv_c.forEach(valve -> {
            valve.setState(1);
        });
    }//GEN-LAST:event_sdvcAutoActionPerformed

    private void sdvcManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcManualActionPerformed
        autoControl.sdv_cControl.forEach(controller -> {
            controller.setEnabled(false);
        });
        sdvcManualLed.setLedOn(true);
        sdvcStop1.setSelected(true);
        sdvcStop2.setSelected(true);
        sdvcStop1ActionPerformed(evt);
        sdvcStop2ActionPerformed(evt);
    }//GEN-LAST:event_sdvcManualActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        UI.createOrContinue(PCSUI.class, true, false);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        UI.createOrContinue(SelsynPanel.class, false, false);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        NPPSim.ui.toFront();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        UI.createOrContinue(MCPUI.class, true, false);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel annunciatorPanel;
    private javax.swing.JRadioButton autoSteamPressure1Off;
    private javax.swing.JRadioButton autoSteamPressure1Off1;
    private javax.swing.JRadioButton autoSteamPressure1On;
    private javax.swing.JRadioButton autoSteamPressure1On1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.ButtonGroup buttonGroup6;
    private javax.swing.ButtonGroup buttonGroup7;
    private eu.hansolo.steelseries.extras.Led interlockLed1;
    private eu.hansolo.steelseries.extras.Led interlockLed2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField8;
    private eu.hansolo.steelseries.gauges.Linear linear1;
    private eu.hansolo.steelseries.gauges.Linear linear2;
    private eu.hansolo.steelseries.gauges.Linear linear3;
    private eu.hansolo.steelseries.gauges.Linear linear4;
    private javax.swing.JTextField lowVacuum1;
    private javax.swing.JTextField lowVacuum2;
    private javax.swing.JTextField noVacuum1;
    private javax.swing.JTextField noVacuum2;
    private javax.swing.JButton precisionDecrement;
    private javax.swing.JButton precisionDecrement1;
    private javax.swing.JButton precisionDecrement2;
    private javax.swing.JButton precisionDecrement3;
    private javax.swing.JButton precisionIncrement;
    private javax.swing.JButton precisionIncrement1;
    private javax.swing.JButton precisionIncrement2;
    private javax.swing.JButton precisionIncrement3;
    private eu.hansolo.steelseries.gauges.Linear pressure1;
    private eu.hansolo.steelseries.gauges.Linear pressure2;
    private eu.hansolo.steelseries.gauges.Radial radial1;
    private eu.hansolo.steelseries.gauges.Radial radial2;
    private eu.hansolo.steelseries.gauges.Radial2Top radial2Top1;
    private eu.hansolo.steelseries.gauges.Radial2Top radial2Top2;
    private eu.hansolo.steelseries.gauges.Radial radial3;
    private eu.hansolo.steelseries.gauges.Radial radial4;
    private eu.hansolo.steelseries.gauges.DisplaySingle rpm1B;
    private eu.hansolo.steelseries.gauges.DisplaySingle rpm1B1;
    private javax.swing.JRadioButton sdvcAuto;
    private javax.swing.JRadioButton sdvcClose1;
    private javax.swing.JRadioButton sdvcClose2;
    private eu.hansolo.steelseries.gauges.DisplaySingle sdvcFlow1;
    private eu.hansolo.steelseries.gauges.DisplaySingle sdvcFlow2;
    private javax.swing.JRadioButton sdvcManual;
    private eu.hansolo.steelseries.extras.Led sdvcManualLed;
    private javax.swing.JRadioButton sdvcOpen1;
    private javax.swing.JRadioButton sdvcOpen2;
    private javax.swing.JSpinner sdvcSpinner1;
    private javax.swing.JSpinner sdvcSpinner2;
    private javax.swing.JRadioButton sdvcStop1;
    private javax.swing.JRadioButton sdvcStop2;
    private eu.hansolo.steelseries.gauges.DisplaySingle sdvcTotal1;
    private eu.hansolo.steelseries.gauges.DisplaySingle sdvcTotal2;
    private javax.swing.JTextField steamPress;
    private javax.swing.JTextField steamTemp;
    private javax.swing.JTextField tg1Rev;
    private javax.swing.JTextField tg1Speed;
    private javax.swing.JTextField tg1Trip;
    private javax.swing.JTextField tg2Rev;
    private javax.swing.JTextField tg2Speed;
    private javax.swing.JTextField tg2Trip;
    private javax.swing.JRadioButton tgValvesClose;
    private javax.swing.JRadioButton tgValvesClose1;
    private javax.swing.JRadioButton tgValvesOpen;
    private javax.swing.JRadioButton tgValvesOpen1;
    private javax.swing.JRadioButton tgValvesStop;
    private javax.swing.JRadioButton tgValvesStop1;
    private eu.hansolo.steelseries.gauges.RadialBargraph valvePos1;
    private eu.hansolo.steelseries.gauges.RadialBargraph valvePos2;
    private eu.hansolo.steelseries.gauges.RadialBargraph valvePos3;
    private eu.hansolo.steelseries.gauges.RadialBargraph valvePos4;
    private eu.hansolo.steelseries.gauges.RadialBargraph valvePos5;
    private eu.hansolo.steelseries.gauges.RadialBargraph valvePos6;
    private eu.hansolo.steelseries.gauges.RadialBargraph valvePos7;
    private eu.hansolo.steelseries.gauges.RadialBargraph valvePos8;
    // End of variables declaration//GEN-END:variables
}
