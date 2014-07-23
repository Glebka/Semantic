package com.semantic.probability.parser;

import java.util.List;

/**
 * Created by gleab on 23.07.2014.
 */
public interface IProbabilityParserEventListener{
    public void probabilityFound(String key, double probability);
    public void suffixFound(String suffix, int total, List<Pair> pairs);
    public void parsingFinished(boolean hasError);
}
