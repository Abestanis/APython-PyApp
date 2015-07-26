# These are just test Python sources.
print("Hello!")

import sys
print(sys.path)

from time import gmtime, strftime
print('Current time is: ' + strftime("%Y-%m-%d %H:%M:%S", gmtime()))

from util import test
print(test(1))

import this

import twistedtest

print('Running twistedtest...')
twistedtest.run()
