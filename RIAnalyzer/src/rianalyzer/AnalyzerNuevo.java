package rianalyzer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.apache.lucene.analysis.Analyzer; 
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.util.CharsRef;




public class AnalyzerNuevo extends Analyzer { 
    
  /** Tokens longer than this length are discarded. Defaults to 50 chars. */
    public int maxTokenLength = 50;
    private static final List<String> stopwords = Arrays.asList("abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default",
"do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import",
"instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short",
"static", "strictfp", "super",	 "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while" ); 
    

    
    public AnalyzerNuevo() { 
      
    }
  
    @Override
    protected TokenStreamComponents createComponents(String string)   {
        
            //To change body of generated methods, choose Tools | Templates.
            final Tokenizer source = new StandardTokenizer();
            
            SynonymMap.Builder builder = new SynonymMap.Builder(true);
            builder.add(new CharsRef("text"), new CharsRef("documento"), true);
            
            SynonymMap synonymMap;
        
        
            TokenStream pipeline = source;
            //pipeline = new StandardFilter(pipeline);
          
            //pipeline = new EnglishPossessiveFilter(pipeline);
            try {
            synonymMap = builder.build();
            pipeline = new SynonymFilter(pipeline,synonymMap,true);
            } catch (IOException ex) {
               Logger.getLogger(AnalyzerNuevo.class.getName()).log(Level.SEVERE, null, ex);
            }
            
         
        
            
            
            //pipeline = new ASCIIFoldingFilter(pipeline);
            pipeline = new LowerCaseFilter(pipeline);
            pipeline = new StopFilter(pipeline, new CharArraySet(stopwords,true));
            //pipeline = new PorterStemFilter(pipeline);
          
        
          return new TokenStreamComponents(source, pipeline);
    }
} 
