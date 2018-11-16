package irs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.text.ParseException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;


public class Searcher {
    
    //String indexPath = "E:\\Users\\Usuario\\Documents\\UGR\\4º\\RI\\P3\\src\\irs\\index";
    String indexPath = "C:\\Users\\David\\Documents\\UGR\\4º\\RI\\P3\\src\\irs\\index";
    
    public void indexSearch(Analyzer analyzer, Similarity similarity) throws IOException{
        IndexReader reader = null;
        try{
            reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
            IndexSearcher searcher = new IndexSearcher(reader);

            searcher.setSimilarity(similarity);
            BufferedReader in = null;
            in = new BufferedReader(new InputStreamReader(System.in));

            QueryParser parser = new QueryParser("body", analyzer);

            while(true){
                System.out.print("Introduzca consulta: ");
                String line = in.readLine();

                if(line == null || line.length() == -1)
                    break;

                line = line.trim();
                if(line.length() == 0)
                    break;

                Query query;
                try{
                    query = parser.parse(line);
                } catch ( org.apache.lucene.queryparser.classic.ParseException e){
                    System.out.println("Error en cadena consulta.");
                    continue;
                }

                TopDocs results = searcher.search(query, 10);
                ScoreDoc[] hits = results.scoreDocs;

                int numTotalHits = (int) results.totalHits;
                System.out.println(numTotalHits + " documentos encontrados");

                for(int j=0; j< hits.length; j++){
                    Document doc = searcher.doc(hits[j].doc);
                    String body = doc.get("body");
                    System.out.println(doc.getFields());
                    Integer id = Integer.parseInt(doc.getField("id").stringValue());
                    System.out.println("--------------------------------");
                    System.out.println("ID: " + id);
                    System.out.println("Cuerpo: " + body + "\n");
                }

                if (line.equals(""))
                    break;  
            }
        reader.close();
        } catch(IOException e){
            try{
                reader.close();
            } catch (IOException except){
                except.printStackTrace();
            }
            e.printStackTrace();
        }
    }
    
    
    
    public static void main(String[] args) throws IOException {
        Searcher searcher = new Searcher();
        
        Analyzer analyzer = new StandardAnalyzer();
        Similarity similarity = new ClassicSimilarity();
        searcher.indexSearch(analyzer, similarity);
    
    }
}
