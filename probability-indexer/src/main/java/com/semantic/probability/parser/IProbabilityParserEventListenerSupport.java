package com.semantic.probability.parser;

/**
 * Created by gleab on 23.07.2014.
 */
public interface IProbabilityParserEventListenerSupport<T> {
    public void addListener(T listener);
    public void removeListener(T listener);
}
