import time
import api


def screen():
    import ssd1306

    i2c = machine.I2C(0, sda=machine.Pin(12), scl=machine.Pin(13), freq=100000)
    #time.sleep(1)
    print(i2c.scan())
    #i2c.writeto(0x3C, b'\x00\xAE')  # display off
    #i2c.writeto(0x3C, b'\x40\xFF')  # try write data manually
    #i2c.writeto(0x3C, b'\x00\xAE')
    time.sleep(1)
    oled = ssd1306.SSD1306_I2C(128, 64, i2c)
    time.sleep(1)
    #oled.write_cmd(0xAE)
    print(i2c.scan())
    oled.invert(True)
    oled.fill(1)
    oled.show()
    i2c.writeto(0x3C, b'\x00\xAE')       # display off
    i2c.writeto(0x3C, b'\x40\xFF')       # write 1 byte of data
    i2c.writeto(0x3C, b'\x00\xAF')       # display on



api.init()

while True:
    print("Temperature:", api.get_temperature())
    print("Light level:", api.get_light_level())
    time.sleep(3)
