import simplejson as json

class ObjectHandler:
    
    OK_JSON = '[{"result":"ok"}]'
    FAIL_JSON = '[{"result":"fail"}]'
    
    def __init__(self, conn_provider):
        self.conn_provider = conn_provider
       
    def to_json(self, obj):
        return json.dumps(obj)
    
    def rows_to_dict_list(self, cursor):
        columns = [i[0] for i in cursor.description]
        return [dict(zip(columns, row)) for row in cursor]
    
    def from_json(self, jsonObj):
        if jsonObj is None or len(jsonObj) == 0:
            return None
        return json.loads(jsonObj)