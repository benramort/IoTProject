import time
import api

api.init()

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



def play(song):
    for freq, duration in song:
        api.play_sound(freq)
        api.turn_lights_on()
        time.sleep_ms(duration)
        api.play_sound(0)
        api.turn_lights_off()
        time.sleep_ms(int(duration * 0.3))   # 30% spacing


def find_mode():
    play(wish)
    counter = 10
    while counter > 0:
        counter -= 1
        api.toggle_lights()
        time.sleep_ms(500)

while True:
    print("Temperature:", api.get_temperature())
    print("Light level:", api.get_light_level())
    #api.turn_lights_off()
    #api.play_sound(33000)
    #api.lock_bike()
    #time.sleep(1)
    #api.turn_lights_on()
    #api.play_sound(0)
    #api.unlock_bike()
    find_mode()
    break
    time.sleep(1)
