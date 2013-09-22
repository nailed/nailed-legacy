#!/usr/bin/env python

from tornado.web import Application
from tornado.ioloop import IOLoop

from nailed import app, config

if __name__ == '__main__':
    app.listen(config.app_port)
    
    IOLoop.instance().start()
