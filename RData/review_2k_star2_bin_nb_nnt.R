#load data
load("review_2k_star2_bin_nb_nnt.RData")

#load library
library(caret)
library(lattice)
library(klaR)
library(nnet)

#train scheme
control <- trainControl(method = "repeatedcv", number = 10, repeats = 3)

#train naive Bayers model freq20 on 2k_star2 dataset
set.seed(7)
modelNbFreq <- train(stars~., data = review_2k_binfreq, method = "nb", trControl = control)
print(modelNbFreq)

set.seed(7)
modelNbPres <- train(stars~., data = review_2k_binpres, method = "nb", trControl = control)
print(modelNbPres)

#save current work
save.image(file = "review_2k_star2_bin_nb_nnt.RData")

#train Neural Network model
set.seed(7)
modelNnetFreq <- train(stars~., data = review_2k_binfreq, method = "nnet", trControl = control)
print(modelNnetFreq)

set.seed(7)
modelNnetPres <- train(stars~., data = review_2k_binpres, method = "nnet", trControl = control)
print(modelNnetPres)

#save current work
save.image(file = "review_2k_star2_bin_nb_nnt.RData")

#train Neural Networks with Feature Extraction model
set.seed(7)
modelPcaNNetFreq <- train(stars~., data = review_2k_binfreq, method = "pcaNNet", trControl = control)
print(modelPcaNNetFreq)

set.seed(7)
modelPcaNNetPres <- train(stars~., data = review_2k_binpres, method = "pcaNNet", trControl = control)
print(modelPcaNNetPres)

#save current work
save.image(file = "review_2k_star2_bin_nb_nnt.RData")

#collect resamples
results <- resamples(list(nbFreq = modelNbFreq, nbPres = modelNbPres, 
                           nnetFreq = modelNnetFreq, nnetPres = modelNnetPres,
                           PcaNNetFreq = modelPcaNNetFreq, PcaNNetPres = modelPcaNNetPres))

#summrize result
summary(results)

#boxplots result
bwplot(results)

#dotplots result
dotplot(results)

#save current work
save.image(file = "review_2k_star2_bin_nb_nnt.RData")

#quit
q()


