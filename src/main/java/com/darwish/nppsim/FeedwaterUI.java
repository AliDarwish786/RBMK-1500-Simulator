package com.darwish.nppsim;

import static com.darwish.nppsim.NPPSim.autoControl;
import static com.darwish.nppsim.NPPSim.auxFeederValves;
import static com.darwish.nppsim.NPPSim.feedwaterMixer1;
import static com.darwish.nppsim.NPPSim.feedwaterMixer2;
import static com.darwish.nppsim.NPPSim.mainFeederValves;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import static com.darwish.nppsim.NPPSim.mcc;
import static com.darwish.nppsim.NPPSim.mainFeedwaterPumps;
import static com.darwish.nppsim.NPPSim.auxFeedwaterPumps;

public class FeedwaterUI extends javax.swing.JFrame implements UIUpdateable {
    private final Annunciator annunciator;

    /**
     * Creates new form CondensateUI
     */
    public FeedwaterUI() {
        initComponents();
        this.setTitle("Feedwater Control");
        annunciator = new Annunciator(annunciatorPanel);
        
        ((JSpinner.DefaultEditor)mFWPSpinner.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor)auxFWPSpinner.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor)mFW1Spinner.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor)mFW2Spinner.getEditor()).getTextField().setEditable(false);
        mFWPSpinner.addChangeListener((ChangeEvent e) -> {
            Pump currentSelection = mainFeedwaterPumps.get((int)mFWPSpinner.getValue() - 1);
            rpm1A.setLcdValue(currentSelection.getRPM());
            amps1A.setLcdValue(currentSelection.getPowerUsage());
            if (currentSelection.isActive()) {
                startMFWP.setSelected(true);
            } else {
                stopMWFP.setSelected(true);
            }
        });
        auxFWPSpinner.addChangeListener((ChangeEvent e) -> {
            Pump currentSelection = auxFeedwaterPumps.get((int)auxFWPSpinner.getValue() - 1);
            rpm2A.setLcdValue(currentSelection.getRPM());
            amps2A.setLcdValue(currentSelection.getPowerUsage());
            if (currentSelection.isActive()) {
                startAuxFWP.setSelected(true);
            } else {
                stopAuxFWP.setSelected(true);
            }
        });
        mFW1Spinner.addChangeListener((ChangeEvent e) -> {
            WaterValve currentSelection = mainFeederValves.get((int)mFW1Spinner.getValue() - 1);
            switch (currentSelection.getState()) {
                case 0:
                    mFW1Close.setSelected(true);
                    break;
                case 1:
                    mFW1Stop.setSelected(true);
                    break;
                case 2:
                    mFW1Open.setSelected(true);
                    break;  
            }
        });
        mFW2Spinner.addChangeListener((ChangeEvent e) -> {
            WaterValve currentSelection = mainFeederValves.get((int)mFW2Spinner.getValue() + 2);
            switch (currentSelection.getState()) {
                case 0:
                    mFW2Close.setSelected(true);
                    break;
                case 1:
                    mFW2Stop.setSelected(true);
                    break;
                case 2:
                    mFW2Open.setSelected(true);
                    break;  
            }
        });
        initializeDialUpdateThread();
        
        //set variables
        Pump currentSelection = mainFeedwaterPumps.get((int)mFWPSpinner.getValue() - 1);
        if (currentSelection.isActive()) {
            startMFWP.setSelected(true);
        } else {
            stopMWFP.setSelected(true);
        }
        currentSelection = auxFeedwaterPumps.get((int)auxFWPSpinner.getValue() - 1);
        if (currentSelection.isActive()) {
            startAuxFWP.setSelected(true);
        } else {
            stopAuxFWP.setSelected(true);
        }
        powerFWP1.setLedOn(mainFeedwaterPumps.get(0).isActive());
        powerFWP2.setLedOn(mainFeedwaterPumps.get(1).isActive());
        powerFWP3.setLedOn(mainFeedwaterPumps.get(2).isActive());
        powerFWP4.setLedOn(mainFeedwaterPumps.get(3).isActive());
        powerFWP5.setLedOn(mainFeedwaterPumps.get(4).isActive());
        powerFWP6.setLedOn(mainFeedwaterPumps.get(5).isActive());
        powerFWP7.setLedOn(mainFeedwaterPumps.get(6).isActive());
        power2A1.setLedOn(auxFeedwaterPumps.get(0).isActive());
        power2A2.setLedOn(auxFeedwaterPumps.get(1).isActive());
        power2A3.setLedOn(auxFeedwaterPumps.get(2).isActive());
        power2A4.setLedOn(auxFeedwaterPumps.get(3).isActive());
        power2A5.setLedOn(auxFeedwaterPumps.get(4).isActive());
        power2A6.setLedOn(auxFeedwaterPumps.get(5).isActive());
        if (autoControl.mainFeederControl.get(0).isEnabled() || autoControl.mainFeederControl.get(1).isEnabled()) {
            autoControl3Elem.setSelected(true);
            autoControlMain1.setSelected(autoControl.mainFeederControl.get(0).isEnabled());
            autoControlMain2.setSelected(autoControl.mainFeederControl.get(1).isEnabled());
        }
        if (autoControl.auxFeederControl.get(0).isEnabled() || autoControl.auxFeederControl.get(1).isEnabled()) {
            autoControl3Elem.setSelected(true);
            autoControlAux1.setSelected(autoControl.auxFeederControl.get(0).isEnabled());
            autoControlAux2.setSelected(autoControl.auxFeederControl.get(1).isEnabled());
        }
    }
    
    
    @Override
    public void update() {
        checkAlarms();
        if (this.isVisible()) {
            java.awt.EventQueue.invokeLater(() -> {
                Pump selectedMain = mainFeedwaterPumps.get((int)mFWPSpinner.getValue() - 1);
                Pump selectedAux = auxFeedwaterPumps.get((int)auxFWPSpinner.getValue() - 1);

                rpm1A.setLcdValue(selectedMain.getRPM());
                flow1A.setLcdValue(selectedMain.getFlowRate());
                amps1A.setLcdValue(selectedMain.getPowerUsage());
                rpm2A.setLcdValue(selectedAux.getRPM());
                flow2A.setLcdValue(selectedAux.getFlowRate());
                amps2A.setLcdValue(selectedAux.getPowerUsage());

                flow1.setLcdValue(mainFeederValves.get(0).timestepFlow * 20);
                flow2.setLcdValue(mainFeederValves.get(1).timestepFlow * 20);
                flow3.setLcdValue(mainFeederValves.get(2).timestepFlow * 20);
                flow4.setLcdValue(mainFeederValves.get(3).timestepFlow * 20);
                flow5.setLcdValue(mainFeederValves.get(4).timestepFlow * 20);
                flow6.setLcdValue(mainFeederValves.get(5).timestepFlow * 20);
                auxFlow1.setLcdValue(auxFeederValves.get(0).timestepFlow * 20);
                auxFlow2.setLcdValue(auxFeederValves.get(1).timestepFlow * 20);
            });
        }
    }

    @Override
    public void initializeDialUpdateThread() {
        UI.uiThreads.add(new Thread(() -> {
                try {
                    while (true) {
                        annunciator.update();
                        if (this.isVisible()) { 
                            java.awt.EventQueue.invokeLater(() -> {
                                double[] mfwFlows = new double[] {0.0, 0.0};
                                for (int i = 0; i < 3; i++) {
                                    mfwFlows[0] += mainFeederValves.get(i).timestepFlow * 20;
                                }
                                for (int i = 3; i < 6; i++) {
                                    mfwFlows[1] += mainFeederValves.get(i).timestepFlow * 20;
                                }
                                drumLevel1.setValue(mcc.drum1.getWaterLevel());
                                drumLevel2.setValue(mcc.drum2.getWaterLevel());
                                mainFWFlow1.setValue(mfwFlows[0]);
                                mainFWFlow2.setValue(mfwFlows[1]);
                                auxFWFlow1.setValue(auxFeederValves.get(0).timestepFlow * 20);
                                auxFWFlow2.setValue(auxFeederValves.get(1).timestepFlow * 20);
                                fwTemp1.setValue(feedwaterMixer1.getWaterTemperature());
                                fwTemp2.setValue(feedwaterMixer2.getWaterTemperature());

                                fwPos1.setValue(mainFeederValves.get(0).getPosition() * 100);
                                fwPos2.setValue(mainFeederValves.get(1).getPosition() * 100);
                                fwPos3.setValue(mainFeederValves.get(2).getPosition() * 100);
                                fwPos4.setValue(mainFeederValves.get(3).getPosition() * 100);
                                fwPos5.setValue(mainFeederValves.get(4).getPosition() * 100);
                                fwPos6.setValue(mainFeederValves.get(5).getPosition() * 100);
                                aFWPos1.setValue(auxFeederValves.get(0).getPosition() * 100);
                                aFWPos2.setValue(auxFeederValves.get(1).getPosition() * 100);
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
        if (mcc.drum1.getWaterLevel() < -25) {
            annunciator.trigger(drum1Low);
        } else {
            annunciator.reset(drum1Low);
        }
        if (mcc.drum2.getWaterLevel() < -25) {
            annunciator.trigger(drum2Low);
        } else {
            annunciator.reset(drum2Low);
        }
        if (mcc.drum1.getWaterLevel() > 25) {
            annunciator.trigger(drum1High);
        } else {
            annunciator.reset(drum1High);
        }
        if (mcc.drum2.getWaterLevel() > 25) {
            annunciator.trigger(drum2High);
        } else {
            annunciator.reset(drum2High);
        }
        if (feedwaterMixer1.getWaterTemperature() < 150 || feedwaterMixer2.getWaterTemperature() < 150) {
            annunciator.trigger(FWTemp); 
        } else {
            annunciator.reset(FWTemp);
        }
        boolean mainCavitation = false;
        for (Pump pump: mainFeedwaterPumps) {
            if (pump.isCavitating) {
                mainCavitation = true;
                break;
            }
        }
        annunciator.setTrigger(mainCavitation, mainCavit);
        boolean auxCavitation = false;
        for (Pump pump: auxFeedwaterPumps) {
            if (pump.isCavitating) {
                auxCavitation = true;
                break;
            }
        }
        annunciator.setTrigger(auxCavitation, auxCavit);
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
        buttonGroup8 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        auxFWFlow1 = new eu.hansolo.steelseries.gauges.Linear();
        jPanel8 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        startMFWP = new javax.swing.JRadioButton();
        stopMWFP = new javax.swing.JRadioButton();
        jLabel12 = new javax.swing.JLabel();
        mFWPSpinner = new javax.swing.JSpinner();
        rpm1A = new eu.hansolo.steelseries.gauges.DisplaySingle();
        amps1A = new eu.hansolo.steelseries.gauges.DisplaySingle();
        flow1A = new eu.hansolo.steelseries.gauges.DisplaySingle();
        powerFWP2 = new eu.hansolo.steelseries.extras.Led();
        powerFWP3 = new eu.hansolo.steelseries.extras.Led();
        powerFWP4 = new eu.hansolo.steelseries.extras.Led();
        jLabel17 = new javax.swing.JLabel();
        powerFWP7 = new eu.hansolo.steelseries.extras.Led();
        powerFWP6 = new eu.hansolo.steelseries.extras.Led();
        powerFWP5 = new eu.hansolo.steelseries.extras.Led();
        powerFWP1 = new eu.hansolo.steelseries.extras.Led();
        annunciatorPanel = new javax.swing.JPanel();
        drum1Low = new javax.swing.JTextField();
        drum2Low = new javax.swing.JTextField();
        mainCavit = new javax.swing.JTextField();
        auxCavit = new javax.swing.JTextField();
        drum1High = new javax.swing.JTextField();
        drum2High = new javax.swing.JTextField();
        mainTrip = new javax.swing.JTextField();
        auxTrip = new javax.swing.JTextField();
        FWTemp = new javax.swing.JTextField();
        trip2B = new javax.swing.JTextField();
        jTextField10 = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        auxFWFlow2 = new eu.hansolo.steelseries.gauges.Linear();
        jPanel16 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        startAuxFWP = new javax.swing.JRadioButton();
        stopAuxFWP = new javax.swing.JRadioButton();
        jLabel29 = new javax.swing.JLabel();
        auxFWPSpinner = new javax.swing.JSpinner();
        rpm2A = new eu.hansolo.steelseries.gauges.DisplaySingle();
        amps2A = new eu.hansolo.steelseries.gauges.DisplaySingle();
        flow2A = new eu.hansolo.steelseries.gauges.DisplaySingle();
        power2A1 = new eu.hansolo.steelseries.extras.Led();
        power2A2 = new eu.hansolo.steelseries.extras.Led();
        power2A3 = new eu.hansolo.steelseries.extras.Led();
        jLabel31 = new javax.swing.JLabel();
        power2A4 = new eu.hansolo.steelseries.extras.Led();
        power2A5 = new eu.hansolo.steelseries.extras.Led();
        power2A6 = new eu.hansolo.steelseries.extras.Led();
        drumLevel1 = new eu.hansolo.steelseries.gauges.Linear();
        drumLevel2 = new eu.hansolo.steelseries.gauges.Linear();
        mainFWFlow1 = new eu.hansolo.steelseries.gauges.Linear();
        mainFWFlow2 = new eu.hansolo.steelseries.gauges.Linear();
        jPanel20 = new javax.swing.JPanel();
        mFW1Open = new javax.swing.JRadioButton();
        mFW1Close = new javax.swing.JRadioButton();
        jLabel38 = new javax.swing.JLabel();
        mFW1Spinner = new javax.swing.JSpinner();
        jLabel40 = new javax.swing.JLabel();
        mFW1Stop = new javax.swing.JRadioButton();
        fwPos3 = new eu.hansolo.steelseries.gauges.Radial2Top();
        flow3 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        fwPos2 = new eu.hansolo.steelseries.gauges.Radial2Top();
        flow2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        fwPos1 = new eu.hansolo.steelseries.gauges.Radial2Top();
        flow1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        fwTemp1 = new eu.hansolo.steelseries.gauges.Linear();
        fwTemp2 = new eu.hansolo.steelseries.gauges.Linear();
        jPanel24 = new javax.swing.JPanel();
        mFW2Open = new javax.swing.JRadioButton();
        mFW2Close = new javax.swing.JRadioButton();
        jLabel44 = new javax.swing.JLabel();
        mFW2Spinner = new javax.swing.JSpinner();
        jLabel47 = new javax.swing.JLabel();
        mFW2Stop = new javax.swing.JRadioButton();
        fwPos6 = new eu.hansolo.steelseries.gauges.Radial2Top();
        flow6 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        fwPos5 = new eu.hansolo.steelseries.gauges.Radial2Top();
        flow5 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        fwPos4 = new eu.hansolo.steelseries.gauges.Radial2Top();
        flow4 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jPanel25 = new javax.swing.JPanel();
        aFWOpen1 = new javax.swing.JRadioButton();
        aFWClose1 = new javax.swing.JRadioButton();
        jLabel42 = new javax.swing.JLabel();
        aFWStop1 = new javax.swing.JRadioButton();
        aFWPos1 = new eu.hansolo.steelseries.gauges.Radial2Top();
        auxFlow1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jPanel26 = new javax.swing.JPanel();
        aFWOpen2 = new javax.swing.JRadioButton();
        aFWClose2 = new javax.swing.JRadioButton();
        jLabel50 = new javax.swing.JLabel();
        aFWStop2 = new javax.swing.JRadioButton();
        aFWPos2 = new eu.hansolo.steelseries.gauges.Radial2Top();
        auxFlow2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jPanel27 = new javax.swing.JPanel();
        autoControlWaterLevel = new javax.swing.JRadioButton();
        autoControlOff = new javax.swing.JRadioButton();
        jLabel51 = new javax.swing.JLabel();
        autoControl3Elem = new javax.swing.JRadioButton();
        autoControlMain1 = new javax.swing.JCheckBox();
        autoControlMain2 = new javax.swing.JCheckBox();
        autoControlAux1 = new javax.swing.JCheckBox();
        autoControlAux2 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();

        jScrollPane1.setPreferredSize(new java.awt.Dimension(1366, 768));

        jPanel3.setBackground(UI.BACKGROUND);
        jPanel3.setPreferredSize(new java.awt.Dimension(1366, 768));

        auxFWFlow1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        auxFWFlow1.setFrameVisible(false);
        auxFWFlow1.setLcdVisible(false);
        auxFWFlow1.setLedVisible(false);
        auxFWFlow1.setMajorTickSpacing(50.0);
        auxFWFlow1.setMaxValue(500.0);
        auxFWFlow1.setTitle("Auxiliary Feedwater Flow 1");
        auxFWFlow1.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        auxFWFlow1.setTrackStart(800.0);
        auxFWFlow1.setTrackStartColor(new java.awt.Color(255, 0, 0));
        auxFWFlow1.setUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "CondensateUI.totalFlow1.unitString")); // NOI18N
        auxFWFlow1.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        jPanel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel11.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, "Main Feedwater Pumps");

        jPanel9.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup3.add(startMFWP);
        org.openide.awt.Mnemonics.setLocalizedText(startMFWP, "Start");
        startMFWP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                startMFWPItemStateChanged(evt);
            }
        });
        startMFWP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startMFWPActionPerformed(evt);
            }
        });

        buttonGroup3.add(stopMWFP);
        stopMWFP.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(stopMWFP, "Stop");
        stopMWFP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                stopMWFPItemStateChanged(evt);
            }
        });
        stopMWFP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopMWFPActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, "Pump Selector");

        mFWPSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 7, 1));
        mFWPSpinner.setDoubleBuffered(true);

        rpm1A.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        rpm1A.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        rpm1A.setLcdUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "CondensateUI.rpm1A.lcdUnitString")); // NOI18N

        amps1A.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        amps1A.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        amps1A.setLcdUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "CondensateUI.amps1A.lcdUnitString")); // NOI18N

        flow1A.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        flow1A.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        flow1A.setLcdUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "CondensateUI.flow1A.lcdUnitString")); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mFWPSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(startMFWP)
                            .addComponent(stopMWFP))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(amps1A, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rpm1A, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(flow1A, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 12, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(mFWPSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(startMFWP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stopMWFP))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(rpm1A, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(amps1A, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(flow1A, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        powerFWP2.setPreferredSize(new java.awt.Dimension(20, 20));

        powerFWP3.setPreferredSize(new java.awt.Dimension(20, 20));

        powerFWP4.setPreferredSize(new java.awt.Dimension(20, 20));

        jLabel17.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel17, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "CondensateUI.jLabel17.text")); // NOI18N

        powerFWP7.setPreferredSize(new java.awt.Dimension(20, 20));

        powerFWP6.setPreferredSize(new java.awt.Dimension(20, 20));

        powerFWP5.setPreferredSize(new java.awt.Dimension(20, 20));

        powerFWP1.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel8Layout.createSequentialGroup()
                            .addComponent(jLabel17)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(powerFWP1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(powerFWP2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(powerFWP3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(powerFWP4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel8Layout.createSequentialGroup()
                            .addGap(109, 109, 109)
                            .addComponent(powerFWP5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(powerFWP6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(powerFWP7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel11))
                .addGap(0, 9, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(powerFWP2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(powerFWP3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(powerFWP4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(powerFWP1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(powerFWP5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(powerFWP6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(powerFWP7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        annunciatorPanel.setLayout(new java.awt.GridLayout(3, 6));

        drum1Low.setEditable(false);
        drum1Low.setBackground(new java.awt.Color(142, 0, 0));
        drum1Low.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        drum1Low.setForeground(new java.awt.Color(0, 0, 0));
        drum1Low.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        drum1Low.setText(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.drum1Low.text")); // NOI18N
        drum1Low.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        drum1Low.setFocusable(false);
        annunciatorPanel.add(drum1Low);

        drum2Low.setEditable(false);
        drum2Low.setBackground(new java.awt.Color(142, 0, 0));
        drum2Low.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        drum2Low.setForeground(new java.awt.Color(0, 0, 0));
        drum2Low.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        drum2Low.setText(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.drum2Low.text")); // NOI18N
        drum2Low.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        drum2Low.setFocusable(false);
        drum2Low.setPreferredSize(new java.awt.Dimension(100, 30));
        drum2Low.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drum2LowActionPerformed(evt);
            }
        });
        annunciatorPanel.add(drum2Low);

        mainCavit.setEditable(false);
        mainCavit.setBackground(new java.awt.Color(142, 0, 0));
        mainCavit.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        mainCavit.setForeground(new java.awt.Color(0, 0, 0));
        mainCavit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mainCavit.setText("Main FWP Cavit");
        mainCavit.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        mainCavit.setFocusable(false);
        mainCavit.setPreferredSize(new java.awt.Dimension(100, 30));
        mainCavit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainCavitActionPerformed(evt);
            }
        });
        annunciatorPanel.add(mainCavit);

        auxCavit.setEditable(false);
        auxCavit.setBackground(new java.awt.Color(142, 0, 0));
        auxCavit.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        auxCavit.setForeground(new java.awt.Color(0, 0, 0));
        auxCavit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        auxCavit.setText("Aux FWP Cavit");
        auxCavit.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        auxCavit.setFocusable(false);
        annunciatorPanel.add(auxCavit);

        drum1High.setEditable(false);
        drum1High.setBackground(new java.awt.Color(142, 0, 0));
        drum1High.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        drum1High.setForeground(new java.awt.Color(0, 0, 0));
        drum1High.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        drum1High.setText(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.drum1High.text")); // NOI18N
        drum1High.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        drum1High.setFocusable(false);
        annunciatorPanel.add(drum1High);

        drum2High.setEditable(false);
        drum2High.setBackground(new java.awt.Color(142, 0, 0));
        drum2High.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        drum2High.setForeground(new java.awt.Color(0, 0, 0));
        drum2High.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        drum2High.setText(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.drum2High.text")); // NOI18N
        drum2High.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        drum2High.setFocusable(false);
        annunciatorPanel.add(drum2High);

        mainTrip.setEditable(false);
        mainTrip.setBackground(new java.awt.Color(142, 0, 0));
        mainTrip.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        mainTrip.setForeground(new java.awt.Color(0, 0, 0));
        mainTrip.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mainTrip.setText(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.mainTrip.text")); // NOI18N
        mainTrip.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        mainTrip.setFocusable(false);
        mainTrip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainTripActionPerformed(evt);
            }
        });
        annunciatorPanel.add(mainTrip);

        auxTrip.setEditable(false);
        auxTrip.setBackground(new java.awt.Color(142, 0, 0));
        auxTrip.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        auxTrip.setForeground(new java.awt.Color(0, 0, 0));
        auxTrip.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        auxTrip.setText(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.auxTrip.text")); // NOI18N
        auxTrip.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        auxTrip.setFocusable(false);
        annunciatorPanel.add(auxTrip);

        FWTemp.setEditable(false);
        FWTemp.setBackground(new java.awt.Color(107, 103, 0));
        FWTemp.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        FWTemp.setForeground(new java.awt.Color(0, 0, 0));
        FWTemp.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        FWTemp.setText(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.FWTemp.text")); // NOI18N
        FWTemp.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        FWTemp.setFocusable(false);
        annunciatorPanel.add(FWTemp);

        trip2B.setEditable(false);
        trip2B.setBackground(new java.awt.Color(107, 103, 0));
        trip2B.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        trip2B.setForeground(new java.awt.Color(0, 0, 0));
        trip2B.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        trip2B.setText(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.trip2B.text")); // NOI18N
        trip2B.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        trip2B.setFocusable(false);
        trip2B.setPreferredSize(new java.awt.Dimension(100, 30));
        trip2B.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trip2BActionPerformed(evt);
            }
        });
        annunciatorPanel.add(trip2B);

        jTextField10.setEditable(false);
        jTextField10.setBackground(new java.awt.Color(107, 103, 0));
        jTextField10.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jTextField10.setForeground(new java.awt.Color(0, 0, 0));
        jTextField10.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField10.setFocusable(false);
        jTextField10.setPreferredSize(new java.awt.Dimension(100, 30));
        jTextField10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField10ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(jTextField10);

        jTextField11.setEditable(false);
        jTextField11.setBackground(new java.awt.Color(107, 103, 0));
        jTextField11.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jTextField11.setForeground(new java.awt.Color(0, 0, 0));
        jTextField11.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField11.setText(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "CondensateUI.jTextField11.text")); // NOI18N
        jTextField11.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField11.setFocusable(false);
        jTextField11.setPreferredSize(new java.awt.Dimension(100, 30));
        jTextField11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField11ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(jTextField11);

        auxFWFlow2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        auxFWFlow2.setFrameVisible(false);
        auxFWFlow2.setLcdVisible(false);
        auxFWFlow2.setLedVisible(false);
        auxFWFlow2.setMajorTickSpacing(50.0);
        auxFWFlow2.setMaxValue(500.0);
        auxFWFlow2.setTitle("Auxiliary Feedwater Flow 2");
        auxFWFlow2.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        auxFWFlow2.setTrackStart(800.0);
        auxFWFlow2.setTrackStartColor(new java.awt.Color(255, 0, 0));
        auxFWFlow2.setUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "CondensateUI.totalFlow2.unitString")); // NOI18N
        auxFWFlow2.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        jPanel16.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel28.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel28, "Aux Feedwater Pumps");

        jPanel17.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup4.add(startAuxFWP);
        org.openide.awt.Mnemonics.setLocalizedText(startAuxFWP, "Start");
        startAuxFWP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                startAuxFWPItemStateChanged(evt);
            }
        });
        startAuxFWP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startAuxFWPActionPerformed(evt);
            }
        });

        buttonGroup4.add(stopAuxFWP);
        stopAuxFWP.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(stopAuxFWP, "Stop");
        stopAuxFWP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                stopAuxFWPItemStateChanged(evt);
            }
        });
        stopAuxFWP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopAuxFWPActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel29, "Pump Selector");

        auxFWPSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 6, 1));
        auxFWPSpinner.setDoubleBuffered(true);

        rpm2A.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        rpm2A.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        rpm2A.setLcdUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "CondensateUI.rpm2A.lcdUnitString")); // NOI18N

        amps2A.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        amps2A.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        amps2A.setLcdUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "CondensateUI.amps2A.lcdUnitString")); // NOI18N

        flow2A.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        flow2A.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        flow2A.setLcdUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "CondensateUI.flow2A.lcdUnitString")); // NOI18N

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(auxFWPSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(startAuxFWP)
                            .addComponent(stopAuxFWP))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(amps2A, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rpm2A, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(flow2A, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 12, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(auxFWPSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(startAuxFWP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stopAuxFWP))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(rpm2A, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(amps2A, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(flow2A, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        power2A1.setPreferredSize(new java.awt.Dimension(20, 20));

        power2A2.setPreferredSize(new java.awt.Dimension(20, 20));

        power2A3.setPreferredSize(new java.awt.Dimension(20, 20));

        jLabel31.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel31, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "CondensateUI.jLabel31.text")); // NOI18N

        power2A4.setPreferredSize(new java.awt.Dimension(20, 20));

        power2A5.setPreferredSize(new java.awt.Dimension(20, 20));

        power2A6.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel16Layout.createSequentialGroup()
                            .addComponent(jLabel31)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(power2A1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(power2A2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(power2A3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel28))
                .addGap(0, 12, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(power2A4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(power2A5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(power2A6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(power2A1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(power2A2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(power2A3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(power2A4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(power2A5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(power2A6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        drumLevel1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        drumLevel1.setFrameVisible(false);
        drumLevel1.setLcdVisible(false);
        drumLevel1.setLedVisible(false);
        drumLevel1.setMaxValue(50.0);
        drumLevel1.setMinValue(-50.0);
        drumLevel1.setTitle(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.drumLevel1.title")); // NOI18N
        drumLevel1.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        drumLevel1.setTrackStartColor(new java.awt.Color(255, 0, 0));
        drumLevel1.setUnitString("%");
        drumLevel1.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        drumLevel2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        drumLevel2.setFrameVisible(false);
        drumLevel2.setLcdVisible(false);
        drumLevel2.setLedVisible(false);
        drumLevel2.setMaxValue(50.0);
        drumLevel2.setMinValue(-50.0);
        drumLevel2.setTitle(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.drumLevel2.title")); // NOI18N
        drumLevel2.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        drumLevel2.setTrackStartColor(new java.awt.Color(255, 0, 0));
        drumLevel2.setUnitString("%");
        drumLevel2.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        mainFWFlow1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        mainFWFlow1.setFrameVisible(false);
        mainFWFlow1.setLcdVisible(false);
        mainFWFlow1.setLedVisible(false);
        mainFWFlow1.setMaxValue(1600.0);
        mainFWFlow1.setThreshold(800.0);
        mainFWFlow1.setTitle("Main Feedwater Flow 1");
        mainFWFlow1.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        mainFWFlow1.setTrackStart(800.0);
        mainFWFlow1.setTrackStartColor(new java.awt.Color(255, 0, 0));
        mainFWFlow1.setTrackStop(1000.0);
        mainFWFlow1.setUnitString("kg/s");
        mainFWFlow1.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        mainFWFlow2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        mainFWFlow2.setFrameVisible(false);
        mainFWFlow2.setLcdVisible(false);
        mainFWFlow2.setLedVisible(false);
        mainFWFlow2.setMaxValue(1600.0);
        mainFWFlow2.setThreshold(800.0);
        mainFWFlow2.setTitle(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.mainFWFlow2.title")); // NOI18N
        mainFWFlow2.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        mainFWFlow2.setTrackStart(800.0);
        mainFWFlow2.setTrackStartColor(new java.awt.Color(255, 0, 0));
        mainFWFlow2.setTrackStop(1000.0);
        mainFWFlow2.setUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "CondensateUI.totalInflow2.unitString")); // NOI18N
        mainFWFlow2.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        jPanel20.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup1.add(mFW1Open);
        org.openide.awt.Mnemonics.setLocalizedText(mFW1Open, "Open");
        mFW1Open.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                mFW1OpenItemStateChanged(evt);
            }
        });
        mFW1Open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mFW1OpenActionPerformed(evt);
            }
        });

        buttonGroup1.add(mFW1Close);
        org.openide.awt.Mnemonics.setLocalizedText(mFW1Close, "Close");
        mFW1Close.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                mFW1CloseItemStateChanged(evt);
            }
        });
        mFW1Close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mFW1CloseActionPerformed(evt);
            }
        });

        jLabel38.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel38, "Valve Selector");

        mFW1Spinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 3, 1));

        jLabel40.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel40, "Main Feeder Valves 1");

        buttonGroup1.add(mFW1Stop);
        mFW1Stop.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(mFW1Stop, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.mFW1Stop.text")); // NOI18N
        mFW1Stop.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                mFW1StopItemStateChanged(evt);
            }
        });
        mFW1Stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mFW1StopActionPerformed(evt);
            }
        });

        fwPos3.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        fwPos3.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        fwPos3.setFrameVisible(false);
        fwPos3.setLcdBackgroundVisible(false);
        fwPos3.setLcdVisible(false);
        fwPos3.setLedVisible(false);
        fwPos3.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        fwPos3.setPointerShadowVisible(false);
        fwPos3.setPreferredSize(new java.awt.Dimension(100, 100));
        fwPos3.setTitle(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.fwPos3.title")); // NOI18N
        fwPos3.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        fwPos3.setTitleAndUnitFontEnabled(true);
        fwPos3.setUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.fwPos3.unitString")); // NOI18N

        flow3.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        flow3.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        flow3.setLcdDecimals(2);
        flow3.setLcdUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.flow3.lcdUnitString")); // NOI18N
        fwPos3.add(flow3);
        flow3.setBounds(0, 60, 100, 30);

        fwPos2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        fwPos2.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        fwPos2.setFrameVisible(false);
        fwPos2.setLcdBackgroundVisible(false);
        fwPos2.setLcdVisible(false);
        fwPos2.setLedVisible(false);
        fwPos2.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        fwPos2.setPointerShadowVisible(false);
        fwPos2.setPreferredSize(new java.awt.Dimension(100, 100));
        fwPos2.setTitle(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.fwPos2.title")); // NOI18N
        fwPos2.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        fwPos2.setTitleAndUnitFontEnabled(true);
        fwPos2.setUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.fwPos2.unitString")); // NOI18N

        flow2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        flow2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        flow2.setLcdDecimals(2);
        flow2.setLcdUnitString("kg/s");
        fwPos2.add(flow2);
        flow2.setBounds(0, 60, 100, 30);

        fwPos1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        fwPos1.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        fwPos1.setFrameVisible(false);
        fwPos1.setLcdBackgroundVisible(false);
        fwPos1.setLcdVisible(false);
        fwPos1.setLedVisible(false);
        fwPos1.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        fwPos1.setPointerShadowVisible(false);
        fwPos1.setPreferredSize(new java.awt.Dimension(100, 100));
        fwPos1.setTitle(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.fwPos1.title")); // NOI18N
        fwPos1.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        fwPos1.setTitleAndUnitFontEnabled(true);
        fwPos1.setUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.fwPos1.unitString")); // NOI18N

        flow1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        flow1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        flow1.setLcdDecimals(2);
        flow1.setLcdUnitString("kg/s");
        fwPos1.add(flow1);
        flow1.setBounds(0, 60, 100, 30);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel40)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mFW1Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(mFW1Open)
                    .addComponent(mFW1Close)
                    .addComponent(mFW1Stop))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fwPos1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fwPos2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fwPos3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mFW1Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel38))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mFW1Open)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mFW1Stop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mFW1Close))
                    .addComponent(fwPos3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fwPos2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fwPos1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        fwTemp1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        fwTemp1.setCustomThresholdColor(null);
        fwTemp1.setFrameVisible(false);
        fwTemp1.setLcdVisible(false);
        fwTemp1.setLedVisible(false);
        fwTemp1.setMaxValue(400.0);
        fwTemp1.setTitle(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.fwTemp1.title")); // NOI18N
        fwTemp1.setTrackSectionColor(java.awt.Color.red);
        fwTemp1.setTrackStart(80.0);
        fwTemp1.setTrackStartColor(java.awt.Color.red);
        fwTemp1.setTrackStop(150.0);
        fwTemp1.setTrackStopColor(java.awt.Color.red);
        fwTemp1.setUnitString("°C");
        fwTemp1.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        fwTemp2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        fwTemp2.setCustomThresholdColor(null);
        fwTemp2.setFrameVisible(false);
        fwTemp2.setLcdVisible(false);
        fwTemp2.setLedVisible(false);
        fwTemp2.setMaxValue(400.0);
        fwTemp2.setTitle(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.fwTemp2.title")); // NOI18N
        fwTemp2.setTrackSectionColor(java.awt.Color.red);
        fwTemp2.setTrackStart(80.0);
        fwTemp2.setTrackStartColor(java.awt.Color.red);
        fwTemp2.setTrackStop(150.0);
        fwTemp2.setTrackStopColor(java.awt.Color.red);
        fwTemp2.setUnitString("°C");
        fwTemp2.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        jPanel24.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup2.add(mFW2Open);
        org.openide.awt.Mnemonics.setLocalizedText(mFW2Open, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.mFW2Open.text")); // NOI18N
        mFW2Open.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                mFW2OpenItemStateChanged(evt);
            }
        });
        mFW2Open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mFW2OpenActionPerformed(evt);
            }
        });

        buttonGroup2.add(mFW2Close);
        org.openide.awt.Mnemonics.setLocalizedText(mFW2Close, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.mFW2Close.text")); // NOI18N
        mFW2Close.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                mFW2CloseItemStateChanged(evt);
            }
        });
        mFW2Close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mFW2CloseActionPerformed(evt);
            }
        });

        jLabel44.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel44, "Valve Selector");

        mFW2Spinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 3, 1));

        jLabel47.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel47, "Main Feeder Valves 2");

        buttonGroup2.add(mFW2Stop);
        mFW2Stop.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(mFW2Stop, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.mFW2Stop.text")); // NOI18N
        mFW2Stop.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                mFW2StopItemStateChanged(evt);
            }
        });
        mFW2Stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mFW2StopActionPerformed(evt);
            }
        });

        fwPos6.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        fwPos6.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        fwPos6.setFrameVisible(false);
        fwPos6.setLcdBackgroundVisible(false);
        fwPos6.setLcdVisible(false);
        fwPos6.setLedVisible(false);
        fwPos6.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        fwPos6.setPointerShadowVisible(false);
        fwPos6.setPreferredSize(new java.awt.Dimension(100, 100));
        fwPos6.setTitle(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.fwPos6.title")); // NOI18N
        fwPos6.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        fwPos6.setTitleAndUnitFontEnabled(true);
        fwPos6.setUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.fwPos6.unitString")); // NOI18N

        flow6.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        flow6.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        flow6.setLcdDecimals(2);
        flow6.setLcdUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.flow6.lcdUnitString")); // NOI18N
        fwPos6.add(flow6);
        flow6.setBounds(0, 60, 100, 30);

        fwPos5.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        fwPos5.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        fwPos5.setFrameVisible(false);
        fwPos5.setLcdBackgroundVisible(false);
        fwPos5.setLcdVisible(false);
        fwPos5.setLedVisible(false);
        fwPos5.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        fwPos5.setPointerShadowVisible(false);
        fwPos5.setPreferredSize(new java.awt.Dimension(100, 100));
        fwPos5.setTitle(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.fwPos5.title")); // NOI18N
        fwPos5.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        fwPos5.setTitleAndUnitFontEnabled(true);
        fwPos5.setUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.fwPos5.unitString")); // NOI18N

        flow5.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        flow5.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        flow5.setLcdDecimals(2);
        flow5.setLcdUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.flow5.lcdUnitString")); // NOI18N
        fwPos5.add(flow5);
        flow5.setBounds(0, 60, 100, 30);

        fwPos4.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        fwPos4.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        fwPos4.setFrameVisible(false);
        fwPos4.setLcdBackgroundVisible(false);
        fwPos4.setLcdVisible(false);
        fwPos4.setLedVisible(false);
        fwPos4.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        fwPos4.setPointerShadowVisible(false);
        fwPos4.setPreferredSize(new java.awt.Dimension(100, 100));
        fwPos4.setTitle(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.fwPos4.title")); // NOI18N
        fwPos4.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        fwPos4.setTitleAndUnitFontEnabled(true);
        fwPos4.setUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.fwPos4.unitString")); // NOI18N

        flow4.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        flow4.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        flow4.setLcdDecimals(2);
        flow4.setLcdUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.flow4.lcdUnitString")); // NOI18N
        fwPos4.add(flow4);
        flow4.setBounds(0, 60, 100, 30);

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel47)
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addComponent(jLabel44)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mFW2Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(mFW2Open)
                    .addComponent(mFW2Close)
                    .addComponent(mFW2Stop))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fwPos4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fwPos5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fwPos6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addComponent(jLabel47)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mFW2Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel44))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mFW2Open)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mFW2Stop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mFW2Close))
                    .addComponent(fwPos6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fwPos5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fwPos4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel25.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup5.add(aFWOpen1);
        org.openide.awt.Mnemonics.setLocalizedText(aFWOpen1, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.aFWOpen1.text")); // NOI18N
        aFWOpen1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                aFWOpen1ItemStateChanged(evt);
            }
        });
        aFWOpen1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aFWOpen1ActionPerformed(evt);
            }
        });

        buttonGroup5.add(aFWClose1);
        org.openide.awt.Mnemonics.setLocalizedText(aFWClose1, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.aFWClose1.text")); // NOI18N
        aFWClose1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                aFWClose1ItemStateChanged(evt);
            }
        });
        aFWClose1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aFWClose1ActionPerformed(evt);
            }
        });

        jLabel42.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel42, "Aux Feeder Valve 1");

        buttonGroup5.add(aFWStop1);
        aFWStop1.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(aFWStop1, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.aFWStop1.text")); // NOI18N
        aFWStop1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                aFWStop1ItemStateChanged(evt);
            }
        });
        aFWStop1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aFWStop1ActionPerformed(evt);
            }
        });

        aFWPos1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        aFWPos1.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        aFWPos1.setFrameVisible(false);
        aFWPos1.setLcdBackgroundVisible(false);
        aFWPos1.setLcdVisible(false);
        aFWPos1.setLedVisible(false);
        aFWPos1.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        aFWPos1.setPointerShadowVisible(false);
        aFWPos1.setPreferredSize(new java.awt.Dimension(100, 100));
        aFWPos1.setTitle(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.aFWPos1.title")); // NOI18N
        aFWPos1.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        aFWPos1.setTitleAndUnitFontEnabled(true);
        aFWPos1.setUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.aFWPos1.unitString")); // NOI18N

        auxFlow1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        auxFlow1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        auxFlow1.setLcdDecimals(2);
        auxFlow1.setLcdUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.auxFlow1.lcdUnitString")); // NOI18N
        aFWPos1.add(auxFlow1);
        auxFlow1.setBounds(0, 60, 100, 30);

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel42)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(aFWStop1)
                            .addComponent(aFWOpen1)
                            .addComponent(aFWClose1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(aFWPos1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addComponent(aFWOpen1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(aFWStop1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(aFWClose1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(aFWPos1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel26.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup6.add(aFWOpen2);
        org.openide.awt.Mnemonics.setLocalizedText(aFWOpen2, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.aFWOpen2.text")); // NOI18N
        aFWOpen2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                aFWOpen2ItemStateChanged(evt);
            }
        });
        aFWOpen2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aFWOpen2ActionPerformed(evt);
            }
        });

        buttonGroup6.add(aFWClose2);
        org.openide.awt.Mnemonics.setLocalizedText(aFWClose2, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.aFWClose2.text")); // NOI18N
        aFWClose2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                aFWClose2ItemStateChanged(evt);
            }
        });
        aFWClose2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aFWClose2ActionPerformed(evt);
            }
        });

        jLabel50.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel50, "Aux Feeder Valve 2");

        buttonGroup6.add(aFWStop2);
        aFWStop2.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(aFWStop2, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.aFWStop2.text")); // NOI18N
        aFWStop2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                aFWStop2ItemStateChanged(evt);
            }
        });
        aFWStop2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aFWStop2ActionPerformed(evt);
            }
        });

        aFWPos2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        aFWPos2.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        aFWPos2.setFrameVisible(false);
        aFWPos2.setLcdBackgroundVisible(false);
        aFWPos2.setLcdVisible(false);
        aFWPos2.setLedVisible(false);
        aFWPos2.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        aFWPos2.setPointerShadowVisible(false);
        aFWPos2.setPreferredSize(new java.awt.Dimension(100, 100));
        aFWPos2.setTitle(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.aFWPos2.title")); // NOI18N
        aFWPos2.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        aFWPos2.setTitleAndUnitFontEnabled(true);
        aFWPos2.setUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.aFWPos2.unitString")); // NOI18N

        auxFlow2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        auxFlow2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        auxFlow2.setLcdDecimals(2);
        auxFlow2.setLcdUnitString(org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.auxFlow2.lcdUnitString")); // NOI18N
        aFWPos2.add(auxFlow2);
        auxFlow2.setBounds(0, 60, 100, 30);

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel50)
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(aFWStop2)
                            .addComponent(aFWOpen2)
                            .addComponent(aFWClose2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(aFWPos2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel50)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addComponent(aFWOpen2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(aFWStop2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(aFWClose2)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(aFWPos2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel27.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup7.add(autoControlWaterLevel);
        org.openide.awt.Mnemonics.setLocalizedText(autoControlWaterLevel, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.autoControlWaterLevel.text")); // NOI18N
        autoControlWaterLevel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoControlWaterLevelItemStateChanged(evt);
            }
        });
        autoControlWaterLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoControlWaterLevelActionPerformed(evt);
            }
        });

        buttonGroup7.add(autoControlOff);
        autoControlOff.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(autoControlOff, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.autoControlOff.text")); // NOI18N
        autoControlOff.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoControlOffItemStateChanged(evt);
            }
        });
        autoControlOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoControlOffActionPerformed(evt);
            }
        });

        jLabel51.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel51, "Auto-Control");

        buttonGroup7.add(autoControl3Elem);
        org.openide.awt.Mnemonics.setLocalizedText(autoControl3Elem, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.autoControl3Elem.text")); // NOI18N
        autoControl3Elem.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoControl3ElemItemStateChanged(evt);
            }
        });
        autoControl3Elem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoControl3ElemActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(autoControlMain1, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.autoControlMain1.text")); // NOI18N
        autoControlMain1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoControlMain1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(autoControlMain2, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.autoControlMain2.text")); // NOI18N
        autoControlMain2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoControlMain2ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(autoControlAux1, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.autoControlAux1.text")); // NOI18N
        autoControlAux1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoControlAux1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(autoControlAux2, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.autoControlAux2.text")); // NOI18N
        autoControlAux2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoControlAux2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(autoControlWaterLevel)
                    .addComponent(autoControlOff)
                    .addComponent(autoControl3Elem)
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(autoControlMain1)
                            .addComponent(autoControlAux1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(autoControlAux2)
                            .addComponent(autoControlMain2))))
                .addGap(49, 49, 49))
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel51)
                .addContainerGap())
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel51)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(autoControlMain1)
                    .addComponent(autoControlMain2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(autoControlAux2)
                    .addComponent(autoControlAux1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(autoControlWaterLevel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(autoControl3Elem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(autoControlOff)
                .addContainerGap())
        );

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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(annunciatorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(auxFWFlow1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(auxFWFlow2, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(drumLevel1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(mainFWFlow1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(mainFWFlow2, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(drumLevel2, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(fwTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fwTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(289, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(annunciatorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(drumLevel1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(drumLevel2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mainFWFlow1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mainFWFlow2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(auxFWFlow1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(auxFWFlow2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fwTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fwTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(88, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel3);

        org.openide.awt.Mnemonics.setLocalizedText(jMenu1, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "CondensateUI.jMenu1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem6, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.jMenuItem6.text")); // NOI18N
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem3, "Core Map");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem10, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.jMenuItem10.text")); // NOI18N
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem10);

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem12, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.jMenuItem12.text")); // NOI18N
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem12);

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem1, "Turbine-Generators");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem2, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.jMenuItem2.text")); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem4, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "CondensateUI.jMenuItem4.text")); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem5, org.openide.util.NbBundle.getMessage(FeedwaterUI.class, "FeedwaterUI.jMenuItem5.text")); // NOI18N
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 747, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        UI.createOrContinue(CoreMap.class, false, false);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        UI.createOrContinue(TGUI.class, true, false);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        UI.createOrContinue(DearatorUI.class, true, false);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void aFWStop1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aFWStop1ActionPerformed
        auxFeederValves.get(0).setState(1);
    }//GEN-LAST:event_aFWStop1ActionPerformed

    private void aFWStop1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_aFWStop1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_aFWStop1ItemStateChanged

    private void aFWClose1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aFWClose1ActionPerformed
        auxFeederValves.get(0).setState(0);
    }//GEN-LAST:event_aFWClose1ActionPerformed

    private void aFWClose1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_aFWClose1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_aFWClose1ItemStateChanged

    private void aFWOpen1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aFWOpen1ActionPerformed
        auxFeederValves.get(0).setState(2);
    }//GEN-LAST:event_aFWOpen1ActionPerformed

    private void aFWOpen1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_aFWOpen1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_aFWOpen1ItemStateChanged

    private void mFW2StopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mFW2StopActionPerformed
        mainFeederValves.get((int)mFW2Spinner.getValue() + 2).setState(1);
    }//GEN-LAST:event_mFW2StopActionPerformed

    private void mFW2StopItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_mFW2StopItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_mFW2StopItemStateChanged

    private void mFW2CloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mFW2CloseActionPerformed
        mainFeederValves.get((int)mFW2Spinner.getValue() + 2).setState(0);
    }//GEN-LAST:event_mFW2CloseActionPerformed

    private void mFW2CloseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_mFW2CloseItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_mFW2CloseItemStateChanged

    private void mFW2OpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mFW2OpenActionPerformed
        mainFeederValves.get((int)mFW2Spinner.getValue() + 2).setState(2);
    }//GEN-LAST:event_mFW2OpenActionPerformed

    private void mFW2OpenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_mFW2OpenItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_mFW2OpenItemStateChanged

    private void mFW1StopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mFW1StopActionPerformed
        mainFeederValves.get((int)mFW1Spinner.getValue() - 1).setState(1);
    }//GEN-LAST:event_mFW1StopActionPerformed

    private void mFW1StopItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_mFW1StopItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_mFW1StopItemStateChanged

    private void mFW1CloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mFW1CloseActionPerformed
        mainFeederValves.get((int)mFW1Spinner.getValue() - 1).setState(0);
    }//GEN-LAST:event_mFW1CloseActionPerformed

    private void mFW1CloseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_mFW1CloseItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_mFW1CloseItemStateChanged

    private void mFW1OpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mFW1OpenActionPerformed
        mainFeederValves.get((int)mFW1Spinner.getValue() - 1).setState(2);
    }//GEN-LAST:event_mFW1OpenActionPerformed

    private void mFW1OpenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_mFW1OpenItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_mFW1OpenItemStateChanged

    private void stopAuxFWPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopAuxFWPActionPerformed
        auxFeedwaterPumps.get((int)auxFWPSpinner.getValue() - 1).setActive(false);
        switch((int)auxFWPSpinner.getValue()) {
            case 1:
                power2A1.setLedOn(false);
                break;
            case 2:
                power2A2.setLedOn(false);
                break;
            case 3:
                power2A3.setLedOn(false);
                break;
            case 4:
                power2A4.setLedOn(false);
                break;
            case 5:
                power2A5.setLedOn(false);
                break;
            case 6:
                power2A6.setLedOn(false);
                break;
        }
    }//GEN-LAST:event_stopAuxFWPActionPerformed

    private void stopAuxFWPItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_stopAuxFWPItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_stopAuxFWPItemStateChanged

    private void startAuxFWPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startAuxFWPActionPerformed
        auxFeedwaterPumps.get((int)auxFWPSpinner.getValue() - 1).setActive(true);
        switch((int)auxFWPSpinner.getValue()) {
            case 1:
                power2A1.setLedOn(true);
                break;
            case 2:
                power2A2.setLedOn(true);
                break;
            case 3:
                power2A3.setLedOn(true);
                break;
            case 4:
                power2A4.setLedOn(true);
                break;
            case 5:
                power2A5.setLedOn(true);
                break;
            case 6:
                power2A6.setLedOn(true);
                break;
        }
    }//GEN-LAST:event_startAuxFWPActionPerformed

    private void startAuxFWPItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_startAuxFWPItemStateChanged
        //TODO
    }//GEN-LAST:event_startAuxFWPItemStateChanged

    private void jTextField11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField11ActionPerformed

    private void jTextField10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField10ActionPerformed

    private void trip2BActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trip2BActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_trip2BActionPerformed

    private void mainTripActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainTripActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mainTripActionPerformed

    private void mainCavitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainCavitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mainCavitActionPerformed

    private void drum2LowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drum2LowActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_drum2LowActionPerformed

    private void stopMWFPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopMWFPActionPerformed
        mainFeedwaterPumps.get((int)mFWPSpinner.getValue() - 1).setActive(false);
        switch((int)mFWPSpinner.getValue()) {
            case 1:
                powerFWP1.setLedOn(false);
                break;
            case 2:
                powerFWP2.setLedOn(false);
                break;
            case 3:
                powerFWP3.setLedOn(false);
                break;
            case 4:
                powerFWP4.setLedOn(false);
                break;
            case 5:
                powerFWP5.setLedOn(false);
                break;
            case 6:
                powerFWP6.setLedOn(false);
                break;
            case 7:
                powerFWP7.setLedOn(false);
                break;
        }
    }//GEN-LAST:event_stopMWFPActionPerformed

    private void stopMWFPItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_stopMWFPItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_stopMWFPItemStateChanged

    private void startMFWPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startMFWPActionPerformed
        mainFeedwaterPumps.get((int)mFWPSpinner.getValue() - 1).setActive(true);
        switch((int)mFWPSpinner.getValue()) {
            case 1:
                powerFWP1.setLedOn(true);
                break;
            case 2:
                powerFWP2.setLedOn(true);
                break;
            case 3:
                powerFWP3.setLedOn(true);
                break;
            case 4:
                powerFWP4.setLedOn(true);
                break;
            case 5:
                powerFWP5.setLedOn(true);
                break;
            case 6:
                powerFWP6.setLedOn(true);
                break;
            case 7:
                powerFWP7.setLedOn(true);
                break;
        }
    }//GEN-LAST:event_startMFWPActionPerformed

    private void startMFWPItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_startMFWPItemStateChanged
        //TODO
    }//GEN-LAST:event_startMFWPItemStateChanged

    private void aFWOpen2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_aFWOpen2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_aFWOpen2ItemStateChanged

    private void aFWOpen2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aFWOpen2ActionPerformed
        auxFeederValves.get(1).setState(2);
    }//GEN-LAST:event_aFWOpen2ActionPerformed

    private void aFWClose2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_aFWClose2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_aFWClose2ItemStateChanged

    private void aFWClose2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aFWClose2ActionPerformed
        auxFeederValves.get(1).setState(0);
    }//GEN-LAST:event_aFWClose2ActionPerformed

    private void aFWStop2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_aFWStop2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_aFWStop2ItemStateChanged

    private void aFWStop2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aFWStop2ActionPerformed
        auxFeederValves.get(1).setState(1);
    }//GEN-LAST:event_aFWStop2ActionPerformed

    private void autoControlWaterLevelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoControlWaterLevelItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoControlWaterLevelItemStateChanged

    private void autoControlWaterLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoControlWaterLevelActionPerformed
        autoControlAux1ActionPerformed(evt);
        autoControlAux2ActionPerformed(evt);
        autoControlMain1ActionPerformed(evt);
        autoControlMain2ActionPerformed(evt);
    }//GEN-LAST:event_autoControlWaterLevelActionPerformed

    private void autoControlOffItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoControlOffItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoControlOffItemStateChanged

    private void autoControlOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoControlOffActionPerformed
        autoControl.mainFeederControl.forEach(controller -> {
            controller.setEnabled(false);
        });
        autoControl.auxFeederControl.forEach(controller -> {
            controller.setEnabled(false);
        });
    }//GEN-LAST:event_autoControlOffActionPerformed

    private void autoControl3ElemItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoControl3ElemItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoControl3ElemItemStateChanged

    private void autoControl3ElemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoControl3ElemActionPerformed
        autoControlAux1ActionPerformed(evt);
        autoControlAux2ActionPerformed(evt);
        autoControlMain1ActionPerformed(evt);
        autoControlMain2ActionPerformed(evt);
    }//GEN-LAST:event_autoControl3ElemActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        UI.createOrContinue(CondensateUI.class, true, false);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        annunciator.acknowledge();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void autoControlAux1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoControlAux1ActionPerformed
        if (autoControlAux1.isSelected()) {
            if (autoControl3Elem.isSelected()) {
                autoControl.auxFeederControl.get(0).setEnabled(true);
            } else if (autoControlWaterLevel.isSelected()) {
                autoControl.auxFeederControl.get(0).setEnabled(true);
            }
        } else {
            autoControl.auxFeederControl.get(0).setEnabled(false);
        }
    }//GEN-LAST:event_autoControlAux1ActionPerformed

    private void autoControlAux2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoControlAux2ActionPerformed
        if (autoControlAux2.isSelected()) {
            if (autoControl3Elem.isSelected()) {
                autoControl.auxFeederControl.get(1).setEnabled(true);
            } else if (autoControlWaterLevel.isSelected()) {
                autoControl.auxFeederControl.get(1).setEnabled(true);
            }
        } else {
            autoControl.auxFeederControl.get(1).setEnabled(false);
        }
    }//GEN-LAST:event_autoControlAux2ActionPerformed

    private void autoControlMain1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoControlMain1ActionPerformed
        if (autoControlMain1.isSelected()) {
            if (autoControl3Elem.isSelected()) {
                autoControl.mainFeederControl.get(0).setEnabled(true);
            } else if (autoControlWaterLevel.isSelected()) {
                autoControl.mainFeederControl.get(0).setEnabled(true);
            }
        } else {
            autoControl.mainFeederControl.get(0).setEnabled(false);
        }
    }//GEN-LAST:event_autoControlMain1ActionPerformed

    private void autoControlMain2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoControlMain2ActionPerformed
        if (autoControlMain2.isSelected()) {
            if (autoControl3Elem.isSelected()) {
                autoControl.mainFeederControl.get(1).setEnabled(true);
            } else if (autoControlWaterLevel.isSelected()) {
                autoControl.mainFeederControl.get(1).setEnabled(true);
            }
        } else {
            autoControl.mainFeederControl.get(1).setEnabled(false);
        }
    }//GEN-LAST:event_autoControlMain2ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        UI.createOrContinue(PCSUI.class, true, false);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        UI.createOrContinue(SelsynPanel.class, false, false);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        NPPSim.ui.toFront();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        UI.createOrContinue(MCPUI.class, false, false);
    }//GEN-LAST:event_jMenuItem12ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField FWTemp;
    private javax.swing.JRadioButton aFWClose1;
    private javax.swing.JRadioButton aFWClose2;
    private javax.swing.JRadioButton aFWOpen1;
    private javax.swing.JRadioButton aFWOpen2;
    private eu.hansolo.steelseries.gauges.Radial2Top aFWPos1;
    private eu.hansolo.steelseries.gauges.Radial2Top aFWPos2;
    private javax.swing.JRadioButton aFWStop1;
    private javax.swing.JRadioButton aFWStop2;
    private eu.hansolo.steelseries.gauges.DisplaySingle amps1A;
    private eu.hansolo.steelseries.gauges.DisplaySingle amps2A;
    private javax.swing.JPanel annunciatorPanel;
    private javax.swing.JRadioButton autoControl3Elem;
    private javax.swing.JCheckBox autoControlAux1;
    private javax.swing.JCheckBox autoControlAux2;
    private javax.swing.JCheckBox autoControlMain1;
    private javax.swing.JCheckBox autoControlMain2;
    private javax.swing.JRadioButton autoControlOff;
    private javax.swing.JRadioButton autoControlWaterLevel;
    private javax.swing.JTextField auxCavit;
    private eu.hansolo.steelseries.gauges.Linear auxFWFlow1;
    private eu.hansolo.steelseries.gauges.Linear auxFWFlow2;
    private javax.swing.JSpinner auxFWPSpinner;
    private eu.hansolo.steelseries.gauges.DisplaySingle auxFlow1;
    private eu.hansolo.steelseries.gauges.DisplaySingle auxFlow2;
    private javax.swing.JTextField auxTrip;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.ButtonGroup buttonGroup6;
    private javax.swing.ButtonGroup buttonGroup7;
    private javax.swing.ButtonGroup buttonGroup8;
    private javax.swing.JTextField drum1High;
    private javax.swing.JTextField drum1Low;
    private javax.swing.JTextField drum2High;
    private javax.swing.JTextField drum2Low;
    private eu.hansolo.steelseries.gauges.Linear drumLevel1;
    private eu.hansolo.steelseries.gauges.Linear drumLevel2;
    private eu.hansolo.steelseries.gauges.DisplaySingle flow1;
    private eu.hansolo.steelseries.gauges.DisplaySingle flow1A;
    private eu.hansolo.steelseries.gauges.DisplaySingle flow2;
    private eu.hansolo.steelseries.gauges.DisplaySingle flow2A;
    private eu.hansolo.steelseries.gauges.DisplaySingle flow3;
    private eu.hansolo.steelseries.gauges.DisplaySingle flow4;
    private eu.hansolo.steelseries.gauges.DisplaySingle flow5;
    private eu.hansolo.steelseries.gauges.DisplaySingle flow6;
    private eu.hansolo.steelseries.gauges.Radial2Top fwPos1;
    private eu.hansolo.steelseries.gauges.Radial2Top fwPos2;
    private eu.hansolo.steelseries.gauges.Radial2Top fwPos3;
    private eu.hansolo.steelseries.gauges.Radial2Top fwPos4;
    private eu.hansolo.steelseries.gauges.Radial2Top fwPos5;
    private eu.hansolo.steelseries.gauges.Radial2Top fwPos6;
    private eu.hansolo.steelseries.gauges.Linear fwTemp1;
    private eu.hansolo.steelseries.gauges.Linear fwTemp2;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JRadioButton mFW1Close;
    private javax.swing.JRadioButton mFW1Open;
    private javax.swing.JSpinner mFW1Spinner;
    private javax.swing.JRadioButton mFW1Stop;
    private javax.swing.JRadioButton mFW2Close;
    private javax.swing.JRadioButton mFW2Open;
    private javax.swing.JSpinner mFW2Spinner;
    private javax.swing.JRadioButton mFW2Stop;
    private javax.swing.JSpinner mFWPSpinner;
    private javax.swing.JTextField mainCavit;
    private eu.hansolo.steelseries.gauges.Linear mainFWFlow1;
    private eu.hansolo.steelseries.gauges.Linear mainFWFlow2;
    private javax.swing.JTextField mainTrip;
    private eu.hansolo.steelseries.extras.Led power2A1;
    private eu.hansolo.steelseries.extras.Led power2A2;
    private eu.hansolo.steelseries.extras.Led power2A3;
    private eu.hansolo.steelseries.extras.Led power2A4;
    private eu.hansolo.steelseries.extras.Led power2A5;
    private eu.hansolo.steelseries.extras.Led power2A6;
    private eu.hansolo.steelseries.extras.Led powerFWP1;
    private eu.hansolo.steelseries.extras.Led powerFWP2;
    private eu.hansolo.steelseries.extras.Led powerFWP3;
    private eu.hansolo.steelseries.extras.Led powerFWP4;
    private eu.hansolo.steelseries.extras.Led powerFWP5;
    private eu.hansolo.steelseries.extras.Led powerFWP6;
    private eu.hansolo.steelseries.extras.Led powerFWP7;
    private eu.hansolo.steelseries.gauges.DisplaySingle rpm1A;
    private eu.hansolo.steelseries.gauges.DisplaySingle rpm2A;
    private javax.swing.JRadioButton startAuxFWP;
    private javax.swing.JRadioButton startMFWP;
    private javax.swing.JRadioButton stopAuxFWP;
    private javax.swing.JRadioButton stopMWFP;
    private javax.swing.JTextField trip2B;
    // End of variables declaration//GEN-END:variables
}
