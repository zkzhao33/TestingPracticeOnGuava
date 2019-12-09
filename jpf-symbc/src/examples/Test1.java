public class Test1 {
	public static int myMethod(int x, int y){
		int z = x + y;
		if (x/z > 0) {
			if(y>0){
				z = x/z;
			} else{
				z = x/z;
			}
		} else {
			if(x>0){
				z = z - x;
			} else{
				z = z + x;
			}
		}
		z = 2 * z;
		return z;
	}
	public static void main(String[] args){
		myMethod(0,1);
	}
}
