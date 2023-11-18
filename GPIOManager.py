import RPi.GPIO as GPIO
from abc import abstractmethod

class GpioManager :

    def __init__(self):
        GPIO.setmode(GPIO.BCM)
        GPIO.setwarnings (False)

    @abstractmethod
    def __setup(self):
        pass

    def __del__(self):
        GPIO.cleanup()

