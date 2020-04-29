# Load datasets
load("review_2k_binfreq_svm.RData")

#load library
library(mlbench)
library(caret)

# prepare training scheme
control <- trainControl(method="repeatedcv", number=10, repeats=3)

# train the SVM model freq20
set.seed(7)
modelSvmRadial20 <- train(stars~., data=review_2k_binfreq20, method="svmRadial", trControl=control)

set.seed(7)
modelSvmLinear20 <- train(stars~., data=review_2k_binfreq20, method="svmLinear", trControl=control)


# train the SVM model freq01
set.seed(7)
modelSvmLinear01 <- train(stars~., data=review_2k_binfreq01, method="svmLinear", trControl=control)


# train the SVM model freq04
set.seed(7)
modelSvmLinear04 <- train(stars~., data=review_2k_binfreq04, method="svmLinear", trControl=control)


# collect resamples
results_svmLinear <- resamples(list(freq01=modelSvmLinear01, freq04=modelSvmLinear04, freq20=modelSvmLinear20))

# summarize the distributions
summary(results_svmLinear)


# boxplots of results
bwplot(results_svmLinear)
 
# dot plots of results
dotplot(results_svmLinear)

save.image(file = "review_2k_binfreq_svm.RData")
q()