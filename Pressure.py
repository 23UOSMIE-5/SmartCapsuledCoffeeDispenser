import RPi.GPIO as GPIO
from GPIOManager import GpioManager

class Pressure(GpioManager):

    __pressure = None

    def __init__(self, pressure : int):
        super().__init__()
        self.__pressure =  pressure
        self.__setup()

    def __setup(self):
        GPIO.setup(self.__pressure, GPIO.IN)
    
    def read(self):
        return GPIO.input(self.__pressure)

if __name__ =='__main__' : 
    led = Pressure(3)
    print("helloo")