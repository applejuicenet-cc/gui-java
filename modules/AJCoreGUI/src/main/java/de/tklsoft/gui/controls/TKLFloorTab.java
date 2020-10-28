package de.tklsoft.gui.controls;

import de.tklsoft.gui.controls.TKLFloorMainMenu;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JPanel;

public class TKLFloorTab extends JPanel {

   private ArrayList buttons = new ArrayList();
   private JPanel northPanel = new JPanel(new GridBagLayout());
   private JPanel southPanel = new JPanel(new GridBagLayout());
   private CardLayout cardLayout = new CardLayout();
   private JPanel centerPanel;
   private ArrayList mainMenuButtons;
   private HashMap yIndizes;
   private GridBagConstraints constraints;
   private int maxY;


   public TKLFloorTab() {
      this.centerPanel = new JPanel(this.cardLayout);
      this.mainMenuButtons = new ArrayList();
      this.yIndizes = new HashMap();
      this.constraints = new GridBagConstraints();
      this.maxY = -1;
      this.setLayout(new BorderLayout());
      this.add(this.northPanel, "North");
      this.add(this.southPanel, "South");
      this.add(this.centerPanel, "Center");
      this.constraints.anchor = 11;
      this.constraints.fill = 1;
      this.constraints.insets = new Insets(0, 0, 0, 0);
      this.constraints.weightx = 1.0D;
      this.constraints.weighty = 0.0D;
      this.constraints.gridx = 0;
   }

   public void showMenu(TKLFloorMainMenu tKLFloorMainMenu) {
      String name = tKLFloorMainMenu.getName();
      if(name != null && name.length() != 0) {
         Integer indexTmp = (Integer)this.yIndizes.get(name.toLowerCase());
         if(indexTmp != null) {
            int index = indexTmp.intValue();

            for(int i = 0; i < this.yIndizes.size(); ++i) {
               JButton northButton = ((TKLFloorMainMenu)this.mainMenuButtons.get(i)).getNorthButton();
               northButton.setSelected(false);
               JButton southButton = ((TKLFloorMainMenu)this.mainMenuButtons.get(i)).getSouthButton();
               southButton.setSelected(false);
               if(i <= index) {
                  northButton.setVisible(true);
                  if(i != index) {
                     northButton.setBackground(Color.LIGHT_GRAY);
                  } else {
                     northButton.setBackground(Color.WHITE);
                  }

                  southButton.setVisible(false);
               } else {
                  northButton.setVisible(false);
                  southButton.setVisible(true);
               }
            }

            this.cardLayout.show(this.centerPanel, name.toLowerCase());
         }

      }
   }

   public void addMenu(TKLFloorMainMenu tKLFloorMainMenu) {
      String name = tKLFloorMainMenu.getName();
      int size = this.mainMenuButtons.size();
      Iterator northButton = this.mainMenuButtons.iterator();

      TKLFloorMainMenu southButton;
      do {
         if(!northButton.hasNext()) {
            JButton northButton1 = tKLFloorMainMenu.getNorthButton();
            JButton southButton1 = tKLFloorMainMenu.getSouthButton();
            ++this.maxY;
            this.constraints.gridy = this.maxY;
            northButton1.setVisible(false);
            this.northPanel.add(northButton1, this.constraints);
            this.southPanel.add(southButton1, this.constraints);
            northButton1.setVisible(false);
            this.mainMenuButtons.add(tKLFloorMainMenu);
            this.yIndizes.put(name.toLowerCase(), new Integer(this.maxY));
            this.centerPanel.add(name.toLowerCase(), tKLFloorMainMenu.getMenuPanel());
            return;
         }

         southButton = (TKLFloorMainMenu)northButton.next();
      } while(!southButton.getName().equalsIgnoreCase(name));

      throw new RuntimeException("Menüname \'" + name + "\' bereits definiert!");
   }
}
