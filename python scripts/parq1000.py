import json, time
from kafka import KafkaProducer
from timeit import default_timer as timer
from kafka.errors import KafkaError
from random import uniform

producer = KafkaProducer(bootstrap_servers=['172.24.41.149:8081'], 
						 value_serializer=lambda v: json.dumps(v).encode('utf-8'))

i = 0
maxLat = 4.783360
maxLong = -74.026316
minLat = 4.601618
minLong = -74.163695
while i < 1000:
	lat = round(uniform(minLat, maxLat),6)
	lon = round(uniform(minLong, maxLong),6)
	calle = round(uniform(20,100))
	num1 = round(uniform(20,100))
	num2 = round(uniform(0,99))
	direccion = "Calle " + str(calle) + " #" + str(num1) + "-" + str(num2)
	producer.send('testChannel', {'type':'add', 'espacioId':i,'id':i, 'lat':lat, 'lon':lon, 'direccion':direccion})
	print("manda"+ str(i) + "add Parq")
	producer.flush()
	print(str(timer()*1000))
	i += 1
	'''time.sleep(0.001)'''
