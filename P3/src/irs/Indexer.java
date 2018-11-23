
package irs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class Indexer {

    private IndexWriter writer;
    boolean create = true;
    
    public void configurarIndice(PerFieldAnalyzerWrapper analyzer, Similarity similarity) throws IOException{
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setSimilarity(similarity);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        //FSDirectory dir = FSDirectory.open(Paths.get("E:\\Users\\Usuario\\Documents\\UGR\\4º\\RI\\P3\\src\\irs\\index"));
        FSDirectory dir = FSDirectory.open(Paths.get("C:\\Users\\David\\Documents\\UGR\\4º\\RI\\P3\\src\\irs\\index"));
        writer = new IndexWriter(dir, config);   
    }
    
    public void closeIndex() throws IOException{
        writer.commit();
        writer.close();
    }
    
    private void addFile(File file) throws IOException, FileNotFoundException, ParseException {
        if (!file.exists()) {
            System.out.println("file: " + file + " doesn't exist");
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                addFile(f);
                System.out.println("file: " + f);
            }
        } else {
            String cadena, answer = "";
            Boolean categories = true;
            FileReader fReader = new FileReader(file.toString());
            BufferedReader b = new BufferedReader(fReader);
            
            int i=0;
            while((cadena = b.readLine())!=null && i<10000) {
                i++;
                if (!"\"".equals(cadena) && categories == false){
                    answer += cadena;
                    continue;
                }
                
                if(categories == false){
                    System.out.println(answer);
                    this.indexDoc(answer, file);
                    answer = "";
                }else{
                    categories = false;
                    answer = "";
                }
            }
        }        
    }
     
    public void indexDoc(String cadena, File file) throws FileNotFoundException, ParseException, IOException{
        Document doc = new Document();

        if ("Answers.csv".equals(file.getName())){
            ArrayList<String> fields = this.getFields(cadena, 6);
            //System.out.println(fields);

            doc.add(new IntPoint("id", Integer.parseInt(fields.get(0))));
            doc.add(new StoredField("id", fields.get(0)));
            if (fields.get(1).contains("NA")){
                doc.add(new IntPoint("owneruserid", 0));
                doc.add(new StoredField("owneruserid", 0));
            }else{                
            doc.add(new IntPoint("owneruserid", Integer.parseInt(fields.get(1))));         
            doc.add(new StoredField("owneruserid", fields.get(1)));
            }
            
            try{
                Date date = new SimpleDateFormat("yyyy−MM−dd 'T' HH:mm:ss 'Z'" ).parse(fields.get(2));
                doc.add (new LongPoint("date", date.getTime()));
                doc.add(new StoredField("date", fields.get(2)));
            } catch(ParseException e){
                Date date = new Date(0);
                doc.add (new LongPoint("date", date.getTime()));
                
                doc.add(new StoredField("date", fields.get(2)));
            }         

            doc.add(new IntPoint("parentid", Integer.parseInt(fields.get(3))));
            doc.add(new StoredField("parentid", fields.get(3)));
            doc.add(new IntPoint("score", Integer.parseInt(fields.get(4))));
            doc.add(new StoredField("score", fields.get(4)));

            doc.add(new StringField("isacceptedanswer", fields.get(5), Field.Store.YES));
            
            String respuesta = fields.get(6);
            org.jsoup.nodes.Document jsoup = Jsoup.parse(respuesta);
            doc.add(new TextField("body", fields.get(6) , Field.Store.YES));
            //doc.add(new TextField("body", jsoup.body().text() , Field.Store.YES));
            
            String codeString = "";
            for (Element e : jsoup.getAllElements()){
                if (e.tagName().equals("code"))
                    codeString += e.text();
            }
           doc.add(new TextField("code", codeString , Field.Store.YES));

            writer.addDocument(doc);
            
        }
        
        if ("Questions.csv".equals(file.getName())){
            ArrayList<String> fields = this.getFields(cadena, 5);
            
            //doc.add(new StringField("id",fields.get(0),Field.Store.YES));
            doc.add(new IntPoint("id", Integer.parseInt(fields.get(0))));
            doc.add(new StoredField("id", fields.get(0)));
            
            if (fields.get(1).contains("NA")){
                doc.add(new IntPoint("owneruserid", 0));
                doc.add(new StoredField("owneruserid", 0));
            }else{ 
            doc.add(new IntPoint("owneruserid", Integer.parseInt(fields.get(1))));         
            doc.add(new StoredField("owneruserid", fields.get(1)));
            }

            try{
                Date date = new SimpleDateFormat("yyyy−MM−dd 'T' HH:mm:ss 'Z'" ).parse(fields.get(2));
                doc.add (new LongPoint("date", date.getTime()));
                doc.add(new StoredField("date", fields.get(2)));
            } catch(ParseException e){
                Date date = new Date(0);
                doc.add (new LongPoint("date", date.getTime()));
                doc.add(new StoredField("date", fields.get(2)));
            }
            
            doc.add(new IntPoint("score", Integer.parseInt(fields.get(3))));
            doc.add(new StoredField("score", fields.get(3)));
            
            doc.add(new TextField("title", fields.get(4) , Field.Store.YES));
            
            String respuesta = fields.get(5);
            org.jsoup.nodes.Document jsoup = Jsoup.parse(respuesta);
            //doc.add(new TextField("body", fields.get(5) , Field.Store.YES));
            doc.add(new TextField("body", jsoup.body().text() , Field.Store.YES));   
            
            writer.addDocument(doc);
        }
        
        if ("Tags.csv".equals(file.getName())){
            ArrayList<String> fields = this.getFields(cadena, 1);
            
            doc.add(new IntPoint("id", Integer.parseInt(fields.get(0))));
            doc.add(new StoredField("id", fields.get(0)));
            doc.add(new TextField("tags", fields.get(1) , Field.Store.YES));
            
            writer.addDocument(doc);
        }
         
    }
    
    public ArrayList getFields(String cadena, int nfields) {
        ArrayList<String> fields = new ArrayList<>();
        int i=0, j=0;
        
        while(i < cadena.length()){                     
            if(String.valueOf(cadena.charAt(i)).equals(",")) {
                fields.add(cadena.substring(j, i));
                i += 2;                  
            }else{
                if(fields.size() != nfields) {
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
        
        Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
        analyzerPerField.put("id", new WhitespaceAnalyzer());
        analyzerPerField.put("owneruserid", new WhitespaceAnalyzer());
        analyzerPerField.put("parentid", new WhitespaceAnalyzer());
        analyzerPerField.put("date", new WhitespaceAnalyzer());
        analyzerPerField.put("score", new WhitespaceAnalyzer());
        analyzerPerField.put("isacceptedanswer", new WhitespaceAnalyzer());
        analyzerPerField.put("tags", new WhitespaceAnalyzer());
        analyzerPerField.put("body", new EnglishAnalyzer());
        analyzerPerField.put("code", new WhitespaceAnalyzer());
        PerFieldAnalyzerWrapper analyzer  = new PerFieldAnalyzerWrapper(new WhitespaceAnalyzer() , analyzerPerField);
        //BM25
        Similarity similarity = new ClassicSimilarity();
        Indexer indice = new Indexer();
        
        indice.configurarIndice(analyzer, similarity);
  
        //File f = new File("E:\\Users\\Usuario\\Documents\\UGR\\4º\\RI\\P3\\src\\irs\\rquestions");
        File f = new File("C:\\Users\\David\\Documents\\UGR\\4º\\RI\\P3\\src\\irs\\rquestions");
        indice.addFile(f);
        
        indice.closeIndex();
    }

}