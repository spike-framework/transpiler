package com.spike.templates.processors;

import com.spike.templates.U;
import org.jsoup.nodes.Element;

/**
 * Created by Dawid on 2017-09-06.
 */
public class HrefProcessor implements Processor {

    static private int hrefId = 0;

    @Override
    public void process(Element element, String spikeAttribute) throws Exception {

        if (element.tagName().equals("spike")) {
            throw new Exception("Spike Compiler: 'href' are not allowed on @spike tags");
        }

        String hrefValue = element.attr(spikeAttribute);

        if(element.id().isEmpty()){
            hrefId++;
            element.attr("id", "spike-href-"+hrefId);
        }

        element.attr("href", hrefValue);
        element.attr("spike-href","");
        element.removeAttr(spikeAttribute);

    }

}
