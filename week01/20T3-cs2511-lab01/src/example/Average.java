package example;

public class Average {

        /**
         * Returns the average of an array of numbers
         * @param the array of integer numbers
         * @return the average of the numbers
         */
        public float average(int[] nums) {
            float result = 0;
            int length = nums.length;
            for(int i = 0; i < length; i++){
                result = result + nums[i];
            }
            result = result/length;
            return result;
        }

        public static void main(String[] args) {
            int[] array = new int[10];
            array[0] = 10;
            array[1] = 9;
            array[2] = 8;
            array[3] = 7;
            array[4] = 6;
            array[5] = 5;
            array[6] = 4;
            array[7] = 3;
            array[8] = 2;
            array[9] = 1;
            //question why cant i used Average.average(array) instead
            //since Average is the class and not the method
            float result = new Average().average(array);
            System.out.println(result);
        }
}
