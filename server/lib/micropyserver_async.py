import uasyncio as asyncio
import re

class MicroPyServer_async:

    def __init__(self, host="0.0.0.0", port=80):
        self._host = host
        self._port = port
        self._routes = []
        self._on_request_handler = None
        self._on_not_found_handler = None
        self._on_error_handler = None

    # ------------------------------
    # Public API
    # ------------------------------

    def add_route(self, path, handler, method="GET"):
        self._routes.append({"path": path, "handler": handler, "method": method})

    def on_request(self, handler):
        self._on_request_handler = handler

    def on_not_found(self, handler):
        self._on_not_found_handler = handler

    def on_error(self, handler):
        self._on_error_handler = handler

    # ------------------------------
    # Async server core
    # ------------------------------

    async def start(self):
        print("Async HTTP server running on", self._host, self._port)
        server = await asyncio.start_server(self._handle_client,
                                            self._host, self._port)
        async with server:
            await server.serve_forever()

    async def _handle_client(self, reader, writer):
        try:
            request_line = await reader.readline()

            if not request_line:
                await self._close(writer)
                return

            request_line = request_line.decode().strip()

            # Read headers (we ignore them)
            while True:
                line = await reader.readline()
                if line in (b"\r\n", b"", None):
                    break

            # Optional request hook
            if self._on_request_handler:
                if not self._on_request_handler(request_line):
                    await self._close(writer)
                    return

            route = self._find_route(request_line)

            if route:
                await route["handler"](request_line, writer)
            else:
                await self._route_not_found(writer)

        except Exception as e:
            await self._internal_error(e, writer)

        await self._close(writer)

    async def _close(self, writer):
        try:
            await writer.drain()
        except: 
            pass
        try:
            writer.close()
            await writer.wait_closed()
        except:
            pass

    # ------------------------------
    # Routing
    # ------------------------------

    def _find_route(self, request_line):
        m = re.match(r"([A-Z]+)\s+(/[-a-zA-Z0-9_.]*)", request_line)
        if not m:
            return None

        method, path = m.group(1), m.group(2)

        for route in self._routes:
            if method != route["method"]:
                continue
            if path == route["path"]:
                return route
            if re.match("^" + route["path"] + "$", path):
                return route

        return None

    # ------------------------------
    # Default handlers
    # ------------------------------

    async def _route_not_found(self, writer):
        if self._on_not_found_handler:
            await self._on_not_found_handler(writer)
        else:
            await self._send(writer,
                "HTTP/1.0 404 Not Found\r\nContent-Type: text/plain\r\n\r\nNot found")

    async def _internal_error(self, error, writer):
        if self._on_error_handler:
            await self._on_error_handler(error, writer)
        else:
            await self._send(writer,
                "HTTP/1.0 500 Internal Server Error\r\n"
                "Content-Type: text/plain\r\n\r\n"
                f"Error: {error}")

    async def _send(self, writer, text):
        writer.write(text.encode())
        await writer.drain()
