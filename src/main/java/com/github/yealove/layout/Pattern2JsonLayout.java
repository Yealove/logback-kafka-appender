package com.github.yealove.layout;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.ConverterUtil;
import ch.qos.logback.core.pattern.PatternLayoutBase;
import ch.qos.logback.core.pattern.parser.Node;
import ch.qos.logback.core.pattern.parser.Parser;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.StatusManager;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * 根据模板取值，将结果以json字符串返回
 *
 * @see PatternLayoutBase
 * Created by Yealove on 2018-08-08.
 */
public class Pattern2JsonLayout extends PatternLayout {
    private static final int INTIAL_STRING_BUILDER_SIZE = 256;
    Converter<ILoggingEvent> head;
    Node t;
    Gson gson = new Gson();

    @Override
    protected String writeLoopOnConverters(ILoggingEvent event) {
        StringBuilder strBuilder = new StringBuilder(INTIAL_STRING_BUILDER_SIZE);

        Map<String, String> current = new HashMap<>();
        Converter<ILoggingEvent> c = head;
        Node tmp = t;
        while (c != null) {
            c.write(strBuilder, event);
            if (tmp != null) {
                current.put((String) tmp.getValue(), strBuilder.toString());
                tmp = tmp.getNext();
            }
            c = c.getNext();
            strBuilder.delete(0, strBuilder.length());
        }
        return gson.toJson(current);
    }

    @Override
    public void start() {
        if (getPattern() == null || getPattern().length() == 0) {
            addError("Empty or null pattern.");
            return;
        }
        try {
            Parser<ILoggingEvent> p = new Parser<ILoggingEvent>(getPattern());
            if (getContext() != null) {
                p.setContext(getContext());
            }
            t = p.parse();
            this.head = p.compile(t, getEffectiveConverterMap());
            if (postCompileProcessor != null) {
                postCompileProcessor.process(context, head);
            }
            ConverterUtil.setContextForConverters(getContext(), head);
            ConverterUtil.startConverters(this.head);
            super.start();
        } catch (ScanException sce) {
            StatusManager sm = getContext().getStatusManager();
            sm.add(new ErrorStatus("Failed to parse pattern \"" + getPattern() + "\".", this, sce));
        }
    }
}
