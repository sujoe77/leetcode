package com.pineapple.java.algo.listNode;

import com.pineapple.java.algo.ListNode;

public class ReverseListNode {
    public ListNode reverseList(ListNode head) {
        ListNode ret = recursive(head);
        if (head != null)
            head.next = null;
        return ret;
    }

    private ListNode iterate1(ListNode head) {
        ListNode h = null, p = null;
        while (head != null) {
            p = head.next; //p >>
            head.next = h; //add new head
            h = head; //new head >>
            head = p; //head >>
        }
        return h;
    }

    private ListNode recursive(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode h = recursive(head.next);
        head.next.next = head;
        return h;
    }
}
