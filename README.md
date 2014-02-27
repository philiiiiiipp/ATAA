#ATAA

## States

A state vector has twelve entries:

```
u		velocity north 	[-5,5]
v		velocity east
w		velocity down
x 		position north	[-20,20]
y 		position east
z 		position down
p 		angular rate around forward axis	[-12.566,12.566]
q 		angular rate around sideways axis
r 		angular rate around vertical axis
phi 	quaternion (rotation) (roll) 		[-1,1]
theta 	quaternion (rotation) (pitch)
omega 	quaternion (rotation) (yaw)
```

## Actions

An action vector has four entries:

```
a1		longitudal cyclic path (aileron) 	[-1,1]
a2 		latitudal cyclic path (elevator)
a3 		tail rotor collective pitch (rudder)
a4 		main rotor collective pitch
```

## MDPs

- we have a uniform distribution over the set of all MDPs
- an MDP (or: task) is defined by two sinusoidal wind waves,
  one along the north-south axis, the other along the east-west axis

A sinusoidal wind wave is specified by four parameters:

```
amp 	[0,1] 	maximum force (velocity) wind will exert
freq 	[0,1] 	number of cycles per second
phase 	[0,1] 	a fraction of the wave period
center 	[0,1] 	center amplitude of the sine wave
```

- the above numbers are given to Helicopter.java as numbers in the interval [0,1], but the helicopter (environment) will scale them to different intervals itself
- wind changes over time
- at each timestep ts, the wind is given by (note that this formula is from shimon's paper, haven't looked for it in the code since we don't have to care; also, it assumes that the intervals are already scaled to the correct ones, not in [0,1])

```
wind(ts+1) = amp * sin( freq * (ts/100) * phase ) + center
```

- (!!!) should check if uniform (like shimon says in his paper) is true; look into GenParamSamples.py; for the wind laong the north-south-axis it seems to be different:

```
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
```
