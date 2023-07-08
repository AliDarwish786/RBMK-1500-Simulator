package com.darwish.nppsim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.data.general.DefaultValueDataset;

public class Selsyn extends JPanel {
    private static final Color DARKGREY = new Color(15,15,15);
    private static final Color LABELGREY = new Color(30,30,30);
    private static final Color LT_GRAY = new Color(0xe0e0e0);
    private final DefaultValueDataset data;
    private final StandardDialFrame frame, innerFrame, centerFrame;
    private final Font font; 
    private final DialPointer.Pointer pointer;
    private final DecimalFormat decimalFormat;
    private DialBackground backgroundLayer;
    private DialPlot plot;
    private JFreeChart chart;
    private StandardDialScale scale;
    private ChartPanel chartPanel;

    public Selsyn() {
        data = new DefaultValueDataset(7);
        plot = new DialPlot(data);
        font = new Font("Verdana", Font.BOLD, 27);
        frame = new StandardDialFrame();
        chart = new JFreeChart(plot);
        pointer = new DialPointer.Pointer();
        innerFrame = new StandardDialFrame();
        centerFrame = new StandardDialFrame();
        decimalFormat = new DecimalFormat();
        backgroundLayer = new DialBackground(LT_GRAY);
        frame.setStroke(new BasicStroke(7));
        innerFrame.setStroke(new BasicStroke(10));
        centerFrame.setStroke(new BasicStroke(10));
        frame.setRadius(0.95);
        centerFrame.setRadius(0.07);
        innerFrame.setRadius(0.15);
        innerFrame.setForegroundPaint(DARKGREY);
        innerFrame.setBackgroundPaint(DARKGREY);
        centerFrame.setForegroundPaint(Color.GRAY);
        pointer.setRadius(0.8);
        pointer.setWidthRadius(0.1);
        pointer.setFillPaint(DARKGREY);
        this.setLayout(new GridBagLayout());
        this.setVisible(true);
        display();
    }

    public void setValue(double value) {
        data.setValue(value);
    }

    public void setFrameColor(Color color) {
        frame.setForegroundPaint(color);
        frame.setBackgroundPaint(color.darker());
    }

    public void setBackgroundColor(Color color) {
        chart.setBackgroundPaint(color);
    }

    public void setDialColor(Color color) {
        plot.setBackground(new DialBackground(color));
    }

    public void invert() {
        this.remove(chartPanel);
        plot = new DialPlot(data);
        plot.setDialFrame(frame);
        scale = new StandardDialScale(0, 7.3, -90, 290, 1, 10);
        scale.setTickLabelFormatter(decimalFormat);
        scale.setTickLabelFont(font);
        scale.setTickRadius(0.85);
        scale.setTickLabelOffset(0.2);
        scale.setMajorTickIncrement(1);
        scale.setTickLabelPaint(LABELGREY);
        plot.addScale(0, scale);
        plot.addLayer(pointer);
        plot.addLayer(innerFrame);
        plot.addLayer(centerFrame);
        plot.setBackground(backgroundLayer);
        chart.setBackgroundPaint(LT_GRAY);
        chart = new JFreeChart(plot);
        chartPanel = new ChartPanel(chart) {
            @Override
            public Dimension getMaximumSize(){
                return getCustomDimensions();
            }
            @Override
            public Dimension getMinimumSize(){
                return getCustomDimensions();
            }
            @Override
            public Dimension getPreferredSize(){
                return getCustomDimensions();
            }
        }; 
        this.add(chartPanel);
    }

    private Dimension getCustomDimensions(){
        return new Dimension((int)(getBounds().width), (int)(getBounds().height));
    }

    private void display() {
        plot.setDialFrame(frame);
        scale = new StandardDialScale(0, 7.3, 90, -290, 1, 10);
        scale.setTickLabelFormatter(decimalFormat);
        scale.setTickLabelFont(font);
        scale.setTickRadius(0.85);
        scale.setTickLabelOffset(0.2);
        scale.setMajorTickIncrement(1);
        scale.setTickLabelPaint(LABELGREY);
        plot.addScale(0, scale);
        DialPointer.Pointer pointer = new DialPointer.Pointer();
        pointer.setRadius(0.8);
        pointer.setWidthRadius(0.1);
        pointer.setFillPaint(DARKGREY);
        plot.addLayer(pointer);
        plot.addLayer(innerFrame);
        plot.addLayer(centerFrame);
        plot.setBackground(backgroundLayer);
        chart.setBackgroundPaint(LT_GRAY);
        chartPanel = new ChartPanel(chart) {
            @Override
            public Dimension getMaximumSize(){
                return getCustomDimensions();
            }
            @Override
            public Dimension getMinimumSize(){
                return getCustomDimensions();
            }
            @Override
            public Dimension getPreferredSize(){
                return getCustomDimensions();
            }
        }; 
        this.add(chartPanel);
    }
}


