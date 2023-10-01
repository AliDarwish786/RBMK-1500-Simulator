package com.darwish.nppsim;

import static com.darwish.nppsim.NPPSim.autoControl;
import static com.darwish.nppsim.NPPSim.dearatorValves;
import static com.darwish.nppsim.NPPSim.dearators;
import static com.darwish.nppsim.NPPSim.fwSuctionHeader;
import static com.darwish.nppsim.NPPSim.pcs;

public class DearatorUI extends javax.swing.JFrame implements UIUpdateable {
    private final Dearator da1 = dearators.get(0);
    private final Dearator da2 = dearators.get(1);
    private final Dearator da3 = dearators.get(2);
    private final Dearator da4 = dearators.get(3);
    private final Annunciator annunciator;
    /**
     * Creates new form DearatorUI
     */
    public DearatorUI() {
        initComponents();
        this.setTitle("Dearators");
        annunciator = new Annunciator(annunciatorPanel);
        level1.setTrackVisible(true);
        level1.setTrackStop(40);
        level1.setTrackStart(25);
        level1.setMaxValue(40);
        level1.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        level1.setTrackStartColor(new java.awt.Color(255, 0, 0));
        level1.setTrackStopColor(new java.awt.Color(255, 0, 0));
        level2.setTrackVisible(true);
        level2.setTrackStop(40);
        level2.setTrackStart(25);
        level2.setMaxValue(40);
        level2.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        level2.setTrackStartColor(new java.awt.Color(255, 0, 0));
        level2.setTrackStopColor(new java.awt.Color(255, 0, 0));
        level3.setTrackVisible(true);
        level3.setTrackStop(40);
        level3.setTrackStart(25);
        level3.setMaxValue(40);
        level3.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        level3.setTrackStartColor(new java.awt.Color(255, 0, 0));
        level3.setTrackStopColor(new java.awt.Color(255, 0, 0));
        level3.setTrackVisible(true);
        level4.setTrackStop(40);
        level4.setTrackStart(25);
        level4.setMaxValue(40);
        level4.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        level4.setTrackStartColor(new java.awt.Color(255, 0, 0));
        level4.setTrackStopColor(new java.awt.Color(255, 0, 0));
        initializeDialUpdateThread();
        
        //set variables
        isolate1.setSelected(fwSuctionHeader.isolationValveArray.get(0).getPosition() == 0);
        isolate2.setSelected(fwSuctionHeader.isolationValveArray.get(1).getPosition() == 0);
        isolate3.setSelected(fwSuctionHeader.isolationValveArray.get(2).getPosition() == 0);
        isolate4.setSelected(fwSuctionHeader.isolationValveArray.get(3).getPosition() == 0);
        autoWaterLevel1On.setSelected(autoControl.dearatorWaterControl.get(0).isEnabled() || autoControl.dearatorWaterAndMakeupControl.get(0).isEnabled());
        autoWaterLevel2On.setSelected(autoControl.dearatorWaterControl.get(1).isEnabled() || autoControl.dearatorWaterAndMakeupControl.get(1).isEnabled());
        autoWaterLevel3On.setSelected(autoControl.dearatorWaterControl.get(2).isEnabled() || autoControl.dearatorWaterAndMakeupControl.get(2).isEnabled());
        autoWaterLevel4On.setSelected(autoControl.dearatorWaterControl.get(3).isEnabled() || autoControl.dearatorWaterAndMakeupControl.get(3).isEnabled());
        autoSteamPressure1On.setSelected(autoControl.dearatorPressureControl.get(0).isEnabled());
        autoSteamPressure2On.setSelected(autoControl.dearatorPressureControl.get(1).isEnabled());
        autoSteamPressure3On.setSelected(autoControl.dearatorPressureControl.get(2).isEnabled());
        autoSteamPressure4On.setSelected(autoControl.dearatorPressureControl.get(3).isEnabled());
        setpoint1.setLcdValue(autoControl.dearatorPressureControl.get(0).getSetpoint());
        setpoint2.setLcdValue(autoControl.dearatorPressureControl.get(1).getSetpoint());
        setpoint3.setLcdValue(autoControl.dearatorPressureControl.get(2).getSetpoint());
        setpoint4.setLcdValue(autoControl.dearatorPressureControl.get(3).getSetpoint());
    }
    
    @Override
    public void update() {
        checkAlarms();
        if (this.isVisible()) {
            temp1.setLcdValue(da1.getWaterTemperature());
            temp2.setLcdValue(da2.getWaterTemperature());
            temp3.setLcdValue(da3.getWaterTemperature());
            temp4.setLcdValue(da4.getWaterTemperature());
            steamIn1.setLcdValue(da1.getSteamInflowRate());
            steamIn2.setLcdValue(da2.getSteamInflowRate());
            steamIn3.setLcdValue(da3.getSteamInflowRate());
            steamIn4.setLcdValue(da4.getSteamInflowRate());
            steamOut1.setLcdValue(da1.getSteamOutflowRate());
            steamOut2.setLcdValue(da2.getSteamOutflowRate());
            steamOut3.setLcdValue(da3.getSteamOutflowRate());
            steamOut4.setLcdValue(da4.getSteamOutflowRate());
            over1.setLcdValue(pcs.dearatorOverflowValves.get(0).timestepFlow * 20);
            over2.setLcdValue(pcs.dearatorOverflowValves.get(1).timestepFlow * 20);
            over3.setLcdValue(pcs.dearatorOverflowValves.get(2).timestepFlow * 20);
            over4.setLcdValue(pcs.dearatorOverflowValves.get(3).timestepFlow * 20);
        }
    }

