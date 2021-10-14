package com.haha.im;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerDemo {
    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();

        // 必须设置的属性
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        props.put("group.id", "group1");

        // 可选设置属性
        props.put("enable.auto.commit", "true");
        // 自动提交offset,每1s提交一次
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset","earliest ");
        props.put("client.id", "zy_client_id");
        KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);
        // 订阅test1 topic
        consumer.subscribe(Collections.singletonList("haha_im_msg"));

        while(true) {
            //  从服务器开始拉取数据
            ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(100));
            records.forEach(record -> {
                System.out.printf("topic = %s ,partition = %d,offset = %d, key = %s, value = %s%n", record.topic(), record.partition(),
                        record.offset(), record.key(), record.value());
            });
        }
    }
}
