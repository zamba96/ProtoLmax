Para ejecutar, simplemente ejecute el jar desde la consola con 
java -jar KafkaLoadTester, 
este se comunicará con el servidor Kafka en una maquina virtual

Luego especifique el numero de peticiones que desea realizar y el numero de peticiones por segundo que desea
A partir de 2000, el programa va a mandar solicitudes lo más rapido que pueda

luego, cuando aparezca
==============
Fin de la prueba
===============

puede escribir stop y se generara un reporte en formato CSV de la prueba, con la latencia de cada peticion que fue realizada