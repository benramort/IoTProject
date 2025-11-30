import machine 

light_state = False
i2c = None
r_pwm = g_pwm = b_pwm = None
buzzer_pwm = None

def init():
    global i2c, r_pwm, g_pwm, b_pwm, buzzer_pwm
    i2c = machine.I2C(0, sda=machine.Pin(12), scl=machine.Pin(13))
    #configure light level sensor
    i2c.writeto_mem(0x10, 0x1000, b'\x00')
    #configure buzzer
    buzzer_pwm = machine.PWM(machine.Pin(17))
    buzzer_pwm.freq(4000)
    #configure RGB led
    r_pwm = machine.PWM(machine.Pin(18))
    r_pwm.freq(19000 // 4)
    g_pwm = machine.PWM(machine.Pin(19))
    g_pwm.freq(19000 // 4)
    b_pwm = machine.PWM(machine.Pin(20))
    b_pwm.freq(19000 // 4)
    return


def get_temperature():
    """
    Use HDC2021 sensor, which has I2C address = 0x40. 
    Temperature data is located at 0x00 and 0x01 in the sensor
    """ 
    i2c.writeto_mem(0x40, 0x0F, b'\x01')
    data = i2c.readfrom_mem(0x40, 0x00, 2)
    temp_raw = (data[1] << 8) | data[0] 
    return (temp_raw / 65536) * 165 - 40


def get_light_level():
    """
    Use VEML6030 sensor, which has I2C address = 0x10
    """
    data = i2c.readfrom_mem(0x10, 0x04, 2)
    lux = int.from_bytes(data, 'little') * 0.5376
    if lux > 1000:
        lux = pow(lux, 4)*6.0135e-13 + pow(lux, 3)*-9.3924e-9 + pow(lux, 2)*8.1488e-5 + lux*1.0023
    return lux


def play_sound(freq):
    buzzer_pwm.duty_u16(freq)
    return


def draw_screen():
    return


def get_lock_state():
    return False


def lock_bike():
    return


def unlock_bike():
    return


def get_light_state():
    return light_state


def set_rgb_value(r, g, b):
    r_pwm.duty_u16((255-r)**2)
    g_pwm.duty_u16((255-g)**2)
    b_pwm.duty_u16((255-b)**2)
    return


def turn_lights_on():
    global light_state
    light_state = True
    set_rgb_value(255, 255, 255)
    return


def turn_lights_off():
    global light_state
    light_state = False
    set_rgb_value(0, 0, 0)
    return


def get_gps():
    return
