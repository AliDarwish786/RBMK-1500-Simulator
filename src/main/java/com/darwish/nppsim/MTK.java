package com.darwish.nppsim;

import java.awt.Color;
import java.util.HashMap;
import javax.swing.JTextField;

public class MTK extends javax.swing.JPanel {
    static Color CPSONCOLOR = Color.GREEN.brighter();
    static Color CPSOFFCOLOR = Color.GREEN.darker().darker();
    static Color CHANNELOFFCOLOR; 
    private short rowNumber = 0;
    private int[] fuelCPSCount = {0, 0};
    private HashMap<Channel, JTextField> channelMtkBinding;
    private boolean positive = true;
    private float threshold = 0.15f;
    
    public MTK() {
        initComponents();
        this.setVisible(true);
        if (NPPSim.core == null) { //return when the IDE tries to create this object as the mcc will not be initialized 
            return;
        }
        CHANNELOFFCOLOR = new FuelChannel().uiData.UIBackgroundColor;
        channelMtkBinding = new HashMap<>();
        while (rowNumber != 50) {
            createRow();
        }
        channelMtkBinding.forEach((channel, light) -> {
            if (channel instanceof FuelChannel) {
                fuelCPSCount[0]++;
            } else if (channel instanceof CPSChannel) {
                fuelCPSCount[1]++;
            }
        });
        
    }

    public void update() {
        try {
            while(NPPSim.core1) {
                Thread.sleep(5);
                System.out.println("mtk is asleep for 5 ms");
            }
        } catch (InterruptedException e) {
            
        }
        Double[] medianFuelAndCPSFlux = new Double[] {0.0, 0.0};
        channelMtkBinding.forEach((channel, light) -> {
            if (channel instanceof FuelChannel) {
                medianFuelAndCPSFlux[0] += channel.getNeutronPopulation()[0];
            } else if (channel instanceof CPSChannel) {
                medianFuelAndCPSFlux[1] += channel.getNeutronPopulation()[0];
            }
        });
        medianFuelAndCPSFlux[0] /= fuelCPSCount[0];
        medianFuelAndCPSFlux[1] /= fuelCPSCount[1];
        channelMtkBinding.forEach((channel, light) -> {
            if (positive) {
                if (channel instanceof FuelChannel) {
                   if (channel.getNeutronPopulation()[0] > medianFuelAndCPSFlux[0] * (1 + threshold)) {
                       light.setBackground(((FuelChannel) channel).uiData.UISelectedColor.brighter().brighter());
                   } else {
                       light.setBackground(CHANNELOFFCOLOR);
                   }
                } else if (channel instanceof CPSChannel) {
                    if (channel.getNeutronPopulation()[0] > medianFuelAndCPSFlux[1] * (1 + threshold)) {
                       light.setBackground(CPSONCOLOR);
                   } else {
                       light.setBackground(CPSOFFCOLOR);
                   }
                }
            } else {
                if (channel instanceof FuelChannel) {
                   if (channel.getNeutronPopulation()[0] < medianFuelAndCPSFlux[0] * (1 - threshold)) {
                       light.setBackground(((FuelChannel) channel).uiData.UISelectedColor.brighter().brighter());
                   } else {
                       light.setBackground(CHANNELOFFCOLOR);
                   }
                } else if (channel instanceof CPSChannel) {
                    if (channel.getNeutronPopulation()[0] < medianFuelAndCPSFlux[1] * (1 - threshold)) {
                       light.setBackground(CPSONCOLOR);
                   } else {
                       light.setBackground(CPSOFFCOLOR);
                   }
                }
            }
        });
        
    }
    
    private javax.swing.JPanel createRow() {
        javax.swing.JPanel row = new javax.swing.JPanel();
        row.setBackground(UI.BACKGROUND.darker());
        row.setMaximumSize(new java.awt.Dimension(1000, 7));
        row.setMinimumSize(new java.awt.Dimension(8, 7));
        row.setPreferredSize(new java.awt.Dimension(8, 7));
        row.setRequestFocusEnabled(false);
        row.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        for (short i = 50; i > 0; i--) {
            Channel currentChannel = NPPSim.core.coreArray.get(50 - rowNumber + 2).get(i + 2);
            if (currentChannel instanceof FuelChannel || currentChannel instanceof CPSChannel) {
                javax.swing.JTextField button = new javax.swing.JTextField();
                channelMtkBinding.put(currentChannel, button);
                button.setBorder(javax.swing.BorderFactory.createLineBorder(CHANNELOFFCOLOR.darker(), 1));
                button.setMaximumSize(new java.awt.Dimension(8, 7));
                button.setMinimumSize(new java.awt.Dimension(8, 7));
                button.setPreferredSize(new java.awt.Dimension(8, 7));
                if (currentChannel instanceof FuelChannel) {
                    button.setBackground(currentChannel.uiData.UIBackgroundColor);
                } else {
                    button.setBackground(CPSOFFCOLOR);
                }
                button.setEditable(false);
                row.add(button);
            } else {
                javax.swing.JPanel button = new javax.swing.JPanel();
                button.setBackground(UI.BACKGROUND.darker());
                button.setMaximumSize(new java.awt.Dimension(8, 7));
                button.setMinimumSize(new java.awt.Dimension(8, 7));
                button.setPreferredSize(new java.awt.Dimension(8, 7));
                row.add(button);
            }
        }
        this.add(row);
        rowNumber++;
        return row;
    }
    
    public void setThresHold(float threshold) {
        this.threshold = threshold;
        update();
    }
    
    public void setSignPositive(boolean positive) {
        this.positive = positive;
        update();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(UI.BACKGROUND.darker());
        setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.darkGray, java.awt.Color.darkGray, java.awt.Color.darkGray, java.awt.Color.darkGray));
        setMaximumSize(new java.awt.Dimension(500, 500));
        setMinimumSize(new java.awt.Dimension(20, 20));
        setPreferredSize(new java.awt.Dimension(100, 100));
        setLayout(new java.awt.GridLayout(50, 50));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}