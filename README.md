# Andx-file_format

A new designed file format called **AN**imated **D**ocument e**X**tension. 

## **1. Technical Specifications:**

The number of pages in a single AnDx file is 18*10^18(18e18).

The maximum file size can be upto 18EB (1EB=1000 PentaBytes).

The header can be from 27-90 bytes.

The maximum video size can be up-to 18,000 PentaBytes.

## **2.1 Andx File Format:**

        	The .andx file format starts header followed by ‘n’ number of frames.Fig. 2.1 shows AnDx file format.

![andx file format](https://github.com/thisIsAnil/Andx-file_format/blob/master/images/image11.png)

                     Fig. 2.1 AnDx File Format

## **2.2 AnDx Header:**

	The header of AnDx contains following fields: 

**Magic Number   :**	 This is  a 5 byte field used to identify a Andx file.

**Version Number :**  This is a 3-byte field which tells the version number.

**Encoding       :** This is a 10-byte field which stores encoding used.

**Password       :** This is 8-byte optional field to set password to open the file.

**Frame Count    :** This is a variable length field which tells number of pages.

		The header format is shown in fig 2.2.

![header format image](https://github.com/thisIsAnil/Andx-file_format/blob/master/images/image24.png)


			Figure 2.2 AnDx Frame format 
## **2.3 AnDx Page Format:**

	    The frame format contains fields as follows:

**Start Of Frame	:** 	indicates start of page/frame.
**Compression code	:**	indicates compression algorithm used.
**Frame length		:**  	Actual data length in bytes.
**Start of frame data	:**  	Indicates start of frame data.
**Frame Data	     	:**	The actual data is  stored here.
**End of frame data	:**	This indicate end of actual data.
**End of frame		:**	This indicates end of frame/page.

![frame format image](https://github.com/thisIsAnil/Andx-file_format/blob/master/images/image25.png)

				Figure 2.3 AnDx Frame header


## **2.4 AnDx-Encoder:**

	The AnDx-Encoder takes all frames as input. It then computes header accordingly and writes each frame to output
 file sequentially.The Figure 2.4 show the architecture of AnDx Encoder.	

	
![encoder image](https://github.com/thisIsAnil/Andx-file_format/blob/master/images/image12.png)

			Figure 2.4 AnDx Encoder Architecture.

## **2.5 AnDx Decoder:**

The AnDx decoder is given an input file.It decodes this file into set of frames. Each frame is then rasterized on screen later.The Figure 4.2.5 shows AnDx Decoder.

![decoder image](https://github.com/thisIsAnil/Andx-file_format/blob/master/images/image13.png)

 			Figure 2.5 PCX Decoder Architecture.
