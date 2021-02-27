# bayesian_network


Description: This project contains 4 inferencers: Enumeration, RejectionSampling, LikelihoodWeighting, GibbsSampling. You need to give the program xml or bif
files for constructing bayesian network, and you also need to give some 
necessary parameters using command-line. Then, the program will return the 
distribution of the random variable you want to query.



How to Run The Program:

$cd src
$javac bn/inferencer/Main.java   (Although I already included class files, you
can skip this if you did not make any changes)

For inferencer needs a sample: 
$java bn/inferencer/Main [Inferencer] [sample size] [file name] [query] [E1] [e1] [E1] [e1].....

For enumeration:
$java bn/inferencer/Main [Inferencer] [file name] [query] [E1] [e1] [E1] [e1].....


Here are two examples for running the program using inference with and without sampling,respectively.


1.(no sampling)
$java bn/inferencer/Main Enumeration aima-alarm.xml B A true E true J false M true

2.(with sampling)
$java bn/inferencer/Main GibbsSampling 1000 aima-alarm.xml B A true E true J false M true




For your convenience HERE are 4 inferencer names that the program will accept (You can COPY from here):
Enumeration
RejectionSampling
LikelihoodWeighting
GibbsSampling

If something still goes wrongly, you can lookup the code from line35 to line 65 to see what String the program will take.
