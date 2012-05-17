/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN;

import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.BaseModuleLauncher;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.Setup;
import at.redeye.FrameWork.base.prm.impl.gui.LocalConfig;
import at.redeye.FrameWork.base.tablemanipulator.TableManipulator;
import at.redeye.Plugins.ShellExec.ShellExec;
import at.redeye.UnsecureWLAN.views.connections.ConnectionsView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import org.apache.log4j.PatternLayout;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

/**
 *
 * @author moberza
 */
public class MainWin extends BaseDialog {
    
    TableManipulator tm;

    StringBuilder errbuf = new StringBuilder();
    List<PcapIf> alldevs = new ArrayList<PcapIf>();
    ArrayList<InterfaceStruct> interfaces;
    Vector<DeviceListener> listeners;
    ArrayList<StreamHandler> handlers = new ArrayList<StreamHandler>();
    
    public MainWin(Root root) {
        super(root,root.getAppTitle());
        initComponents();
        
        /*
        String path = System.getProperty("JNETPCAP_HOME") + "/" + JNetPcapDLL.getLibName();
        System.out.println(System.getProperty("java.library.path"));
        System.load(path);     
        //System.loadLibrary("jnetpcap_x86");        
        */
        
        TextAreaAppender appender = new TextAreaAppender();
        PatternLayout layout = new PatternLayout("%m%n"); //new PatternLayout("%d{ISO8601} %-5p (%F:%L): %m%n");
        appender.setLayout(layout);
        appender.setTextArea(jTextArea1);
        
        BaseModuleLauncher.logger.addAppender( appender  );
                
        
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                
                try {
                    initDeviceList();
                } catch( UnsatisfiedLinkError ex ) {
                    if( ex.toString().contains("dependent") || ex.toString().contains("jnetpcap")) {
                        logger.error(ex,ex);
                        
                        if( Setup.is_linux_system() ) {
                            JOptionPane.showMessageDialog(rootPane, "Pleasy copy libjnetpcap.so from ext resources to you library path and restart this app" );
                        } else if( Setup.is_win_system() ) {
                            JOptionPane.showMessageDialog(rootPane, "Please Install the WinPcap Library http://www.winpcap.org/install/default.htm");
                        } 
                                                                       
                        if( Setup.is_win_system() ) {
                         ShellExec exec = new ShellExec();
                         exec.execute("http://www.winpcap.org/install/default.htm");
                        }
                    } else {
                        logger.error(ex,ex);
                    }
                }
            }
        });                           
        
        ButtonGroup bgroup  = new ButtonGroup();                  
          
    }
    
    void initDeviceList()
    {
        interfaces = new ArrayList<InterfaceStruct>();
        listeners = new Vector();
        
        int r = Pcap.findAllDevs(alldevs, errbuf);  
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {  
            logger.error(String.format("Can't read list of devices, error is %s %d", errbuf  
                .toString(), r));  
            return;  
        }  
  
        logger.debug("Network devices found:");            
        
        int i = 0;  
        for (PcapIf device : alldevs) {  
            String description =  
                (device.getDescription() != null) ? device.getDescription()  
                    : "No description available";                          
            
            logger.debug(String.format("#%d: %s [%s]", i++, device.getName(), description));                        
            
            InterfaceStruct iface = new InterfaceStruct();
            iface.iface.loadFromString(DeviceListener.getName(device));                                   
            iface.dev_name.loadFromString(device.getName());
            
            interfaces.add(iface);
/*            
            if( iface.iface.toString().contains("wlan") ) {     
                iface.listen.loadFromString("JA");
                DeviceListener listener = new DeviceListener(device, this);
                listener.start();
                listeners.add(listener);
                break;
            }
*/           
        }
               
        
        ButtonGroup bgroup  = new ButtonGroup();
        
        String last_dev_name = root.getSetup().getLocalConfig("listendevice",null);
        
        // create jmenu items
        for( InterfaceStruct iface :  interfaces) {
           
            JMenuItem miface = new JRadioButtonMenuItem(iface.iface.getValue());
            
            if( last_dev_name != null ) {
                if( last_dev_name.equals(iface.dev_name.getValue())) {
                    miface.setSelected(true);
                }
            }
            
            bgroup.add(miface);
            
            final InterfaceStruct my_iface = iface;
            
            miface.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    chooseInterface(my_iface);
                }
            });
            
            jMInterface.add(miface);            
        }
                        
        
        if( last_dev_name != null ) {
            InterfaceStruct iface = new InterfaceStruct();
            iface.dev_name.loadFromString(last_dev_name);
            chooseInterface(iface);
        }
    }
    
    public void chooseInterface(InterfaceStruct networkinterface)
    {
        stoppAllDevices();
        
        PcapIf device = null;
        
        for( PcapIf dev : alldevs ) {            
            if( dev.getName().equals(networkinterface.dev_name.getValue())) {
                device = dev;
                break;
            }
        }
        
        for (InterfaceStruct iface : interfaces) {
            logger.debug(iface.dev_name.getValue() + " == " + networkinterface.dev_name.getValue());
            if (iface.dev_name.getValue().equals(networkinterface.dev_name.getValue())) {
                logger.debug("YES");
                DeviceListener listener = new DeviceListener(device, this);
                listener.start();
                listeners.add(listener);
                logger.debug("listen on device " + networkinterface.dev_name.getValue());
                root.getSetup().setLocalConfig("listendevice", networkinterface.dev_name.getValue());

                for (StreamHandler handler : handlers) {
                    listener.addHandler(handler);
                }

                return;
            } // if
        } // for  
    }
    
    public void stoppAllDevices()
    {
        for (DeviceListener listener : listeners) {
            listener.doStop();
        }

        for (DeviceListener listener : listeners) {
            try {
                listener.join();
            } catch (InterruptedException ex) {
            }
        }

        listeners.clear();                
    }
    
    @Override
    public void close() {

        stoppAllDevices();
        
        listeners = null;
        interfaces = null;        

        super.close();
    }
  
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem2 = new javax.swing.JMenuItem();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMSettings = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMInterface = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMConnectionsView = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMAbout = new javax.swing.JMenuItem();
        jMChangeLog = new javax.swing.JMenuItem();

        jMenuItem2.setText("jMenuItem2");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        jMenu1.setText("Programm");

        jMSettings.setText("Einstellungen");
        jMSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMSettingsActionPerformed(evt);
            }
        });
        jMenu1.add(jMSettings);
        jMenu1.add(jSeparator2);

        jMenuItem1.setText("Beenden");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMInterface.setText("Interface");
        jMenuBar1.add(jMInterface);

        jMenu3.setText("Ansichten");

        jMConnectionsView.setText("Verbindungen");
        jMConnectionsView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMConnectionsViewActionPerformed(evt);
            }
        });
        jMenu3.add(jMConnectionsView);

        jMenuBar1.add(jMenu3);

        jMenu2.setText("Info");

        jMAbout.setText("Über");
        jMAbout.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jMAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMAboutActionPerformed(evt);
            }
        });
        jMenu2.add(jMAbout);

        jMChangeLog.setText("Änderungsprotokoll");
        jMChangeLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMChangeLogActionPerformed(evt);
            }
        });
        jMenu2.add(jMChangeLog);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMAboutActionPerformed
        invokeDialogUnique(new About(root));
    }//GEN-LAST:event_jMAboutActionPerformed

    private void jMChangeLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMChangeLogActionPerformed

        invokeDialogUnique(new LocalHelpWin(root, "ChangeLog"));
    }//GEN-LAST:event_jMChangeLogActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        
        close();
        
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMSettingsActionPerformed
        invokeDialogUnique(new LocalConfig(root));
    }//GEN-LAST:event_jMSettingsActionPerformed

    private void jMConnectionsViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMConnectionsViewActionPerformed
        invokeDialogUnique(new ConnectionsView(root, this));
    }//GEN-LAST:event_jMConnectionsViewActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem jMAbout;
    private javax.swing.JMenuItem jMChangeLog;
    private javax.swing.JMenuItem jMConnectionsView;
    private javax.swing.JMenu jMInterface;
    private javax.swing.JMenuItem jMSettings;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

    public void registerHandler(StreamHandler handler) {
        handlers.add(handler);
        
        for (DeviceListener listener : listeners) {
            listener.addHandler( handler );
        }
    }
    
    public void unregisterHandler(StreamHandler handler) {
        handlers.remove(handler);
        
        if( listeners != null ) {        
            for (DeviceListener listener : listeners) {
                listener.removeHandler( handler );
            }        
        }
    }
}
