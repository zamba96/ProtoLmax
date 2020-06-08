import json, time
from kafka import KafkaProducer
from kafka.errors import KafkaError
from random import uniform

producer = KafkaProducer(bootstrap_servers=['172.24.41.149:8081'], 
						 value_serializer=lambda v: json.dumps(v).encode('utf-8'))

i = 0
while i < 1000:
	'''yyyy-MM-dd HH:mm:ss'''
	producer.send('testChannel', {'type':'reserva', 'espacioId':i,'id':i, 'fechaInicio':'2018-08-10 10:55:00','duracion':60})
	print("manda"+ str(i) + " reserva")
	producer.flush()
	i += 1
	'''time.sleep(0.001)'''
