package easy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TwoSum {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer,Integer> valueMap = new HashMap<>();
        int[] result = new int[2];
        for (int i = 0; i < nums.length; i++){
            if (valueMap.containsKey(nums[i])){
                result[0] = valueMap.get(nums[i]);
                result[1] = i;
                return result;
            }
            int difference = target - nums[i];
            valueMap.put(difference, i);
        }
        return result;
    }

    public static void main(String[] args) {
        TwoSum solver = new TwoSum();
        int[] result = solver.twoSum(new int[]{2, 7, 11, 15}, 9);
        System.out.println("Result: " + Arrays.toString(result));
    }
}
