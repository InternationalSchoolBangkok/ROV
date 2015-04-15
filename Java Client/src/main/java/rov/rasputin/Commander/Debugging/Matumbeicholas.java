/*
 * The MIT License
 *
 * Copyright 2015 Kolatat Thangkasemvathana <kolatat.t@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package rov.rasputin.Commander.Debugging;

import java.awt.Color;
import java.awt.Graphics2D;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import rov.rasputin.Communication.ROV;

/**
 *
 * @author Kolatat Thangkasemvathana <kolatat.t@gmail.com>
 */
public class Matumbeicholas extends javax.swing.JFrame
{

    private ROV rasputin;
    private DefaultTableModel dtm;
    private Worker myWorker;

    private Grapher[] grapher;

    /**
     * Creates new form PIDTuner
     */
    public Matumbeicholas()
    {
        try {
            rasputin = new ROV("10.69.69.69", 6969, 6969, 32, 50);
        } catch(Exception ex) {
            Logger.getLogger(Matumbeicholas.class.getName()).log(Level.SEVERE, null, ex);
        }
        myWorker = new Worker();

        initComponents();

        dtm = (DefaultTableModel) stateTable.getModel();

        rasputin.startTXRX();
        myWorker.start();

        grapher = new Grapher[3];
        grapher[0] = new Grapher(graph0);
        grapher[0].start();
    }

    private class Worker extends Thread
    {

        @Override
        public void run()
        {
            while(true) {
                for(int i = 0; i < 32; ++i) {
                    dtm.setValueAt(rasputin.get(i), i, 2);
                }
                try {
                    Thread.sleep(50);
                } catch(InterruptedException ex) {
                    Logger.getLogger(Matumbeicholas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    private class Grapher extends Thread
    {

        private JPanel panel;
        public int gMode;
        public int channel = 0, resX = 20, resY = 32, previousValue;
        public double integral;
        public int gx = 0;
        private Graphics2D g;

        public Grapher(JPanel panel)
        {
            this.panel = panel;
            gMode = 0;
            g = (Graphics2D) panel.getGraphics();
        }

        public void reset()
        {
            previousValue = rasputin.get(channel);
            integral = 0;
            gx = 0;
            g.setColor(Color.white);
            g.fillRect(0, 0, panel.getWidth(), panel.getHeight());
        }

        @Override
        public void run()
        {
            //reset();

            int oy;
            double yscale;
            double y = 0;

            while(true) {
                if(gx > panel.getWidth()) {
                    gx %= panel.getWidth();
                    g.setColor(new Color(255, 255, 255, 200));
                    g.fillRect(0, 0, panel.getWidth(), panel.getHeight());
                }

                oy = panel.getHeight() / 2;
                yscale = panel.getHeight() / (double) resY;

                g.setColor(Color.white);
                g.drawLine(gx, 0, gx, oy * 2);
                g.setColor(Color.black);
                g.drawLine(gx + 1, 0, gx + 1, oy * 2);

                switch(gMode) {
                case 0:
                    y = rasputin.get(channel);
                    break;
                case 1:
                    integral += rasputin.get(channel) * resX / 1000d;
                    y = (int) integral;
                    break;
                case 2:
                    y = (rasputin.get(channel) - previousValue) / (resX / 1000d);
                    previousValue = rasputin.get(channel);
                    break;
                }

                g.setColor(Color.blue);
                g.drawLine(gx, (int) (oy - y * yscale), gx, (int) (oy - y * yscale));

                gx++;
                try {
                    Thread.sleep(resX);
                } catch(InterruptedException ex) {
                    Logger.getLogger(Matumbeicholas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        stateTableSp = new javax.swing.JScrollPane();
        stateTable = new javax.swing.JTable();
        resetBtn = new javax.swing.JButton();
        setBtn = new javax.swing.JButton();
        status = new javax.swing.JLabel();
        g0mode = new javax.swing.JComboBox();
        graph0 = new javax.swing.JPanel();
        g0channel = new javax.swing.JComboBox();
        g0resX = new javax.swing.JComboBox();
        g0resY = new javax.swing.JComboBox();
        g0reset = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Niggress Matumbeicholas Ramirez-Icaza");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        stateTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Direction State",  new Short((short) 0), null,  new Byte((byte) 0)},
                {"Misc. State",  new Short((short) 1), null,  new Byte((byte) 0)},
                {"Left x",  new Short((short) 2), null,  new Byte((byte) 0)},
                {"Left y",  new Short((short) 3), null,  new Byte((byte) 0)},
                {"Right x",  new Short((short) 4), null,  new Byte((byte) 0)},
                {"Right y",  new Short((short) 5), null,  new Byte((byte) 0)},
                {null,  new Short((short) 6), null,  new Byte((byte) 0)},
                {null,  new Short((short) 7), null,  new Byte((byte) 0)},
                {null,  new Short((short) 8), null,  new Byte((byte) 0)},
                {null,  new Short((short) 9), null,  new Byte((byte) 0)},
                {null,  new Short((short) 10), null,  new Byte((byte) 0)},
                {null,  new Short((short) 11), null,  new Byte((byte) 0)},
                {null,  new Short((short) 12), null,  new Byte((byte) 0)},
                {null,  new Short((short) 13), null,  new Byte((byte) 0)},
                {null,  new Short((short) 14), null,  new Byte((byte) 0)},
                {null,  new Short((short) 15), null,  new Byte((byte) 0)},
                {null,  new Short((short) 16), null,  new Byte((byte) 0)},
                {null,  new Short((short) 17), null,  new Byte((byte) 0)},
                {null,  new Short((short) 18), null,  new Byte((byte) 0)},
                {null,  new Short((short) 19), null,  new Byte((byte) 0)},
                {null,  new Short((short) 20), null,  new Byte((byte) 0)},
                {null,  new Short((short) 21), null,  new Byte((byte) 0)},
                {null,  new Short((short) 22), null,  new Byte((byte) 0)},
                {null,  new Short((short) 23), null,  new Byte((byte) 0)},
                {null,  new Short((short) 24), null,  new Byte((byte) 0)},
                {null,  new Short((short) 25), null,  new Byte((byte) 0)},
                {null,  new Short((short) 26), null,  new Byte((byte) 0)},
                {null,  new Short((short) 27), null,  new Byte((byte) 0)},
                {null,  new Short((short) 28), null,  new Byte((byte) 0)},
                {"D/PID/Kp",  new Short((short) 29), null,  new Byte((byte) 0)},
                {"D/PID/Ki",  new Short((short) 30), null,  new Byte((byte) 0)},
                {"D/PID/Kd",  new Short((short) 31), null,  new Byte((byte) 0)}
            },
            new String [] {
                "Mapping", "Channel", "Commander", "Rasputin"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Short.class, java.lang.Byte.class, java.lang.Byte.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        stateTable.setColumnSelectionAllowed(true);
        stateTable.getTableHeader().setReorderingAllowed(false);
        stateTableSp.setViewportView(stateTable);
        stateTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        resetBtn.setText("Reset Rasputin");
        resetBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetBtnActionPerformed(evt);
            }
        });

        setBtn.setText("Set Rasputin");
        setBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setBtnActionPerformed(evt);
            }
        });

        status.setText("STATUS");

        g0mode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Direct", "Integral", "Derivative" }));
        g0mode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                g0modeActionPerformed(evt);
            }
        });

