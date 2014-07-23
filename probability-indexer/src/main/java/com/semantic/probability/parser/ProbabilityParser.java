package com.semantic.probability.parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

/**
 * Created by gleab on 23.07.2014.
 */

class ParserAction {
    public static final int NONE = 0,
        SKIP = 2,
        PROBABILITY_PARSING = 4,
        SUFFIXES_PARSING = 8,
        POP_STATE = 16;
}

class Action {
    public final String token;
    public final int action;

    public Action(String token, int action) {
        this.token=token;
        this.action=action;
    }
}

public class ProbabilityParser implements IProbabilityParserEventListenerSupport<IProbabilityParserEventListener>, Runnable {

    private Path m_file_path;
    private final static Charset ENCODING = StandardCharsets.UTF_8;
    private ProbabilityParserEventListenerSupport m_listeners=new ProbabilityParserEventListenerSupport();
    private String m_last_error;
    private Stack<Integer> m_states_stack = new Stack<>();
    private ArrayList<Action> m_predefined_actions = new ArrayList<>();

    public ProbabilityParser() {
        m_states_stack.push(ParserAction.NONE);
        m_predefined_actions.add(new Action("<TagsetFile>",ParserAction.SKIP));
        m_predefined_actions.add(new Action("<Smoothing>",ParserAction.SKIP));
        m_predefined_actions.add(new Action("<Initial>",ParserAction.SKIP));
        m_predefined_actions.add(new Action("<UNOBSERVED_WORD>",ParserAction.SKIP));
        m_predefined_actions.add(new Action("<Theeta>",ParserAction.SKIP));
        m_predefined_actions.add(new Action("<SingleTagFreq>",ParserAction.SKIP));
        m_predefined_actions.add(new Action("<ClassTagFreq>",ParserAction.SKIP));
        m_predefined_actions.add(new Action("<FormTagFreq>",ParserAction.SKIP));
        m_predefined_actions.add(new Action("<UnknownTags>",ParserAction.SKIP));
        m_predefined_actions.add(new Action("<Suffixes>",ParserAction.SUFFIXES_PARSING));
        m_predefined_actions.add(new Action("</",ParserAction.POP_STATE));
        m_predefined_actions.add(new Action("<",ParserAction.PROBABILITY_PARSING));
    }

    public String lastError()
    {
        return m_last_error;
    }

    private void parseProbability(String line)
    {
        Scanner scanner = new Scanner(line);
        scanner.useDelimiter(" ");
        if (scanner.hasNext())
        {
            String key = scanner.next();
            double probability = Double.parseDouble(scanner.next());
            m_listeners.probabilityFound(key, probability);
        }
    }
    private void parseSuffix(String line)
    {
        Scanner scanner = new Scanner(line);
        scanner.useDelimiter(" ");
        ArrayList<Pair> pairs = new ArrayList<>();
        if (scanner.hasNext())
        {
            String suffix = scanner.next();
            int total = Integer.parseInt(scanner.next());
            while(scanner.hasNext())
                pairs.add(new Pair(scanner.next(),Integer.parseInt(scanner.next())));
            m_listeners.suffixFound(suffix,total,pairs);
        }
    }

    private void parseLine(String line) throws Exception
    {
        for (Action a : m_predefined_actions)
        {
            if(line.contains(a.token))
            {
                if(a.action==ParserAction.POP_STATE)
                    m_states_stack.pop();
                else
                    m_states_stack.push(a.action);
                return;
            }
        }
        switch (m_states_stack.peek())
        {
            case ParserAction.NONE:
            case ParserAction.SKIP:
                return;
            case ParserAction.PROBABILITY_PARSING:
                parseProbability(line);
                break;
            case ParserAction.SUFFIXES_PARSING:
                parseSuffix(line);
                break;
            default:
                throw new Exception("Invalid parser state while parsing: '"+line+"'");
        }

    }
    public void setFileName(String fileName)
    {
        m_file_path= Paths.get(fileName);
    }

    @Override
    public void addListener(IProbabilityParserEventListener listener) {
        m_listeners.addListener(listener);
    }

    @Override
    public void removeListener(IProbabilityParserEventListener listener) {
        m_listeners.removeListener(listener);
    }

    @Override
    public void run() {
        try {
            Scanner scanner =  new Scanner(m_file_path, ENCODING.name());
            while (scanner.hasNextLine())
                parseLine(scanner.nextLine());
            m_listeners.parsingFinished(false);
        } catch (IOException ex) {
            m_last_error=ex.getMessage();
            m_listeners.parsingFinished(true);
        } catch (Exception e) {
            m_last_error=e.getMessage();
            m_listeners.parsingFinished(true);
        }
    }
}
