import lib.utils as utils
from lib.micropyserver import MicroPyServer
import core.core as core
import json

server : MicroPyServer

def set_server(srv : MicroPyServer):
    global server
    server = srv

def hello_world(request):
    # print(server)
    # server.send("HELLO WORLD FROM MICROPYTHON SERVER!")
    utils.send_response(server, "HELLO", 201)

def configure_settings(request):
    print(request)
    try:
        json_start = str.find(request, "{")
        request_body = request[json_start : ]
        request_json = json.loads(request_body)

        core.configure_settings(
            request_json["auto_ligth"],
            request_json["auto_ligth_level_on"],
            request_json["auto_ligth_level_off"],
            request_json["proximity_lock"],
            request_json["proximity_meters"])

        utils.send_response(server, "", 204)
    except (ValueError, KeyError) as e:
        print(e)
        utils.send_response(server, "Invalid JSON", 400)

    
def get_sensor_data(request):
    data = core.get_sensor_data()
    json_data = json.dumps(data)
    # print(data)
    utils.send_response(server, json_data, 200)

def set_ligth(request):
    print(request)
    try:
        json_start = str.find(request, "{")
        request_body = request[json_start : ]
        request_json = json.loads(request_body)
        core.setLight(request_json["state"])
        utils.send_response(server, "OK", 200)
    except (ValueError, KeyError) as e:
        print(e)
        utils.send_response(server, "Invalid JSON", 400)

def set_lock(request):
    print(request)
    try:
        json_start = str.find(request, "{")
        request_body = request[json_start : ]
        request_json = json.loads(request_body)
        core.setLight(request_json["state"])
    except (ValueError, KeyError) as e:
        print(e)
        utils.send_response(server, "Invalid JSON", 400)


def find_mode(request):
    #request - HTTP request in text
    #add listener to the main.py
    core.activate_find_mode = True
    utils.send_response(server, "OK", 200)


def check_proximity(request):
    print(request)
    try:
        json_start = str.find(request, "{")
        request_body = request[json_start:]
        request_json = json.loads(request_body)

        user_lat = request_json["lat"]
        user_lon = request_json["lon"]

        # Use core’s proximity check
        is_close = core.proximity_check(
            user_lon=user_lon,
            user_lat=user_lat
        )

        response = {"close": is_close}
        utils.send_response(server, json.dumps(response), 200)

    except (ValueError, KeyError) as e:
        print(e)
        utils.send_response(server, "Invalid JSON", 400)


