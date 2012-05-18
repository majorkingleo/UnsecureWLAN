/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN.views.images;

import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.imagestorage.ImageCellRenderer;
import at.redeye.UnsecureWLAN.MainWin;
import at.redeye.UnsecureWLAN.StreamEntry;
import at.redeye.UnsecureWLAN.StreamEntryWithoutContent;
import at.redeye.UnsecureWLAN.views.browser.Port80Handler;
import at.redeye.UnsecureWLAN.views.images.test.PicStrip;
import at.redeye.UnsecureWLAN.views.lib.HTTPContent;
import java.awt.MediaTracker;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *
 * @author root
 */
public class ImagesView extends BaseDialog {

    MainWin main_win;    
    Port80Handler handler;    
    Vector<JLabel> labels = new Vector<JLabel>();
    private DefaultListModel iconListModel = new DefaultListModel();
    
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
        
        imageList.setCellRenderer(new ImageCellRenderer());
        imageList.setModel(iconListModel);
                
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
        
        final long dropouttime = System.currentTimeMillis() - 1000L * 60 * 5;
        final long waittime = System.currentTimeMillis() - 1000L * 1;
        
        for (StreamEntryWithoutContent entry : con_map.values()) {

            final StreamEntry e = (StreamEntry) entry;
            String data = null;

            byte[] bytes = null;            
            boolean is_image = false;
            
            // drop out all pakets that are older than 5 minutes
            if( e.getCreationTime() < dropouttime ) {
                handler.removeEntry(entry);
            }
            
            // skip unifished loaded pictures
            if( e.getModificationTime() > waittime ) {
                continue;
            }

            try {
                bytes = e.getByteArray();
                
                if (bytes != null) {

                    // logger.debug(new String(bytes));
                    final HTTPContent content = new HTTPContent(bytes);
                    
                    byte[] payload = content.getPayload();
                    
                    if (payload != null) {
                        
                        final String mime_type = content.getHeader("Content-Type");
                        
                        if( mime_type != null ) {
                            is_image = is_image_mime_type(mime_type);
                        }
                        
                        DetectImage.IMAGE_TYPES type = DetectImage.IMAGE_TYPES.UNKNOWN;
                        
                        if (!is_image) {
                            type = DetectImage.guessImageType(payload);

                            if (type != DetectImage.IMAGE_TYPES.UNKNOWN) {
                                is_image = true;
                            }
                        }

                        if (is_image) {                           
                            
                            logger.debug("image detected: " + e.getConnectionId());
                            // ImageIcon icon = new ImageIcon(payload, e.getConnectionId() + "." + type.toString());

                            ImageIcon icon = null;
                            
                            try {
                                BufferedImage buf_img = ImageIO.read(new ByteArrayInputStream(payload));
                                
                                if( buf_img != null ) {                                
                                    icon = new ImageIcon(buf_img);
                                }
                            } catch( Exception ex ) {
                                logger.error(ex,ex);
                            }
                            
                            /*
                            FileOutputStream fout = new FileOutputStream("/tmp/foo.gif");
                            fout.write(payload);
                            fout.close();
                            */
                            if (icon != null) {
                                if (icon.getImageLoadStatus() == MediaTracker.ERRORED) {
                                    logger.debug("image has still errors");
                                    // this image needs more data                                                                  
                                } else {
                                    // logger.debug("Status: " + icon.getImageLoadStatus());
                                    JLabel label = new JLabel(e.getConnectionId(), icon, JLabel.LEFT);
                                    label.setVisible(true);
                                    labels.add(label);
                                    added_something = true;

                                    handler.removeEntry(entry);
                                    iconListModel.addElement(label);
                                }
                            }
                        }
                    }

                }
            } catch (Exception ex) {
                logger.debug(ex, ex);
            }

        }
        
      if (added_something) {
          Window win = SwingUtilities.getWindowAncestor(imageList);
          win.pack();

          /*
           * imageList.setListData(labels);
           */
          int lastIndex = imageList.getModel().getSize() - 1;
          if (lastIndex >= 0) {
              imageList.ensureIndexIsVisible(lastIndex);
          }
      }
    }
  
    static boolean is_image_mime_type( String mime )
    {
        if( mime.equals("image/jpeg") )
            return true;
        else if( mime.equals("image/gif") )
            return true;
        else if( mime.equals("image/png") )
            return true;

        return false;
    }
      

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        imageList = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

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
