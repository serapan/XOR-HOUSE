import mysql.connector
import sys

fhandle = open('src/main/resources/application.properties', 'r')
x = fhandle.read().split('\n')
username = list(filter(lambda a: "spring.datasource.username" in a, x))[0].split("=")[1]
password = list(filter(lambda a: "spring.datasource.password" in a, x))[0].split("=")[1]

mydb = mysql.connector.connect(host="localhost", user=username, passwd=password, database="cheapset")
mycursor = mydb.cursor()
query = "INSERT INTO token VALUES ('{}', '{} {}')".format(sys.argv[1], sys.argv[2], sys.argv[3])
#print (sys.argv)
#print (query)
mycursor.execute(query)
mydb.commit()

