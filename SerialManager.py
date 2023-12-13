import serial

RECENT_SERIAL = "/dev/serial/by-id/usb-Arduino__www.arduino.cc__0043_85138313034351403151-if00"

class SerialManager :

    __serial = None

    def __init__(self, serial_name):
        self.__serial = serial.Serial(serial_name, 9600, timeout=1 )
        self.__serial.reset_input_buffer()

    def __setup(self):
        pass

    def __del__(self):
        return


    def readline(self) -> str :
        line = None
        self.__serial.reset_input_buffer()
        while( line == None):
            if(self.__serial.in_waiting > 0 ):
                line = self.__serial.readline().decode('utf-8').rstrip()
                self.__serial.reset_input_buffer()
        return line


if __name__ == '__main__':
    ser = SerialManager(RECENT_SERIAL)

    while True:
        print(ser.readline())
        