package rianalyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.apache.tika.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class RIAnalyzer {

    private Tika tika = new Tika(); // Tika object used to parse the document into a String
    private File directory; // The directory which contains our documents
    private String directory_string; // Path of the directory
    private ArrayList<File> files_array = new ArrayList<>(); // Array of the documents to be processed
    private String[] files; // The list of our documents
    
    // Word frecuency containers
    // This will be used to have the tokens ordered by their frecuency
    private HashMap<String, Integer> frecuencies = new HashMap<>();
    private ArrayList<Pair<Integer, String>> frecuencies_ordered = new ArrayList();
    
    
    
    
    private void store_files_paths() {
        this.files = (this.directory).list();
        System.out.println("Files in the directory:\n");

        for(String file_aux : files) {
            File aux = new File(directory_string+"\\"+file_aux); // Assigns absolute path 
            this.files_array.add(aux); // Makes an array with all the files in the dir
            System.out.println(file_aux); // Prints all the files of the dir
        }
    }
    
    
    
    
    
    // Order token frecuencies decreasingly
    private void order_token_frecuencies() {
        // Changes order Key-> Value to Value -> Key in a new arraylist 
        for (Map.Entry<String, Integer> entry : frecuencies.entrySet()) {
                frecuencies_ordered.add(new Pair(entry.getValue(), entry.getKey()));

        }
        // Prints the number of tokens in the document
        System.out.println("Number of tokens of the document: " + frecuencies_ordered.size());

        //Override the method compare to compare the first value of our Pair
        Collections.sort(frecuencies_ordered, new Comparator<Pair>() {
            @Override public int compare(Pair x, Pair y) {
                return (Integer) y.getL() - (Integer) x.getL() ; // Descending order
            }
        });       
        // Prints the list
        for (int i = 0; i <= frecuencies_ordered.size()-1; i++) {
            System.out.println("n: " + frecuencies_ordered.get(i).getL() + "   Token: " + frecuencies_ordered.get(i).getR());
        }       
    }
    
    
    
    
    
    // Saves each document to result file
    private void save_results_to_file(File f) throws FileNotFoundException, IOException {
        File out;          
        out = new File("results2_" + f.getName() + ".csv");
        
        FileWriter fw = new FileWriter(out);
        BufferedWriter buff_writer = new BufferedWriter(fw);
        // Writes the rank of a token and the times it appeared in the document
        // Counter starts at 1 and gets paired with the first item of the arraylist
        for(int i=1; i<frecuencies_ordered.size(); i++){
            buff_writer.write(i  + "," + frecuencies_ordered.get(i-1).getL() ); // Writes rank and occurences
            buff_writer.newLine();
        }
        buff_writer.close();
    }
    
    
    
    
    
    private void analyze(File f) throws IOException, TikaException{
        String temp=""; // Will store tokens
        Integer default_value = 0; // Will be used as counter
        frecuencies = new HashMap<>();
        
//            new WhitespaceAnalyzer(),
//            new SimpleAnalyzer(),
//            new StopAnalyzer(),
//            new StandardAnalyzer( ),
//            new SpanishAnalyzer(),
//            new UAX29URLEmailAnalyzer(),
//            new AnalyzerNuevo()
        Analyzer an = new SpanishAnalyzer();
        System.out.println("\nAnalyzing: " + f.getName() + "\n");
        String cadena = tika.parseToString(f);
        TokenStream stream  = an.tokenStream(null,  cadena);

        //ShingleFilter sf = new ShingleFilter(stream,2,2);

        stream.reset();
        while (stream.incrementToken()) {
            
            temp = stream.getAttribute(CharTermAttribute.class).toString();
            default_value = frecuencies.getOrDefault(temp, 0);
            if(default_value == 0) {
                frecuencies.put(temp, 1);   
            } else {
                frecuencies.put(temp, (default_value)+1);
            }
            //cad = stream.getAttribute(CharTermAttribute.class).toString();
            //System.out.println(stream.getAttribute(CharTermAttribute.class));
        }
        this.order_token_frecuencies();
        this.save_results_to_file(f);
        frecuencies_ordered = new ArrayList<>(); // Empties it to the next document
        stream.end();
        stream.close();
    }
    
    
    
    
    
    public void run(String directory_path) throws IOException, TikaException, SAXException {
            this.directory_string = directory_path;
            this.directory = new File(directory_path);
            System.out.println("\nDirectory used: " + directory_string + "\n");

            this.store_files_paths();
            // Analyzes all the files in the directory
            for(File f : files_array) {
                this.analyze(f); 
            }
    }
    
    
    
    public static void main(String[] args) throws IOException, TikaException, SAXException {
        RIAnalyzer example = new RIAnalyzer();
        // Should be changed to the appropiate directory, may be changed in the future to make it an input
        example.run("E:\\Users\\Usuario\\Documents\\UGR\\4º\\RI\\RIAnalyzer\\src\\rianalyzer\\files");
        
    }
    
}