import json, time
from kafka import KafkaProducer
from kafka.errors import KafkaError
from random import uniform

producer = KafkaProducer(bootstrap_servers=['172.24.41.149:8081'], 
						 value_serializer=lambda v: json.dumps(v).encode('utf-8'))

i = 1
j = 0
while j < 10000:

	producer.send('testChannel', {'type': 'get', 'id':i, 'espacioId':i})
	print("manda"+ str(i))
	producer.flush()
	i += 1
	j += 1
	if i == 999:
		i = 0
	'''time.sleep(0.1)'''
