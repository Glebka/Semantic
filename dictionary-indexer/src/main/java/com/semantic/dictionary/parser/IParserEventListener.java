package com.semantic.dictionary.parser;

/**
 * Created by gleab on 23.07.2014.
 */
public interface IParserEventListener{
    public void entryFound(String word,String lemma,String code);
    public void parsingFinished(boolean hasError);
}
