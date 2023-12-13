import sys
import time
from Led import Led
from Loadcell import Loadcell
from Pressure import Pressure
import DataStructure as DS
from DBAccessor import DBManager
from SerialManager import RECENT_SERIAL

deviceSerialNumber = '360464004024' + '51229'

class StockChecker :
    loads = [Loadcell(17,21,gain=128,grad=3749.812,offset=8260639.978), 
                Loadcell(20,27,gain=128,grad=4719.77,offset=8700255.42),
                Loadcell(16,22,gain=128,grad=3770.89,offset=8786543.75) ]

    deviceInfo : DS.Dispensor = None

    def setDeviceInfo(self, db : DBManager) :
        self.deviceInfo =  db.getDeviceStock(deviceSerialNumber)

    def calibrate(self):
        idx = 0
        for lc in self.loads:
            desired = self.deviceInfo.coffee[idx].weight * self.deviceInfo.stock[idx]
            lc.calibrate(desired)
            idx += 1
   

if __name__ =='__main__' : 

    db =  DBManager()
    device = StockChecker()             #3무게센서
    led = Led(11)                       #led
    press = Pressure(RECENT_SERIAL)     #압력센서

    device.setDeviceInfo(db)
    device.calibrate()

    limit = 0.05 #무게 신뢰오차

    while(True):
        idx = 0
        deltaStock = [0 ,0, 0]  #재고변화량
        for lc in device.loads : 
            weight = device.deviceInfo.coffee[idx].weight

            desired = weight * device.deviceInfo.stock[idx]
            now = lc.readGrams_avg(times=16)

            delta =  abs(desired - now)

            if( delta > weight * limit ) :
                #test  필요, 정확도가 얼마나 나올지..
                deltaStock[idx] +=  int( delta / weight )

            idx += 1
        
        if ( int(press.readline())  == 0 ) :  #비유저 모드 혹은 유저모드 이용 끝 (db write)
            new =  device.deviceInfo
            for i, delta in new.stock , deltaStock :
                i -= delta
            db.setDeviceInfo(new)




    




    



    


