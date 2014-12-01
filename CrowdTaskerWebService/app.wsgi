import sys
import os

# Change working directory so relative paths (and template lookup) work again
root_folder = os.path.dirname(__file__)
web_folder = "C:\\web\\uploads\\"
os.chdir(root_folder)
os.environ['ORACLE_HOME'] = "C:\\oracle\\instantclient_11_2"
os.environ['PATH']  = os.environ['PATH'] + os.environ['ORACLE_HOME']+";"+os.path.dirname(__file__)

sys.path.append(os.path.dirname(__file__))
sys.path.append(os.environ['ORACLE_HOME'])

from bottle import route, default_app, request, get, post, abort, redirect, static_file

from ConnectionHandler import ConnectionHandler
from UserHandler import UserHandler
from TaskHandler import TaskHandler
from RatingHandler import RatingHandler

conn_handler = ConnectionHandler()
user_handler = UserHandler(conn_handler)
task_handler = TaskHandler(conn_handler)
rating_handler = RatingHandler(conn_handler)

@get('/user/:action')
@post('/user/:action')
def user(action):
    json_response = user_handler.do_action(action,request.body.getvalue())
    return json_response

@get('/task/:action')
@post('/task/:action')
def task(action):
    json_response = task_handler.do_action(action,request.body.getvalue())
    return json_response

@get('/rating/:action')
@post('/rating/:action')
def task(action):
    json_response = rating_handler.do_action(action,request.body.getvalue())
    return json_response

@post('/upload/profile')
def upload_profile():
    return user_handler.save_profile_picture( \
        request.files.get('picture'), request.forms.get('user_id'))
    
@route('/uploads/<filename>')
def get_uploads(filename):
    return static_file(filename, web_folder)

application = default_app()