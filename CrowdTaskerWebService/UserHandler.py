import sys
import os
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
        elif action == 'update':
            return self.update_user(jsonParams)
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
    
    def update_user(self,params): 
        if "ID" not in params:
            return ObjectHandler.FAIL_JSON
        
        sql = "UPDATE USERS SET PASS=:1, EMAIL=:2, FIRST_NAME=:3, LAST_NAME=:4, IMEI=:5"
        
        values = (params.get("PASS",None), params.get("EMAIL",None), \
                  params.get("FIRST_NAME",None), params.get("LAST_NAME",None), \
                  params.get("IMEI",None))
        
        sql += " WHERE ID = :6"
        values = values + (params["ID"],)
        
        conn = self.conn_provider.get_db_connection()
        cursor = conn.cursor()
        r = cursor.execute(sql, values)
        conn.commit()
        cursor.close()
        conn.close()
                
        return ObjectHandler.OK_JSON
        
    def get_users(self, params):
        
        sql = "SELECT u.ID, u.PASS, u.LOGIN, u.EMAIL, u.FIRST_NAME, u.LAST_NAME, u.IMEI, " \
              "u.PROFILE_PIC, AVG(r.rating) AS RATING FROM USERS u LEFT JOIN RATINGS r ON u.id = r.to_id " 
              
        
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
        sql += " GROUP BY u.ID, u.PASS, u.LOGIN, u.EMAIL, u.FIRST_NAME, " \
               " u.LAST_NAME, u.IMEI, u.PROFILE_PIC"
        
        conn = self.conn_provider.get_db_connection()
        cursor = conn.cursor()
        cursor.execute(sql, values)
        
        data = ObjectHandler.rows_to_dict_list(self, cursor)  
        cursor.close()
        conn.close()
        
        return ObjectHandler.to_json(self, data)
    
    def save_profile_picture(self, upload, user_id):
        if upload is not None:
            name, ext = os.path.splitext(upload.filename)

            if ext not in ('.png','.jpg','.jpeg'):
                return "fail"

            save_path = "uploads"
            if not os.path.exists(save_path):
                os.makedirs(save_path)

            file_path = "{path}/{file}".format(path=save_path, file=upload.filename)

            with open(file_path, 'wb') as open_file:
                open_file.write(upload.file.read())
                
            sql = "UPDATE USERS SET PROFILE_PIC=:1 WHERE ID=:2"
            values = (file_path, user_id)
        
            conn = self.conn_provider.get_db_connection()
            cursor = conn.cursor()
            r = cursor.execute(sql, values)
            conn.commit()
            cursor.close()
            conn.close()
                
            return "ok"
        else:
            return "fail"

  