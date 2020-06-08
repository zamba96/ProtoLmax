import json, time
from timeit import default_timer as timer
from kafka import KafkaConsumer, KafkaProducer
from kafka.errors import KafkaError
from random import uniform

consumer = KafkaConsumer(bootstrap_servers=['172.24.41.149:8081'],
                         value_deserializer=lambda m: json.loads(m.decode('utf-8')))
consumer.subscribe(pattern='testChannel2')
i = 1
j = 0

for message in consumer:
	if j == 0:
		print(str(timer()*1000))
	if j == 999:
		print(str(timer()*1000))
		j = 1
	'''print ("%s:%d:%d: key=%s value=%s" % (message.topic, message.partition,
                                          message.offset, message.key,
                                          message.value))
	print(str(i))'''
	i += 1
	j += 1






