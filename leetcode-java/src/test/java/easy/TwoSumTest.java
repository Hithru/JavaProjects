package easy;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;

public class TwoSumTest {
    @Test
    public void testTwoSumCase1() {
        TwoSum solver = new TwoSum();
        int[] result = solver.twoSum(new int[]{2, 7, 11, 15}, 9);
        assertArrayEquals(new int[]{0, 1}, result);
    }

    @Test
    public void testTwoSumCase2() {
        TwoSum solver = new TwoSum();
        int[] result = solver.twoSum(new int[]{0,4,3,0}, 0);
        assertArrayEquals(new int[]{0, 3}, result);
    }

    @Test
    public void testTwoSumCase3() {
        TwoSum solver = new TwoSum();
        int[] result = solver.twoSum(new int[]{-1,-2,-3,-4,-5}, -8);
        assertArrayEquals(new int[]{2, 4}, result);
    }
}
