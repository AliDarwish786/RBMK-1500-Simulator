package com.darwish.nppsim;

import static com.darwish.nppsim.NPPSim.autoControl;
import static com.darwish.nppsim.NPPSim.core;
import static com.darwish.nppsim.NPPSim.msvLoop1;
import static com.darwish.nppsim.NPPSim.msvLoop2;
import static com.darwish.nppsim.NPPSim.sdv_a;
import static com.darwish.nppsim.NPPSim.tg1;
import static com.darwish.nppsim.NPPSim.tg2;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel; //TODO
import org.netbeans.swing.laf.dark.*;
import static com.darwish.nppsim.NPPSim.mcc;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.metal.MetalToggleButtonUI;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class UI extends javax.swing.JFrame implements Serializable {
    static Color BACKGROUND = new Color(170, 130, 40);
    static ArrayList<ControlRodChannel> selectedControlRods;
    static ArrayList<UIUpdateable> elementsToUpdate; 
    static final ArrayList<Thread> uiThreads = new ArrayList<>();
    final Annunciator annunciator;
    private boolean debounce = false;
    private static long uiUpdateRate = 50;
    static boolean hush = true;
    
    public UI() {
        hush = true;
        try {
            javax.swing.UIManager.setLookAndFeel(new DarkMetalLookAndFeel());
            //UI.BACKGROUND = new Color(115, 53, 0); //TODO
            //javax.swing.UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        initComponents();
        selectedControlRods = new ArrayList<>();
        elementsToUpdate = new ArrayList<>();
        annunciator = new Annunciator(annunciatorPanel);
        this.setVisible(true);
        toggleAR1.setUI(new MetalToggleButtonUI() {
            @Override
            protected Color getSelectColor() {
                return Annunciator.BLUEON_COLOR;
            }
        });
        toggleAR2.setUI(new MetalToggleButtonUI() {
            @Override
            protected Color getSelectColor() {
                return Annunciator.BLUEON_COLOR;
            }
        });
        toggleLAC.setUI(new MetalToggleButtonUI() {
            @Override
            protected Color getSelectColor() {
                return Annunciator.BLUEON_COLOR;
            }
        });
        
        initializeDialUpdateThread();
        precisionController();
        
        //set values
        toggleAR1.getModel().setSelected(autoControl.automaticRodController.isEnabled()[1]);
        toggleAR2.getModel().setSelected(autoControl.automaticRodController.isEnabled()[2]);
        toggleLAC.getModel().setSelected(autoControl.automaticRodController.isEnabled()[0]);
        setpoint.setLcdValue(autoControl.automaticRodController.getSetpoint());
        createOrContinue(PCSUI.class, true, true);
        createOrContinue(TGUI.class, true, true);
        createOrContinue(CondensateUI.class, true, true);
        createOrContinue(FeedwaterUI.class, true, true);
        createOrContinue(DearatorUI.class, true, true);
        createOrContinue(MCPUI.class, true, true);
    }
    
    public void update() {
        setTitle(NPPMath.formatSecondsToDaysAndTime(autoControl.getSimulationTime(), true));
        for (UIUpdateable i: elementsToUpdate) {
            i.update();
        }
        mTK1.update();
        double currentNeutronFlux = core.getNeutronCount();
        //double kEffective = Math.pow(currentNeutronFlux / previousNeutronFlux, 1 / 10.0);
        double reactivity = core.getReactivity();//(kEffective - 1) / kEffective;
        double reactorPeriod = core.getPeriod();//0.1 / (kEffective - 1);
        double thermalPower = core.getThermalPower();

        java.awt.EventQueue.invokeLater(() -> {
            thermalPower1.setLcdValue(thermalPower);
            neutronFlux.setLcdValue(currentNeutronFlux / 4.9808177502E13);
            keff.setLcdValue(reactivity);
            period.setLcdValue(reactorPeriod < -9999 || reactorPeriod > 9999 ? Double.POSITIVE_INFINITY : reactorPeriod);
            deltaKeff.setValue(reactivity * 100);
            drumTemp.setLcdValue(mcc.drum1.getWaterTemperature());
            drumTemp1.setLcdValue(mcc.drum2.getWaterTemperature());
            headerTemp.setLcdValue(mcc.pHeader1.getWaterTemperature());
            headerTemp1.setLcdValue(mcc.pHeader2.getWaterTemperature());
            suctionTemp.setLcdValue(mcc.sHeader1.getWaterTemperature());
            suctionTemp1.setLcdValue(mcc.sHeader2.getWaterTemperature());
            headerFlow.setLcdValue(mcc.pHeader1.getWaterInflowRate());
            headerFlow1.setLcdValue(mcc.pHeader2.getWaterInflowRate());
            electric1.setLcdValue(tg1.getGeneratorLoad());
            electric2.setLcdValue(tg2.getGeneratorLoad());
            press1.setLcdValue(mcc.drum1.getPressure());
            press2.setLcdValue(mcc.drum2.getPressure());
        });
        
        checkAlarms();
        //reset flow rates for all elements that implement the UIUpdateable interface
        core.coreArray.forEach(row -> {
            row.forEach(channel -> {
                channel.updateTableValues();
                channel.resetFlowRates();
            });
        });
        mcc.drum1.resetFlowRates();
        mcc.drum2.resetFlowRates();
        hush = false;
    }
    
    public void initializeDialUpdateThread() {
        UI.uiThreads.add(
            new Thread(() -> {
                try {
                    while (true) {
                        annunciator.update();
                        if (this.isFocused()) {
                            manualRodControl1.update();
                            java.awt.EventQueue.invokeLater(() -> {
                                if (autoControl.az1Control.isTripped()) {
                                    rodLimit1.setBackground(Annunciator.REDON_COLOR);
                                } else {
                                    rodLimit1.setBackground(Annunciator.REDOFF_COLOR);
                                }
                                if (autoControl.fasrControl.isTripped()) {
                                    rodLimit10.setBackground(Annunciator.REDON_COLOR);
                                } else {
                                    rodLimit10.setBackground(Annunciator.REDOFF_COLOR);
                                }
                                if (autoControl.automaticRodController.isEnabled()[0]) {
                                    if (autoControl.automaticRodController.onLimit()[0]) {
                                        limitLAC.setBackground(Annunciator.YELLOWON_COLOR);
                                    } else {
                                        limitLAC.setBackground(Annunciator.YELLOWOFF_COLOR);
                                    }
                                    if (autoControl.automaticRodController.hasError()[0]) {
                                        errorLAC.setBackground(Annunciator.YELLOWON_COLOR);
                                    } else {
                                        errorLAC.setBackground(Annunciator.YELLOWOFF_COLOR);
                                    }
                                    if (autoControl.automaticRodController.isBusy()[0]) {
                                        busyLAC.setBackground(Annunciator.GREENON_COLOR);
                                    } else {
                                        busyLAC.setBackground(Annunciator.GREENOFF_COLOR);
                                    }
                                }
                                if (autoControl.automaticRodController.isEnabled()[1]) {
                                    if (autoControl.automaticRodController.onLimit()[1]) {
                                        limit1AR.setBackground(Annunciator.YELLOWON_COLOR);
                                    } else {
                                        limit1AR.setBackground(Annunciator.YELLOWOFF_COLOR);
                                    }
                                    if (autoControl.automaticRodController.hasError()[1]) {
                                        error1AR.setBackground(Annunciator.YELLOWON_COLOR);
                                    } else {
                                        error1AR.setBackground(Annunciator.YELLOWOFF_COLOR);
                                    }
                                    if (autoControl.automaticRodController.isBusy()[1]) {
                                        busy1AR.setBackground(Annunciator.GREENON_COLOR);
                                    } else {
                                        busy1AR.setBackground(Annunciator.GREENOFF_COLOR);
                                    }
                                }
                                if (autoControl.automaticRodController.isEnabled()[2]) {
                                    if (autoControl.automaticRodController.onLimit()[2]) {
                                        limit2AR.setBackground(Annunciator.YELLOWON_COLOR);
                                    } else {
                                        limit2AR.setBackground(Annunciator.YELLOWOFF_COLOR);
                                    }
                                    if (autoControl.automaticRodController.hasError()[2]) {
                                        error2AR.setBackground(Annunciator.YELLOWON_COLOR);
                                    } else {
                                        error2AR.setBackground(Annunciator.YELLOWOFF_COLOR);
                                    }
                                    if (autoControl.automaticRodController.isBusy()[2]) {
                                        busy2AR.setBackground(Annunciator.GREENON_COLOR);
                                    } else {
                                        busy2AR.setBackground(Annunciator.GREENOFF_COLOR);
                                    }
                                }
                            });
                        }
                        Thread.sleep(50);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            })
        );
        UI.uiThreads.get(UI.uiThreads.size() - 1).start();
    }
    
    private void checkAlarms() { 
        boolean sdvA = false;
        boolean msvI = false;
        boolean msvII = false;
        boolean msvIII = false;
        for (int i = 0; i < sdv_a.size(); i++) {
            if (sdv_a.get(i).getPosition() != 0.0f) {
                sdvA= true;
                break;
            }
        }
        if (msvLoop1.get(0).getPosition() != 0.0f || msvLoop2.get(0).getPosition() != 0.0f) {
            msvI = true;
        }
        for (int i = 1; i < 3; i++) {
            if (msvLoop1.get(i).getPosition() != 0.0f || msvLoop2.get(i).getPosition() != 0.0f) {
                msvII= true;
                break;
            }
        }
        for (int i = 3; i < 6; i++) {
            if (msvLoop1.get(i).getPosition() != 0.0f || msvLoop2.get(i).getPosition() != 0.0f) {
                msvIII= true;
                break;
            }
        }
        boolean arInop = (autoControl.automaticRodController.isEnabled()[1] && autoControl.automaticRodController.hasError()[1] && autoControl.automaticRodController.onLimit()[1]) || (autoControl.automaticRodController.isEnabled()[2] && autoControl.automaticRodController.hasError()[2] && autoControl.automaticRodController.onLimit()[2]);
        boolean lacInop = autoControl.automaticRodController.isEnabled()[0] && autoControl.automaticRodController.hasError()[0] && autoControl.automaticRodController.onLimit()[0];
        double currentPeriod = core.getPeriod();
        boolean periodLess30 = currentPeriod < 30 && currentPeriod > 0;
        annunciator.setTrigger(mcc.drum1.getWaterLevel() < -25 || mcc.drum2.getWaterLevel() < -25, lowWaterLevel);
        annunciator.setTrigger(mcc.drum1.getWaterLevel() > 25 || mcc.drum2.getWaterLevel() > 25, highWaterLevel);
        annunciator.setTrigger(mcc.drum1.getPressure() > 7.02 || mcc.drum2.getPressure() > 7.02, highDrumPress);
        annunciator.setTrigger(mcc.drum1.getPressure() < 5 || mcc.drum2.getPressure() < 5, lowDrumPress);
        annunciator.setTrigger(mcc.drum1.getPressure() > 7.02 || mcc.drum2.getPressure() > 7.02, highDrumPress);
        annunciator.setTrigger(msvI, msv1);
        annunciator.setTrigger(msvII, msv2);
        annunciator.setTrigger(msvIII, msv3);
        annunciator.setTrigger(sdvA, sdv_aOpen);
        annunciator.setTrigger(arInop, arINOP);
        annunciator.setTrigger(lacInop, lacINOP);
        annunciator.setTrigger(periodLess30, t30);
        annunciator.setTrigger(core.getThermalPower() > 5000, power);
        annunciator.setTrigger(core.getReactivity() > 0.01, nRate);
    }
    
    private void precisionController() {
        UI.uiThreads.add(
            new Thread(() -> {
                while(true) {
                    final boolean[] travelling = {false};
                    final boolean[] sequenceBlock = {autoControl.fasrControl.getSequenceBlock()};
                    try {
                        selectedControlRods.forEach(channel -> {
                            if (rodsOut.getModel().isPressed()) {
                                if (selectedControlRods.size() > 4) {
                                    rodLimit.setBackground(Annunciator.YELLOWON_COLOR);
                                } else {
                                    if (sequenceBlock[0]) {
                                        if (channel instanceof FASRChannel) {
                                            if (channel.getPosition() > 0) {
                                            channel.setState(0);
                                            travelling[0] = true;
                                            }
                                        } else {
                                            rodSeq.setBackground(Annunciator.YELLOWON_COLOR);
                                            channel.setState(1);
                                        }
                                    } else if (channel.getPosition() > 0) {
                                        channel.setState(0);
                                        travelling[0] = true;
                                    }
                                }
                            } else if (rodsIn.getModel().isPressed()){
                                if (channel.getPosition() < 1) {
                                    channel.setState(2);
                                    travelling[0] = true;
                                }
                            } else {
                                channel.setState(1);
                                rodSeq.setBackground(Annunciator.YELLOWOFF_COLOR);
                                rodLimit.setBackground(Annunciator.YELLOWOFF_COLOR);
                            }
                        });
                        if (travelling[0]) {
                            rodTravel.setBackground(Annunciator.GREENON_COLOR);
                        } else {
                            rodTravel.setBackground(Annunciator.GREENOFF_COLOR);
                        }
                        if (!sequenceBlock[0]) {
                            rodSeq.setBackground(Annunciator.YELLOWOFF_COLOR);
                        }
                        
                        if (debounce) {
                            Thread.sleep(50);
                            debounce = false;
                        }
                        if (precisionIncrement2.getModel().isPressed()) {
                            precisionIncrement2ActionPerformed(null);
                        } else if (precisionDecrement2.getModel().isPressed()) {
                            precisionDecrement2ActionPerformed(null);
                        }
                        
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            })
        );
        uiThreads.get(uiThreads.size() - 1).start();
    }

    public static void createOrContinue(Class element, boolean fullscreen, boolean justInit) {
        for (UIUpdateable i: elementsToUpdate) {
            if (i.getClass() == element) {
                i.setVisibility(true);
                ((JFrame)i).toFront();
                ((JFrame)i).requestFocus();
                return;
            }
        }
        try {
            Object newElement = element.getDeclaredConstructor().newInstance();
            if (fullscreen) {
                ((javax.swing.JFrame)newElement).setSize(1366, 768); //2000,2000
            }
            if (!justInit) {
                ((UIUpdateable)newElement).setVisibility(true);
            }
            elementsToUpdate.add((UIUpdateable)newElement);
        } catch (Exception e) {
            e.printStackTrace();
            new ErrorWindow("Error loading UI element class" , ExceptionUtils.getStackTrace(e), false).setVisible(true);
        }
    }
    
    public static long getUpdateRate() {
        return uiUpdateRate;
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
        annunciatorPanel = new javax.swing.JPanel();
        highWaterLevel = new javax.swing.JTextField();
        highDrumPress = new javax.swing.JTextField();
        msv1 = new javax.swing.JTextField();
        nRate = new javax.swing.JTextField();
        arINOP = new javax.swing.JTextField();
        lowWaterLevel = new javax.swing.JTextField();
        sdv_aOpen = new javax.swing.JTextField();
        msv2 = new javax.swing.JTextField();
        t30 = new javax.swing.JTextField();
        lacINOP = new javax.swing.JTextField();
        mcpCavit = new javax.swing.JTextField();
        lowDrumPress = new javax.swing.JTextField();
        msv3 = new javax.swing.JTextField();
        power = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        manualRodControl1 = new com.darwish.nppsim.ManualRodControl();
        jPanel1 = new javax.swing.JPanel();
        rodLimit = new javax.swing.JTextField();
        rodSeq = new javax.swing.JTextField();
        rodTravel = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        rodLimit1 = new javax.swing.JTextField();
        rodLimit2 = new javax.swing.JTextField();
        rodLimit3 = new javax.swing.JTextField();
        rodLimit10 = new javax.swing.JTextField();
        rodLimit4 = new javax.swing.JTextField();
        rodLimit5 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        rodsOut1 = new javax.swing.JButton();
        rodsOut = new javax.swing.JButton();
        rodsIn = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        keff = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        period = new eu.hansolo.steelseries.gauges.DisplaySingle();
        neutronFlux = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel7 = new javax.swing.JLabel();
        deltaKeff = new eu.hansolo.steelseries.gauges.Linear();
        jLabel3 = new javax.swing.JLabel();
        electric2 = new eu.hansolo.steelseries.gauges.DisplayRectangular();
        jPanel7 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        setpoint = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel4 = new javax.swing.JLabel();
        toggleLAC = new javax.swing.JToggleButton();
        jPanel8 = new javax.swing.JPanel();
        toggleAR2 = new javax.swing.JToggleButton();
        toggleAR1 = new javax.swing.JToggleButton();
        precisionIncrement2 = new javax.swing.JButton();
        precisionDecrement2 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        busy1AR = new javax.swing.JTextField();
        busy2AR = new javax.swing.JTextField();
        busyLAC = new javax.swing.JTextField();
        limit1AR = new javax.swing.JTextField();
        limit2AR = new javax.swing.JTextField();
        limitLAC = new javax.swing.JTextField();
        error1AR = new javax.swing.JTextField();
        error2AR = new javax.swing.JTextField();
        errorLAC = new javax.swing.JTextField();
        thermalPower1 = new eu.hansolo.steelseries.gauges.DisplayRectangular();
        electric1 = new eu.hansolo.steelseries.gauges.DisplayRectangular();
        mTK1 = new com.darwish.nppsim.MTK();
        jPanel9 = new javax.swing.JPanel();
        drumTemp = new eu.hansolo.steelseries.gauges.DisplaySingle();
        drumTemp1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        suctionTemp = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel51 = new javax.swing.JLabel();
        suctionTemp1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel52 = new javax.swing.JLabel();
        press1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        press2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        headerTemp1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel55 = new javax.swing.JLabel();
        headerTemp = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel56 = new javax.swing.JLabel();
        headerFlow1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel57 = new javax.swing.JLabel();
        headerFlow = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabel58 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        pause = new javax.swing.JCheckBoxMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem2 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem3 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem4 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem5 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem6 = new javax.swing.JRadioButtonMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();
        mtkPlus = new javax.swing.JRadioButtonMenuItem();
        mtkMin = new javax.swing.JRadioButtonMenuItem();
        jMenu6 = new javax.swing.JMenu();
        zero5 = new javax.swing.JRadioButtonMenuItem();
        one = new javax.swing.JRadioButtonMenuItem();
        two5 = new javax.swing.JRadioButtonMenuItem();
        five = new javax.swing.JRadioButtonMenuItem();
        ten = new javax.swing.JRadioButtonMenuItem();
        fifteen = new javax.swing.JRadioButtonMenuItem();
        twenty = new javax.swing.JRadioButtonMenuItem();
        twentyfive = new javax.swing.JRadioButtonMenuItem();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(UI.BACKGROUND);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jScrollPane1.setBackground(UI.BACKGROUND);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(1366, 768));

        jPanel3.setBackground(BACKGROUND);
        jPanel3.setMinimumSize(new java.awt.Dimension(0, 0));

        annunciatorPanel.setBackground(UI.BACKGROUND);
        annunciatorPanel.setLayout(new java.awt.GridLayout(3, 5));

        highWaterLevel.setEditable(false);
        highWaterLevel.setBackground(new java.awt.Color(142, 0, 0));
        highWaterLevel.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        highWaterLevel.setForeground(new java.awt.Color(0, 0, 0));
        highWaterLevel.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        highWaterLevel.setText("High water level");
        highWaterLevel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        annunciatorPanel.add(highWaterLevel);

        highDrumPress.setEditable(false);
        highDrumPress.setBackground(new java.awt.Color(142, 0, 0));
        highDrumPress.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        highDrumPress.setForeground(new java.awt.Color(0, 0, 0));
        highDrumPress.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        highDrumPress.setText("High drum pressure");
        highDrumPress.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        highDrumPress.setPreferredSize(new java.awt.Dimension(100, 30));
        highDrumPress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                highDrumPressActionPerformed(evt);
            }
        });
        annunciatorPanel.add(highDrumPress);

        msv1.setEditable(false);
        msv1.setBackground(new java.awt.Color(142, 0, 0));
        msv1.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        msv1.setForeground(new java.awt.Color(0, 0, 0));
        msv1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        msv1.setText("MSV group I");
        msv1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        annunciatorPanel.add(msv1);

        nRate.setEditable(false);
        nRate.setBackground(new java.awt.Color(142, 0, 0));
        nRate.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        nRate.setForeground(new java.awt.Color(0, 0, 0));
        nRate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        nRate.setText("Neuton rate");
        nRate.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        annunciatorPanel.add(nRate);

        arINOP.setEditable(false);
        arINOP.setBackground(new java.awt.Color(142, 0, 0));
        arINOP.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        arINOP.setForeground(new java.awt.Color(0, 0, 0));
        arINOP.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        arINOP.setText("AR Authority");
        arINOP.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        annunciatorPanel.add(arINOP);

        lowWaterLevel.setEditable(false);
        lowWaterLevel.setBackground(new java.awt.Color(142, 0, 0));
        lowWaterLevel.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        lowWaterLevel.setForeground(new java.awt.Color(0, 0, 0));
        lowWaterLevel.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        lowWaterLevel.setText("Low water level");
        lowWaterLevel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        annunciatorPanel.add(lowWaterLevel);

        sdv_aOpen.setEditable(false);
        sdv_aOpen.setBackground(new java.awt.Color(142, 0, 0));
        sdv_aOpen.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        sdv_aOpen.setForeground(new java.awt.Color(0, 0, 0));
        sdv_aOpen.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sdv_aOpen.setText("SDV-A open");
        sdv_aOpen.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        annunciatorPanel.add(sdv_aOpen);

        msv2.setEditable(false);
        msv2.setBackground(new java.awt.Color(142, 0, 0));
        msv2.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        msv2.setForeground(new java.awt.Color(0, 0, 0));
        msv2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        msv2.setText("MSV group II");
        msv2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        msv2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                msv2ActionPerformed(evt);
            }
        });
        annunciatorPanel.add(msv2);

        t30.setEditable(false);
        t30.setBackground(new java.awt.Color(107, 103, 0));
        t30.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        t30.setForeground(new java.awt.Color(0, 0, 0));
        t30.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        t30.setText("T < 30");
        t30.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        annunciatorPanel.add(t30);

        lacINOP.setEditable(false);
        lacINOP.setBackground(new java.awt.Color(142, 0, 0));
        lacINOP.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        lacINOP.setForeground(new java.awt.Color(0, 0, 0));
        lacINOP.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        lacINOP.setText("LAC Authority");
        lacINOP.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        annunciatorPanel.add(lacINOP);

        mcpCavit.setEditable(false);
        mcpCavit.setBackground(new java.awt.Color(142, 0, 0));
        mcpCavit.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        mcpCavit.setForeground(new java.awt.Color(0, 0, 0));
        mcpCavit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mcpCavit.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        mcpCavit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mcpCavitActionPerformed(evt);
            }
        });
        annunciatorPanel.add(mcpCavit);

        lowDrumPress.setEditable(false);
        lowDrumPress.setBackground(new java.awt.Color(107, 103, 0));
        lowDrumPress.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        lowDrumPress.setForeground(new java.awt.Color(0, 0, 0));
        lowDrumPress.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        lowDrumPress.setText("Low drum pressure");
        lowDrumPress.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        annunciatorPanel.add(lowDrumPress);

        msv3.setEditable(false);
        msv3.setBackground(new java.awt.Color(142, 0, 0));
        msv3.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        msv3.setForeground(new java.awt.Color(0, 0, 0));
        msv3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        msv3.setText("MSV group III");
        msv3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        annunciatorPanel.add(msv3);

        power.setEditable(false);
        power.setBackground(new java.awt.Color(107, 103, 0));
        power.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        power.setForeground(new java.awt.Color(0, 0, 0));
        power.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        power.setText("High Power");
        power.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        power.setPreferredSize(new java.awt.Dimension(100, 30));
        power.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                powerActionPerformed(evt);
            }
        });
        annunciatorPanel.add(power);

        jButton1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jButton1.setText("ACK");
        jButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton1.setMaximumSize(new java.awt.Dimension(50, 50));
        jButton1.setMinimumSize(new java.awt.Dimension(50, 50));
        jButton1.setPreferredSize(new java.awt.Dimension(50, 50));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        manualRodControl1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        manualRodControl1.setMinimumSize(new java.awt.Dimension(500, 600));
        manualRodControl1.setPreferredSize(new java.awt.Dimension(600, 400));

        jPanel1.setLayout(new java.awt.GridLayout(2, 3));

        rodLimit.setEditable(false);
        rodLimit.setBackground(new java.awt.Color(107, 103, 0));
        rodLimit.setFont(new java.awt.Font("Dialog", 1, 8)); // NOI18N
        rodLimit.setForeground(new java.awt.Color(0, 0, 0));
        rodLimit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        rodLimit.setText("selection limit");
        rodLimit.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel1.add(rodLimit);

        rodSeq.setEditable(false);
        rodSeq.setBackground(new java.awt.Color(107, 103, 0));
        rodSeq.setFont(new java.awt.Font("Dialog", 1, 8)); // NOI18N
        rodSeq.setForeground(new java.awt.Color(0, 0, 0));
        rodSeq.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        rodSeq.setText("rod sequence");
        rodSeq.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel1.add(rodSeq);

        rodTravel.setEditable(false);
        rodTravel.setBackground(new java.awt.Color(27, 113, 27));
        rodTravel.setFont(new java.awt.Font("Dialog", 1, 8)); // NOI18N
        rodTravel.setForeground(new java.awt.Color(0, 0, 0));
        rodTravel.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        rodTravel.setText("rod traversal");
        rodTravel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel1.add(rodTravel);

        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jButton3.setBackground(new java.awt.Color(183, 0, 0));
        jButton3.setText("AZ-1K");
        jButton3.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton2.setBackground(java.awt.SystemColor.controlDkShadow);
        jButton2.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jButton2.setText("Reset AZ");
        jButton2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel2.setLayout(new java.awt.GridLayout(2, 3));

        rodLimit1.setEditable(false);
        rodLimit1.setBackground(new java.awt.Color(142, 0, 0));
        rodLimit1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        rodLimit1.setForeground(new java.awt.Color(0, 0, 0));
        rodLimit1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        rodLimit1.setText("AZ-1");
        rodLimit1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel2.add(rodLimit1);

        rodLimit2.setEditable(false);
        rodLimit2.setBackground(new java.awt.Color(142, 0, 0));
        rodLimit2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        rodLimit2.setForeground(new java.awt.Color(0, 0, 0));
        rodLimit2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        rodLimit2.setText("AZ-2");
        rodLimit2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel2.add(rodLimit2);

        rodLimit3.setEditable(false);
        rodLimit3.setBackground(new java.awt.Color(142, 0, 0));
        rodLimit3.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        rodLimit3.setForeground(new java.awt.Color(0, 0, 0));
        rodLimit3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        rodLimit3.setText("AZ-6");
        rodLimit3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel2.add(rodLimit3);

        rodLimit10.setEditable(false);
        rodLimit10.setBackground(new java.awt.Color(142, 0, 0));
        rodLimit10.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        rodLimit10.setForeground(new java.awt.Color(0, 0, 0));
        rodLimit10.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        rodLimit10.setText("BAZ");
        rodLimit10.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel2.add(rodLimit10);

        rodLimit4.setEditable(false);
        rodLimit4.setBackground(new java.awt.Color(107, 103, 0));
        rodLimit4.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        rodLimit4.setForeground(new java.awt.Color(0, 0, 0));
        rodLimit4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        rodLimit4.setText("AZ-4");
        rodLimit4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel2.add(rodLimit4);

        rodLimit5.setEditable(false);
        rodLimit5.setBackground(new java.awt.Color(107, 103, 0));
        rodLimit5.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        rodLimit5.setForeground(new java.awt.Color(0, 0, 0));
        rodLimit5.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        rodLimit5.setText("AZ-3");
        rodLimit5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel2.add(rodLimit5);

        jButton4.setBackground(new java.awt.Color(183, 0, 0));
        jButton4.setText("BSM");
        jButton4.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        rodsOut1.setText("RESET");
        rodsOut1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rodsOut1ActionPerformed(evt);
            }
        });

        rodsOut.setText("OUT");
        rodsOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rodsOutActionPerformed(evt);
            }
        });

        rodsIn.setText("IN");
        rodsIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rodsInActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(rodsOut1, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(rodsOut, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(rodsIn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rodsOut1)
                .addGap(50, 50, 50)
                .addComponent(rodsOut)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rodsIn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        keff.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        keff.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        keff.setLcdDecimals(4);
        keff.setLcdUnitString("");
        keff.setLcdUnitStringVisible(false);
        keff.setMinimumSize(new java.awt.Dimension(94, 23));
        keff.setSize(new java.awt.Dimension(94, 23));

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel5.setText("ρ");

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel6.setText("T");

        period.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        period.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        period.setLcdDecimals(0);
        period.setLcdMaxValue(9999.0);
        period.setLcdMinValue(-9999.0);
        period.setLcdUnitString("");
        period.setLcdUnitStringVisible(false);
        period.setLcdValue(999.0);
        period.setMinimumSize(new java.awt.Dimension(94, 23));
        period.setSize(new java.awt.Dimension(94, 23));

        neutronFlux.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        neutronFlux.setCustomLcdUnitFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        neutronFlux.setFont(new java.awt.Font("Dialog", 0, 5)); // NOI18N
        neutronFlux.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        neutronFlux.setLcdDecimals(4);
        neutronFlux.setLcdScientificFormat(true);
        neutronFlux.setLcdUnitString("");
        neutronFlux.setLcdUnitStringVisible(false);
        neutronFlux.setLcdValue(1.43E-14);
        neutronFlux.setMinimumSize(new java.awt.Dimension(94, 23));
        neutronFlux.setSize(new java.awt.Dimension(94, 23));

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel7.setText("N");

        deltaKeff.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.SATIN_GRAY);
        deltaKeff.setFrameVisible(false);
        deltaKeff.setLcdValueFont(new java.awt.Font("Verdana", 0, 36)); // NOI18N
        deltaKeff.setLcdVisible(false);
        deltaKeff.setLedVisible(false);
        deltaKeff.setMaxNoOfMajorTicks(5);
        deltaKeff.setMaxNoOfMinorTicks(5);
        deltaKeff.setMaxValue(10.0);
        deltaKeff.setMinValue(-10.0);
        deltaKeff.setTitle("%ΔK/K");
        deltaKeff.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        deltaKeff.setTrackSectionColor(new java.awt.Color(255, 0, 0));
        deltaKeff.setTrackStart(800.0);
        deltaKeff.setTrackStartColor(new java.awt.Color(255, 0, 0));
        deltaKeff.setUnitString("kg/s");
        deltaKeff.setUnitStringVisible(false);
        deltaKeff.setValueColor(eu.hansolo.steelseries.tools.ColorDef.YELLOW);

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        jLabel3.setText("Reactivity");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(neutronFlux, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(period, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(keff, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(deltaKeff, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 4, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(period, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(jLabel3)))
                .addGap(11, 11, 11)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(keff, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(11, 11, 11)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(neutronFlux, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(deltaKeff, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        electric2.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.BEIGE);
        electric2.setBackgroundVisible(false);
        electric2.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.BLACK_METAL);
        electric2.setFrameVisible(false);
        electric2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.STANDARD_LCD);
        electric2.setLcdUnitString("MWe");
        electric2.setLcdUnitStringVisible(true);
        electric2.setMaxValue(1000.0);
        electric2.setSize(new java.awt.Dimension(170, 90));

        jPanel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel9.setText("Setpoint");

        setpoint.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        setpoint.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        setpoint.setLcdDecimals(0);
        setpoint.setLcdMaxValue(5200.0);
        setpoint.setLcdUnitString("");
        setpoint.setLcdUnitStringVisible(false);
        setpoint.setMinimumSize(new java.awt.Dimension(94, 23));
        setpoint.setSize(new java.awt.Dimension(94, 23));

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        jLabel4.setText("Auto-Control");

        toggleLAC.setBackground(Annunciator.BLUEOFF_COLOR);
        toggleLAC.setText("LAC");
        toggleLAC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleLACActionPerformed(evt);
            }
        });

        jPanel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));

        toggleAR2.setBackground(Annunciator.BLUEOFF_COLOR);
        toggleAR2.setText("2AR");
        toggleAR2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleAR2ActionPerformed(evt);
            }
        });

        toggleAR1.setBackground(Annunciator.BLUEOFF_COLOR);
        toggleAR1.setText("1AR");
        toggleAR1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleAR1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(toggleAR2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                    .addComponent(toggleAR1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(toggleAR1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toggleAR2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        precisionIncrement2.setText(org.openide.util.NbBundle.getMessage(UI.class, "TGUI.precisionIncrement2.text")); // NOI18N
        precisionIncrement2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionIncrement2.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionIncrement2.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionIncrement2.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionIncrement2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionIncrement2ActionPerformed(evt);
            }
        });

        precisionDecrement2.setText(org.openide.util.NbBundle.getMessage(UI.class, "TGUI.precisionDecrement2.text")); // NOI18N
        precisionDecrement2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        precisionDecrement2.setMaximumSize(new java.awt.Dimension(30, 30));
        precisionDecrement2.setMinimumSize(new java.awt.Dimension(25, 25));
        precisionDecrement2.setPreferredSize(new java.awt.Dimension(30, 30));
        precisionDecrement2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precisionDecrement2ActionPerformed(evt);
            }
        });

        jPanel10.setLayout(new java.awt.GridLayout(3, 3));

        busy1AR.setEditable(false);
        busy1AR.setBackground(new java.awt.Color(27, 113, 27));
        busy1AR.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        busy1AR.setForeground(new java.awt.Color(0, 0, 0));
        busy1AR.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        busy1AR.setText("1AR");
        busy1AR.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel10.add(busy1AR);

        busy2AR.setEditable(false);
        busy2AR.setBackground(new java.awt.Color(27, 113, 27));
        busy2AR.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        busy2AR.setForeground(new java.awt.Color(0, 0, 0));
        busy2AR.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        busy2AR.setText("2AR");
        busy2AR.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel10.add(busy2AR);

        busyLAC.setEditable(false);
        busyLAC.setBackground(new java.awt.Color(27, 113, 27));
        busyLAC.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        busyLAC.setForeground(new java.awt.Color(0, 0, 0));
        busyLAC.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        busyLAC.setText("LAC");
        busyLAC.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel10.add(busyLAC);

        limit1AR.setEditable(false);
        limit1AR.setBackground(new java.awt.Color(107, 103, 0));
        limit1AR.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        limit1AR.setForeground(new java.awt.Color(0, 0, 0));
        limit1AR.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        limit1AR.setText("limit");
        limit1AR.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel10.add(limit1AR);

        limit2AR.setEditable(false);
        limit2AR.setBackground(new java.awt.Color(107, 103, 0));
        limit2AR.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        limit2AR.setForeground(new java.awt.Color(0, 0, 0));
        limit2AR.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        limit2AR.setText("limit");
        limit2AR.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel10.add(limit2AR);

        limitLAC.setEditable(false);
        limitLAC.setBackground(new java.awt.Color(107, 103, 0));
        limitLAC.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        limitLAC.setForeground(new java.awt.Color(0, 0, 0));
        limitLAC.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        limitLAC.setText("limit");
        limitLAC.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel10.add(limitLAC);

        error1AR.setEditable(false);
        error1AR.setBackground(new java.awt.Color(107, 103, 0));
        error1AR.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        error1AR.setForeground(new java.awt.Color(0, 0, 0));
        error1AR.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        error1AR.setText("error");
        error1AR.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel10.add(error1AR);

        error2AR.setEditable(false);
        error2AR.setBackground(new java.awt.Color(107, 103, 0));
        error2AR.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        error2AR.setForeground(new java.awt.Color(0, 0, 0));
        error2AR.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        error2AR.setText("error");
        error2AR.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel10.add(error2AR);

        errorLAC.setEditable(false);
        errorLAC.setBackground(new java.awt.Color(107, 103, 0));
        errorLAC.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        errorLAC.setForeground(new java.awt.Color(0, 0, 0));
        errorLAC.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        errorLAC.setText("error");
        errorLAC.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel10.add(errorLAC);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(toggleLAC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel9)
                                .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addComponent(setpoint, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(precisionIncrement2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(precisionDecrement2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addComponent(jLabel4))
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel9))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addGap(23, 23, 23)
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(precisionIncrement2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(precisionDecrement2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(setpoint, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel4)
                        .addGap(10, 10, 10)
                        .addComponent(toggleLAC)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        thermalPower1.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.BLACK_METAL);
        thermalPower1.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_BULGE);
        thermalPower1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.BLACKRED_LCD);
        thermalPower1.setLcdUnitString("MWt");
        thermalPower1.setLcdUnitStringVisible(true);
        thermalPower1.setSize(new java.awt.Dimension(170, 90));

        electric1.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.BEIGE);
        electric1.setBackgroundVisible(false);
        electric1.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.BLACK_METAL);
        electric1.setFrameVisible(false);
        electric1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.STANDARD_LCD);
        electric1.setLcdUnitString("MWe");
        electric1.setLcdUnitStringVisible(true);
        electric1.setMaxValue(1000.0);
        electric1.setSize(new java.awt.Dimension(170, 90));

        mTK1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mTK1.setMaximumSize(new java.awt.Dimension(427, 325));
        mTK1.setMinimumSize(new java.awt.Dimension(427, 325));
        mTK1.setPreferredSize(new java.awt.Dimension(427, 325));

        jPanel9.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(222, 222, 222), 1, true));
        jPanel9.setMaximumSize(new java.awt.Dimension(9999, 9999));
        jPanel9.setMinimumSize(new java.awt.Dimension(30, 30));
        jPanel9.setName(""); // NOI18N
        jPanel9.setPreferredSize(new java.awt.Dimension(426, 128));

        drumTemp.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        drumTemp.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        drumTemp.setLcdUnitString("°C");
        drumTemp.setLcdValue(20.0);
        drumTemp.setMaximumSize(new java.awt.Dimension(94, 32));
        drumTemp.setPreferredSize(new java.awt.Dimension(94, 32));

        drumTemp1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        drumTemp1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        drumTemp1.setLcdUnitString("°C");
        drumTemp1.setLcdValue(20.0);
        drumTemp1.setMaximumSize(new java.awt.Dimension(94, 32));
        drumTemp1.setPreferredSize(new java.awt.Dimension(94, 32));

        jLabel10.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel10.setText("Parameters Loop 1");

        jLabel11.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel11.setText("Parameters Loop 2");

        jLabel49.setText("Drum Press");

        jLabel50.setText("Drum Temp");

        suctionTemp.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        suctionTemp.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        suctionTemp.setLcdUnitString("°C");
        suctionTemp.setLcdValue(20.0);
        suctionTemp.setMaximumSize(new java.awt.Dimension(94, 32));
        suctionTemp.setPreferredSize(new java.awt.Dimension(94, 32));

        jLabel51.setText("Suction Temp");

        suctionTemp1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        suctionTemp1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        suctionTemp1.setLcdUnitString("°C");
        suctionTemp1.setLcdValue(20.0);
        suctionTemp1.setMaximumSize(new java.awt.Dimension(94, 32));
        suctionTemp1.setPreferredSize(new java.awt.Dimension(94, 32));

        jLabel52.setText("Suction Temp");

        press1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        press1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        press1.setLcdDecimals(2);
        press1.setLcdMaxValue(10.0);
        press1.setLcdUnitString("MPa");
        press1.setLcdValue(0.10142);
        press1.setMaximumSize(new java.awt.Dimension(94, 32));
        press1.setPreferredSize(new java.awt.Dimension(94, 32));

        press2.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        press2.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        press2.setLcdDecimals(2);
        press2.setLcdMaxValue(10.0);
        press2.setLcdUnitString("MPa");
        press2.setLcdValue(0.10142);
        press2.setMaximumSize(new java.awt.Dimension(94, 32));
        press2.setPreferredSize(new java.awt.Dimension(94, 32));

        jLabel53.setText("Drum Press");

        jLabel54.setText("Drum Temp");

        headerTemp1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        headerTemp1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        headerTemp1.setLcdUnitString("°C");
        headerTemp1.setLcdValue(20.0);
        headerTemp1.setMaximumSize(new java.awt.Dimension(94, 32));
        headerTemp1.setPreferredSize(new java.awt.Dimension(94, 32));

        jLabel55.setText("Header Temp");

        headerTemp.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        headerTemp.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        headerTemp.setLcdUnitString("°C");
        headerTemp.setLcdValue(20.0);
        headerTemp.setMaximumSize(new java.awt.Dimension(94, 32));
        headerTemp.setPreferredSize(new java.awt.Dimension(94, 32));

        jLabel56.setText("Header Temp");

        headerFlow1.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        headerFlow1.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        headerFlow1.setLcdUnitString("kg/s");
        headerFlow1.setLcdValue(20.0);
        headerFlow1.setMaximumSize(new java.awt.Dimension(94, 32));
        headerFlow1.setPreferredSize(new java.awt.Dimension(94, 32));

        jLabel57.setText("Header Flow");

        headerFlow.setCustomLcdForeground(new java.awt.Color(204, 204, 255));
        headerFlow.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.LIGHTBLUE_LCD);
        headerFlow.setLcdUnitString("kg/s");
        headerFlow.setLcdValue(20.0);
        headerFlow.setMaximumSize(new java.awt.Dimension(94, 32));
        headerFlow.setPreferredSize(new java.awt.Dimension(94, 32));

        jLabel58.setText("Header Flow");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel51)
                            .addComponent(jLabel49)
                            .addComponent(jLabel50)
                            .addComponent(jLabel56)
                            .addComponent(jLabel58))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(suctionTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(drumTemp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(press1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(headerTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(headerFlow, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(30, 30, 30)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel52)
                            .addComponent(jLabel53, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel54, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel55, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel57, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(drumTemp1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(suctionTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(press2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(headerTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(headerFlow1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(press1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(drumTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(suctionTemp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(headerTemp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(headerFlow, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(press2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(drumTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(suctionTemp1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(headerTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(headerFlow1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel57, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(annunciatorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(53, 53, 53)
                                .addComponent(thermalPower1, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(73, 73, 73)
                                .addComponent(electric1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(electric2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(manualRodControl1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mTK1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(thermalPower1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(electric1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(electric2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(mTK1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(annunciatorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(manualRodControl1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(45, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel3);

        jMenu1.setText("File");

        jMenuItem2.setText("Save IC");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem11.setText("Exit to Menu");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem11);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Window");

        jMenuItem3.setText("Core Map");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuItem10.setText("Selsyns");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem10);

        jMenuItem12.setText("MCP");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem12);

        jMenuItem5.setText("Turbine-Generators");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuItem6.setText("Condensate");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem6);

        jMenuItem7.setText("Dearators");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem7);

        jMenuItem8.setText("Feedwater");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem8);

        jMenuItem9.setText(org.openide.util.NbBundle.getMessage(UI.class, "TGUI.jMenuItem4.text")); // NOI18N
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem9);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Settings");

        pause.setText("Paused");
        pause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseActionPerformed(evt);
            }
        });
        jMenu3.add(pause);

        jMenuItem4.setText("Clear Event Logs");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem4);

        jMenu7.setText("UI Update rate");

        buttonGroup7.add(jRadioButtonMenuItem1);
        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("50ms");
        jRadioButtonMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem1ActionPerformed(evt);
            }
        });
        jMenu7.add(jRadioButtonMenuItem1);

        buttonGroup7.add(jRadioButtonMenuItem2);
        jRadioButtonMenuItem2.setText("100ms");
        jRadioButtonMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem2ActionPerformed(evt);
            }
        });
        jMenu7.add(jRadioButtonMenuItem2);

        buttonGroup7.add(jRadioButtonMenuItem3);
        jRadioButtonMenuItem3.setText("150ms");
        jRadioButtonMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem3ActionPerformed(evt);
            }
        });
        jMenu7.add(jRadioButtonMenuItem3);

        buttonGroup7.add(jRadioButtonMenuItem4);
        jRadioButtonMenuItem4.setText("200ms");
        jRadioButtonMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem4ActionPerformed(evt);
            }
        });
        jMenu7.add(jRadioButtonMenuItem4);

        buttonGroup7.add(jRadioButtonMenuItem5);
        jRadioButtonMenuItem5.setText("500ms");
        jRadioButtonMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem5ActionPerformed(evt);
            }
        });
        jMenu7.add(jRadioButtonMenuItem5);

        buttonGroup7.add(jRadioButtonMenuItem6);
        jRadioButtonMenuItem6.setText("1000ms");
        jRadioButtonMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem6ActionPerformed(evt);
            }
        });
        jMenu7.add(jRadioButtonMenuItem6);

        jMenu3.add(jMenu7);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("MTK");

        jMenu5.setText("Sign");

        buttonGroup3.add(mtkPlus);
        mtkPlus.setSelected(true);
        mtkPlus.setText("+");
        mtkPlus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mtkPlusActionPerformed(evt);
            }
        });
        jMenu5.add(mtkPlus);

        buttonGroup3.add(mtkMin);
        mtkMin.setText("-");
        mtkMin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mtkMinActionPerformed(evt);
            }
        });
        jMenu5.add(mtkMin);

        jMenu4.add(jMenu5);

        jMenu6.setText("Threshold");

        buttonGroup4.add(zero5);
        zero5.setText("0.5%");
        zero5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zero5ActionPerformed(evt);
            }
        });
        jMenu6.add(zero5);

        buttonGroup4.add(one);
        one.setText("1%");
        one.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneActionPerformed(evt);
            }
        });
        jMenu6.add(one);

        buttonGroup4.add(two5);
        two5.setText("2.5%");
        two5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                two5ActionPerformed(evt);
            }
        });
        jMenu6.add(two5);

        buttonGroup4.add(five);
        five.setText("5%");
        five.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fiveActionPerformed(evt);
            }
        });
        jMenu6.add(five);

        buttonGroup4.add(ten);
        ten.setText("10%");
        ten.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tenActionPerformed(evt);
            }
        });
        jMenu6.add(ten);

        buttonGroup4.add(fifteen);
        fifteen.setSelected(true);
        fifteen.setText("15%");
        fifteen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fifteenActionPerformed(evt);
            }
        });
        jMenu6.add(fifteen);

        buttonGroup4.add(twenty);
        twenty.setText("20%");
        twenty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                twentyActionPerformed(evt);
            }
        });
        jMenu6.add(twenty);

        buttonGroup4.add(twentyfive);
        twentyfive.setText("25%");
        twentyfive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                twentyfiveActionPerformed(evt);
            }
        });
        jMenu6.add(twentyfive);

        jMenu4.add(jMenu6);

        jMenuBar1.add(jMenu4);

        jMenu8.setText("Data");

        jMenuItem1.setText("Log");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem1);

        jMenuBar1.add(jMenu8);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1409, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 797, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        createOrContinue(SaveDialog.class, false, false);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        createOrContinue(TGUI.class, true, false);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        createOrContinue(CondensateUI.class, true, false);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        createOrContinue(DearatorUI.class, true, false);
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        createOrContinue(FeedwaterUI.class, true, false);
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        UI.createOrContinue(PCSUI.class, true, false);
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        annunciator.acknowledge();
        for (UIUpdateable window: elementsToUpdate) {
            window.acknowledge();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void powerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_powerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_powerActionPerformed

    private void mcpCavitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mcpCavitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mcpCavitActionPerformed

    private void msv2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_msv2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_msv2ActionPerformed

    private void highDrumPressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_highDrumPressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_highDrumPressActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        createOrContinue(SelsynPanel.class, false, false);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void rodsOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rodsOutActionPerformed

    }//GEN-LAST:event_rodsOutActionPerformed

    private void rodsInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rodsInActionPerformed

    }//GEN-LAST:event_rodsInActionPerformed

    private void rodsOut1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rodsOut1ActionPerformed
        manualRodControl1.resetSelection();
    }//GEN-LAST:event_rodsOut1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        autoControl.az1Control.trip("AZ-1K Pressed");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        autoControl.az1Control.reset();
        autoControl.fasrControl.reset();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        Loader.loader.setLoading(false);
        NPPSim.endSimulation();
        elementsToUpdate.forEach(elem -> {
            elem.discard();
        });
        elementsToUpdate.clear();
        Loader.loader.setVisible(true);
    }//GEN-LAST:event_formWindowClosed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void pauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseActionPerformed
        if (pause.isSelected()) {
            NPPSim.setPaused(true);
        } else {
            NPPSim.setPaused(false);
        }
    }//GEN-LAST:event_pauseActionPerformed

    private void precisionDecrement2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionDecrement2ActionPerformed
        if (debounce) {
            return;
        }
        debounce = true;
        if (autoControl.automaticRodController.getSetpoint() == 0) {
            return;
        }
        setpoint.setLcdValue(setpoint.getLcdValue() - 5);
        autoControl.automaticRodController.setSetpoint(setpoint.getLcdValue());
    }//GEN-LAST:event_precisionDecrement2ActionPerformed

    private void precisionIncrement2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precisionIncrement2ActionPerformed
        if (debounce) {
            return;
        }
        debounce = true;
        if (autoControl.automaticRodController.getSetpoint() == 5200) {
            return;
        }
        setpoint.setLcdValue(setpoint.getLcdValue() + 5);
        autoControl.automaticRodController.setSetpoint(setpoint.getLcdValue());
    }//GEN-LAST:event_precisionIncrement2ActionPerformed

    private void toggleAR1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleAR1ActionPerformed
        if (toggleAR1.getModel().isSelected()) {
            autoControl.automaticRodController.enable1AR();
        } else {
            autoControl.automaticRodController.disable1AR();
        }
        limit1AR.setBackground(Annunciator.YELLOWOFF_COLOR);
        error1AR.setBackground(Annunciator.YELLOWOFF_COLOR);
        busy1AR.setBackground(Annunciator.GREENOFF_COLOR);
        
    }//GEN-LAST:event_toggleAR1ActionPerformed

    private void toggleAR2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleAR2ActionPerformed
        if (toggleAR2.getModel().isSelected()) {
            autoControl.automaticRodController.enable2AR();
        } else {
            autoControl.automaticRodController.disable2AR();
        }
        limit2AR.setBackground(Annunciator.YELLOWOFF_COLOR);
        error2AR.setBackground(Annunciator.YELLOWOFF_COLOR);
        busy2AR.setBackground(Annunciator.GREENOFF_COLOR);
        
    }//GEN-LAST:event_toggleAR2ActionPerformed

    private void toggleLACActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleLACActionPerformed
        if (toggleLAC.getModel().isSelected()) {
            autoControl.automaticRodController.enableLAR();
        } else {
            autoControl.automaticRodController.disableLar();
        }
        limitLAC.setBackground(Annunciator.YELLOWOFF_COLOR);
        errorLAC.setBackground(Annunciator.YELLOWOFF_COLOR);
        busyLAC.setBackground(Annunciator.GREENOFF_COLOR);
    }//GEN-LAST:event_toggleLACActionPerformed

    private void mtkPlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mtkPlusActionPerformed
        NPPSim.ui.mTK1.setSignPositive(true);
    }//GEN-LAST:event_mtkPlusActionPerformed

    private void mtkMinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mtkMinActionPerformed
        NPPSim.ui.mTK1.setSignPositive(false);
    }//GEN-LAST:event_mtkMinActionPerformed

    private void zero5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zero5ActionPerformed
        NPPSim.ui.mTK1.setThresHold(0.005f);
    }//GEN-LAST:event_zero5ActionPerformed

    private void oneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oneActionPerformed
        NPPSim.ui.mTK1.setThresHold(0.01f);
    }//GEN-LAST:event_oneActionPerformed

    private void two5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_two5ActionPerformed
        NPPSim.ui.mTK1.setThresHold(0.025f);
    }//GEN-LAST:event_two5ActionPerformed

    private void fiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fiveActionPerformed
        NPPSim.ui.mTK1.setThresHold(0.05f);
    }//GEN-LAST:event_fiveActionPerformed

    private void tenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tenActionPerformed
        NPPSim.ui.mTK1.setThresHold(0.1f);
    }//GEN-LAST:event_tenActionPerformed

    private void fifteenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fifteenActionPerformed
        NPPSim.ui.mTK1.setThresHold(0.15f);
    }//GEN-LAST:event_fifteenActionPerformed

    private void twentyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_twentyActionPerformed
        NPPSim.ui.mTK1.setThresHold(0.2f);
    }//GEN-LAST:event_twentyActionPerformed

    private void twentyfiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_twentyfiveActionPerformed
        NPPSim.ui.mTK1.setThresHold(0.25f);
    }//GEN-LAST:event_twentyfiveActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        createOrContinue(LogWindow.class, false, false);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        autoControl.eventLog.clear();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jRadioButtonMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem2ActionPerformed
        uiUpdateRate = 100;
    }//GEN-LAST:event_jRadioButtonMenuItem2ActionPerformed

    private void jRadioButtonMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem1ActionPerformed
        uiUpdateRate = 50;
    }//GEN-LAST:event_jRadioButtonMenuItem1ActionPerformed

    private void jRadioButtonMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem3ActionPerformed
        uiUpdateRate = 150;
    }//GEN-LAST:event_jRadioButtonMenuItem3ActionPerformed

    private void jRadioButtonMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem4ActionPerformed
        uiUpdateRate = 200;
    }//GEN-LAST:event_jRadioButtonMenuItem4ActionPerformed

    private void jRadioButtonMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem5ActionPerformed
        uiUpdateRate = 500;
    }//GEN-LAST:event_jRadioButtonMenuItem5ActionPerformed

    private void jRadioButtonMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem6ActionPerformed
        uiUpdateRate = 1000;
    }//GEN-LAST:event_jRadioButtonMenuItem6ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
         autoControl.fasrControl.trip("BSM Button Pressed");
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        createOrContinue(MCPUI.class, true, false);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {
        createOrContinue(CoreMap.class, false, false);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel annunciatorPanel;
    private javax.swing.JTextField arINOP;
    private javax.swing.JTextField busy1AR;
    private javax.swing.JTextField busy2AR;
    private javax.swing.JTextField busyLAC;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.ButtonGroup buttonGroup6;
    private javax.swing.ButtonGroup buttonGroup7;
    private eu.hansolo.steelseries.gauges.Linear deltaKeff;
    private eu.hansolo.steelseries.gauges.DisplaySingle drumTemp;
    private eu.hansolo.steelseries.gauges.DisplaySingle drumTemp1;
    private eu.hansolo.steelseries.gauges.DisplayRectangular electric1;
    private eu.hansolo.steelseries.gauges.DisplayRectangular electric2;
    private javax.swing.JTextField error1AR;
    private javax.swing.JTextField error2AR;
    private javax.swing.JTextField errorLAC;
    private javax.swing.JRadioButtonMenuItem fifteen;
    private javax.swing.JRadioButtonMenuItem five;
    private eu.hansolo.steelseries.gauges.DisplaySingle headerFlow;
    private eu.hansolo.steelseries.gauges.DisplaySingle headerFlow1;
    private eu.hansolo.steelseries.gauges.DisplaySingle headerTemp;
    private eu.hansolo.steelseries.gauges.DisplaySingle headerTemp1;
    private javax.swing.JTextField highDrumPress;
    private javax.swing.JTextField highWaterLevel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem3;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem4;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem5;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem6;
    private javax.swing.JScrollPane jScrollPane1;
    private eu.hansolo.steelseries.gauges.DisplaySingle keff;
    private javax.swing.JTextField lacINOP;
    private javax.swing.JTextField limit1AR;
    private javax.swing.JTextField limit2AR;
    private javax.swing.JTextField limitLAC;
    private javax.swing.JTextField lowDrumPress;
    private javax.swing.JTextField lowWaterLevel;
    private com.darwish.nppsim.MTK mTK1;
    private com.darwish.nppsim.ManualRodControl manualRodControl1;
    private javax.swing.JTextField mcpCavit;
    private javax.swing.JTextField msv1;
    private javax.swing.JTextField msv2;
    private javax.swing.JTextField msv3;
    private javax.swing.JRadioButtonMenuItem mtkMin;
    private javax.swing.JRadioButtonMenuItem mtkPlus;
    private javax.swing.JTextField nRate;
    private eu.hansolo.steelseries.gauges.DisplaySingle neutronFlux;
    private javax.swing.JRadioButtonMenuItem one;
    private javax.swing.JCheckBoxMenuItem pause;
    private eu.hansolo.steelseries.gauges.DisplaySingle period;
    private javax.swing.JTextField power;
    private javax.swing.JButton precisionDecrement2;
    private javax.swing.JButton precisionIncrement2;
    private eu.hansolo.steelseries.gauges.DisplaySingle press1;
    private eu.hansolo.steelseries.gauges.DisplaySingle press2;
    private javax.swing.JTextField rodLimit;
    private javax.swing.JTextField rodLimit1;
    private javax.swing.JTextField rodLimit10;
    private javax.swing.JTextField rodLimit2;
    private javax.swing.JTextField rodLimit3;
    private javax.swing.JTextField rodLimit4;
    private javax.swing.JTextField rodLimit5;
    private javax.swing.JTextField rodSeq;
    private javax.swing.JTextField rodTravel;
    private javax.swing.JButton rodsIn;
    private javax.swing.JButton rodsOut;
    private javax.swing.JButton rodsOut1;
    private javax.swing.JTextField sdv_aOpen;
    private eu.hansolo.steelseries.gauges.DisplaySingle setpoint;
    private eu.hansolo.steelseries.gauges.DisplaySingle suctionTemp;
    private eu.hansolo.steelseries.gauges.DisplaySingle suctionTemp1;
    private javax.swing.JTextField t30;
    private javax.swing.JRadioButtonMenuItem ten;
    private eu.hansolo.steelseries.gauges.DisplayRectangular thermalPower1;
    private javax.swing.JToggleButton toggleAR1;
    private javax.swing.JToggleButton toggleAR2;
    private javax.swing.JToggleButton toggleLAC;
    private javax.swing.JRadioButtonMenuItem twenty;
    private javax.swing.JRadioButtonMenuItem twentyfive;
    private javax.swing.JRadioButtonMenuItem two5;
    private javax.swing.JRadioButtonMenuItem zero5;
    // End of variables declaration//GEN-END:variables
}
