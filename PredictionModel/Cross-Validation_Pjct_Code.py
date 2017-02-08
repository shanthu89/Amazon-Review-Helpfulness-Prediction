#Importing all the related packages into Python.
import csv
import numpy as np
from sklearn import linear_model
from sklearn import cross_validation
import timeit
start = timeit.default_timer()
#Importing the data into Python for analysis
with open('E:\\Shanth\\Big data\\Project\\Submission\\Data Prep & Data\\Processed_dataset.csv', 'r') as f:
    reader = csv.reader(f)
    data = list(reader)

#Lists to store the dependent and independent variables for Multi-linear regression. 
# y --> Dependent variable (Review_helpfulness)
# X --> Independent variables (Review_Score & Review_length)
y=[]
X=[]

#Extracting data from the imported file into lists X and y created above.
for i in range(len(data)):
    y.append(data[i][0])
    x=(data[i][1:])
    x=list(map(int,x))
    X.append(x)
y=np.array(list(map(float, y)))
X=np.array(X)

#Splitting the dataset into Training and Testing dataset (10-sets) for cross validation.
k_fold = cross_validation.KFold(len(y), n_folds=10, shuffle=False)

#Fitting the Multi-linear model for each of the Train dataset and predicting the corresponding test dataset.
#And calculating the RMSE.
rmse = 0.0
rmseSum = 0.0
for train_indices, test_indices in k_fold:
    print ("TRAIN:", train_indices,'\n' "TEST:", test_indices)
#Fitting the model
    regr = linear_model.LinearRegression()
    regr.fit(X[train_indices], y[train_indices])
#Predicting the test data and calculating the RMSE for each fold.
    rmse = np.sqrt(np.mean((regr.predict(X[test_indices]) - y[test_indices]) ** 2))
    print ("RMSE %lf" % rmse);
    rmseSum += rmse

#Calculating the average RMSE across all the 10-folds.
print ("Averate RMSE: %lf" % (rmseSum/10))
   
#calculate computaion time
stop = timeit.default_timer()
print ("Time taken:",stop - start)
