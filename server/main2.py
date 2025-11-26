import network
from machine import Pin

def generate_html(status):
    html = f"""\
    HTTP/1.1 200 OK
    Content-Type: text/html

    <!DOCTYPE html>
    <html>
    <head><title>Raspberry Pi Pico Web Server</title></head>
    <body>
    <h1>TOGGLE LED</h1>
    <h2>LED is now {status}</h2>
    <p><a href='/toggle'><button style="background-color: #ed9418; padding: 20px; font-
    size:20px">Toggle</button></a></p>
    </body>
    </html>
    """
    return str(html)

led = Pin("LED", Pin.OUT)
led.value(0)
led_status = "OFF"

sta_if = network.WLAN(network.STA_IF)
sta_if.active(True)
sta_if.connect('debian', '12345678')
print(sta_if.isconnected())
print(sta_if.ifconfig()[0])

if (sta_if.isconnected()):
    import socket

    addr = socket.getaddrinfo('0.0.0.0', 80)[0][-1]

    s = socket.socket()
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    s.bind(addr)
    s.listen(1)

    print("Listening on: ", addr)

    while True:
        cl, addr = s.accept()
        print('client connected from', addr)
        req_bytes = cl.recv(1024)
        request = req_bytes.decode('utf-8')
        try:
            request = request.split('\r\n')[0]
            request = request.split(' ')[1]
            if (request == '/toggle'):
                print('Toggling LED')
                if (led.value() == 0):
                    led.value(1)
                    led_status = "ON"
                else:
                    led.value(0)
                    led_status = "OFF"
        except IndexError:
            pass
        
        # print('Request:', request)
        response = generate_html(led_status)
        cl.send(response)
        cl.close()