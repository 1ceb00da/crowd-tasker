from ObjectHandler import ObjectHandler

class TaskHandler(ObjectHandler):
    
    def __init__(self, conn_provider):
        ObjectHandler.__init__(self, conn_provider)
        
    def do_action(self, action, params):
        jsonParams = ObjectHandler.from_json(self,params)
        if action == 'get':
            return self.get_tasks(jsonParams)
        elif action == 'create':
            return self.create_task(jsonParams)
        elif action == 'update':
            return self.update_task(jsonParams)
        else:
            return ""
        
        
    def create_task(self,params):
        sql = "INSERT INTO TASKS (OWNER_ID, WORKER_ID, NAME, DESCRIPTION, " \
        "PAYMENT, PICKUP_ADDR, DROPOFF_ADDR, STATUS, DEADLINE, PICKUP_LOC, DROPOFF_LOC) " \
        "VALUES (:1,:2,:3,:4,:5,:6,:7,:8"
        
        values = (params.get('OWNER_ID',None), params.get("WORKER_ID",None), params.get("NAME",None), \
            params.get("DESCRIPTION",""), params.get("PAYMENT",None), \
            params.get('PICKUP_ADDR', None), params.get('DROPOFF_ADDR', None), params.get('STATUS','0'))
            
        if "DEADLINE" in params:
            sql += ",TO_DATE(:9,'YYYY-MM-DD HH24:MI')"
            values = values + (params['DEADLINE'],)
        else:
            sql += ",NULL"
           
        if ("PICKUP_LAT" in params and "PICKUP_LONG" in params):
            sql += ",SDO_GEOMETRY(2001,8307, SDO_POINT_TYPE(:10, :11, NULL),NULL,NULL)"
            values = values + (params['PICKUP_LAT'], params['PICKUP_LONG'])
        else:
            sql += ",NULL"
           
        if ("DROPOFF_LAT" in params and "DROPOFF_LONG" in params):
            sql += ",SDO_GEOMETRY(2001,8307, SDO_POINT_TYPE(:12, :13, NULL),NULL,NULL)"
            values = values + (params['DROPOFF_LAT'], params['DROPOFF_LONG'])
        else:
            sql += ",NULL"
        
        sql += ")"
        
        conn = self.conn_provider.get_db_connection()
        cursor = conn.cursor()
        r = cursor.execute(sql, values)
        conn.commit()
        cursor.close()
        conn.close()
                
        return ObjectHandler.OK_JSON
    
    def update_task(self,params):
        if "ID" not in params:
            return ObjectHandler.FAIL_JSON
        
        sql = "UPDATE TASKS SET OWNER_ID=:1, WORKER_ID=:2, NAME=:3, DESCRIPTION=:4, " \
        "PAYMENT=:5, PICKUP_ADDR=:6, DROPOFF_ADDR=:7, STATUS=:8"
        
        values = (params.get('OWNER_ID',None), params.get("WORKER_ID",None), params.get("NAME",None), \
            params.get("DESCRIPTION",""), params.get("PAYMENT",None), \
            params.get('PICKUP_ADDR', None), params.get('DROPOFF_ADDR', None), params.get('STATUS','0'))
            
        if "DEADLINE" in params:
            sql += ",DEADLINE=TO_DATE(:9,'YYYY-MM-DD HH24:MI')"
            values = values + (params['DEADLINE'],)
        else:
            sql += ",DEADLINE=NULL"
           
        if ("PICKUP_LAT" in params and "PICKUP_LONG" in params):
            sql += ",PICKUP_LOC=SDO_GEOMETRY(2001,8307, SDO_POINT_TYPE(:10, :11, NULL),NULL,NULL)"
            values = values + (params['PICKUP_LAT'], params['PICKUP_LONG'])
        else:
            sql += ",PICKUP_LOC=NULL"
           
        if ("DROPOFF_LAT" in params and "DROPOFF_LONG" in params):
            sql += ",DROPOFF_LOC=SDO_GEOMETRY(2001,8307, SDO_POINT_TYPE(:12, :13, NULL),NULL,NULL)"
            values = values + (params['DROPOFF_LAT'], params['DROPOFF_LONG'])
        else:
            sql += ",DROPOFF_LOC=NULL"
        
        sql += " WHERE ID = :14"
        values = values + (params["ID"],)
            
        conn = self.conn_provider.get_db_connection()
        cursor = conn.cursor()
        r = cursor.execute(sql, values)
        conn.commit()
        cursor.close()
        conn.close()
        
        return ObjectHandler.OK_JSON
        
    def get_tasks(self,params):
        sql = "SELECT t.ID, t.OWNER_ID, t.WORKER_ID, t.NAME, t.DESCRIPTION, " \
        "TO_CHAR(t.DEADLINE,'YYYY-MM-DD HH24:MI') AS DEADLINE, t.PAYMENT, "\
        "t.PICKUP_LOC.SDO_POINT.X AS PICKUP_LAT, t.PICKUP_LOC.SDO_POINT.Y AS PICKUP_LONG, " \
        "t.DROPOFF_LOC.SDO_POINT.X AS DROPOFF_LAT, t.DROPOFF_LOC.SDO_POINT.Y AS DROPOFF_LONG, " \
        "t.PICKUP_ADDR, t.DROPOFF_ADDR, t.STATUS FROM TASKS t"
        
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
