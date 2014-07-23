package com.semantic.probability.searcher;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
public class ProbabilitySearch {
    
    private IndexSearcher searcher = null;
    private QueryParser parser = null;
    
    public ProbabilitySearch(String[] args) {
        if(args.length==0)
        {
            System.out.println("Usage: probability-search INDEX_NAME");
            System.exit(0);
        }
        try {
          searcher = new IndexSearcher(IndexReader.open(FSDirectory.open(new File(args[0]))));
          parser = new QueryParser(Version.LUCENE_4_9,"key",new WhitespaceAnalyzer(Version.LUCENE_4_9));
      } catch (IOException ex) {
            System.out.println(ex.getMessage());
      }
      while(true)
      {
          System.out.print("\nEnter search pattern: ");
          String search_pattern=System.console().readLine();
          System.out.println("");
            try {
                for (ScoreDoc doc : performSearch(search_pattern).scoreDocs) {
                    Document d=searcher.doc(doc.doc);
                    for(IndexableField f : d.getFields())
                        System.out.println(f.name()+": "+f.stringValue());
                    System.out.println();
                } 
            } catch (IOException ex) {
                Logger.getLogger(ProbabilitySearch.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(ProbabilitySearch.class.getName()).log(Level.SEVERE, null, ex);
            }
      }
    }
    
    private TopDocs performSearch(String queryString) throws IOException, ParseException 
    {
        Query query = parser.parse(queryString);
        TopDocs hits = searcher.search(query,20);
        return hits;
    }
    public static void main(String[] args) {
        new ProbabilitySearch(args);
    }
    
}
