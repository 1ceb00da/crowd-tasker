from ObjectHandler import ObjectHandler

class RatingHandler(ObjectHandler):
    
    def __init__(self, conn_provider):
        ObjectHandler.__init__(self, conn_provider)
        
    def do_action(self, action, params):
        jsonParams = ObjectHandler.from_json(self,params)

        if action == 'create':
            return self.create_rating(jsonParams)
        elif action == 'update':
            return self.update_rating(jsonParams)
        else:
            return ""
        
    def create_rating(self,params):
        
        sql = "INSERT INTO RATINGS (FROM_ID, TO_ID, TASK_ID, RATING) VALUES (:1,:2,:3,:4)"
        
        values = (params.get('FROM_ID',None), params.get("TO_ID",None), \
                  params.get("TASK_ID",None), params.get("RATING",None))
        
        conn = self.conn_provider.get_db_connection()
        cursor = conn.cursor()
        r = cursor.execute(sql, values)
        conn.commit()
        cursor.close()
        conn.close()
                
        return ObjectHandler.OK_JSON
    
    def update_rating(self,params): 
        sql = "UPDATE RATINGS SET RATING=:1, TO_ID=:2 WHERE FROM_ID=:3 AND TASK_ID=:4"
        
        values = (params.get("RATING",0), params.get("TO_ID",None), \
                  params.get("FROM_ID",None), params.get("TASK_ID",None))
        
        conn = self.conn_provider.get_db_connection()
        cursor = conn.cursor()
        r = cursor.execute(sql, values)
        conn.commit()
        cursor.close()
        conn.close()
                
        return ObjectHandler.OK_JSON
        
    

  