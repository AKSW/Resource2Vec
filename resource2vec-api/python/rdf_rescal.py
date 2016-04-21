import logging
import sys
from rdflib import Graph
from scipy.sparse import *
from scipy import *
import numpy as np
import imp
import ConfigParser
import os
import re

"""
Turtle reader to parse a turtle file into a
n*n*m Matrix where 
n = number of distinct resources
m = number of predicates
to use the rescal algorithm for computing the tensor factorization
on the m n*n slices

#todo: write turtle result into file, or make it accessible to java
"""

#read configs
config = ConfigParser.ConfigParser()
config.read("python/rescal.ini")
pathToRescal = config.get("paths","pathToRescal")

pathToFile = sys.argv[1] # config.get("paths","pathToTurtleFile")
targetDir = sys.argv[2] # config.get("paths","pathToTargetDir")

#load rescal algorithm
rescal = imp.load_source('rescal', pathToRescal)

#set logging to basic
logging.basicConfig()

graph = Graph()
#get format from extension
file_extension = os.path.splitext(pathToFile)[1][1:].lower()
if file_extension == "rdf":
#parsing RDF file into data structure
    graph.parse(pathToFile)
else:
    if file_extension == "ttl":
        file_extension = "n3"
    graph.parse(pathToFile,format=file_extension)

if not os.path.exists(targetDir):
    os.mkdir(targetDir)

resources = set()
predicates = set()

#calculate number of distinct resources and predicates by collecting them
for s, p, o in graph:
#if the object is a literal, ignore this triple
    if("http" in o):
        resources.add(s)
        resources.add(o)
        predicates.add(p)

resourcesList = list()
predicatesList = list()

#build iterable,indexalbe lists
f = open(targetDir + '/resources.tsv', 'w')
for r in resources:
    resourcesList.append(r)
    f.write(re.sub(r"\n", " ", re.sub(r"\r", " ", r.n3().encode('utf8')[1:-1])) + "\n")
f.close()
for p in predicates:
    predicatesList.append(p)

#build empty n*n*m matrix 
allData = zeros((size(predicatesList),size(resourcesList),size(resourcesList)))

#fill in allData to have 1's in every cell representing an existing triple
for s,p,o in graph:
    if("http" in o):
        allData[predicatesList.index(p),resourcesList.index(s),resourcesList.index(o)] = 1

#build single slices of the tensor
sliceCollection = list()
for i in range(len(predicatesList)):
    sliceCollection.append(csr_matrix(allData[i]))


#call rescal
A, R, fit, itr, exectimes = rescal.als(sliceCollection,int(sys.argv[3]))

np.savetxt(targetDir + '/vectors.tsv', A, delimiter='\t')

# #collect rescal result for every slice
# rescalResults = list()
# for i in range(len(predicatesList)):
#     rescalResults.append(A.dot(R[i]).dot(A.T))
#
# #build new graph to put found triples
# # for some reason namespace binding is not even needed
# newGraph = Graph()
#
# slicenumber=0
# i=0
# j=0
#
# for slice in rescalResults:
#     for line in slice:
#         for element in line:
#             if allData[slicenumber][i][j] != 1:
#                 if rescalResults[slicenumber][i][j] >= float(sys.argv[1]):
#                     newGraph.add( (resourcesList[i],predicatesList[slicenumber],resourcesList[j]) )
#             j = j+1
#         j=0
#         i = i+1
#     slicenumber = slicenumber+1
#     i=0
#
# print newGraph.serialize(format='nt')

