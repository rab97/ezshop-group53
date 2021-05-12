package it.polito.ezshop;

public class Operator {

	public boolean isValidCode(String productCode) {
    	if(productCode.length() != 12 && productCode.length() != 13 && productCode.length() != 14) {
			return false;
		}
		int counter = 0;
		int values3[] = {3, 1,3,1,3,1,3,1,3,1,3,1,3,1,3};
		int values1[] = {1,3, 1,3,1,3,1,3,1,3,1,3,1,3,1,3};
		int result[] = new int[productCode.length()];
		if(productCode.length() != 12) {			
			for(int i = 0; i < productCode.length() - 1; i++) {
				result[i] = Integer.parseInt(productCode.charAt(i) + "") * values1[i];
				counter += result[i];
				System.out.print(result[i] + "-");
			
			} 
		} else {
			for(int i = 0; i < productCode.length() - 1; i++) {
				result[i] = Integer.parseInt(productCode.charAt(i) + "") * values3[i];
				counter += result[i];
				System.out.print(result[i] + "-");				
			}
		}
		
		int minMultiple  = (counter / 10) *10;
		int digit = minMultiple + 10;
		if(Integer.parseInt(productCode.charAt(productCode.length() - 1) + "") == (digit - counter)){
			return true;
		}
    	return false;
    }
	
}
