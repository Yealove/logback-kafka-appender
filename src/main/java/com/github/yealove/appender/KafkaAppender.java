package com.github.yealove.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.encoder.Encoder;
import org.apache.kafka.clients.producer.*;

import java.io.IOException;
import java.util.Properties;

/**
 * 将日志发送到kafka中
 *
 * @see OutputStreamAppender#subAppend
 * Created by Yealove on 2018-08-08.
 */
public class KafkaAppender extends AppenderBase<ILoggingEvent> {

    private Encoder<ILoggingEvent> encoder;

    private String producerConfig;

    private String topic;

    //异常时，错误日志打印频率
    private int errorInterval = 10000;

    //发送异常计数
    private int count = 0;

    private Producer<String, byte[]> producer;

    protected void append(ILoggingEvent event) {
        if (!isStarted()) {
            return;
        }

        producer.send(new ProducerRecord<String, byte[]>(topic, encoder.encode(event)), new Callback() {
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if (count++ == errorInterval && e != null) {
                    count = 0;
                    addError("L og send error: ", e);
                }
            }
        });
    }

    @Override
    public void start() {
        init();
        super.start();
    }

    public void init() {
        Properties props = new Properties();
        if (producerConfig != null) {
            try {
                props.load(this.getClass().getClassLoader().getResourceAsStream(producerConfig));
            } catch (IOException e) {
                addError("Load properties failed", e);
            }
        }
        props.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.setProperty("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        producer = new KafkaProducer<>(props);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Encoder<ILoggingEvent> getEncoder() {
        return encoder;
    }

    public void setEncoder(Encoder<ILoggingEvent> encoder) {
        this.encoder = encoder;
    }

    public int getErrorInterval() {
        return errorInterval;
    }

    public void setErrorInterval(int errorInterval) {
        this.errorInterval = errorInterval;
    }

    public String getProducerConfig() {
        return producerConfig;
    }

    public void setProducerConfig(String producerConfig) {
        this.producerConfig = producerConfig;
    }
}
