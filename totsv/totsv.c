/**
*AUTHOR: GUANPING YU
*DATE: 2020-04-22
*Version: 0.4.0
*GOAL: preprocess the raw data from HIVE to standard tsv (table separated value) file. 
*The raw data is n columns null (0x00) separated file. 
* The raw data first column must be one char and other column should more one char
*
*first step: replace the newline (0x0A), carriage return (0x0D) and tab (0x09) with space (0x20) 
*second step: delimiter for the row. 
              change the char to newline (0x0A) between tuples. [...0x0A xx 0x00...], xx is decimal 1-5
*third step: delimiter for the column.
             replace null (0x00) with tab (0x09)

*compile
$ gcc totsv.c -o totsv

* "example:"
* covert raw data 'review_1k_col7_delim_null' to tsv file 'review_1k_col7.tsv'
$ ./totsv review_1k_col7_delim_null review_1k_col7.tsv
*/

#include <stdio.h>
#include <stdlib.h>

#define MAX 4096
#define NAMESIZE 1024   

int main(int argc, char* argv[])
{
    int fsize, //the raw data size
    	csize; //current file pointer


	FILE *fpr, //raw data
	     *fpw; //tsv file
	unsigned char buff[MAX], //block of data
				  temp[2]; //array for the first two char of the block.

	size_t bytes_read;
	int i;

	fpr = fopen(argv[1], "r");
	fpw = fopen(argv[2], "w");

	if(fpr == NULL){
		perror ("input file does't exist!");
		return -1;
	}

	//get the size of file
	fseek(fpr, 0, SEEK_END);
	fsize = ftell(fpr);
	//printf("%d\n", fsize);

	//reset the pointer to the beginning
	fseek(fpr, 0, SEEK_SET);

	do{
		/*read size MAX bytes from raw data to buff*/
		bytes_read = fread(buff, 1, MAX, fpr);

		/*replace the newline (0x0A), carriage return (0x0D) and tab (0x09) with space (0x20)*/
		for(i = 0; i < bytes_read; i++){
			if(buff[i] == 0x0A || buff[i] == 0x0D || buff[i] == 0x09)
				buff[i] = 0x20;
		}


		/*change the char to newline (0x0A) between tuples. [...0x0A xx 0x00...], xx is decimal 1-5
		
		*/
		for(i = 2; i < bytes_read; i++){
			if(buff[i] == 0x00 && buff[i-2] == 0x20){
				buff[i-2] = 0x0A;
			}
		}


		/*check the last two char*/
		if(bytes_read == MAX){//not the last block
			fread(temp, 1, 2, fpr);

			if(temp[0] == 0x00 && buff[bytes_read-2] == 0x20){
				buff[bytes_read-2] = 0x0A;
			}
			else if(temp[1] == 0x00 && buff[bytes_read-1] == 0x20){
				buff[bytes_read-1] = 0x0A;
			}

			//reset the pointer of fpr to the end of block
			fseek(fpr, -2, SEEK_CUR);
		}


		/*replace null (0x00) with tab (0x09)*/
		for (i = 0; i < bytes_read; i++){
			if(buff[i] == 0x00)
				buff[i] = 0x09;
		}


		/*write the revised buff to tsv file*/
		fwrite(buff, bytes_read, 1, fpw);

		/*how much percent finish*/
		csize = ftell(fpr);
		printf("Transfer raw data to tsv file: %.2f%%", (float)csize/fsize *100);
		fflush(stdout);
    	printf("\r");

	}while(bytes_read);
	
	printf("\n File size %d byte. Task finish :) \n", fsize);

	//close file
	fclose(fpr);
	fclose(fpw);

	return 0;
}
