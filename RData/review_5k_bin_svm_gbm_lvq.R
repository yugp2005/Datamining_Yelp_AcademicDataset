#load data
load("review_5k_bin_svm_gbm_lvq.RData")

# load the library
library(mlbench)
library(caret)
library(lattice)
# prepare training scheme
control <- trainControl(method="repeatedcv", number=10, repeats=3)

# train the SVM model freq50 on 5k binfreq dataset
set.seed(7)
modelSvmLinearFreq <- train(stars~., data=review_5k_binfreq, method="svmLinear", trControl=control)

# train the SVM model freq50 on 5k binfreq dataset
set.seed(7)
modelSvmLinearPres <- train(stars~., data=review_5k_binpres, method="svmLinear", trControl=control)

# train the LVQ model
set.seed(7)
modelLvqFreq <- train(stars~., data=review_5k_binfreq, method="lvq", trControl=control)

set.seed(7)
modelLvqPres <- train(stars~., data=review_5k_binpres, method="lvq", trControl=control)


# train the GBM model
set.seed(7)
modelGbmFreq <- train(stars~., data=review_5k_binfreq, method="gbm", trControl=control, verbose=FALSE)

set.seed(7)
modelGbmPres <- train(stars~., data=review_5k_binpres, method="gbm", trControl=control, verbose=FALSE)


# collect resamples
results <- resamples(list(svmFreq=modelSvmLinearFreq, svmPres = modelSvmLinearPres, 
                          lvqFreq = modelLvqFreq, lvqPres = modelLvqPres, gbmFreq=modelGbmFreq, gbmPres = modelGbmPres))
# summarize the distributions
summary(results)
# boxplots of results
bwplot(results)
# dot plots of results
dotplot(results)

save.image(file = "review_5k_bin_svm_gbm_lvq.RData")
q()