package com.semantic.dictionary.parser;

/**
 * Created by gleab on 23.07.2014.
 */
public interface IParserEventListenerSupport<T> {
    public void addListener(T listener);
    public void removeListener(T listener);
}
