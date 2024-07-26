package ru.hw;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.SessionWindows;

import java.time.Duration;

import static ru.hw.Helper.SESSION_TIMEOUT_MLS;
import static ru.hw.Helper.TOPIC;

public class Application {
    public static void main(String[] args) {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, Long> eventsCountStream = streamsBuilder.stream(TOPIC)
                .map((key, value) -> {return new KeyValue<>(key, value);})
                .groupByKey()
                .windowedBy(SessionWindows.ofInactivityGapWithNoGrace(Duration.ofMinutes(5)))
                .count(Materialized.as("event-counts-store"))
                .toStream()
                .map((key, value) -> new KeyValue<>(key.key().toString(), value));

        eventsCountStream.to("events-count-by-key", Produced.with(Serdes.String(), Serdes.Long()));
        eventsCountStream.foreach((key, value) -> System.out.println("key: %s count: %s".formatted(key, value)));
        Topology topology = streamsBuilder.build();
        System.out.println(topology.describe());
        try (KafkaStreams kafkaStreams = new KafkaStreams(topology, Helper.createStreamConfig())){
            kafkaStreams.start();
            Thread.sleep(SESSION_TIMEOUT_MLS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