    @Override
    public void initializeDialUpdateThread() {
        Thread dearatorUIThread = new Thread(() -> {
            try {
                while (true) {
                    annunciator.update();
                    if (this.isVisible()) {
                        java.awt.EventQueue.invokeLater(() -> {
                            float waterInV1Pos = dearatorValves.get(0).getPosition() * 100;
                            float waterInV2Pos = dearatorValves.get(1).getPosition() * 100;
                            float waterInV3Pos = dearatorValves.get(2).getPosition() * 100;
                            float waterInV4Pos = dearatorValves.get(3).getPosition() * 100;
                            float steamInV1Pos = dearators.get(0).steamInlet.getPosition() * 100;
                            float steamInV2Pos = dearators.get(1).steamInlet.getPosition() * 100;
                            float steamInV3Pos = dearators.get(2).steamInlet.getPosition() * 100;
                            float steamInV4Pos = dearators.get(3).steamInlet.getPosition() * 100;
                            float steamOutV1Pos = dearators.get(0).steamOutlet.getPosition() * 100;
                            float steamOutV2Pos = dearators.get(1).steamOutlet.getPosition() * 100;
                            float steamOutV3Pos = dearators.get(2).steamOutlet.getPosition() * 100;
                            float steamOutV4Pos = dearators.get(3).steamOutlet.getPosition() * 100;

                            press1.setValue(da1.getPressure());
                            press2.setValue(da2.getPressure());
                            press3.setValue(da3.getPressure());
                            press4.setValue(da4.getPressure());
                            waterIn1.setValue(da1.getWaterInflowRate());
                            waterIn2.setValue(da2.getWaterInflowRate());
                            waterIn3.setValue(da3.getWaterInflowRate());
                            waterIn4.setValue(da4.getWaterInflowRate());
                            waterOut1.setValue(da1.getWaterOutflowRate());
                            waterOut2.setValue(da2.getWaterOutflowRate());
                            waterOut3.setValue(da3.getWaterOutflowRate());
                            waterOut4.setValue(da4.getWaterOutflowRate());
                            level1.setValue(da1.getWaterLevel());
                            level2.setValue(da2.getWaterLevel());
                            level3.setValue(da3.getWaterLevel());
                            level4.setValue(da4.getWaterLevel());
                            waterInV1.setValue(waterInV1Pos);
                            waterInV2.setValue(waterInV2Pos);
                            waterInV3.setValue(waterInV3Pos);
                            waterInV4.setValue(waterInV4Pos);
                            steamInV1.setValue(steamInV1Pos);
                            steamInV2.setValue(steamInV2Pos);
                            steamInV3.setValue(steamInV3Pos);
                            steamInV4.setValue(steamInV4Pos);
                            steamOutV1.setValue(steamOutV1Pos);
                            steamOutV2.setValue(steamOutV2Pos);
                            steamOutV3.setValue(steamOutV3Pos);
                            steamOutV4.setValue(steamOutV4Pos);
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
        });
        dearatorUIThread.start();
        UI.uiThreads.add(dearatorUIThread);
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
        annunciator.setTrigger(da1.getPressure() > 1.42, hPress1);
        annunciator.setTrigger(da2.getPressure() > 1.42, hPress2);
        annunciator.setTrigger(da3.getPressure() > 1.42, hPress3);
        annunciator.setTrigger(da4.getPressure() > 1.42, hPress4);
        annunciator.setTrigger(da1.getWaterLevel() < -50 || da1.getWaterLevel() > 20, hLevel1);
        annunciator.setTrigger(da2.getWaterLevel() < -50 || da2.getWaterLevel() > 20, hLevel2);
        annunciator.setTrigger(da3.getWaterLevel() < -50 || da3.getWaterLevel() > 20, hLevel3);
        annunciator.setTrigger(da4.getWaterLevel() < -50 || da4.getWaterLevel() > 20, hLevel4);
        annunciator.setTrigger(pcs.dearatorOverflowValves.get(0).timestepFlow > 0, overflow1);
        annunciator.setTrigger(pcs.dearatorOverflowValves.get(1).timestepFlow > 0, overflow2);
        annunciator.setTrigger(pcs.dearatorOverflowValves.get(2).timestepFlow > 0, overflow3);
        annunciator.setTrigger(pcs.dearatorOverflowValves.get(3).timestepFlow > 0, overflow4);
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
        buttonGroup9 = new javax.swing.ButtonGroup();
        buttonGroup10 = new javax.swing.ButtonGroup();
        buttonGroup11 = new javax.swing.ButtonGroup();
        buttonGroup12 = new javax.swing.ButtonGroup();
        buttonGroup13 = new javax.swing.ButtonGroup();
        buttonGroup14 = new javax.swing.ButtonGroup();
        buttonGroup15 = new javax.swing.ButtonGroup();
        buttonGroup16 = new javax.swing.ButtonGroup();
        buttonGroup17 = new javax.swing.ButtonGroup();
        buttonGroup18 = new javax.swing.ButtonGroup();
        buttonGroup19 = new javax.swing.ButtonGroup();
        buttonGroup20 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        annunciatorPanel = new javax.swing.JPanel();
        hLevel1 = new javax.swing.JTextField();
        hLevel2 = new javax.swing.JTextField();
        hLevel3 = new javax.swing.JTextField();
        hLevel4 = new javax.swing.JTextField();
        hPress1 = new javax.swing.JTextField();
        hPress2 = new javax.swing.JTextField();
        hPress3 = new javax.swing.JTextField();
        hPress4 = new javax.swing.JTextField();
        overflow1 = new javax.swing.JTextField();
        overflow2 = new javax.swing.JTextField();
        overflow3 = new javax.swing.JTextField();
        overflow4 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        waterInV1 = new eu.hansolo.steelseries.gauges.Radial2Top();
        waterInVOpen1 = new javax.swing.JRadioButton();
        waterInVStop1 = new javax.swing.JRadioButton();
        waterInVClose1 = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        autoWaterLevel1On = new javax.swing.JRadioButton();
        autoWaterLevel1Off = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        autoSteamPressure1On = new javax.swing.JRadioButton();
        autoSteamPressure1Off = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        setpoint1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        precisionDecrement1 = new javax.swing.JButton();
        precisionIncrement1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        steamInV1 = new eu.hansolo.steelseries.gauges.Radial2Top();
        steamInVOpen1 = new javax.swing.JRadioButton();
        steamInVStop1 = new javax.swing.JRadioButton();
        steamInVClose1 = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        steamOutV1 = new eu.hansolo.steelseries.gauges.Radial2Top();
        steamOutVOpen1 = new javax.swing.JRadioButton();
        steamOutVStop1 = new javax.swing.JRadioButton();
        steamOutVClose1 = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        temp1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        steamIn1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        steamOut1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        over1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        isolate1 = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        waterInV4 = new eu.hansolo.steelseries.gauges.Radial2Top();
        waterInVOpen4 = new javax.swing.JRadioButton();
        waterInVStop4 = new javax.swing.JRadioButton();
        waterInVClose4 = new javax.swing.JRadioButton();
        jLabel11 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        autoWaterLevel4On = new javax.swing.JRadioButton();
        autoWaterLevel4Off = new javax.swing.JRadioButton();
        jLabel13 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        autoSteamPressure4On = new javax.swing.JRadioButton();
        autoSteamPressure4Off = new javax.swing.JRadioButton();
        jLabel14 = new javax.swing.JLabel();
        setpoint4 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        precisionDecrement4 = new javax.swing.JButton();
        precisionIncrement4 = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        steamInV4 = new eu.hansolo.steelseries.gauges.Radial2Top();
        steamInVopen4 = new javax.swing.JRadioButton();
        steamInVStop4 = new javax.swing.JRadioButton();
        steamInVClose4 = new javax.swing.JRadioButton();
        jLabel17 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        steamOutV4 = new eu.hansolo.steelseries.gauges.Radial2Top();
        steamOutVOpen4 = new javax.swing.JRadioButton();
        steamOutVStop4 = new javax.swing.JRadioButton();
        steamOutVClose4 = new javax.swing.JRadioButton();
        jLabel18 = new javax.swing.JLabel();
        temp4 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        steamIn4 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        steamOut4 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        over4 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        isolate4 = new javax.swing.JCheckBox();
        jPanel16 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        waterInV2 = new eu.hansolo.steelseries.gauges.Radial2Top();
        waterInVOpen2 = new javax.swing.JRadioButton();
        waterInVStop2 = new javax.swing.JRadioButton();
        waterInVClose2 = new javax.swing.JRadioButton();
        jLabel20 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        autoWaterLevel2On = new javax.swing.JRadioButton();
        autoWaterLevel2Off = new javax.swing.JRadioButton();
        jLabel22 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        autoSteamPressure2On = new javax.swing.JRadioButton();
        autoSteamPressure2Off = new javax.swing.JRadioButton();
        jLabel23 = new javax.swing.JLabel();
        setpoint2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        precisionDecrement2 = new javax.swing.JButton();
        precisionIncrement2 = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        steamInV2 = new eu.hansolo.steelseries.gauges.Radial2Top();
        steamInVopen2 = new javax.swing.JRadioButton();
        steamInVStop2 = new javax.swing.JRadioButton();
        steamInVClose2 = new javax.swing.JRadioButton();
        jLabel26 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        steamOutV2 = new eu.hansolo.steelseries.gauges.Radial2Top();
        steamOutVOpen2 = new javax.swing.JRadioButton();
        steamOutVStop2 = new javax.swing.JRadioButton();
        steamOutVClose2 = new javax.swing.JRadioButton();
        jLabel27 = new javax.swing.JLabel();
        temp2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        steamIn2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        steamOut2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        over2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        isolate2 = new javax.swing.JCheckBox();
        jPanel23 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jPanel24 = new javax.swing.JPanel();
        waterInV3 = new eu.hansolo.steelseries.gauges.Radial2Top();
        waterInVOpen3 = new javax.swing.JRadioButton();
        waterInVStop3 = new javax.swing.JRadioButton();
        waterInVClose3 = new javax.swing.JRadioButton();
        jLabel29 = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jPanel26 = new javax.swing.JPanel();
        autoWaterLevel3On = new javax.swing.JRadioButton();
        autoWaterLevel3Off = new javax.swing.JRadioButton();
        jLabel31 = new javax.swing.JLabel();
        jPanel27 = new javax.swing.JPanel();
        autoSteamPressure3On = new javax.swing.JRadioButton();
        autoSteamPressure3Off = new javax.swing.JRadioButton();
        jLabel32 = new javax.swing.JLabel();
        setpoint3 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        precisionDecrement3 = new javax.swing.JButton();
        precisionIncrement3 = new javax.swing.JButton();
        jPanel28 = new javax.swing.JPanel();
        steamInV3 = new eu.hansolo.steelseries.gauges.Radial2Top();
        steamInVopen3 = new javax.swing.JRadioButton();
        steamInVStop3 = new javax.swing.JRadioButton();
        steamInVClose3 = new javax.swing.JRadioButton();
        jLabel35 = new javax.swing.JLabel();
        jPanel29 = new javax.swing.JPanel();
        steamOutV3 = new eu.hansolo.steelseries.gauges.Radial2Top();
        steamOutVOpen3 = new javax.swing.JRadioButton();
        steamOutVStop3 = new javax.swing.JRadioButton();
        steamOutVClose3 = new javax.swing.JRadioButton();
        jLabel36 = new javax.swing.JLabel();
        temp3 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        steamIn3 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        steamOut3 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        over3 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        isolate3 = new javax.swing.JCheckBox();
        level1 = new eu.hansolo.steelseries.gauges.Linear();
        waterIn1 = new eu.hansolo.steelseries.gauges.Linear();
        waterOut1 = new eu.hansolo.steelseries.gauges.Linear();
        level2 = new eu.hansolo.steelseries.gauges.Linear();
        waterIn2 = new eu.hansolo.steelseries.gauges.Linear();
        waterOut2 = new eu.hansolo.steelseries.gauges.Linear();
        level3 = new eu.hansolo.steelseries.gauges.Linear();
        waterIn3 = new eu.hansolo.steelseries.gauges.Linear();
        waterOut3 = new eu.hansolo.steelseries.gauges.Linear();
        level4 = new eu.hansolo.steelseries.gauges.Linear();
        waterIn4 = new eu.hansolo.steelseries.gauges.Linear();
        waterOut4 = new eu.hansolo.steelseries.gauges.Linear();
        press1 = new eu.hansolo.steelseries.gauges.Linear();
        press2 = new eu.hansolo.steelseries.gauges.Linear();
        press3 = new eu.hansolo.steelseries.gauges.Linear();
        press4 = new eu.hansolo.steelseries.gauges.Linear();
        jButton1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();

        jPanel3.setBackground(UI.BACKGROUND);

        annunciatorPanel.setLayout(new java.awt.GridLayout(3, 6));

        hLevel1.setEditable(false);
        hLevel1.setBackground(new java.awt.Color(142, 0, 0));
        hLevel1.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        hLevel1.setForeground(new java.awt.Color(0, 0, 0));
        hLevel1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        hLevel1.setText("Water level 1");
        hLevel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        hLevel1.setFocusable(false);
        hLevel1.setPreferredSize(new java.awt.Dimension(100, 30));
        hLevel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hLevel1ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(hLevel1);

        hLevel2.setEditable(false);
        hLevel2.setBackground(new java.awt.Color(142, 0, 0));
        hLevel2.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        hLevel2.setForeground(new java.awt.Color(0, 0, 0));
        hLevel2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        hLevel2.setText("Water level 2");
        hLevel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        hLevel2.setFocusable(false);
        hLevel2.setPreferredSize(new java.awt.Dimension(100, 30));
        hLevel2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hLevel2ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(hLevel2);

        hLevel3.setEditable(false);
        hLevel3.setBackground(new java.awt.Color(142, 0, 0));
        hLevel3.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        hLevel3.setForeground(new java.awt.Color(0, 0, 0));
        hLevel3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        hLevel3.setText("Water level 3");
        hLevel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        hLevel3.setFocusable(false);
        annunciatorPanel.add(hLevel3);

        hLevel4.setEditable(false);
        hLevel4.setBackground(new java.awt.Color(142, 0, 0));
        hLevel4.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        hLevel4.setForeground(new java.awt.Color(0, 0, 0));
        hLevel4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        hLevel4.setText("Water level 4");
        hLevel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        hLevel4.setFocusable(false);
        annunciatorPanel.add(hLevel4);

        hPress1.setEditable(false);
        hPress1.setBackground(new java.awt.Color(142, 0, 0));
        hPress1.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        hPress1.setForeground(new java.awt.Color(0, 0, 0));
        hPress1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        hPress1.setText("High press. 1");
        hPress1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        hPress1.setFocusable(false);
        annunciatorPanel.add(hPress1);

        hPress2.setEditable(false);
        hPress2.setBackground(new java.awt.Color(142, 0, 0));
        hPress2.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        hPress2.setForeground(new java.awt.Color(0, 0, 0));
        hPress2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        hPress2.setText("High press. 2");
        hPress2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        hPress2.setFocusable(false);
        hPress2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hPress2ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(hPress2);

        hPress3.setEditable(false);
        hPress3.setBackground(new java.awt.Color(142, 0, 0));
        hPress3.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        hPress3.setForeground(new java.awt.Color(0, 0, 0));
        hPress3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        hPress3.setText("High press. 3");
        hPress3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        hPress3.setFocusable(false);
        annunciatorPanel.add(hPress3);

        hPress4.setEditable(false);
        hPress4.setBackground(new java.awt.Color(142, 0, 0));
        hPress4.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        hPress4.setForeground(new java.awt.Color(0, 0, 0));
        hPress4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        hPress4.setText("High press. 4");
        hPress4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        hPress4.setFocusable(false);
        annunciatorPanel.add(hPress4);

        overflow1.setEditable(false);
        overflow1.setBackground(new java.awt.Color(107, 103, 0));
        overflow1.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        overflow1.setForeground(new java.awt.Color(0, 0, 0));
        overflow1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        overflow1.setText("Overflow 1");
        overflow1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        overflow1.setFocusable(false);
        overflow1.setPreferredSize(new java.awt.Dimension(100, 30));
        overflow1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overflow1ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(overflow1);

        overflow2.setEditable(false);
        overflow2.setBackground(new java.awt.Color(107, 103, 0));
        overflow2.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        overflow2.setForeground(new java.awt.Color(0, 0, 0));
        overflow2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        overflow2.setText("Overflow 2");
        overflow2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        overflow2.setFocusable(false);
        overflow2.setPreferredSize(new java.awt.Dimension(100, 30));
        overflow2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overflow2ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(overflow2);

        overflow3.setEditable(false);
        overflow3.setBackground(new java.awt.Color(107, 103, 0));
        overflow3.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        overflow3.setForeground(new java.awt.Color(0, 0, 0));
        overflow3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        overflow3.setText("Overflow 3");
        overflow3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        overflow3.setFocusable(false);
        overflow3.setPreferredSize(new java.awt.Dimension(100, 30));
        overflow3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overflow3ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(overflow3);

        overflow4.setEditable(false);
        overflow4.setBackground(new java.awt.Color(107, 103, 0));
        overflow4.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        overflow4.setForeground(new java.awt.Color(0, 0, 0));
        overflow4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        overflow4.setText("Overflow 4");
        overflow4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        overflow4.setFocusable(false);
        overflow4.setPreferredSize(new java.awt.Dimension(100, 30));
        overflow4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overflow4ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(overflow4);

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        jLabel2.setText("Dearator 1");

        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        waterInV1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        waterInV1.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        waterInV1.setFrameVisible(false);
        waterInV1.setLcdBackgroundVisible(false);
        waterInV1.setLcdVisible(false);
        waterInV1.setLedVisible(false);
        waterInV1.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        waterInV1.setPointerShadowVisible(false);
        waterInV1.setPreferredSize(new java.awt.Dimension(100, 100));
        waterInV1.setTitle("Position");
        waterInV1.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        waterInV1.setTitleAndUnitFontEnabled(true);
        waterInV1.setUnitString("%");

        buttonGroup1.add(waterInVOpen1);
        waterInVOpen1.setText("Open");
        waterInVOpen1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                waterInVOpen1ItemStateChanged(evt);
            }
        });
        waterInVOpen1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterInVOpen1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(waterInVStop1);
        waterInVStop1.setSelected(true);
        waterInVStop1.setText("Stop");
        waterInVStop1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                waterInVStop1ItemStateChanged(evt);
            }
        });
        waterInVStop1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterInVStop1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(waterInVClose1);
        waterInVClose1.setText("Close");
        waterInVClose1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                waterInVClose1ItemStateChanged(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel1.setText("Water Inlet Valve");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(waterInVOpen1)
                            .addComponent(waterInVStop1)
                            .addComponent(waterInVClose1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(waterInV1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(7, 7, 7)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(waterInVOpen1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(waterInVStop1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(waterInVClose1))
                    .addComponent(waterInV1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel5.setText("Auto-Control");

        jPanel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup4.add(autoWaterLevel1On);
        autoWaterLevel1On.setText("On");
        autoWaterLevel1On.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoWaterLevel1OnItemStateChanged(evt);
            }
        });
        autoWaterLevel1On.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoWaterLevel1OnActionPerformed(evt);
            }
        });

        buttonGroup4.add(autoWaterLevel1Off);
        autoWaterLevel1Off.setSelected(true);
        autoWaterLevel1Off.setText("Off");
        autoWaterLevel1Off.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoWaterLevel1OffItemStateChanged(evt);
            }
        });
        autoWaterLevel1Off.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoWaterLevel1OffActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel6.setText("Water-Level");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(autoWaterLevel1On)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(autoWaterLevel1Off)
                .addGap(13, 13, 13))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(autoWaterLevel1On)
                    .addComponent(autoWaterLevel1Off))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup5.add(autoSteamPressure1On);
        autoSteamPressure1On.setText("On");
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

        buttonGroup5.add(autoSteamPressure1Off);
        autoSteamPressure1Off.setSelected(true);
        autoSteamPressure1Off.setText("Off");
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

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel7.setText("Steam Pressure");

        setpoint1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        setpoint1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        setpoint1.setLcdDecimals(2);
        setpoint1.setLcdUnitString("");
        setpoint1.setLcdUnitStringVisible(false);
        setpoint1.setLcdValue(1.0);

        jLabel8.setText("Setpoint");

        jLabel9.setText("Mpa");

        precisionDecrement1.setText("-");
        precisionDecrement1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionDecrement1.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionDecrement1.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionDecrement1.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionDecrement1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionDecrement1ActionPerformed(evt);
            }
        });

        precisionIncrement1.setText("+");
        precisionIncrement1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionIncrement1.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionIncrement1.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionIncrement1.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionIncrement1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionIncrement1ActionPerformed(evt);
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
                        .addComponent(jLabel8)
                        .addGap(7, 7, 7)
                        .addComponent(setpoint1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(autoSteamPressure1On)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(autoSteamPressure1Off)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(precisionIncrement1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(precisionDecrement1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel7)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(setpoint1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(precisionDecrement1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precisionIncrement1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(autoSteamPressure1Off)
                    .addComponent(autoSteamPressure1On))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(117, 117, 117))
        );

        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        steamInV1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        steamInV1.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        steamInV1.setFrameVisible(false);
        steamInV1.setLcdBackgroundVisible(false);
        steamInV1.setLcdVisible(false);
        steamInV1.setLedVisible(false);
        steamInV1.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        steamInV1.setPointerShadowVisible(false);
        steamInV1.setPreferredSize(new java.awt.Dimension(100, 100));
        steamInV1.setTitle("Position");
        steamInV1.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        steamInV1.setTitleAndUnitFontEnabled(true);
        steamInV1.setUnitString("%");

        buttonGroup2.add(steamInVOpen1);
        steamInVOpen1.setText("Open");
        steamInVOpen1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamInVOpen1ItemStateChanged(evt);
            }
        });
        steamInVOpen1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                steamInVOpen1ActionPerformed(evt);
            }
        });

        buttonGroup2.add(steamInVStop1);
        steamInVStop1.setSelected(true);
        steamInVStop1.setText("Stop");
        steamInVStop1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamInVStop1ItemStateChanged(evt);
            }
        });
        steamInVStop1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                steamInVStop1ActionPerformed(evt);
            }
        });

        buttonGroup2.add(steamInVClose1);
        steamInVClose1.setText("Close");
        steamInVClose1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamInVClose1ItemStateChanged(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel3.setText("Steam Inlet Valve");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(steamInVOpen1)
                            .addComponent(steamInVStop1)
                            .addComponent(steamInVClose1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamInV1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(7, 7, 7)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(steamInVOpen1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamInVStop1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamInVClose1))
                    .addComponent(steamInV1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        steamOutV1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        steamOutV1.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        steamOutV1.setFrameVisible(false);
        steamOutV1.setLcdBackgroundVisible(false);
        steamOutV1.setLcdVisible(false);
        steamOutV1.setLedVisible(false);
        steamOutV1.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        steamOutV1.setPointerShadowVisible(false);
        steamOutV1.setPreferredSize(new java.awt.Dimension(100, 100));
        steamOutV1.setTitle("Position");
        steamOutV1.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        steamOutV1.setTitleAndUnitFontEnabled(true);
        steamOutV1.setUnitString("%");

        buttonGroup3.add(steamOutVOpen1);
        steamOutVOpen1.setText("Open");
        steamOutVOpen1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamOutVOpen1ItemStateChanged(evt);
            }
        });
        steamOutVOpen1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                steamOutVOpen1ActionPerformed(evt);
            }
        });

        buttonGroup3.add(steamOutVStop1);
        steamOutVStop1.setSelected(true);
        steamOutVStop1.setText("Stop");
        steamOutVStop1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamOutVStop1ItemStateChanged(evt);
            }
        });
        steamOutVStop1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                steamOutVStop1ActionPerformed(evt);
            }
        });

        buttonGroup3.add(steamOutVClose1);
        steamOutVClose1.setText("Close");
        steamOutVClose1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamOutVClose1ItemStateChanged(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel4.setText("Steam Outlet Valve");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(steamOutVOpen1)
                            .addComponent(steamOutVStop1)
                            .addComponent(steamOutVClose1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamOutV1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(7, 7, 7)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(steamOutVOpen1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamOutVStop1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamOutVClose1))
                    .addComponent(steamOutV1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        temp1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        temp1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        temp1.setLcdUnitString("°C");
        temp1.setLcdValue(20.0);

        steamIn1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        steamIn1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        steamIn1.setLcdUnitString("kg/s");
        steamIn1.setLcdValue(20.0);

        steamOut1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        steamOut1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        steamOut1.setLcdUnitString("kg/s");
        steamOut1.setLcdValue(20.0);

        over1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        over1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        over1.setLcdUnitString("kg/s");

        jLabel49.setText("Temperature");

        jLabel50.setText("Steam Inflow");

        jLabel51.setText("Steam Outflow");

        jLabel52.setText("Overflow");

        isolate1.setText("Isolate");
        isolate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isolate1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel50, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel49, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel51, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel52, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(temp1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(steamIn1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(steamOut1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(over1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(isolate1)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(temp1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamIn1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamOut1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(over1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel52, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(isolate1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel10.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        jLabel10.setText("Dearator 4");

        jPanel10.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        waterInV4.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        waterInV4.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        waterInV4.setFrameVisible(false);
        waterInV4.setLcdBackgroundVisible(false);
        waterInV4.setLcdVisible(false);
        waterInV4.setLedVisible(false);
        waterInV4.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        waterInV4.setPointerShadowVisible(false);
        waterInV4.setPreferredSize(new java.awt.Dimension(100, 100));
        waterInV4.setTitle("Position");
        waterInV4.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        waterInV4.setTitleAndUnitFontEnabled(true);
        waterInV4.setUnitString("%");

        buttonGroup16.add(waterInVOpen4);
        waterInVOpen4.setText("Open");
        waterInVOpen4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                waterInVOpen4ItemStateChanged(evt);
            }
        });
        waterInVOpen4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterInVOpen4ActionPerformed(evt);
            }
        });

        buttonGroup16.add(waterInVStop4);
        waterInVStop4.setSelected(true);
        waterInVStop4.setText("Stop");
        waterInVStop4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                waterInVStop4ItemStateChanged(evt);
            }
        });
        waterInVStop4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterInVStop4ActionPerformed(evt);
            }
        });

        buttonGroup16.add(waterInVClose4);
        waterInVClose4.setText("Close");
        waterInVClose4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                waterInVClose4ItemStateChanged(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel11.setText("Water Inlet Valve");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(waterInVOpen4)
                            .addComponent(waterInVStop4)
                            .addComponent(waterInVClose4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(waterInV4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addGap(7, 7, 7)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(waterInVOpen4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(waterInVStop4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(waterInVClose4))
                    .addComponent(waterInV4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel11.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel12.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel12.setText("Auto-Control");

        jPanel12.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup19.add(autoWaterLevel4On);
        autoWaterLevel4On.setText("On");
        autoWaterLevel4On.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoWaterLevel4OnItemStateChanged(evt);
            }
        });
        autoWaterLevel4On.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoWaterLevel4OnActionPerformed(evt);
            }
        });

        buttonGroup19.add(autoWaterLevel4Off);
        autoWaterLevel4Off.setSelected(true);
        autoWaterLevel4Off.setText("Off");
        autoWaterLevel4Off.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoWaterLevel4OffItemStateChanged(evt);
            }
        });
        autoWaterLevel4Off.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoWaterLevel4OffActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel13.setText("Water-Level");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(autoWaterLevel4On)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(autoWaterLevel4Off)
                .addGap(13, 13, 13))
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(autoWaterLevel4On)
                    .addComponent(autoWaterLevel4Off))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup20.add(autoSteamPressure4On);
        autoSteamPressure4On.setText("On");
        autoSteamPressure4On.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoSteamPressure4OnItemStateChanged(evt);
            }
        });
        autoSteamPressure4On.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoSteamPressure4OnActionPerformed(evt);
            }
        });

        buttonGroup20.add(autoSteamPressure4Off);
        autoSteamPressure4Off.setSelected(true);
        autoSteamPressure4Off.setText("Off");
        autoSteamPressure4Off.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoSteamPressure4OffItemStateChanged(evt);
            }
        });
        autoSteamPressure4Off.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoSteamPressure4OffActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel14.setText("Steam Pressure");

        setpoint4.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        setpoint4.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        setpoint4.setLcdDecimals(2);
        setpoint4.setLcdUnitString("");
        setpoint4.setLcdUnitStringVisible(false);
        setpoint4.setLcdValue(1.0);

        jLabel15.setText("Setpoint");

        jLabel16.setText("Mpa");

        precisionDecrement4.setText("-");
        precisionDecrement4.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionDecrement4.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionDecrement4.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionDecrement4.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionDecrement4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionDecrement4ActionPerformed(evt);
            }
        });

        precisionIncrement4.setText("+");
        precisionIncrement4.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionIncrement4.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionIncrement4.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionIncrement4.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionIncrement4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionIncrement4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel15)
                        .addGap(7, 7, 7)
                        .addComponent(setpoint4, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(autoSteamPressure4On)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(autoSteamPressure4Off)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(precisionIncrement4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(precisionDecrement4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel14)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(setpoint4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(precisionDecrement4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precisionIncrement4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(autoSteamPressure4Off)
                    .addComponent(autoSteamPressure4On))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(117, 117, 117))
        );

        jPanel14.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        steamInV4.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        steamInV4.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        steamInV4.setFrameVisible(false);
        steamInV4.setLcdBackgroundVisible(false);
        steamInV4.setLcdVisible(false);
        steamInV4.setLedVisible(false);
        steamInV4.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        steamInV4.setPointerShadowVisible(false);
        steamInV4.setPreferredSize(new java.awt.Dimension(100, 100));
        steamInV4.setTitle("Position");
        steamInV4.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        steamInV4.setTitleAndUnitFontEnabled(true);
        steamInV4.setUnitString("%");

        buttonGroup17.add(steamInVopen4);
        steamInVopen4.setText("Open");
        steamInVopen4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamInVopen4ItemStateChanged(evt);
            }
        });
        steamInVopen4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                steamInVopen4ActionPerformed(evt);
            }
        });

        buttonGroup17.add(steamInVStop4);
        steamInVStop4.setSelected(true);
        steamInVStop4.setText("Stop");
        steamInVStop4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamInVStop4ItemStateChanged(evt);
            }
        });
        steamInVStop4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                steamInVStop4ActionPerformed(evt);
            }
        });

        buttonGroup17.add(steamInVClose4);
        steamInVClose4.setText("Close");
        steamInVClose4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamInVClose4ItemStateChanged(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel17.setText("Steam Inlet Valve");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(steamInVopen4)
                            .addComponent(steamInVStop4)
                            .addComponent(steamInVClose4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamInV4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel17)
                .addGap(7, 7, 7)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(steamInVopen4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamInVStop4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamInVClose4))
                    .addComponent(steamInV4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel15.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        steamOutV4.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        steamOutV4.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        steamOutV4.setFrameVisible(false);
        steamOutV4.setLcdBackgroundVisible(false);
        steamOutV4.setLcdVisible(false);
        steamOutV4.setLedVisible(false);
        steamOutV4.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        steamOutV4.setPointerShadowVisible(false);
        steamOutV4.setPreferredSize(new java.awt.Dimension(100, 100));
        steamOutV4.setTitle("Position");
        steamOutV4.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        steamOutV4.setTitleAndUnitFontEnabled(true);
        steamOutV4.setUnitString("%");

        buttonGroup18.add(steamOutVOpen4);
        steamOutVOpen4.setText("Open");
        steamOutVOpen4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamOutVOpen4ItemStateChanged(evt);
            }
        });
        steamOutVOpen4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                steamOutVOpen4ActionPerformed(evt);
            }
        });

        buttonGroup18.add(steamOutVStop4);
        steamOutVStop4.setSelected(true);
        steamOutVStop4.setText("Stop");
        steamOutVStop4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamOutVStop4ItemStateChanged(evt);
            }
        });
        steamOutVStop4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                steamOutVStop4ActionPerformed(evt);
            }
        });

        buttonGroup18.add(steamOutVClose4);
        steamOutVClose4.setText("Close");
        steamOutVClose4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamOutVClose4ItemStateChanged(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel18.setText("Steam Outlet Valve");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(steamOutVOpen4)
                            .addComponent(steamOutVStop4)
                            .addComponent(steamOutVClose4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamOutV4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel18)
                .addGap(7, 7, 7)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(steamOutVOpen4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamOutVStop4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamOutVClose4))
                    .addComponent(steamOutV4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        temp4.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        temp4.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        temp4.setLcdUnitString("°C");
        temp4.setLcdValue(20.0);

        steamIn4.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        steamIn4.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        steamIn4.setLcdUnitString("kg/s");
        steamIn4.setLcdValue(20.0);

        jLabel37.setText("Temperature");

        jLabel38.setText("Steam Inflow");

        steamOut4.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        steamOut4.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        steamOut4.setLcdUnitString("kg/s");
        steamOut4.setLcdValue(20.0);

        over4.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        over4.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        over4.setLcdUnitString("kg/s");

        jLabel53.setText("Steam Outflow");

        jLabel54.setText("Overflow");

        isolate4.setSelected(true);
        isolate4.setText("Isolate");
        isolate4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isolate4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(temp4, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel38)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(steamIn4, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel53)
                                    .addComponent(jLabel54))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(steamOut4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(over4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(isolate4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(temp4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(steamIn4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(steamOut4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(over4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(isolate4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel16.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel19.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        jLabel19.setText("Dearator 2");

        jPanel17.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        waterInV2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        waterInV2.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        waterInV2.setFrameVisible(false);
        waterInV2.setLcdBackgroundVisible(false);
        waterInV2.setLcdVisible(false);
        waterInV2.setLedVisible(false);
        waterInV2.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        waterInV2.setPointerShadowVisible(false);
        waterInV2.setPreferredSize(new java.awt.Dimension(100, 100));
        waterInV2.setTitle("Position");
        waterInV2.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        waterInV2.setTitleAndUnitFontEnabled(true);
        waterInV2.setUnitString("%");

        buttonGroup6.add(waterInVOpen2);
        waterInVOpen2.setText("Open");
        waterInVOpen2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                waterInVOpen2ItemStateChanged(evt);
            }
        });
        waterInVOpen2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterInVOpen2ActionPerformed(evt);
            }
        });

        buttonGroup6.add(waterInVStop2);
        waterInVStop2.setSelected(true);
        waterInVStop2.setText("Stop");
        waterInVStop2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                waterInVStop2ItemStateChanged(evt);
            }
        });
        waterInVStop2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterInVStop2ActionPerformed(evt);
            }
        });

        buttonGroup6.add(waterInVClose2);
        waterInVClose2.setText("Close");
        waterInVClose2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                waterInVClose2ItemStateChanged(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel20.setText("Water Inlet Valve");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(waterInVOpen2)
                            .addComponent(waterInVStop2)
                            .addComponent(waterInVClose2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(waterInV2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel20)
                .addGap(7, 7, 7)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(waterInVOpen2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(waterInVStop2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(waterInVClose2))
                    .addComponent(waterInV2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel18.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel21.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel21.setText("Auto-Control");

        jPanel19.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup9.add(autoWaterLevel2On);
        autoWaterLevel2On.setText("On");
        autoWaterLevel2On.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoWaterLevel2OnItemStateChanged(evt);
            }
        });
        autoWaterLevel2On.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoWaterLevel2OnActionPerformed(evt);
            }
        });

        buttonGroup9.add(autoWaterLevel2Off);
        autoWaterLevel2Off.setSelected(true);
        autoWaterLevel2Off.setText("Off");
        autoWaterLevel2Off.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoWaterLevel2OffItemStateChanged(evt);
            }
        });
        autoWaterLevel2Off.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoWaterLevel2OffActionPerformed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel22.setText("Water-Level");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(autoWaterLevel2On)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(autoWaterLevel2Off)
                .addGap(13, 13, 13))
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(autoWaterLevel2On)
                    .addComponent(autoWaterLevel2Off))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel20.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup10.add(autoSteamPressure2On);
        autoSteamPressure2On.setText("On");
        autoSteamPressure2On.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoSteamPressure2OnItemStateChanged(evt);
            }
        });
        autoSteamPressure2On.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoSteamPressure2OnActionPerformed(evt);
            }
        });

        buttonGroup10.add(autoSteamPressure2Off);
        autoSteamPressure2Off.setSelected(true);
        autoSteamPressure2Off.setText("Off");
        autoSteamPressure2Off.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoSteamPressure2OffItemStateChanged(evt);
            }
        });
        autoSteamPressure2Off.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoSteamPressure2OffActionPerformed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel23.setText("Steam Pressure");

        setpoint2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        setpoint2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        setpoint2.setLcdDecimals(2);
        setpoint2.setLcdUnitString("");
        setpoint2.setLcdUnitStringVisible(false);
        setpoint2.setLcdValue(1.0);

        jLabel24.setText("Setpoint");

        jLabel25.setText("Mpa");

        precisionDecrement2.setText("-");
        precisionDecrement2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionDecrement2.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionDecrement2.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionDecrement2.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionDecrement2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionDecrement2ActionPerformed(evt);
            }
        });

        precisionIncrement2.setText("+");
        precisionIncrement2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionIncrement2.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionIncrement2.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionIncrement2.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionIncrement2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionIncrement2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel24)
                        .addGap(7, 7, 7)
                        .addComponent(setpoint2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel25))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(autoSteamPressure2On)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(autoSteamPressure2Off)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(precisionIncrement2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(precisionDecrement2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel23)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(setpoint2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(precisionDecrement2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precisionIncrement2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(autoSteamPressure2Off)
                    .addComponent(autoSteamPressure2On))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(117, 117, 117))
        );

        jPanel21.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        steamInV2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        steamInV2.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        steamInV2.setFrameVisible(false);
        steamInV2.setLcdBackgroundVisible(false);
        steamInV2.setLcdVisible(false);
        steamInV2.setLedVisible(false);
        steamInV2.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        steamInV2.setPointerShadowVisible(false);
        steamInV2.setPreferredSize(new java.awt.Dimension(100, 100));
        steamInV2.setTitle("Position");
        steamInV2.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        steamInV2.setTitleAndUnitFontEnabled(true);
        steamInV2.setUnitString("%");

        buttonGroup7.add(steamInVopen2);
        steamInVopen2.setText("Open");
        steamInVopen2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamInVopen2ItemStateChanged(evt);
            }
        });
        steamInVopen2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                steamInVopen2ActionPerformed(evt);
            }
        });

        buttonGroup7.add(steamInVStop2);
        steamInVStop2.setSelected(true);
        steamInVStop2.setText("Stop");
        steamInVStop2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamInVStop2ItemStateChanged(evt);
            }
        });
        steamInVStop2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                steamInVStop2ActionPerformed(evt);
            }
        });

        buttonGroup7.add(steamInVClose2);
        steamInVClose2.setText("Close");
        steamInVClose2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamInVClose2ItemStateChanged(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel26.setText("Steam Inlet Valve");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(steamInVopen2)
                            .addComponent(steamInVStop2)
                            .addComponent(steamInVClose2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamInV2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel26)
                .addGap(7, 7, 7)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(steamInVopen2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamInVStop2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamInVClose2))
                    .addComponent(steamInV2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel22.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        steamOutV2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        steamOutV2.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        steamOutV2.setFrameVisible(false);
        steamOutV2.setLcdBackgroundVisible(false);
        steamOutV2.setLcdVisible(false);
        steamOutV2.setLedVisible(false);
        steamOutV2.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        steamOutV2.setPointerShadowVisible(false);
        steamOutV2.setPreferredSize(new java.awt.Dimension(100, 100));
        steamOutV2.setTitle("Position");
        steamOutV2.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        steamOutV2.setTitleAndUnitFontEnabled(true);
        steamOutV2.setUnitString("%");

        buttonGroup8.add(steamOutVOpen2);
        steamOutVOpen2.setText("Open");
        steamOutVOpen2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamOutVOpen2ItemStateChanged(evt);
            }
        });
        steamOutVOpen2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                steamOutVOpen2ActionPerformed(evt);
            }
        });

        buttonGroup8.add(steamOutVStop2);
        steamOutVStop2.setSelected(true);
        steamOutVStop2.setText("Stop");
        steamOutVStop2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamOutVStop2ItemStateChanged(evt);
            }
        });
        steamOutVStop2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                steamOutVStop2ActionPerformed(evt);
            }
        });

        buttonGroup8.add(steamOutVClose2);
        steamOutVClose2.setText("Close");
        steamOutVClose2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamOutVClose2ItemStateChanged(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel27.setText("Steam Outlet Valve");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(steamOutVOpen2)
                            .addComponent(steamOutVStop2)
                            .addComponent(steamOutVClose2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamOutV2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel27)
                .addGap(7, 7, 7)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addComponent(steamOutVOpen2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamOutVStop2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamOutVClose2))
                    .addComponent(steamOutV2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        temp2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        temp2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        temp2.setLcdUnitString("°C");
        temp2.setLcdValue(20.0);

        steamIn2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        steamIn2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        steamIn2.setLcdUnitString("kg/s");
        steamIn2.setLcdValue(20.0);

        steamOut2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        steamOut2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        steamOut2.setLcdUnitString("kg/s");
        steamOut2.setLcdValue(20.0);

        over2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        over2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        over2.setLcdUnitString("kg/s");

        jLabel45.setText("Temperature");

        jLabel46.setText("Steam Outflow");

        jLabel47.setText("Steam Inflow");

        jLabel48.setText("Overflow");

        isolate2.setSelected(true);
        isolate2.setText("Isolate");
        isolate2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isolate2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                                .addComponent(jLabel47)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(steamIn2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                                .addComponent(jLabel45)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(temp2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                                .addComponent(jLabel46)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(steamOut2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                                .addComponent(jLabel48)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(over2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(isolate2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(temp2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(steamIn2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addComponent(steamOut2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(over2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(isolate2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel23.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel28.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        jLabel28.setText("Dearator 3");

        jPanel24.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        waterInV3.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        waterInV3.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        waterInV3.setFrameVisible(false);
        waterInV3.setLcdBackgroundVisible(false);
        waterInV3.setLcdVisible(false);
        waterInV3.setLedVisible(false);
        waterInV3.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        waterInV3.setPointerShadowVisible(false);
        waterInV3.setPreferredSize(new java.awt.Dimension(100, 100));
        waterInV3.setTitle("Position");
        waterInV3.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        waterInV3.setTitleAndUnitFontEnabled(true);
        waterInV3.setUnitString("%");

        buttonGroup11.add(waterInVOpen3);
        waterInVOpen3.setText("Open");
        waterInVOpen3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                waterInVOpen3ItemStateChanged(evt);
            }
        });
        waterInVOpen3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterInVOpen3ActionPerformed(evt);
            }
        });

        buttonGroup11.add(waterInVStop3);
        waterInVStop3.setSelected(true);
        waterInVStop3.setText("Stop");
        waterInVStop3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                waterInVStop3ItemStateChanged(evt);
            }
        });
        waterInVStop3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterInVStop3ActionPerformed(evt);
            }
        });

        buttonGroup11.add(waterInVClose3);
        waterInVClose3.setText("Close");
        waterInVClose3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                waterInVClose3ItemStateChanged(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel29.setText("Water Inlet Valve");

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29)
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(waterInVOpen3)
                            .addComponent(waterInVStop3)
                            .addComponent(waterInVClose3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(waterInV3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel29)
                .addGap(7, 7, 7)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addComponent(waterInVOpen3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(waterInVStop3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(waterInVClose3))
                    .addComponent(waterInV3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel25.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel30.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel30.setText("Auto-Control");

        jPanel26.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup14.add(autoWaterLevel3On);
        autoWaterLevel3On.setText("On");
        autoWaterLevel3On.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoWaterLevel3OnItemStateChanged(evt);
            }
        });
        autoWaterLevel3On.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoWaterLevel3OnActionPerformed(evt);
            }
        });

        buttonGroup14.add(autoWaterLevel3Off);
        autoWaterLevel3Off.setSelected(true);
        autoWaterLevel3Off.setText("Off");
        autoWaterLevel3Off.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoWaterLevel3OffItemStateChanged(evt);
            }
        });
        autoWaterLevel3Off.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoWaterLevel3OffActionPerformed(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel31.setText("Water-Level");

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(autoWaterLevel3On)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(autoWaterLevel3Off)
                .addGap(13, 13, 13))
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel31)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(autoWaterLevel3On)
                    .addComponent(autoWaterLevel3Off))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel27.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup15.add(autoSteamPressure3On);
        autoSteamPressure3On.setText("On");
        autoSteamPressure3On.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoSteamPressure3OnItemStateChanged(evt);
            }
        });
        autoSteamPressure3On.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoSteamPressure3OnActionPerformed(evt);
            }
        });

        buttonGroup15.add(autoSteamPressure3Off);
        autoSteamPressure3Off.setSelected(true);
        autoSteamPressure3Off.setText("Off");
        autoSteamPressure3Off.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoSteamPressure3OffItemStateChanged(evt);
            }
        });
        autoSteamPressure3Off.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoSteamPressure3OffActionPerformed(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel32.setText("Steam Pressure");

        setpoint3.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        setpoint3.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        setpoint3.setLcdDecimals(2);
        setpoint3.setLcdUnitString("");
        setpoint3.setLcdUnitStringVisible(false);
        setpoint3.setLcdValue(1.0);

        jLabel33.setText("Setpoint");

        jLabel34.setText("Mpa");

        precisionDecrement3.setText("-");
        precisionDecrement3.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionDecrement3.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionDecrement3.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionDecrement3.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionDecrement3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionDecrement3ActionPerformed(evt);
            }
        });

        precisionIncrement3.setText("+");
        precisionIncrement3.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionIncrement3.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionIncrement3.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionIncrement3.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionIncrement3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionIncrement3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel33)
                        .addGap(7, 7, 7)
                        .addComponent(setpoint3, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel34))
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(autoSteamPressure3On)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(autoSteamPressure3Off)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(precisionIncrement3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(precisionDecrement3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel32)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(setpoint3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(precisionDecrement3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precisionIncrement3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(autoSteamPressure3Off)
                    .addComponent(autoSteamPressure3On))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(117, 117, 117))
        );

        jPanel28.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        steamInV3.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        steamInV3.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        steamInV3.setFrameVisible(false);
        steamInV3.setLcdBackgroundVisible(false);
        steamInV3.setLcdVisible(false);
        steamInV3.setLedVisible(false);
        steamInV3.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        steamInV3.setPointerShadowVisible(false);
        steamInV3.setPreferredSize(new java.awt.Dimension(100, 100));
        steamInV3.setTitle("Position");
        steamInV3.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        steamInV3.setTitleAndUnitFontEnabled(true);
        steamInV3.setUnitString("%");

        buttonGroup12.add(steamInVopen3);
        steamInVopen3.setText("Open");
        steamInVopen3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamInVopen3ItemStateChanged(evt);
            }
        });
        steamInVopen3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                steamInVopen3ActionPerformed(evt);
            }
        });

        buttonGroup12.add(steamInVStop3);
        steamInVStop3.setSelected(true);
        steamInVStop3.setText("Stop");
        steamInVStop3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamInVStop3ItemStateChanged(evt);
            }
        });
        steamInVStop3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                steamInVStop3ActionPerformed(evt);
            }
        });

        buttonGroup12.add(steamInVClose3);
        steamInVClose3.setText("Close");
        steamInVClose3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamInVClose3ItemStateChanged(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel35.setText("Steam Inlet Valve");

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel35)
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(steamInVopen3)
                            .addComponent(steamInVStop3)
                            .addComponent(steamInVClose3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamInV3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel35)
                .addGap(7, 7, 7)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addComponent(steamInVopen3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamInVStop3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamInVClose3))
                    .addComponent(steamInV3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel29.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        steamOutV3.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        steamOutV3.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        steamOutV3.setFrameVisible(false);
        steamOutV3.setLcdBackgroundVisible(false);
        steamOutV3.setLcdVisible(false);
        steamOutV3.setLedVisible(false);
        steamOutV3.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        steamOutV3.setPointerShadowVisible(false);
        steamOutV3.setPreferredSize(new java.awt.Dimension(100, 100));
        steamOutV3.setTitle("Position");
        steamOutV3.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        steamOutV3.setTitleAndUnitFontEnabled(true);
        steamOutV3.setUnitString("%");

        buttonGroup13.add(steamOutVOpen3);
        steamOutVOpen3.setText("Open");
        steamOutVOpen3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamOutVOpen3ItemStateChanged(evt);
            }
        });
        steamOutVOpen3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                steamOutVOpen3ActionPerformed(evt);
            }
        });

        buttonGroup13.add(steamOutVStop3);
        steamOutVStop3.setSelected(true);
        steamOutVStop3.setText("Stop");
        steamOutVStop3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamOutVStop3ItemStateChanged(evt);
            }
        });
        steamOutVStop3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                steamOutVStop3ActionPerformed(evt);
            }
        });

        buttonGroup13.add(steamOutVClose3);
        steamOutVClose3.setText("Close");
        steamOutVClose3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                steamOutVClose3ItemStateChanged(evt);
            }
        });

        jLabel36.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel36.setText("Steam Outlet Valve");

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel36)
                    .addGroup(jPanel29Layout.createSequentialGroup()
                        .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(steamOutVOpen3)
                            .addComponent(steamOutVStop3)
                            .addComponent(steamOutVClose3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamOutV3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel36)
                .addGap(7, 7, 7)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel29Layout.createSequentialGroup()
                        .addComponent(steamOutVOpen3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamOutVStop3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(steamOutVClose3))
                    .addComponent(steamOutV3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        temp3.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        temp3.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        temp3.setLcdUnitString("°C");
        temp3.setLcdValue(20.0);

        steamIn3.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        steamIn3.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        steamIn3.setLcdUnitString("kg/s");
        steamIn3.setLcdValue(20.0);

        steamOut3.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        steamOut3.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        steamOut3.setLcdUnitString("kg/s");
        steamOut3.setLcdValue(20.0);

        over3.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        over3.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        over3.setLcdUnitString("kg/s");

        jLabel41.setText("Temperature");

        jLabel42.setText("Steam Outflow");

        jLabel43.setText("Steam Inflow");

        jLabel44.setText("Overflow");

        isolate3.setSelected(true);
        isolate3.setText("Isolate");
        isolate3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isolate3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28)
                            .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel44, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel41, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel42, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel43, javax.swing.GroupLayout.Alignment.TRAILING)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(temp3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(steamIn3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(steamOut3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(over3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(isolate3))
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(temp3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addComponent(steamIn3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                                .addComponent(steamOut3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(over3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(isolate3))
                .addGap(13, 13, 13))
        );

        level1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        level1.setFrameVisible(false);
        level1.setLcdVisible(false);
        level1.setLedVisible(false);
        level1.setMinValue(-100.0);
        level1.setTitle("Dearator Level 1");
        level1.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        level1.setTrackStart(25.0);
        level1.setTrackStartColor(new java.awt.Color(255, 0, 0));
        level1.setTrackStopColor(new java.awt.Color(255, 0, 0));
        level1.setTrackVisible(true);
        level1.setUnitString("%");
        level1.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        waterIn1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        waterIn1.setFrameVisible(false);
        waterIn1.setLcdVisible(false);
        waterIn1.setLedVisible(false);
        waterIn1.setMaxValue(1000.0);
        waterIn1.setThreshold(800.0);
        waterIn1.setTitle("Water Inflow 1");
        waterIn1.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        waterIn1.setTrackStart(800.0);
        waterIn1.setTrackStartColor(new java.awt.Color(255, 0, 0));
        waterIn1.setTrackStop(1000.0);
        waterIn1.setUnitString("kg/s");
        waterIn1.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        waterOut1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        waterOut1.setFrameVisible(false);
        waterOut1.setLcdVisible(false);
        waterOut1.setLedVisible(false);
        waterOut1.setMaxValue(1000.0);
        waterOut1.setThreshold(800.0);
        waterOut1.setTitle("Water Outflow 1");
        waterOut1.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        waterOut1.setTrackStart(800.0);
        waterOut1.setTrackStartColor(new java.awt.Color(255, 0, 0));
        waterOut1.setTrackStop(1000.0);
        waterOut1.setUnitString("kg/s");
        waterOut1.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        level2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        level2.setFrameVisible(false);
        level2.setLcdVisible(false);
        level2.setLedVisible(false);
        level2.setMinValue(-100.0);
        level2.setTitle("Water Level 2");
        level2.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        level2.setTrackStart(25.0);
        level2.setTrackStartColor(new java.awt.Color(255, 0, 0));
        level2.setTrackStopColor(new java.awt.Color(255, 0, 0));
        level2.setTrackVisible(true);
        level2.setUnitString("%");
        level2.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        waterIn2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        waterIn2.setFrameVisible(false);
        waterIn2.setLcdVisible(false);
        waterIn2.setLedVisible(false);
        waterIn2.setMaxValue(1000.0);
        waterIn2.setThreshold(800.0);
        waterIn2.setTitle("Water Inflow 2");
        waterIn2.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        waterIn2.setTrackStart(800.0);
        waterIn2.setTrackStartColor(new java.awt.Color(255, 0, 0));
        waterIn2.setTrackStop(1000.0);
        waterIn2.setUnitString("kg/s");
        waterIn2.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        waterOut2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        waterOut2.setFrameVisible(false);
        waterOut2.setLcdVisible(false);
        waterOut2.setLedVisible(false);
        waterOut2.setMaxValue(1000.0);
        waterOut2.setThreshold(800.0);
        waterOut2.setTitle("Water Outflow 2");
        waterOut2.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        waterOut2.setTrackStart(800.0);
        waterOut2.setTrackStartColor(new java.awt.Color(255, 0, 0));
        waterOut2.setTrackStop(1000.0);
        waterOut2.setUnitString("kg/s");
        waterOut2.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        level3.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        level3.setFrameVisible(false);
        level3.setLcdVisible(false);
        level3.setLedVisible(false);
        level3.setMinValue(-100.0);
        level3.setTitle("Water Level 3");
        level3.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        level3.setTrackStart(25.0);
        level3.setTrackStartColor(new java.awt.Color(255, 0, 0));
        level3.setTrackStopColor(new java.awt.Color(255, 0, 0));
        level3.setTrackVisible(true);
        level3.setUnitString("%");
        level3.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        waterIn3.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        waterIn3.setFrameVisible(false);
        waterIn3.setLcdVisible(false);
        waterIn3.setLedVisible(false);
        waterIn3.setMaxValue(1000.0);
        waterIn3.setThreshold(800.0);
        waterIn3.setTitle("Water Inflow 3");
        waterIn3.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        waterIn3.setTrackStart(800.0);
        waterIn3.setTrackStartColor(new java.awt.Color(255, 0, 0));
        waterIn3.setTrackStop(1000.0);
        waterIn3.setUnitString("kg/s");
        waterIn3.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        waterOut3.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        waterOut3.setFrameVisible(false);
        waterOut3.setLcdVisible(false);
        waterOut3.setLedVisible(false);
        waterOut3.setMaxValue(1000.0);
        waterOut3.setThreshold(800.0);
        waterOut3.setTitle("Water Outflow 3");
        waterOut3.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        waterOut3.setTrackStart(800.0);
        waterOut3.setTrackStartColor(new java.awt.Color(255, 0, 0));
        waterOut3.setTrackStop(1000.0);
        waterOut3.setUnitString("kg/s");
        waterOut3.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        level4.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        level4.setFrameVisible(false);
        level4.setLcdVisible(false);
        level4.setLedVisible(false);
        level4.setMinValue(-100.0);
        level4.setTitle("Water Level 4");
        level4.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        level4.setTrackStart(25.0);
        level4.setTrackStartColor(new java.awt.Color(255, 0, 0));
        level4.setTrackStopColor(new java.awt.Color(255, 0, 0));
        level4.setTrackVisible(true);
        level4.setUnitString("%");
        level4.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        waterIn4.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        waterIn4.setFrameVisible(false);
        waterIn4.setLcdVisible(false);
        waterIn4.setLedVisible(false);
        waterIn4.setMaxValue(1000.0);
        waterIn4.setThreshold(800.0);
        waterIn4.setTitle("Water Inflow 4");
        waterIn4.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        waterIn4.setTrackStart(800.0);
        waterIn4.setTrackStartColor(new java.awt.Color(255, 0, 0));
        waterIn4.setTrackStop(1000.0);
        waterIn4.setUnitString("kg/s");
        waterIn4.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        waterOut4.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        waterOut4.setFrameVisible(false);
        waterOut4.setLcdVisible(false);
        waterOut4.setLedVisible(false);
        waterOut4.setMaxValue(1000.0);
        waterOut4.setThreshold(800.0);
        waterOut4.setTitle("Water Outflow 4");
        waterOut4.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        waterOut4.setTrackStart(800.0);
        waterOut4.setTrackStartColor(new java.awt.Color(255, 0, 0));
        waterOut4.setTrackStop(1000.0);
        waterOut4.setUnitString("kg/s");
        waterOut4.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        press1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        press1.setFrameVisible(false);
        press1.setLcdVisible(false);
        press1.setLedVisible(false);
        press1.setMaxValue(2.0);
        press1.setTitle("Pressure 1");
        press1.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        press1.setTrackStart(1.475);
        press1.setTrackStartColor(new java.awt.Color(255, 0, 0));
        press1.setTrackStopColor(new java.awt.Color(255, 0, 0));
        press1.setTrackVisible(true);
        press1.setUnitString("Mpa");
        press1.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        press2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        press2.setFrameVisible(false);
        press2.setLcdVisible(false);
        press2.setLedVisible(false);
        press2.setMaxValue(2.0);
        press2.setTitle("Pressure 2");
        press2.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        press2.setTrackStart(1.475);
        press2.setTrackStartColor(new java.awt.Color(255, 0, 0));
        press2.setTrackStopColor(new java.awt.Color(255, 0, 0));
        press2.setTrackVisible(true);
        press2.setUnitString("Mpa");
        press2.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        press3.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        press3.setFrameVisible(false);
        press3.setLcdVisible(false);
        press3.setLedVisible(false);
        press3.setMaxValue(2.0);
        press3.setTitle("Pressure 3");
        press3.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        press3.setTrackStart(1.475);
        press3.setTrackStartColor(new java.awt.Color(255, 0, 0));
        press3.setTrackStopColor(new java.awt.Color(255, 0, 0));
        press3.setTrackVisible(true);
        press3.setUnitString("Mpa");
        press3.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        press4.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        press4.setFrameVisible(false);
        press4.setLcdVisible(false);
        press4.setLedVisible(false);
        press4.setMaxValue(2.0);
        press4.setTitle("Pressure 4\n");
        press4.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        press4.setTrackStart(1.475);
        press4.setTrackStartColor(new java.awt.Color(255, 0, 0));
        press4.setTrackStopColor(new java.awt.Color(255, 0, 0));
        press4.setTrackVisible(true);
        press4.setUnitString("Mpa");
        press4.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        jButton1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jButton1.setText(org.openide.util.NbBundle.getMessage(DearatorUI.class, "CondensateUI.jButton1.text")); // NOI18N
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
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(level1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(waterIn1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(waterOut1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(press1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(level2, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(waterIn2, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(waterOut2, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(level3, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(waterIn3, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(waterOut3, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(level4, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(waterIn4, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(waterOut4, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(press4, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(press2, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(press3, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(annunciatorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(annunciatorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(level3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(waterIn3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(level2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(waterIn2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(waterOut3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(waterOut2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(press2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(press3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(level1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(waterIn1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(waterOut1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(press1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(level4, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(waterIn4, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(waterOut4, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(press4, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel3);

        jMenu1.setText("Window");

        jMenuItem5.setText(org.openide.util.NbBundle.getMessage(DearatorUI.class, "TGUI.jMenuItem5.text")); // NOI18N
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem3.setText("Core Map");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem10.setText("Selsyns");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem10);

        jMenuItem12.setText("MCP");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem12);

        jMenuItem1.setText("Turbine-Generators");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Condensate");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem8.setText(org.openide.util.NbBundle.getMessage(DearatorUI.class, "TGUI.jMenuItem8.text")); // NOI18N
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem8);

        jMenuItem4.setText(org.openide.util.NbBundle.getMessage(DearatorUI.class, "TGUI.jMenuItem4.text")); // NOI18N
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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1750, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        UI.createOrContinue(CoreMap.class, false, false);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void steamOutVClose3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamOutVClose3ItemStateChanged
        dearators.get(2).steamOutlet.setState(0);
    }//GEN-LAST:event_steamOutVClose3ItemStateChanged

    private void steamOutVStop3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_steamOutVStop3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_steamOutVStop3ActionPerformed

    private void steamOutVStop3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamOutVStop3ItemStateChanged
        dearators.get(2).steamOutlet.setState(1);
    }//GEN-LAST:event_steamOutVStop3ItemStateChanged

    private void steamOutVOpen3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_steamOutVOpen3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_steamOutVOpen3ActionPerformed

    private void steamOutVOpen3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamOutVOpen3ItemStateChanged
        dearators.get(2).steamOutlet.setState(2);
    }//GEN-LAST:event_steamOutVOpen3ItemStateChanged

    private void steamInVClose3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamInVClose3ItemStateChanged
        dearators.get(2).steamInlet.setState(0);
    }//GEN-LAST:event_steamInVClose3ItemStateChanged

    private void steamInVStop3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_steamInVStop3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_steamInVStop3ActionPerformed

    private void steamInVStop3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamInVStop3ItemStateChanged
        dearators.get(2).steamInlet.setState(1);
    }//GEN-LAST:event_steamInVStop3ItemStateChanged

    private void steamInVopen3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_steamInVopen3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_steamInVopen3ActionPerformed

    private void steamInVopen3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamInVopen3ItemStateChanged
        dearators.get(2).steamInlet.setState(2);
    }//GEN-LAST:event_steamInVopen3ItemStateChanged

    private void precisionIncrement3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionIncrement3ActionPerformed
        double currentValue = setpoint3.getLcdValue();
        if (currentValue > 1.4) {
            return;
        }
        setpoint3.setLcdValue(currentValue + 0.05);
        autoControl.dearatorPressureControl.get(2).setSetpoint(currentValue + 0.05);
    }//GEN-LAST:event_precisionIncrement3ActionPerformed

    private void precisionDecrement3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionDecrement3ActionPerformed
        double currentValue = setpoint3.getLcdValue();
        if (currentValue < 0.10142) {
            return;
        }
        setpoint3.setLcdValue(currentValue - 0.05);
        autoControl.dearatorPressureControl.get(2).setSetpoint(currentValue - 0.05);
    }//GEN-LAST:event_precisionDecrement3ActionPerformed

    private void autoSteamPressure3OffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSteamPressure3OffActionPerformed
        autoControl.dearatorPressureControl.get(2).setEnabled(false);
    }//GEN-LAST:event_autoSteamPressure3OffActionPerformed

    private void autoSteamPressure3OffItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoSteamPressure3OffItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoSteamPressure3OffItemStateChanged

    private void autoSteamPressure3OnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSteamPressure3OnActionPerformed
        autoControl.dearatorPressureControl.get(2).setEnabled(true);
    }//GEN-LAST:event_autoSteamPressure3OnActionPerformed

    private void autoSteamPressure3OnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoSteamPressure3OnItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoSteamPressure3OnItemStateChanged

    private void autoWaterLevel3OffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoWaterLevel3OffActionPerformed
        if (autoControl.dearatorWaterAndMakeupControl.get(2).isEnabled()) {
            autoControl.dearatorWaterAndMakeupControl.get(2).setEnabled(false);
            autoControl.dearatorMakeupControl.get(2).setEnabled(true);
        }
        autoControl.dearatorWaterControl.get(2).setEnabled(false);
        //autoControl.dearatorWaterControl.get(2).setEnabled(false);
    }//GEN-LAST:event_autoWaterLevel3OffActionPerformed

    private void autoWaterLevel3OffItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoWaterLevel3OffItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoWaterLevel3OffItemStateChanged

    private void autoWaterLevel3OnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoWaterLevel3OnActionPerformed
        if (autoControl.dearatorMakeupControl.get(2).isEnabled()) {
            autoControl.dearatorWaterAndMakeupControl.get(2).setEnabled(true);
            autoControl.dearatorMakeupControl.get(2).setEnabled(false);
        } else {
            autoControl.dearatorWaterControl.get(2).setEnabled(true);
        }
        //autoControl.dearatorWaterControl.get(2).setEnabled(true);
    }//GEN-LAST:event_autoWaterLevel3OnActionPerformed

    private void autoWaterLevel3OnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoWaterLevel3OnItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoWaterLevel3OnItemStateChanged

    private void waterInVClose3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_waterInVClose3ItemStateChanged
        dearatorValves.get(2).setState(0);
    }//GEN-LAST:event_waterInVClose3ItemStateChanged

    private void waterInVStop3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterInVStop3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterInVStop3ActionPerformed

    private void waterInVStop3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_waterInVStop3ItemStateChanged
        dearatorValves.get(2).setState(1);
    }//GEN-LAST:event_waterInVStop3ItemStateChanged

    private void waterInVOpen3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterInVOpen3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterInVOpen3ActionPerformed

    private void waterInVOpen3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_waterInVOpen3ItemStateChanged
        dearatorValves.get(2).setState(2);
    }//GEN-LAST:event_waterInVOpen3ItemStateChanged

    private void steamOutVClose2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamOutVClose2ItemStateChanged
        dearators.get(1).steamOutlet.setState(0);
    }//GEN-LAST:event_steamOutVClose2ItemStateChanged

    private void steamOutVStop2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_steamOutVStop2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_steamOutVStop2ActionPerformed

    private void steamOutVStop2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamOutVStop2ItemStateChanged
        dearators.get(1).steamOutlet.setState(1);
    }//GEN-LAST:event_steamOutVStop2ItemStateChanged

    private void steamOutVOpen2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_steamOutVOpen2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_steamOutVOpen2ActionPerformed

    private void steamOutVOpen2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamOutVOpen2ItemStateChanged
        dearators.get(1).steamOutlet.setState(2);
    }//GEN-LAST:event_steamOutVOpen2ItemStateChanged

    private void steamInVClose2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamInVClose2ItemStateChanged
        dearators.get(1).steamInlet.setState(0);
    }//GEN-LAST:event_steamInVClose2ItemStateChanged

    private void steamInVStop2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_steamInVStop2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_steamInVStop2ActionPerformed

    private void steamInVStop2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamInVStop2ItemStateChanged
        dearators.get(1).steamInlet.setState(1);
    }//GEN-LAST:event_steamInVStop2ItemStateChanged

    private void steamInVopen2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_steamInVopen2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_steamInVopen2ActionPerformed

    private void steamInVopen2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamInVopen2ItemStateChanged
        dearators.get(1).steamInlet.setState(2);
    }//GEN-LAST:event_steamInVopen2ItemStateChanged

    private void precisionIncrement2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionIncrement2ActionPerformed
        double currentValue = setpoint2.getLcdValue();
        if (currentValue > 1.4) {
            return;
        }
        setpoint2.setLcdValue(currentValue + 0.05);
        autoControl.dearatorPressureControl.get(1).setSetpoint(currentValue + 0.05);
    }//GEN-LAST:event_precisionIncrement2ActionPerformed

    private void precisionDecrement2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionDecrement2ActionPerformed
        double currentValue = setpoint2.getLcdValue();
        if (currentValue < 0.10142) {
            return;
        }
        setpoint2.setLcdValue(currentValue - 0.05);
        autoControl.dearatorPressureControl.get(1).setSetpoint(currentValue - 0.05);
    }//GEN-LAST:event_precisionDecrement2ActionPerformed

    private void autoSteamPressure2OffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSteamPressure2OffActionPerformed
        autoControl.dearatorPressureControl.get(1).setEnabled(false);
    }//GEN-LAST:event_autoSteamPressure2OffActionPerformed

    private void autoSteamPressure2OffItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoSteamPressure2OffItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoSteamPressure2OffItemStateChanged

    private void autoSteamPressure2OnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSteamPressure2OnActionPerformed
        autoControl.dearatorPressureControl.get(1).setEnabled(true);
    }//GEN-LAST:event_autoSteamPressure2OnActionPerformed

    private void autoSteamPressure2OnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoSteamPressure2OnItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoSteamPressure2OnItemStateChanged

    private void autoWaterLevel2OffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoWaterLevel2OffActionPerformed
        if (autoControl.dearatorWaterAndMakeupControl.get(1).isEnabled()) {
            autoControl.dearatorWaterAndMakeupControl.get(1).setEnabled(false);
            autoControl.dearatorMakeupControl.get(1).setEnabled(true);
        }
        autoControl.dearatorWaterControl.get(1).setEnabled(false);
        //autoControl.dearatorWaterControl.get(1).setEnabled(false);
    }//GEN-LAST:event_autoWaterLevel2OffActionPerformed

    private void autoWaterLevel2OffItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoWaterLevel2OffItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoWaterLevel2OffItemStateChanged

    private void autoWaterLevel2OnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoWaterLevel2OnActionPerformed
        if (autoControl.dearatorMakeupControl.get(1).isEnabled()) {
            autoControl.dearatorWaterAndMakeupControl.get(1).setEnabled(true);
            autoControl.dearatorMakeupControl.get(1).setEnabled(false);
        } else {
            autoControl.dearatorWaterControl.get(1).setEnabled(true);
        }
        //autoControl.dearatorWaterControl.get(1).setEnabled(true);
    }//GEN-LAST:event_autoWaterLevel2OnActionPerformed

    private void autoWaterLevel2OnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoWaterLevel2OnItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoWaterLevel2OnItemStateChanged

    private void waterInVClose2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_waterInVClose2ItemStateChanged
        dearatorValves.get(1).setState(0);
    }//GEN-LAST:event_waterInVClose2ItemStateChanged

    private void waterInVStop2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterInVStop2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterInVStop2ActionPerformed

    private void waterInVStop2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_waterInVStop2ItemStateChanged
        dearatorValves.get(1).setState(1);
    }//GEN-LAST:event_waterInVStop2ItemStateChanged

    private void waterInVOpen2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterInVOpen2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterInVOpen2ActionPerformed

    private void waterInVOpen2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_waterInVOpen2ItemStateChanged
        dearatorValves.get(1).setState(2);
    }//GEN-LAST:event_waterInVOpen2ItemStateChanged

    private void steamOutVClose4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamOutVClose4ItemStateChanged
        dearators.get(3).steamOutlet.setState(0);
    }//GEN-LAST:event_steamOutVClose4ItemStateChanged

    private void steamOutVStop4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_steamOutVStop4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_steamOutVStop4ActionPerformed

    private void steamOutVStop4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamOutVStop4ItemStateChanged
        dearators.get(3).steamOutlet.setState(1);
    }//GEN-LAST:event_steamOutVStop4ItemStateChanged

    private void steamOutVOpen4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_steamOutVOpen4ActionPerformed
        //void
    }//GEN-LAST:event_steamOutVOpen4ActionPerformed

    private void steamOutVOpen4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamOutVOpen4ItemStateChanged
        dearators.get(3).steamOutlet.setState(2);
    }//GEN-LAST:event_steamOutVOpen4ItemStateChanged

    private void steamInVClose4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamInVClose4ItemStateChanged
        dearators.get(3).steamInlet.setState(0);
    }//GEN-LAST:event_steamInVClose4ItemStateChanged

    private void steamInVStop4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_steamInVStop4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_steamInVStop4ActionPerformed

    private void steamInVStop4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamInVStop4ItemStateChanged
        dearators.get(3).steamInlet.setState(1);
    }//GEN-LAST:event_steamInVStop4ItemStateChanged

    private void steamInVopen4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_steamInVopen4ActionPerformed
        //void
    }//GEN-LAST:event_steamInVopen4ActionPerformed

    private void steamInVopen4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamInVopen4ItemStateChanged
        dearators.get(3).steamInlet.setState(2);
    }//GEN-LAST:event_steamInVopen4ItemStateChanged

    private void precisionIncrement4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionIncrement4ActionPerformed
        double currentValue = setpoint4.getLcdValue();
        if (currentValue > 1.4) {
            return;
        }
        setpoint4.setLcdValue(currentValue + 0.05);
        autoControl.dearatorPressureControl.get(3).setSetpoint(currentValue + 0.05);
    }//GEN-LAST:event_precisionIncrement4ActionPerformed

    private void precisionDecrement4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionDecrement4ActionPerformed
        double currentValue = setpoint4.getLcdValue();
        if (currentValue < 0.10142) {
            return;
        }
        setpoint4.setLcdValue(currentValue - 0.05);
        autoControl.dearatorPressureControl.get(3).setSetpoint(currentValue - 0.05);
    }//GEN-LAST:event_precisionDecrement4ActionPerformed

    private void autoSteamPressure4OffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSteamPressure4OffActionPerformed
        autoControl.dearatorPressureControl.get(3).setEnabled(false);
    }//GEN-LAST:event_autoSteamPressure4OffActionPerformed

    private void autoSteamPressure4OffItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoSteamPressure4OffItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoSteamPressure4OffItemStateChanged

    private void autoSteamPressure4OnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSteamPressure4OnActionPerformed
        autoControl.dearatorPressureControl.get(3).setEnabled(true);
    }//GEN-LAST:event_autoSteamPressure4OnActionPerformed

    private void autoSteamPressure4OnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoSteamPressure4OnItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoSteamPressure4OnItemStateChanged

    private void autoWaterLevel4OffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoWaterLevel4OffActionPerformed
        if (autoControl.dearatorWaterAndMakeupControl.get(3).isEnabled()) {
            autoControl.dearatorWaterAndMakeupControl.get(3).setEnabled(false);
            autoControl.dearatorMakeupControl.get(3).setEnabled(true);
        }
        autoControl.dearatorWaterControl.get(3).setEnabled(false);
        //autoControl.dearatorWaterControl.get(3).setEnabled(false);
    }//GEN-LAST:event_autoWaterLevel4OffActionPerformed

    private void autoWaterLevel4OffItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoWaterLevel4OffItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoWaterLevel4OffItemStateChanged

    private void autoWaterLevel4OnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoWaterLevel4OnActionPerformed
        if (autoControl.dearatorMakeupControl.get(3).isEnabled()) {
            autoControl.dearatorWaterAndMakeupControl.get(3).setEnabled(true);
            autoControl.dearatorMakeupControl.get(3).setEnabled(false);
        } else {
            autoControl.dearatorWaterControl.get(3).setEnabled(true);
        }
        //autoControl.dearatorWaterControl.get(3).setEnabled(true);
    }//GEN-LAST:event_autoWaterLevel4OnActionPerformed

    private void autoWaterLevel4OnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoWaterLevel4OnItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoWaterLevel4OnItemStateChanged

    private void waterInVClose4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_waterInVClose4ItemStateChanged
        dearatorValves.get(3).setState(0);
    }//GEN-LAST:event_waterInVClose4ItemStateChanged

    private void waterInVStop4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterInVStop4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterInVStop4ActionPerformed

    private void waterInVStop4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_waterInVStop4ItemStateChanged
        dearatorValves.get(3).setState(1);
    }//GEN-LAST:event_waterInVStop4ItemStateChanged

    private void waterInVOpen4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterInVOpen4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterInVOpen4ActionPerformed

    private void waterInVOpen4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_waterInVOpen4ItemStateChanged
        dearatorValves.get(3).setState(2);
    }//GEN-LAST:event_waterInVOpen4ItemStateChanged

    private void steamOutVClose1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamOutVClose1ItemStateChanged
        dearators.get(0).steamOutlet.setState(0);
    }//GEN-LAST:event_steamOutVClose1ItemStateChanged

    private void steamOutVStop1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_steamOutVStop1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_steamOutVStop1ActionPerformed

    private void steamOutVStop1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamOutVStop1ItemStateChanged
        dearators.get(0).steamOutlet.setState(1);
    }//GEN-LAST:event_steamOutVStop1ItemStateChanged

    private void steamOutVOpen1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_steamOutVOpen1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_steamOutVOpen1ActionPerformed

    private void steamOutVOpen1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamOutVOpen1ItemStateChanged
        dearators.get(0).steamOutlet.setState(2);
    }//GEN-LAST:event_steamOutVOpen1ItemStateChanged

    private void steamInVClose1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamInVClose1ItemStateChanged
        dearators.get(0).steamInlet.setState(0);
    }//GEN-LAST:event_steamInVClose1ItemStateChanged

    private void steamInVStop1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_steamInVStop1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_steamInVStop1ActionPerformed

    private void steamInVStop1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamInVStop1ItemStateChanged
        dearators.get(0).steamInlet.setState(1);
    }//GEN-LAST:event_steamInVStop1ItemStateChanged

    private void steamInVOpen1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_steamInVOpen1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_steamInVOpen1ActionPerformed

    private void steamInVOpen1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_steamInVOpen1ItemStateChanged
        dearators.get(0).steamInlet.setState(2);
    }//GEN-LAST:event_steamInVOpen1ItemStateChanged

    private void precisionIncrement1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionIncrement1ActionPerformed
        double currentValue = setpoint1.getLcdValue();
        if (currentValue > 1.4) {
            return;
        }
        setpoint1.setLcdValue(currentValue + 0.05);
        autoControl.dearatorPressureControl.get(0).setSetpoint(currentValue + 0.05);
    }//GEN-LAST:event_precisionIncrement1ActionPerformed

    private void precisionDecrement1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionDecrement1ActionPerformed
        double currentValue = setpoint1.getLcdValue();
        if (currentValue < 0.10142) {
            return;
        }
        setpoint1.setLcdValue(currentValue - 0.05);
        autoControl.dearatorPressureControl.get(0).setSetpoint(currentValue - 0.05);
    }//GEN-LAST:event_precisionDecrement1ActionPerformed

    private void autoSteamPressure1OffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSteamPressure1OffActionPerformed
        autoControl.dearatorPressureControl.get(0).setEnabled(false);
    }//GEN-LAST:event_autoSteamPressure1OffActionPerformed

    private void autoSteamPressure1OffItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoSteamPressure1OffItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoSteamPressure1OffItemStateChanged

    private void autoSteamPressure1OnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSteamPressure1OnActionPerformed
        autoControl.dearatorPressureControl.get(0).setEnabled(true);
    }//GEN-LAST:event_autoSteamPressure1OnActionPerformed

    private void autoSteamPressure1OnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoSteamPressure1OnItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoSteamPressure1OnItemStateChanged

    private void autoWaterLevel1OffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoWaterLevel1OffActionPerformed
        if (autoControl.dearatorWaterAndMakeupControl.get(0).isEnabled()) {
            autoControl.dearatorWaterAndMakeupControl.get(0).setEnabled(false);
            autoControl.dearatorMakeupControl.get(0).setEnabled(true);
        }
        autoControl.dearatorWaterControl.get(0).setEnabled(false);
    }//GEN-LAST:event_autoWaterLevel1OffActionPerformed

    private void autoWaterLevel1OffItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoWaterLevel1OffItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoWaterLevel1OffItemStateChanged

    private void autoWaterLevel1OnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoWaterLevel1OnActionPerformed
        if (autoControl.dearatorMakeupControl.get(0).isEnabled()) {
            autoControl.dearatorWaterAndMakeupControl.get(0).setEnabled(true);
            autoControl.dearatorMakeupControl.get(0).setEnabled(false);
        } else {
            autoControl.dearatorWaterControl.get(0).setEnabled(true);
        }
        //autoControl.dearatorWaterControl.get(0).setEnabled(true);
    }//GEN-LAST:event_autoWaterLevel1OnActionPerformed

    private void autoWaterLevel1OnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoWaterLevel1OnItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_autoWaterLevel1OnItemStateChanged

    private void waterInVClose1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_waterInVClose1ItemStateChanged
        dearatorValves.get(0).setState(0);
    }//GEN-LAST:event_waterInVClose1ItemStateChanged

    private void waterInVStop1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterInVStop1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterInVStop1ActionPerformed

    private void waterInVStop1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_waterInVStop1ItemStateChanged
        dearatorValves.get(0).setState(1);
    }//GEN-LAST:event_waterInVStop1ItemStateChanged

    private void waterInVOpen1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterInVOpen1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterInVOpen1ActionPerformed

    private void waterInVOpen1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_waterInVOpen1ItemStateChanged
        dearatorValves.get(0).setState(2);
    }//GEN-LAST:event_waterInVOpen1ItemStateChanged

    private void overflow4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overflow4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_overflow4ActionPerformed

    private void overflow3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overflow3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_overflow3ActionPerformed

    private void overflow2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overflow2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_overflow2ActionPerformed

    private void overflow1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overflow1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_overflow1ActionPerformed

    private void hPress2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hPress2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hPress2ActionPerformed

    private void hLevel2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hLevel2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hLevel2ActionPerformed

    private void hLevel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hLevel1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hLevel1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        UI.createOrContinue(TGUI.class, true, false);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        UI.createOrContinue(CondensateUI.class, true, false);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        UI.createOrContinue(FeedwaterUI.class, true, false);
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void isolate4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_isolate4ActionPerformed
        if (isolate4.isSelected()) {
            fwSuctionHeader.isolationValveArray.get(3).setState(0);
        } else {
            fwSuctionHeader.isolationValveArray.get(3).setState(2);
        }
    }//GEN-LAST:event_isolate4ActionPerformed

    private void isolate3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_isolate3ActionPerformed
        if (isolate3.isSelected()) {
            fwSuctionHeader.isolationValveArray.get(2).setState(0);
        } else {
            fwSuctionHeader.isolationValveArray.get(2).setState(2);
        }
    }//GEN-LAST:event_isolate3ActionPerformed

    private void isolate2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_isolate2ActionPerformed
        if (isolate2.isSelected()) {
            fwSuctionHeader.isolationValveArray.get(1).setState(0);
        } else {
            fwSuctionHeader.isolationValveArray.get(1).setState(2);
        }
    }//GEN-LAST:event_isolate2ActionPerformed

    private void isolate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_isolate1ActionPerformed
        if (isolate1.isSelected()) {
            fwSuctionHeader.isolationValveArray.get(0).setState(0);
        } else {
            fwSuctionHeader.isolationValveArray.get(0).setState(2);
        }
    }//GEN-LAST:event_isolate1ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        UI.createOrContinue(PCSUI.class, true, false);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        annunciator.acknowledge();
    }//GEN-LAST:event_jButton1ActionPerformed

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
    private javax.swing.JRadioButton autoSteamPressure1On;
    private javax.swing.JRadioButton autoSteamPressure2Off;
    private javax.swing.JRadioButton autoSteamPressure2On;
    private javax.swing.JRadioButton autoSteamPressure3Off;
    private javax.swing.JRadioButton autoSteamPressure3On;
    private javax.swing.JRadioButton autoSteamPressure4Off;
    private javax.swing.JRadioButton autoSteamPressure4On;
    private javax.swing.JRadioButton autoWaterLevel1Off;
    private javax.swing.JRadioButton autoWaterLevel1On;
    private javax.swing.JRadioButton autoWaterLevel2Off;
    private javax.swing.JRadioButton autoWaterLevel2On;
    private javax.swing.JRadioButton autoWaterLevel3Off;
    private javax.swing.JRadioButton autoWaterLevel3On;
    private javax.swing.JRadioButton autoWaterLevel4Off;
    private javax.swing.JRadioButton autoWaterLevel4On;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup10;
    private javax.swing.ButtonGroup buttonGroup11;
    private javax.swing.ButtonGroup buttonGroup12;
    private javax.swing.ButtonGroup buttonGroup13;
    private javax.swing.ButtonGroup buttonGroup14;
    private javax.swing.ButtonGroup buttonGroup15;
    private javax.swing.ButtonGroup buttonGroup16;
    private javax.swing.ButtonGroup buttonGroup17;
    private javax.swing.ButtonGroup buttonGroup18;
    private javax.swing.ButtonGroup buttonGroup19;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup20;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.ButtonGroup buttonGroup6;
    private javax.swing.ButtonGroup buttonGroup7;
    private javax.swing.ButtonGroup buttonGroup8;
    private javax.swing.ButtonGroup buttonGroup9;
    private javax.swing.JTextField hLevel1;
    private javax.swing.JTextField hLevel2;
    private javax.swing.JTextField hLevel3;
    private javax.swing.JTextField hLevel4;
    private javax.swing.JTextField hPress1;
    private javax.swing.JTextField hPress2;
    private javax.swing.JTextField hPress3;
    private javax.swing.JTextField hPress4;
    private javax.swing.JCheckBox isolate1;
    private javax.swing.JCheckBox isolate2;
    private javax.swing.JCheckBox isolate3;
    private javax.swing.JCheckBox isolate4;
    private javax.swing.JButton jButton1;
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
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
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
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private eu.hansolo.steelseries.gauges.Linear level1;
    private eu.hansolo.steelseries.gauges.Linear level2;
    private eu.hansolo.steelseries.gauges.Linear level3;
    private eu.hansolo.steelseries.gauges.Linear level4;
    private eu.hansolo.steelseries.gauges.DisplaySingle over1;
    private eu.hansolo.steelseries.gauges.DisplaySingle over2;
    private eu.hansolo.steelseries.gauges.DisplaySingle over3;
    private eu.hansolo.steelseries.gauges.DisplaySingle over4;
    private javax.swing.JTextField overflow1;
    private javax.swing.JTextField overflow2;
    private javax.swing.JTextField overflow3;
    private javax.swing.JTextField overflow4;
    private javax.swing.JButton precisionDecrement1;
    private javax.swing.JButton precisionDecrement2;
    private javax.swing.JButton precisionDecrement3;
    private javax.swing.JButton precisionDecrement4;
    private javax.swing.JButton precisionIncrement1;
    private javax.swing.JButton precisionIncrement2;
    private javax.swing.JButton precisionIncrement3;
    private javax.swing.JButton precisionIncrement4;
    private eu.hansolo.steelseries.gauges.Linear press1;
    private eu.hansolo.steelseries.gauges.Linear press2;
    private eu.hansolo.steelseries.gauges.Linear press3;
    private eu.hansolo.steelseries.gauges.Linear press4;
    private eu.hansolo.steelseries.gauges.DisplaySingle setpoint1;
    private eu.hansolo.steelseries.gauges.DisplaySingle setpoint2;
    private eu.hansolo.steelseries.gauges.DisplaySingle setpoint3;
    private eu.hansolo.steelseries.gauges.DisplaySingle setpoint4;
    private eu.hansolo.steelseries.gauges.DisplaySingle steamIn1;
    private eu.hansolo.steelseries.gauges.DisplaySingle steamIn2;
    private eu.hansolo.steelseries.gauges.DisplaySingle steamIn3;
    private eu.hansolo.steelseries.gauges.DisplaySingle steamIn4;
    private eu.hansolo.steelseries.gauges.Radial2Top steamInV1;
    private eu.hansolo.steelseries.gauges.Radial2Top steamInV2;
    private eu.hansolo.steelseries.gauges.Radial2Top steamInV3;
    private eu.hansolo.steelseries.gauges.Radial2Top steamInV4;
    private javax.swing.JRadioButton steamInVClose1;
    private javax.swing.JRadioButton steamInVClose2;
    private javax.swing.JRadioButton steamInVClose3;
    private javax.swing.JRadioButton steamInVClose4;
    private javax.swing.JRadioButton steamInVOpen1;
    private javax.swing.JRadioButton steamInVStop1;
    private javax.swing.JRadioButton steamInVStop2;
    private javax.swing.JRadioButton steamInVStop3;
    private javax.swing.JRadioButton steamInVStop4;
    private javax.swing.JRadioButton steamInVopen2;
    private javax.swing.JRadioButton steamInVopen3;
    private javax.swing.JRadioButton steamInVopen4;
    private eu.hansolo.steelseries.gauges.DisplaySingle steamOut1;
    private eu.hansolo.steelseries.gauges.DisplaySingle steamOut2;
    private eu.hansolo.steelseries.gauges.DisplaySingle steamOut3;
    private eu.hansolo.steelseries.gauges.DisplaySingle steamOut4;
    private eu.hansolo.steelseries.gauges.Radial2Top steamOutV1;
    private eu.hansolo.steelseries.gauges.Radial2Top steamOutV2;
    private eu.hansolo.steelseries.gauges.Radial2Top steamOutV3;
    private eu.hansolo.steelseries.gauges.Radial2Top steamOutV4;
    private javax.swing.JRadioButton steamOutVClose1;
    private javax.swing.JRadioButton steamOutVClose2;
    private javax.swing.JRadioButton steamOutVClose3;
    private javax.swing.JRadioButton steamOutVClose4;
    private javax.swing.JRadioButton steamOutVOpen1;
    private javax.swing.JRadioButton steamOutVOpen2;
    private javax.swing.JRadioButton steamOutVOpen3;
    private javax.swing.JRadioButton steamOutVOpen4;
    private javax.swing.JRadioButton steamOutVStop1;
    private javax.swing.JRadioButton steamOutVStop2;
    private javax.swing.JRadioButton steamOutVStop3;
    private javax.swing.JRadioButton steamOutVStop4;
    private eu.hansolo.steelseries.gauges.DisplaySingle temp1;
    private eu.hansolo.steelseries.gauges.DisplaySingle temp2;
    private eu.hansolo.steelseries.gauges.DisplaySingle temp3;
    private eu.hansolo.steelseries.gauges.DisplaySingle temp4;
    private eu.hansolo.steelseries.gauges.Linear waterIn1;
    private eu.hansolo.steelseries.gauges.Linear waterIn2;
    private eu.hansolo.steelseries.gauges.Linear waterIn3;
    private eu.hansolo.steelseries.gauges.Linear waterIn4;
    private eu.hansolo.steelseries.gauges.Radial2Top waterInV1;
    private eu.hansolo.steelseries.gauges.Radial2Top waterInV2;
    private eu.hansolo.steelseries.gauges.Radial2Top waterInV3;
    private eu.hansolo.steelseries.gauges.Radial2Top waterInV4;
    private javax.swing.JRadioButton waterInVClose1;
    private javax.swing.JRadioButton waterInVClose2;
    private javax.swing.JRadioButton waterInVClose3;
    private javax.swing.JRadioButton waterInVClose4;
    private javax.swing.JRadioButton waterInVOpen1;
    private javax.swing.JRadioButton waterInVOpen2;
    private javax.swing.JRadioButton waterInVOpen3;
    private javax.swing.JRadioButton waterInVOpen4;
    private javax.swing.JRadioButton waterInVStop1;
    private javax.swing.JRadioButton waterInVStop2;
    private javax.swing.JRadioButton waterInVStop3;
    private javax.swing.JRadioButton waterInVStop4;
    private eu.hansolo.steelseries.gauges.Linear waterOut1;
    private eu.hansolo.steelseries.gauges.Linear waterOut2;
    private eu.hansolo.steelseries.gauges.Linear waterOut3;
    private eu.hansolo.steelseries.gauges.Linear waterOut4;
    // End of variables declaration//GEN-END:variables

}
