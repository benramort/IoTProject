import network
from machine import Pin
from lib.micropyserver_async import MicroPyServer_async
import facade.facade as facade
import sensors.api as api
import _thread
import core.core as core
import uasyncio

async def main():
    uasyncio.create_task(server.start())
    uasyncio.create_task(core.subroutine())
    await uasyncio.sleep(200)



sta_if = network.WLAN(network.STA_IF)
sta_if.active(True)
sta_if.connect('debian', '12345678')
print(sta_if.isconnected())
print(sta_if.ifconfig()[0])

if (sta_if.isconnected() == False):
    raise Exception("WiFi not connected")

# api.init()

server : MicroPyServer_async = MicroPyServer_async()
facade.set_server(server)
server.add_route("/", facade.hello_world)
server.add_route("/settings", facade.configure_settings, "PUT")
server.add_route("/sensors", facade.get_sensor_data, "GET")
server.add_route("/ligth", facade.set_ligth, "PUT")

uasyncio.run(main())





