/** ToBinary.java
*AUTHOR: GUANPING YU
*DATE: 2020-04-28
*VERSION: 0.6.0
*@update: feature with the number of times occurs in total document > 1% number of observations can be selected
*GOAL: clean the file,count the word, add negation not_ tag and count the word again
*      Select features by intersection wordcount and dictionary
*	   transfer text to binary using freqency of the features.
*
*clean file: replace punctuation with space (not include single quote '')
Dec	Hex	Binary	  Char  Description
33	21	00100001	!	exclamation mark
34	22	00100010	"	double quote
35	23	00100011	#	number
36	24	00100100	$	dollar
37	25	00100101	%	percent
38	26	00100110	&	ampersand
40	28	00101000	(	left parenthesis
41	29	00101001	)	right parenthesis
42	2A	00101010	*	asterisk
43	2B	00101011	+	plus
44	2C	00101100	,	comma
45	2D	00101101	-	minus
46	2E	00101110	.	period
47	2F	00101111	/	slash
58	3A	00111010	:	colon
59	3B	00111011	;	semicolon
60	3C	00111100	<	less than
61	3D	00111101	=	equality sign
62	3E	00111110	>	greater than
63	3F	00111111	?	question mark
64	40	01000000	@	at sign
91	5B	01011011	[	left square bracket
92	5C	01011100	\	backslash
93	5D	01011101	]	right square bracket
94	5E	01011110	^	caret / circumflex
95	5F	01011111	_	underscore
96	60	01100000	`	grave / accent
*
*Tag the negation: add not_ to every word between a negation word
("not", isn't, didn't, etc)

* TODO Handle unicode spanish char
*/

import java.io.IOException;
import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Iterator;



public class ToBinary{

