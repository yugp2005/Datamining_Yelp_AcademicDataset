#load data
load("review_5k_star3_bin_svm_gbm_lvq.RData")

#load library
library(caret)
library(mlbench)
library(lattice)

#train scheme
control <- trainControl(method = "repeatedcv", number = 10, repeats = 3)

#train svm model freq50 on 5k_star3 dataset
set.seed(7)
modelSvmLinearFreq3 <- train(stars~., data = review_5k_star3_binfreq, method = "svmLinear", trControl = control)
print(modelSvmLinearFreq3)


set.seed(7)
modelSvmLinearPres3 <- train(stars~., data = review_5k_star3_binpres, method = "svmLinear", trControl = control)
print(modelSvmLinearPres3)

#save current work
save.image(file = "review_5k_star3_bin_svm_gbm_lvq.RData")

#train LVQ model
set.seed(7)
modelLvqFreq3 <- train(stars~., data = review_5k_star3_binfreq, method = "lvq", trControl = control)
print(modelLvqFreq3)

set.seed(7)
modelLvqPres3 <- train(stars~., data = review_5k_star3_binpres, method = "lvq", trControl = control)
print(modelLvqPres3)

#save current work
save.image(file = "review_5k_star3_bin_svm_gbm_lvq.RData")

#train GBM model
set.seed(7)
modelGbmFreq3 <- train(stars~., data = review_5k_star3_binfreq, method = "gbm", trControl = control, verbose=FALSE)
print(modelGbmFreq3)

set.seed(7)
modelGbmPres3 <- train(stars~., data = review_5k_star3_binpres, method = "gbm", trControl = control, verbose=FALSE)
print(modelGbmPres3)

#save current work
save.image(file = "review_5k_star3_bin_svm_gbm_lvq.RData")

#collect resamples
results3 <- resamples(list(svmFreqL3 = modelSvmLinearFreq3, svmPresL3 = modelSvmLinearPres3, 
                           lvqFreqL3 = modelLvqFreq3, lvqPresL3 = modelLvqPres3,
                           gbmFreqL3 = modelGbmFreq3, gbmPresL3 = modelGbmPres3))

#summrize result
summary(results3)

#boxplots result
bwplot(results3)

#dotplots result
dotplot(results3)

#save current work
save.image(file = "review_5k_star3_bin_svm_gbm_lvq.RData")

#quit
q()


