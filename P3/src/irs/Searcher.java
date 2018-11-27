package irs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;


public class Searcher {
    
    String indexPath = "E:\\Users\\Usuario\\Documents\\UGR\\4º\\RI\\P3\\src\\irs\\index";
    //String indexPath = "C:\\Users\\David\\Documents\\UGR\\4º\\RI\\P3\\src\\irs\\index";
    
    public String docEncontrados = "";
    
    public String getDocEncontrados(){
        return docEncontrados;
    }
    
    public String indexSearch(Map<String, Analyzer> analyzerPerField, Similarity similarity, String line, String campo, String line2, String campo2, String operation) throws IOException{
        IndexReader reader = null;
        
        
        reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(similarity);
        
        //QueryParser parser = new QueryParser(campo, analyzerPerField.get(campo));
        // QUE ANALYZER SE LES HA PASADO A LOS CAMPOS INTRODUCIDOS PARA LA BUSQUEDA?
        Query query = null, query2 = null;
        query = new TermQuery(new Term(campo, line));
        query2 = new TermQuery(new Term(campo2, line2));
        BooleanClause bc1 = null, bc2 = null;
        
        if(operation.equals("AND")){
            bc1 = new BooleanClause(query, BooleanClause.Occur.MUST);
            bc2 = new BooleanClause(query2, BooleanClause.Occur.MUST);
        }
        
        if(operation.equals("OR")){
            bc1 = new BooleanClause(query, BooleanClause.Occur.MUST);
            bc2 = new BooleanClause(query2, BooleanClause.Occur.SHOULD);
        }
        
        if(operation.equals("NOT")){
            bc1 = new BooleanClause(query, BooleanClause.Occur.MUST);
            bc2 = new BooleanClause(query2, BooleanClause.Occur.MUST_NOT);
        }
        
        BooleanQuery.Builder bqbuilder = new BooleanQuery.Builder();
        
        bqbuilder.add(bc1);
        bqbuilder.add(bc2);
        
        BooleanQuery bq = bqbuilder.build();
        
        TopDocs results = searcher.search(bq, 10);
        ScoreDoc[] hits = results.scoreDocs;

        String encontrados = "", resultado = "";
        int numTotalHits = (int) results.totalHits;
        encontrados = (numTotalHits + "\n");
        docEncontrados = encontrados;

        for(int j=0; j< hits.length; j++){
            Document doc = searcher.doc(hits[j].doc);
            String body = doc.get("body");
            Integer id = Integer.parseInt(doc.getField("id").stringValue());
            resultado += ("--------------------------------\n");
            resultado += ("ID: " + id + "\n");
            resultado += ("Cuerpo: " + body + "\n");
            resultado += (doc.getFields() + "\n");

            //System.out.println(resultado);
        }
        reader.close(); 
        return resultado;
        }
    
    
    
    
    public static void main(String[] args) throws IOException {
//        Searcher searcher = new Searcher();
//        
//        Analyzer analyzer = new StandardAnalyzer();
//        Similarity similarity = new ClassicSimilarity();
//        String line = "";
//        line = searcher.indexSearch(analyzer, similarity, line);
    
    }
}