	public static void main(String[] args) throws IOException{

		try{
			/*Command-Line Arguments
			args[0] the raw (tsv) file, 
			args[1] the number of variable 
			args[2] the positive and negative dictionary
			*/
			
			//TODO handle args exception: args.length > 0, args[1] is int
			String rawFile = args[0];			
			int numVar = Integer.parseInt(args[1]);				
			String dicPosNeg = args[2];	
			
			int numFreq; //the frequency of selected features			

			//Start timer for the task
			long startTime = System.nanoTime();

			FileReader frRaw = new FileReader(rawFile);
			BufferedReader brRaw = new BufferedReader(frRaw);
			
			//count the lines in input file
			System.out.print("\nStart processing: \n1. Count the observations in the input file");
			int numLines = 0; // number of observations (rows)
			while((brRaw.readLine()) != null){
				numLines++;
			}

			System.out.printf("\n%s has %d observations\n\n", rawFile, numLines);

			/*output file: rawFile_clean_tag*/

			String cleanTagFile = rawFile + "_clean_tag";
			FileWriter fwCleanTag = new FileWriter(cleanTagFile);
			BufferedWriter bwCleanTag = new BufferedWriter(fwCleanTag);

			/*hashmap for wordcount and wordcount with tag*/
			HashMap<String, Integer> hmwcTag = new HashMap<>();

			/*reopen the raw file and do the process*/
			frRaw = new FileReader(rawFile);
			brRaw = new BufferedReader(frRaw);

			int leftLines = numLines;
			while(leftLines > 0){
				//status
				float status = (float)(numLines - leftLines)/numLines *100;
				System.out.printf("2. Clean and tag file , word count: %.2f%%\r", status);
				leftLines--;

				//the text column of the observation
				String obsRaw = brRaw.readLine();
				String[] tokensRaw = obsRaw.split("\t");
				String obsRawText = tokensRaw[tokensRaw.length -1];

				/*write clean text with or without tag to new clean file and 
				  clean_tag file, separately*/
				String obsRawTextClean = cleanText(obsRawText);
				String obsRawTextCleanTag = tagText(obsRawTextClean);
				
				for (int i = 0; i < tokensRaw.length; i++){

					//table separated values(TSV)
					if(i == tokensRaw.length -1){
						bwCleanTag.write(obsRawTextCleanTag);
						bwCleanTag.write("\n");
					}
					else{
						bwCleanTag.write(tokensRaw[i]);
						bwCleanTag.write("\t");
					}					
				}//end for

				/*wordcount for clean text with tag*/
				String[] tokensCleanTag = obsRawTextCleanTag.split(" +");
				for(int i = 0; i < tokensCleanTag.length; i++){
					String wordCleanTag = tokensCleanTag[i];

					if(!hmwcTag.containsKey(wordCleanTag)){
						hmwcTag.put(wordCleanTag, 1);
					}
					else{
						hmwcTag.put(wordCleanTag, hmwcTag.get(wordCleanTag) + 1);
					}
				}//end for
				
			}//end while

			//close bwClean and bwCleanTag
			bwCleanTag.close();

			/*write file: rawFile_clean_tag_wordCount*/
			String cleanFileWCTag = rawFile + "_wordCount";
			FileWriter fwCleanWCTag = new FileWriter(cleanFileWCTag);
			BufferedWriter bwCleanWCTag = new BufferedWriter(fwCleanWCTag);

			for(Map.Entry<String, Integer> entry : hmwcTag.entrySet()){
				bwCleanWCTag.write(entry.getKey());
				bwCleanWCTag.write("\t");
				bwCleanWCTag.write(Integer.toString(entry.getValue()));
				bwCleanWCTag.write("\n");
			}

			//close bwCleanWCTag
			bwCleanWCTag.close();
			System.out.println();

			/* ========================================================== */
			/*Select Features according to the dictionary*/
			/*http://www.cs.uic.edu/~liub/FBS/sentiment-analysis.html*/
			/*============================================================ */
			System.out.println("\n3. Select Features according to the dictionary and wordcount result");

			//String dicPosNeg = "dic_pos_neg";
			String featuresTag = rawFile + "_features";

			//dictionary for the positive and negtive words
			FileReader frDic = new FileReader(dicPosNeg);
			BufferedReader brDic = new BufferedReader(frDic);

			//file for the selected features with tag
			FileWriter fwFeaturesTag = new FileWriter(featuresTag);
			BufferedWriter bwFeaturesTag = new BufferedWriter(fwFeaturesTag);

			String feature = null;
			String featureTag = null;
			int numFeatures = 0;
			numFreq = numLines/100;//the frequency of selected features			
			
			while((feature = brDic.readLine()) != null){
				featureTag = "not_" + feature; 

				//the freq need large than 4
				if(hmwcTag.containsKey(feature) && hmwcTag.get(feature) >= numFreq){
					bwFeaturesTag.write(feature + "\n");
					numFeatures++;
				}
				else if(hmwcTag.containsKey(featureTag) && hmwcTag.get(featureTag) >= numFreq){
					bwFeaturesTag.write(featureTag + "\n");
					numFeatures++;
				}
			}
			
			System.out.printf("Feature frequency (the number of times fi occurs in whole dataset.) is %d \n", numFreq);

			System.out.printf("%d features were selected\n\n", numFeatures);

			//close bwFeaturesTag
			bwFeaturesTag.close();

			/* ========================================================== */
			/*convert text to binary: frequency (freq) and presence(pres)*/
			/*text: *_clean_tag 
			/*features: *_clean_features_tag*/
			/*binary: *_clean_binary_tag*/
			/*============================================================ */		

			/*binary text file freq and pres*/
			String cleanBinFreqTagFile = rawFile + "_freq_binary";
			FileWriter fwCleanBinFreqTag = new FileWriter(cleanBinFreqTagFile);
			BufferedWriter bwCleanBinFreqTag = new BufferedWriter(fwCleanBinFreqTag);

			String cleanBinPresTagFile = rawFile + "_pres_binary";
			FileWriter fwCleanBinPresTag = new FileWriter(cleanBinPresTagFile);
			BufferedWriter bwCleanBinPresTag = new BufferedWriter(fwCleanBinPresTag);

			/*read features to an array*/
			FileReader frFeaturesTag = new FileReader(featuresTag);
			BufferedReader brFeaturesTag = new BufferedReader(frFeaturesTag);

			String[] setOfFeatures = new String[numFeatures];

			for(int i = 0; i < numFeatures; i++){
				setOfFeatures[i] = brFeaturesTag.readLine();
				//System.out.println(setOfFeatures[i]);
			}

			/*read text file and convert to binary*/
			FileReader frCleanTag = new FileReader(cleanTagFile);
			BufferedReader brCleanTag = new BufferedReader(frCleanTag);

			String obsRawCleanTag = null;


			leftLines = numLines;
			while(leftLines > 0){
				//status
				float status = (float)(numLines - leftLines)/numLines *100;
				System.out.printf("4. Read clean taged text file and convert to binary: %.2f%%\r", status);
				leftLines--;

				obsRawCleanTag = brCleanTag.readLine();
				String[] tokens = obsRawCleanTag.split("\t");

				//hashmap stores wordcount result for each observation text
			    HashMap<String, Integer> hmwcTagObs = new HashMap<>();

				//last column is text
				hmwcTagObs = wordCount(tokens[tokens.length -1]);

				//string for the frequency of featurs
			    StringBuffer textBinFreqToStr = new StringBuffer();

			    //string for the presence of featurs
			    StringBuffer textBinPresToStr = new StringBuffer();

				for(int i = 0; i < numFeatures; i++){
					feature = setOfFeatures[i];
					int freq = 0;
					int pres = 0;

					
					if(hmwcTagObs.containsKey(feature)){
							freq = hmwcTagObs.get(feature);
							pres = 1;
					}

					if(i == numFeatures -1){
						textBinFreqToStr.append(freq);
						textBinPresToStr.append(pres);
					}
					else{
						textBinFreqToStr.append(freq + "\t");
						textBinPresToStr.append(pres + "\t");
					}

				}//end for					
				
				//write variables except binary text to file
				for(int i = 0; i < numVar-1; i++){
					bwCleanBinFreqTag.write(tokens[i] + "\t");
					bwCleanBinPresTag.write(tokens[i] + "\t");
				}//end for

				//write binary text to file
				bwCleanBinFreqTag.write(textBinFreqToStr.toString() + "\n");
				bwCleanBinPresTag.write(textBinPresToStr.toString() + "\n");				

			}//end of while

			//close bwCleanBinFreqTag and bwCleanBinPresTag
			bwCleanBinFreqTag.close();
			bwCleanBinPresTag.close();			

			/*close reading files*/
			brRaw.close();
			brDic.close();
			brFeaturesTag.close();
			
			//end timer
			System.out.println("\n\nProcess finish!");
			long elapsedTime = System.nanoTime() - startTime;
			System.out.printf("\n\nProcess finish! Time taken %.3f Second\n", elapsedTime/1e+9);
		}//end try
		catch(IOException e){
			System.out.println(e.getMessage());
		}

	}//end main


