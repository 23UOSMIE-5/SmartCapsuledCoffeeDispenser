import RPi.GPIO as GPIO
from GPIOManager import GpioManager
from SerialManager import SerialManager, RECENT_SERIAL




class Pressure(SerialManager) :

    def __init__(self, serial_name):
        super().__init__(serial_name)
        self.__setup()

    def __setup(self):
        return True
       

if __name__ =='__main__' : 
    pres = Pressure(RECENT_SERIAL)
    while True:
        print(pres.readline())