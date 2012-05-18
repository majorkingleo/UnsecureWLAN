/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN.views.images.test;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

class ImageUtil {
   public static final int DEST_WIDTH = 100;
   public static final int DEST_HEIGHT = 75;
   public static final double ASPECT_RATIO = (double) DEST_WIDTH / DEST_HEIGHT;

   public static BufferedImage createScaledImage(BufferedImage original) {
      double origAspectRatio = (double) original.getWidth()
            / original.getHeight();
      double scale = origAspectRatio > ASPECT_RATIO ? 
            (double) DEST_WIDTH / original.getWidth() : 
               (double) DEST_HEIGHT / original.getHeight();            
      int newW = (int) (original.getWidth() * scale);            
      int newH = (int) (original.getHeight() * scale);
      BufferedImage img = new BufferedImage(DEST_WIDTH, DEST_HEIGHT,
            BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = img.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g2.drawImage(original, 0, 0, newW, newH, null);
      g2.dispose();
      return img;
   }
}