package it.polito.ezshop.model;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.NumberFormatException;

public class Operator {

	public boolean isValidCode(String productCode) {
    	if(productCode == null || (productCode.length() != 12 && productCode.length() != 13 && productCode.length() != 14)) {
			return false;
		}
		int counter = 0;
		int values3[] = {3, 1,3,1,3,1,3,1,3,1,3,1,3,1,3};
		int values1[] = {1,3,1,3,1,3,1,3,1,3,1,3};
		int result[] = new int[productCode.length()];
		if(productCode.length() == 13) {
			for(int i = 0; i < productCode.length() - 1; i++) {
				result[i] = Integer.parseInt(productCode.charAt(i) + "") * values1[i];
				counter += result[i];
				//System.out.print(result[i] + "-");
			}
			//System.out.print("\n");
		} else {
			for(int i = 0; i < productCode.length() - 1; i++) {
				result[i] = Integer.parseInt(productCode.charAt(i) + "") * values3[i];
				counter += result[i];
				//System.out.print(result[i] + "-");				
			}
		}
		
		int minMultiple  = (counter / 10) *10;
		int digit = 0;
		
		if(counter == minMultiple){
			digit = minMultiple;
		} else {
			digit = minMultiple + 10;
		}
		if(Long.parseLong(productCode.charAt(productCode.length() - 1) + "") == (digit - counter)){
			return true;
		}
    	return false;
    }
	
	public boolean luhnCheck(String ccNumber)
    {
		try {
			Long.parseLong(ccNumber);
		} catch (Exception e) {
			return false;
		}
            int sum = 0;
            boolean alternate = false;
            for (int i = ccNumber.length() - 1; i >= 0; i--)
            {
                    int n = Integer.parseInt(ccNumber.substring(i, i + 1));
                    if (alternate)
                    {
                            n *= 2;
                            if (n > 9)
                            {
                                    n = (n % 10) + 1;
                            }
                    }
                    sum += n;
                    alternate = !alternate;
            }
            return (sum % 10 == 0);
    }
	
	/*return false if the credit card doesn't exist or if it hasn't enough money, true otherwise*/
	//debit==true if it is a sale, false if it is a return (no need to check amount but only existence)
	public boolean checkCreditCardAmount(String creditCard, Double toPay, boolean debit) {
		String lineString;
        String cardNumber;
        double balance;
        int index;
        
        if(toPay <= 0 || creditCard == null || creditCard.isEmpty()) {
        	return false;
        }
        
        try {
        	Long.parseLong(creditCard);
            // Using file pointer creating the file.
            File file = new File("CreditCards.txt");
 
            if (!file.exists()) {
                System.out.println("ERROR, FILE DOESN'T EXIST!!");  	//it shouldn't happen...
                return false;
            }
 
            // Opening file in reading mode
            RandomAccessFile raf = new RandomAccessFile(file, "r");

            // Traversing the file
            // getFilePointer() give the current offset value from start of the file.
            while (raf.getFilePointer() < raf.length()) {
 
                // reading line from the file.
                lineString = raf.readLine();
                
                if (lineString.startsWith("#"))
                	continue;
 
                // splitting the string to get credit card number and balance
                String[] lineSplit = lineString.split(";");
 
                // separating name and number.
                cardNumber = lineSplit[0];
                balance = Double.parseDouble(lineSplit[1]);
                // Print the card data
                //System.out.println( "Credit card number: " + cardNumber + "\n" + "Balance: " + balance + "\n");
                
                if(cardNumber.equals(creditCard)) {
                	if(balance>=toPay || debit == false)
                		return true;
                	else {
                		return false;
                	}
                }
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        } catch (NumberFormatException nef) {
            System.out.println(nef);
        }
        return false;
	}
	
	public boolean updateCreditCardAmount(String creditCard, Double toPay, boolean debit) {			 
		
		String lineString;
		String cardNumber;
		double balance = 0.0;
		int index;
		if(creditCard == null || creditCard.isEmpty() || toPay <= 0) {
			return false;
		}
		
		try {
			// Using file pointer creating the file.
			File file = new File("CreditCards.txt");

			if (!file.exists()) {
                System.out.println("ERROR, FILE DOESN'T EXIST!!");  	//it shouldn't happen...
                return false;
            }
					
			// Opening file in reading and write mode.
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			boolean found = false;
			
			// Creating a temporary file
			File tmpFile = new File("temp.txt");
			RandomAccessFile tmpraf= new RandomAccessFile(tmpFile, "rw");

			// I assume that the credit card exist (because because I've checked the amount)
			// getFilePointer() give the current offset value from start of the file.
			while (raf.getFilePointer() < raf.length()) {
				// reading line from the file.
                lineString = raf.readLine();
                
                if (lineString.startsWith("#")) {                	
                	tmpraf.writeBytes(lineString);
					tmpraf.writeBytes(System.lineSeparator());
                	continue;
                } else {;
	                // splitting the string to get credit card number and balance
	                String[] lineSplit = lineString.split(";");
	                // separating name and number.
	                cardNumber = lineSplit[0];
	                try {	                	
	                	balance = Double.parseDouble(lineSplit[1]);
	                } catch (Exception e) {
						System.out.println("exceptio " + e);
					}
					if (cardNumber.equals(creditCard)) {
						found = true;
						if(debit) {
							if(balance < toPay) {
								tmpraf.close();
								tmpFile.delete();
								raf.close();
								return false;
							}
							balance-=toPay;
						}
						else
							balance+=toPay;
					}
					lineString=cardNumber + ";" + String.valueOf(balance);
					tmpraf.writeBytes(lineString);
					tmpraf.writeBytes(System.lineSeparator());
                }
			}

			// The contact has been updated now. So copy the updated content from the temporary file to original file.
			// Set both files pointers to start
			raf.seek(0);
			tmpraf.seek(0);

			// Copy the contents from the temporary file to original file.
			while (tmpraf.getFilePointer() < tmpraf.length()) {
				String line = tmpraf.readLine();
				raf.writeBytes(line);
				if(!line.isEmpty()) {					
					raf.writeBytes(System.lineSeparator());
				}
			}

			// Set the length of the original file to that of temporary.
			raf.setLength(tmpraf.length());

			// Closing the resources.
			tmpraf.close();
			raf.close();
			// Deleting the temporary file
			tmpFile.delete();
        	
			//System.out.println(" Credit card updated. ");
			return found;
			
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
		
		return false;
	}

}
