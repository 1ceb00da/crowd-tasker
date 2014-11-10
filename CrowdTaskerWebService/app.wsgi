import sys
import os

# Change working directory so relative paths (and template lookup) work again
os.chdir(os.path.dirname(__file__))
os.environ['ORACLE_HOME'] = "C:\\oracle\\instantclient_11_2"
os.environ['PATH']  = os.environ['PATH'] + os.environ['ORACLE_HOME']+";"+os.path.dirname(__file__)

sys.path.append(os.path.dirname(__file__))
sys.path.append(os.environ['ORACLE_HOME'])

from bottle import route, default_app, request, get, post, abort, redirect

from ConnectionHandler import ConnectionHandler
from UserHandler import UserHandler
from TaskHandler import TaskHandler

conn_handler = ConnectionHandler()
user_handler = UserHandler(conn_handler)
task_handler = TaskHandler(conn_handler)


@get('/user/:action')
@post('/user/:action')
def user(action):
    json_response = user_handler.do_action(action,request.body.gevalue())
    return json_response

@get('/task/:action')
@post('/task/:action')
def task(action):
    json_response = task_handler.do_action(action,request.body.getvalue())
    return json_response


@route('/showenv')
def hello():
    return "PATH " + os.environ.get('PATH') +"<br>" + " ORACLE_HOME " + os.environ.get('ORACLE_HOME')

application = default_app()