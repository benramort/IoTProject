import time
import sensors.api as api
import uasyncio
import math
from datetime import datetime
import requests
import json

_auto_lights_level_on = 0
_auto_lights_level_off = 0

def configure_settings(enable_auto_ligths : bool, auto_ligths_level_on : float, auto_ligths_level_off : float, enable_proximity_lock : bool, proximity_lock_meters : float):
    print("Auto light: {}, On level: {:.2f}, Off level: {:.2f}, Proximity lock: {}, Proximity lock meters: {:.2f}".format(
        enable_auto_ligths, auto_ligths_level_on, auto_ligths_level_off, enable_proximity_lock, proximity_lock_meters))
    global _auto_lights_level_on, _auto_lights_level_off
    _auto_lights_level_on = auto_ligths_level_on
    _auto_lights_level_off = auto_ligths_level_off

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


def proximity_check(bike_lon, bike_lat, user_lon, user_lat):
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


def parse_lat_lon(lat_str, ns, lon_str, ew):
    lat_deg = int(lat_str[:2])
    lat_min = float(lat_str[2:])
    lat = lat_deg + lat_min / 60
    if ns.upper() == 'S':
        lat = -lat

    lon_deg = int(lon_str[:3])
    lon_min = float(lon_str[3:])
    lon = lon_deg + lon_min / 60
    if ew.upper() == 'W':
        lon = -lon

    return lat, lon


def load_nmea_data(file_path):
    data_points = []

    with open(file_path, 'r') as f:
        for line in f:
            line = line.strip()
            if line.startswith('$GPRMC'):
                parts = line.split(',')
                if len(parts) < 9:
                    continue
                time_str = parts[1]        # hhmmss.sss
                lat_str = parts[3]
                ns = parts[4]
                lon_str = parts[5]
                ew = parts[6]
                speed_kmph = float(parts[7])*1.852
                date_str = parts[9]        # ddmmyy

                # Convert time & date to datetime
                dt = datetime.strptime(date_str + time_str[:6], '%d%m%y%H%M%S')

                # Convert lat/lon to decimal degrees
                lat, lon = parse_lat_lon(lat_str, ns, lon_str, ew)

                data_points.append({
                    'timestamp': dt,
                    'lat': lat,
                    'lon': lon,
                    'speed_kmph': speed_kmph
                })

    return data_points


def send_gps_data(data):
    url = "http://localhost:5000/gps" #has to be actual destination
    headers = {'Content-Type' : 'application/json'}
    response = requests.post(url, headers = headers, data = json.dumps(data))
    print(f"Setn: {json.dumps(data)} | Response: {response.status_code}")


def stream_gps(file_path):
    data_points = load_nmea_data(file_path)
    print(f"Loaded {len(data_points)} GPS points.")

    for point in data_points:
        payload = {
            'timestamp': point['timestamp'].isoformat() + "Z",
            'lat': point['lat'],
            'lon': point['lon'],
            'speed_kmph': round(point['speed_kmph'], 2)
        }
        send_gps_data(payload)