	/**cleanText method replace punctuation (not include single quote) with space
	*@para str  the input text
	*@return a clean text which do not contain punctuation except space and single quote
	*/
	private static String cleanText(String str){
		StringBuffer nstr = new StringBuffer(); //the clean text 

		HashMap<Character, String> hmPunctuation = new HashMap<>();

		//HashMap hmPunctuation containing punctuation (not include single quote)
		hmPunctuation.put('!',	"exclamation mark");
		hmPunctuation.put('\"',	"double quote");
		hmPunctuation.put('#',	"number");
		hmPunctuation.put('$',	"dollar");
		hmPunctuation.put('%',	"percent");
		hmPunctuation.put('&',	"ampersand");
		hmPunctuation.put('(',	"left parenthesis");
		hmPunctuation.put(')',	"right parenthesis");
		hmPunctuation.put('*',	"asterisk");
		hmPunctuation.put('+',	"plus");
		hmPunctuation.put(',',	"comma");
		hmPunctuation.put('-',	"minus");
		hmPunctuation.put('.',	"period");
		hmPunctuation.put('/',	"slash");
		hmPunctuation.put(':',	"colon");
		hmPunctuation.put(';',	"semicolon");
		hmPunctuation.put('<',	"less than");
		hmPunctuation.put('=',	"equality sign");
		hmPunctuation.put('>',	"greater than");
		hmPunctuation.put('?',	"question mark");
		hmPunctuation.put('@',	"at sign");
		hmPunctuation.put('[',	"left square bracket");
		hmPunctuation.put('\\',	"backslash");
		hmPunctuation.put(']',	"right square bracket");
		hmPunctuation.put('^',	"caret / circumflex");
		hmPunctuation.put('_',  "underscore");
		hmPunctuation.put('`',	"grave / accent");

		for(int i=0; i < str.length(); i++){
			char ch = str.charAt(i);

			if(hmPunctuation.containsKey(ch)){
				nstr.append(' ');
			}
			else
				nstr.append(Character.toLowerCase(ch));
		}

		return nstr.toString();
	}//end of cleanText method


	/**tagText method  add not_ to every word between a negation word
     *("not", isn't, didn't, etc)
     *https://en.wikipedia.org/wiki/Wikipedia:List_of_English_contractions
     *
     *param str  input string
     *return string which word add not_ if there is negation word before it.
	*/
	private static String tagText(String str){
		//the pattern for the negation word: not, *n't

		StringBuffer nstr = new StringBuffer();

		//convert string to array delimited by space or multi space
		String[] tokens = str.split(" +");

		for(int i = 0; i < tokens.length; i++){
			if(i == 0){
				nstr.append(tokens[i]);
				nstr.append(" ");
			}
			else if(isNot(tokens[i-1]) && i == tokens.length - 1){
				nstr.append("not_" + tokens[i]);
			}
			else if(!isNot(tokens[i-1]) && i == tokens.length - 1){
				nstr.append(tokens[i]);
			}
			else if(isNot(tokens[i-1])){
				nstr.append("not_" + tokens[i]);
				nstr.append(" ");
			}
			else{
				nstr.append(tokens[i]);
				nstr.append(" ");
			}
		}//end for

		return nstr.toString();

	}//end of tagText method

	/** isNot method to evaluate the word is not or didn't, isn't etc. or not
	*@param str the input word
	*@return boolean value if the word is not or derivative return true
	*/
	private static boolean isNot (String str){
		//the pattern for the negation word: not, *n't

		int lenStr = str.length();

		String nstr = str.toLowerCase();

		if(nstr.equals("not")){
			return true;
		}
		else if(lenStr > 3){
			if(nstr.charAt(lenStr - 1) == 't' 
			&& nstr.charAt(lenStr - 2) == '\'' && nstr.charAt(lenStr - 3) == 'n'){
				return true;
			}
		}


		return false;
	}//end of isNot method

	/** wordCount method count the frequency of word in the text
	*param str  input text
	*return hashmap the key is the word and value is the frequency
	*/
	private static HashMap<String, Integer> wordCount(String str){
		HashMap<String, Integer> hm = new HashMap<>();

		String[] tokens = str.split(" +");

		for(int i = 0; i < tokens.length; i++){
			String token = tokens[i];

			if(!hm.containsKey(token)){
				hm.put(token, 1);
			}
			else{
				hm.put(token, hm.get(token)+1);
			}
		}//end for loop

		return hm;

	}//end of wordCount method

}//end of class