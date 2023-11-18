import RPi.GPIO as GPIO
from GPIOManager import GPIOManager

class Led(GPIOManager):

    __led = None 

    def __init__(self, led):
        super().__init__()
        __led =  led
        self.__setup()

    def __setup(self):
        GPIO.setup(self.__led, GPIO.OUT)
    
    def turnOn(self):
        pin = self.__led
        GPIO.output(pin, True)

    def turnOff(self):
        pin = self.__led
        GPIO.output(pin, False)


