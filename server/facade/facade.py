import lib.utils as utils
from lib.micropyserver import MicroPyServer

server : MicroPyServer

def set_server(srv : MicroPyServer):
    global server
    server = srv

def hello_world(request):
    # print(server)
    # server.send("HELLO WORLD FROM MICROPYTHON SERVER!")
    utils.send_response(server, "HELLO", 201)