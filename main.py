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
    serialNumber : str  = None
    
    deviceInfo : DS.Dispensor = None
    user : DS.PersonalStatics = None
    
    isUser = False
    
    db = DBManager()
    
    def __init__(self, serialNumber):
        self.serialNumber = serialNumber
        self.getDeviceInfoFromDb()
        self.getUserStaticsFromDb()
        self.calibrate()
        
        #get deviceinfo and userstatics
    def getDeviceInfoFromDb(self) :
        self.deviceInfo =  self.db.getDeviceStock(self.serialNumber)
        self.user = self.db.getPersonalStatics(self.serialNumber)
        
    def getUserStaticsFromDb(self) : 
        self.user = self.db.getPersonalStatics(self.serialNumber)
    
    def writeDeviceInfoFromLocal(self) :
        self.db.setDeviceInfo(self.deviceInfo)
        stk =  self.deviceInfo.stock
        # print(f"write device info :  {stk[0]}, {stk[1]}, {stk[2]}")
        
    def writeUserStaticsFromLoacl(self):
        self.db.setUserStatics(self.user)
        #consume and write db
    def consumeCoffee(self, deltastock : list ) :
        for i in range(len(self.loads)): #line 수만큼
            self.deviceInfo.stock[i] -= deltaStock[i]
            print(f"after stock : {self.deviceInfo.stock[i]}")
            if(self.user != None):
                self.user.consumeCoffee(self.deviceInfo.coffee[i])
                
        self.writeDeviceInfoFromLocal()
        if(self.user != None):
            self.writeUserStaticsFromLoacl()
            self.db.resetUsingId(self.deviceInfo)
      
    def calibrate(self):
        idx = 0
        for lc in self.loads:
            desired = self.deviceInfo.coffee[idx].weight * self.deviceInfo.stock[idx]
            lc.calibrate(desired)
            idx += 1
            

if __name__ =='__main__' : 

    device = StockChecker(deviceSerialNumber)        
    led = Led(26)                               #led
    press = Pressure(RECENT_SERIAL)             #압력센서

    limit = 0.50                               #무게 신뢰오차
    user : DS.PersonalStatics = None            #user personal statics

    while(True):
        device.getDeviceInfoFromDb()
        
        deltaStock = [0 ,0, 0]  #재고변화량
        isStockChanged = False  #재고변화 존재여부
        
        try :
            device.isUser = int(press.readline()) > 0
            print(f"isUser : {device.isUser}")
           
            #for debug
            device.user.id = "mylandy2"
        except:
            device.isUser = False
        led.turnFromData(device.isUser)
        
        if(device.isUser):
            device.getUserStaticsFromDb()
            #user = db.getPersonalStatics(deviceSerialNumber) #if no one using it, return none
            
        for idx, lc in enumerate(device.loads) : 
            weight = device.deviceInfo.coffee[idx].weight
            desired = weight * device.deviceInfo.stock[idx]
            print(f" [{idx}]desired : {desired}")
            
            now = lc.readGrams_avg(times=16)
            print(f" num {idx} is  now : {now} g")
            
           
            delta =  ((desired) - now)
            print(f"delta : {delta}")
            
            
            exp_amount = int(round(now/float(weight)))

            if( delta > weight * limit ) :
                #limit 
                deltaStock[idx] +=  int( round(delta / float(weight),0) )
                isStockChanged = True
                print(f"num : {idx} dleta amount : {int( delta / weight)}")
                
            if(idx == 2 ):
                print("-------"*30)


        #[비유저 모드 혹은 유저모드 이용 끝] + [재고변화] =  db write
        if ( device.isUser != True and isStockChanged) :  
            print(f"write db")
            device.consumeCoffee(deltaStock)
            # device.isUser = False
        
                    
        #todo:
        '''
        1 .압력센서 디버깅   [v]
        2. dstatics update  [v]
        3. led              [v]
        4. 관바꿀때 캘리 안해도 되는지 테스트하기[]
        
        @. app nfc 1회 버그 고치기
        @@. app  nfc 모듈 동작 백그라운드화
        '''




    




    



    


