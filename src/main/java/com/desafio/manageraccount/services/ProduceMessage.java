package com.desafio.manageraccount.services;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class ProduceMessage<T> implements Closeable {

    private final KafkaProducer<String, T> producer;

    public ProduceMessage() {
        this.producer = new KafkaProducer<>(properties());
    }

    public void sendMessage(String topic, T message) throws ExecutionException, InterruptedException, TimeoutException {
        var record = new ProducerRecord<String,T>(topic, message);
        producer.send(record, (data, ex) -> {
            if(ex != null) {
                ex.printStackTrace();
                return;
            }
            System.out.println("sucesso enviando " + data.topic() + ":::partition " + data.partition() + "/ offset "  + data.offset() + "/ timestamp " + data.timestamp());
        }).get(15, TimeUnit.SECONDS);
    }

    private static Properties properties() {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return properties;
    }

    @Override
    public void close() throws IOException {
        producer.close();
    }
}
