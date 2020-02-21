/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emre;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.UIManager;
import mdlaf.MaterialLookAndFeel;
import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler.ChartTheme;

/**
 * @author emre
 */
public class Main extends javax.swing.JFrame {

    private ArrayList<Double> timeDomain = new ArrayList<>();
    private ArrayList<Double> tempList = new ArrayList<>();
    private ArrayList<Double> accList = new ArrayList<>();
    private ArrayList<Double> pressureList = new ArrayList<>();
    private ArrayList<Double> altitudeList = new ArrayList<>();
    
    private GLTD u;
    
    private ServerSocket server;

    public Main() {
        initComponents();

        setTitle("Megatech - Yer İstasyonu");

        MaterialUIMovement.add(jButton1, MaterialColors.BLUE_200);
        MaterialUIMovement.add(jButton2, MaterialColors.BLUE_200);

        prepareGraphs();

        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //JOptionPane.showMessageDialog(startServerButton, "Hi");

            }
        });
    }

    private int iq = 1;

    //private Universe u;
    private XYChart temperatureGraph, accelerationGraph, pressureGraph, velocityGraph, altitudeGraph;

    private void prepareGraphs() {

        accelerationGraph = new XYChartBuilder().width(800).height(800).theme(ChartTheme.GGPlot2).build();
        temperatureGraph = new XYChartBuilder().width(800).height(800).theme(ChartTheme.GGPlot2).build();
        altitudeGraph = new XYChartBuilder().width(800).height(800).theme(ChartTheme.GGPlot2).build();
        pressureGraph = new XYChartBuilder().width(800).height(800).theme(ChartTheme.GGPlot2).build();

        accPanel.setPreferredSize(new Dimension(300, 300));
        accPanel.setLayout(new java.awt.BorderLayout());

        pressurePanel.setPreferredSize(new Dimension(300, 300));
        pressurePanel.setLayout(new java.awt.BorderLayout());

        altitudePanel.setPreferredSize(new Dimension(300, 300));
        altitudePanel.setLayout(new java.awt.BorderLayout());

        tdpanel.setPreferredSize(new Dimension(250, 250));
        tdpanel.setLayout(new java.awt.BorderLayout());

        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JPanel chartPanel = new XChartPanel<XYChart>(accelerationGraph);
                accPanel.add(chartPanel, BorderLayout.CENTER);

                JPanel chartPanel2 = new XChartPanel<XYChart>(pressureGraph);
                pressurePanel.add(chartPanel2, BorderLayout.CENTER);

                JPanel chartPanel3 = new XChartPanel<XYChart>(altitudeGraph);
                altitudePanel.add(chartPanel3, BorderLayout.CENTER);

                u = new GLTD();

                tdpanel.add(u);

                //tdpanel.repaint();
            }
        });

        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                accList.add(1.0 * generateRandomInt(20));
                pressureList.add(1.0 * generateRandomInt(70));
                altitudeList.add(1.0 * generateRandomInt(15));
                timeDomain.add(timeDomain.size() + 1.0);

                updateChart(1);
                updateChart(2);
                updateChart(3);

            }
        };

        Timer timer = new Timer();

        accPanel.validate();
        pressurePanel.validate();
        altitudePanel.validate();

        timer.scheduleAtFixedRate(t, 750, 750);
    }

    private String line;
    private String logs  = "";

    /*
    Message notations
    a-ivme
    3d-3d özellikler
    */
    
    private void initSocketServer() {
        try {
            server = new ServerSocket(9500);

            new Thread(new Runnable() {
                @Override
                public void run() {

                    System.out.println("Listening for connection on port 9500...");

                    while (true) {
                        try {
                            Socket clientSocket = server.accept();
                            InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
                            BufferedReader reader = new BufferedReader(isr);
                            line = reader.readLine();

                            while (line != null) {
                                if (!line.isEmpty()) {
                                    System.out.println(line);
                                    
                                    if (line != null) {
                                        logs += "\n" + line;
                                    }else {
                                        logs += "\n" + "no message";
                                    }
                                    
                                    Random random = new Random();
                                    
                                    int[] data = {random.nextInt(15),random.nextInt(50)};
                                    
                                    u.onNewDataReceived(data);
                                    
                                    logPanel.setText("Connected\n" + logs);

                                    line = reader.readLine();
                                }
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }
            }).start();

        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //1-pressure 2-acc 3-alt 4-temp
    private void updateChart(int type) {
        switch (type) {
            case 1:
                if (pressureGraph.getSeriesMap().isEmpty()) {
                    XYSeries accSeries = pressureGraph.addSeries("pressure", timeDomain, pressureList);

                    pressureGraph.setXAxisTitle("Zaman (s)");
                    pressureGraph.setYAxisTitle("Basınç (hPa)");

                    accSeries.setLineColor(Color.blue);
                    accSeries.setMarkerColor(Color.blue);
                } else {
                    pressureGraph.updateXYSeries("pressure", timeDomain, pressureList, null);
                }

                pressurePanel.repaint();

            case 2:
                if (accelerationGraph.getSeriesMap().isEmpty()) {
                    XYSeries accSeries = accelerationGraph.addSeries("pressure", timeDomain, accList);

                    accelerationGraph.setXAxisTitle("Zaman (s)");
                    accelerationGraph.setYAxisTitle("İvme (m/s^2)");

                    accSeries.setLineColor(Color.red);
                    accSeries.setMarkerColor(Color.red);
                } else {
                    accelerationGraph.updateXYSeries("pressure", timeDomain, accList, null);
                }

                accPanel.repaint();

            case 3:
                if (altitudeGraph.getSeriesMap().isEmpty()) {
                    XYSeries accSeries = altitudeGraph.addSeries("alt", timeDomain, altitudeList);

                    altitudeGraph.setXAxisTitle("Zaman (s)");
                    altitudeGraph.setYAxisTitle("Yükseklik (m)");

                    accSeries.setLineColor(Color.darkGray);
                    accSeries.setMarkerColor(Color.darkGray);
                } else {
                    altitudeGraph.updateXYSeries("alt", timeDomain, altitudeList, null);
                }

                altitudePanel.repaint();
        }

    }

    private void startSocketServer() {
        //TODO
    }

    private int generateRandomInt(int max) {
        final Random r = new Random();
        return r.nextInt(max);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        logPanel = new javax.swing.JLabel();
        accPanel = new java.awt.Panel();
        jLabel4 = new javax.swing.JLabel();
        pressurePanel = new java.awt.Panel();
        altitudePanel = new java.awt.Panel();
        tdpanel = new java.awt.Panel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(51, 51, 51));

        jButton1.setText("Başlat");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Durdur");

        jLabel1.setText("Server Status: Offline");

        jLabel2.setText("Client Logs:");

        logPanel.setBackground(new java.awt.Color(51, 51, 51));
        logPanel.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        logPanel.setForeground(new java.awt.Color(255, 255, 255));
        logPanel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        logPanel.setText("CONNECTED! TEMP:5, ATM: 5 Bar");
        logPanel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        logPanel.setRequestFocusEnabled(false);
        logPanel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout accPanelLayout = new javax.swing.GroupLayout(accPanel);
        accPanel.setLayout(accPanelLayout);
        accPanelLayout.setHorizontalGroup(
            accPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 284, Short.MAX_VALUE)
        );
        accPanelLayout.setVerticalGroup(
            accPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 291, Short.MAX_VALUE)
        );

        jLabel4.setText("İletişim Protokolü Ayarları:");

        javax.swing.GroupLayout pressurePanelLayout = new javax.swing.GroupLayout(pressurePanel);
        pressurePanel.setLayout(pressurePanelLayout);
        pressurePanelLayout.setHorizontalGroup(
            pressurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 281, Short.MAX_VALUE)
        );
        pressurePanelLayout.setVerticalGroup(
            pressurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 265, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout altitudePanelLayout = new javax.swing.GroupLayout(altitudePanel);
        altitudePanel.setLayout(altitudePanelLayout);
        altitudePanelLayout.setHorizontalGroup(
            altitudePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 264, Short.MAX_VALUE)
        );
        altitudePanelLayout.setVerticalGroup(
            altitudePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 271, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout tdpanelLayout = new javax.swing.GroupLayout(tdpanel);
        tdpanel.setLayout(tdpanelLayout);
        tdpanelLayout.setHorizontalGroup(
            tdpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 221, Short.MAX_VALUE)
        );
        tdpanelLayout.setVerticalGroup(
            tdpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 203, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(86, 86, 86)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton1))
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(tdpanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(427, 427, 427)
                .addComponent(accPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(altitudePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 601, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addComponent(pressurePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(1285, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(accPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(altitudePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 415, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(logPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(tdpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2)
                            .addComponent(jLabel1))))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(pressurePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(577, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

        initSocketServer();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(new MaterialLookAndFeel());
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Panel accPanel;
    private java.awt.Panel altitudePanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel logPanel;
    private java.awt.Panel pressurePanel;
    private java.awt.Panel tdpanel;
    // End of variables declaration//GEN-END:variables
}
