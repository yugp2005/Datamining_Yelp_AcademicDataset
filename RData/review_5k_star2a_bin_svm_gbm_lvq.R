#load data
load("review_5k_star2a_bin_svm_gbm_lvq.RData")

#load library
library(caret)
library(mlbench)
library(lattice)

#train scheme
control <- trainControl(method = "repeatedcv", number = 10, repeats = 3)

#train svm model freq50 on 5k_star3 dataset
set.seed(7)
modelSvmLinearFreq2a <- train(stars~., data = review_5k_star2a_binfreq, method = "svmLinear", trControl = control)
print(modelSvmLinearFreq2a)


set.seed(7)
modelSvmLinearPres2a <- train(stars~., data = review_5k_star2a_binpres, method = "svmLinear", trControl = control)
print(modelSvmLinearPres2a)

#save current work
save.image(file = "review_5k_star2a_bin_svm_gbm_lvq.RData")

#train LVQ model
set.seed(7)
modelLvqFreq2a <- train(stars~., data = review_5k_star2a_binfreq, method = "lvq", trControl = control)
print(modelLvqFreq2a)

set.seed(7)
modelLvqPres2a <- train(stars~., data = review_5k_star2a_binpres, method = "lvq", trControl = control)
print(modelLvqPres2a)

#save current work
save.image(file = "review_5k_star2a_bin_svm_gbm_lvq.RData")

#train GBM model
set.seed(7)
modelGbmFreq2a <- train(stars~., data = review_5k_star2a_binfreq, method = "gbm", trControl = control, verbose=FALSE)
print(modelGbmFreq2a)

set.seed(7)
modelGbmPres2a <- train(stars~., data = review_5k_star2a_binpres, method = "gbm", trControl = control, verbose=FALSE)
print(modelGbmPres2a)

#save current work
save.image(file = "review_5k_star2a_bin_svm_gbm_lvq.RData")

#collect resamples
results2a <- resamples(list(svmFreq2a = modelSvmLinearFreq2a, svmPres2a = modelSvmLinearPres2a, 
                           lvqFreq2a = modelLvqFreq2a, lvqPres2a = modelLvqPres2a,
                           gbmFreq2a = modelGbmFreq2a, gbmPres2a = modelGbmPres2a))

#summrize result
summary(results2a)

#boxplots result
bwplot(results2a)

#dotplots result
dotplot(results2a)

#save current work
save.image(file = "review_5k_star2a_bin_svm_gbm_lvq.RData")

#quit
q()


