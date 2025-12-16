import json
import math
import core

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

def convert_time_format(date):
    year = date[4:6]
    month = date[2:4]
    day = date[0:2]
    return year + month + day

def load_nmea_data(file_path, date1, date2):
    data_points = []

    with open(file_path, 'r') as f:
        date1 = convert_time_format(date1)
        date2 = convert_time_format(date2)
        for line in f:
            line = line.strip()
            if line.startswith('$GPRMC'):
                parts = line.split(',')
                if len(parts) < 9:
                    continue
                current = convert_time_format(parts[9])
                if current < date1 or current > date2:
                    continue
                time_str = parts[1]        # hhmmss.sss
                lat_str = parts[3]
                ns = parts[4]
                lon_str = parts[5]
                ew = parts[6]
                speed_kmph = float(parts[7])*1.852
                date_str = parts[9]        # ddmmyy

                # Convert time & date to datetime
                dt = f"20{date_str[4:6]}-{date_str[2:4]}-{date_str[0:2]}T{time_str[:2]}:{time_str[2:4]}:{time_str[4:6]}Z"


                # Convert lat/lon to decimal degrees
                lat, lon = parse_lat_lon(lat_str, ns, lon_str, ew)

                data_points.append({
                    'timestamp': dt,
                    'lat': lat,
                    'lon': lon,
                    'speed_kmph': speed_kmph
                })

    return data_points

def get_gps(date1, date2):
    data_points = load_nmea_data('core/nmea_data.nmea', date1, date2)
    print(f"Loaded {len(data_points)} GPS points.")
    
    json_list = []
    for point in data_points:
        payload = {
            'timestamp': point['timestamp'],
            'lat': point['lat'],
            'lon': point['lon'],
            'speed_kmph': round(point['speed_kmph'], 2)
        }
        json_list.append(payload)
    return json.dumps(json_list)

def rad(x):
    return x * 0.017453292519943295  # π/180

def proximity_check(bike_lon, bike_lat, user_lon, user_lat):
    # Earth radius in meters
    R = 6371000  

    # Convert degrees to radians
    phi1 = rad(bike_lat)
    phi2 = rad(user_lat)
    delta_phi = rad(user_lat - bike_lat)
    delta_lambda = rad(user_lon - bike_lon)

    # Haversine formula
    a = math.sin(delta_phi/2)**2 + math.cos(phi1) * math.cos(phi2) * math.sin(delta_lambda/2)**2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    distance = R * c  # distance in meters
    return distance <= 15

def get_current_position():
    return{
        "lat": core.current_lat,
        "lon": core.current_lon
    }
