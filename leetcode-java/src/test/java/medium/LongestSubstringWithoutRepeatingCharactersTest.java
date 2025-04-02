package medium;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LongestSubstringWithoutRepeatingCharactersTest {

    private final LongestSubstringWithoutRepeatingCharacters solver =
            new LongestSubstringWithoutRepeatingCharacters();

    @Test
    public void testExample1() {
        assertEquals(3, solver.lengthOfLongestSubstring("abcabcbb"));
    }

    @Test
    public void testExample2() {
        assertEquals(1, solver.lengthOfLongestSubstring("bbbbb"));
    }

    @Test
    public void testExample3() {
        assertEquals(3, solver.lengthOfLongestSubstring("pwwkew"));
    }

    @Test
    public void testEmptyString() {
        assertEquals(0, solver.lengthOfLongestSubstring(""));
    }

    @Test
    public void testAllUnique() {
        assertEquals(6, solver.lengthOfLongestSubstring("abcdef"));
    }

    @Test
    public void testSingleCharacter() {
        assertEquals(1, solver.lengthOfLongestSubstring("z"));
    }

    @Test
    public void testWithSpecialCharacters() {
        assertEquals(5, solver.lengthOfLongestSubstring("abc!@abc"));
    }

    @Test
    public void testLongRepeatingPattern() {
        assertEquals(2, solver.lengthOfLongestSubstring("abababab"));
    }

    @Test
    public void testLongInput() {
        String input = "a".repeat(10000);
        assertEquals(1, solver.lengthOfLongestSubstring(input));
    }
}
