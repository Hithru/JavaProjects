package medium;

import java.util.HashMap;
import java.util.Map;

public class LongestSubstringWithoutRepeatingCharacters {
    public int lengthOfLongestSubstring(String s) {
        int answer = 0;
        int current_length = 0;
        int begin_index = 0 ;
        Map<String,Integer> charToIndex = new HashMap<>();
        for (int i = 0; i< s.length(); i++){
            String chr = Character.toString(s.charAt(i));
            if (charToIndex.containsKey(chr)){
                begin_index = Math.max(charToIndex.get(chr),begin_index);
                current_length = i - begin_index;

            }else{
                current_length +=1;
            }

            charToIndex.put(chr, i);

            if (current_length > answer) {
                answer = current_length;
            }
        }

        return answer;
    }

    public static void main(String[] args) {
        LongestSubstringWithoutRepeatingCharacters solver = new LongestSubstringWithoutRepeatingCharacters();
        int result = solver.lengthOfLongestSubstring("pwwkew");
        System.out.println("Result: " + result);
    }
}
