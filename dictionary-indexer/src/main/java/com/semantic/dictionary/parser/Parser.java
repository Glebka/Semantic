package com.semantic.dictionary.parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Parser implements IParserEventListenerSupport<IParserEventListener>,Runnable{

    private Path m_file_path;
    private final static Charset ENCODING = StandardCharsets.UTF_8; 
    private ParserEventListenerSupport m_listeners=new ParserEventListenerSupport();
    private String m_last_error;
    
    public String lastError()
    {
        return m_last_error;
    }
    private void parse_line(String line)
    {
        Scanner scanner = new Scanner(line);
        scanner.useDelimiter(" ");
        if (scanner.hasNext())
        {
            String word = scanner.next();
            String lemma = scanner.next();
            String code = scanner.next();
            m_listeners.entryFound(word, lemma, code);
        }
    }
    public void setFileName(String fileName)
    {
       m_file_path=Paths.get(fileName);
    }

    @Override
    public void addListener(IParserEventListener listener) {
        m_listeners.addListener(listener);
    }

    @Override
    public void removeListener(IParserEventListener listener) {
        m_listeners.removeListener(listener);
    }

    @Override
    public void run() {
        try {
            Scanner scanner =  new Scanner(m_file_path, ENCODING.name());
            while (scanner.hasNextLine())
                parse_line(scanner.nextLine());
            m_listeners.parsingFinished(false);
        } catch (IOException ex) {
            m_last_error=ex.getMessage();
            m_listeners.parsingFinished(true);
        }
    }
}
