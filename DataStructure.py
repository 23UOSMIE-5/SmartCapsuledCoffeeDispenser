import datetime

class Coffee :
    name = ""
    weight = 0.0
    caffeine = 0.0
    calories =  0.0

class CoffeeSet:
    coffeeSet = list()

    def make(self,*args):
        for i in args:
            self.coffeeSet.append(i)
class PersonalStatics:
    id = "mylandy2"  #for debug
    date = None
    consumtionOfDay = 0
    caloriesOfDay = 0
    caffeineOfDay = 0

    def consumeCoffee(self, coffee : Coffee, num : int = 1 ) :
        self.date =  datetime.datetime.now().strftime("%Y-%m-%d")
        self.consumtionOfDay += num
        self.caloriesOfDay += coffee.calories * num
        self.caffeineOfDay += coffee.caffeine * num


class Dispensor :
    serialNumber = None
    coffee = [Coffee() , Coffee() , Coffee() ]
    stock  = [0, 0, 0]

if __name__ =='__main__' : 
    cof =  CoffeeSet()

    cof.make(1,2,3,34)
    