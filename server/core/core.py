import time
import sensors.api as api
import uasyncio
import math


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

def proximityCheck(bike_lon, bike_lat, user_lon, user_lat):
    # Earth radius in meters
    R = 6371000  

    # Convert degrees to radians
    phi1 = math.radians(bike_lat)
    phi2 = math.radians(user_lat)
    delta_phi = math.radians(user_lat - bike_lat)
    delta_lambda = math.radians(user_lon - bike_lon)

    # Haversine formula
    a = math.sin(delta_phi/2)**2 + math.cos(phi1) * math.cos(phi2) * math.sin(delta_lambda/2)**2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    distance = R * c  # distance in meters

    # Return True if within 15 meters, otherwise False
    return distance <= 15


def subroutine():
    while True:
        print("Subroutine running")
        time.sleep(1)
