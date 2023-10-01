package com.darwish.nppsim;

import static com.darwish.nppsim.NPPSim.mcc;


public class MCPUI extends javax.swing.JFrame implements UIUpdateable {
    private final Annunciator annunciator;
    Pump[] mcpArray;
    /**
     * Creates new form MCPUI
     */
    public MCPUI() {
        initComponents();
        this.setTitle("MCP Controls");
        annunciator = new Annunciator(annunciatorPanel);
        mcpArray = new Pump[] {mcc.mcp.get(0), mcc.mcp.get(1), mcc.mcp.get(2), mcc.mcp.get(3), mcc.mcp.get(4), mcc.mcp.get(5), mcc.mcp.get(6), mcc.mcp.get(7)};
        led1.setLedOn(mcpArray[0].isActive());
        led2.setLedOn(mcpArray[1].isActive());
        led3.setLedOn(mcpArray[2].isActive());
        led4.setLedOn(mcpArray[3].isActive());
        led5.setLedOn(mcpArray[4].isActive());
        led6.setLedOn(mcpArray[5].isActive());
        led7.setLedOn(mcpArray[6].isActive());
        led8.setLedOn(mcpArray[7].isActive());
        initializeDialUpdateThread();
        precisionController();
    }
    
    @Override
    public void update() {
        checkAlarms();
        if (this.isVisible()) {
            java.awt.EventQueue.invokeLater(() -> {

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
                                flow1.setValue(mcpArray[0].getFlow());
                                flow2.setValue(mcpArray[1].getFlow());
                                flow3.setValue(mcpArray[2].getFlow());
                                flow4.setValue(mcpArray[3].getFlow());
                                flow5.setValue(mcpArray[4].getFlow());
                                flow6.setValue(mcpArray[5].getFlow());
                                flow7.setValue(mcpArray[6].getFlow());
                                flow8.setValue(mcpArray[7].getFlow());
                                amps1.setValue(mcpArray[0].getPowerUsage());
                                amps2.setValue(mcpArray[1].getPowerUsage());
                                amps3.setValue(mcpArray[2].getPowerUsage());
                                amps4.setValue(mcpArray[3].getPowerUsage());
                                amps5.setValue(mcpArray[4].getPowerUsage());
                                amps6.setValue(mcpArray[5].getPowerUsage());
                                amps7.setValue(mcpArray[6].getPowerUsage());
                                amps8.setValue(mcpArray[7].getPowerUsage());
                                rpm1.setValue(mcpArray[0].getRPM());
                                rpm2.setValue(mcpArray[1].getRPM());
                                rpm3.setValue(mcpArray[2].getRPM());
                                rpm4.setValue(mcpArray[3].getRPM());
                                rpm5.setValue(mcpArray[4].getRPM());
                                rpm6.setValue(mcpArray[5].getRPM());
                                rpm7.setValue(mcpArray[6].getRPM());
                                rpm8.setValue(mcpArray[7].getRPM());
                                pos1.setValue(mcpArray[0].dischargeValve.getPosition() * 100);
                                pos2.setValue(mcpArray[1].dischargeValve.getPosition() * 100);
                                pos3.setValue(mcpArray[2].dischargeValve.getPosition() * 100);
                                pos4.setValue(mcpArray[3].dischargeValve.getPosition() * 100);
                                pos5.setValue(mcpArray[4].dischargeValve.getPosition() * 100);
                                pos6.setValue(mcpArray[5].dischargeValve.getPosition() * 100);
                                pos7.setValue(mcpArray[6].dischargeValve.getPosition() * 100);
                                pos8.setValue(mcpArray[7].dischargeValve.getPosition() * 100);
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
    
    private void precisionController() {
        final float increment = mcpArray[0].accelerationSpeed / mcpArray[0].ratedRPM * 1.2f;
        UI.uiThreads.add(
            new Thread(() -> {
                while(true) {
                    try {
                        if (precisionIncrement1.getModel().isPressed()) {
                            mcc.mcp.get(0).setSetPoint(mcpArray[0].getSetPoint() + increment);
                        } else if (precisionIncrement2.getModel().isPressed()) {
                            mcc.mcp.get(1).setSetPoint(mcpArray[1].getSetPoint() + increment);
                        } else if (precisionIncrement3.getModel().isPressed()) {
                            mcc.mcp.get(2).setSetPoint(mcpArray[2].getSetPoint() + increment);
                        } else if (precisionIncrement4.getModel().isPressed()) {
                            mcc.mcp.get(3).setSetPoint(mcpArray[3].getSetPoint() + increment);
                        } else if (precisionIncrement5.getModel().isPressed()) {
                            mcc.mcp.get(4).setSetPoint(mcpArray[4].getSetPoint() + increment);
                        } else if (precisionIncrement6.getModel().isPressed()) {
                            mcc.mcp.get(5).setSetPoint(mcpArray[5].getSetPoint() + increment);
                        } else if (precisionIncrement7.getModel().isPressed()) {
                            mcc.mcp.get(6).setSetPoint(mcpArray[6].getSetPoint() + increment);
                        } else if (precisionIncrement8.getModel().isPressed()) {
                            mcc.mcp.get(7).setSetPoint(mcpArray[7].getSetPoint() + increment);
                        } else if (precisionDecrement1.getModel().isPressed()) {
                            mcc.mcp.get(0).setSetPoint(mcpArray[0].getSetPoint() - increment);
                        } else if (precisionDecrement2.getModel().isPressed()) {
                            mcc.mcp.get(1).setSetPoint(mcpArray[1].getSetPoint() - increment);
                        } else if (precisionDecrement3.getModel().isPressed()) {
                            mcc.mcp.get(2).setSetPoint(mcpArray[2].getSetPoint() - increment);
                        } else if (precisionDecrement4.getModel().isPressed()) {
                            mcc.mcp.get(3).setSetPoint(mcpArray[3].getSetPoint() - increment);
                        } else if (precisionDecrement5.getModel().isPressed()) {
                            mcc.mcp.get(4).setSetPoint(mcpArray[4].getSetPoint() - increment);
                        } else if (precisionDecrement6.getModel().isPressed()) {
                            mcc.mcp.get(5).setSetPoint(mcpArray[5].getSetPoint() - increment);
                        } else if (precisionDecrement7.getModel().isPressed()) {
                            mcc.mcp.get(6).setSetPoint(mcpArray[6].getSetPoint() - increment);
                        } else if (precisionDecrement8.getModel().isPressed()) {
                            mcc.mcp.get(7).setSetPoint(mcpArray[7].getSetPoint() - increment);
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
        annunciator.setTrigger(mcpArray[0].isCavitating, cavit1);
        annunciator.setTrigger(mcpArray[1].isCavitating, cavit2);
        annunciator.setTrigger(mcpArray[2].isCavitating, cavit3);
        annunciator.setTrigger(mcpArray[3].isCavitating, cavit4);
        annunciator.setTrigger(mcpArray[4].isCavitating, cavit5);
        annunciator.setTrigger(mcpArray[5].isCavitating, cavit6);
        annunciator.setTrigger(mcpArray[6].isCavitating, cavit7);
        annunciator.setTrigger(mcpArray[7].isCavitating, cavit8);
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
        annunciatorPanel = new javax.swing.JPanel();
        trip1 = new javax.swing.JTextField();
        cavit1 = new javax.swing.JTextField();
        waterTemp2 = new javax.swing.JTextField();
        trip5 = new javax.swing.JTextField();
        cavit5 = new javax.swing.JTextField();
        waterTemp10 = new javax.swing.JTextField();
        trip2 = new javax.swing.JTextField();
        cavit2 = new javax.swing.JTextField();
        waterTemp12 = new javax.swing.JTextField();
        trip6 = new javax.swing.JTextField();
        cavit6 = new javax.swing.JTextField();
        waterTemp3 = new javax.swing.JTextField();
        trip3 = new javax.swing.JTextField();
        cavit3 = new javax.swing.JTextField();
        waterTemp11 = new javax.swing.JTextField();
        trip7 = new javax.swing.JTextField();
        cavit7 = new javax.swing.JTextField();
        waterTemp17 = new javax.swing.JTextField();
        trip4 = new javax.swing.JTextField();
        cavit4 = new javax.swing.JTextField();
        waterTemp19 = new javax.swing.JTextField();
        trip8 = new javax.swing.JTextField();
        cavit8 = new javax.swing.JTextField();
        waterTemp23 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        flow1 = new eu.hansolo.steelseries.gauges.Linear();
        flow2 = new eu.hansolo.steelseries.gauges.Linear();
        flow3 = new eu.hansolo.steelseries.gauges.Linear();
        flow4 = new eu.hansolo.steelseries.gauges.Linear();
        amps1 = new eu.hansolo.steelseries.gauges.Linear();
        amps2 = new eu.hansolo.steelseries.gauges.Linear();
        amps3 = new eu.hansolo.steelseries.gauges.Linear();
        amps4 = new eu.hansolo.steelseries.gauges.Linear();
        flow8 = new eu.hansolo.steelseries.gauges.Linear();
        amps8 = new eu.hansolo.steelseries.gauges.Linear();
        amps7 = new eu.hansolo.steelseries.gauges.Linear();
        flow7 = new eu.hansolo.steelseries.gauges.Linear();
        flow6 = new eu.hansolo.steelseries.gauges.Linear();
        flow5 = new eu.hansolo.steelseries.gauges.Linear();
        amps5 = new eu.hansolo.steelseries.gauges.Linear();
        amps6 = new eu.hansolo.steelseries.gauges.Linear();
        jPanel35 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jPanel34 = new javax.swing.JPanel();
        pos1 = new eu.hansolo.steelseries.gauges.Radial2Top();
        tgValvesOpen11 = new javax.swing.JRadioButton();
        tgValvesStop11 = new javax.swing.JRadioButton();
        tgValvesClose11 = new javax.swing.JRadioButton();
        jLabel18 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        pos2 = new eu.hansolo.steelseries.gauges.Radial2Top();
        tgValvesOpen12 = new javax.swing.JRadioButton();
        tgValvesStop12 = new javax.swing.JRadioButton();
        tgValvesClose12 = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        pos4 = new eu.hansolo.steelseries.gauges.Radial2Top();
        tgValvesOpen14 = new javax.swing.JRadioButton();
        tgValvesStop14 = new javax.swing.JRadioButton();
        tgValvesClose14 = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        pos3 = new eu.hansolo.steelseries.gauges.Radial2Top();
        tgValvesOpen3 = new javax.swing.JRadioButton();
        tgValvesStop3 = new javax.swing.JRadioButton();
        tgValvesClose3 = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        pos6 = new eu.hansolo.steelseries.gauges.Radial2Top();
        tgValvesOpen6 = new javax.swing.JRadioButton();
        tgValvesStop6 = new javax.swing.JRadioButton();
        tgValvesClose6 = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        pos5 = new eu.hansolo.steelseries.gauges.Radial2Top();
        tgValvesOpen5 = new javax.swing.JRadioButton();
        tgValvesStop5 = new javax.swing.JRadioButton();
        tgValvesClose5 = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        pos7 = new eu.hansolo.steelseries.gauges.Radial2Top();
        tgValvesOpen17 = new javax.swing.JRadioButton();
        tgValvesStop17 = new javax.swing.JRadioButton();
        tgValvesClose17 = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        pos8 = new eu.hansolo.steelseries.gauges.Radial2Top();
        tgValvesOpen18 = new javax.swing.JRadioButton();
        tgValvesStop18 = new javax.swing.JRadioButton();
        tgValvesClose18 = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        rpm1 = new eu.hansolo.steelseries.gauges.Radial();
        jPanel13 = new javax.swing.JPanel();
        rpm2 = new eu.hansolo.steelseries.gauges.Radial();
        jPanel14 = new javax.swing.JPanel();
        rpm5 = new eu.hansolo.steelseries.gauges.Radial();
        jPanel15 = new javax.swing.JPanel();
        rpm6 = new eu.hansolo.steelseries.gauges.Radial();
        jPanel16 = new javax.swing.JPanel();
        precisionIncrement1 = new javax.swing.JButton();
        precisionDecrement1 = new javax.swing.JButton();
        jPanel17 = new javax.swing.JPanel();
        precisionIncrement2 = new javax.swing.JButton();
        precisionDecrement2 = new javax.swing.JButton();
        jPanel18 = new javax.swing.JPanel();
        precisionIncrement5 = new javax.swing.JButton();
        precisionDecrement5 = new javax.swing.JButton();
        jPanel19 = new javax.swing.JPanel();
        precisionIncrement6 = new javax.swing.JButton();
        precisionDecrement6 = new javax.swing.JButton();
        jPanel20 = new javax.swing.JPanel();
        rpm3 = new eu.hansolo.steelseries.gauges.Radial();
        jPanel21 = new javax.swing.JPanel();
        rpm4 = new eu.hansolo.steelseries.gauges.Radial();
        jPanel22 = new javax.swing.JPanel();
        rpm7 = new eu.hansolo.steelseries.gauges.Radial();
        jPanel23 = new javax.swing.JPanel();
        rpm8 = new eu.hansolo.steelseries.gauges.Radial();
        jPanel24 = new javax.swing.JPanel();
        precisionIncrement3 = new javax.swing.JButton();
        precisionDecrement3 = new javax.swing.JButton();
        jPanel25 = new javax.swing.JPanel();
        precisionIncrement4 = new javax.swing.JButton();
        precisionDecrement4 = new javax.swing.JButton();
        jPanel26 = new javax.swing.JPanel();
        precisionIncrement7 = new javax.swing.JButton();
        precisionDecrement7 = new javax.swing.JButton();
        jPanel27 = new javax.swing.JPanel();
        precisionIncrement8 = new javax.swing.JButton();
        precisionDecrement8 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jPanel36 = new javax.swing.JPanel();
        off1 = new javax.swing.JButton();
        on1 = new javax.swing.JButton();
        off2 = new javax.swing.JButton();
        on2 = new javax.swing.JButton();
        on3 = new javax.swing.JButton();
        off3 = new javax.swing.JButton();
        off4 = new javax.swing.JButton();
        on4 = new javax.swing.JButton();
        off5 = new javax.swing.JButton();
        off6 = new javax.swing.JButton();
        off7 = new javax.swing.JButton();
        off8 = new javax.swing.JButton();
        on8 = new javax.swing.JButton();
        on7 = new javax.swing.JButton();
        on6 = new javax.swing.JButton();
        on5 = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        led1 = new eu.hansolo.steelseries.extras.Led();
        led2 = new eu.hansolo.steelseries.extras.Led();
        led3 = new eu.hansolo.steelseries.extras.Led();
        led4 = new eu.hansolo.steelseries.extras.Led();
        led5 = new eu.hansolo.steelseries.extras.Led();
        led6 = new eu.hansolo.steelseries.extras.Led();
        led7 = new eu.hansolo.steelseries.extras.Led();
        led8 = new eu.hansolo.steelseries.extras.Led();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();

        setBackground(new java.awt.Color(204, 0, 153));
        setSize(new java.awt.Dimension(1366, 768));

        jPanel3.setBackground(UI.BACKGROUND);

        annunciatorPanel.setBackground(UI.BACKGROUND);
        annunciatorPanel.setLayout(new java.awt.GridLayout(4, 6));

        trip1.setEditable(false);
        trip1.setBackground(new java.awt.Color(142, 0, 0));
        trip1.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        trip1.setForeground(new java.awt.Color(0, 0, 0));
        trip1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        trip1.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.trip1.text")); // NOI18N
        trip1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        trip1.setFocusable(false);
        trip1.setPreferredSize(new java.awt.Dimension(100, 30));
        trip1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trip1ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(trip1);

        cavit1.setEditable(false);
        cavit1.setBackground(new java.awt.Color(142, 0, 0));
        cavit1.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        cavit1.setForeground(new java.awt.Color(0, 0, 0));
        cavit1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cavit1.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.cavit1.text")); // NOI18N
        cavit1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cavit1.setFocusable(false);
        cavit1.setPreferredSize(new java.awt.Dimension(100, 30));
        cavit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cavit1ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(cavit1);

        waterTemp2.setEditable(false);
        waterTemp2.setBackground(new java.awt.Color(142, 0, 0));
        waterTemp2.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        waterTemp2.setForeground(new java.awt.Color(0, 0, 0));
        waterTemp2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        waterTemp2.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.waterTemp2.text")); // NOI18N
        waterTemp2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        waterTemp2.setFocusable(false);
        waterTemp2.setPreferredSize(new java.awt.Dimension(100, 30));
        waterTemp2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterTemp2ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(waterTemp2);

        trip5.setEditable(false);
        trip5.setBackground(new java.awt.Color(142, 0, 0));
        trip5.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        trip5.setForeground(new java.awt.Color(0, 0, 0));
        trip5.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        trip5.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.trip5.text")); // NOI18N
        trip5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        trip5.setFocusable(false);
        trip5.setPreferredSize(new java.awt.Dimension(100, 30));
        trip5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trip5ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(trip5);

        cavit5.setEditable(false);
        cavit5.setBackground(new java.awt.Color(142, 0, 0));
        cavit5.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        cavit5.setForeground(new java.awt.Color(0, 0, 0));
        cavit5.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cavit5.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.cavit5.text")); // NOI18N
        cavit5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cavit5.setFocusable(false);
        cavit5.setPreferredSize(new java.awt.Dimension(100, 30));
        cavit5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cavit5ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(cavit5);

        waterTemp10.setEditable(false);
        waterTemp10.setBackground(new java.awt.Color(142, 0, 0));
        waterTemp10.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        waterTemp10.setForeground(new java.awt.Color(0, 0, 0));
        waterTemp10.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        waterTemp10.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.waterTemp10.text")); // NOI18N
        waterTemp10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        waterTemp10.setFocusable(false);
        waterTemp10.setPreferredSize(new java.awt.Dimension(100, 30));
        waterTemp10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterTemp10ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(waterTemp10);

        trip2.setEditable(false);
        trip2.setBackground(new java.awt.Color(142, 0, 0));
        trip2.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        trip2.setForeground(new java.awt.Color(0, 0, 0));
        trip2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        trip2.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.trip2.text")); // NOI18N
        trip2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        trip2.setFocusable(false);
        trip2.setPreferredSize(new java.awt.Dimension(100, 30));
        trip2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trip2ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(trip2);

        cavit2.setEditable(false);
        cavit2.setBackground(new java.awt.Color(142, 0, 0));
        cavit2.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        cavit2.setForeground(new java.awt.Color(0, 0, 0));
        cavit2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cavit2.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.cavit2.text")); // NOI18N
        cavit2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cavit2.setFocusable(false);
        cavit2.setPreferredSize(new java.awt.Dimension(100, 30));
        cavit2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cavit2ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(cavit2);

        waterTemp12.setEditable(false);
        waterTemp12.setBackground(new java.awt.Color(142, 0, 0));
        waterTemp12.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        waterTemp12.setForeground(new java.awt.Color(0, 0, 0));
        waterTemp12.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        waterTemp12.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.waterTemp12.text")); // NOI18N
        waterTemp12.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        waterTemp12.setFocusable(false);
        waterTemp12.setPreferredSize(new java.awt.Dimension(100, 30));
        waterTemp12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterTemp12ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(waterTemp12);

        trip6.setEditable(false);
        trip6.setBackground(new java.awt.Color(142, 0, 0));
        trip6.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        trip6.setForeground(new java.awt.Color(0, 0, 0));
        trip6.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        trip6.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.trip6.text")); // NOI18N
        trip6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        trip6.setFocusable(false);
        trip6.setPreferredSize(new java.awt.Dimension(100, 30));
        trip6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trip6ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(trip6);

        cavit6.setEditable(false);
        cavit6.setBackground(new java.awt.Color(142, 0, 0));
        cavit6.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        cavit6.setForeground(new java.awt.Color(0, 0, 0));
        cavit6.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cavit6.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.cavit6.text")); // NOI18N
        cavit6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cavit6.setFocusable(false);
        cavit6.setPreferredSize(new java.awt.Dimension(100, 30));
        cavit6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cavit6ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(cavit6);

        waterTemp3.setEditable(false);
        waterTemp3.setBackground(new java.awt.Color(142, 0, 0));
        waterTemp3.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        waterTemp3.setForeground(new java.awt.Color(0, 0, 0));
        waterTemp3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        waterTemp3.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.waterTemp3.text")); // NOI18N
        waterTemp3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        waterTemp3.setFocusable(false);
        waterTemp3.setPreferredSize(new java.awt.Dimension(100, 30));
        waterTemp3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterTemp3ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(waterTemp3);

        trip3.setEditable(false);
        trip3.setBackground(new java.awt.Color(142, 0, 0));
        trip3.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        trip3.setForeground(new java.awt.Color(0, 0, 0));
        trip3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        trip3.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.trip3.text")); // NOI18N
        trip3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        trip3.setFocusable(false);
        trip3.setPreferredSize(new java.awt.Dimension(100, 30));
        trip3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trip3ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(trip3);

        cavit3.setEditable(false);
        cavit3.setBackground(new java.awt.Color(142, 0, 0));
        cavit3.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        cavit3.setForeground(new java.awt.Color(0, 0, 0));
        cavit3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cavit3.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.cavit3.text")); // NOI18N
        cavit3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cavit3.setFocusable(false);
        cavit3.setPreferredSize(new java.awt.Dimension(100, 30));
        cavit3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cavit3ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(cavit3);

        waterTemp11.setEditable(false);
        waterTemp11.setBackground(new java.awt.Color(142, 0, 0));
        waterTemp11.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        waterTemp11.setForeground(new java.awt.Color(0, 0, 0));
        waterTemp11.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        waterTemp11.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.waterTemp11.text")); // NOI18N
        waterTemp11.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        waterTemp11.setFocusable(false);
        waterTemp11.setPreferredSize(new java.awt.Dimension(100, 30));
        waterTemp11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterTemp11ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(waterTemp11);

        trip7.setEditable(false);
        trip7.setBackground(new java.awt.Color(142, 0, 0));
        trip7.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        trip7.setForeground(new java.awt.Color(0, 0, 0));
        trip7.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        trip7.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.trip7.text")); // NOI18N
        trip7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        trip7.setFocusable(false);
        trip7.setPreferredSize(new java.awt.Dimension(100, 30));
        trip7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trip7ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(trip7);

        cavit7.setEditable(false);
        cavit7.setBackground(new java.awt.Color(142, 0, 0));
        cavit7.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        cavit7.setForeground(new java.awt.Color(0, 0, 0));
        cavit7.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cavit7.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.cavit7.text")); // NOI18N
        cavit7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cavit7.setFocusable(false);
        cavit7.setPreferredSize(new java.awt.Dimension(100, 30));
        cavit7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cavit7ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(cavit7);

        waterTemp17.setEditable(false);
        waterTemp17.setBackground(new java.awt.Color(142, 0, 0));
        waterTemp17.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        waterTemp17.setForeground(new java.awt.Color(0, 0, 0));
        waterTemp17.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        waterTemp17.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.waterTemp17.text")); // NOI18N
        waterTemp17.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        waterTemp17.setFocusable(false);
        waterTemp17.setPreferredSize(new java.awt.Dimension(100, 30));
        waterTemp17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterTemp17ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(waterTemp17);

        trip4.setEditable(false);
        trip4.setBackground(new java.awt.Color(142, 0, 0));
        trip4.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        trip4.setForeground(new java.awt.Color(0, 0, 0));
        trip4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        trip4.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.trip4.text")); // NOI18N
        trip4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        trip4.setFocusable(false);
        trip4.setPreferredSize(new java.awt.Dimension(100, 30));
        trip4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trip4ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(trip4);

        cavit4.setEditable(false);
        cavit4.setBackground(new java.awt.Color(142, 0, 0));
        cavit4.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        cavit4.setForeground(new java.awt.Color(0, 0, 0));
        cavit4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cavit4.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.cavit4.text")); // NOI18N
        cavit4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cavit4.setFocusable(false);
        cavit4.setPreferredSize(new java.awt.Dimension(100, 30));
        cavit4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cavit4ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(cavit4);

        waterTemp19.setEditable(false);
        waterTemp19.setBackground(new java.awt.Color(142, 0, 0));
        waterTemp19.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        waterTemp19.setForeground(new java.awt.Color(0, 0, 0));
        waterTemp19.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        waterTemp19.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.waterTemp19.text")); // NOI18N
        waterTemp19.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        waterTemp19.setFocusable(false);
        waterTemp19.setPreferredSize(new java.awt.Dimension(100, 30));
        waterTemp19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterTemp19ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(waterTemp19);

        trip8.setEditable(false);
        trip8.setBackground(new java.awt.Color(142, 0, 0));
        trip8.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        trip8.setForeground(new java.awt.Color(0, 0, 0));
        trip8.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        trip8.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.trip8.text")); // NOI18N
        trip8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        trip8.setFocusable(false);
        trip8.setPreferredSize(new java.awt.Dimension(100, 30));
        trip8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trip8ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(trip8);

        cavit8.setEditable(false);
        cavit8.setBackground(new java.awt.Color(142, 0, 0));
        cavit8.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        cavit8.setForeground(new java.awt.Color(0, 0, 0));
        cavit8.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cavit8.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.cavit8.text")); // NOI18N
        cavit8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cavit8.setFocusable(false);
        cavit8.setPreferredSize(new java.awt.Dimension(100, 30));
        cavit8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cavit8ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(cavit8);

        waterTemp23.setEditable(false);
        waterTemp23.setBackground(new java.awt.Color(142, 0, 0));
        waterTemp23.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        waterTemp23.setForeground(new java.awt.Color(0, 0, 0));
        waterTemp23.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        waterTemp23.setText(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.waterTemp23.text")); // NOI18N
        waterTemp23.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        waterTemp23.setFocusable(false);
        waterTemp23.setPreferredSize(new java.awt.Dimension(100, 30));
        waterTemp23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterTemp23ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(waterTemp23);

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

        flow1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        flow1.setFrameVisible(false);
        flow1.setLcdVisible(false);
        flow1.setLedVisible(false);
        flow1.setMaxValue(2400.0);
        flow1.setMinorTickSpacing(100.0);
        flow1.setThreshold(800.0);
        flow1.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.flow1.title")); // NOI18N
        flow1.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        flow1.setTrackStart(800.0);
        flow1.setTrackStartColor(new java.awt.Color(255, 0, 0));
        flow1.setTrackStop(1000.0);
        flow1.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.flow1.unitString")); // NOI18N
        flow1.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        flow2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        flow2.setFrameVisible(false);
        flow2.setLcdVisible(false);
        flow2.setLedVisible(false);
        flow2.setMaxValue(2400.0);
        flow2.setMinorTickSpacing(100.0);
        flow2.setThreshold(800.0);
        flow2.setTitle("Pump 2 Flow");
        flow2.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        flow2.setTrackStart(800.0);
        flow2.setTrackStartColor(new java.awt.Color(255, 0, 0));
        flow2.setTrackStop(1000.0);
        flow2.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.flow2.unitString")); // NOI18N
        flow2.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        flow3.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        flow3.setFrameVisible(false);
        flow3.setLcdVisible(false);
        flow3.setLedVisible(false);
        flow3.setMaxValue(2400.0);
        flow3.setMinorTickSpacing(100.0);
        flow3.setThreshold(800.0);
        flow3.setTitle("Pump 3 Flow");
        flow3.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        flow3.setTrackStart(800.0);
        flow3.setTrackStartColor(new java.awt.Color(255, 0, 0));
        flow3.setTrackStop(1000.0);
        flow3.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.flow3.unitString")); // NOI18N
        flow3.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        flow4.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        flow4.setFrameVisible(false);
        flow4.setLcdVisible(false);
        flow4.setLedVisible(false);
        flow4.setMaxValue(2400.0);
        flow4.setMinorTickSpacing(100.0);
        flow4.setThreshold(800.0);
        flow4.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.flow4.title")); // NOI18N
        flow4.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        flow4.setTrackStart(800.0);
        flow4.setTrackStartColor(new java.awt.Color(255, 0, 0));
        flow4.setTrackStop(1000.0);
        flow4.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.flow4.unitString")); // NOI18N
        flow4.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        amps1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        amps1.setFrameVisible(false);
        amps1.setLcdVisible(false);
        amps1.setLedVisible(false);
        amps1.setMaxValue(1000.0);
        amps1.setThreshold(800.0);
        amps1.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.amps1.title")); // NOI18N
        amps1.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        amps1.setTrackStart(800.0);
        amps1.setTrackStartColor(new java.awt.Color(255, 0, 0));
        amps1.setTrackStop(1000.0);
        amps1.setUnitString("A");
        amps1.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        amps2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        amps2.setFrameVisible(false);
        amps2.setLcdVisible(false);
        amps2.setLedVisible(false);
        amps2.setMaxValue(1000.0);
        amps2.setThreshold(800.0);
        amps2.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.amps2.title")); // NOI18N
        amps2.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        amps2.setTrackStart(800.0);
        amps2.setTrackStartColor(new java.awt.Color(255, 0, 0));
        amps2.setTrackStop(1000.0);
        amps2.setUnitString("A");
        amps2.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        amps3.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        amps3.setFrameVisible(false);
        amps3.setLcdVisible(false);
        amps3.setLedVisible(false);
        amps3.setMaxValue(1000.0);
        amps3.setThreshold(800.0);
        amps3.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.amps3.title")); // NOI18N
        amps3.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        amps3.setTrackStart(800.0);
        amps3.setTrackStartColor(new java.awt.Color(255, 0, 0));
        amps3.setTrackStop(1000.0);
        amps3.setUnitString("A");
        amps3.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        amps4.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        amps4.setFrameVisible(false);
        amps4.setLcdVisible(false);
        amps4.setLedVisible(false);
        amps4.setMaxValue(1000.0);
        amps4.setThreshold(800.0);
        amps4.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.amps4.title")); // NOI18N
        amps4.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        amps4.setTrackStart(800.0);
        amps4.setTrackStartColor(new java.awt.Color(255, 0, 0));
        amps4.setTrackStop(1000.0);
        amps4.setUnitString("A");
        amps4.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        flow8.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        flow8.setFrameVisible(false);
        flow8.setLcdVisible(false);
        flow8.setLedVisible(false);
        flow8.setMaxValue(2400.0);
        flow8.setMinorTickSpacing(100.0);
        flow8.setThreshold(800.0);
        flow8.setTitle("Pump 8 Flow");
        flow8.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        flow8.setTrackStart(800.0);
        flow8.setTrackStartColor(new java.awt.Color(255, 0, 0));
        flow8.setTrackStop(1000.0);
        flow8.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.flow8.unitString")); // NOI18N
        flow8.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        amps8.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        amps8.setFrameVisible(false);
        amps8.setLcdVisible(false);
        amps8.setLedVisible(false);
        amps8.setMaxValue(1000.0);
        amps8.setThreshold(800.0);
        amps8.setTitle("Pump 8 Current");
        amps8.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        amps8.setTrackStart(800.0);
        amps8.setTrackStartColor(new java.awt.Color(255, 0, 0));
        amps8.setTrackStop(1000.0);
        amps8.setUnitString("A");
        amps8.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        amps7.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        amps7.setFrameVisible(false);
        amps7.setLcdVisible(false);
        amps7.setLedVisible(false);
        amps7.setMaxValue(1000.0);
        amps7.setThreshold(800.0);
        amps7.setTitle("Pump 7 Current");
        amps7.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        amps7.setTrackStart(800.0);
        amps7.setTrackStartColor(new java.awt.Color(255, 0, 0));
        amps7.setTrackStop(1000.0);
        amps7.setUnitString("A");
        amps7.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        flow7.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        flow7.setFrameVisible(false);
        flow7.setLcdVisible(false);
        flow7.setLedVisible(false);
        flow7.setMaxValue(2400.0);
        flow7.setMinorTickSpacing(100.0);
        flow7.setThreshold(800.0);
        flow7.setTitle("Pump 7 Flow");
        flow7.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        flow7.setTrackStart(800.0);
        flow7.setTrackStartColor(new java.awt.Color(255, 0, 0));
        flow7.setTrackStop(1000.0);
        flow7.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.flow7.unitString")); // NOI18N
        flow7.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        flow6.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        flow6.setFrameVisible(false);
        flow6.setLcdVisible(false);
        flow6.setLedVisible(false);
        flow6.setMaxValue(2400.0);
        flow6.setMinorTickSpacing(100.0);
        flow6.setThreshold(800.0);
        flow6.setTitle("Pump 6 Flow");
        flow6.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        flow6.setTrackStart(800.0);
        flow6.setTrackStartColor(new java.awt.Color(255, 0, 0));
        flow6.setTrackStop(1000.0);
        flow6.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.flow6.unitString")); // NOI18N
        flow6.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        flow5.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        flow5.setFrameVisible(false);
        flow5.setLcdVisible(false);
        flow5.setLedVisible(false);
        flow5.setMaxValue(2400.0);
        flow5.setMinorTickSpacing(100.0);
        flow5.setThreshold(800.0);
        flow5.setTitle("Pump 5 Flow");
        flow5.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        flow5.setTrackStart(800.0);
        flow5.setTrackStartColor(new java.awt.Color(255, 0, 0));
        flow5.setTrackStop(1000.0);
        flow5.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.flow5.unitString")); // NOI18N
        flow5.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        amps5.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        amps5.setFrameVisible(false);
        amps5.setLcdVisible(false);
        amps5.setLedVisible(false);
        amps5.setMaxValue(1000.0);
        amps5.setThreshold(800.0);
        amps5.setTitle("Pump 5 Current");
        amps5.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        amps5.setTrackStart(800.0);
        amps5.setTrackStartColor(new java.awt.Color(255, 0, 0));
        amps5.setTrackStop(1000.0);
        amps5.setUnitString("A");
        amps5.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        amps6.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        amps6.setFrameVisible(false);
        amps6.setLcdVisible(false);
        amps6.setLedVisible(false);
        amps6.setMaxValue(1000.0);
        amps6.setThreshold(800.0);
        amps6.setTitle("Pump 6 Current");
        amps6.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        amps6.setTrackStart(800.0);
        amps6.setTrackStartColor(new java.awt.Color(255, 0, 0));
        amps6.setTrackStop(1000.0);
        amps6.setUnitString("A");
        amps6.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        jPanel35.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel17.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel17, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.jLabel2.text")); // NOI18N

        jPanel34.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        pos1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        pos1.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        pos1.setFrameVisible(false);
        pos1.setLcdBackgroundVisible(false);
        pos1.setLcdVisible(false);
        pos1.setLedVisible(false);
        pos1.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        pos1.setPointerShadowVisible(false);
        pos1.setPreferredSize(new java.awt.Dimension(100, 100));
        pos1.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.radial2Top1.title")); // NOI18N
        pos1.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        pos1.setTitleAndUnitFontEnabled(true);
        pos1.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.radial2Top1.unitString")); // NOI18N

        buttonGroup1.add(tgValvesOpen11);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesOpen11, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesOpen.text")); // NOI18N
        tgValvesOpen11.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesOpenItemStateChanged(evt);
            }
        });
        tgValvesOpen11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesOpenActionPerformed(evt);
            }
        });

        buttonGroup1.add(tgValvesStop11);
        tgValvesStop11.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesStop11, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesStop.text")); // NOI18N
        tgValvesStop11.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesStopItemStateChanged(evt);
            }
        });
        tgValvesStop11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesStopActionPerformed(evt);
            }
        });

