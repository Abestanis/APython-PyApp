#!/usr/bin/env python
# -*- coding: utf-8 -*-

# These are just test Python sources.
print("Hello!")

import sys
print(sys.path)

from time import gmtime, strftime
print('Current time is: ' + strftime("%Y-%m-%d %H:%M:%S", gmtime()))

from util import test
print(test(1))

import this

#from turtledemo import nim
#nim.main()
#nim.turtle.mainloop()

#print('loading turtledemo...')
#from turtledemo.wikipedia import main
#print('Finished loading turtledemo. Starting demo...')
#main()
#print('Finished demo.')

import twistedtest

print('Running twistedtest...')
twistedtest.run()
