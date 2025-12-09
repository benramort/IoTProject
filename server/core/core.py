import time
import sensors.api as api
import uasyncio
import math
from datetime import datetime
import json

_auto_lights_level_on = None
_auto_lights_level_off = None

def configure_settings(enable_auto_ligth : bool, auto_ligth_level_on : float, auto_ligth_level_off : float, enable_proximity_lock : bool, proximity_lock_meters : float):
    print("Auto light: {}, On level: {:.2f}, Off level: {:.2f}, Proximity lock: {}, Proximity lock meters: {:.2f}".format(
        enable_auto_ligths, auto_ligths_level_on, auto_ligths_level_off, enable_proximity_lock, proximity_lock_meters))
    _auto_lights_level_on = auto_lights_level_on
    _auto_lights_level_off = auto_lights_level_off

def get_sensor_data() -> dict:
    timestamp = time.time() # This maybe doesnt work as is should
    ligth_level = api.get_light_level()
    temperature = api.get_temperature()
    ligth_state = api.get_light_state()
    lock_state = api.get_lock_state()
    return {"timestamp" : timestamp, "ligth_level" : ligth_level, "temperature": temperature, "gps" : (0.0, 0.0), "lock_state" : lock_state, "light_state" : ligth_state}


def setLock(state : bool):
    if (state == True):
        api.lock_bike()
    else:
        api.unlock_bike()


def setLight(state : bool):
    if (state == True):
        api.turn_lights_on()
    else:
        api.turn_lights_off()


def auto_lights_check():
    current_level = api.get_light_level()
    if current_level < _auto_lights_level_on:
        api.turn_lights_on()
    elif current_level > _auto_lights_level_off:
        api.turn_lights_off()
    return


wish = [
    (247, 250),
    (349, 250), (349, 125), (392, 125), (349, 125), (330, 125),
    (294, 250), (294, 250), (294, 250),
    (392, 250), (392, 125), (440, 125), (392, 125), (349, 125),
    (330, 250), (330, 250), (330, 250),
    (440, 250), (440, 125), (494, 125), (440, 125), (392, 125),
    (349, 250), (294, 250), (247, 125), (247, 125),
    (294, 250), (392, 250), (330, 250),
    (349, 500)
]
def findMode():
    for freq, duration in wish:
        api.play_sound(freq)
        api.turn_lights_on()
        time.sleep_ms(duration)
        api.play_sound(0)
        api.turn_lights_off()
        time.sleep_ms(int(duration * 0.3))   # 30% spacing

def get_GPS_history(start_time : int, end_time : int) -> list:
    ...
def subroutine():
    while True:
        print("Subroutine running")
        time.sleep(1)