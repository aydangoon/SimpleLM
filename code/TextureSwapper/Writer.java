import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.LinkedList;

public class Writer {

    private Logger log;
    
    private int targetTextureNumber;
    private int currentTextureNumber;
    
    private String character;
    private File ghgFile;
    private File ddsFile;
    private File ghgOutputFile;
    
    private final byte[] DDS_HEADER = new byte[] {68, 68, 83, 32, 124};
    private LinkedList<Byte> currentBuffer;
    
    public Writer(String character, File ghgFile, File ddsFile, int targetTextureNumber) {
        
        this.character = character;
        this.ghgFile = ghgFile;
        this.ddsFile = ddsFile;
        this.targetTextureNumber = targetTextureNumber;
        currentTextureNumber = -1;
        currentBuffer = new LinkedList<Byte>();
        log = new Logger("Texture Write");
        
    }
    
    public OutputStatus write() {
        
        try {  
            
            byte[] ghgRaw = Files.readAllBytes(ghgFile.toPath());
            
            byte[] ddsRaw = Files.readAllBytes(ddsFile.toPath());
            
            int start = -1;
            
            
            String directoryName = character + "_DUMP";
            File charDirectory = new File(directoryName);
            if (!charDirectory.exists()) {
                charDirectory.mkdir();
            }
            ghgOutputFile = new File(directoryName + "/" + character + "_EDITTED.GHG");
            
            log.write("created output ghg file in: " + charDirectory.getAbsolutePath());
            log.write("searching for texture address...");
            
            for (int i = 0; i < ghgRaw.length; i++) {
                
                currentBuffer.add(ghgRaw[i]);
                if (currentBuffer.size() > 5) {
                    currentBuffer.removeFirst();
                }
                
                currentTextureNumber += headerFound() ? 1 : 0;
                if (currentTextureNumber == targetTextureNumber) {
                    start = i - 4;
                    log.write("texture address found!");
                    break;
                }         
            }
            
            log.write("starting new texture write");
            
            for (int i = 0; i < ddsRaw.length; i++) {
                ghgRaw[start + i] = ddsRaw[i];
                log.write("writing byte " + Integer.toHexString(ddsRaw[i]));
            }
            
            OutputStream os = new FileOutputStream(ghgOutputFile);
            os.write(ghgRaw);
            os.close();   
            
            log.write("texture write successful!");
            log.write("<<<");
            log.write("View your new .ghg file at: " + charDirectory.getAbsolutePath());
            log.write("To add the texture in-game, swap the current character LR_PC.GHG file with the editted file. \n" + 
            " Make sure the editted file has the name [character name]_LR_PC.GHG when you put it into the LSWTCS game files.");
            log.write(">>>");
            
            return OutputStatus.GOOD;
        
        } catch (Exception e) {
            
            log.write("write failed with error: " + e);
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
    
}
