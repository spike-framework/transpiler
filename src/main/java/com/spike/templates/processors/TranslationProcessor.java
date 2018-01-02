package com.spike.templates.processors;

import com.spike.templates.NewTemplateCompiler;
import org.jsoup.nodes.Element;

/**
 * Created by Dawid on 2017-09-06.
 */
public class TranslationProcessor implements Processor {

    @Override
    public void process(Element element, String spikeAttribute) {

        String translation = element.attr(spikeAttribute);
        String params = element.attr(NewTemplateCompiler.PARAMS);

        if (params.length() == 0) {
            translation = "[[app.message.get('" + translation + "')]]";
        } else {
            translation = "[[app.message.get('" + translation + "'," + "[" + params + "])]]";
        }

        element.removeAttr(spikeAttribute);
        element.removeAttr(NewTemplateCompiler.PARAMS);
        element.html(translation);


    }

}