from ObjectHandler import ObjectHandler

class UserHandler(ObjectHandler):
    
    def __init__(self, conn_provider):
        ObjectHandler.__init__(self, conn_provider)
        
    def do_action(self, action, params):
        if action == 'get':
            return self.get_users(params)
        else:
            return ""
        
    def get_users(self, params):
        sql = "SELECT * FROM USERS"
        conn = self.conn_provider.get_db_connection()
        cursor = conn.cursor()
        cursor.execute(sql)
        
        data = ObjectHandler.rows_to_dict_list(self, cursor)  
        cursor.close()
        conn.close()
        
        return ObjectHandler.to_json(self, data)
