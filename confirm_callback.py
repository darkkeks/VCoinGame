#!/usr/bin/env python
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer

import sys

port = int(sys.argv[1])
resp = sys.argv[2]

class Handler(BaseHTTPRequestHandler):
    def do_POST(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()
        self.wfile.write(resp)

httpd = HTTPServer(('', port), Handler)
httpd.serve_forever()