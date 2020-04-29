#load data
load("review_5k_star2b_bin_svm_gbm_lvq.RData")

#load library
library(caret)
library(mlbench)
library(lattice)

#train scheme
control <- trainControl(method = "repeatedcv", number = 10, repeats = 3)

#train svm model freq50 on 5k_star3 dataset
set.seed(7)
modelSvmLinearFreq2b <- train(stars~., data = review_5k_star2b_binfreq, method = "svmLinear", trControl = control)
print(modelSvmLinearFreq2b)


set.seed(7)
modelSvmLinearPres2b <- train(stars~., data = review_5k_star2b_binpres, method = "svmLinear", trControl = control)
print(modelSvmLinearPres2b)

#save current work
save.image(file = "review_5k_star2b_bin_svm_gbm_lvq.RData")

#train LVQ model
set.seed(7)
modelLvqFreq2b <- train(stars~., data = review_5k_star2b_binfreq, method = "lvq", trControl = control)
print(modelLvqFreq2b)

set.seed(7)
modelLvqPres2b <- train(stars~., data = review_5k_star2b_binpres, method = "lvq", trControl = control)
print(modelLvqPres2b)

#save current work
save.image(file = "review_5k_star2b_bin_svm_gbm_lvq.RData")

#train GBM model
set.seed(7)
modelGbmFreq2b <- train(stars~., data = review_5k_star2b_binfreq, method = "gbm", trControl = control, verbose=FALSE)
print(modelGbmFreq2b)

set.seed(7)
modelGbmPres2b <- train(stars~., data = review_5k_star2b_binpres, method = "gbm", trControl = control, verbose=FALSE)
print(modelGbmPres2b)

#save current work
save.image(file = "review_5k_star2b_bin_svm_gbm_lvq.RData")

#collect resamples
results2b <- resamples(list(svmFreq2b = modelSvmLinearFreq2b, svmPres2b = modelSvmLinearPres2b, 
                           lvqFreq2b = modelLvqFreq2b, lvqPres2b = modelLvqPres2b,
                           gbmFreq2b = modelGbmFreq2b, gbmPres2b = modelGbmPres2b))

#summrize result
summary(results2b)

#boxplots result
bwplot(results2b)

#dotplots result
dotplot(results2b)

#save current work
save.image(file = "review_5k_star2b_bin_svm_gbm_lvq.RData")

#quit
q()


