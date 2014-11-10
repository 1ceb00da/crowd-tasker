from ObjectHandler import ObjectHandler

class UserHandler(ObjectHandler):
    USER_EXIST_JSON = '[{"result":"exist"}]'
    
    def __init__(self, conn_provider):
        ObjectHandler.__init__(self, conn_provider)
        
    def do_action(self, action, params):
        jsonParams = ObjectHandler.from_json(self,params)

        if action == 'get':
            return self.get_users(jsonParams)
        elif action == 'create':
            return self.create_user(jsonParams)
        else:
            return ""
        
    def create_user(self,params):
        if 'LOGIN' not in params or 'PASS' not in params  or 'EMAIL' not in params:
            return ObjectHandler.FAIL_JSON
        
        checkExistParams = {"LOGIN":params.get('LOGIN','')}
        
        existing_user = ObjectHandler.from_json(self, self.get_users(checkExistParams))
        if (len(existing_user) > 0):
            return self.USER_EXIST_JSON
        
        
        sql = "INSERT INTO USERS (LOGIN, PASS, EMAIL, IMEI) VALUES (:1,:2,:3,:4)"
        
        values = (params.get('LOGIN',None), params.get("PASS",None), \
                  params.get("EMAIL",None), params.get("IMEI",None))
        
        conn = self.conn_provider.get_db_connection()
        cursor = conn.cursor()
        r = cursor.execute(sql, values)
        conn.commit()
        cursor.close()
        conn.close()
                
        return ObjectHandler.OK_JSON
        
    def get_users(self, params):
        sql = "SELECT * FROM USERS"
        
        values = ()
        if(params is not None and len(params) > 0):
            paramsLen = len(params)
            sql += " WHERE "
            i = 0
            for key in params:
                sql += key + "=:" + str(i)
                if(i < paramsLen - 1):
                    sql += " AND "
                values = values + (params[key],)
                i += 1
    
        conn = self.conn_provider.get_db_connection()
        cursor = conn.cursor()
        cursor.execute(sql, values)
        
        data = ObjectHandler.rows_to_dict_list(self, cursor)  
        cursor.close()
        conn.close()
        
        return ObjectHandler.to_json(self, data)

  