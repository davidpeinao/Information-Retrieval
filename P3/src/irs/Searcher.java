package irs;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.LabelAndValue;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;


public class Searcher {
    
    //String indexPath = "E:\\Users\\Usuario\\Documents\\UGR\\4º\\RI\\P3\\src\\irs\\index";
    String indexPath = "C:\\Users\\David\\Documents\\UGR\\4º\\RI\\P3\\src\\irs\\index";
    String facetsPath = "C:\\Users\\David\\Documents\\UGR\\4º\\RI\\P3\\src\\irs\\facets";
    //String facetsPath = "E:\\Users\\Usuario\\Documents\\UGR\\4º\\RI\\P3\\src\\irs\\facets";
    
    public String docEncontrados = "";
    
    public String getDocEncontrados(){
        return docEncontrados;
    }
    
    public ArrayList<String> indexSearch(Map<String, Analyzer> analyzerPerField, Similarity similarity, String line, 
           String campo, String line2, String campo2, String operation, String mostrarNDocs) throws IOException, ParseException{
        
        IndexReader reader = null;
        
        reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(similarity);
        
        FSDirectory taxoDir = FSDirectory.open(Paths.get(facetsPath));
        TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDir);
        FacetsConfig fconfig = new FacetsConfig();
        
        QueryParser parser = new QueryParser(campo, analyzerPerField.get(campo));
        QueryParser parser2 = new QueryParser(campo2, analyzerPerField.get(campo2));

        Query query = null, query2 = null;
        query = parser.parse(line);
        
        BooleanClause bc1 = null, bc2 = null;
        bc1 = new BooleanClause(query, BooleanClause.Occur.MUST);
        
        if (!line2.equals("")){
            query2 = parser2.parse(line2);
            if(operation.equals("AND")){
                bc2 = new BooleanClause(query2, BooleanClause.Occur.MUST);
            }

            if(operation.equals("OR")){
                bc2 = new BooleanClause(query2, BooleanClause.Occur.SHOULD);
            }

            if(operation.equals("NOT")){
                bc2 = new BooleanClause(query2, BooleanClause.Occur.MUST_NOT);
            }
        }
        BooleanQuery.Builder bqbuilder = new BooleanQuery.Builder();
        
        bqbuilder.add(bc1);
        if (bc2 != null)
            bqbuilder.add(bc2);
        
        BooleanQuery bq = bqbuilder.build();
        
        int nDocs = Integer.parseInt(mostrarNDocs);
        FacetsCollector fc = new FacetsCollector();
        
//        TopDocs a= searcher.search(bq, 10);
//        for (int i=0;i<10;i++)
//            System.out.println(a.totalHits+ " "+a.scoreDocs[i].score);
        TopDocs results = FacetsCollector.search(searcher,bq, nDocs, fc);
        ScoreDoc[] hits = results.scoreDocs;
        System.out.println(results.totalHits);
        Facets facetas = new FastTaxonomyFacetCounts(taxoReader, fconfig, fc);
        
        
        //
        String resultadoFacetas = "";
        List<FacetResult> allDims = facetas.getAllDims(50);
        resultadoFacetas += ("Categorias totales: " + allDims.size() + "\n");
        for(FacetResult fr : allDims){
            resultadoFacetas += ("Categoría: " + fr.dim + "\n");
            for(LabelAndValue lv : fr.labelValues){
                resultadoFacetas += ("     Etiqueta: " + lv.label + ",  ocurrencias --> " + lv.value + "\n");
            }
        }
        //
        
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
        }
        reader.close(); 
        
        ArrayList<String> res = new ArrayList();
        res.add(resultadoFacetas);
        res.add(resultado);
        
        return res;
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