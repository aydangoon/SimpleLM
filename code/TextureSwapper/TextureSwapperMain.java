import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TextureSwapperMain {

    public static void main(String[] args) {
        
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        String[] buttons = { "Dump Textures", "Add Texture"};    
        int optionVal = JOptionPane.showOptionDialog(window, "Selected an action.",
                "Texture Swapper", 0, JOptionPane.NO_OPTION, 
                null, buttons, buttons[0]);
        
        if (optionVal == 0) {
            
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter ghgFilter = new FileNameExtensionFilter(".GHG files", "ghg", "GHG");
            
            fileChooser.setFileFilter(ghgFilter);
            fileChooser.setDialogTitle("Select a character .GHG file (this should be the LR_PC.GHG file) to dump their textures.");
            
            int returnVal = fileChooser.showOpenDialog(window);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                
                File charFile = fileChooser.getSelectedFile();
                String charName = charFile.getName().substring(0, charFile.getName().length() - 4);
                
                Dumper dumper = new Dumper(charName, charFile);
                OutputStatus output = dumper.parse();
                
                if (output == OutputStatus.GOOD) { 
                    dumper.dump();   
                }
                
            }
            
        } else if (optionVal == 1) {
            
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter ghgFilter = new FileNameExtensionFilter(".GHG files", "ghg", "GHG");
            
            fileChooser.setFileFilter(ghgFilter);
            fileChooser.setDialogTitle("Select the .GHG file (this should be the LR_PC.GHG file) of the character you want to give your new texture.");
            
            int returnVal = fileChooser.showOpenDialog(window);
            String charName = "";
            File ghgFile = null;
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {  
                ghgFile = fileChooser.getSelectedFile();  
                charName = ghgFile.getName().substring(0, ghgFile.getName().length() - 4);
            } else {
                window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
            }
            
            FileNameExtensionFilter ddsFilter = new FileNameExtensionFilter(".dds files", "dds");
            fileChooser.setFileFilter(ddsFilter);
            fileChooser.setDialogTitle("Select your new texture .dds file.");
            
            returnVal = fileChooser.showOpenDialog(window);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String[] writeButtons = { "Continue", "Stop"};    
                int writeOptionVal = JOptionPane.showOptionDialog(window, "Only continue if you are sure your modified .dds file "
                        + "is the same size as the original texture and you exported it with DXT1 compression.",
                        "Warning", 0, JOptionPane.NO_OPTION, 
                        null, writeButtons, writeButtons[0]);
                
                if (writeOptionVal == 1) {
                    window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
                } 
                    
                File ddsFile = fileChooser.getSelectedFile();
                
                int targetTextureNumber = Integer.parseInt(JOptionPane.showInputDialog("Enter the texture number. i.e. 0, 1, 2, ..."));
                
                Writer writer = new Writer(charName, ghgFile, ddsFile, targetTextureNumber);
                
                writer.write();
                
            }
            
            
        } else {
            window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
        }

    }

}
