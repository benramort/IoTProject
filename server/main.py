import network
from machine import Pin
from lib.micropyserver import MicroPyServer
import facade.facade as facade

sta_if = network.WLAN(network.STA_IF)
sta_if.active(True)
sta_if.connect('debian', '12345678')
print(sta_if.isconnected())
print(sta_if.ifconfig()[0])

if (sta_if.isconnected() == False):
    raise Exception("WiFi not connected")

server : MicroPyServer = MicroPyServer()
facade.set_server(server)
server.add_route("/", facade.hello_world)
server.start()


