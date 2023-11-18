from GPIOManager import GpioManager
import RPi.GPIO as GPIO
from hx711py import hx711



class Loadcell(GpioManager):

    __hx : hx711.HX711 = None
    __lineGradient = None
    __error = None

    def __init__(self, dout, pd_sck, gain=128, grad = 1, error = 0):
        super().__init__()

        self.__hx = hx711.HX711(dout, pd_sck, gain=128)
        self.__lineGradient = grad
        self.__error = error

        self.__setup()

    def __setup(self):
        print("Initializing.\n Please ensure that the scale is empty.")
        scale_ready = False
        while not scale_ready:
            if (GPIO.input(self.__hx.DOUT) == 0):
                scale_ready = False
            if (GPIO.input(self.__hx.DOUT) == 1):
                print("Initialization complete!")
                scale_ready = True

    def readGrams_avg(self, times=16):
        x = self.__hx.read_average(times=16)
        return x* self.__lineGradient + self.__error


if __name__ =='__main__' : 
    lc = Loadcell(5, 6, 128, -0.025 , 2200)
    print("helloo")