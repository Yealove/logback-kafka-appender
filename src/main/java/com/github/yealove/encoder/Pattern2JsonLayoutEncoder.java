package com.github.yealove.encoder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;
import com.github.yealove.layout.Pattern2JsonLayout;

/**
 * 根据模板取值，将结果以json字符串返回
 *
 * @see ch.qos.logback.classic.encoder.PatternLayoutEncoder
 * Created by Yealove on 2018-08-08.
 */
public class Pattern2JsonLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {
    @Override
    public void start() {
        Pattern2JsonLayout pattern2JsonLayout = new Pattern2JsonLayout();
        pattern2JsonLayout.setContext(context);
        pattern2JsonLayout.setPattern(getPattern());
        pattern2JsonLayout.setOutputPatternAsHeader(outputPatternAsHeader);
        pattern2JsonLayout.start();
        this.layout = pattern2JsonLayout;
        super.start();
    }
}
