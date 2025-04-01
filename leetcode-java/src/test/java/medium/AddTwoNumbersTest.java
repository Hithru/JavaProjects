package medium;

import org.junit.Test;
import static org.junit.Assert.*;

public class AddTwoNumbersTest {

    private ListNode createLinkedList(int[] values) {
        if (values == null || values.length == 0) {
            return null;
        }
        ListNode dummyHead = new ListNode(0);
        ListNode current = dummyHead;
        for (int val : values) {
            current.next = new ListNode(val);
            current = current.next;
        }
        return dummyHead.next;
    }

    private void assertLinkedListEquals(ListNode expected, ListNode actual) {
        ListNode currentExpected = expected;
        ListNode currentActual = actual;
        int position = 0;
        while (currentExpected != null && currentActual != null) {
            assertEquals("Node value mismatch at position " + position, currentExpected.val, currentActual.val);
            currentExpected = currentExpected.next;
            currentActual = currentActual.next;
            position++;
        }

        assertNull("Actual list is longer than expected (ended at pos " + position + ")", currentActual);
        assertNull("Expected list is longer than actual (ended at pos " + position + ")", currentExpected);
    }

    @Test
    public void testAddTwoNumbers_Example1() {
        AddTwoNumbers solver = new AddTwoNumbers();
        // l1 = [2,4,3] (represents 342)
        ListNode l1 = createLinkedList(new int[]{2, 4, 3});
        // l2 = [5,6,4] (represents 465)
        ListNode l2 = createLinkedList(new int[]{5, 6, 4});
        // expected = [7,0,8] (represents 807 = 342 + 465)
        ListNode expected = createLinkedList(new int[]{7, 0, 8});

        ListNode result = solver.addTwoNumbers(l1, l2);
        assertLinkedListEquals(expected, result);
    }

    @Test
    public void testAddTwoNumbers_Zeroes() {
        AddTwoNumbers solver = new AddTwoNumbers();
        // l1 = [0]
        ListNode l1 = createLinkedList(new int[]{0});
        // l2 = [0]
        ListNode l2 = createLinkedList(new int[]{0});
        // expected = [0]
        ListNode expected = createLinkedList(new int[]{0});

        ListNode result = solver.addTwoNumbers(l1, l2);
        assertLinkedListEquals(expected, result);
    }

    @Test
    public void testAddTwoNumbers_DifferentLengths() {
        AddTwoNumbers solver = new AddTwoNumbers();
        // l1 = [9,9,9,9,9,9,9] (represents 9999999)
        ListNode l1 = createLinkedList(new int[]{9, 9, 9, 9, 9, 9, 9});
        // l2 = [9,9,9,9] (represents 9999)
        ListNode l2 = createLinkedList(new int[]{9, 9, 9, 9});
        // expected = [8,9,9,9,0,0,0,1] (represents 10009998 = 9999999 + 9999)
        ListNode expected = createLinkedList(new int[]{8, 9, 9, 9, 0, 0, 0, 1});

        ListNode result = solver.addTwoNumbers(l1, l2);
        assertLinkedListEquals(expected, result);
    }

    @Test
    public void testAddTwoNumbers_CarryAtEnd() {
        AddTwoNumbers solver = new AddTwoNumbers();
        // l1 = [5]
        ListNode l1 = createLinkedList(new int[]{5});
        // l2 = [5]
        ListNode l2 = createLinkedList(new int[]{5});
        // expected = [0, 1] (represents 10 = 5 + 5)
        ListNode expected = createLinkedList(new int[]{0, 1});

        ListNode result = solver.addTwoNumbers(l1, l2);
        assertLinkedListEquals(expected, result);
    }

    @Test
    public void testAddTwoNumbers_OneListNull() {
        AddTwoNumbers solver = new AddTwoNumbers();
        // l1 = [1, 2, 3]
        ListNode l1 = createLinkedList(new int[]{1, 2, 3});
        // l2 = null
        ListNode l2 = null;
        // expected = [1, 2, 3]
        ListNode expected = createLinkedList(new int[]{1, 2, 3});

        ListNode result = solver.addTwoNumbers(l1, l2);
        assertLinkedListEquals(expected, result);

        // Test the other way around
        result = solver.addTwoNumbers(l2, l1); // Pass null first
        assertLinkedListEquals(expected, result);
    }

    @Test
    public void testAddTwoNumbers_BothListsNull() {
        AddTwoNumbers solver = new AddTwoNumbers();
        ListNode l1 = null;
        ListNode l2 = null;
        ListNode expected = null; // Adding two null lists should result in null

        ListNode result = solver.addTwoNumbers(l1, l2);
        assertLinkedListEquals(expected, result);
    }
}