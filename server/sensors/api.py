import machine 
import time
#import ssd1306

light_state = False
lock_state = False
i2c = None
buzzer_pwm = None
button_1 = None
display = None

def init(light_state_i = False, lock_state_i = False):
    global i2c, buzzer_pwm, light_state, lock_state, button_1, display

    light_state = light_state_i
    lock_state = lock_state_i
    i2c = machine.I2C(0, sda=machine.Pin(12), scl=machine.Pin(13), freq=100000)

    #configure button
    button_1 = machine.Pin(2, machine.Pin.IN, machine.Pin.PULL_UP)
    button_1.irq(trigger=machine.Pin.IRQ_RISING, handler=buttons_callback)

    #configure light level sensor
    #print(i2c.scan())
    #time.sleep_ms(100)
    i2c.writeto_mem(0x10, 0x1000, b'\x00')
    time.sleep_ms(100)

    #configure buzzer
    buzzer_pwm = machine.PWM(machine.Pin(17))
    buzzer_pwm.duty_u16(512)
    
    #configure temp sensor
    get_temperature()

    #configure display
    #print(i2c.scan())
    #display = ssd1306.SSD1306_I2C(128, 64, i2c)
    #display.fill(0)
    #display.show()
    #time.sleep_ms(100)
    #print(i2c.scan())
    return


def buttons_callback(pin):
    if pin == button_1:
        toggle_lights()


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
    if freq == 0:
        buzzer_pwm.duty_u16(0)
    else:
        buzzer_pwm.duty_u16(512)
        try:
            buzzer_pwm.freq(freq)
        except ValueError:
            print(freq)
            play_sound(freq + 1)
    return


def draw_screen():
    display.text('Hello, World!', 0, 0, 1)
    display.show()
    return


def get_lock_state():
    return lock_state


def lock_bike():
    lock_state = True
    machine.Pin(14, machine.Pin.OUT).value(1)
    return


def unlock_bike():
    lock_state = False
    machine.Pin(14, machine.Pin.OUT).value(0)
    return


def get_light_state():
    return light_state


def toggle_lights():
    global light_state
    if light_state:
        turn_lights_off()
    else:
        turn_lights_on()
    return


def turn_lights_on():
    global light_state
    light_state = True
    machine.Pin(18, machine.Pin.OUT).value(0)
    machine.Pin(19, machine.Pin.OUT).value(0)
    machine.Pin(20, machine.Pin.OUT).value(0)
    return


def turn_lights_off():
    global light_state
    light_state = False
    machine.Pin(18, machine.Pin.OUT).value(1)
    machine.Pin(19, machine.Pin.OUT).value(1)
    machine.Pin(20, machine.Pin.OUT).value(1)
    return
