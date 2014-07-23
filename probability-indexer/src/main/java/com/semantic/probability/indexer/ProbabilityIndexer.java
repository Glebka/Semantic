package com.semantic.probability.indexer;
import com.semantic.probability.parser.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class ProbabilityIndexer implements IProbabilityParserEventListener{
    private IndexWriter m_index_writer;
    
    public ProbabilityIndexer(String indexPath) {
        try {
            Directory dir = FSDirectory.open(new File(indexPath));
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
            iwc.setOpenMode(OpenMode.CREATE);
            iwc.setRAMBufferSizeMB(256.0);
            m_index_writer = new IndexWriter(dir, iwc);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void closeIndex()
    {
        try {
            m_index_writer.forceMerge(1);
            m_index_writer.close(true);
        } catch (IOException ex) {
            Logger.getLogger(ProbabilityIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void probabilityFound(String key, double probability) {
        Document doc=new Document();
        doc.add(new StringField("key", key, Field.Store.YES));
        doc.add(new DoubleField("probability", probability, Field.Store.YES));
        try {
            m_index_writer.addDocument(doc);
        } catch (IOException ex) {
            Logger.getLogger(ProbabilityIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void suffixFound(String suffix, int total, List<Pair> pairs) {
        Document doc=new Document();
        doc.add(new StringField("suffix", suffix, Field.Store.YES));
        doc.add(new IntField("total", total, Field.Store.YES));
        for(Pair p : pairs)
            doc.add(new IntField(p.tag, p.count, Field.Store.YES));
        try {
            m_index_writer.addDocument(doc);
        } catch (IOException ex) {
            Logger.getLogger(ProbabilityIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void parsingFinished(boolean hasError) {
        
    }
    
}