        buttonGroup1.add(tgValvesClose11);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesClose11, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesClose.text")); // NOI18N
        tgValvesClose11.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesCloseItemStateChanged(evt);
            }
        });
        tgValvesClose11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesClose11ActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel18, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout jPanel34Layout = new javax.swing.GroupLayout(jPanel34);
        jPanel34.setLayout(jPanel34Layout);
        jPanel34Layout.setHorizontalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel34Layout.createSequentialGroup()
                .addGap(0, 10, Short.MAX_VALUE)
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(112, 112, 112))
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tgValvesOpen11)
                            .addComponent(tgValvesStop11)
                            .addComponent(tgValvesClose11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pos1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel34Layout.setVerticalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel34Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addGap(7, 7, 7)
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addComponent(tgValvesOpen11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesStop11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesClose11))
                    .addComponent(pos1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        pos2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        pos2.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        pos2.setFrameVisible(false);
        pos2.setLcdBackgroundVisible(false);
        pos2.setLcdVisible(false);
        pos2.setLedVisible(false);
        pos2.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        pos2.setPointerShadowVisible(false);
        pos2.setPreferredSize(new java.awt.Dimension(100, 100));
        pos2.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.pos2.title")); // NOI18N
        pos2.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        pos2.setTitleAndUnitFontEnabled(true);
        pos2.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.pos2.unitString")); // NOI18N

        buttonGroup2.add(tgValvesOpen12);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesOpen12, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesOpen12.text")); // NOI18N
        tgValvesOpen12.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesOpen12ItemStateChanged(evt);
            }
        });
        tgValvesOpen12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesOpen12ActionPerformed(evt);
            }
        });

        buttonGroup2.add(tgValvesStop12);
        tgValvesStop12.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesStop12, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesStop12.text")); // NOI18N
        tgValvesStop12.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesStop12ItemStateChanged(evt);
            }
        });
        tgValvesStop12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesStop12ActionPerformed(evt);
            }
        });

        buttonGroup2.add(tgValvesClose12);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesClose12, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesClose12.text")); // NOI18N
        tgValvesClose12.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesClose12ItemStateChanged(evt);
            }
        });
        tgValvesClose12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesClose12ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, "Pump 2");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 10, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(112, 112, 112))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tgValvesOpen12)
                            .addComponent(tgValvesStop12)
                            .addComponent(tgValvesClose12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pos2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(7, 7, 7)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(tgValvesOpen12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesStop12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesClose12))
                    .addComponent(pos2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        pos4.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        pos4.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        pos4.setFrameVisible(false);
        pos4.setLcdBackgroundVisible(false);
        pos4.setLcdVisible(false);
        pos4.setLedVisible(false);
        pos4.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        pos4.setPointerShadowVisible(false);
        pos4.setPreferredSize(new java.awt.Dimension(100, 100));
        pos4.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.pos4.title")); // NOI18N
        pos4.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        pos4.setTitleAndUnitFontEnabled(true);
        pos4.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.pos4.unitString")); // NOI18N

        buttonGroup4.add(tgValvesOpen14);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesOpen14, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesOpen14.text")); // NOI18N
        tgValvesOpen14.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesOpen14ItemStateChanged(evt);
            }
        });
        tgValvesOpen14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesOpen14ActionPerformed(evt);
            }
        });

        buttonGroup4.add(tgValvesStop14);
        tgValvesStop14.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesStop14, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesStop14.text")); // NOI18N
        tgValvesStop14.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesStop14ItemStateChanged(evt);
            }
        });
        tgValvesStop14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesStop14ActionPerformed(evt);
            }
        });

        buttonGroup4.add(tgValvesClose14);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesClose14, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesClose14.text")); // NOI18N
        tgValvesClose14.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesClose14ItemStateChanged(evt);
            }
        });
        tgValvesClose14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesClose14ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, "Pump 4");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(0, 10, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(112, 112, 112))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tgValvesOpen14)
                            .addComponent(tgValvesStop14)
                            .addComponent(tgValvesClose14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pos4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(7, 7, 7)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(tgValvesOpen14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesStop14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesClose14))
                    .addComponent(pos4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        pos3.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        pos3.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        pos3.setFrameVisible(false);
        pos3.setLcdBackgroundVisible(false);
        pos3.setLcdVisible(false);
        pos3.setLedVisible(false);
        pos3.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        pos3.setPointerShadowVisible(false);
        pos3.setPreferredSize(new java.awt.Dimension(100, 100));
        pos3.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.pos3.title")); // NOI18N
        pos3.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        pos3.setTitleAndUnitFontEnabled(true);
        pos3.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.pos3.unitString")); // NOI18N

        buttonGroup3.add(tgValvesOpen3);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesOpen3, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesOpen3.text")); // NOI18N
        tgValvesOpen3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesOpen3ItemStateChanged(evt);
            }
        });
        tgValvesOpen3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesOpen3ActionPerformed(evt);
            }
        });

        buttonGroup3.add(tgValvesStop3);
        tgValvesStop3.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesStop3, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesStop3.text")); // NOI18N
        tgValvesStop3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesStop3ItemStateChanged(evt);
            }
        });
        tgValvesStop3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesStop3ActionPerformed(evt);
            }
        });

        buttonGroup3.add(tgValvesClose3);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesClose3, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesClose3.text")); // NOI18N
        tgValvesClose3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesClose3ItemStateChanged(evt);
            }
        });
        tgValvesClose3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesClose3ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, "Pump 3");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(0, 10, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(112, 112, 112))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tgValvesOpen3)
                            .addComponent(tgValvesStop3)
                            .addComponent(tgValvesClose3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pos3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(7, 7, 7)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(tgValvesOpen3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesStop3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesClose3))
                    .addComponent(pos3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        pos6.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        pos6.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        pos6.setFrameVisible(false);
        pos6.setLcdBackgroundVisible(false);
        pos6.setLcdVisible(false);
        pos6.setLedVisible(false);
        pos6.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        pos6.setPointerShadowVisible(false);
        pos6.setPreferredSize(new java.awt.Dimension(100, 100));
        pos6.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.pos6.title")); // NOI18N
        pos6.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        pos6.setTitleAndUnitFontEnabled(true);
        pos6.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.pos6.unitString")); // NOI18N

        buttonGroup6.add(tgValvesOpen6);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesOpen6, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesOpen6.text")); // NOI18N
        tgValvesOpen6.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesOpen6ItemStateChanged(evt);
            }
        });
        tgValvesOpen6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesOpen6ActionPerformed(evt);
            }
        });

        buttonGroup6.add(tgValvesStop6);
        tgValvesStop6.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesStop6, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesStop6.text")); // NOI18N
        tgValvesStop6.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesStop6ItemStateChanged(evt);
            }
        });
        tgValvesStop6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesStop6ActionPerformed(evt);
            }
        });

        buttonGroup6.add(tgValvesClose6);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesClose6, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesClose6.text")); // NOI18N
        tgValvesClose6.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesClose6ItemStateChanged(evt);
            }
        });
        tgValvesClose6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesClose6ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.jLabel7.text")); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(0, 10, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(112, 112, 112))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tgValvesOpen6)
                            .addComponent(tgValvesStop6)
                            .addComponent(tgValvesClose6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pos6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addGap(7, 7, 7)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(tgValvesOpen6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesStop6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesClose6))
                    .addComponent(pos6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        pos5.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        pos5.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        pos5.setFrameVisible(false);
        pos5.setLcdBackgroundVisible(false);
        pos5.setLcdVisible(false);
        pos5.setLedVisible(false);
        pos5.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        pos5.setPointerShadowVisible(false);
        pos5.setPreferredSize(new java.awt.Dimension(100, 100));
        pos5.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.pos5.title")); // NOI18N
        pos5.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        pos5.setTitleAndUnitFontEnabled(true);
        pos5.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.pos5.unitString")); // NOI18N

        buttonGroup5.add(tgValvesOpen5);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesOpen5, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesOpen5.text")); // NOI18N
        tgValvesOpen5.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesOpen5ItemStateChanged(evt);
            }
        });
        tgValvesOpen5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesOpen5ActionPerformed(evt);
            }
        });

        buttonGroup5.add(tgValvesStop5);
        tgValvesStop5.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesStop5, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesStop5.text")); // NOI18N
        tgValvesStop5.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesStop5ItemStateChanged(evt);
            }
        });
        tgValvesStop5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesStop5ActionPerformed(evt);
            }
        });

        buttonGroup5.add(tgValvesClose5);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesClose5, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesClose5.text")); // NOI18N
        tgValvesClose5.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesClose5ItemStateChanged(evt);
            }
        });
        tgValvesClose5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesClose5ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.jLabel3.text")); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 10, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(112, 112, 112))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tgValvesOpen5)
                            .addComponent(tgValvesStop5)
                            .addComponent(tgValvesClose5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pos5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(7, 7, 7)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(tgValvesOpen5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesStop5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesClose5))
                    .addComponent(pos5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        pos7.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        pos7.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        pos7.setFrameVisible(false);
        pos7.setLcdBackgroundVisible(false);
        pos7.setLcdVisible(false);
        pos7.setLedVisible(false);
        pos7.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        pos7.setPointerShadowVisible(false);
        pos7.setPreferredSize(new java.awt.Dimension(100, 100));
        pos7.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.pos7.title")); // NOI18N
        pos7.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        pos7.setTitleAndUnitFontEnabled(true);
        pos7.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.pos7.unitString")); // NOI18N

        buttonGroup7.add(tgValvesOpen17);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesOpen17, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesOpen17.text")); // NOI18N
        tgValvesOpen17.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesOpen17ItemStateChanged(evt);
            }
        });
        tgValvesOpen17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesOpen17ActionPerformed(evt);
            }
        });

        buttonGroup7.add(tgValvesStop17);
        tgValvesStop17.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesStop17, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesStop17.text")); // NOI18N
        tgValvesStop17.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesStop17ItemStateChanged(evt);
            }
        });
        tgValvesStop17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesStop17ActionPerformed(evt);
            }
        });

        buttonGroup7.add(tgValvesClose17);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesClose17, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesClose17.text")); // NOI18N
        tgValvesClose17.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesClose17ItemStateChanged(evt);
            }
        });
        tgValvesClose17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesClose17ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.jLabel8.text")); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(0, 10, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(112, 112, 112))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tgValvesOpen17)
                            .addComponent(tgValvesStop17)
                            .addComponent(tgValvesClose17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pos7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(7, 7, 7)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(tgValvesOpen17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesStop17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesClose17))
                    .addComponent(pos7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        pos8.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        pos8.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.TILTED_BLACK);
        pos8.setFrameVisible(false);
        pos8.setLcdBackgroundVisible(false);
        pos8.setLcdVisible(false);
        pos8.setLedVisible(false);
        pos8.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.BLACK);
        pos8.setPointerShadowVisible(false);
        pos8.setPreferredSize(new java.awt.Dimension(100, 100));
        pos8.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.pos8.title")); // NOI18N
        pos8.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 8)); // NOI18N
        pos8.setTitleAndUnitFontEnabled(true);
        pos8.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.pos8.unitString")); // NOI18N

        buttonGroup8.add(tgValvesOpen18);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesOpen18, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesOpen18.text")); // NOI18N
        tgValvesOpen18.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesOpen18ItemStateChanged(evt);
            }
        });
        tgValvesOpen18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesOpen18ActionPerformed(evt);
            }
        });

        buttonGroup8.add(tgValvesStop18);
        tgValvesStop18.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesStop18, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesStop18.text")); // NOI18N
        tgValvesStop18.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesStop18ItemStateChanged(evt);
            }
        });
        tgValvesStop18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesStop18ActionPerformed(evt);
            }
        });

        buttonGroup8.add(tgValvesClose18);
        org.openide.awt.Mnemonics.setLocalizedText(tgValvesClose18, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.tgValvesClose18.text")); // NOI18N
        tgValvesClose18.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tgValvesClose18ItemStateChanged(evt);
            }
        });
        tgValvesClose18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgValvesClose18ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.jLabel9.text")); // NOI18N

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGap(0, 10, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(112, 112, 112))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tgValvesOpen18)
                            .addComponent(tgValvesStop18)
                            .addComponent(tgValvesClose18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pos8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addGap(7, 7, 7)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(tgValvesOpen18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesStop18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgValvesClose18))
                    .addComponent(pos8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel35Layout = new javax.swing.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel35Layout.createSequentialGroup()
                                .addComponent(jPanel34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel35Layout.createSequentialGroup()
                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel35Layout.createSequentialGroup()
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel35Layout.createSequentialGroup()
                                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        jPanel35Layout.setVerticalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel34, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPanel12.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));
        jPanel12.setPreferredSize(new java.awt.Dimension(200, 43));

        rpm1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        rpm1.setFrameType(eu.hansolo.steelseries.tools.FrameType.SQUARE);
        rpm1.setFrameVisible(false);
        rpm1.setLcdVisible(false);
        rpm1.setLedVisible(false);
        rpm1.setMaxValue(1500.0);
        rpm1.setMaximumSize(new java.awt.Dimension(160, 160));
        rpm1.setMinimumSize(new java.awt.Dimension(160, 160));
        rpm1.setMinorTickSpacing(100.0);
        rpm1.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.GRAY);
        rpm1.setPointerShadowVisible(false);
        rpm1.setPostsVisible(false);
        rpm1.setTicklabelOrientation(eu.hansolo.steelseries.tools.TicklabelOrientation.HORIZONTAL);
        rpm1.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.rpm1.title")); // NOI18N
        rpm1.setTrackSection(200.0);
        rpm1.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        rpm1.setTrackStart(3100.0);
        rpm1.setTrackStartColor(new java.awt.Color(255, 0, 0));
        rpm1.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.rpm1.unitString")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(rpm1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(rpm1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        rpm2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        rpm2.setFrameType(eu.hansolo.steelseries.tools.FrameType.SQUARE);
        rpm2.setFrameVisible(false);
        rpm2.setLcdVisible(false);
        rpm2.setLedVisible(false);
        rpm2.setMaxValue(1500.0);
        rpm2.setMaximumSize(new java.awt.Dimension(160, 160));
        rpm2.setMinimumSize(new java.awt.Dimension(160, 160));
        rpm2.setMinorTickSpacing(100.0);
        rpm2.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.GRAY);
        rpm2.setPointerShadowVisible(false);
        rpm2.setPostsVisible(false);
        rpm2.setTicklabelOrientation(eu.hansolo.steelseries.tools.TicklabelOrientation.HORIZONTAL);
        rpm2.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.rpm2.title")); // NOI18N
        rpm2.setTrackSection(200.0);
        rpm2.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        rpm2.setTrackStart(3100.0);
        rpm2.setTrackStartColor(new java.awt.Color(255, 0, 0));
        rpm2.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.rpm2.unitString")); // NOI18N

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(rpm2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(rpm2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        rpm5.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        rpm5.setFrameType(eu.hansolo.steelseries.tools.FrameType.SQUARE);
        rpm5.setFrameVisible(false);
        rpm5.setLcdVisible(false);
        rpm5.setLedVisible(false);
        rpm5.setMaxValue(1500.0);
        rpm5.setMaximumSize(new java.awt.Dimension(160, 160));
        rpm5.setMinimumSize(new java.awt.Dimension(160, 160));
        rpm5.setMinorTickSpacing(100.0);
        rpm5.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.GRAY);
        rpm5.setPointerShadowVisible(false);
        rpm5.setPostsVisible(false);
        rpm5.setTicklabelOrientation(eu.hansolo.steelseries.tools.TicklabelOrientation.HORIZONTAL);
        rpm5.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.rpm5.title")); // NOI18N
        rpm5.setTrackSection(200.0);
        rpm5.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        rpm5.setTrackStart(3100.0);
        rpm5.setTrackStartColor(new java.awt.Color(255, 0, 0));
        rpm5.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.rpm5.unitString")); // NOI18N

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(rpm5, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(rpm5, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        rpm6.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        rpm6.setFrameType(eu.hansolo.steelseries.tools.FrameType.SQUARE);
        rpm6.setFrameVisible(false);
        rpm6.setLcdVisible(false);
        rpm6.setLedVisible(false);
        rpm6.setMaxValue(1500.0);
        rpm6.setMaximumSize(new java.awt.Dimension(160, 160));
        rpm6.setMinimumSize(new java.awt.Dimension(160, 160));
        rpm6.setMinorTickSpacing(100.0);
        rpm6.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.GRAY);
        rpm6.setPointerShadowVisible(false);
        rpm6.setPostsVisible(false);
        rpm6.setTicklabelOrientation(eu.hansolo.steelseries.tools.TicklabelOrientation.HORIZONTAL);
        rpm6.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.rpm6.title")); // NOI18N
        rpm6.setTrackSection(200.0);
        rpm6.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        rpm6.setTrackStart(3100.0);
        rpm6.setTrackStartColor(new java.awt.Color(255, 0, 0));
        rpm6.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.rpm6.unitString")); // NOI18N

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(rpm6, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(rpm6, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(222, 222, 222)));

        org.openide.awt.Mnemonics.setLocalizedText(precisionIncrement1, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.precisionIncrement1.text")); // NOI18N
        precisionIncrement1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionIncrement1.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionIncrement1.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionIncrement1.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionIncrement1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionIncrement1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(precisionDecrement1, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.precisionDecrement1.text")); // NOI18N
        precisionDecrement1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionDecrement1.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionDecrement1.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionDecrement1.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionDecrement1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionDecrement1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(precisionIncrement1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(precisionDecrement1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(precisionIncrement1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precisionDecrement1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(222, 222, 222)));

        org.openide.awt.Mnemonics.setLocalizedText(precisionIncrement2, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.precisionIncrement2.text")); // NOI18N
        precisionIncrement2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionIncrement2.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionIncrement2.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionIncrement2.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionIncrement2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionIncrement2ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(precisionDecrement2, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.precisionDecrement2.text")); // NOI18N
        precisionDecrement2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionDecrement2.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionDecrement2.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionDecrement2.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionDecrement2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionDecrement2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(precisionIncrement2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(precisionDecrement2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(precisionIncrement2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precisionDecrement2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(222, 222, 222)));

        org.openide.awt.Mnemonics.setLocalizedText(precisionIncrement5, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.precisionIncrement5.text")); // NOI18N
        precisionIncrement5.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionIncrement5.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionIncrement5.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionIncrement5.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionIncrement5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionIncrement5ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(precisionDecrement5, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.precisionDecrement5.text")); // NOI18N
        precisionDecrement5.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionDecrement5.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionDecrement5.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionDecrement5.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionDecrement5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionDecrement5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(precisionIncrement5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(precisionDecrement5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(precisionIncrement5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precisionDecrement5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel19.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(222, 222, 222)));

        org.openide.awt.Mnemonics.setLocalizedText(precisionIncrement6, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.precisionIncrement6.text")); // NOI18N
        precisionIncrement6.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionIncrement6.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionIncrement6.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionIncrement6.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionIncrement6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionIncrement6ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(precisionDecrement6, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.precisionDecrement6.text")); // NOI18N
        precisionDecrement6.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionDecrement6.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionDecrement6.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionDecrement6.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionDecrement6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionDecrement6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(precisionIncrement6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(precisionDecrement6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(precisionIncrement6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precisionDecrement6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        rpm3.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        rpm3.setFrameType(eu.hansolo.steelseries.tools.FrameType.SQUARE);
        rpm3.setFrameVisible(false);
        rpm3.setLcdVisible(false);
        rpm3.setLedVisible(false);
        rpm3.setMaxValue(1500.0);
        rpm3.setMaximumSize(new java.awt.Dimension(160, 160));
        rpm3.setMinimumSize(new java.awt.Dimension(160, 160));
        rpm3.setMinorTickSpacing(100.0);
        rpm3.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.GRAY);
        rpm3.setPointerShadowVisible(false);
        rpm3.setPostsVisible(false);
        rpm3.setTicklabelOrientation(eu.hansolo.steelseries.tools.TicklabelOrientation.HORIZONTAL);
        rpm3.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.rpm3.title")); // NOI18N
        rpm3.setTrackSection(200.0);
        rpm3.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        rpm3.setTrackStart(3100.0);
        rpm3.setTrackStartColor(new java.awt.Color(255, 0, 0));
        rpm3.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.rpm3.unitString")); // NOI18N

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(rpm3, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(rpm3, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        rpm4.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        rpm4.setFrameType(eu.hansolo.steelseries.tools.FrameType.SQUARE);
        rpm4.setFrameVisible(false);
        rpm4.setLcdVisible(false);
        rpm4.setLedVisible(false);
        rpm4.setMaxValue(1500.0);
        rpm4.setMaximumSize(new java.awt.Dimension(160, 160));
        rpm4.setMinimumSize(new java.awt.Dimension(160, 160));
        rpm4.setMinorTickSpacing(100.0);
        rpm4.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.GRAY);
        rpm4.setPointerShadowVisible(false);
        rpm4.setPostsVisible(false);
        rpm4.setTicklabelOrientation(eu.hansolo.steelseries.tools.TicklabelOrientation.HORIZONTAL);
        rpm4.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.rpm4.title")); // NOI18N
        rpm4.setTrackSection(200.0);
        rpm4.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        rpm4.setTrackStart(3100.0);
        rpm4.setTrackStartColor(new java.awt.Color(255, 0, 0));
        rpm4.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.rpm4.unitString")); // NOI18N

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(rpm4, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(rpm4, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        rpm7.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        rpm7.setFrameType(eu.hansolo.steelseries.tools.FrameType.SQUARE);
        rpm7.setFrameVisible(false);
        rpm7.setLcdVisible(false);
        rpm7.setLedVisible(false);
        rpm7.setMaxValue(1500.0);
        rpm7.setMaximumSize(new java.awt.Dimension(160, 160));
        rpm7.setMinimumSize(new java.awt.Dimension(160, 160));
        rpm7.setMinorTickSpacing(100.0);
        rpm7.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.GRAY);
        rpm7.setPointerShadowVisible(false);
        rpm7.setPostsVisible(false);
        rpm7.setTicklabelOrientation(eu.hansolo.steelseries.tools.TicklabelOrientation.HORIZONTAL);
        rpm7.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.rpm7.title")); // NOI18N
        rpm7.setTrackSection(200.0);
        rpm7.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        rpm7.setTrackStart(3100.0);
        rpm7.setTrackStartColor(new java.awt.Color(255, 0, 0));
        rpm7.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.rpm7.unitString")); // NOI18N

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(rpm7, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(rpm7, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        rpm8.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        rpm8.setFrameType(eu.hansolo.steelseries.tools.FrameType.SQUARE);
        rpm8.setFrameVisible(false);
        rpm8.setLcdVisible(false);
        rpm8.setLedVisible(false);
        rpm8.setMaxValue(1500.0);
        rpm8.setMaximumSize(new java.awt.Dimension(160, 160));
        rpm8.setMinimumSize(new java.awt.Dimension(160, 160));
        rpm8.setMinorTickSpacing(100.0);
        rpm8.setPointerColor(eu.hansolo.steelseries.tools.ColorDef.GRAY);
        rpm8.setPointerShadowVisible(false);
        rpm8.setPostsVisible(false);
        rpm8.setTicklabelOrientation(eu.hansolo.steelseries.tools.TicklabelOrientation.HORIZONTAL);
        rpm8.setTitle(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.rpm8.title")); // NOI18N
        rpm8.setTrackSection(200.0);
        rpm8.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        rpm8.setTrackStart(3100.0);
        rpm8.setTrackStartColor(new java.awt.Color(255, 0, 0));
        rpm8.setUnitString(org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.rpm8.unitString")); // NOI18N

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(rpm8, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(rpm8, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel24.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(222, 222, 222)));

        org.openide.awt.Mnemonics.setLocalizedText(precisionIncrement3, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.precisionIncrement3.text")); // NOI18N
        precisionIncrement3.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionIncrement3.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionIncrement3.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionIncrement3.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionIncrement3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionIncrement3ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(precisionDecrement3, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.precisionDecrement3.text")); // NOI18N
        precisionDecrement3.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionDecrement3.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionDecrement3.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionDecrement3.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionDecrement3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionDecrement3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(precisionIncrement3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(precisionDecrement3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(precisionIncrement3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precisionDecrement3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel25.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(222, 222, 222)));

        org.openide.awt.Mnemonics.setLocalizedText(precisionIncrement4, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.precisionIncrement4.text")); // NOI18N
        precisionIncrement4.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionIncrement4.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionIncrement4.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionIncrement4.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionIncrement4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionIncrement4ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(precisionDecrement4, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.precisionDecrement4.text")); // NOI18N
        precisionDecrement4.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionDecrement4.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionDecrement4.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionDecrement4.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionDecrement4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionDecrement4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(precisionIncrement4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(precisionDecrement4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(precisionIncrement4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precisionDecrement4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel26.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(222, 222, 222)));

        org.openide.awt.Mnemonics.setLocalizedText(precisionIncrement7, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.precisionIncrement7.text")); // NOI18N
        precisionIncrement7.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionIncrement7.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionIncrement7.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionIncrement7.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionIncrement7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionIncrement7ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(precisionDecrement7, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.precisionDecrement7.text")); // NOI18N
        precisionDecrement7.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionDecrement7.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionDecrement7.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionDecrement7.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionDecrement7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionDecrement7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(precisionIncrement7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(precisionDecrement7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(precisionIncrement7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precisionDecrement7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel27.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(222, 222, 222)));

        org.openide.awt.Mnemonics.setLocalizedText(precisionIncrement8, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.precisionIncrement8.text")); // NOI18N
        precisionIncrement8.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionIncrement8.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionIncrement8.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionIncrement8.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionIncrement8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionIncrement8ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(precisionDecrement8, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.precisionDecrement8.text")); // NOI18N
        precisionDecrement8.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionDecrement8.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionDecrement8.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionDecrement8.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionDecrement8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionDecrement8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(precisionIncrement8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(precisionDecrement8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(precisionIncrement8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precisionDecrement8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel10.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.jLabel10.text")); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addGap(7, 7, 7)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel36.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        off1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(off1, "OFF");
        off1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        off1.setMaximumSize(new java.awt.Dimension(30, 30));
        off1.setMinimumSize(new java.awt.Dimension(25, 25));
        off1.setPreferredSize(new java.awt.Dimension(30, 30));
        off1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                off1ActionPerformed(evt);
            }
        });

        on1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(on1, "ON");
        on1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        on1.setMaximumSize(new java.awt.Dimension(30, 30));
        on1.setMinimumSize(new java.awt.Dimension(25, 25));
        on1.setPreferredSize(new java.awt.Dimension(30, 30));
        on1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                on1ActionPerformed(evt);
            }
        });

        off2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(off2, "OFF");
        off2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        off2.setMaximumSize(new java.awt.Dimension(30, 30));
        off2.setMinimumSize(new java.awt.Dimension(25, 25));
        off2.setPreferredSize(new java.awt.Dimension(30, 30));
        off2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                off2ActionPerformed(evt);
            }
        });

        on2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(on2, "ON");
        on2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        on2.setMaximumSize(new java.awt.Dimension(30, 30));
        on2.setMinimumSize(new java.awt.Dimension(25, 25));
        on2.setPreferredSize(new java.awt.Dimension(30, 30));
        on2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                on2ActionPerformed(evt);
            }
        });

        on3.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(on3, "ON");
        on3.setMargin(new java.awt.Insets(2, 2, 2, 2));
        on3.setMaximumSize(new java.awt.Dimension(30, 30));
        on3.setMinimumSize(new java.awt.Dimension(25, 25));
        on3.setPreferredSize(new java.awt.Dimension(30, 30));
        on3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                on3ActionPerformed(evt);
            }
        });

        off3.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(off3, "OFF");
        off3.setMargin(new java.awt.Insets(2, 2, 2, 2));
        off3.setMaximumSize(new java.awt.Dimension(30, 30));
        off3.setMinimumSize(new java.awt.Dimension(25, 25));
        off3.setPreferredSize(new java.awt.Dimension(30, 30));
        off3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                off3ActionPerformed(evt);
            }
        });

        off4.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(off4, "OFF");
        off4.setMargin(new java.awt.Insets(2, 2, 2, 2));
        off4.setMaximumSize(new java.awt.Dimension(30, 30));
        off4.setMinimumSize(new java.awt.Dimension(25, 25));
        off4.setPreferredSize(new java.awt.Dimension(30, 30));
        off4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                off4ActionPerformed(evt);
            }
        });

        on4.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(on4, "ON");
        on4.setMargin(new java.awt.Insets(2, 2, 2, 2));
        on4.setMaximumSize(new java.awt.Dimension(30, 30));
        on4.setMinimumSize(new java.awt.Dimension(25, 25));
        on4.setPreferredSize(new java.awt.Dimension(30, 30));
        on4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                on4ActionPerformed(evt);
            }
        });

        off5.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(off5, "OFF");
        off5.setMargin(new java.awt.Insets(2, 2, 2, 2));
        off5.setMaximumSize(new java.awt.Dimension(30, 30));
        off5.setMinimumSize(new java.awt.Dimension(25, 25));
        off5.setPreferredSize(new java.awt.Dimension(30, 30));
        off5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                off5ActionPerformed(evt);
            }
        });

        off6.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(off6, "OFF");
        off6.setMargin(new java.awt.Insets(2, 2, 2, 2));
        off6.setMaximumSize(new java.awt.Dimension(30, 30));
        off6.setMinimumSize(new java.awt.Dimension(25, 25));
        off6.setPreferredSize(new java.awt.Dimension(30, 30));
        off6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                off6ActionPerformed(evt);
            }
        });

        off7.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(off7, "OFF");
        off7.setMargin(new java.awt.Insets(2, 2, 2, 2));
        off7.setMaximumSize(new java.awt.Dimension(30, 30));
        off7.setMinimumSize(new java.awt.Dimension(25, 25));
        off7.setPreferredSize(new java.awt.Dimension(30, 30));
        off7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                off7ActionPerformed(evt);
            }
        });

        off8.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(off8, "OFF");
        off8.setMargin(new java.awt.Insets(2, 2, 2, 2));
        off8.setMaximumSize(new java.awt.Dimension(30, 30));
        off8.setMinimumSize(new java.awt.Dimension(25, 25));
        off8.setPreferredSize(new java.awt.Dimension(30, 30));
        off8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                off8ActionPerformed(evt);
            }
        });

        on8.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(on8, "ON");
        on8.setMargin(new java.awt.Insets(2, 2, 2, 2));
        on8.setMaximumSize(new java.awt.Dimension(30, 30));
        on8.setMinimumSize(new java.awt.Dimension(25, 25));
        on8.setPreferredSize(new java.awt.Dimension(30, 30));
        on8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                on8ActionPerformed(evt);
            }
        });

        on7.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(on7, "ON");
        on7.setMargin(new java.awt.Insets(2, 2, 2, 2));
        on7.setMaximumSize(new java.awt.Dimension(30, 30));
        on7.setMinimumSize(new java.awt.Dimension(25, 25));
        on7.setPreferredSize(new java.awt.Dimension(30, 30));
        on7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                on7ActionPerformed(evt);
            }
        });

        on6.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(on6, "ON");
        on6.setMargin(new java.awt.Insets(2, 2, 2, 2));
        on6.setMaximumSize(new java.awt.Dimension(30, 30));
        on6.setMinimumSize(new java.awt.Dimension(25, 25));
        on6.setPreferredSize(new java.awt.Dimension(30, 30));
        on6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                on6ActionPerformed(evt);
            }
        });

        on5.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(on5, "ON");
        on5.setMargin(new java.awt.Insets(2, 2, 2, 2));
        on5.setMaximumSize(new java.awt.Dimension(30, 30));
        on5.setMinimumSize(new java.awt.Dimension(25, 25));
        on5.setPreferredSize(new java.awt.Dimension(30, 30));
        on5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                on5ActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel19, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.jLabel19.text")); // NOI18N

        led1.setPreferredSize(new java.awt.Dimension(30, 30));

        led2.setPreferredSize(new java.awt.Dimension(30, 30));

        led3.setPreferredSize(new java.awt.Dimension(30, 30));

        led4.setPreferredSize(new java.awt.Dimension(30, 30));

        led5.setPreferredSize(new java.awt.Dimension(30, 30));

        led6.setPreferredSize(new java.awt.Dimension(30, 30));

        led7.setPreferredSize(new java.awt.Dimension(30, 30));

        led8.setPreferredSize(new java.awt.Dimension(30, 30));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel20, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.jLabel20.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel21, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.jLabel21.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel22, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.jLabel22.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel23, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.jLabel23.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel24, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.jLabel24.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel25, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.jLabel25.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel26, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.jLabel26.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel27, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.jLabel27.text")); // NOI18N

        javax.swing.GroupLayout jPanel36Layout = new javax.swing.GroupLayout(jPanel36);
        jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addGap(20, 20, 20)
                        .addComponent(jLabel20))
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addComponent(on2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(off2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(led2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel25))
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addComponent(on3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(off3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(led3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel26))
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addComponent(on4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(off4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(led4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel27))
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addComponent(on1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(off1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(led1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel24)))
                .addGap(9, 9, 9)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addComponent(on5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(off5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(led5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addComponent(on6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(off6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(led6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addComponent(on7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(off7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(led7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addComponent(on8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(off8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(led8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 21, Short.MAX_VALUE))
        );
        jPanel36Layout.setVerticalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel36Layout.createSequentialGroup()
                                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel36Layout.createSequentialGroup()
                                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(on5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(off5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel24))
                                            .addComponent(led5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(on6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(off6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel25)))
                                    .addComponent(led6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(on7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(off7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel26)))
                            .addComponent(led7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(on8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(off8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel27))
                            .addComponent(led8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel36Layout.createSequentialGroup()
                                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel36Layout.createSequentialGroup()
                                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(led1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(on1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(off1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel20)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(on2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(off2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel21)))
                                    .addComponent(led2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(on3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(off3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel22)))
                            .addComponent(led3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(led4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(on4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(off4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel23))))
                    .addComponent(jLabel19))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(annunciatorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 764, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(flow1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(flow2, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(flow3, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(flow4, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(amps1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(amps2, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(amps3, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(amps4, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(flow8, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(flow7, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(flow6, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(flow5, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(amps8, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(amps7, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(amps5, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(amps6, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(208, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(annunciatorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(flow1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(flow2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(flow3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(flow4, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(amps1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(amps2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(amps3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(amps4, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(flow5, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(flow6, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(flow7, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(flow8, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(amps5, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(amps6, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(amps7, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(amps8, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE))
                .addContainerGap(922, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel3);

        org.openide.awt.Mnemonics.setLocalizedText(jMenu1, "Window");

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem5, org.openide.util.NbBundle.getMessage(MCPUI.class, "PCSUI.jMenuItem5.text")); // NOI18N
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

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem10, org.openide.util.NbBundle.getMessage(MCPUI.class, "PCSUI.jMenuItem10.text")); // NOI18N
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem10);

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem4, org.openide.util.NbBundle.getMessage(MCPUI.class, "PCSUI.jMenuItem4.text")); // NOI18N
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

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem8, org.openide.util.NbBundle.getMessage(MCPUI.class, "TGUI.jMenuItem8.text")); // NOI18N
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem8);

        org.openide.awt.Mnemonics.setLocalizedText(jMenuItem6, org.openide.util.NbBundle.getMessage(MCPUI.class, "MCPUI.jMenuItem6.text")); // NOI18N
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1497, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1461, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        UI.createOrContinue(CoreMap.class, false, false);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void trip1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trip1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_trip1ActionPerformed

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

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        UI.createOrContinue(TGUI.class, true, false);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        UI.createOrContinue(SelsynPanel.class, false, false);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        NPPSim.ui.toFront();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void cavit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cavit1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cavit1ActionPerformed

    private void waterTemp2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterTemp2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterTemp2ActionPerformed

    private void trip2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trip2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_trip2ActionPerformed

    private void cavit2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cavit2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cavit2ActionPerformed

    private void trip5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trip5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_trip5ActionPerformed

    private void trip3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trip3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_trip3ActionPerformed

    private void cavit5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cavit5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cavit5ActionPerformed

    private void waterTemp10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterTemp10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterTemp10ActionPerformed

    private void waterTemp12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterTemp12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterTemp12ActionPerformed

    private void trip6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trip6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_trip6ActionPerformed

    private void cavit6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cavit6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cavit6ActionPerformed

    private void waterTemp3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterTemp3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterTemp3ActionPerformed

    private void cavit3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cavit3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cavit3ActionPerformed

    private void waterTemp11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterTemp11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterTemp11ActionPerformed

    private void trip7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trip7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_trip7ActionPerformed

    private void cavit7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cavit7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cavit7ActionPerformed

    private void waterTemp17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterTemp17ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterTemp17ActionPerformed

    private void cavit4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cavit4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cavit4ActionPerformed

    private void waterTemp19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterTemp19ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterTemp19ActionPerformed

    private void trip8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trip8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_trip8ActionPerformed

    private void trip4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trip4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_trip4ActionPerformed

    private void cavit8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cavit8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cavit8ActionPerformed

    private void waterTemp23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterTemp23ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterTemp23ActionPerformed

    private void precisionIncrement1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionIncrement1ActionPerformed

    }//GEN-LAST:event_precisionIncrement1ActionPerformed

    private void precisionDecrement1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionDecrement1ActionPerformed

    }//GEN-LAST:event_precisionDecrement1ActionPerformed

    private void precisionIncrement2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionIncrement2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_precisionIncrement2ActionPerformed

    private void precisionDecrement2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionDecrement2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_precisionDecrement2ActionPerformed

    private void precisionIncrement5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionIncrement5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_precisionIncrement5ActionPerformed

    private void precisionDecrement5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionDecrement5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_precisionDecrement5ActionPerformed

    private void precisionIncrement6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionIncrement6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_precisionIncrement6ActionPerformed

    private void precisionDecrement6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionDecrement6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_precisionDecrement6ActionPerformed

    private void precisionIncrement3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionIncrement3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_precisionIncrement3ActionPerformed

    private void precisionDecrement3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionDecrement3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_precisionDecrement3ActionPerformed

    private void precisionIncrement4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionIncrement4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_precisionIncrement4ActionPerformed

    private void precisionDecrement4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionDecrement4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_precisionDecrement4ActionPerformed

    private void precisionIncrement7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionIncrement7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_precisionIncrement7ActionPerformed

    private void precisionDecrement7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionDecrement7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_precisionDecrement7ActionPerformed

    private void precisionIncrement8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionIncrement8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_precisionIncrement8ActionPerformed

    private void precisionDecrement8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionDecrement8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_precisionDecrement8ActionPerformed

    private void tgValvesClose18ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesClose18ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesClose18ItemStateChanged

    private void tgValvesStop18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesStop18ActionPerformed
        mcpArray[7].dischargeValve.setState(1);
    }//GEN-LAST:event_tgValvesStop18ActionPerformed

    private void tgValvesStop18ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesStop18ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesStop18ItemStateChanged

    private void tgValvesOpen18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesOpen18ActionPerformed
        mcpArray[7].dischargeValve.setState(2);
    }//GEN-LAST:event_tgValvesOpen18ActionPerformed

    private void tgValvesOpen18ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesOpen18ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesOpen18ItemStateChanged

    private void tgValvesClose17ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesClose17ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesClose17ItemStateChanged

    private void tgValvesStop17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesStop17ActionPerformed
        mcpArray[6].dischargeValve.setState(1);
    }//GEN-LAST:event_tgValvesStop17ActionPerformed

    private void tgValvesStop17ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesStop17ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesStop17ItemStateChanged

    private void tgValvesOpen17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesOpen17ActionPerformed
        mcpArray[6].dischargeValve.setState(2);
    }//GEN-LAST:event_tgValvesOpen17ActionPerformed

    private void tgValvesOpen17ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesOpen17ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesOpen17ItemStateChanged

    private void tgValvesClose5ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesClose5ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesClose5ItemStateChanged

    private void tgValvesStop5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesStop5ActionPerformed
        mcpArray[4].dischargeValve.setState(1);
    }//GEN-LAST:event_tgValvesStop5ActionPerformed

    private void tgValvesStop5ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesStop5ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesStop5ItemStateChanged

    private void tgValvesOpen5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesOpen5ActionPerformed
        mcpArray[4].dischargeValve.setState(2);
    }//GEN-LAST:event_tgValvesOpen5ActionPerformed

    private void tgValvesOpen5ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesOpen5ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesOpen5ItemStateChanged

    private void tgValvesClose6ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesClose6ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesClose6ItemStateChanged

    private void tgValvesStop6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesStop6ActionPerformed
        mcpArray[5].dischargeValve.setState(1);
    }//GEN-LAST:event_tgValvesStop6ActionPerformed

    private void tgValvesStop6ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesStop6ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesStop6ItemStateChanged

    private void tgValvesOpen6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesOpen6ActionPerformed
        mcpArray[5].dischargeValve.setState(2);
    }//GEN-LAST:event_tgValvesOpen6ActionPerformed

    private void tgValvesOpen6ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesOpen6ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesOpen6ItemStateChanged

    private void tgValvesClose3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesClose3ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesClose3ItemStateChanged

    private void tgValvesStop3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesStop3ActionPerformed
        mcpArray[2].dischargeValve.setState(1);
    }//GEN-LAST:event_tgValvesStop3ActionPerformed

    private void tgValvesStop3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesStop3ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesStop3ItemStateChanged

    private void tgValvesOpen3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesOpen3ActionPerformed
        mcpArray[2].dischargeValve.setState(2);
    }//GEN-LAST:event_tgValvesOpen3ActionPerformed

    private void tgValvesOpen3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesOpen3ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesOpen3ItemStateChanged

    private void tgValvesClose14ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesClose14ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesClose14ItemStateChanged

    private void tgValvesStop14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesStop14ActionPerformed
        mcpArray[3].dischargeValve.setState(1);
    }//GEN-LAST:event_tgValvesStop14ActionPerformed

    private void tgValvesStop14ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesStop14ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesStop14ItemStateChanged

    private void tgValvesOpen14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesOpen14ActionPerformed
        mcpArray[3].dischargeValve.setState(2);
    }//GEN-LAST:event_tgValvesOpen14ActionPerformed

    private void tgValvesOpen14ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesOpen14ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesOpen14ItemStateChanged

    private void tgValvesClose12ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesClose12ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesClose12ItemStateChanged

    private void tgValvesStop12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesStop12ActionPerformed
        mcpArray[1].dischargeValve.setState(1);
    }//GEN-LAST:event_tgValvesStop12ActionPerformed

    private void tgValvesStop12ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesStop12ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesStop12ItemStateChanged

    private void tgValvesOpen12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesOpen12ActionPerformed
        mcpArray[1].dischargeValve.setState(2);
    }//GEN-LAST:event_tgValvesOpen12ActionPerformed

    private void tgValvesOpen12ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesOpen12ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tgValvesOpen12ItemStateChanged

    private void tgValvesCloseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesCloseItemStateChanged

    }//GEN-LAST:event_tgValvesCloseItemStateChanged

    private void tgValvesStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesStopActionPerformed
        mcpArray[0].dischargeValve.setState(1);
    }//GEN-LAST:event_tgValvesStopActionPerformed

    private void tgValvesStopItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesStopItemStateChanged

    }//GEN-LAST:event_tgValvesStopItemStateChanged

    private void tgValvesOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesOpenActionPerformed
        mcpArray[0].dischargeValve.setState(2);
    }//GEN-LAST:event_tgValvesOpenActionPerformed

    private void tgValvesOpenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tgValvesOpenItemStateChanged

    }//GEN-LAST:event_tgValvesOpenItemStateChanged

    private void off1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_off1ActionPerformed
        mcpArray[0].setActive(false);
        led1.setLedOn(false);
    }//GEN-LAST:event_off1ActionPerformed

    private void on1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_on1ActionPerformed
        mcpArray[0].setActive(true);
        led1.setLedOn(true);
    }//GEN-LAST:event_on1ActionPerformed

    private void off2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_off2ActionPerformed
        mcpArray[1].setActive(false);
        led2.setLedOn(false);
    }//GEN-LAST:event_off2ActionPerformed

    private void on2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_on2ActionPerformed
        mcpArray[1].setActive(true);
        led2.setLedOn(true);
    }//GEN-LAST:event_on2ActionPerformed

    private void on3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_on3ActionPerformed
        mcpArray[2].setActive(true);
        led3.setLedOn(true);
    }//GEN-LAST:event_on3ActionPerformed

    private void off3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_off3ActionPerformed
        mcpArray[2].setActive(false);
        led3.setLedOn(false);
    }//GEN-LAST:event_off3ActionPerformed

    private void off4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_off4ActionPerformed
        mcpArray[3].setActive(false);
        led4.setLedOn(false);
    }//GEN-LAST:event_off4ActionPerformed

    private void on4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_on4ActionPerformed
        mcpArray[3].setActive(true);
        led4.setLedOn(true);
    }//GEN-LAST:event_on4ActionPerformed

    private void off5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_off5ActionPerformed
        mcpArray[4].setActive(false);
        led5.setLedOn(false);
    }//GEN-LAST:event_off5ActionPerformed

    private void off6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_off6ActionPerformed
        mcpArray[5].setActive(false);
        led6.setLedOn(false);
    }//GEN-LAST:event_off6ActionPerformed

    private void off7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_off7ActionPerformed
        mcpArray[6].setActive(false);
        led7.setLedOn(false);
    }//GEN-LAST:event_off7ActionPerformed

    private void off8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_off8ActionPerformed
        mcpArray[7].setActive(false);
        led8.setLedOn(false);
    }//GEN-LAST:event_off8ActionPerformed

    private void on8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_on8ActionPerformed
        mcpArray[7].setActive(true);
        led8.setLedOn(true);
    }//GEN-LAST:event_on8ActionPerformed

    private void on7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_on7ActionPerformed
        mcpArray[6].setActive(true);
        led7.setLedOn(true);
    }//GEN-LAST:event_on7ActionPerformed

    private void on6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_on6ActionPerformed
        mcpArray[5].setActive(true);
        led6.setLedOn(true);
    }//GEN-LAST:event_on6ActionPerformed

    private void on5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_on5ActionPerformed
        mcpArray[4].setActive(true);
        led5.setLedOn(true);
    }//GEN-LAST:event_on5ActionPerformed

    private void tgValvesClose11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesClose11ActionPerformed
        mcpArray[0].dischargeValve.setState(0);
    }//GEN-LAST:event_tgValvesClose11ActionPerformed

    private void tgValvesClose12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesClose12ActionPerformed
        mcpArray[1].dischargeValve.setState(0);
    }//GEN-LAST:event_tgValvesClose12ActionPerformed

    private void tgValvesClose3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesClose3ActionPerformed
        mcpArray[2].dischargeValve.setState(0);
    }//GEN-LAST:event_tgValvesClose3ActionPerformed

    private void tgValvesClose14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesClose14ActionPerformed
        mcpArray[3].dischargeValve.setState(0);
    }//GEN-LAST:event_tgValvesClose14ActionPerformed

    private void tgValvesClose5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesClose5ActionPerformed
        mcpArray[4].dischargeValve.setState(0);
    }//GEN-LAST:event_tgValvesClose5ActionPerformed

    private void tgValvesClose6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesClose6ActionPerformed
        mcpArray[5].dischargeValve.setState(0);
    }//GEN-LAST:event_tgValvesClose6ActionPerformed

    private void tgValvesClose17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesClose17ActionPerformed
        mcpArray[6].dischargeValve.setState(0);
    }//GEN-LAST:event_tgValvesClose17ActionPerformed

    private void tgValvesClose18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgValvesClose18ActionPerformed
        mcpArray[7].dischargeValve.setState(0);
    }//GEN-LAST:event_tgValvesClose18ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        UI.createOrContinue(PCSUI.class, true, false);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private eu.hansolo.steelseries.gauges.Linear amps1;
    private eu.hansolo.steelseries.gauges.Linear amps2;
    private eu.hansolo.steelseries.gauges.Linear amps3;
    private eu.hansolo.steelseries.gauges.Linear amps4;
    private eu.hansolo.steelseries.gauges.Linear amps5;
    private eu.hansolo.steelseries.gauges.Linear amps6;
    private eu.hansolo.steelseries.gauges.Linear amps7;
    private eu.hansolo.steelseries.gauges.Linear amps8;
    private javax.swing.JPanel annunciatorPanel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.ButtonGroup buttonGroup6;
    private javax.swing.ButtonGroup buttonGroup7;
    private javax.swing.ButtonGroup buttonGroup8;
    private javax.swing.JTextField cavit1;
    private javax.swing.JTextField cavit2;
    private javax.swing.JTextField cavit3;
    private javax.swing.JTextField cavit4;
    private javax.swing.JTextField cavit5;
    private javax.swing.JTextField cavit6;
    private javax.swing.JTextField cavit7;
    private javax.swing.JTextField cavit8;
    private eu.hansolo.steelseries.gauges.Linear flow1;
    private eu.hansolo.steelseries.gauges.Linear flow2;
    private eu.hansolo.steelseries.gauges.Linear flow3;
    private eu.hansolo.steelseries.gauges.Linear flow4;
    private eu.hansolo.steelseries.gauges.Linear flow5;
    private eu.hansolo.steelseries.gauges.Linear flow6;
    private eu.hansolo.steelseries.gauges.Linear flow7;
    private eu.hansolo.steelseries.gauges.Linear flow8;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem8;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private eu.hansolo.steelseries.extras.Led led1;
    private eu.hansolo.steelseries.extras.Led led2;
    private eu.hansolo.steelseries.extras.Led led3;
    private eu.hansolo.steelseries.extras.Led led4;
    private eu.hansolo.steelseries.extras.Led led5;
    private eu.hansolo.steelseries.extras.Led led6;
    private eu.hansolo.steelseries.extras.Led led7;
    private eu.hansolo.steelseries.extras.Led led8;
    private javax.swing.JButton off1;
    private javax.swing.JButton off2;
    private javax.swing.JButton off3;
    private javax.swing.JButton off4;
    private javax.swing.JButton off5;
    private javax.swing.JButton off6;
    private javax.swing.JButton off7;
    private javax.swing.JButton off8;
    private javax.swing.JButton on1;
    private javax.swing.JButton on2;
    private javax.swing.JButton on3;
    private javax.swing.JButton on4;
    private javax.swing.JButton on5;
    private javax.swing.JButton on6;
    private javax.swing.JButton on7;
    private javax.swing.JButton on8;
    private eu.hansolo.steelseries.gauges.Radial2Top pos1;
    private eu.hansolo.steelseries.gauges.Radial2Top pos2;
    private eu.hansolo.steelseries.gauges.Radial2Top pos3;
    private eu.hansolo.steelseries.gauges.Radial2Top pos4;
    private eu.hansolo.steelseries.gauges.Radial2Top pos5;
    private eu.hansolo.steelseries.gauges.Radial2Top pos6;
    private eu.hansolo.steelseries.gauges.Radial2Top pos7;
    private eu.hansolo.steelseries.gauges.Radial2Top pos8;
    private javax.swing.JButton precisionDecrement1;
    private javax.swing.JButton precisionDecrement2;
    private javax.swing.JButton precisionDecrement3;
    private javax.swing.JButton precisionDecrement4;
    private javax.swing.JButton precisionDecrement5;
    private javax.swing.JButton precisionDecrement6;
    private javax.swing.JButton precisionDecrement7;
    private javax.swing.JButton precisionDecrement8;
    private javax.swing.JButton precisionIncrement1;
    private javax.swing.JButton precisionIncrement2;
    private javax.swing.JButton precisionIncrement3;
    private javax.swing.JButton precisionIncrement4;
    private javax.swing.JButton precisionIncrement5;
    private javax.swing.JButton precisionIncrement6;
    private javax.swing.JButton precisionIncrement7;
    private javax.swing.JButton precisionIncrement8;
    private eu.hansolo.steelseries.gauges.Radial rpm1;
    private eu.hansolo.steelseries.gauges.Radial rpm2;
    private eu.hansolo.steelseries.gauges.Radial rpm3;
    private eu.hansolo.steelseries.gauges.Radial rpm4;
    private eu.hansolo.steelseries.gauges.Radial rpm5;
    private eu.hansolo.steelseries.gauges.Radial rpm6;
    private eu.hansolo.steelseries.gauges.Radial rpm7;
    private eu.hansolo.steelseries.gauges.Radial rpm8;
    private javax.swing.JRadioButton tgValvesClose11;
    private javax.swing.JRadioButton tgValvesClose12;
    private javax.swing.JRadioButton tgValvesClose14;
    private javax.swing.JRadioButton tgValvesClose17;
    private javax.swing.JRadioButton tgValvesClose18;
    private javax.swing.JRadioButton tgValvesClose3;
    private javax.swing.JRadioButton tgValvesClose5;
    private javax.swing.JRadioButton tgValvesClose6;
    private javax.swing.JRadioButton tgValvesOpen11;
    private javax.swing.JRadioButton tgValvesOpen12;
    private javax.swing.JRadioButton tgValvesOpen14;
    private javax.swing.JRadioButton tgValvesOpen17;
    private javax.swing.JRadioButton tgValvesOpen18;
    private javax.swing.JRadioButton tgValvesOpen3;
    private javax.swing.JRadioButton tgValvesOpen5;
    private javax.swing.JRadioButton tgValvesOpen6;
    private javax.swing.JRadioButton tgValvesStop11;
    private javax.swing.JRadioButton tgValvesStop12;
    private javax.swing.JRadioButton tgValvesStop14;
    private javax.swing.JRadioButton tgValvesStop17;
    private javax.swing.JRadioButton tgValvesStop18;
    private javax.swing.JRadioButton tgValvesStop3;
    private javax.swing.JRadioButton tgValvesStop5;
    private javax.swing.JRadioButton tgValvesStop6;
    private javax.swing.JTextField trip1;
    private javax.swing.JTextField trip2;
    private javax.swing.JTextField trip3;
    private javax.swing.JTextField trip4;
    private javax.swing.JTextField trip5;
    private javax.swing.JTextField trip6;
    private javax.swing.JTextField trip7;
    private javax.swing.JTextField trip8;
    private javax.swing.JTextField waterTemp10;
    private javax.swing.JTextField waterTemp11;
    private javax.swing.JTextField waterTemp12;
    private javax.swing.JTextField waterTemp17;
    private javax.swing.JTextField waterTemp19;
    private javax.swing.JTextField waterTemp2;
    private javax.swing.JTextField waterTemp23;
    private javax.swing.JTextField waterTemp3;
    // End of variables declaration//GEN-END:variables
}
