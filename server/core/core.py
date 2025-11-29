import time


def configure_settings(enableAutoLigth : bool, autoLigthLevelOn : float, autoLigthLevelOf : float, enableProximityLock : bool, proximityLockMeters : float):
    print("Auto light: {}, On level: {:.2f}, Off level: {:.2f}, Proximity lock: {}, Proximity lock meters: {:.2f}".format(
+        enableAutoLigth, autoLigthLevelOn, autoLigthLevelOf, enableProximityLock, proximityLockMeters))

def get_sensor_data() -> dict:
    timestamp = time.time() # This maybe doesnt work as is should
    return {"timestamp" : timestamp, "ligth_level" : 0.0, "temperature": 0.0, "gps" : (0.0, 0.0), "lock_state" : False, "light_state" : False}

def setLock(state : bool):
    ...

def setLight(state : bool):
    ...

def findMode():
    ...

def getGPSHistory(starttime : int, endtime : int) -> list:
    ...