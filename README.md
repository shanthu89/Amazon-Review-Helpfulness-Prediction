----------------------------------------------------------------------------------------------------------------------------
# Amazon-Review-Helpfulness-Prediction
----------------------------------------------------------------------------------------------------------------------------
Use the amazon fine food dataset to predict the review helpfulness based on the review score and review length.
In this project, review score and review text length are considered to predict review helpfulness.
Compare computation time and RMSE across Python,R and any Big data framework(used Hadoop).

###Dataset Used:
https://snap.stanford.edu/data/web-FineFoods.html

###Algorithm Used
Linear regression model is used to perform the prediction since there are only two attributes based on which the predictions were to be made.
10 fold cross validation to evaluate validation of model.

####Results
#####Average RMSE across all 10-folds
- R - 0.27399
- Python - 0.273966
- Big data - 0.2739621

#####Computation Time
- R - 379 sec
- Python - 2.1840 sec
- Big data - greater than 140 sec

