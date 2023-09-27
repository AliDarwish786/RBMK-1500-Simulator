package com.darwish.nppsim;

import static com.darwish.nppsim.NPPSim.core;
import static com.darwish.nppsim.NPPSim.mcc;
import static com.darwish.nppsim.NPPSim.sdv_c;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.FontUIResource;
import java.awt.event.*;

public class oldUI implements UIUpdateable {
    private JFrame frame;
    private JTextArea drum1;
    private JTextArea drum2;
    private JTextArea loop1;
    private JTextArea loop2;
    private JLabel sdv_cStatus;
    private JLabel mcpFlow;
    private JLabel mcpRPM;
    private JSpinner mcpSelector;
    public oldUI() {
        initComponents();
    }
    
    private void initComponents() {
        frame = new JFrame("ReactorData");
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(800,400);
        frame.setLayout(null);

        drum1 = new JTextArea();
        drum1.setEditable(false);
        drum1.setSize(220, 200);
        frame.getContentPane().add(drum1);

        drum2 = new JTextArea();
        drum2.setEditable(false);
        drum2.setSize(220, 200);
        drum2.setLocation(220, 0);
        frame.getContentPane().add(drum2);

        loop1 = new JTextArea();
        loop1.setEditable(false);
        loop1.setSize(220, 200);
        loop1.setLocation(0, 200);
        frame.getContentPane().add(loop1);

        loop2 = new JTextArea();
        loop2.setEditable(false);
        loop2.setSize(220, 200);
        loop2.setLocation(220, 200);
        frame.getContentPane().add(loop2);

        JLabel sdv_cLabel = new JLabel("Bypass SDV-C");
        sdv_cStatus = new JLabel("Position: ");
        sdv_cStatus.setLocation(500, 15);
        sdv_cStatus.setFont(new FontUIResource(null, 0, 12));
        sdv_cStatus.setSize(100, 15);
        sdv_cLabel.setLocation(500, 0);
        sdv_cLabel.setSize(130, 15);
        ButtonGroup sdv_cPos = new ButtonGroup();
        JRadioButton sdv_cPosOpen = new JRadioButton();
        JRadioButton sdv_cPosN = new JRadioButton();
        JRadioButton sdv_cPosClose = new JRadioButton();
        sdv_cPosOpen.setText("open");
        sdv_cPosN.setText("stop");
        sdv_cPosClose.setText("close");
        sdv_cPosOpen.setLocation(500, 30);
        sdv_cPosN.setLocation(500, 60);
        sdv_cPosClose.setLocation(500, 90);
        sdv_cPos.add(sdv_cPosClose);
        sdv_cPos.add(sdv_cPosOpen);
        sdv_cPos.add(sdv_cPosN);
        sdv_cPosOpen.setSize(100, 30);
        sdv_cPosClose.setSize(100, 30);
        sdv_cPosN.setSize(100, 30);
        sdv_cPosN.setSelected(true);
        frame.getContentPane().add(sdv_cPosOpen);
        frame.getContentPane().add(sdv_cPosN);
        frame.getContentPane().add(sdv_cPosClose);
        frame.getContentPane().add(sdv_cLabel);
        frame.getContentPane().add(sdv_cStatus);
        sdv_cPosN.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {             
               if (e.getStateChange() == 1) {
                for (SteamValve i: sdv_c) {
                    i.setState(1);
                }
               }
            }           
        });
        sdv_cPosOpen.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {             
               if (e.getStateChange() == 1) {
                    for (SteamValve i: sdv_c) {
                        i.setState(2);
                    }
               }
            }           
        });
        sdv_cPosClose.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {             
               if (e.getStateChange() == 1) {
                    for (SteamValve i: sdv_c) {
                        i.setState(0);
                    }
               }
            }           
        });

        JPanel mcpPanel = new JPanel();
        mcpPanel.setLayout(null);
        mcpPanel.setLocation(500, 130);
        mcpPanel.setSize(200, 200);
        JLabel mcpLabel = new JLabel("Main Circulation Pumps");
        mcpLabel.setSize(200, 15);
        JLabel mcpSelectLabel = new JLabel("Pump Selection:");
        mcpSelectLabel.setFont(new FontUIResource(null, 0, 12));
        mcpSelectLabel.setLocation(0, 25);
        mcpSelectLabel.setSize(120, 15);
        mcpRPM = new JLabel("RPM: ");
        mcpRPM.setLocation(0, 50);
        mcpRPM.setFont(new FontUIResource(null, 0, 12));
        mcpRPM.setSize(100, 15);
        mcpFlow = new JLabel("Flow: ");
        mcpFlow.setLocation(100, 50);
        mcpFlow.setFont(new FontUIResource(null, 0, 12));
        mcpFlow.setSize(100, 15);
        ButtonGroup mcpStatus = new ButtonGroup();
        JRadioButton mcpStart = new JRadioButton();
        JRadioButton mcpStop = new JRadioButton();
        mcpStart.setText("start");
        mcpStop.setText("stop");
        mcpStart.setLocation(0, 65);
        mcpStop.setLocation(0, 95);
        mcpStatus.add(mcpStop);
        mcpStatus.add(mcpStart);
        mcpStart.setSize(100, 30);
        mcpStop.setSize(100, 30);
        mcpStop.setSelected(true);
        mcpSelector = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
        mcpSelector.setLocation(110, 20);
        mcpSelector.setSize(35, 25);
        ((JSpinner.DefaultEditor)mcpSelector.getEditor()).getTextField().setEditable(false);
        mcpSelector.addChangeListener((ChangeEvent e) -> {
            Pump currentSelection = mcc.mcp.get((int)mcpSelector.getValue() - 1);
            mcpFlow.setText("Flow: " + NPPMath.round(currentSelection.getFlow()));
            mcpRPM.setText("RPM: " + NPPMath.round(currentSelection.getRPM()));
            if (currentSelection.isActive()) {
                mcpStart.setSelected(true);
            } else {
                mcpStop.setSelected(true);
            }
        });
        mcpPanel.add(mcpLabel);
        mcpPanel.add(mcpRPM);
        mcpPanel.add(mcpStart);
        mcpPanel.add(mcpStop);
        mcpPanel.add(mcpSelectLabel);
        mcpPanel.add(mcpSelector);
        mcpPanel.add(mcpFlow);
        frame.getContentPane().add(mcpPanel);

        //frame.getContentPane().add(mcpFlow);
        mcpStart.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {             
                if (e.getStateChange() == 1) {
                    mcc.mcp.get((int)mcpSelector.getValue() - 1).setActive(true);
                }
            }           
        });
        mcpStop.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {             
                if (e.getStateChange() == 1) {
                    mcc.mcp.get((int)mcpSelector.getValue() - 1).setActive(false);
                }
            }           
        });
        frame.setVisible(true);
    }

    @Override
    public void update() {

        MCC.SeparatorDrum drum1 = mcc.drum1;
        MCC.SeparatorDrum drum2 = mcc.drum2;
        FuelChannel loop1 = (FuelChannel)core.coreArray.get(25).get(25);
        FuelChannel loop2 = (FuelChannel)core.coreArray.get(28).get(31);
        MCC.MCPPressureHeader pHeader1 = mcc.pHeader1;
        MCC.MCPPressureHeader pHeader2 = mcc.pHeader2;
        
        //System.out.println("specific heat cap " + specificHeat + " specVapEnthalpy " + specificVaporEnthalpy+ " bp " + boilingPoint + " steamMass " + steamMass + " water mass " + feedwaterMass + " volume " + feedwaterVolume + " specific density: " + specificDensity + " power " + thermalPower);
        this.drum1.setText("Water Temp: " + NPPMath.round(drum1.getWaterTemperature()) + "\nBoiling Point:" + NPPMath.round(drum1.getBoilingPoint()) + "\nDrum Pressure: " + NPPMath.round(drum1.getPressure(), 3) + "\nSteam Production: " + NPPMath.round(drum1.getSteamProduction()) + "\nSteam Flow: " + NPPMath.round(drum1.getSteamOutflowRate()) + "\nSteam Mass: " + NPPMath.round(drum1.getSteamMass()) + "\nWater Mass " + NPPMath.round(drum1.getWaterMass()) + "\nDrum Water Level: " + NPPMath.round(drum1.getWaterLevel()));
        //drum1.resetFlowRates();
        this.drum2.setText("Water Temp: " + NPPMath.round(drum2.getWaterTemperature()) + "\nBoiling Point:" + NPPMath.round(drum2.getBoilingPoint()) + "\nDrum Pressure: " + NPPMath.round(drum2.getPressure(), 3) + "\nSteam Production: " + NPPMath.round(drum2.getSteamProduction()) + "\nSteam Flow: " + NPPMath.round(drum2.getSteamOutflowRate()) + "\nSteam Mass: " + NPPMath.round(drum2.getSteamMass()) + "\nWater Mass " + NPPMath.round(drum2.getWaterMass()) + "\nDrum Water Level: " + NPPMath.round(drum2.getWaterLevel()));
        //drum2.resetFlowRates();
        this.loop1.setText("Water Temp: " + NPPMath.round(loop1.getWaterTemperature()) + "\nBoiling Point:" + NPPMath.round(loop1.getBoilingPoint()) + "\nPressure: " + NPPMath.round(loop1.getPressure(), 3) + "\nSteam Mass: " + NPPMath.round(loop1.getSteamMass()) + "\nFeedwater Mass " + NPPMath.round(loop1.getWaterMass()) + "\nWater Level: " + NPPMath.round(loop1.getWaterLevel()) + "\n Water Inflow: " + NPPMath.round(loop1.getWaterInflowRate()) + "\nVoiding:" + NPPMath.round(loop1.getVoidFraction()) + "\nPressure Header Temp: " + NPPMath.round(pHeader1.getWaterTemperature()) + "\nPressure Header Flow: " + NPPMath.round(pHeader1.getWaterOutflowRate()));
        //loop1.resetFlowRates();
        //pHeader1.resetFlowRates();
        this.loop2.setText("Water Temp: " + NPPMath.round(loop2.getWaterTemperature()) + "\nBoiling Point:" + NPPMath.round(loop2.getBoilingPoint()) + "\nPressure: " + NPPMath.round(loop2.getPressure(), 3) + "\nSteam Mass: " + NPPMath.round(loop2.getSteamMass()) + "\nFeedwater Mass " + NPPMath.round(loop2.getWaterMass()) + "\nWater Level: " + NPPMath.round(loop2.getWaterLevel()) + "\n Water Inflow: " + NPPMath.round(loop2.getWaterInflowRate()) + "\nVoiding:" + NPPMath.round(loop2.getVoidFraction()) + "\nPressure Header Temp: " + NPPMath.round(pHeader2.getWaterTemperature()) + "\nPressure Header Flow: " + NPPMath.round(pHeader2.getWaterOutflowRate()));
        //loop2.resetFlowRates();
        //pHeader2.resetFlowRates();
        sdv_cStatus.setText("Position: " + NPPMath.round(sdv_c.get(0).getPosition() * 100));
        mcpFlow.setText("Flow: " + NPPMath.round(mcc.mcp.get((int)mcpSelector.getValue() - 1).getFlow()));
        mcpRPM.setText("RPM: " + NPPMath.round(mcc.mcp.get((int)mcpSelector.getValue() - 1).getRPM()));
    }
    
    @Override
    public void initializeDialUpdateThread() {
        //void
    }

    @Override
    public void setVisibility(boolean visible) {
        frame.setVisible(visible);
    }
    
    @Override
    public void discard() {
        frame.setVisible(false);
    }
    
    @Override
    public void acknowledge() {

    }
}
