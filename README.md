# logback-kafka-appender

## usage

logback.xml:
```xml
<appender name="KAFKA" class="com.github.yealove.appender.KafkaAppender">
    <encoder class="com.github.yealove.encoder.Pattern2JsonLayoutEncoder">
        <charset>UTF-8</charset>
        <pattern>%date{yyyy-MM-dd HH:mm:ssZ}%thread%level%logger%message%exception</pattern>
    </encoder>
    <topic>logs</topic>
    <producerConfig>producer.properties</producerConfig>
</appender>

<logger name="com.github.yealove" level="DEBUG" additivity="false">
    <appender-ref ref="KAFKA"/>
</logger>
```

kafka producer配置中不用配置key.serializer和value.serializer，固定为：

key.serializer=org.apache.kafka.common.serialization.StringSerializer

value.serializer=org.apache.kafka.common.serialization.ByteArraySerializer
