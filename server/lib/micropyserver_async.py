# ...existing code...
import re
import socket
import sys
import io
import uasyncio as asyncio

class MicroPyServer_async(object):
    def __init__(self, host="0.0.0.0", port=80):
        self._host = host
        self._port = port
        self._routes = []
        self._on_request_handler = None
        self._on_not_found_handler = None
        self._on_error_handler = None
        self._server = None
        self._connect = None  # holds current StreamWriter during handler

    def add_route(self, path, handler, method="GET"):
        self._routes.append({"path": path, "handler": handler, "method": method})

    def on_request(self, handler):
        self._on_request_handler = handler

    def on_not_found(self, handler):
        self._on_not_found_handler = handler

    def on_error(self, handler):
        self._on_error_handler = handler

    async def start_async(self):
        """Start async server; returns a coroutine that keeps running."""
        async def _handle_client(reader, writer):
            try:
                data = await reader.read(4096)
                if not data:
                    try:
                        writer.close()
                    except Exception:
                        pass
                    return
                try:
                    request = data.decode("utf-8", "ignore")
                except Exception:
                    request = str(data)

                # expose current writer for server.send()
                self._connect = writer

                # optional on_request hook
                if self._on_request_handler:
                    try:
                        peer = None
                        try:
                            peer = writer.get_extra_info("peername")
                        except Exception:
                            peer = None
                        try:
                            self._on_request_handler(request, peer)
                        except TypeError:
                            self._on_request_handler(request)
                    except Exception as e:
                        # call error handler but continue
                        if self._on_error_handler:
                            try:
                                self._on_error_handler(e)
                            except Exception:
                                pass

                # route dispatch
                route = self.find_route(request)
                if route:
                    try:
                        route["handler"](request)
                    except Exception as e:
                        self._internal_error(e)
                else:
                    self._route_not_found(request)

                # ensure buffered data is sent
                try:
                    await writer.drain()
                except Exception:
                    pass
            except Exception as e:
                self._internal_error(e)
            finally:
                self._connect = None
                try:
                    writer.close()
                except Exception:
                    pass

        try:
            self._server = await asyncio.start_server(_handle_client, self._host, self._port)
        except Exception as e:
            print("start_async error:", e)
            return

        # keep coroutine alive while server runs
        try:
            while True:
                await asyncio.sleep(60)
        finally:
            # attempt graceful shutdown
            try:
                self._server.close()
            except Exception:
                pass

    def stop(self):
        """Stop server (best-effort)."""
        try:
            if self._server:
                try:
                    self._server.close()
                except Exception:
                    pass
                self._server = None
        except Exception:
            pass

    def send(self, data):
        """Send data to current client. Accepts bytes or str."""
        if self._connect is None:
            raise Exception("No active connection to send to")
        payload = data if isinstance(data, (bytes, bytearray)) else str(data).encode("utf-8")
        try:
            # StreamWriter API
            if hasattr(self._connect, "write"):
                try:
                    self._connect.write(payload)
                except Exception as e:
                    # some ports expect string write
                    try:
                        self._connect.write(payload.decode("utf-8"))
                    except Exception:
                        print("writer.write error:", e)
            else:
                # fallback socket style
                try:
                    self._connect.sendall(payload)
                except Exception:
                    try:
                        self._connect.send(payload)
                    except Exception as e:
                        print("socket send error:", e)
        except Exception as e:
            print("send error:", e)

    def find_route(self, request):
        """Parse request line and match registered routes. Supports exact and simple regex routes."""
        lines = request.split("\r\n")
        if not lines or not lines[0]:
            return None
        m_method = re.search(r"^([A-Z]+)", lines[0])
        m_path = re.search(r"^[A-Z]+\s+([^\s?]+)", lines[0])
        if not m_method or not m_path:
            return None
        method = m_method.group(1)
        path = m_path.group(1)
        # normalize trailing slash
        if path != "/" and path.endswith("/"):
            path = path[:-1]
        for route in self._routes:
            if method != route["method"]:
                continue
            route_path = route["path"]
            if route_path != "/" and route_path.endswith("/"):
                route_path = route_path[:-1]
            if route_path == path:
                return route
            else:
                try:
                    match = re.search("^" + route_path + "$", path)
                    if match:
                        return route
                except Exception:
                    # ignore bad regex in route path and continue
                    continue
        return None

    def _route_not_found(self, request):
        if self._on_not_found_handler:
            try:
                self._on_not_found_handler(request)
                return
            except Exception as e:
                self._internal_error(e)
                return
        # default 404
        try:
            self.send("HTTP/1.0 404 Not Found\r\n")
            self.send("Content-Type: text/plain\r\n\r\n")
            self.send("Not found")
        except Exception:
            pass

    def _internal_error(self, error):
        if self._on_error_handler:
            try:
                self._on_error_handler(error)
                return
            except Exception:
                pass
        # default 500
        try:
            # safe stringify
            if "print_exception" in dir(sys):
                out = io.StringIO()
                sys.print_exception(error, out)
                err_str = out.getvalue()
                out.close()
            else:
                err_str = str(error)
            self.send("HTTP/1.0 500 Internal Server Error\r\n")
            self.send("Content-Type: text/plain\r\n\r\n")
            self.send("Error: " + err_str)
            print(err_str)
        except Exception:
            pass