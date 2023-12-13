import RPi.GPIO as GPIO
from GPIOManager import GpioManager
import time 
class Led(GpioManager):

    __led = None 

    def __init__(self, led):
        super().__init__()
        self.__led =  led
        self.__setup()
        self.turnOff()

    def __setup(self):
        GPIO.setup(self.__led, GPIO.OUT)
    
    def turnOn(self):
        pin = self.__led
        GPIO.output(pin, True)

    def turnOff(self):
        pin = self.__led
        GPIO.output(pin, False)
        
    def turnFromData(self,data):
        if(data > 0 ):
            self.turnOn()
        else:
            self.turnOff()
        
    


if __name__ =='__main__' : 
    led = Led(26)
    while True:
        led.turnOn()
        time.sleep(0.5)
        led.turnOff()
        time.sleep(0.5)
    
