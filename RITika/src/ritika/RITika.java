package ritika;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;

import org.apache.tika.*;
import org.apache.tika.exception.TikaException;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.xml.sax.SAXException;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;

public class RITika {
    private Tika tika = new Tika(); // Tika object so we can use and apply their functions to our documents 
    private File directory; // The directory which contains our documents
    private String directory_string; // Path of the directory
    private ArrayList<File> files_array = new ArrayList<>(); // Array of the documents to be processed
    private String[] files; // The list of our documents
  
    // Tika functions results will be stored in the next list of variables
    private Parser parser = new AutoDetectParser(); 	
    private BodyContentHandler handler = new BodyContentHandler(-1); // Disables string limit  
    private Metadata metadata = new Metadata();
    private FileInputStream content;
    private String document_type = "";
    LanguageIdentifier lang;

    // Variables for link extraction
    private LinkContentHandler link_handler = new LinkContentHandler();
    private Parser parser_links = new AutoDetectParser();
    private BodyContentHandler text_handler = new BodyContentHandler(-1); // Disables string limit 
    private ToHTMLContentHandler toHTML = new ToHTMLContentHandler();
    TeeContentHandler teeHandle;
    ParseContext parseContext = new ParseContext();

    // Word frecuency containers
    // This will be used to have the tokens ordered by their frecuency
    private HashMap<String, Integer> frecuencies = new HashMap<>();
    private ArrayList<Pair<Integer, String>> frecuencies_ordered = new ArrayList();
    
    private File f; // File which represents a document output, of our search documents
    private String file_in_string;// Stores the file's text in String format 
    private Scanner fileScanner; // Used for scanning over the file's text

    
    
//
// FUNCTIONS
//
    // Stores directory's files 
    private void store_files_paths() {
        this.files = (this.directory).list();
        System.out.println("Files in the directory:");

        for(String file_aux : files) {
            File aux = new File(directory_string+"\\"+file_aux); // Assigns absolute path 
            this.files_array.add(aux); // Makes an array with all the files in the dir
            System.out.println(aux); // Prints all the files of the dir
        }
    }

    // Applies tika functions to documents
    private void generate_tika_functions(File file) throws IOException, SAXException, TikaException {
        content = new FileInputStream(file);
        document_type = tika.detect(file);
        parser = new AutoDetectParser(); 
        handler = new BodyContentHandler(-1); // Disables string limit 
        metadata = new Metadata();

        // Parsing the given document
        parser.parse(content, handler, metadata, new ParseContext());
        lang = new LanguageIdentifier(handler.toString());
    }

    // Extracts all the links from document and prints them
    private void extract_links(File file) throws IOException, SAXException, TikaException {
        content = new FileInputStream(file);
        teeHandle = new TeeContentHandler(link_handler, text_handler, toHTML);
        parser_links.parse(content, teeHandle, metadata, parseContext);
        System.out.println("\nList of links:\n" + link_handler.getLinks()+"\n");
        // Removes links in container so it is empty for new file
        link_handler = new LinkContentHandler();
    }

    // Shows the metadata of the file
    private void get_metadata(File file) {
        System.out.println("Metadata of the file: " + file.getName());
        System.out.println("Title: "+ metadata.get("title"));
        System.out.println("Type: "+ document_type);
        System.out.println("Codification: "+ metadata.get("Content-Type"));
        System.out.println("Language: "+ lang.getLanguage() + "\n");
        System.out.println("----------------------------------------------");
    }

    // Order token frecuencies decreasingly
    private void order_token_frecuencies() {
        // Changes order Key-> Value to Value -> Key in a new arraylist 
        for (Entry<String, Integer> entry : frecuencies.entrySet()) {
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
        out = new File("results_" + f.getName() + ".csv");
        
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


    // Processes the document
    private void tokenize_file() throws IOException, TikaException, SAXException{
        String temp=""; // Will store tokens
        Integer default_value = 0; // Will be used as counter

        for(File f : files_array) {
            if (f.isFile()){
            System.out.println("Can read = " +f.canRead());
            this.extract_links(f);
            this.generate_tika_functions(f);
            this.get_metadata(f);

            file_in_string = tika.parseToString(f);// Text of the file in String 
            fileScanner = new Scanner(file_in_string);// Stores in scanner to tokenize it
            fileScanner.useDelimiter(" ");// Assings the delimiter, in this case we will use blank space, as told by our teacher
            frecuencies = new HashMap<>();

            while(fileScanner.hasNext()) { // Iterates over the tokens extracted by the scanner 
                temp = fileScanner.next();
                default_value = frecuencies.getOrDefault(temp, 0);
                if(default_value == 0) {
                    frecuencies.put(temp, 1);   
                } else {
                    frecuencies.put(temp, (default_value)+1);
                }
            }
            this.order_token_frecuencies();
            this.save_results_to_file(f);
            frecuencies_ordered = new ArrayList<>(); // Empties it to the next document
            }
            else {
                System.out.println("Error\n");
            }
        }
    } 
    
    // Does all
    public void run(String directory_path) throws IOException, TikaException, SAXException {
            this.directory_string = directory_path;
            this.directory = new File(directory_path);
            System.out.println("\nDirectory used: " + directory_string + "\n");

            this.store_files_paths();
            this.tokenize_file();
    }

    public static void main(String[] args) throws Exception, IOException{
        Tika tika = new Tika();
        RITika example = new RITika();
        // Should be changed to the appropiate directory, may be changed in the past to make it an input
        example.run("E:\\Users\\Usuario\\Documents\\UGR\\4º\\RI\\P1\\src\\ritika\\files");
    }
}