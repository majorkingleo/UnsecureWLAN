/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN.views.images;

import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.imagestorage.ImageUtils;
import at.redeye.UnsecureWLAN.MainWin;
import at.redeye.UnsecureWLAN.StreamEntry;
import at.redeye.UnsecureWLAN.StreamEntryWithoutContent;
import at.redeye.UnsecureWLAN.views.browser.Port80Handler;
import java.util.HashMap;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author root
 */
public class ImagesView extends BaseDialog {

    MainWin main_win;    
    Port80Handler handler;    
    
    public ImagesView(Root root, MainWin main_win) {
        super(root, root.MlM("Bilder"));
        initComponents();
        
        this.main_win = main_win;                
        
        handler = new Port80Handler();
        main_win.registerHandler( handler );

        getAutoRefreshTimer().schedule(new TimerTask() {

            @Override
            public void run() {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        update();
                    }
                });
            }
        }, 300, 1000);        
    }

    @Override
    public void close()
    {
        main_win.unregisterHandler(handler);
        super.close();
    }
    
  public void update() {                
        
        HashMap<String,StreamEntryWithoutContent> con_map = handler.getEntries();
        boolean added_something = false;
        
        for (StreamEntryWithoutContent entry : con_map.values()) {

            StreamEntry e = (StreamEntry) entry;
            String data = null;

            byte[] bytes;
            boolean is_image = false;

            try {
                bytes = e.getByteArray();
                
                if (bytes != null) {

                    logger.debug(new String(bytes));
                    
                    DetectImage.IMAGE_TYPES type = DetectImage.guessImageType(bytes);

                    if (type != DetectImage.IMAGE_TYPES.UNKNOWN) {
                        is_image = true;
                    } 
                    
                    if( is_image ) {

                        logger.debug("image detected: " + e.getConnectionId() + " " + type.toString() );
                        ImageIcon icon = ImageUtils.loadImageIcon(bytes, e.getConnectionId());

                        imageList.add(new JLabel(e.getConnectionId(), icon, JLabel.LEFT));
                    }

                }
            } catch (Exception ex) {
                logger.debug(ex, ex);
            }

        }

    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        imageList = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        imageList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(imageList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList imageList;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
