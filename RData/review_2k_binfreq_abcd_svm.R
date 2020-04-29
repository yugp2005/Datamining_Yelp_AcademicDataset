#load data
load("review_2k_binfreq_abcd_svm.RData")

# load the library
library(mlbench)
library(caret)
library(lattice)
# prepare training scheme
control <- trainControl(method="repeatedcv", number=10, repeats=3)

# train the SVM model freq20 on 2ka dataset
set.seed(7)
modelSvmLinear20a <- train(stars~., data=review_2ka_binfreq, method="svmLinear", trControl=control)

# train the SVM model freq20 on 2kb dataset
set.seed(7)
modelSvmLinear20b <- train(stars~., data=review_2kb_binfreq, method="svmLinear", trControl=control)

# train the SVM model freq20 on 2kc dataset
set.seed(7)
modelSvmLinear20c <- train(stars~., data=review_2kc_binfreq, method="svmLinear", trControl=control)

# train the SVM model freq20 on 2kd dataset
set.seed(7)
modelSvmLinear20d <- train(stars~., data=review_2kd_binfreq, method="svmLinear", trControl=control)

# collect resamples
results_svmLinear_abcd <- resamples(list(a_2k=modelSvmLinear20a, b_2k=modelSvmLinear20b,c_2k=modelSvmLinear20c,d_2k=modelSvmLinear20d))
# summarize the distributions
summary(results_svmLinear_abcd)
# boxplots of results
bwplot(results_svmLinear_abcd)
# dot plots of results
dotplot(results_svmLinear_abcd)

save.image(file = "review_2k_binfreq_abcd_svm.RData")
q()





