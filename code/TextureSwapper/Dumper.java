import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class Dumper {

    protected static enum status { GOOD, BAD }
    private String character;
    private File ghgFile;
    private byte[] rawData;
    private Logger log;
    
    private final byte[] DDS_HEADER = new byte[] {68, 68, 83, 32, 124};
    private LinkedList<Byte> currentBuffer;
    private LinkedList<Integer> DDSHeaderIndices;
    
    public Dumper(String character, File ghgFile) {
        
        this.character = character;
        this.ghgFile = ghgFile;
        
        currentBuffer = new LinkedList<Byte>();
        DDSHeaderIndices = new LinkedList<Integer>();
        log = new Logger("Texture Dump");
        
    }
    
    public OutputStatus parse() {
        
        try {  
            
            rawData = Files.readAllBytes(ghgFile.toPath());
            
            log.write("starting parse");
            
            for (int i = 0; i < rawData.length; i++) {
                
                if (i % 16 == 0) {
                    log.write("parsing row address: " + Integer.toHexString(i)); 
                }
                
                currentBuffer.add(rawData[i]);
                if (currentBuffer.size() > 5) {
                    currentBuffer.removeFirst();
                }
                
                if (headerFound()) {
                    DDSHeaderIndices.add(i - 4);
                    log.write("DDS header found at " + i);
                }
                
            }
            
            DDSHeaderIndices.add(rawData.length - 1);
            
            //System.out.println(DDSHeaderIndices);
            log.write("parse good");
            return OutputStatus.GOOD;
        
        } catch (Exception e) {
            
            log.write("parse failed with error: " + e);
            return OutputStatus.BAD;
        }
        
    }
    
    private boolean headerFound() {
        
        for (int i = 0; i < DDS_HEADER.length; i++) {
            if (DDS_HEADER[i] != currentBuffer.get(i)) {
                return false;
            }
        }
        return true;
        
    }
    
    public void dump() {
        
        log.write("dumping textures...");
        
        String directoryName = character + "_DUMP";
        File charDirectory = new File(directoryName);
        if (!charDirectory.exists()) {
            charDirectory.mkdir();
        }
        
        log.write("made directory at: " + charDirectory.getAbsolutePath());
        
        for (int i = 0; i < DDSHeaderIndices.size() - 1; i++) {
            
            try { 
                
                String filename = directoryName + "/" + character + "_TEXTURE_" + i + ".dds";
                
                File DDSFile = new File(filename);
                log.write("made file: " + filename);
                
                OutputStream os = new FileOutputStream(DDSFile); 
      
                int start = DDSHeaderIndices.get(i);
                
                log.write("writing texture to file...");
                for (int j = 0; j < DDSHeaderIndices.get(i + 1) - start; j++) {
                    os.write(rawData[start + j]);
                }
                os.close(); 
                log.write("success!");
                
            } 
      
            catch (Exception e) { 
                log.write("dump failed with error: " + e);
            }
            
        }
        
        log.write("<<<");
        log.write("dump succeeded!");
        log.write("textures are located at: " + charDirectory.getAbsolutePath());
        log.write("Edit the textures using GIMP, Photoshop, or any other image editing software with DDS compatiblity.");
        log.write(">>>");
        
    }
      
}
