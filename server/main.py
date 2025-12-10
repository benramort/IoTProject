import time
import network
from machine import Pin
from lib.micropyserver import MicroPyServer
import facade.facade as facade
import sensors.api as api
import _thread
import core.core as core


sta_if = network.WLAN(network.STA_IF)
sta_if.active(True)
print('Connecting to debian')
sta_if.connect('debian', '12345678')
timeout = 10
while timeout > 0 and not sta_if.isconnected():
    timeout -= 1
    print("Connecting...")
    time.sleep(1)
print("Connected:", sta_if.isconnected())
print(sta_if.ifconfig()[0])

if (sta_if.isconnected() == False):
    raise Exception("WiFi not connected")

api.init()

_thread.start_new_thread(core.subroutine, ())

server : MicroPyServer = MicroPyServer()
facade.set_server(server)
server.add_route("/", facade.hello_world)
server.add_route("/settings", facade.configure_settings, "PUT")
server.add_route("/sensors", facade.get_sensor_data, "GET")
server.add_route("/light", facade.set_ligth, "PUT")
server.add_route("/lock", facade.set_lock, "PUT")
server.add_route("/proximityCheck", facade.check_proximity, "PUT")
server.add_route("/getGps", facade.get_gps_history, "PUT")
server.add_route("/findMode", facade.find_mode, "GET")

server.start()





