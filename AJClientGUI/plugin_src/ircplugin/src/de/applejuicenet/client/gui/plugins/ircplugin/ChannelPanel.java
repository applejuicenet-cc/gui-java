package de.applejuicenet.client.gui.plugins.ircplugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import de.applejuicenet.client.gui.AppleJuiceDialog;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/ircplugin/ChannelPanel.java,v 1.1 2004/05/13 13:55:16 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r [Maj0r@applejuicenet.de]
 *
 */

public class ChannelPanel
    extends JPanel
    implements ActionListener, TabPanel {
    private String name;
    private SortedListModel usernameList = new SortedListModel();
    private JList userList = new JList(usernameList);
    private JTextPane textArea = new JTextPane();
    private JTextField textField;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
    private ArrayList befehle = new ArrayList();
    private int befehlPos = -1;

    private JTextPane titleArea = new JTextPane();

    private JButton closeButton = new JButton("X");
    private XdccIrc parentPanel;

    public ChannelPanel(XdccIrc parentPanel, String name) {
        this.name = name;
        this.parentPanel = parentPanel;
        makePanel();
    }

    public SortedListModel getUserNameList(){
        return usernameList;
    }

    public void selected(){

    }

    private void makePanel() {
        setLayout(new BorderLayout());

        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textArea.setEditable(false);
        textArea.setBackground(Color.WHITE);
        textField = new JTextField();
        Set set = new HashSet(1);
        set.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_JAPANESE_HIRAGANA, 0));
        textField.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, set);
        textField.addActionListener(this);
        textArea.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent ke){
                textField.setText(textField.getText() + ke.getKeyChar());
                textField.requestFocus();
            }

        });
        textField.addKeyListener(new KeyAdapter(){
            public void keyReleased(KeyEvent ke){
                super.keyReleased(ke);
                if (ke.getKeyCode() == KeyEvent.VK_TAB) {
                    String text = textField.getText();
                    if (text.length()>0){
                        int index = text.lastIndexOf(' ');
                        int index2 = text.lastIndexOf(',');
                        if (index2>index){
                            index = index2;
                        }
                        String searchString;
                        if (index != -1){
                            searchString = text.substring(index+1).toLowerCase();
                        }
                        else{
                            searchString = text.toLowerCase();
                        }
                        Set values = usernameList.getValues();
                        String treffer = "";
                        int count = 0;
                        synchronized (values) {
                            Iterator it = values.iterator();
                            String value;
                            while (it.hasNext()) {
                                value = (String) it.next();
                                if (value.indexOf('!') == 0 || value.indexOf('@') == 0 || value.indexOf('%') == 0 || value.indexOf('+') == 0){
                                    value = value.substring(1);
                                }
                                if (value.toLowerCase().indexOf(searchString)==0){
                                    treffer += value + " ";
                                    count ++;
                                }
                            }
                        }
                        if (treffer.length()>0){
                            treffer = treffer.substring(0,
                                treffer.length() - 1);
                            if (count == 1) {
                                if (index != -1) {
                                    String newText = text.subSequence(0,
                                        index + 1) + treffer;
                                    textField.setText(newText);
                                }
                                else {
                                    textField.setText(treffer);
                                }
                            }
                            else if (count > 1) {
                                updateTextArea("\t" + treffer, false);
                            }
                        }
                    }
                }
                else if (befehlPos != -1){
                    if (ke.getKeyCode() == KeyEvent.VK_UP) {
                        textField.setText((String)befehle.get(befehlPos));
                        if (befehlPos>0){
                            befehlPos--;
                        }
                    }
                    else if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
                        if (befehlPos<befehle.size()-1){
                            befehlPos++;
                        }
                        textField.setText( (String) befehle.get(
                            befehlPos));
                    }
                }
            }
        });

        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent me){
                if (me.getClickCount()==2){
                    String name = (String)userList.getSelectedValue();
                    if (name.charAt(0)=='!' || name.charAt(0)=='%' || name.charAt(0)=='@' || name.charAt(0)=='+'){
                        name = name.substring(1);
                    }
                    if (name.compareToIgnoreCase(parentPanel.getNickname()) != 0){
                        JTabbedPane tabbedPane = parentPanel.getTabbedPane();
                        for (int i = 1; i < tabbedPane.getTabCount(); i++) {
                            if (tabbedPane.getTitleAt(i).
                                compareToIgnoreCase(name) == 0) {
                                return;
                            }
                        }
                        parentPanel.addUser(tabbedPane, name);
                        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() -
                            1);
                    }
                }
            }
        });
        userList.setCellRenderer(new UserListCellRenderer());

        JScrollPane sp1 = new JScrollPane(textArea);
        JScrollPane sp2 = new JScrollPane(userList);

        sp1.setVerticalScrollBarPolicy(JScrollPane.
                                       VERTICAL_SCROLLBAR_ALWAYS);
        sp2.setVerticalScrollBarPolicy(JScrollPane.
                                       VERTICAL_SCROLLBAR_ALWAYS);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                              sp1,
                                              sp2);
        splitPane.setDividerLocation(AppleJuiceDialog.getApp().getSize().width - 200);
        add(splitPane, BorderLayout.CENTER);
        add(textField, BorderLayout.SOUTH);
        add(makeNorth(), BorderLayout.NORTH);
    }

    private Box makeNorth() {
        Box northBox = Box.createHorizontalBox();

        // let's add actions
        closeButton.addActionListener(this);

        northBox.add(closeButton);
        titleArea.setEditable(false);
        northBox.add(titleArea);

        return northBox;
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // let's take care of textField
        if (source == textField) {
            String message = textField.getText();
            if (message.length()>450){
                message = message.substring(0, 450);
            }
            // let's send to server
            if (message.startsWith("/")) {
                // commands that start with "/"
                // message = message.substring(1);
                parentPanel.analyzeCommand(message);
                textField.setText("");
            }
            else {
                // A private message to channel!
                parentPanel.parseSendToCommand("PRIVMSG " + name + " :" + message);

                // let's update textArea
                // textArea.append(textField.getText() + "\n");
                updateTextArea(parentPanel.formatNickname("<" + parentPanel.getNickname() + "> ") +
                               textField.getText());
                textField.setText("");
            }
            if (message != null && message.length() > 0){
                befehle.add(message);
                if (befehle.size() > 40) {
                    befehle.remove(0);
                }
                befehlPos = befehle.size() - 1;
            }
        }
        else if (source == closeButton) {
            parentPanel.closeChannel(parentPanel.getTabbedPane(), name);

            // Let's send a sensible message
            parentPanel.parseSendToCommand("PART " + name);
        }
    }

    public void setTitleArea(String title) {
        StyledDocument doc = titleArea.getStyledDocument();
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setBackground(attributes, Color.WHITE);
        try {
            doc.remove(0, doc.getLength());
            int startIndex = 0;
            for (int i=0; i<title.length(); i++){
                if (title.charAt(i) == 3 || i==title.length()-1){
                    if (title.charAt(i) == 3){
                        String toWrite = title.substring(startIndex, i);
                        if (toWrite.length()>0){
                            attributes = writeString(doc, attributes,
                                toWrite);
                        }
                        startIndex = i+1;
                        i++;
                    }
                    else{
                        String toWrite = title.substring(startIndex);
                        if (toWrite.length()>0){
                            attributes = writeString(doc, attributes,
                                toWrite);
                        }
                    }
                }
            }
        }
        catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    private SimpleAttributeSet writeString(StyledDocument doc, SimpleAttributeSet attributes, String toWrite){
        boolean istNachkomma = false;
        boolean parsEnde = false;
        int index = 0;
        if (toWrite.length()>1){
            while (!parsEnde) {
                if (toWrite.charAt(index) == ',') {
                    istNachkomma = true;
                    index++;
                    continue;
                }
                try {
                    int colorCode = Integer.parseInt(toWrite.substring(
                        index, index+2));
                    index += 2;
                    Color color = getColor(colorCode);
                    if (!istNachkomma) {
                        StyleConstants.setForeground(attributes, color);
                    }
                }
                catch (NumberFormatException nfE) {
                    if (toWrite.charAt(index + 1) != ',') {
                        parsEnde = true;
                    }
                    try {
                        int colorCode = Integer.parseInt(toWrite.substring(
                            index, index+1));
                        index++;
                        Color color = getColor(colorCode);
                        if (!istNachkomma) {
                            StyleConstants.setForeground(attributes, color);
                        }
                    }
                    catch (NumberFormatException nfE2) {
                        parsEnde = true;
                        StyleConstants.setForeground(attributes,
                            Color.BLACK);
                    }
                }
                catch(StringIndexOutOfBoundsException sioobE){
                    parsEnde = true;
                    int colorCode = Integer.parseInt(toWrite.substring(
                        index, index+1));
                    Color color = getColor(colorCode);
                    if (!istNachkomma) {
                        StyleConstants.setForeground(attributes, color);
                    }
                    return attributes;
                }
            }
        }
        try {
            doc.insertString(doc.getLength(),
                             toWrite.substring(index),
                             attributes);
        }
        catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        return attributes;
    }

    private Color getColor(int code){
        switch (code){
            case 0: return Color.BLACK;
            case 1: return Color.BLACK;
            case 2: return Color.BLUE;
            case 3: return Color.GREEN;
            case 4: return Color.RED;
            case 5: return Color.BLACK;
            case 6: return Color.PINK;
            case 7: return Color.ORANGE;
            case 8: return Color.YELLOW;
            case 9: return Color.GREEN;
            case 10: return Color.GREEN;
            case 11: return Color.CYAN;
            case 12: return Color.BLUE;
            case 13: return Color.PINK;
            case 14: return Color.GRAY;
            case 15: return Color.LIGHT_GRAY;
            default: return null;
        }
    }

    public void updateTextArea(String message, boolean withTimeStamp) {
        int oldCaretPosition = textArea.getCaretPosition();
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setBackground(attributes, Color.WHITE);
        StyleConstants.setForeground(attributes, Color.BLACK);
        int index = message.indexOf('>');
        String compareValue;
        if (index!=-1 && message.length()-1>index){
            compareValue = message.substring(index+1);
        }
        else{
            compareValue = message;
        }
        if (compareValue.indexOf(parentPanel.getNickname())!=-1){
            StyleConstants.setBackground(attributes, Color.ORANGE);
        }
        else if (message.indexOf("---> JOIN:")!=-1){
            StyleConstants.setForeground(attributes, Color.GREEN);
        }
        else if (message.indexOf("<--- PART:")!=-1){
            StyleConstants.setForeground(attributes, Color.RED);
        }
        else if (!withTimeStamp || index==-1){
            StyleConstants.setForeground(attributes, Color.GRAY);
        }
        else{
            StyleConstants.setForeground(attributes, Color.BLACK);
        }
        Document doc = textArea.getDocument();
        try{
            if (withTimeStamp) {
                String zeit = dateFormatter.format(new Date(System.
                    currentTimeMillis()));
                doc.insertString(doc.getLength(),
                                 "[" + zeit + "]\t" + message + "\n",
                                 attributes);
            }
            else {
                doc.insertString(doc.getLength(), message + "\n",
                                      attributes);
            }
        }
        catch(BadLocationException blE){
            blE.printStackTrace();
        }
        int newCaretPosition = textArea.getCaretPosition();
        if (newCaretPosition == oldCaretPosition) {
            textArea.setCaretPosition(oldCaretPosition +
                                      (message + "\n").length());
        }
    }


    public void updateTextArea(String message) {
        updateTextArea(message, true);
    }

    public void updateUserArea(String username, String command) {
        if (command.equals("add")) {
            usernameList.add(username);
        }

        else if (command.equals("remove")) {
            usernameList.remove(username);
        }
    }
}
