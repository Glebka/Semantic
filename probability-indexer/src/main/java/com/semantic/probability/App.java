package com.semantic.probability;

import com.semantic.probability.indexer.ProbabilityIndexer;
import com.semantic.probability.parser.IProbabilityParserEventListener;
import com.semantic.probability.parser.Pair;
import com.semantic.probability.parser.ProbabilityParser;

import java.util.List;
import java.util.Stack;

public class App implements IProbabilityParserEventListener
{
    private ProbabilityIndexer m_indexer;
    private ProbabilityParser m_parser;
    private Stack<String> m_files=new Stack<String>();

    public App(String args[])
    {
        if(args.length<3)
        {
            System.out.println("Usage: probability-indexer.jar -o INDEX_NAME FILE1[ FILE2 ...]");
            return;
        }
        for(int i=2;i<args.length;i++)
            m_files.push(args[i]);
        m_indexer=new ProbabilityIndexer(args[1]);
        m_parser=new ProbabilityParser();
        m_parser.addListener(m_indexer);
        m_parser.addListener(this);
        Runtime.getRuntime().addShutdownHook(new Thread(new Hook(m_indexer)));
        parseDict();
    }

    public static void main( String[] args )
    {
        new App(args);
    }


    private void parseDict()
    {
        m_parser.setFileName(m_files.peek());
        Thread t=new Thread(m_parser);
        t.start();
        System.out.println("Parsing started: "+m_files.peek());
    }

    class Hook implements Runnable
    {
        private final ProbabilityIndexer indexer;
        public Hook(ProbabilityIndexer i)
        {
            indexer=i;
        }

        @Override
        public void run() {
            indexer.closeIndex();
        }
    }

    @Override
    public void probabilityFound(String key, double probability) {

    }

    @Override
    public void suffixFound(String suffix, int total, List<Pair> pairs) {

    }

    @Override
    public void parsingFinished(boolean hasError) {
        System.out.println("Parsing finished: "+m_files.peek());
        m_files.pop();
        if(m_files.isEmpty())
            System.exit(0);
        parseDict();
    }
}
