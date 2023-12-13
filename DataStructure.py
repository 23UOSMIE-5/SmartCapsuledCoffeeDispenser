import time

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
    id = None
    date = None
    consumtionOfDay = 0
    caloriesOfDay = 0
    caffeineOfDay = 0

    def consumeCoffee(self, coffee : Coffee ) :
        self.date =  time.now()
        self.consumeCoffee += 1
        self.caloriesOfDay += coffee.calories
        self.caffeineOfDay += coffee.caffeine


class Dispensor :
    serialNumber = None
    coffee = [Coffee() , Coffee() , Coffee() ]
    stock  = [0, 0, 0]

    def consumeCoffee(self, idx):
        if(self.stock[idx]> 1) :
            self.stock[idx] -= 1 
            return True
        else:
            print("OverConsumption Called!")
            return False

if __name__ =='__main__' : 
    cof =  CoffeeSet()

    cof.make(1,2,3,34)
    