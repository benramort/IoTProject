import time
import api

api.init()

while True:
    print("Temperature:", api.get_temperature())
    print("Light level:", api.get_light_level())
    api.turn_lights_off()
    api.play_sound(33000)
    time.sleep(1)
    api.turn_lights_on()
    api.play_sound(0)
    time.sleep(1)

