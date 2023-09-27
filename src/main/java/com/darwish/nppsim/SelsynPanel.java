package com.darwish.nppsim;

import static com.darwish.nppsim.NPPSim.autoControl;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

public class SelsynPanel extends javax.swing.JFrame implements UIUpdateable {
    private short colNumber = 0;
    private final Color dialLightColor = new Color(249, 249, 185);
    private final HashMap<ControlRodChannel, Selsyn> selsynBinding;
    private final ArrayList<ControlRodChannel> previousSelectedRods = new ArrayList<>();
  
    public SelsynPanel() {
        initComponents();
        this.setTitle("Rod Position Indicators");
        selsynBinding = new HashMap<>();
        while (colNumber != 50) {
            if (colNumber % 2 != 0 ) {
                createRow();
            } else {
                colNumber++;
            }
        }
        initializeDialUpdateThread();
    }

    @Override
    public void update() {
        if (this.isVisible()) {
            java.awt.EventQueue.invokeLater(() -> {
                selsynBinding.forEach((channel, selsyn) -> {
                selsyn.setValue(channel.getPosition() * 7);
                });
            });
        }
    }
    
    @Override
    public void initializeDialUpdateThread() {
        UI.uiThreads.add(
            new Thread(() -> {
                try {
                    while (true) {
                        if (this.isVisible()) {
                            java.awt.EventQueue.invokeLater(() -> {
                                if (!autoControl.automaticRodController.linkedChannels.isEmpty()) {
                                    for (ControlRodChannel i: autoControl.automaticRodController.linkedChannels) {
                                        selsynBinding.get(i).setValue(i.getPosition() * 7);
                                    }
                                }
                                if (NPPSim.autoControl.az1Control.isTripped()) {
                                    selsynBinding.forEach((channel, selsyn) -> {
                                        selsyn.setValue(channel.getPosition() * 7);
                                    });
                                }
                                UI.selectedControlRods.forEach(channel -> {
                                    selsynBinding.get(channel).setValue(channel.getPosition() * 7);
                                    selsynBinding.get(channel).setDialColor(dialLightColor);

                                });
                                previousSelectedRods.forEach(channel -> {
                                    if (!UI.selectedControlRods.contains(channel)) {
                                        selsynBinding.get(channel).setDialColor(Color.LIGHT_GRAY);
                                    }
                                });
                                previousSelectedRods.clear();
                                previousSelectedRods.addAll(UI.selectedControlRods);
                            });
                        }
                        Thread.sleep(UI.getUpdateRate());
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

    }
    
    private javax.swing.JPanel createRow() {
        boolean fuelchannelIteratorActive = false;
        int fuelchannelIterator = 0;
        javax.swing.JPanel row = new javax.swing.JPanel();
        row.setBackground(UI.BACKGROUND);
        row.setMaximumSize(new java.awt.Dimension(50, 1300));
        row.setMinimumSize(new java.awt.Dimension(50, 1300));
        row.setPreferredSize(new java.awt.Dimension(50, 1300));
        row.setRequestFocusEnabled(false);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.add(createEmptySpace(40));
        for (short i = 47; i > 0; i--) {
            Channel currentChannel = NPPSim.core.coreArray.get(i + 3).get(50 - colNumber + 3);
            if (currentChannel instanceof ControlRodChannel) {
                Selsyn selsyn = new Selsyn();
                selsynBinding.put((ControlRodChannel)currentChannel, selsyn);
                if (currentChannel instanceof SARChannel) {
                    selsyn.invert();
                }
                selsyn.setFrameColor(((ControlRodChannel) currentChannel).uiData.UISelsynColor);
                selsyn.setBackgroundColor(null);
                selsyn.setDialColor(Color.LIGHT_GRAY);
                selsyn.setPreferredSize(new java.awt.Dimension(50, 50));
                selsyn.setMaximumSize(new java.awt.Dimension(50, 50));
                selsyn.setMinimumSize(new java.awt.Dimension(50, 50));
                row.add(selsyn);
                row.add(createEmptySpace(15, currentChannel.uiData.positionString));
                fuelchannelIteratorActive = true;
            } else if (fuelchannelIteratorActive) {
                fuelchannelIterator++;
                if (fuelchannelIterator == 3) {
                    fuelchannelIterator = 0;
                    fuelchannelIteratorActive = false;
                }
            } else {
                row.add(createEmptySpace(16));
            }
        }
        jPanel1.add(row);
        colNumber++;
        return row;
    }
    
    private javax.swing.JPanel createEmptySpace(int height) {
        javax.swing.JPanel button = new javax.swing.JPanel();
        button.setBackground(UI.BACKGROUND);
        button.setMaximumSize(new java.awt.Dimension(50, height));
        button.setMinimumSize(new java.awt.Dimension(50, height));
        button.setPreferredSize(new java.awt.Dimension(50, height));
        button.setVisible(true);
        return button;
    }

    private javax.swing.JPanel createEmptySpace(int height, String text) {
        javax.swing.JPanel button = new javax.swing.JPanel();
        button.setBackground(UI.BACKGROUND);
        button.setMaximumSize(new java.awt.Dimension(50, height));
        button.setMinimumSize(new java.awt.Dimension(50, height));
        button.setPreferredSize(new java.awt.Dimension(50, height));
        JLabel label = new JLabel("      " + text);
        label.setFont(new Font("ubuntu", 0, 8));
        label.setForeground(Color.DARK_GRAY);
        button.add(label);
        button.setLayout(new BoxLayout(button, BoxLayout.X_AXIS));
        button.setVisible(true);
        return button;
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();

        setAlwaysOnTop(true);
        setAutoRequestFocus(false);
        setBackground(UI.BACKGROUND);
        setFocusable(false);
        setFocusableWindowState(false);
        setMaximumSize(new java.awt.Dimension(1200, 700));
        setPreferredSize(new java.awt.Dimension(1200, 700));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(1200, 1200));

        jPanel1.setBackground(UI.BACKGROUND);
        jPanel1.setMaximumSize(new java.awt.Dimension(1300, 900));
        jPanel1.setPreferredSize(new java.awt.Dimension(1300, 900));
        jPanel1.setLayout(new java.awt.GridLayout(1, 23));
        jScrollPane1.setViewportView(jPanel1);

        jMenu1.setText("Settings");
        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

}