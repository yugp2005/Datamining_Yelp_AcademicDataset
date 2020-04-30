# Text Datamining Yelp Academic Dataset

This is a text data mining project which use yelp review dataset to guess a review's rating from its text alone.

## Getting Started

Yelp connects people to great local businesses. Yelp dataset challenge provide rich set of data to train models. I use the academic dataset to do text mining.
This project is based on Linux (ubuntu) using Hive on Hadoop system to process raw json file to null separated file. Convert null separated file to table separated file (tsv) with program totsv. Then convert tsv text file to binary using ToBinary for text mining. Use R (Rstudio) package to train model to predict a review's rating from its text alone. Details see document
[Datamining_Yelp_AcademicDataset.adoc](./Datamining_Yelp_AcademicDataset.adoc)

### Prerequisites

The following software (system) are need for the project.
- Ubuntu (LST 16.04 or 18.04)
- Hadoop
- Hive
- R, Rstudio

### Installing
- [Install Ubuntu](https://releases.ubuntu.com/)

- [install Hadoop](./Install_Hadoop.adoc)

- [install Hive](./Install_Hive.adoc)

- [Install R and Rstudio](https://rstudio.com/products/rstudio/download/#download)

## Process Data

Run Hadoop
```
Login localhost
$ ssh localhost

Format a new distributed-filesystem
$ hdfs namenode –format

Start Hadoop daemons
$ start-dfs.sh
$ start-yarn.sh

Verification of daemons
$ jps
```

Start hive and do query
```
safemode off
$ hdfs dfsadmin -safemode leave

start hive
$ hive
```

Process json file with Hive. Import [yelp_academic_dataset_review_1000records.json](./sampleData/yelp_academic_dataset_review_1000records.json) file into Hadoop and output null \000 separated file as following
```
hive>CREATE TABLE IF NOT EXISTS table1kreviews(str string);
hive> LOAD DATA LOCAL INPATH '/home/gpyu/yelp/yelpDatasets/yelp_academic_dataset_review_1000records.json' OVERWRITE INTO TABLE table1kreviews;

hive> INSERT OVERWRITE LOCAL DIRECTORY '/home/gpyu/hadoopOUT/' ROW FORMAT DELIMITED FIELDS TERMINATED BY '\000' SELECT GET_JSON_OBJECT(table1kreviews.str, '$.stars'), GET_JSON_OBJECT(table1kreviews.str, '$.user_id'), GET_JSON_OBJECT(table1kreviews.str, '$.review_id'), GET_JSON_OBJECT(table1kreviews.str, '$.date'), GET_JSON_OBJECT(table1kreviews.str, '$.type'), GET_JSON_OBJECT(table1kreviews.str, '$.business_id'), GET_JSON_OBJECT(table1kreviews.str, '$.text') from table1kreviews;
```
output file:[000000_0](./sampleData/000000_0)

Rename '000000_0' to [review_1k_col7_delim_null](./sampleData/review_1k_col7_delim_null)

Compile and Run [totsv](./totsv/totsv.c).
Input file review_1k_col7_delim_null need at same folder with totsv.
```
#compile totsv.c
$ gcc totsv.c -o totsv

#run totsv.
$ ./totsv review_1k_col7_delim_null review_1k_col7.tsv
```

Compile and Run ToBinary (version 0.6.0).
Input data: [review_1k_col7.tsv](./ToBinary\sampleData/input/review_1k_col7.tsv)
```
Input file 'review_1k_col7.tsv' and 'dic_pos_neg' need at same folder with ToBinary.

compile and run ToBinary, command line parameters for ToBinary:
review_1k_col7.tsv: args[0] is raw tsv file
7: args[1] is the number of variable (columns) in raw tsv file
dic_pos_neg: args[2] the positive and negative dictionary

$ javac ToBinary.java

$ java ToBinary review_1k_col7.tsv 7 dic_pos_neg

Start processing:
1. Count the observations in the input file
review_1k_col7.tsv has 1000 observations

2. Clean and tag file , word count: 99.90%

3. Select Features according to the dictionary and wordcount result
Feature frequency (the number of times fi occurs in whole dataset.) is 10
147 features were selected

4. Read clean taged text file and convert to binary: 99.90%

Process finish!


Process finish! Time taken 0.602 Second
```

## Text mining experiment
Install R packages
```
> install.packages("caret")
> install.packages("klaR")
> install.packages("mlbench")
> install.packages("lattice")
> install.packages("nnet")
```
1. Discuss the number of features and the prediction accuracy.
R Command: [review_2k_binfreq_svm.R](./RData/review_2k_binfreq_svm.R)

2. Freq vs pres, Model: svmLinear, lvq and gbm.
R Command: [review_2k_star2_bin_svm_gbm_lvq.R](./RData/review_2k_star2_bin_svm_gbm_lvq.R)

3. Freq vs pres, Model: nnet and nb.
R Command: [review_2k_star2_bin_nb_nnt.R](./RData/review_2k_star2_bin_nb_nnt.R)

4. Evaluate the model stability.
R Command: [review_2k_binfreq_abcd_svm.R](./RData/review_2k_binfreq_abcd_svm.R)

5. Evaluate the model for 5 levels (1-5).
R Command: [review_5k_bin_svm_gbm_lvq.R](./RData/review_5k_bin_svm_gbm_lvq.R)

6. Evaluate the model for 3 levels (star1, star3, start5).
R Command: [review_5k_star3_bin_svm_gbm_lvq.R](./RData/review_5k_star3_bin_svm_gbm_lvq.R)

7. Evaluate the model for 2 combine levels (star1, star5)
R Command: [review_5k_star2a_bin_svm_gbm_lvq.R](./RData/review_5k_star2a_bin_svm_gbm_lvq.R)

8. Evaluate the model combine star3 to star1 or star5
R Command: [review_5k_star2b_bin_svm_gbm_lvq.R](./RData/review_5k_star2b_bin_svm_gbm_lvq.R)

## Summary
This project was designed to solve the Yelp Dataset Challenge first question: How well can you guess a review’s rating from its text alone?

Process raw review Json data to table separated file (tsv) and convert review text to binary input. Implement machine learning algorithms
according to [Pang's paper](https://www.aclweb.org/anthology/W02-1011/).

The result was promising, model was robust and when combine levels to 2 (star1, star5), predict review's rating from its text alone accuracy could reach 80%.

## Deployment
Add later

## Contributing

Add later

## Versioning
TODO. Example from [PurpleBooth] (https://gist.github.com/PurpleBooth/109311bb0361f32d87a2):

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags).

## Authors

* **Guanping Yu** - *Initial work* - [yugp2005](https://github.com/yugp2005)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments
This project was finished with assistance from [Dr. Sunnie Sun Chung](http://cis.csuohio.edu/~sschung/?_ga=2.19094651.271117479.1587669425-2103934368.1586175169)
