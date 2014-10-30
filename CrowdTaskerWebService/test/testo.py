#!/Python27/python
import hashlib
import cx_Oracle
print "Content-type: text/html"
print
print "<html><head>"
print ""
print "</head><body>"
hashed = hashlib.sha1("geoman").hexdigest()
print "HASHED: " + hashed + "<br />"

print "USERS: <br />"

connection = cx_Oracle.connect('team12/team12@geodb.usc.edu:1521/GEODBS')
sql = "SELECT * FROM USERS"

cursor = connection.cursor()
cursor.execute(SQL)
for row in cursor:
    print row
cursor.close()
connection.close()
