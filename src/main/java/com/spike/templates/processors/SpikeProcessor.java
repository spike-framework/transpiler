package com.spike.templates.processors;

import org.jsoup.nodes.Element;

/**
 * Created by Dawid on 2017-09-06.
 */
public abstract class SpikeProcessor extends Processor {

    public abstract void process(Element element, String spikeAttribute) throws Exception;

}
