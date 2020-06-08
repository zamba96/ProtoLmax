import json, time, random
from kafka import KafkaProducer
from kafka.errors import KafkaError
from random import uniform, choice

producer = KafkaProducer(bootstrap_servers=['172.24.41.149:8081'], 
						 value_serializer=lambda v: json.dumps(v).encode('utf-8'))

while True:
	producer.send('Notificaciones', {'Hora': time.strftime("%X"), 'Tipo': choice(['Actualizacion','Noticia','Promocion']),'Mensaje': 'mensajePrueba', })
	producer.flush()
	time.sleep(5)
