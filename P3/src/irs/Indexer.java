
package irs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class Indexer {


    static HashMap<String, Analyzer> analyzers = new HashMap<>();
    static PerFieldAnalyzerWrapper analyzer = null;

    
    private void addFile(File file) throws IOException, FileNotFoundException, ParseException {
        if (!file.exists()) {
            System.out.println("file: " + file + " doesn't exist");
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                addFile(f);
            }
        } else {
            
            int i = 0;
            
            
            String cadena;
            Boolean categories = true;
            FileReader fReader = new FileReader(file.toString());
            BufferedReader b = new BufferedReader(fReader);
            
            // hace esto solo con 4 lineas para probar, cuando todo este bien se quita
            while((cadena = b.readLine())!=null && i < 4) {
                if(categories == false)
                    this.indexDoc(cadena, file);
                else
                    categories = false;
            }
        }
    }
     
    private void indexDoc(String cadena, File file) throws FileNotFoundException, ParseException{
        Document doc = new Document();
        
        ArrayList<String> fields = this.getFields(cadena);
        System.out.println(fields.toString());
        
        if (file.getName() == "Answers.csv"){
            doc.add(new IntPoint("id", Integer.parseInt(fields.get(0))));
            doc.add(new StoredField("id", fields.get(0)));
            doc.add(new IntPoint("owneruserid", Integer.parseInt(fields.get(1))));
            doc.add(new StoredField("owneruserid", fields.get(1)));

            Date date = new SimpleDateFormat("yyyy−MM−dd 'T' HH:mm:ss 'Z'" ).parse(fields.get(2));
            doc.add (new LongPoint("date", date.getTime()));
            doc.add(new StoredField("date", fields.get(2)));

            doc.add(new IntPoint("parentid", Integer.parseInt(fields.get(3))));
            doc.add(new StoredField("parentid", fields.get(3)));
            doc.add(new IntPoint("score", Integer.parseInt(fields.get(4))));
            doc.add(new StoredField("score", fields.get(4)));

            doc.add(new StringField("isacceptedanswer", fields.get(5), Field.Store.YES));
            doc.add(new TextField("body", fields.get(6) , Field.Store.NO));
        }
        if (file.getName() == "Questions.csv"){
            doc.add(new IntPoint("id", Integer.parseInt(fields.get(0))));
            doc.add(new StoredField("id", fields.get(0)));
            doc.add(new IntPoint("owneruserid", Integer.parseInt(fields.get(1))));
            doc.add(new StoredField("owneruserid", fields.get(1)));

            Date date = new SimpleDateFormat("yyyy−MM−dd 'T' HH:mm:ss 'Z'" ).parse(fields.get(2));
            doc.add (new LongPoint("date", date.getTime()));
            doc.add(new StoredField("date", fields.get(2)));
            
            doc.add(new IntPoint("score", Integer.parseInt(fields.get(4))));
            doc.add(new StoredField("score", fields.get(4)));
            
            doc.add(new TextField("title", fields.get(6) , Field.Store.NO));
            doc.add(new TextField("body", fields.get(6) , Field.Store.NO));          
        }
        if (file.getName() == "Tags.csv"){
            doc.add(new IntPoint("id", Integer.parseInt(fields.get(0))));
            doc.add(new StoredField("id", fields.get(0)));
            doc.add(new TextField("tags", fields.get(6) , Field.Store.NO));
        }
    }
    
    private ArrayList getFields(String cadena) {
        ArrayList<String> fields = new ArrayList<>();
        int i=0, j=0;
        while(i < cadena.length()){
                        
                if(String.valueOf(cadena.charAt(i+1)).equals(",")) {
                    fields.add(cadena.substring(j, i));
                    i += 2;
                }else{
                if(fields.size() != 6) {
                    j = i;
                    while (!String.valueOf(cadena.charAt(i)).equals(",")) {
                        i++;
                    }
                    fields.add(cadena.substring(j, i));
                    i++;
                }
                else{ //This is for the last field
                    j=i;
                    while(i<cadena.length()){
                        i++;
                    }
                    fields.add(cadena.substring(j, i));
                    i++;
                }

            }
        }

        return fields;

    }
    
    
    public static void main(String[] args) throws IOException, FileNotFoundException, ParseException {
        Indexer instance = new Indexer();
        File f = new File("E:\\Users\\Usuario\\Documents\\UGR\\4º\\RI\\P3\\src\\irs\\rquestions");
        instance.addFile(f);
    }

    
    
}
