import time
import sensors.api as api
import uasyncio


def configure_settings(enableAutoLigth : bool, autoLigthLevelOn : float, autoLigthLevelOf : float, enableProximityLock : bool, proximityLockMeters : float):
    print("Auto light: {}, On level: {:.2f}, Off level: {:.2f}, Proximity lock: {}, Proximity lock meters: {:.2f}".format(
        enableAutoLigth, autoLigthLevelOn, autoLigthLevelOf, enableProximityLock, proximityLockMeters))

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

def findMode():
    ...

def getGPSHistory(starttime : int, endtime : int) -> list:
    ...

def proximityCheck():
    ...


def subroutine():
    while True:
        print("Subroutine running")
        time.sleep(1)
