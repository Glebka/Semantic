package com.semantic.probability.parser;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gleab on 23.07.2014.
 */
public class ProbabilityParserEventListenerSupport implements IProbabilityParserEventListener, IProbabilityParserEventListenerSupport<IProbabilityParserEventListener> {
    private LinkedList<IProbabilityParserEventListener> m_listeners=new LinkedList<IProbabilityParserEventListener>();

    @Override
    public void addListener(IProbabilityParserEventListener listener) {
        m_listeners.add(listener);
    }

    @Override
    public void removeListener(IProbabilityParserEventListener listener) {
        m_listeners.remove(listener);
    }

    @Override
    public void probabilityFound(String key, double probability) {
        for (IProbabilityParserEventListener iParserEventListener : m_listeners) {
            iParserEventListener.probabilityFound(key, probability);
        }
    }

    @Override
    public void suffixFound(String suffix, int total, List<Pair> pairs) {
        for (IProbabilityParserEventListener iParserEventListener : m_listeners) {
            iParserEventListener.suffixFound(suffix, total, pairs);
        }
    }

    @Override
    public void parsingFinished(boolean hasError) {
        for (IProbabilityParserEventListener iParserEventListener : m_listeners) {
            iParserEventListener.parsingFinished(hasError);
        }
    }
}
