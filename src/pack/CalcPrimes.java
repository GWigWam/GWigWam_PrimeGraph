package pack;

public class CalcPrimes implements Runnable{

	public int totalPrimes = 1; // 2skiped
	public boolean prime = true;
	public double numbersProcessed = 0;
	private int primesThisSecond = 0;
	public int primesPerSecond = 0;
	private long secTime;
	
	public int[] list = new int[350000]; // Ongeveer 30min om te vullen op laptop Aan het eind 65primes/s DEFAULT: 350000
	
	@Override
	public void run() {
		try{
			int n = 1;
			secTime = System.currentTimeMillis();
			while(totalPrimes < list.length){
				for(int d = 2; d <= (n/2) && prime; d++){
					if(n % d == 0){
						prime = false;
						break;
					}
				}
				
				if(prime){
					list[totalPrimes-1] = n;
					totalPrimes++;
					primesThisSecond++;
				}
				prime = true;
				numbersProcessed = n;
				n+=2; //Skip even numbers
				if(System.currentTimeMillis() - secTime > 1000){
					primesPerSecond = primesThisSecond;
					primesThisSecond = 0;
					secTime = System.currentTimeMillis();
				}
			}
			Thread.sleep(10000);
		}catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public int[] getHist(int interval){
		int[] a = new int[list.length];
		try{
			for(int i = 0; i < list.length; i++){
				if(list[i] != 0){
					a[(int) Math.floor(list[i]/interval)]++;
				}else{
					break;
				}
			}
		}catch(Exception e){
			System.out.println("getHist() problem. Prob ArrayIndexOutOfBounds");
			System.exit(-1);
		}
		return a;
	}
}
