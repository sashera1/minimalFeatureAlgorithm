# minimalFeatureAlgorithm
Optimized Algorithm for a phonological problem.
Input into main is the path to the csv file of the format:
,featureName, featureName...,featureName  
phonemeName,X1,X2...Xn,  
phonemeName,Y1,Y2...Yn  
...  
Xa, Ya, etc being '-' or '+'


sample csv:  
,a,e,o,i,u  
front,-,+,-,+,-  
back,-,-,+,-,+  
high,-,-,-,+,+  
low,+,-,-,-,-  
rounded,-,-,+,-,+  


