package com.darwish.nppsim;

import static com.darwish.nppsim.Loader.soundProvider;
import static com.darwish.nppsim.NPPSim.autoControl;
import java.awt.Color;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Annunciator {
    private static boolean isInitialized = false;
    protected static boolean annunciatorBlinker = false;
    protected final HashMap<javax.swing.JTextField, Integer> annunciatorArray = new HashMap<>();
    public static final Color REDOFF_COLOR = new Color(142, 0, 0);
    public static final Color REDON_COLOR = new Color(255, 22, 25);
    public static final Color YELLOWOFF_COLOR = new Color(107, 103, 0);
    public static final Color YELLOWON_COLOR = new Color(255, 246, 35);
    public static final Color GREENON_COLOR = new Color(143, 228, 143);
    public static final Color GREENOFF_COLOR = new Color(27, 113, 27);
    public static final Color BLUEOFF_COLOR = new Color(8,106,107);
    public static final Color BLUEON_COLOR = new Color(15,206,208);
    public static Thread annunciatorThread;
    private static boolean alarmPlaying = false;

    /**
     * @param annunciatorPanel the panel with JTextFields which act as annunciator lights
     */
    public Annunciator(javax.swing.JPanel annunciatorPanel) {
        for(short i = 0; i < annunciatorPanel.getComponentCount(); i++) {
            annunciatorArray.put((javax.swing.JTextField)annunciatorPanel.getComponent(i), 0);
        }
        if(!isInitialized) {
            initializeAnnunciatorBlinker();
            isInitialized = true;
        }
    }
    
    private static void initializeAnnunciatorBlinker() {
        annunciatorThread = new Thread(() -> {
            while (true) {
                try {
                    while(NPPSim.isPaused()) {
                        Thread.sleep(500);
                    }
                    annunciatorBlinker = true;
                    Thread.sleep(500);
                    annunciatorBlinker = false;
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        annunciatorThread.start();
    }
    
    public void update() {
        annunciatorArray.forEach((field, state) -> {
            Color fieldColor = field.getBackground();
            Color colorOn, colorOff;
            if (fieldColor.equals(REDOFF_COLOR) || fieldColor.equals(REDON_COLOR)) {
                colorOn = REDON_COLOR;
                colorOff = REDOFF_COLOR;
            } else {
                colorOn = YELLOWON_COLOR;
                colorOff = YELLOWOFF_COLOR;
            }
            switch(state) {
                case 0:
                    field.setBackground(colorOff);
                    break;
                case 1:
                    field.setBackground(colorOn);
                    break;
                case 2:
                    if (annunciatorBlinker) {
                        field.setBackground(colorOff);
                    } else {
                        field.setBackground(colorOn);
                    }
                    alarmPlaying = true;
                    break;
            }
            if (alarmPlaying) {
                soundProvider.playContinuously(soundProvider.ALARM_1);
            } else {
                soundProvider.stop(soundProvider.ALARM_1);
            }
        });
    }
    
    public void trigger(javax.swing.JTextField element) {
        if (annunciatorArray.get(element) == 0) {
            autoControl.recordEvent("Warning: " + element.getText());
        } else {
            return;
        }
        annunciatorArray.replace(element, 0, 2);
        if (UI.hush) { 
            annunciatorArray.replace(element, 2, 1);
        } else {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(element);
            topFrame.setState(JFrame.NORMAL);
            topFrame.setVisible(true);
            topFrame.toFront();
        }
    }
    
    public void acknowledge() {
        annunciatorArray.forEach((element, state) -> {
            annunciatorArray.replace(element, 2, 1);
        });
        alarmPlaying = false;
        soundProvider.stop(soundProvider.ALARM_2);
    }
    
    public void reset(javax.swing.JTextField element) {
        if (annunciatorArray.get(element) != 0) {
            autoControl.recordEvent("Cleared: " + element.getText());
        } else {
            return;
        }
        annunciatorArray.replace(element, 0);
        if (annunciatorArray.containsValue(2)) {
            return;
        }
        alarmPlaying = false;
    }
    
    /**
     * Triggers or resets the element based on the condition given
     * @param condition the condition to trigger the element
     * @param element the element to trigger if condition is met
     */
    public void setTrigger(boolean condition, javax.swing.JTextField element) {
        if (condition) {
            trigger(element);
        } else {
            reset(element);
        }
    }
}
