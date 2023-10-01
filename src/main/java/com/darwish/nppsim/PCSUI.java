package com.darwish.nppsim;

import static com.darwish.nppsim.NPPSim.autoControl;
import static com.darwish.nppsim.NPPSim.pcs;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;

public class PCSUI extends javax.swing.JFrame implements UIUpdateable {
    private final Annunciator annunciator;
    /**
     * Creates new form TGUI
     */
    public PCSUI() {
        initComponents();
        this.setTitle("Purification and Cooling System");
        annunciator = new Annunciator(annunciatorPanel);
        
        ((JSpinner.DefaultEditor)spinner1A.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor)spinner1A1.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor)spinner1A2.getEditor()).getTextField().setEditable(false);
        spinner1A.addChangeListener((ChangeEvent e) -> {
            Pump currentSelection;
            if ((int)spinner1A.getValue() == 1) {
                currentSelection = pcs.pcsPump1;
            } else {
                currentSelection = pcs.pcsPump2;
            }
            rpm1A.setLcdValue(currentSelection.getRPM());
            amps1A.setLcdValue(currentSelection.getPowerUsage());
            if (currentSelection.isActive()) {
                start1A.setSelected(true);
            } else {
                stop1A.setSelected(true);
            }
            
        });
        spinner1A1.addChangeListener((ChangeEvent e) -> {
            Pump currentSelection = pcs.admsPumps.get((int)spinner1A1.getValue() - 1);
            rpm1A1.setLcdValue(currentSelection.getRPM());
            amps1A1.setLcdValue(currentSelection.getPowerUsage());
            if (currentSelection.isActive()) {
                start1A1.setSelected(true);
            } else {
                stop1A1.setSelected(true);
            }
        });
        
        spinner1A2.addChangeListener((ChangeEvent e) -> {
            WaterValve currentSelection = pcs.dearatorMakeupValves.get((int)spinner1A2.getValue() - 1);
            admsAC.setSelected(autoControl.dearatorMakeupControl.get((int)spinner1A2.getValue() - 1).isEnabled() || autoControl.dearatorWaterAndMakeupControl.get((int)spinner1A2.getValue() - 1).isEnabled());
            switch (currentSelection.getState()) {
                case 0:
                    sdvcClose5.setSelected(true);
                    break;
                case 1:
                    sdvcStop5.setSelected(true);
                    break;
                case 2:
                    sdvcOpen5.setSelected(true);
                    break;  
            }
        });
        
        initializeDialUpdateThread();
        
        //set initial values
        Pump currentSelection = pcs.pcsPump1;
        if (currentSelection.isActive()) {
            start1A.setSelected(true);
        } else {
            stop1A.setSelected(true);
        }
        currentSelection = pcs.coolingPump;
        if (currentSelection.isActive()) {
            start1A2.setSelected(true);
        } else {
            stop1A2.setSelected(true);
        }
        if (pcs.pcsFilterPressureHeader.isolationValveArray.get(2).getPosition() == 1) {
            dwTankButton.setSelected(true);
        } else if (pcs.pcsFilterPressureHeader.isolationValveArray.get(0).getPosition() == 0 && pcs.pcsFilterPressureHeader.isolationValveArray.get(1).getPosition() == 0 && pcs.pcsFilterPressureHeader.isolationValveArray.get(3).getPosition() == 2 && pcs.pcsFilterPressureHeader.isolationValveArray.get(4).getPosition() == 2) {
            bypassButton.setSelected(true);
        } else if (pcs.pcsFilterPressureHeader.isolationValveArray.get(0).getPosition() == 0) {
            regen2.setSelected(true);
        } else if (pcs.pcsFilterPressureHeader.isolationValveArray.get(1).getPosition() == 0) {
            regen1.setSelected(true);
        }
        if (autoControl.dearatorMakeupControl.get(0).isEnabled() || autoControl.dearatorWaterAndMakeupControl.get(0).isEnabled()) {
            admsAC.setSelected(true);
        }
        if (pcs.admsPumps.get(0).isActive()) {
            start1A1.setSelected(true);
        }
        if (pcs.dwMakeupPump.isActive()) {
            start1A3.setSelected(true);
        }
       
    }
    
    @Override
    public void update() {
        checkAlarms();
        if (this.isVisible()) {
            java.awt.EventQueue.invokeLater(() -> {
                Pump coolingPump = pcs.coolingPump;
                Pump selected1A1 = pcs.admsPumps.get((int)spinner1A1.getValue() - 1);
                Pump selected1A;
                if ((int)spinner1A.getValue() == 1) {
                    selected1A = pcs.pcsPump1;
                } else {
                    selected1A = pcs.pcsPump2;
                }
                operationalLed.setLedOn(pcs.pcsFilterPressureHeader.getWaterTemperature() < 60 && (filter1In.isSelected() || filter2In.isSelected() && pcs.pcsPressureHeader.getWaterInflowRate() > 20));
                filterInletTemp.setLcdValue(pcs.pcsFilterPressureHeader.getWaterTemperature());

                rpm1A.setLcdValue(selected1A.getRPM());
                flow1A.setLcdValue(selected1A.timestepFlow * 20);
                amps1A.setLcdValue(selected1A.getPowerUsage());
                rpm1A2.setLcdValue(coolingPump.getRPM());
                flow1A2.setLcdValue(coolingPump.timestepFlow * 20);
                amps1A2.setLcdValue(coolingPump.getPowerUsage());
                rpm1A1.setLcdValue(selected1A1.getRPM());
                flow1A1.setLcdValue(selected1A1.getFlowRate());
                amps1A1.setLcdValue(selected1A1.getPowerUsage());

                flow1A4.setLcdValue(pcs.dearatorMakeupValves.get((int)spinner1A2.getValue() - 1).timestepFlow * 20);

                regen1InTemp1.setLcdValue(pcs.regenerator1.getWaterInflow1Temp());
                regen1InTemp2.setLcdValue(pcs.regenerator1.getWaterInflow2Temp());
                regen2InTemp1.setLcdValue(pcs.regenerator2.getWaterInflow1Temp());
                regen2InTemp2.setLcdValue(pcs.regenerator2.getWaterInflow2Temp());
                regen1OutTemp1.setLcdValue(pcs.regenerator1.getWaterOutflow1Temp());
                regen1OutTemp2.setLcdValue(pcs.regenerator1.getWaterOutflow2Temp());
                regen2OutTemp1.setLcdValue(pcs.regenerator2.getWaterOutflow1Temp());
                regen2OutTemp2.setLcdValue(pcs.regenerator2.getWaterOutflow2Temp());
                regen1Inflow1.setLcdValue(pcs.regenerator1.getWaterFlowRate1());
                regen1Inflow2.setLcdValue(pcs.regenerator1.getWaterFlowRate2());
                regen2Inflow1.setLcdValue(pcs.regenerator2.getWaterFlowRate1());
                regen2Inflow2.setLcdValue(pcs.regenerator2.getWaterFlowRate2());

                cooler1InTemp1.setLcdValue(pcs.pcsCooler1.getWaterInflow1Temp());
                cooler1InTemp2.setLcdValue(pcs.pcsCooler1.getWaterInflow2Temp());
                cooler2InTemp1.setLcdValue(pcs.pcsCooler2.getWaterInflow1Temp());
                cooler2InTemp2.setLcdValue(pcs.pcsCooler2.getWaterInflow2Temp());
                cooler1OutTemp1.setLcdValue(pcs.pcsCooler1.getWaterOutflow1Temp());
                cooler1OutTemp2.setLcdValue(pcs.pcsCooler1.getWaterOutflow2Temp());
                cooler2OutTemp1.setLcdValue(pcs.pcsCooler2.getWaterOutflow1Temp());
                cooler2OutTemp2.setLcdValue(pcs.pcsCooler2.getWaterOutflow2Temp());
                cooler1Inflow1.setLcdValue(pcs.pcsCooler1.getWaterFlowRate1());
                cooler1Inflow2.setLcdValue(pcs.pcsCooler1.getWaterFlowRate2());
                cooler2Inflow1.setLcdValue(pcs.pcsCooler2.getWaterFlowRate1());
                cooler2Inflow2.setLcdValue(pcs.pcsCooler2.getWaterFlowRate2());

                dwTank.setValue(pcs.demineralizedWaterTank.getWaterLevel() * 10 + 1000);
                dwTemp.setLcdValue(pcs.demineralizedWaterTank.getWaterTemperature());
                dwInflow.setLcdValue(pcs.demineralizedWaterTank.getWaterInflowRate());
                dwOutflow.setLcdValue(pcs.demineralizedWaterTank.getWaterOutflowRate());
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
                                valvePos3.setValue(pcs.pcsValve.getPosition() * 100);
                                valvePos4.setValue(pcs.coolingPump.outletValve.getPosition() * 100);
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
        annunciator.setTrigger(pcs.pcsFilterPressureHeader.waterTemperature > 60, waterTemp);
        annunciator.setTrigger(pcs.demineralizedWaterTank.getWaterLevel() < 0, dwLow);
        annunciator.setTrigger(pcs.demineralizedWaterTank.getWaterLevel() > 95, dwHigh);
        annunciator.setTrigger(!(pcs.pcsFilterPressureHeader.getWaterTemperature() < 60 && (filter1In.isSelected() || filter2In.isSelected() && pcs.pcsPressureHeader.getWaterInflowRate() > 20)), INOP);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        annunciatorPanel = new javax.swing.JPanel();
        waterTemp = new javax.swing.JTextField();
        dwLow = new javax.swing.JTextField();
        dwHigh = new javax.swing.JTextField();
        coolingCavit = new javax.swing.JTextField();
        dwTankTemp = new javax.swing.JTextField();
        INOP = new javax.swing.JTextField();
        circCavit = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        start1A2 = new javax.swing.JRadioButton();
        stop1A2 = new javax.swing.JRadioButton();
        jLabel23 = new javax.swing.JLabel();
        rpm1A2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        amps1A2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        flow1A2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jPanel27 = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        cooler1InTemp1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel30 = new javax.swing.JLabel();
        cooler1InTemp2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel31 = new javax.swing.JLabel();
        cooler1OutTemp1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        cooler1OutTemp2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        cooler1Inflow1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        cooler1Inflow2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jPanel28 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        cooler2InTemp1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel32 = new javax.swing.JLabel();
        cooler2InTemp2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel33 = new javax.swing.JLabel();
        cooler2OutTemp1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        cooler2OutTemp2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        cooler2Inflow1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        cooler2Inflow2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel2 = new javax.swing.JLabel();
        sdvcOpen2 = new javax.swing.JRadioButton();
        sdvcClose2 = new javax.swing.JRadioButton();
        sdvcStop2 = new javax.swing.JRadioButton();
        jLabel24 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        valvePos4 = new eu.hansolo.steelseries.gauges.RadialBargraph();
        sdvcOpen4 = new javax.swing.JRadioButton();
        sdvcStop4 = new javax.swing.JRadioButton();
        sdvcClose4 = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel26 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        start1A = new javax.swing.JRadioButton();
        stop1A = new javax.swing.JRadioButton();
        jLabel19 = new javax.swing.JLabel();
        spinner1A = new javax.swing.JSpinner();
        jLabel20 = new javax.swing.JLabel();
        rpm1A = new eu.hansolo.steelseries.gauges.DisplaySingle();
        amps1A = new eu.hansolo.steelseries.gauges.DisplaySingle();
        flow1A = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jPanel22 = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        sdvcOpen1 = new javax.swing.JRadioButton();
        sdvcStop1 = new javax.swing.JRadioButton();
        sdvcClose1 = new javax.swing.JRadioButton();
        valvePos3 = new eu.hansolo.steelseries.gauges.RadialBargraph();
        jLabel6 = new javax.swing.JLabel();
        jPanel29 = new javax.swing.JPanel();
        jPanel24 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        regen1InTemp1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel26 = new javax.swing.JLabel();
        regen1InTemp2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel27 = new javax.swing.JLabel();
        regen1OutTemp1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        regen1OutTemp2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        regen1Inflow1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        regen1Inflow2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jPanel25 = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        regen2InTemp1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel28 = new javax.swing.JLabel();
        regen2InTemp2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel29 = new javax.swing.JLabel();
        regen2OutTemp1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        regen2OutTemp2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        regen2Inflow1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        regen2Inflow2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel25 = new javax.swing.JLabel();
        sdvcStop3 = new javax.swing.JRadioButton();
        sdvcClose3 = new javax.swing.JRadioButton();
        sdvcOpen3 = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        operationalLed = new eu.hansolo.steelseries.extras.Led();
        jLabel16 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        filter1In = new javax.swing.JRadioButton();
        filter1Reg = new javax.swing.JRadioButton();
        jLabel34 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        regen2 = new javax.swing.JRadioButton();
        regen1 = new javax.swing.JRadioButton();
        regenBoth = new javax.swing.JRadioButton();
        dwTankButton = new javax.swing.JRadioButton();
        bypassButton = new javax.swing.JRadioButton();
        filterInletTemp = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel5 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        filter2In = new javax.swing.JRadioButton();
        filter2Reg = new javax.swing.JRadioButton();
        jLabel37 = new javax.swing.JLabel();
        jPanel33 = new javax.swing.JPanel();
        jLabel52 = new javax.swing.JLabel();
        dwTank = new eu.hansolo.steelseries.gauges.LinearBargraph();
        dwInflow = new eu.hansolo.steelseries.gauges.DisplaySingle();
        dwOutflow = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        dwTemp = new eu.hansolo.steelseries.gauges.DisplaySingle();
        start1A3 = new javax.swing.JRadioButton();
        stop1A3 = new javax.swing.JRadioButton();
        jLabel12 = new javax.swing.JLabel();
        jPanel30 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        start1A1 = new javax.swing.JRadioButton();
        stop1A1 = new javax.swing.JRadioButton();
        jLabel21 = new javax.swing.JLabel();
        spinner1A1 = new javax.swing.JSpinner();
        jLabel22 = new javax.swing.JLabel();
        rpm1A1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        amps1A1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        flow1A1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        spinner1A2 = new javax.swing.JSpinner();
        jLabel35 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        sdvcOpen5 = new javax.swing.JRadioButton();
        sdvcStop5 = new javax.swing.JRadioButton();
        sdvcClose5 = new javax.swing.JRadioButton();
        flow1A4 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel11 = new javax.swing.JLabel();
        admsAC = new javax.swing.JCheckBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();

        setBackground(new java.awt.Color(204, 0, 153));
        setSize(new java.awt.Dimension(1366, 768));

        jPanel3.setBackground(UI.BACKGROUND);
        jPanel3.setPreferredSize(new java.awt.Dimension(1366, 768));

        annunciatorPanel.setBackground(UI.BACKGROUND);
        annunciatorPanel.setLayout(new java.awt.GridLayout(3, 6));

        waterTemp.setEditable(false);
        waterTemp.setBackground(new java.awt.Color(142, 0, 0));
        waterTemp.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        waterTemp.setForeground(new java.awt.Color(0, 0, 0));
        waterTemp.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        waterTemp.setText("Water Temp High");
        waterTemp.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        waterTemp.setFocusable(false);
        waterTemp.setPreferredSize(new java.awt.Dimension(100, 30));
        waterTemp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterTempActionPerformed(evt);
            }
        });
        annunciatorPanel.add(waterTemp);

        dwLow.setEditable(false);
        dwLow.setBackground(new java.awt.Color(142, 0, 0));
        dwLow.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        dwLow.setForeground(new java.awt.Color(0, 0, 0));
        dwLow.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        dwLow.setText("DW Tank Level Low");
        dwLow.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        dwLow.setFocusable(false);
        dwLow.setPreferredSize(new java.awt.Dimension(100, 30));
        dwLow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dwLowActionPerformed(evt);
            }
        });
        annunciatorPanel.add(dwLow);

        dwHigh.setEditable(false);
        dwHigh.setBackground(new java.awt.Color(107, 103, 0));
        dwHigh.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        dwHigh.setForeground(new java.awt.Color(0, 0, 0));
        dwHigh.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        dwHigh.setText(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.dwHigh.text")); // NOI18N
        dwHigh.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        dwHigh.setFocusable(false);
        dwHigh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dwHighActionPerformed(evt);
            }
        });
        annunciatorPanel.add(dwHigh);

        coolingCavit.setEditable(false);
        coolingCavit.setBackground(new java.awt.Color(142, 0, 0));
        coolingCavit.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        coolingCavit.setForeground(new java.awt.Color(0, 0, 0));
        coolingCavit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        coolingCavit.setText("Cooling Pump Cavit.");
        coolingCavit.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        coolingCavit.setFocusable(false);
        coolingCavit.setPreferredSize(new java.awt.Dimension(100, 30));
        coolingCavit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                coolingCavitActionPerformed(evt);
            }
        });
        annunciatorPanel.add(coolingCavit);

        dwTankTemp.setEditable(false);
        dwTankTemp.setBackground(new java.awt.Color(142, 0, 0));
        dwTankTemp.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        dwTankTemp.setForeground(new java.awt.Color(0, 0, 0));
        dwTankTemp.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        dwTankTemp.setText(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.dwTankTemp.text")); // NOI18N
        dwTankTemp.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        dwTankTemp.setFocusable(false);
        dwTankTemp.setPreferredSize(new java.awt.Dimension(100, 30));
        dwTankTemp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dwTankTempActionPerformed(evt);
            }
        });
        annunciatorPanel.add(dwTankTemp);

        INOP.setEditable(false);
        INOP.setBackground(new java.awt.Color(107, 103, 0));
        INOP.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        INOP.setForeground(new java.awt.Color(0, 0, 0));
        INOP.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        INOP.setText(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.INOP.text")); // NOI18N
        INOP.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        INOP.setFocusable(false);
        annunciatorPanel.add(INOP);

        circCavit.setEditable(false);
        circCavit.setBackground(new java.awt.Color(142, 0, 0));
        circCavit.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        circCavit.setForeground(new java.awt.Color(0, 0, 0));
        circCavit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        circCavit.setText("Circ Pump Cavit.");
        circCavit.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        circCavit.setFocusable(false);
        annunciatorPanel.add(circCavit);

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

        jPanel14.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jPanel15.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup9.add(start1A2);
        org.openide.awt.Mnemonics.setLocalizedText(start1A2, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.start1A2.text")); // NOI18N
        start1A2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                start1A2ItemStateChanged(evt);
            }
        });
        start1A2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                start1A2ActionPerformed(evt);
            }
        });

        buttonGroup9.add(stop1A2);
        stop1A2.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(stop1A2, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.stop1A2.text")); // NOI18N
        stop1A2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                stop1A2ItemStateChanged(evt);
            }
        });
        stop1A2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stop1A2ActionPerformed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel23, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel23.text")); // NOI18N

        rpm1A2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        rpm1A2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        rpm1A2.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.rpm1A2.lcdUnitString")); // NOI18N
        rpm1A2.setLcdValue(3000.0);

        amps1A2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        amps1A2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        amps1A2.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.amps1A2.lcdUnitString")); // NOI18N
        amps1A2.setLcdValue(1600.7);

        flow1A2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        flow1A2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        flow1A2.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.flow1A2.lcdUnitString")); // NOI18N
        flow1A2.setLcdValue(530.2);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(start1A2)
                            .addComponent(stop1A2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(amps1A2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rpm1A2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(flow1A2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 12, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(start1A2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stop1A2))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(rpm1A2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(amps1A2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(flow1A2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel27.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel47.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel47, "Inlet Cooler 1");

        cooler1InTemp1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        cooler1InTemp1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        cooler1InTemp1.setLcdUnitString("°C     IN");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel30, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel30.text")); // NOI18N

        cooler1InTemp2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        cooler1InTemp2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        cooler1InTemp2.setLcdUnitString("°C     IN");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel31, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel31.text")); // NOI18N

        cooler1OutTemp1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        cooler1OutTemp1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        cooler1OutTemp1.setLcdUnitString("°C OUT");

        cooler1OutTemp2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        cooler1OutTemp2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        cooler1OutTemp2.setLcdUnitString("°C OUT");

        cooler1Inflow1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        cooler1Inflow1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        cooler1Inflow1.setLcdUnitString("kg/s    .");

        cooler1Inflow2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        cooler1Inflow2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        cooler1Inflow2.setLcdUnitString("kg/s    .");

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel47)
                            .addGroup(jPanel27Layout.createSequentialGroup()
                                .addComponent(cooler1OutTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cooler1OutTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel27Layout.createSequentialGroup()
                                .addComponent(cooler1InTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cooler1InTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel27Layout.createSequentialGroup()
                                .addComponent(cooler1Inflow1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cooler1Inflow2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel30)
                        .addGap(24, 24, 24))))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel47)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(jLabel31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cooler1InTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cooler1InTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cooler1OutTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cooler1OutTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cooler1Inflow2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cooler1Inflow1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel28.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel48.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel48, "Inlet Cooler 2");

        cooler2InTemp1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        cooler2InTemp1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        cooler2InTemp1.setLcdUnitString("°C     IN");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel32, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel32.text")); // NOI18N

        cooler2InTemp2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        cooler2InTemp2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        cooler2InTemp2.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.cooler2InTemp2.lcdUnitString")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel33, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel33.text")); // NOI18N

        cooler2OutTemp1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        cooler2OutTemp1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        cooler2OutTemp1.setLcdUnitString("°C OUT");

        cooler2OutTemp2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        cooler2OutTemp2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        cooler2OutTemp2.setLcdUnitString("°C OUT");

        cooler2Inflow1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        cooler2Inflow1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        cooler2Inflow1.setLcdUnitString("kg/s    .");

        cooler2Inflow2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        cooler2Inflow2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        cooler2Inflow2.setLcdUnitString("kg/s    .");

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel48)
                            .addGroup(jPanel28Layout.createSequentialGroup()
                                .addComponent(cooler2OutTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cooler2OutTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel28Layout.createSequentialGroup()
                                .addComponent(cooler2InTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cooler2InTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel28Layout.createSequentialGroup()
                                .addComponent(cooler2Inflow1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cooler2Inflow2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel32)
                        .addGap(24, 24, 24))))
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel48)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(jLabel33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cooler2InTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cooler2InTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cooler2OutTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cooler2OutTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cooler2Inflow2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cooler2Inflow1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "Cooling System");

        buttonGroup1.add(sdvcOpen2);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcOpen2, "Cooler 1");
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

        buttonGroup1.add(sdvcClose2);
        sdvcClose2.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcClose2, "Both");
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

        buttonGroup1.add(sdvcStop2);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcStop2, "Cooler 2");
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

        jLabel24.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel24, "Cooling Water Drain");

        jLabel44.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel44, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel44.text")); // NOI18N

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
        valvePos4.setTitle(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.valvePos4.title")); // NOI18N
        valvePos4.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        valvePos4.setUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.valvePos4.unitString")); // NOI18N

        buttonGroup3.add(sdvcOpen4);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcOpen4, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.sdvcOpen4.text")); // NOI18N
        sdvcOpen4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sdvcOpen4ItemStateChanged(evt);
            }
        });
        sdvcOpen4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdvcOpen4ActionPerformed(evt);
            }
        });

        buttonGroup3.add(sdvcStop4);
        sdvcStop4.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcStop4, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.sdvcStop4.text")); // NOI18N
        sdvcStop4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sdvcStop4ItemStateChanged(evt);
            }
        });
        sdvcStop4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdvcStop4ActionPerformed(evt);
            }
        });

        buttonGroup3.add(sdvcClose4);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcClose4, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.sdvcClose4.text")); // NOI18N
        sdvcClose4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sdvcClose4ItemStateChanged(evt);
            }
        });
        sdvcClose4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdvcClose4ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Position");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel24)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(sdvcOpen2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sdvcClose2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sdvcStop2))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel44)
                        .addGap(44, 44, 44)
                        .addComponent(jLabel1))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sdvcStop4)
                            .addComponent(sdvcOpen4)
                            .addComponent(sdvcClose4))
                        .addGap(49, 49, 49)
                        .addComponent(valvePos4, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sdvcOpen2)
                            .addComponent(sdvcClose2)
                            .addComponent(sdvcStop2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel44)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                                .addComponent(sdvcOpen4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sdvcStop4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sdvcClose4))
                            .addComponent(valvePos4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel26.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jPanel10.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup8.add(start1A);
        org.openide.awt.Mnemonics.setLocalizedText(start1A, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.start1A.text")); // NOI18N
        start1A.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                start1AItemStateChanged(evt);
            }
        });
        start1A.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                start1AActionPerformed(evt);
            }
        });

        buttonGroup8.add(stop1A);
        stop1A.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(stop1A, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.stop1A.text")); // NOI18N
        stop1A.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                stop1AItemStateChanged(evt);
            }
        });
        stop1A.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stop1AActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel19, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel19.text")); // NOI18N

        spinner1A.setModel(new javax.swing.SpinnerNumberModel(1, 1, 2, 1));
        spinner1A.setDoubleBuffered(true);

        jLabel20.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel20, "Circulation Pumps");

        rpm1A.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        rpm1A.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        rpm1A.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.rpm1A.lcdUnitString")); // NOI18N
        rpm1A.setLcdValue(3000.0);

        amps1A.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        amps1A.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        amps1A.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.amps1A.lcdUnitString")); // NOI18N
        amps1A.setLcdValue(1600.7);

        flow1A.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        flow1A.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        flow1A.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.flow1A.lcdUnitString")); // NOI18N
        flow1A.setLcdValue(530.2);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinner1A, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(start1A)
                            .addComponent(stop1A))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(amps1A, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rpm1A, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(flow1A, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 12, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(spinner1A, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(start1A)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stop1A))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(rpm1A, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(amps1A, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(flow1A, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel22.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel43.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel43, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel43.text")); // NOI18N

        buttonGroup4.add(sdvcOpen1);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcOpen1, org.openide.util.NbBundle.getMessage(PCSUI.class, "TGUI.sdvcOpen1.text")); // NOI18N
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

        buttonGroup4.add(sdvcStop1);
        sdvcStop1.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcStop1, org.openide.util.NbBundle.getMessage(PCSUI.class, "TGUI.sdvcStop1.text")); // NOI18N
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

        buttonGroup4.add(sdvcClose1);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcClose1, org.openide.util.NbBundle.getMessage(PCSUI.class, "TGUI.sdvcClose1.text")); // NOI18N
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
        valvePos3.setTitle(org.openide.util.NbBundle.getMessage(PCSUI.class, "TGUI.valvePos3.title")); // NOI18N
        valvePos3.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        valvePos3.setUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "TGUI.valvePos3.unitString")); // NOI18N

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel43)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sdvcOpen1)
                            .addComponent(sdvcClose1)
                            .addComponent(sdvcStop1))
                        .addGap(18, 18, 18)
                        .addComponent(valvePos3, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel43)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addComponent(sdvcOpen1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sdvcStop1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sdvcClose1))
                    .addComponent(valvePos3, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, "PCS Feedwater Inlet\n");

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel29.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jPanel24.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel45.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel45, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel45.text")); // NOI18N

        regen1InTemp1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        regen1InTemp1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        regen1InTemp1.setLcdUnitString("°C     IN");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel26, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel26.text")); // NOI18N

        regen1InTemp2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        regen1InTemp2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        regen1InTemp2.setLcdUnitString("°C     IN");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel27, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel27.text")); // NOI18N

        regen1OutTemp1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        regen1OutTemp1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        regen1OutTemp1.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.regen1OutTemp1.lcdUnitString")); // NOI18N

        regen1OutTemp2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        regen1OutTemp2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        regen1OutTemp2.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.regen1OutTemp2.lcdUnitString")); // NOI18N

        regen1Inflow1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        regen1Inflow1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        regen1Inflow1.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.regen1Inflow1.lcdUnitString")); // NOI18N

        regen1Inflow2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        regen1Inflow2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        regen1Inflow2.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.regen1Inflow2.lcdUnitString")); // NOI18N

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel26)
                        .addGap(55, 55, 55))
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel45)
                            .addGroup(jPanel24Layout.createSequentialGroup()
                                .addComponent(regen1OutTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(regen1OutTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel24Layout.createSequentialGroup()
                                .addComponent(regen1InTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(regen1InTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel24Layout.createSequentialGroup()
                                .addComponent(regen1Inflow1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(regen1Inflow2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel45)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regen1InTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regen1InTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regen1OutTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regen1OutTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regen1Inflow2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regen1Inflow1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel25.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel46.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel46, "Regenerator 2");

        regen2InTemp1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        regen2InTemp1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        regen2InTemp1.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.regen2InTemp1.lcdUnitString")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel28, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel28.text")); // NOI18N

        regen2InTemp2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        regen2InTemp2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        regen2InTemp2.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.regen2InTemp2.lcdUnitString")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel29, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel29.text")); // NOI18N

        regen2OutTemp1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        regen2OutTemp1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        regen2OutTemp1.setLcdUnitString("°C OUT");

        regen2OutTemp2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        regen2OutTemp2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        regen2OutTemp2.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.regen2OutTemp2.lcdUnitString")); // NOI18N

        regen2Inflow1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        regen2Inflow1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        regen2Inflow1.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.regen2Inflow1.lcdUnitString")); // NOI18N

        regen2Inflow2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        regen2Inflow2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        regen2Inflow2.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.regen2Inflow2.lcdUnitString")); // NOI18N

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel28)
                        .addGap(55, 55, 55))
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel46)
                            .addGroup(jPanel25Layout.createSequentialGroup()
                                .addComponent(regen2OutTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(regen2OutTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel25Layout.createSequentialGroup()
                                .addComponent(regen2InTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(regen2InTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel25Layout.createSequentialGroup()
                                .addComponent(regen2Inflow1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(regen2Inflow2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel46)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jLabel29))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regen2InTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regen2InTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regen2OutTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regen2OutTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regen2Inflow2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regen2Inflow1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel25.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel25, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel25.text")); // NOI18N

        buttonGroup2.add(sdvcStop3);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcStop3, "2");
        sdvcStop3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sdvcStop3ItemStateChanged(evt);
            }
        });
        sdvcStop3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdvcStop3ActionPerformed(evt);
            }
        });

        buttonGroup2.add(sdvcClose3);
        sdvcClose3.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcClose3, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.sdvcClose3.text")); // NOI18N
        sdvcClose3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sdvcClose3ItemStateChanged(evt);
            }
        });
        sdvcClose3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdvcClose3ActionPerformed(evt);
            }
        });

        buttonGroup2.add(sdvcOpen3);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcOpen3, "1");
        sdvcOpen3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sdvcOpen3ItemStateChanged(evt);
            }
        });
        sdvcOpen3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdvcOpen3ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, "Regenerators");

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel29Layout.createSequentialGroup()
                        .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel29Layout.createSequentialGroup()
                        .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(jPanel29Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sdvcOpen3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sdvcStop3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sdvcClose3)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel29Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sdvcOpen3)
                    .addComponent(sdvcClose3)
                    .addComponent(sdvcStop3)
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel23.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, "Water Purification System");

        operationalLed.setLedColor(eu.hansolo.steelseries.tools.LedColor.GREEN_LED);
        operationalLed.setPreferredSize(new java.awt.Dimension(24, 24));

        jLabel16.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel16, "Operational");

        jPanel16.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup7.add(filter1In);
        org.openide.awt.Mnemonics.setLocalizedText(filter1In, "In");
        filter1In.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                filter1InItemStateChanged(evt);
            }
        });
        filter1In.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filter1InActionPerformed(evt);
            }
        });

        buttonGroup7.add(filter1Reg);
        filter1Reg.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(filter1Reg, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.filter1Reg.text")); // NOI18N
        filter1Reg.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                filter1RegItemStateChanged(evt);
            }
        });
        filter1Reg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filter1RegActionPerformed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel34, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel34.text")); // NOI18N

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filter1Reg)
                    .addComponent(jLabel34)
                    .addComponent(filter1In))
                .addGap(0, 13, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel34)
                .addGap(5, 5, 5)
                .addComponent(filter1In)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filter1Reg)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel17.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel36.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel36, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel36.text")); // NOI18N

        buttonGroup5.add(regen2);
        org.openide.awt.Mnemonics.setLocalizedText(regen2, "Regenerator 2");
        regen2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                regen2ItemStateChanged(evt);
            }
        });
        regen2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regen2ActionPerformed(evt);
            }
        });

        buttonGroup5.add(regen1);
        org.openide.awt.Mnemonics.setLocalizedText(regen1, "Regenerator 1");
        regen1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                regen1ItemStateChanged(evt);
            }
        });
        regen1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regen1ActionPerformed(evt);
            }
        });

        buttonGroup5.add(regenBoth);
        regenBoth.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(regenBoth, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.regenBoth.text")); // NOI18N
        regenBoth.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                regenBothItemStateChanged(evt);
            }
        });
        regenBoth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regenBothActionPerformed(evt);
            }
        });

        buttonGroup5.add(dwTankButton);
        org.openide.awt.Mnemonics.setLocalizedText(dwTankButton, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.dwTankButton.text")); // NOI18N
        dwTankButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                dwTankButtonItemStateChanged(evt);
            }
        });
        dwTankButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dwTankButtonActionPerformed(evt);
            }
        });

        buttonGroup5.add(bypassButton);
        org.openide.awt.Mnemonics.setLocalizedText(bypassButton, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.bypassButton.text")); // NOI18N
        bypassButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                bypassButtonItemStateChanged(evt);
            }
        });
        bypassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bypassButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(regen2)
                        .addGap(18, 18, 18)
                        .addComponent(bypassButton))
                    .addComponent(regenBoth)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(regen1)
                        .addGap(18, 18, 18)
                        .addComponent(dwTankButton))
                    .addComponent(jLabel36))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(regen1)
                    .addComponent(dwTankButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(regen2)
                    .addComponent(bypassButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regenBoth)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        filterInletTemp.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        filterInletTemp.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        filterInletTemp.setLcdUnitString("°C");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, "Inlet Water Temp");

        jPanel18.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup6.add(filter2In);
        filter2In.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(filter2In, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.filter2In.text")); // NOI18N
        filter2In.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                filter2InItemStateChanged(evt);
            }
        });
        filter2In.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filter2InActionPerformed(evt);
            }
        });

        buttonGroup6.add(filter2Reg);
        org.openide.awt.Mnemonics.setLocalizedText(filter2Reg, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.filter2Reg.text")); // NOI18N
        filter2Reg.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                filter2RegItemStateChanged(evt);
            }
        });
        filter2Reg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filter2RegActionPerformed(evt);
            }
        });

        jLabel37.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel37, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel37.text")); // NOI18N

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filter2Reg)
                    .addComponent(jLabel37)
                    .addComponent(filter2In))
                .addGap(0, 13, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel37)
                .addGap(5, 5, 5)
                .addComponent(filter2In)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filter2Reg)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(operationalLed, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(filterInletTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(jLabel16))
                    .addComponent(operationalLed, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(filterInletTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabel5)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPanel33.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel52.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel52, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel52.text")); // NOI18N

        dwTank.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.NOISY_PLASTIC);
        dwTank.setBarGraphColor(eu.hansolo.steelseries.tools.ColorDef.CYAN);
        dwTank.setFrameVisible(false);
        dwTank.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        dwTank.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.dwTank.lcdUnitString")); // NOI18N
        dwTank.setLcdUnitStringVisible(true);
        dwTank.setLcdVisible(false);
        dwTank.setLedVisible(false);
        dwTank.setMaxNoOfMajorTicks(5);
        dwTank.setMaxValue(1500.0);
        dwTank.setTitle(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.dwTank.title")); // NOI18N
        dwTank.setTitleVisible(false);
        dwTank.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        dwTank.setTrackStartColor(new java.awt.Color(255, 0, 0));
        dwTank.setTrackStop(1000.0);
        dwTank.setTrackVisible(true);
        dwTank.setValue(1000.0);

        dwInflow.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        dwInflow.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        dwInflow.setLcdUnitString("kg/s");

        dwOutflow.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        dwOutflow.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        dwOutflow.setLcdUnitString("kg/s");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel8.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, "Temp");

        dwTemp.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        dwTemp.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        dwTemp.setLcdUnitString("°C");

        buttonGroup12.add(start1A3);
        org.openide.awt.Mnemonics.setLocalizedText(start1A3, "Start");
        start1A3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                start1A3ItemStateChanged(evt);
            }
        });
        start1A3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                start1A3ActionPerformed(evt);
            }
        });

        buttonGroup12.add(stop1A3);
        stop1A3.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(stop1A3, "Stop");
        stop1A3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                stop1A3ItemStateChanged(evt);
            }
        });
        stop1A3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stop1A3ActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, "DW Makeup");

        javax.swing.GroupLayout jPanel33Layout = new javax.swing.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(dwTank, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel33Layout.createSequentialGroup()
                                .addGap(38, 38, 38)
                                .addComponent(jLabel7))
                            .addGroup(jPanel33Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dwInflow, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dwOutflow, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel33Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(jLabel8))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel33Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dwTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel33Layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(jLabel9))
                            .addGroup(jPanel33Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(start1A3)
                                    .addComponent(stop1A3)
                                    .addComponent(jLabel12)))))
                    .addComponent(jLabel52))
                .addContainerGap())
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel52)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dwTank, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dwInflow, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8)
                        .addGap(5, 5, 5)
                        .addComponent(dwOutflow, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9)
                        .addGap(5, 5, 5)
                        .addComponent(dwTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(start1A3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stop1A3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel30.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel10.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, "Auxiliary Dearator Makeup System");

        jPanel11.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        buttonGroup10.add(start1A1);
        org.openide.awt.Mnemonics.setLocalizedText(start1A1, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.start1A1.text")); // NOI18N
        start1A1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                start1A1ItemStateChanged(evt);
            }
        });
        start1A1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                start1A1ActionPerformed(evt);
            }
        });

        buttonGroup10.add(stop1A1);
        stop1A1.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(stop1A1, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.stop1A1.text")); // NOI18N
        stop1A1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                stop1A1ItemStateChanged(evt);
            }
        });
        stop1A1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stop1A1ActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel21, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel21.text")); // NOI18N

        spinner1A1.setModel(new javax.swing.SpinnerNumberModel(1, 1, 4, 1));
        spinner1A1.setDoubleBuffered(true);

        jLabel22.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel22, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel22.text")); // NOI18N

        rpm1A1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        rpm1A1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        rpm1A1.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.rpm1A1.lcdUnitString")); // NOI18N
        rpm1A1.setLcdValue(3000.0);

        amps1A1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        amps1A1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        amps1A1.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.amps1A1.lcdUnitString")); // NOI18N
        amps1A1.setLcdValue(1600.7);

        flow1A1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        flow1A1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        flow1A1.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.flow1A1.lcdUnitString")); // NOI18N
        flow1A1.setLcdValue(530.2);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinner1A1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(start1A1)
                            .addComponent(stop1A1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(amps1A1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rpm1A1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(flow1A1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 12, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(spinner1A1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(start1A1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stop1A1))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(rpm1A1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(amps1A1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(flow1A1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        spinner1A2.setModel(new javax.swing.SpinnerNumberModel(1, 1, 4, 1));
        spinner1A2.setDoubleBuffered(true);

        jLabel35.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel35, "Valve Selector");

        jLabel38.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel38, "Makeup Valve Control");

        buttonGroup11.add(sdvcOpen5);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcOpen5, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.sdvcOpen5.text")); // NOI18N
        sdvcOpen5.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sdvcOpen5ItemStateChanged(evt);
            }
        });
        sdvcOpen5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdvcOpen5ActionPerformed(evt);
            }
        });

        buttonGroup11.add(sdvcStop5);
        sdvcStop5.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcStop5, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.sdvcStop5.text")); // NOI18N
        sdvcStop5.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sdvcStop5ItemStateChanged(evt);
            }
        });
        sdvcStop5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdvcStop5ActionPerformed(evt);
            }
        });

        buttonGroup11.add(sdvcClose5);
        org.openide.awt.Mnemonics.setLocalizedText(sdvcClose5, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.sdvcClose5.text")); // NOI18N
        sdvcClose5.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sdvcClose5ItemStateChanged(evt);
            }
        });
        sdvcClose5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdvcClose5ActionPerformed(evt);
            }
        });

        flow1A4.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        flow1A4.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        flow1A4.setLcdUnitString(org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.flow1A4.lcdUnitString")); // NOI18N
        flow1A4.setLcdValue(530.2);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jLabel11.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(admsAC, "Auto-Control");
        admsAC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                admsACActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel30Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel38)
                        .addGroup(jPanel30Layout.createSequentialGroup()
                            .addComponent(sdvcOpen5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(admsAC))
                        .addComponent(sdvcStop5)
                        .addComponent(sdvcClose5)
                        .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11)
                            .addGroup(jPanel30Layout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinner1A2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(flow1A4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel30Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel35)
                            .addComponent(spinner1A2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sdvcOpen5)
                            .addComponent(admsAC))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel30Layout.createSequentialGroup()
                                .addComponent(sdvcStop5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sdvcClose5)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel30Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(flow1A4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(33, 33, 33))))))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(annunciatorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(annunciatorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(122, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel3);

        org.openide.awt.Mnemonics.setLocalizedText(jMenu1, "Window");

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem5, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jMenuItem5.text")); // NOI18N
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

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem10, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jMenuItem10.text")); // NOI18N
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem10);

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem12, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jMenuItem12.text")); // NOI18N
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem12);

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem4, org.openide.util.NbBundle.getMessage(PCSUI.class, "PCSUI.jMenuItem4.text")); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

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

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem8, org.openide.util.NbBundle.getMessage(PCSUI.class, "TGUI.jMenuItem8.text")); // NOI18N
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem8);

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

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        UI.createOrContinue(CoreMap.class, false, false);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void waterTempActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterTempActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterTempActionPerformed

    private void coolingCavitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coolingCavitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_coolingCavitActionPerformed

    private void dwLowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dwLowActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dwLowActionPerformed

    private void dwHighActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dwHighActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dwHighActionPerformed

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
        pcs.pcsValve.setState(2);
    }//GEN-LAST:event_sdvcOpen1ActionPerformed

    private void sdvcClose1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcClose1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcClose1ItemStateChanged

    private void sdvcClose1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcClose1ActionPerformed
        pcs.pcsValve.setState(0);
    }//GEN-LAST:event_sdvcClose1ActionPerformed

    private void sdvcStop1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcStop1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcStop1ItemStateChanged

    private void sdvcStop1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcStop1ActionPerformed
        pcs.pcsValve.setState(1);
    }//GEN-LAST:event_sdvcStop1ActionPerformed

    private void start1AItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_start1AItemStateChanged
        //TODO
    }//GEN-LAST:event_start1AItemStateChanged

    private void start1AActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_start1AActionPerformed
        if ((int)spinner1A.getValue() == 1) {
            pcs.pcsPump1.setActive(true);
        } else {
            pcs.pcsPump2.setActive(true);
        }
    }//GEN-LAST:event_start1AActionPerformed

    private void stop1AItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_stop1AItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_stop1AItemStateChanged

    private void stop1AActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stop1AActionPerformed
        if ((int)spinner1A.getValue() == 1) {
           pcs.pcsPump1.setActive(false);
        } else {
            pcs.pcsPump2.setActive(false);
        }
    }//GEN-LAST:event_stop1AActionPerformed

    private void start1A2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_start1A2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_start1A2ItemStateChanged

    private void start1A2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_start1A2ActionPerformed
       pcs.coolingPump.setActive(true);
    }//GEN-LAST:event_start1A2ActionPerformed

    private void stop1A2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_stop1A2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_stop1A2ItemStateChanged

    private void stop1A2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stop1A2ActionPerformed
      pcs.coolingPump.setActive(false);
    }//GEN-LAST:event_stop1A2ActionPerformed

    private void sdvcOpen2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcOpen2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcOpen2ItemStateChanged

    private void sdvcOpen2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcOpen2ActionPerformed
        pcs.pcsCoolingHeader.isolationValveArray.get(0).setState(2);
        pcs.pcsCoolingHeader.isolationValveArray.get(1).setState(0);
    }//GEN-LAST:event_sdvcOpen2ActionPerformed

    private void sdvcStop2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcStop2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcStop2ItemStateChanged

    private void sdvcStop2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcStop2ActionPerformed
        pcs.pcsCoolingHeader.isolationValveArray.get(0).setState(0);
        pcs.pcsCoolingHeader.isolationValveArray.get(1).setState(2);
    }//GEN-LAST:event_sdvcStop2ActionPerformed

    private void sdvcClose2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcClose2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcClose2ItemStateChanged

    private void sdvcClose2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcClose2ActionPerformed
        pcs.pcsCoolingHeader.isolationValveArray.get(0).setState(2);
        pcs.pcsCoolingHeader.isolationValveArray.get(1).setState(2);
    }//GEN-LAST:event_sdvcClose2ActionPerformed

    private void sdvcStop3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcStop3ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcStop3ItemStateChanged

    private void sdvcStop3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcStop3ActionPerformed
        pcs.pcsPressureHeader.isolationValveArray.get(0).setState(0);
        pcs.pcsPressureHeader.isolationValveArray.get(1).setState(2);
    }//GEN-LAST:event_sdvcStop3ActionPerformed

    private void sdvcClose3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcClose3ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcClose3ItemStateChanged

    private void sdvcClose3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcClose3ActionPerformed
        pcs.pcsPressureHeader.isolationValveArray.get(0).setState(2);
        pcs.pcsPressureHeader.isolationValveArray.get(1).setState(2);
    }//GEN-LAST:event_sdvcClose3ActionPerformed

    private void sdvcOpen3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcOpen3ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcOpen3ItemStateChanged

    private void sdvcOpen3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcOpen3ActionPerformed
        pcs.pcsPressureHeader.isolationValveArray.get(0).setState(2);
        pcs.pcsPressureHeader.isolationValveArray.get(1).setState(0);
    }//GEN-LAST:event_sdvcOpen3ActionPerformed

    private void sdvcOpen4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcOpen4ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcOpen4ItemStateChanged

    private void sdvcOpen4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcOpen4ActionPerformed
        pcs.coolingPump.outletValve.setState(2);
    }//GEN-LAST:event_sdvcOpen4ActionPerformed

    private void sdvcStop4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcStop4ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcStop4ItemStateChanged

    private void sdvcStop4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcStop4ActionPerformed
        pcs.coolingPump.outletValve.setState(1);
    }//GEN-LAST:event_sdvcStop4ActionPerformed

    private void sdvcClose4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcClose4ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcClose4ItemStateChanged

    private void sdvcClose4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcClose4ActionPerformed
        pcs.coolingPump.outletValve.setState(0);
    }//GEN-LAST:event_sdvcClose4ActionPerformed

    private void filter1InItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_filter1InItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_filter1InItemStateChanged

    private void filter1InActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filter1InActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filter1InActionPerformed

    private void filter1RegItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_filter1RegItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_filter1RegItemStateChanged

    private void filter1RegActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filter1RegActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filter1RegActionPerformed

    private void regen2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_regen2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_regen2ItemStateChanged

    private void regen2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regen2ActionPerformed
        pcs.pcsFilterPressureHeader.isolationValveArray.get(0).setState(0);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(1).setState(2);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(2).setState(0);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(3).setState(0);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(4).setState(0);
    }//GEN-LAST:event_regen2ActionPerformed

    private void regenBothItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_regenBothItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_regenBothItemStateChanged

    private void regenBothActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regenBothActionPerformed
        pcs.pcsFilterPressureHeader.isolationValveArray.get(0).setState(2);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(1).setState(2);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(2).setState(0);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(3).setState(0);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(4).setState(0);
    }//GEN-LAST:event_regenBothActionPerformed

    private void regen1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_regen1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_regen1ItemStateChanged

    private void regen1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regen1ActionPerformed
        pcs.pcsFilterPressureHeader.isolationValveArray.get(0).setState(2);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(1).setState(0);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(2).setState(0);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(3).setState(0);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(4).setState(0);
    }//GEN-LAST:event_regen1ActionPerformed

    private void filter2InItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_filter2InItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_filter2InItemStateChanged

    private void filter2InActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filter2InActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filter2InActionPerformed

    private void filter2RegItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_filter2RegItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_filter2RegItemStateChanged

    private void filter2RegActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filter2RegActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filter2RegActionPerformed

    private void dwTankButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_dwTankButtonItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_dwTankButtonItemStateChanged

    private void dwTankButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dwTankButtonActionPerformed
        pcs.pcsFilterPressureHeader.isolationValveArray.get(0).setState(0);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(1).setState(0);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(2).setState(2);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(3).setState(0);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(4).setState(0);
    }//GEN-LAST:event_dwTankButtonActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        UI.createOrContinue(TGUI.class, true, false);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void dwTankTempActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dwTankTempActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dwTankTempActionPerformed

    private void bypassButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_bypassButtonItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_bypassButtonItemStateChanged

    private void bypassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bypassButtonActionPerformed
        pcs.pcsFilterPressureHeader.isolationValveArray.get(0).setState(0);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(1).setState(0);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(2).setState(0);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(3).setState(2);
        pcs.pcsFilterPressureHeader.isolationValveArray.get(4).setState(2);
    }//GEN-LAST:event_bypassButtonActionPerformed

    private void start1A1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_start1A1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_start1A1ItemStateChanged

    private void start1A1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_start1A1ActionPerformed
        pcs.admsPumps.get((int)spinner1A1.getValue() - 1).setActive(true);
    }//GEN-LAST:event_start1A1ActionPerformed

    private void stop1A1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_stop1A1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_stop1A1ItemStateChanged

    private void stop1A1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stop1A1ActionPerformed
        pcs.admsPumps.get((int)spinner1A1.getValue() - 1).setActive(false);
    }//GEN-LAST:event_stop1A1ActionPerformed

    private void sdvcOpen5ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcOpen5ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcOpen5ItemStateChanged

    private void sdvcOpen5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcOpen5ActionPerformed
        pcs.dearatorMakeupValves.get((int)spinner1A2.getValue() - 1).setState(2);
    }//GEN-LAST:event_sdvcOpen5ActionPerformed

    private void sdvcStop5ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcStop5ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcStop5ItemStateChanged

    private void sdvcStop5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcStop5ActionPerformed
        pcs.dearatorMakeupValves.get((int)spinner1A2.getValue() - 1).setState(1);
    }//GEN-LAST:event_sdvcStop5ActionPerformed

    private void sdvcClose5ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sdvcClose5ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sdvcClose5ItemStateChanged

    private void sdvcClose5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdvcClose5ActionPerformed
        pcs.dearatorMakeupValves.get((int)spinner1A2.getValue() - 1).setState(0);
    }//GEN-LAST:event_sdvcClose5ActionPerformed

    private void admsACActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_admsACActionPerformed
        if (admsAC.isSelected()) {
            if (autoControl.dearatorWaterControl.get((int)spinner1A2.getValue() -1 ).isEnabled()) {
                autoControl.dearatorWaterControl.get((int)spinner1A2.getValue() -1 ).setEnabled(false);
                autoControl.dearatorWaterAndMakeupControl.get((int)spinner1A2.getValue() - 1).setEnabled(true);
            } else {
                autoControl.dearatorMakeupControl.get((int)spinner1A2.getValue() - 1).setEnabled(true);
            }
        } else {
            if (autoControl.dearatorWaterAndMakeupControl.get((int)spinner1A2.getValue() - 1).isEnabled()) {
                autoControl.dearatorWaterAndMakeupControl.get((int)spinner1A2.getValue() -1 ).setEnabled(false);
                autoControl.dearatorWaterControl.get((int)spinner1A2.getValue() - 1).setEnabled(true);
            } else {
                autoControl.dearatorMakeupControl.get((int)spinner1A2.getValue() - 1).setEnabled(false);
            }
        }
        //autoControl.dearatorMakeupControl.get((int)spinner1A2.getValue() - 1).setEnabled(admsAC.isSelected());
    }//GEN-LAST:event_admsACActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        UI.createOrContinue(SelsynPanel.class, false, false);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        NPPSim.ui.toFront();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void start1A3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_start1A3ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_start1A3ItemStateChanged

    private void start1A3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_start1A3ActionPerformed
        pcs.dwMakeupPump.setActive(true);
    }//GEN-LAST:event_start1A3ActionPerformed

    private void stop1A3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_stop1A3ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_stop1A3ItemStateChanged

    private void stop1A3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stop1A3ActionPerformed
        pcs.dwMakeupPump.setActive(false);
    }//GEN-LAST:event_stop1A3ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        UI.createOrContinue(MCPUI.class, true, false);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField INOP;
    private javax.swing.JCheckBox admsAC;
    private eu.hansolo.steelseries.gauges.DisplaySingle amps1A;
    private eu.hansolo.steelseries.gauges.DisplaySingle amps1A1;
    private eu.hansolo.steelseries.gauges.DisplaySingle amps1A2;
    private javax.swing.JPanel annunciatorPanel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup10;
    private javax.swing.ButtonGroup buttonGroup11;
    private javax.swing.ButtonGroup buttonGroup12;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.ButtonGroup buttonGroup6;
    private javax.swing.ButtonGroup buttonGroup7;
    private javax.swing.ButtonGroup buttonGroup8;
    private javax.swing.ButtonGroup buttonGroup9;
    private javax.swing.JRadioButton bypassButton;
    private javax.swing.JTextField circCavit;
    private eu.hansolo.steelseries.gauges.DisplaySingle cooler1InTemp1;
    private eu.hansolo.steelseries.gauges.DisplaySingle cooler1InTemp2;
    private eu.hansolo.steelseries.gauges.DisplaySingle cooler1Inflow1;
    private eu.hansolo.steelseries.gauges.DisplaySingle cooler1Inflow2;
    private eu.hansolo.steelseries.gauges.DisplaySingle cooler1OutTemp1;
    private eu.hansolo.steelseries.gauges.DisplaySingle cooler1OutTemp2;
    private eu.hansolo.steelseries.gauges.DisplaySingle cooler2InTemp1;
    private eu.hansolo.steelseries.gauges.DisplaySingle cooler2InTemp2;
    private eu.hansolo.steelseries.gauges.DisplaySingle cooler2Inflow1;
    private eu.hansolo.steelseries.gauges.DisplaySingle cooler2Inflow2;
    private eu.hansolo.steelseries.gauges.DisplaySingle cooler2OutTemp1;
    private eu.hansolo.steelseries.gauges.DisplaySingle cooler2OutTemp2;
    private javax.swing.JTextField coolingCavit;
    private javax.swing.JTextField dwHigh;
    private eu.hansolo.steelseries.gauges.DisplaySingle dwInflow;
    private javax.swing.JTextField dwLow;
    private eu.hansolo.steelseries.gauges.DisplaySingle dwOutflow;
    private eu.hansolo.steelseries.gauges.LinearBargraph dwTank;
    private javax.swing.JRadioButton dwTankButton;
    private javax.swing.JTextField dwTankTemp;
    private eu.hansolo.steelseries.gauges.DisplaySingle dwTemp;
    private javax.swing.JRadioButton filter1In;
    private javax.swing.JRadioButton filter1Reg;
    private javax.swing.JRadioButton filter2In;
    private javax.swing.JRadioButton filter2Reg;
    private eu.hansolo.steelseries.gauges.DisplaySingle filterInletTemp;
    private eu.hansolo.steelseries.gauges.DisplaySingle flow1A;
    private eu.hansolo.steelseries.gauges.DisplaySingle flow1A1;
    private eu.hansolo.steelseries.gauges.DisplaySingle flow1A2;
    private eu.hansolo.steelseries.gauges.DisplaySingle flow1A4;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel16;
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
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel52;
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
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JScrollPane jScrollPane1;
    private eu.hansolo.steelseries.extras.Led operationalLed;
    private javax.swing.JRadioButton regen1;
    private eu.hansolo.steelseries.gauges.DisplaySingle regen1InTemp1;
    private eu.hansolo.steelseries.gauges.DisplaySingle regen1InTemp2;
    private eu.hansolo.steelseries.gauges.DisplaySingle regen1Inflow1;
    private eu.hansolo.steelseries.gauges.DisplaySingle regen1Inflow2;
    private eu.hansolo.steelseries.gauges.DisplaySingle regen1OutTemp1;
    private eu.hansolo.steelseries.gauges.DisplaySingle regen1OutTemp2;
    private javax.swing.JRadioButton regen2;
    private eu.hansolo.steelseries.gauges.DisplaySingle regen2InTemp1;
    private eu.hansolo.steelseries.gauges.DisplaySingle regen2InTemp2;
    private eu.hansolo.steelseries.gauges.DisplaySingle regen2Inflow1;
    private eu.hansolo.steelseries.gauges.DisplaySingle regen2Inflow2;
    private eu.hansolo.steelseries.gauges.DisplaySingle regen2OutTemp1;
    private eu.hansolo.steelseries.gauges.DisplaySingle regen2OutTemp2;
    private javax.swing.JRadioButton regenBoth;
    private eu.hansolo.steelseries.gauges.DisplaySingle rpm1A;
    private eu.hansolo.steelseries.gauges.DisplaySingle rpm1A1;
    private eu.hansolo.steelseries.gauges.DisplaySingle rpm1A2;
    private javax.swing.JRadioButton sdvcClose1;
    private javax.swing.JRadioButton sdvcClose2;
    private javax.swing.JRadioButton sdvcClose3;
    private javax.swing.JRadioButton sdvcClose4;
    private javax.swing.JRadioButton sdvcClose5;
    private javax.swing.JRadioButton sdvcOpen1;
    private javax.swing.JRadioButton sdvcOpen2;
    private javax.swing.JRadioButton sdvcOpen3;
    private javax.swing.JRadioButton sdvcOpen4;
    private javax.swing.JRadioButton sdvcOpen5;
    private javax.swing.JRadioButton sdvcStop1;
    private javax.swing.JRadioButton sdvcStop2;
    private javax.swing.JRadioButton sdvcStop3;
    private javax.swing.JRadioButton sdvcStop4;
    private javax.swing.JRadioButton sdvcStop5;
    private javax.swing.JSpinner spinner1A;
    private javax.swing.JSpinner spinner1A1;
    private javax.swing.JSpinner spinner1A2;
    private javax.swing.JRadioButton start1A;
    private javax.swing.JRadioButton start1A1;
    private javax.swing.JRadioButton start1A2;
    private javax.swing.JRadioButton start1A3;
    private javax.swing.JRadioButton stop1A;
    private javax.swing.JRadioButton stop1A1;
    private javax.swing.JRadioButton stop1A2;
    private javax.swing.JRadioButton stop1A3;
    private eu.hansolo.steelseries.gauges.RadialBargraph valvePos3;
    private eu.hansolo.steelseries.gauges.RadialBargraph valvePos4;
    private javax.swing.JTextField waterTemp;
    // End of variables declaration//GEN-END:variables
}
