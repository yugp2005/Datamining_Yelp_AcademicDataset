#load data
load("review_2k_star2_bin_svm_gbm_lvq.RData")

#load library
library(caret)
library(mlbench)
library(lattice)

#train scheme
control <- trainControl(method = "repeatedcv", number = 10, repeats = 3)

#train svm model freq20 on 2k_star2 dataset
set.seed(7)
modelSvmLinearFreq <- train(stars~., data = review_2k_binfreq, method = "svmLinear", trControl = control)
print(modelSvmLinearFreq)

set.seed(7)
modelSvmLinearPres <- train(stars~., data = review_2k_binpres, method = "svmLinear", trControl = control)
print(modelSvmLinearPres)

#save current work
save.image(file = "review_2k_star2_bin_svm_gbm_lvq.RData")

#train LVQ model
set.seed(7)
modelLvqFreq <- train(stars~., data = review_2k_binfreq, method = "lvq", trControl = control)
print(modelLvqFreq)

set.seed(7)
modelLvqPres <- train(stars~., data = review_2k_binpres, method = "lvq", trControl = control)
print(modelLvqPres)

#save current work
save.image(file = "review_2k_star2_bin_svm_gbm_lvq.RData")

#train GBM model
set.seed(7)
modelGbmFreq <- train(stars~., data = review_2k_binfreq, method = "gbm", trControl = control, verbose=FALSE)
print(modelGbmFreq)

set.seed(7)
modelGbmPres <- train(stars~., data = review_2k_binpres, method = "gbm", trControl = control, verbose=FALSE)
print(modelGbmPres)

#save current work
save.image(file = "review_2k_star2_bin_svm_gbm_lvq.RData")

#collect resamples
results <- resamples(list(svmFreq = modelSvmLinearFreq, svmPres = modelSvmLinearPres, 
                           lvqFreq = modelLvqFreq, lvqPres = modelLvqPres,
                           gbmFreq = modelGbmFreq, gbmPres = modelGbmPres))

#summrize result
summary(results)

#boxplots result
bwplot(results)

#dotplots result
dotplot(results)

#save current work
save.image(file = "review_2k_star2_bin_svm_gbm_lvq.RData")

#quit
q()


