
import DataStructure as DS
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
import datetime

class dbAccessor:
    dummy =  3

    def setup(self):
        # Path to your service account key file
        cred = credentials.Certificate('./SmartCapsuledCoffeeDispenser.json')
        # Initialize Firebase Admin SDK
        firebase_admin.initialize_app(cred)
        # Initialize Firestore
        self.db_firebase = firestore.client()
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

    def getPersonalStatics(self, deviceSerialNumber) :
        # deviceSerialNumber에 해당하는 문서에서 UsingID 필드 읽기
        device_doc = self.__db.db_firebase.collection('SerialNumber').document(deviceSerialNumber).get()
        if not device_doc.exists:
            print("No device found with serial number:", deviceSerialNumber)
            return None

        using_id = device_doc.to_dict()['UsingID']

        # 오늘 날짜 설정
        today = datetime.datetime.now().strftime("%Y-%m-%d")

        # UserStatics 컬렉션에서 ID에 해당하는 문서 찾기
        user_stat_doc = self.__db.db_firebase.collection('UserStatics').document(using_id)\
                        .collection('DailyStatics').document(today).get()

        personal_statics = DS.PersonalStatics()
        personal_statics.id = using_id
        personal_statics.date = today

        if not user_stat_doc.exists:
            print("No statistics found for user ID:", using_id, "on date:", today)
            # 오늘 날짜에 해당하는 문서가 없으면 id와 date만 설정된 객체 반환
            return personal_statics

        user_stat_data = user_stat_doc.to_dict()

        # PersonalStatics 객체에 데이터 할당
        personal_statics.consumtionOfDay = user_stat_data.get('Capsules', 0)
        personal_statics.caloriesOfDay = user_stat_data.get('Calories', 0)
        personal_statics.caffeineOfDay = user_stat_data.get('Caffeine', 0)

        return personal_statics

    def getCoffeeDB(self):
        # Fetch all documents from the 'CoffeeDB' collection
        coffee_docs = self.__db.db_firebase.collection('CoffeeDB').stream()

        coffee_set = DS.CoffeeSet()  # Create a new CoffeeSet instance

        for doc in coffee_docs:
            # Convert each document to a dictionary
            coffee_data = doc.to_dict()

            # Create a new Coffee instance with data from the document
            coffee = DS.Coffee()
            coffee.name = coffee_data['CoffeeName']
            coffee.weight = coffee_data['Weight']
            coffee.caffeine = coffee_data['Caffeine']
            coffee.calories = coffee_data['Calories']

            # Use the make method to add the Coffee instance to the CoffeeSet
            coffee_set.make(coffee)

        return coffee_set

    def getDeviceStock(self, deviceSerialNumber):
        # 먼저 모든 커피 데이터를 가져옵니다.
        all_coffee_set = self.getCoffeeDB()

        # Fetch the document for the given device serial number
        device_doc = self.__db.db_firebase.collection('SerialNumber').document(deviceSerialNumber).get()

        # Check if the document exists
        if not device_doc.exists:
            print("No device found with serial number:", deviceSerialNumber)
            return None

        # Extract data from the document
        device_data = device_doc.to_dict()

        # Create a new Dispensor instance
        dispensor = DS.Dispensor()
        dispensor.serialNumber = deviceSerialNumber

        # Use the all_coffee_set to set the coffee and stock information
        for i in range(3):
            coffee_id = device_data[f'#{i+1} Coffee']
            dispensor.coffee[i] = next((coffee for coffee in all_coffee_set.coffeeSet if coffee.name == coffee_id), None)
            dispensor.stock[i] = device_data[f'#{i+1} Coffee Stock']

        return dispensor
    
    def setUserStatics(self, id, statics : DS.PersonalStatics):
        pass

    def setDeviceInfo(self, id, stock : DS.Dispensor):
        #DB에 write
        pass



