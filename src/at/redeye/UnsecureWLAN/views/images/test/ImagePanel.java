/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN.views.images.test;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

class ImagePanel extends JPanel {
   private static final int PREF_W = (3 * 1280) / 4;
   private static final int PREF_H = (3 * 960) / 4;
   private BufferedImage img = null;

   public void setImage(BufferedImage img) {
      this.img = img;
      repaint();
   }

   @Override
   protected void paintComponent(Graphics g) {
      super.paintComponent(g);

      if (img == null) {
         return;
      }
      Graphics2D g2 = (Graphics2D)g;
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g2.drawImage(img, 0, 0, PREF_W, PREF_H, null);

   }

   @Override
   public Dimension getPreferredSize() {
      return new Dimension(PREF_W, PREF_H);
   }
}