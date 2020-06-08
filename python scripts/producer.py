import json, time
from kafka import KafkaProducer
from kafka.errors import KafkaError
from random import uniform

producer = KafkaProducer(bootstrap_servers=['172.24.41.149:8081'], 
						 value_serializer=lambda v: json.dumps(v).encode('utf-8'))

while True:
	producer.send('alta-piso2-cuarto1', {'time': time.strftime("%X"), 'measurement': 'temperature', 
					'value': round(uniform(20, 25),1), 'unit': 'C', 'place': 'piso2/cuarto1'})
	producer.send('baja-piso2-cuarto2', {'time': time.strftime("%X"), 'measurement': 'temperature', 
					'value': round(uniform(20, 25),1), 'unit': 'C', 'place': 'piso2/cuarto2'})
	print("manda")
	producer.flush()
	time.sleep(5)
