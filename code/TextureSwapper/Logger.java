import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class Logger extends JFrame{
    
    private JTextArea log;
    
    public Logger(String name) {
        
        this.setSize(new Dimension(640, 640));
        this.setTitle(name);     
        //this.setBackground(Color.BLACK);
        
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JScrollPane scrollArea = new JScrollPane();
        scrollArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        log = new JTextArea();
        log.setVisible(true);
        
        write("Log Output: ");
        scrollArea.setBounds(0, 0, this.getWidth() - 15, this.getHeight() - 40);
        scrollArea.getViewport().add(log);
        scrollArea.setVisible(true);
        
        this.add(scrollArea);
        
    }
    
    public void write(String text) {
        log.append(text + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }
}
