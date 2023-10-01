package com.darwish.nppsim;

import static com.darwish.nppsim.NPPSim.core;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.metal.MetalToggleButtonUI;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXTable;

public class CoreMap extends javax.swing.JFrame implements UIUpdateable {
    private short rowNumber = 0;
    private final ArrayList<windowChannelBinding> openWindowArray = new ArrayList<>();

    public CoreMap() {
        initComponents();
        this.setTitle("Core Map");
        while (rowNumber != 50) {
            createRow();
        }
        
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent we) {
                // throw new UnsupportedOperationException("Not supported yet."); // Generated
                // from
                // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void windowClosing(WindowEvent we) {
                openWindowArray.forEach(i -> {
                    i.window.setVisible(false);
                });
            }

            @Override
            public void windowClosed(WindowEvent we) {
                // throw new UnsupportedOperationException("Not supported yet."); // Generated
                // from
                // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void windowIconified(WindowEvent we) {
                // throw new UnsupportedOperationException("Not supported yet."); // Generated
                // from
                // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void windowDeiconified(WindowEvent we) {
                // throw new UnsupportedOperationException("Not supported yet."); // Generated
                // from
                // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void windowActivated(WindowEvent we) {
                openWindowArray.forEach(i -> {
                    i.window.setVisible(true);
                });
            }

            @Override
            public void windowDeactivated(WindowEvent we) {
                // throw new UnsupportedOperationException("Not supported yet."); // Generated
                // from
                // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

        });
    }

    @Override
    public void update() {
        if (this.isVisible()) {
            openWindowArray.forEach(binding -> {
                for (int i = 0; i < binding.channel.uiData.tableData.length; i++) {
                    binding.table.getModel().setValueAt(binding.channel.uiData.tableData[i][1], i, 1);
                }
            });
        }
    }
    
    @Override
    public void initializeDialUpdateThread() {
        //void
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
        javax.swing.JPanel row = new javax.swing.JPanel();
        row.setBackground(UI.BACKGROUND);
        row.setMaximumSize(new java.awt.Dimension(1000, 40));
        row.setMinimumSize(new java.awt.Dimension(1000, 20));
        row.setPreferredSize(new java.awt.Dimension(1000, 26));
        row.setRequestFocusEnabled(false);
        row.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        for (short i = 50; i > 0; i--) {
            Channel currentChannel = core.coreArray.get(50 - rowNumber + 2).get(i + 2);
            if (currentChannel instanceof FuelChannel || currentChannel instanceof CPSChannel) {
                javax.swing.JToggleButton button = new javax.swing.JToggleButton();
                button.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.darkGray, currentChannel.uiData.UIBackgroundColor, java.awt.Color.darkGray, currentChannel.uiData.UIBackgroundColor));
                button.setFont(new java.awt.Font("Ubuntu", 1, 6)); // NOI18N
                button.setText(currentChannel.uiData.positionString);
                button.setForeground(Color.BLACK);
                button.setMargin(new java.awt.Insets(0, 0, 0, 0));
                button.setMaximumSize(new java.awt.Dimension(35, 35));
                button.setMinimumSize(new java.awt.Dimension(10, 10));
                button.setPreferredSize(new java.awt.Dimension(20, 20));
                button.setBackground(currentChannel.uiData.UIBackgroundColor);
                button.setAlignmentX(0.5f);
                button.setUI(new MetalToggleButtonUI() {
                    @Override
                    protected Color getSelectColor() {
                        return currentChannel.uiData.UISelectedColor;
                    }
                });
                button.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent me) {
                        if (button.isSelected()) {
                            JFrame panel = new JFrame();
                            JXTable data = new JXTable();
                            data.setModel(new DefaultTableModel(currentChannel.uiData.tableData, new String[] {"Variable", "Value", "Unit"}) {

                            });
                            if (data.getRowCount() != 0 && data.getColumnCount() != 0) {
                                data.setRowSelectionInterval(0, data.getRowCount() - 1);
                                data.setColumnSelectionInterval(0, data.getColumnCount() - 1);
                            }
                            data.setCellSelectionEnabled(false);
                            data.setDefaultEditor(Object.class, null);
                            data.packAll();
                            panel.setTitle(currentChannel.uiData.positionString);
                            panel.setFocusable(false);
                            panel.setFocusableWindowState(false);
                            panel.add(data);
                            data.setVisible(true);
                            panel.setAlwaysOnTop(true);
                            panel.pack();
                            openWindowArray.add(new windowChannelBinding(panel, data, currentChannel));
                            panel.setLocation((int)getMousePosition().getX() + 15, (int)getMousePosition().getY() + 30);
                            panel.setVisible(true);
                            panel.setLayout(null);
                            panel.setResizable(false);
                            panel.addWindowListener(new WindowListener() {
                                @Override
                                public void windowClosing(WindowEvent e) {
                                    button.setSelected(false);
                                    for(int i = 0; i < openWindowArray.size(); i++) {
                                        if (openWindowArray.get(i).window == panel) {
                                            openWindowArray.remove(openWindowArray.get(i));
                                        }
                                    }
                                    e.getWindow().dispose();
                                }

                                @Override
                                public void windowActivated(WindowEvent e) {
                                }

                                @Override
                                public void windowClosed(WindowEvent e) {
                                }

                                @Override
                                public void windowDeactivated(WindowEvent e) {
                                }

                                @Override
                                public void windowDeiconified(WindowEvent e) {
                                }

                                @Override
                                public void windowIconified(WindowEvent e) {
                                }

                                @Override
                                public void windowOpened(WindowEvent e) {
                                    button.addChangeListener((ChangeEvent e1) -> {
                                        if (!button.isSelected()) {
                                            for(int i = 0; i < openWindowArray.size(); i++) {
                                                if (openWindowArray.get(i).window == panel) {
                                                    openWindowArray.remove(openWindowArray.get(i));
                                                }
                                            }
                                            panel.dispose();
                                        }
                                    });
                                }
                            });
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent me) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent me) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent me) {
                    }

                    @Override
                    public void mouseExited(MouseEvent me) {
                    }
                });

                row.add(button);
            } else {
                javax.swing.JPanel button = new javax.swing.JPanel();
                button.setBackground(UI.BACKGROUND);
                button.setMaximumSize(new java.awt.Dimension(35, 35));
                button.setMinimumSize(new java.awt.Dimension(10, 10));
                button.setPreferredSize(new java.awt.Dimension(20, 20));
                button.setVisible(true);
                row.add(button);
            }
        }
        jPanel1.add(row);
        rowNumber++;
        return row;
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

        setFocusable(false);
        setFocusableWindowState(false);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(800, 800));

        jPanel1.setBackground(new java.awt.Color(155, 92, 38));
        jPanel1.setPreferredSize(new java.awt.Dimension(1000, 1000));
        jPanel1.setLayout(new java.awt.GridLayout(50, 0));
        jScrollPane1.setViewportView(jPanel1);

        jMenu1.setText("Settings");
        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    private class windowChannelBinding {
        JFrame window;
        JXTable table;
        Channel channel;
        public windowChannelBinding(JFrame window, JXTable table, Channel channel) {
            this.channel = channel;
            this.table = table;
            this.window = window;
        }
        
    }
}