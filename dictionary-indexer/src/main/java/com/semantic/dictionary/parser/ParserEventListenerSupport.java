package com.semantic.dictionary.parser;

import java.util.LinkedList;

/**
 * Created by gleab on 23.07.2014.
 */
public class ParserEventListenerSupport implements IParserEventListener, IParserEventListenerSupport<IParserEventListener> {
    private LinkedList<IParserEventListener> m_listeners=new LinkedList<IParserEventListener>();

    @Override
    public void addListener(IParserEventListener listener) {
        m_listeners.add(listener);
    }

    @Override
    public void removeListener(IParserEventListener listener) {
        m_listeners.remove(listener);
    }

    @Override
    public void entryFound(String word, String lemma, String code) {
        for (IParserEventListener iParserEventListener : m_listeners) {
            iParserEventListener.entryFound(word,lemma,code);
        }
    }

    @Override
    public void parsingFinished(boolean hasError) {
        for (IParserEventListener iParserEventListener : m_listeners) {
            iParserEventListener.parsingFinished(hasError);
        }
    }
}
