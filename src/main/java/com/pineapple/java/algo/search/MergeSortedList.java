package com.pineapple.java.algo.search;

import com.pineapple.java.algo.ListNode;

import java.util.PriorityQueue;

public class MergeSortedList {


    public static void main(String[] args) {
        ListNode head = new ListNode(0);
        ListNode head1 = new ListNode(0);
        ListNode head2 = new ListNode(0);
        ListNode last = null, last1 = null, last2 = null;

        ListNode result = null;
        for (int i = 0; i < 10; i += 3) {
            ListNode node = new ListNode(i);
            ListNode node1 = new ListNode(i + 1);
            ListNode node2 = new ListNode(i + 2);
            if (i == 0) {
                head.next = node;
                last = node;
                head1.next = node1;
                last1 = node1;
                head2.next = node2;
                last2 = node2;
            } else {
                last.next = node;
                last = node;
                last2.next = node2;
                last2 = node2;
                last1.next = node1;
                last1 = node1;
            }
        }
        ListNode[] lists = new ListNode[]{head, head1, head2};
        result = mergeKLists(lists);
        System.out.println(result);
    }

    public static class MyPriorityQueue {
        ListNode queue;
        public void add(ListNode input) {
//            System.out.println("add " + input.val);
            if (queue == null) {
                queue = new ListNode(input.val);
                return;
            } else if(input.val <= queue.val){
                ListNode newHead = new ListNode(input.val);
                newHead.next = queue;
                queue = newHead;
                return;
            }
            ListNode node = queue;
            while (node.next != null) {
                ListNode next = node.next;
                if (node.val <= input.val && next.val >= input.val) {
                    node.next = new ListNode(input.val);
                    node.next.next = next;
                    return;
                }
                node = node.next;
            }
            node.next = new ListNode(input.val);
        }
    }

    public static ListNode mergeKLists(ListNode[] lists) {
        //MyPriorityQueue sorted = new MyPriorityQueue();
        PriorityQueue<Integer> sorted = new PriorityQueue();
        for (int i = 0; i < lists.length; i++) {
            ListNode node = lists[i];
            do {
                sorted.add(node.val);
                node = node.next;
            } while (node != null);
        }
        ListNode ret = null;
        ListNode last = null;
        while (!sorted.isEmpty()){
            if(ret == null){
                ret = new ListNode(sorted.poll());
                last = ret;
            } else {
                last.next = new ListNode(sorted.poll());
                last = last.next;
            }
        }
        return ret;
    }
}

