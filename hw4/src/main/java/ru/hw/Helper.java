package ru.hw;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;
import java.util.function.Consumer;

public class Helper {

    public static final String TOPIC = "mytopic";
    public static final long SESSION_TIMEOUT_MLS = 15000;
    private static final String APPLICATION_ID = "hw4";
    private static final String APPLICATION_HOST = "localhost:9092";

    public static final Properties properties = new Properties() {{
        put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, APPLICATION_HOST);
        put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    }};

    public static Properties createStreamConfig() {
        return createStreamConfig(null);
    }

    public static Properties createStreamConfig(Consumer<Properties> consumer) {
        Properties currentProperties = (Properties) properties.clone();
        if (consumer != null)
            consumer.accept(currentProperties);
        return currentProperties;
    }
}
