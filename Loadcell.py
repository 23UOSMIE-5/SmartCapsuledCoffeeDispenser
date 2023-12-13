from GPIOManager import GpioManager
import RPi.GPIO as GPIO
from hx711py import hx711



class Loadcell(GpioManager):

    __hx : hx711.HX711 = None
    __reverseGradient = None
    __offset = None
    __error = 0

    def __init__(self, dout, pd_sck, gain=128, grad = 1.0, offset = 0.0):
        super().__init__()
        self.__hx = hx711.HX711(dout,pd_sck,gain=gain)
        self.__reverseGradient = grad
        self.__offset = offset
        self.__setup()

    def calibrate(self,desired, times=16):
        #desired로 현재 무게를 cali
        self.__error = self.readGrams_avg(times) - desired

    def __setup(self):
        scale_ready = False
        while not scale_ready:
            if (GPIO.input(self.__hx.DOUT) == 0):
                scale_ready = False
            if (GPIO.input(self.__hx.DOUT) == 1):
                print("Initialization complete!")
                scale_ready = True

    def readGrams_avg(self, times=16)-> float : 
        x = self.__hx.read_average(times=times)
        return float((x-self.__offset)/self.__reverseGradient - int(self.__error))


if __name__ =='__main__' : 
    lc = Loadcell(5, 6, 128, -0.025 , 2200)
    print("helloo")