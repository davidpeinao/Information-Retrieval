package ritika;

import java.io.File;
import java.io.IOException;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;


public class RITika {

    public static void main(String[] args) throws IOException, TikaException {
        Tika tika = new Tika();
        for (String file : args){
            File f = new File(file);
            
            String type = tika.detect(f);
            System.out.println(file + " : " + type);
            String text = tika.parseToString(f);
            System.out.print(text);
        }
    }
    
}
