import cx_Oracle

class ConnectionHandler:
    
    def __init__(self):
        self.db_connection = cx_Oracle.connect('team12/team12@geodb.usc.edu:1521/GEODBS')
        self.db_connection.close()
        
    def get_db_connection(self):
        conn = cx_Oracle.connect('team12/team12@geodb.usc.edu:1521/GEODBS')
        return conn