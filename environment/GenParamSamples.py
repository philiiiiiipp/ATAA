import random
import time

random.seed(time.time())

def sampleP1(num):
	whichGauss = random.random()
	val = -2.0
	while val < -1.0 or val > 1.0:
		if whichGauss < 3.0/6.0:
			val = random.gauss(0.0,0.3)
		elif whichGauss < 4.5/6.0:
			val = random.gauss(-0.7,0.1)
		else:
			val = random.gauss(0.8,0.2)
	return abs(val)
	
def sampleP2(num):
	return random.uniform(0.0,1.0)

def main():
	print "x=[",
	for i in range(10000):
		print "%f,%f ;" % (sampleP1(i),sampleP2(i)) ,
	print "];"
if __name__ == "__main__":
	main()