        graph0.setBackground(new java.awt.Color(255, 255, 255));
        graph0.setAutoscrolls(true);
        graph0.setPreferredSize(new java.awt.Dimension(500, 128));

        javax.swing.GroupLayout graph0Layout = new javax.swing.GroupLayout(graph0);
        graph0.setLayout(graph0Layout);
        graph0Layout.setHorizontalGroup(
            graph0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );
        graph0Layout.setVerticalGroup(
            graph0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 128, Short.MAX_VALUE)
        );

        g0channel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        g0channel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                g0channelActionPerformed(evt);
            }
        });

        g0resX.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "20", "50", "100", "150", "200" }));
        g0resX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                g0resXActionPerformed(evt);
            }
        });

        g0resY.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "32", "64", "128", "256", "512", "1024", "2048" }));
        g0resY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                g0resYActionPerformed(evt);
            }
        });

        g0reset.setText("Reset");
        g0reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                g0resetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(stateTableSp, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(resetBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(setBtn))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(graph0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(g0mode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(g0channel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(g0resX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(g0resY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(g0reset)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(resetBtn)
                            .addComponent(setBtn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(status)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(g0mode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g0channel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g0resX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g0resY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g0reset))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(graph0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 325, Short.MAX_VALUE))
                    .addComponent(stateTableSp))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        System.exit(0);
    }//GEN-LAST:event_formWindowClosing

    private void g0modeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_g0modeActionPerformed
        grapher[0].gMode = g0mode.getSelectedIndex();
        grapher[0].reset();
    }//GEN-LAST:event_g0modeActionPerformed

    private void g0channelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_g0channelActionPerformed
        grapher[0].channel = Integer.parseInt(g0channel.getSelectedItem().toString());
        grapher[0].reset();
    }//GEN-LAST:event_g0channelActionPerformed

    private void g0resXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_g0resXActionPerformed
        grapher[0].resX = Integer.parseInt(g0resX.getSelectedItem().toString());
        grapher[0].reset();
    }//GEN-LAST:event_g0resXActionPerformed

    private void g0resYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_g0resYActionPerformed
        grapher[0].resY = Integer.parseInt(g0resY.getSelectedItem().toString());
        grapher[0].reset();
    }//GEN-LAST:event_g0resYActionPerformed

    private void g0resetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_g0resetActionPerformed
        grapher[0].reset();
    }//GEN-LAST:event_g0resetActionPerformed

    private void setBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setBtnActionPerformed
        for(int i = 0; i < 32; i++) {
            int val = (Byte) dtm.getValueAt(i, 3);
            rasputin.set(i, val);
            System.out.printf("%d=%d,", i, val);
        }
        System.out.println();
    }//GEN-LAST:event_setBtnActionPerformed

    private void resetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBtnActionPerformed
        for(int i = 0; i < 32; i++) {
            dtm.setValueAt(0, i, 3);
            rasputin.set(i, 0);
        }
    }//GEN-LAST:event_resetBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        /* Set the Nimbus look and feel */
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Matumbeicholas().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox g0channel;
    private javax.swing.JComboBox g0mode;
    private javax.swing.JComboBox g0resX;
    private javax.swing.JComboBox g0resY;
    private javax.swing.JButton g0reset;
    private javax.swing.JPanel graph0;
    private javax.swing.JButton resetBtn;
    private javax.swing.JButton setBtn;
    private javax.swing.JTable stateTable;
    private javax.swing.JScrollPane stateTableSp;
    private javax.swing.JLabel status;
    // End of variables declaration//GEN-END:variables
}
