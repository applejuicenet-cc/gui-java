package de.tklsoft.gui.controls;

import de.tklsoft.gui.controls.DelegationObject;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;

public class TKLLabel extends JLabel {

   public TKLLabel(String text, Icon image) {
      super(text, image, 0);
      this.init();
   }

   public TKLLabel(String text) {
      this(text, (Icon)null);
   }

   public TKLLabel(Icon image) {
      this("", image);
   }

   public TKLLabel() {
      this("", (Icon)null);
   }

   private void init() {
      this.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent keyEvent) {
            super.keyReleased(keyEvent);
            DelegationObject.notifyPapas(TKLLabel.this.getParent(), keyEvent);
         }
      });
   }

   public void enableBorder() {
      this.setBorder(BorderFactory.createEtchedBorder());
   }
}
