import json, time
from kafka import KafkaProducer
from kafka.errors import KafkaError
from random import uniform

producer = KafkaProducer(bootstrap_servers=['172.24.41.149:8081'], 
						 value_serializer=lambda v: json.dumps(v).encode('utf-8'))

i = 0
while i < 1001:

	producer.send('testChannel', {'type':'getTime', 'espacioId':i,'id':i})
	print("manda"+ str(i) + getTime)
	producer.flush()
	i += 1
	time.sleep(0.001)
