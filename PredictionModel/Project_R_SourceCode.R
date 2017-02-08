#Import data into R after processing.
#We choose data for which the number if votes are >=2.
data<-read.csv(file.choose(),header=FALSE)
ptm <- proc.time()
#Fitting model for the Training set using Multi-Linear Regression
#Review_helpfulness --> Dependent variable
#Review_score & Review_textlength --> Independent variables
model.fit <- lm(V1 ~ V2+V3, data)
summary(model.fit)

#We load library DAAG to perform the cross validation.
library("DAAG", lib.loc="~/R/win-library/3.1")

#10-Fold cross validation of dataset
cv_Result<-CVlm(df=data, model.fit, m=10,plotit = TRUE,printit=TRUE)

#calculate RMSE
MSE<-attr(cv_Result, "ms")
RMSE <- sqrt (MSE)
RMSE
#calculate computaion time
timetaken<-proc.time() - ptm
timetaken
