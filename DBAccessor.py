
import DataStructure as DS

class dbAccessor:
    dummy =  3

    def setup(self):
        print("DB access success!")
        return True


class DBManager :
    __db = None

    def __init__(self):
        __db =  dbAccessor()
        while (__db.setup() != True) :
            None
        



    def __del__(self):
        pass

    def getPersonalStatics(self, id) :
        return DS.PersonalStatics()

    def getCoffeeDB(self):
        return DS.CoffeeSet()

    def getDeviceStock(self):
        '''db에서 디바이스정보 읽어와서 개체 만들기'''
        return DS.Dispensor()
    
    def setUserStatics(self, id, statics : DS.PersonalStatics):
        pass

    def setDeviceInfo(self, id, stock : DS.Dispensor):
        #DB에 write
        pass



