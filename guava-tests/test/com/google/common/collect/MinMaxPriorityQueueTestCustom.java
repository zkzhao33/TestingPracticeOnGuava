/*
 * Copyright (C) 2008 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.collect;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Platform.reduceExponentIfGwt;
import static com.google.common.collect.Platform.reduceIterationsIfGwt;
import static com.google.common.truth.Truth.assertThat;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.MinMaxPriorityQueue.Builder;
import com.google.common.collect.testing.IteratorFeature;
import com.google.common.collect.testing.IteratorTester;
import com.google.common.collect.testing.QueueTestSuiteBuilder;
import com.google.common.collect.testing.TestStringQueueGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.testing.NullPointerTester;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for {@link MinMaxPriorityQueue}.
 *
 * @author Alexei Stolboushkin
 * @author Sverre Sundsdal
 */
@GwtCompatible(emulated = true)
public class MinMaxPriorityQueueTestCustom extends TestCase {

    private void offerAElementToQueue(Integer[] init, int ele, boolean ascending, boolean full,
                                      int size, boolean isFull, Integer firstElement) {
        Ordering<Integer> ordering = ascending ? Ordering.natural() : Ordering.natural().reverse();
        int capacity = full ? init.length : init.length + 1;
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.orderedBy(ordering).maximumSize(capacity).create(Arrays.asList(init));
        queue.offer(ele);
        assertEquals(size, queue.size());
        assertEquals(isFull,queue.size() == queue.maximumSize);
        assertEquals(queue.size() + 1, queue.capacity());
        assertEquals(firstElement, queue.peek());
    }

    public void testOfferAElementToQueue() {
        offerAElementToQueue(new Integer[]{5, 15, 20}, 10, true, false, 4, true, 5);
        offerAElementToQueue(new Integer[]{5, 15, 20}, 10, true, true, 3, true, 5);
        offerAElementToQueue(new Integer[]{5, 15, 20}, 10, false, false, 4, true, 20);
        offerAElementToQueue(new Integer[]{5, 15, 20}, -1, true, false, 4, true, -1);
        offerAElementToQueue(new Integer[]{5, 15, 20}, 0, true, false, 4, true, 0);
        offerAElementToQueue(new Integer[]{5, 15, 20}, 1, true, false, 4, true, 1);
        offerAElementToQueue(new Integer[]{5, 15, 20}, 2, true, false, 4, true, 2);
        offerAElementToQueue(new Integer[]{}, 10, true, false, 1, true, 10);
        offerAElementToQueue(new Integer[]{5}, 10, true, false, 2, true, 5);
        offerAElementToQueue(new Integer[]{5, 15}, 10, true, false, 3, true, 5);
    }

    private void offerElementsToQueue(Integer[] init, Integer[] eles, boolean ascending, boolean full,
                                      int size, boolean isFull, Integer firstElement) {
        Ordering<Integer> ordering = ascending ? Ordering.natural() : Ordering.natural().reverse();
        int capacity = init.length;
        if (!full) capacity += eles.length;
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.orderedBy(ordering).maximumSize(capacity).create(Arrays.asList(init));
        queue.addAll(Arrays.asList(eles));
        assertEquals(size, queue.size());
        assertEquals(isFull,queue.size() == queue.maximumSize);
        assertEquals(firstElement, queue.peek());
    }

    public void testOfferElementsToQueue() {
        offerElementsToQueue(new Integer[]{5, 15, 20}, new Integer[]{1, 10, 30}, true, false, 6, true, 1);
        offerElementsToQueue(new Integer[]{5, 15, 20}, new Integer[]{1, 10, 30}, true, true, 3, true, 1);
        offerElementsToQueue(new Integer[]{5, 15, 20}, new Integer[]{1, 10, 30}, false, false, 6, true, 30);
        offerElementsToQueue(new Integer[]{5, 15, 20}, new Integer[]{}, true, false, 3, true, 5);
        offerElementsToQueue(new Integer[]{5, 15, 20}, new Integer[]{1}, true, false, 4, true, 1);
        offerElementsToQueue(new Integer[]{5, 15, 20}, new Integer[]{1, 10}, true, false, 5, true, 1);
        offerElementsToQueue(new Integer[]{}, new Integer[]{1, 10, 30}, true, false, 3, true, 1);
        offerElementsToQueue(new Integer[]{5}, new Integer[]{1, 10, 30}, true, false, 4, true, 1);
        offerElementsToQueue(new Integer[]{5, 15}, new Integer[]{1, 10, 30}, true, false, 5, true, 1);
    }
    class Node implements Comparable<Node>{
        int data;
        Node (int d) {
            data = d;
        }

        @Override
        public int compareTo(Node node) {
            if (node.data > this.data) {
                return -1;
            } else if (node.data < this.data) {
                return 1;
            }
            else {
                return 0;
            }
        }

        @Override
        public boolean equals(Object that){
            return that != null && that instanceof Node
                    && ((Node) that).data == this.data;
        }

        @Override
        public int hashCode(){
            return 31*this.data;
        }

    }

    public void testCreationEmpty() {
        ArrayList<Integer> empty = new ArrayList<>();
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create(empty);
        assertEquals(11, queue.capacity());
        assertEquals(0, queue.size());
    }
    public void testCreationSingleInteger() {
        int singleInteger = 10;
        ArrayList<Integer> single = new ArrayList<>();
        single.add(singleInteger);
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create(single);
        assertEquals(single.size(), queue.size());
    }
    public void testCreationSingleInteger2() {
        int singleInteger = -32132313;
        ArrayList<Integer> single = new ArrayList<>();
        single.add(singleInteger);
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create(single);
        assertEquals(single.size(), queue.size());
    }
    public void testCreationSingleInteger3() {
        int singleInteger = 32132313;
        ArrayList<Integer> single = new ArrayList<>();
        single.add(singleInteger);
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create(single);
        assertEquals(single.size(), queue.size());
    }
    public void testCreationSingleString() {
        String singleString = "testString";
        ArrayList<String> single = new ArrayList<>();
        single.add(singleString);
        MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue.create(single);
        assertEquals(single.size(), queue.size());
    }
    public void testCreationSingleEmptyString() {
        String singleString = "";
        ArrayList<String> single = new ArrayList<>();
        single.add(singleString);
        MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue.create(single);
        assertEquals(single.size(), queue.size());
    }
    public void testCreationSingleLongString() {
        String singleString = "test Sשּׁtringinginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginginging\n";
        ArrayList<String> single = new ArrayList<>();
        single.add(singleString);
        MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue.create(single);
        assertEquals(single.size(), queue.size());
    }
    public void testCreationSingleNode() {
        Node node = new Node(5);
        ArrayList<Node> single = new ArrayList<>();
        single.add(node);
        MinMaxPriorityQueue<Node> queue = MinMaxPriorityQueue.create(single);
        assertEquals(single.size(), queue.size());
    }
    public void testCreationTwoInteger() {
        int firstInteger = 10;
        int secondInteger = -1;
        ArrayList<Integer> two = new ArrayList<>();
        two.add(firstInteger);
        two.add(secondInteger);
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create(two);
        assertEquals(two.size(), queue.size());
    }
    public void testCreationTwoInteger2() {
        int firstInteger = -47105101;
        int secondInteger = -1;
        ArrayList<Integer> two = new ArrayList<>();
        two.add(firstInteger);
        two.add(secondInteger);
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create(two);
        assertEquals(two.size(), queue.size());
    }
    public void testCreationTwoString() {
        String firstString = "first";
        String secondString = "second";
        ArrayList<String> two = new ArrayList<>();
        two.add(firstString);
        two.add(secondString);
        MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue.create(two);
        assertEquals(two.size(), queue.size());
    }




    public void testCreationTwoNode() {
        Node firstNode = new Node(0);
        Node secondNode = new Node(-1);
        ArrayList<Node> two = new ArrayList<>();
        two.add(firstNode);
        two.add(secondNode);
        MinMaxPriorityQueue<Node> queue = MinMaxPriorityQueue.create(two);
        assertEquals(two.size(), queue.size());
    }


    public void testCreationReverseTwoInteger() {
        int firstInteger = 10;
        int secondInteger = -1;
        ArrayList<Integer> two = new ArrayList<>();
        two.add(firstInteger);
        two.add(secondInteger);
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(two);
        assertEquals(two.size(), queue.size());
    }
    public void testCreationReverseTwoInteger2() {
        int firstInteger = -47105101;
        int secondInteger = -1;
        ArrayList<Integer> two = new ArrayList<>();
        two.add(firstInteger);
        two.add(secondInteger);
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(two);
        assertEquals(two.size(), queue.size());
    }
    public void testCreationReverseTwoString() {
        String firstString = "ff\n" +
                "  fffgifgirst";
        String secondString = "";
        ArrayList<String> two = new ArrayList<>();
        two.add(firstString);
        two.add(secondString);
        MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(two);
        assertEquals(two.size(), queue.size());
    }
    public void testCreationReverseTwoString2() {
        String firstString = "first";
        String secondString = "second";
        ArrayList<String> two = new ArrayList<>();
        two.add(firstString);
        two.add(secondString);
        MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(two);
        assertEquals(two.size(), queue.size());
    }
    public void testCreationReverseTwoNode() {
        Node firstNode = new Node(0);
        Node secondNode = new Node(-877887);
        ArrayList<Node> two = new ArrayList<>();
        two.add(firstNode);
        two.add(secondNode);
        MinMaxPriorityQueue<Node> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(two);
        assertEquals(two.size(), queue.size());
    }



    public void testCreationNInteger() {
        ArrayList<Integer> N = new ArrayList<>();
        N.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create(N);
        assertEquals(N.size(), queue.size());
    }
    public void testCreationNInteger2() {
        ArrayList<Integer> N = new ArrayList<>();
        N.addAll(Arrays.asList(1, 2, 3, 4, 5, 1, 7, 8, 9, 10));
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create(N);
        assertEquals(N.size(), queue.size());
    }
    public void testCreationNString() {
        ArrayList<String> N = new ArrayList<>();
        Collections.addAll(N, new String("1"), new String("2"), new String("3"), new String("4"));
        MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue.create(N);
        assertEquals(N.size(), queue.size());
    }
    public void testCreationNNode() {
        ArrayList<Node> N = new ArrayList<>();
        Collections.addAll(N, new Node(1), new Node(2), new Node(3), new Node(4));
        MinMaxPriorityQueue<Node> queue = MinMaxPriorityQueue.create(N);
        assertEquals(N.size(), queue.size());
    }


    public void testCreationReverseNInteger() {
        ArrayList<Integer> N = new ArrayList<>();
        N.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(N);
        assertEquals(N.size(), queue.size());
    }
    public void testCreationReverseNString() {
        ArrayList<String> N = new ArrayList<>();
        Collections.addAll(N, new String("1"), new String("2"), new String("3"), new String("4"));
        MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(N);
        assertEquals(N.size(), queue.size());
    }
    public void testCreationReverseNNode() {
        ArrayList<Node> N = new ArrayList<>();
        Collections.addAll(N, new Node(1), new Node(2), new Node(3), new Node(4));
        MinMaxPriorityQueue<Node> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(N);
        assertEquals(N.size(), queue.size());
    }
    // start
    public void testPeekLastEmpty() {
        ArrayList<Integer> empty = new ArrayList<>();
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create(empty);
        assertEquals(null, queue.peekLast());
    }
    public void testPeekLastSingleInteger() {
        int singleInteger = 10;
        ArrayList<Integer> single = new ArrayList<>();
        single.add(singleInteger);
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create(single);
        int peek = queue.peekLast();
        assertEquals(singleInteger, peek);
    }
    public void testPeekLastSingleString() {
        String singleString = "testString";
        ArrayList<String> single = new ArrayList<>();
        single.add(singleString);
        MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue.create(single);
        String peek = queue.peekLast();
        assertEquals(singleString, peek);
    }
    public void testPeekLastSingleNode() {
        Node node = new Node(5);
        ArrayList<Node> single = new ArrayList<>();
        single.add(node);
        MinMaxPriorityQueue<Node> queue = MinMaxPriorityQueue.create(single);
        Node peek = queue.peekLast();
        assertEquals(node, peek);
    }
    public void testPeekLastTwoInteger() {
        int firstInteger = -1;
        int secondInteger = 10;
        ArrayList<Integer> two = new ArrayList<>();
        two.add(firstInteger);
        two.add(secondInteger);
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create(two);
        int peek = queue.peekLast();
        assertEquals(secondInteger, peek);
    }
    public void testPeekLastTwoString() {
        String firstString = "";
        String secondString = "second";
        ArrayList<String> two = new ArrayList<>();
        two.add(firstString);
        two.add(secondString);
        MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue.create(two);
        String peek = queue.peekLast();
        assertEquals(secondString, peek);
    }
    public void testPeekLastTwoNode() {
        Node firstNode = new Node(1);
        Node secondNode = new Node(2);
        ArrayList<Node> two = new ArrayList<>();
        two.add(firstNode);
        two.add(secondNode);
        MinMaxPriorityQueue<Node> queue = MinMaxPriorityQueue.create(two);
        Node peek = queue.peekLast();
        assertEquals(secondNode, peek);
    }


    public void testPeekLastReverseTwoInteger() {
        int firstInteger = 1;
        int secondInteger = 2;
        ArrayList<Integer> two = new ArrayList<>();
        two.add(firstInteger);
        two.add(secondInteger);
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(two);
        int peek = queue.peekLast();
        assertEquals(firstInteger, peek);
    }
    public void testPeekLastReverseTwoString() {
        String firstString = "first";
        String secondString = "second";
        ArrayList<String> two = new ArrayList<>();
        two.add(firstString);
        two.add(secondString);
        MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(two);
        String peek = queue.peekLast();
        assertEquals(firstString, peek);
    }
    public void testPeekLastReverseTwoNode() {
        Node firstNode = new Node(1);
        Node secondNode = new Node(2);
        ArrayList<Node> two = new ArrayList<>();
        two.add(firstNode);
        two.add(secondNode);
        MinMaxPriorityQueue<Node> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(two);
        Node peek = queue.peekLast();
        assertEquals(firstNode, peek);
    }



    public void testPeekLastNInteger() {
        ArrayList<Integer> N = new ArrayList<>();
        N.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create(N);
        int peek = queue.peekLast();
        int last = N.get(N.size()-1);
        assertEquals(last, peek);
    }
    public void testPeekLastNString() {
        ArrayList<String> N = new ArrayList<>();
        Collections.addAll(N, new String("1"), new String("2"), new String("3"), new String("4"));
        MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue.create(N);
        String peek = queue.peekLast();
        String last = N.get(N.size()-1);
        assertEquals(last, peek);
    }
    public void testPeekLastNNode() {
        ArrayList<Node> N = new ArrayList<>();
        Collections.addAll(N, new Node(1), new Node(2), new Node(3), new Node(4));
        MinMaxPriorityQueue<Node> queue = MinMaxPriorityQueue.create(N);
        Node peek = queue.peekLast();
        Node last = N.get(N.size()-1);
        assertEquals(last, peek);
    }


    public void testPeekLastReverseNInteger() {
        ArrayList<Integer> N = new ArrayList<>();
        N.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(N);
        int peek = queue.peekLast();
        int first = N.get(0);
        assertEquals(first, peek);
    }
    public void testPeekLastReverseNString() {
        ArrayList<String> N = new ArrayList<>();
        Collections.addAll(N, new String("1"), new String("2"), new String("3"), new String("4"));
        MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(N);
        String peek = queue.peekLast();
        String first = N.get(0);
        assertEquals(first, peek);
    }
    public void testPeekLastReverseNNode() {
        ArrayList<Node> N = new ArrayList<>();
        Collections.addAll(N, new Node(1), new Node(2), new Node(3), new Node(4));
        MinMaxPriorityQueue<Node> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(N);
        Node peek = queue.peekLast();
        Node first = N.get(0);
        assertEquals(first, peek);
    }

    public void testPeekFirstEmpty() {
        ArrayList<Integer> empty = new ArrayList<>();
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create(empty);
        assertEquals(null, queue.peekFirst());
    }
    public void testPeekFirstSingleInteger() {
        int singleInteger = 10;
        ArrayList<Integer> single = new ArrayList<>();
        single.add(singleInteger);
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create(single);
        int peek = queue.peekFirst();
        assertEquals(singleInteger, peek);
    }
    public void testPeekFirstSingleString() {
        String singleString = "testString";
        ArrayList<String> single = new ArrayList<>();
        single.add(singleString);
        MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue.create(single);
        String peek = queue.peekFirst();
        assertEquals(singleString, peek);
    }
    public void testPeekFirstSingleNode() {
        Node node = new Node(5);
        ArrayList<Node> single = new ArrayList<>();
        single.add(node);
        MinMaxPriorityQueue<Node> queue = MinMaxPriorityQueue.create(single);
        Node peek = queue.peekFirst();
        assertEquals(node, peek);
    }
    public void testPeekFirstTwoInteger() {
        int firstInteger = -1;
        int secondInteger = 10;
        ArrayList<Integer> two = new ArrayList<>();
        two.add(firstInteger);
        two.add(secondInteger);
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create(two);
        int peek = queue.peekFirst();
        assertEquals(firstInteger, peek);
    }
    public void testPeekFirstTwoString() {
        String firstString = "first";
        String secondString = "second";
        ArrayList<String> two = new ArrayList<>();
        two.add(firstString);
        two.add(secondString);
        MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue.create(two);
        String peek = queue.peekFirst();
        assertEquals(firstString, peek);
    }
    public void testPeekFirstTwoNode() {
        Node firstNode = new Node(1);
        Node secondNode = new Node(2);
        ArrayList<Node> two = new ArrayList<>();
        two.add(firstNode);
        two.add(secondNode);
        MinMaxPriorityQueue<Node> queue = MinMaxPriorityQueue.create(two);
        Node peek = queue.peekFirst();
        assertEquals(firstNode, peek);
    }


    public void testPeekFirstReverseTwoInteger() {
        int firstInteger = 1;
        int secondInteger = 2;
        ArrayList<Integer> two = new ArrayList<>();
        two.add(firstInteger);
        two.add(secondInteger);
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(two);
        int peek = queue.peekFirst();
        assertEquals(secondInteger, peek);
    }
    public void testPeekFirstReverseTwoString() {
        String firstString = "first";
        String secondString = "second";
        ArrayList<String> two = new ArrayList<>();
        two.add(firstString);
        two.add(secondString);
        MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(two);
        String peek = queue.peekFirst();
        assertEquals(secondString, peek);
    }
    public void testPeekFirstReverseTwoNode() {
        Node firstNode = new Node(1);
        Node secondNode = new Node(2);
        ArrayList<Node> two = new ArrayList<>();
        two.add(firstNode);
        two.add(secondNode);
        MinMaxPriorityQueue<Node> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(two);
        Node peek = queue.peekFirst();
        assertEquals(secondNode, peek);
    }



    public void testPeekFirstNInteger() {
        ArrayList<Integer> N = new ArrayList<>();
        N.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create(N);
        int peek = queue.peekFirst();
        int first = N.get(0);
        assertEquals(first, peek);
    }
    public void testPeekFirstNString() {
        ArrayList<String> N = new ArrayList<>();
        Collections.addAll(N, new String("1"), new String("2"), new String("3"), new String("4"));
        MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue.create(N);
        String peek = queue.peekFirst();
        String first = N.get(0);
        assertEquals(first, peek);
    }
    public void testPeekFirstNNode() {
        ArrayList<Node> N = new ArrayList<>();
        Collections.addAll(N, new Node(1), new Node(2), new Node(3), new Node(4));
        MinMaxPriorityQueue<Node> queue = MinMaxPriorityQueue.create(N);
        Node peek = queue.peekFirst();
        Node first = N.get(0);
        assertEquals(first, peek);
    }


    public void testPeekFirstReverseNInteger() {
        ArrayList<Integer> N = new ArrayList<>();
        N.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(N);
        int peek = queue.peekFirst();
        int last = N.get(N.size() - 1);
        assertEquals(last, peek);
    }
    public void testPeekFirstReverseNString() {
        ArrayList<String> N = new ArrayList<>();
        Collections.addAll(N, new String("1"), new String("2"), new String("3"), new String("4"));
        MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(N);
        String peek = queue.peekFirst();
        String last = N.get(N.size() - 1);
        assertEquals(last, peek);
    }
    public void testPeekFirstReverseNNode() {
        ArrayList<Node> N = new ArrayList<>();
        Collections.addAll(N, new Node(1), new Node(2), new Node(3), new Node(4));
        MinMaxPriorityQueue<Node> queue = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create(N);
        Node peek = queue.peekFirst();
        Node last = N.get(N.size() - 1);
        assertEquals(last, peek);
    }



// end




    // 默认是min heap？？
    public void testRemoveFirstWithIntegerMinHeapContainsOneElement() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.create();
        q.add(1);
        q.removeFirst();
        assertEquals(q.size(), 0);
    }

    public void testRemoveFirstWithStringMinHeapContainsOneElement() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.create();
        q.add("abc");
        q.removeFirst();
        assertEquals(q.size(), 0);
    }

    public void testRemoveFirstWithNodeMinHeapContainsOneElement() {
        MinMaxPriorityQueue<Node> q = MinMaxPriorityQueue.create();
        q.add(new Node(5));
        q.removeFirst();
        assertEquals(q.size(), 0);
    }


    public void testRemoveFirstWithIntegerMinHeapContainsTwoElements() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.create();
        q.add(1);
        q.add(2);
        q.removeFirst();
        assertEquals(q.size(), 1);
        assertEquals((int)(q.peek()), 2);
    }

    public void testRemoveFirstWithStringMinHeapContainsTwoElements() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.create();
        q.add("abc");
        q.add("bcd");
        q.removeFirst();
        assertEquals(q.size(), 1);
        assertEquals((String) (q.peek()), "bcd");
    }

    // find the error at line 428
    public void testRemoveFirstWithNodeMinHeapContainsTwoElements() { // print out same result for removeFirst removeLast
        MinMaxPriorityQueue<Node> minHeapq= MinMaxPriorityQueue.create();
        Node node1 = new Node(5);
        Node node2 = new Node(1);
        minHeapq.add(node1);
        minHeapq.add(node2);
        minHeapq.removeFirst();
        assertEquals(minHeapq.size(), 1);
        assertEquals(minHeapq.peek(), new Node(5));
    }

    public void testRemoveFirstWithIntegerMaxHeapContainsTwoElements() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        q.add(1);
        q.add(2);
        q.removeFirst();
        assertEquals(q.size(), 1);
        assertEquals((int)(q.peek()), 1);
    }

    public void testRemoveFirstWithStringMaxHeapContainsTwoElements() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        q.add("abc");
        q.add("bcd");
        q.removeFirst();
        assertEquals(q.size(), 1);
        assertEquals((String) (q.peek()), "abc");
    }

    public void testRemoveFirstWithNodeMaxHeapContainsTwoElements() {
        MinMaxPriorityQueue<Node> q =  MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        Node node1 = new Node(5);
        Node node2 = new Node(1);
        q.add(node1);
        q.add(node2);
        q.removeFirst();
        assertEquals(q.size(), 1);
        assertEquals(q.peek(), new Node(1));
    }










    // n elements

    //min
    public void testRemoveFirstWithIntegerMinHeapContainsNElements() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.create();
        Collections.addAll(q, 1, 7, 2, 56, 2, 5, 23, 68, 0, 3);
        q.removeFirst();
        assertEquals(q.size(), 9);
        assertEquals((int)(q.peek()), 1);
    }

    public void testRemoveFirstWithStringMinHeapContainsNElements() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.create();
        Collections.addAll(q, "apple", "orange", "pineapple", "banana");
        q.removeFirst();
        assertEquals(q.size(), 3);
        assertEquals((String) (q.peek()), "banana");
    }

    public void testRemoveFirstWithNodeMinHeapContainsNElements() { // print out same result for removeFirst removeLast
        MinMaxPriorityQueue<Node> q= MinMaxPriorityQueue.create();
        Collections.addAll(q, new Node(1), new Node(7), new Node(2), new Node(56), new Node(2), new Node(5), new Node(23), new Node(68), new Node(0), new Node(3));
        q.removeFirst();
        assertEquals(q.size(), 9);
        assertEquals(q.peek(), new Node(1));
    }


    //max
    public void testRemoveFirstWithIntegerMaxHeapContainsNElements() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        Collections.addAll(q, 1, 7, 2, 56, 2, 5, 23, 68, 0, 3,10, 99 , 6, 101);
        q.removeFirst();
        assertEquals(q.size(), 13);
        assertEquals((int)(q.peek()), 99);
    }

    public void testRemoveFirstWithStringMaxHeapContainsNElements() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        Collections.addAll(q, "apple", "orange", "pineapple", "banana" , "pear" , "zoo");
        q.removeFirst();
        assertEquals(q.size(), 5);
        assertEquals((String) (q.peek()), "pineapple");
    }

    public void testRemoveFirstWithNodeMaxHeapContainsNElements() {
        MinMaxPriorityQueue<Node> q =  MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        Collections.addAll(q, new Node(1), new Node(7), new Node(2), new Node(56), new Node(2), new Node(5), new Node(23), new Node(68), new Node(0), new Node(3));
        q.removeFirst();
        assertEquals(q.size(), 9);
        assertEquals(q.peek(), new Node(56));
    }







    //remove last

    public void testRemoveLastWithIntegerMinHeapContainsOneElement() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.create();
        q.add(1);
        q.removeLast();
        assertEquals(q.size(), 0);
    }

    public void testRemoveLastWithStringMinHeapContainsOneElement() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.create();
        q.add("abc");
        q.removeLast();
        assertEquals(q.size(), 0);
    }

    public void testRemoveLastWithNodeMinHeapContainsOneElement() {
        MinMaxPriorityQueue<Node> q = MinMaxPriorityQueue.create();
        q.add(new Node(5));
        q.removeLast();
        assertEquals(q.size(), 0);
    }


    public void testRemoveLastWithIntegerMinHeapContainsTwoElements() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.create();
        q.add(1);
        q.add(2);
        q.removeLast();
        assertEquals(q.size(), 1);
        assertEquals((int)(q.peek()), 1);
    }

    public void testRemoveLastWithStringMinHeapContainsTwoElements() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.create();
        q.add("abc");
        q.add("bcd");
        q.removeLast();
        assertEquals(q.size(), 1);
        assertEquals((String) (q.peek()), "abc");
    }

    // find the error at line 428
    public void testRemoveLastWithNodeMinHeapContainsTwoElements() { // print out same result for removeFirst removeLast
        MinMaxPriorityQueue<Node> minHeapq= MinMaxPriorityQueue.create();
        Node node1 = new Node(5);
        Node node2 = new Node(1);
        minHeapq.add(node1);
        minHeapq.add(node2);
        minHeapq.removeLast();
        assertEquals(minHeapq.size(), 1);
        assertEquals(minHeapq.peek(), new Node(1));
    }

    public void testRemoveLastWithIntegerMaxHeapContainsTwoElements() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        q.add(1);
        q.add(2);
        q.removeLast();
        assertEquals(q.size(), 1);
        assertEquals((int)(q.peek()), 2);
    }

    public void testRemoveLastWithStringMaxHeapContainsTwoElements() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        q.add("abc");
        q.add("bcd");
        q.removeLast();
        assertEquals(q.size(), 1);
        assertEquals((String) (q.peek()), "bcd");
    }

    public void testRemoveLastWithNodeMaxHeapContainsTwoElements() {
        MinMaxPriorityQueue<Node> q =  MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        Node node1 = new Node(5);
        Node node2 = new Node(1);
        q.add(node1);
        q.add(node2);
        q.removeLast();
        assertEquals(q.size(), 1);
        assertEquals(q.peek(), new Node(5));
    }






    // n elements

    //min
    public void testRemoveLastWithIntegerMinHeapContainsNElements() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.create();
        Collections.addAll(q, 1, 7, 2, 56, 2, 5, 23, 68, 0, 3);
        q.removeLast();
        assertEquals(q.size(), 9);
        assertEquals((int)(q.peek()), 0);
    }

    public void testRemoveLastWithStringMinHeapContainsNElements() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.create();
        Collections.addAll(q, "apple", "orange", "pineapple", "banana");
        q.removeLast();
        assertEquals(q.size(), 3);
        assertEquals((String) (q.peek()), "apple");
    }

    public void testRemoveLastWithNodeMinHeapContainsNElements() { // print out same result for removeFirst removeLast
        MinMaxPriorityQueue<Node> q= MinMaxPriorityQueue.create();
        Collections.addAll(q, new Node(1), new Node(7), new Node(2), new Node(56), new Node(2), new Node(5), new Node(23), new Node(68), new Node(0), new Node(3));
        q.removeLast();
        assertEquals(q.size(), 9);
        assertEquals(q.peek(), new Node(0));
    }


    //max
    public void testRemoveLastWithIntegerMaxHeapContainsNElements() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        Collections.addAll(q, 1, 7, 2, 56, 2, 5, 23, 68, 0, 3,10, 99 , 6, 101);
        q.removeLast();
        assertEquals(q.size(), 13);
        assertEquals((int)(q.peek()), 101);
    }

    public void testRemoveLastWithStringMaxHeapContainsNElements() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        Collections.addAll(q, "apple", "orange", "pineapple", "banana" , "pear" , "zoo");
        q.removeLast();
        assertEquals(q.size(), 5);
        assertEquals((String) (q.peek()), "zoo");
    }

    public void testRemoveLastWithNodeMaxHeapContainsNElements() {
        MinMaxPriorityQueue<Node> q =  MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        Collections.addAll(q, new Node(1), new Node(7), new Node(2), new Node(56), new Node(2), new Node(5), new Node(23), new Node(68), new Node(0), new Node(3));
        q.removeLast();
        assertEquals(q.size(), 9);
        assertEquals(q.peek(), new Node(68));
    }

    public void testPollFirstWithIntegerMinHeapContainsNoElement(){
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.create();
        assertNull(q.pollFirst());
    }


    public void testPollFirstWithIntegerMinHeapContainsOneElement() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.create();
        q.add(1);
        assertEquals(q.pollFirst().intValue(), 1);
    }

    public void testPollFirstWithStringMinHeapContainsOneElement() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.create();
        q.add("abc");
        assertEquals(q.pollFirst(), "abc");
    }

    public void testPollFirstWithNodeMinHeapContainsOneElement() {
        MinMaxPriorityQueue<Node> q = MinMaxPriorityQueue.create();
        q.add(new Node(5));
        assertEquals(q.pollFirst().data, 5);
    }


    public void testPollFirstWithIntegerMinHeapContainsTwoElements() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.create();
        q.add(1);
        q.add(2);
        assertEquals(q.pollFirst().intValue(), 1);
        assertEquals(q.peek().intValue(), 2);
    }

    public void testPollFirstWithStringMinHeapContainsTwoElements() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.create();
        q.add("abc");
        q.add("bcd");
        assertEquals(q.pollFirst(), "abc");
        assertEquals(q.peek(), "bcd");
    }

    public void testPollFirstWithNodeMinHeapContainsTwoElements() {
        MinMaxPriorityQueue<Node> q = MinMaxPriorityQueue.create();
        Node node1 = new Node(1);
        Node node2 = new Node(2);
        q.add(node1);
        q.add(node2);
        assertEquals(q.pollFirst().data, 1);
        assertEquals(q.peek().data, 2);
    }

    public void testPollFirstWithIntegerMaxHeapContainsTwoElements() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        q.add(1);
        q.add(Integer.MAX_VALUE + 1);
        assertEquals(q.pollFirst().intValue(), Integer.MAX_VALUE + 1);
        assertEquals(q.peek().intValue(), 1);

    }

    public void testPollFirstWithStringMaxHeapContainsTwoElements() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        q.add("abc");
        q.add("bcd");
        assertEquals(q.pollFirst(), "bcd");
        assertEquals(q.peek(), "abc");
    }

    public void testPollFirstWithNodeMaxHeapContainsTwoElements() {
        MinMaxPriorityQueue<Node> q =  MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        Node node1 = new Node(1);
        Node node2 = new Node(2);
        q.add(node1);
        q.add(node2);

        assertEquals(q.pollFirst().data, 2);
        assertEquals(q.peek().data, 1);
    }


    public void testPollFirstWithIntegerMinHeapContainsNElements() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.create();
        Collections.addAll(q, 1, 7, 2, 56, 2, 5, 23, 68, 0, 3);
        assertEquals(q.pollFirst().intValue(), 0);
        assertEquals(q.peek().intValue(), 1);
    }


    public void testPollFirstWithStringMinHeapContainsNElements() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.create();
        Collections.addAll(q, "apple", "orange", "pineapple", "banana");

        assertEquals(q.pollFirst(), "apple");
        assertEquals(q.peek(), "banana");
    }

    public void testPollFirstWithNodeMinHeapContainsNElements() {
        MinMaxPriorityQueue<Node> q= MinMaxPriorityQueue.create();
        Collections.addAll(q, new Node(1), new Node(7), new Node(2), new Node(56), new Node(2), new Node(5), new Node(23), new Node(68), new Node(0), new Node(3));
        assertEquals(q.pollFirst().data, 0);
        assertEquals(q.peek(), new Node(1));
    }


    public void testPollFirstWithIntegerMaxHeapContainsNElements() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        Collections.addAll(q, 1, 7, 2, 56, 2, 5, 23, 68, 0, 3,10, 99 , 6, 101);
        assertEquals(q.pollFirst().intValue(), 101);
        assertEquals(q.peek().intValue(), 99);
    }

    public void testPollFirstWithStringMaxHeapContainsNElements() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        Collections.addAll(q, "apple", "orange", "pineapple", "banana" , "pear" , "zoo");
        assertEquals(q.pollFirst(), "zoo");
        assertEquals(q.peek(), "pineapple");
    }

    public void testPollFirstWithNodeMaxHeapContainsNElements() {
        MinMaxPriorityQueue<Node> q =  MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        Collections.addAll(q, new Node(1), new Node(7), new Node(2), new Node(56), new Node(2), new Node(5), new Node(23), new Node(68), new Node(0), new Node(3));

        assertEquals(q.pollFirst().data, 68);
        assertEquals(q.peek(), new Node(56));
    }


    //poll last
    public void testPollLastWithIntegerMinHeapContainsNoElement(){
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.create();
        assertNull(q.pollLast());
    }


    public void testPollLastWithIntegerMinHeapContainsOneElement() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.create();
        q.add(1);
        assertEquals(q.pollLast().intValue(), 1);
    }

    public void testPollLastWithStringMinHeapContainsOneElement() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.create();
        q.add("abc");
        assertEquals(q.pollLast(), "abc");
    }

    public void testPollLastWithNodeMinHeapContainsOneElement() {
        MinMaxPriorityQueue<Node> q = MinMaxPriorityQueue.create();
        q.add(new Node(5));
        assertEquals(q.pollLast().data, 5);
    }


    public void testPollLastWithIntegerMinHeapContainsTwoElements() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.create();
        q.add(1);
        q.add(2);
        assertEquals(q.pollLast().intValue(), 2);
        assertEquals(q.peek().intValue(), 1);
    }

    public void testPollLastWithStringMinHeapContainsTwoElements() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.create();
        q.add("abc");
        q.add("bcd");
        assertEquals(q.pollLast(), "bcd");
        assertEquals(q.peek(), "abc");
    }

    public void testPollLastWithNodeMinHeapContainsTwoElements() {
        MinMaxPriorityQueue<Node> q = MinMaxPriorityQueue.create();
        Node node1 = new Node(1);
        Node node2 = new Node(2);
        q.add(node1);
        q.add(node2);
        assertEquals(q.pollLast().data, 2);
        assertEquals(q.peek().data, 1);
    }

    public void testPollLastWithIntegerMaxHeapContainsTwoElements() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        q.add(1);
        q.add(2);
        assertEquals(q.pollLast().intValue(), 1);
        assertEquals(q.peek().intValue(), 2);

    }

    public void testPollLastWithStringMaxHeapContainsTwoElements() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        q.add("abc");
        q.add("bcd");
        assertEquals(q.pollLast(), "abc");
        assertEquals(q.peek(), "bcd");
    }

    public void testPollLastWithNodeMaxHeapContainsTwoElements() {
        MinMaxPriorityQueue<Node> q =  MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        Node node1 = new Node(1);
        Node node2 = new Node(2);
        q.add(node1);
        q.add(node2);

        assertEquals(q.pollLast().data, 1);
        assertEquals(q.peek().data, 2);
    }


    public void testPollLastWithIntegerMinHeapContainsNElements() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.create();
        Collections.addAll(q, 1, 7, 2, 56, 2, 5, 23, 68, 0, 3);
        assertEquals(q.pollLast().intValue(), 68);
        assertEquals(q.peek().intValue(), 0);
    }

    public void testPollLastWithStringMinHeapContainsNElements() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.create();
        Collections.addAll(q, "apple", "orange", "pineapple", "banana");

        assertEquals(q.pollLast(), "pineapple");
        assertEquals(q.peek(), "apple");
    }

    public void testPollLastWithNodeMinHeapContainsNElements() {
        MinMaxPriorityQueue<Node> q= MinMaxPriorityQueue.create();
        Collections.addAll(q, new Node(1), new Node(7), new Node(2), new Node(56), new Node(2), new Node(5), new Node(23), new Node(68), new Node(0), new Node(3));
        assertEquals(q.pollLast().data, 68);
        assertEquals(q.peek(), new Node(0));
    }


    public void testPollLastWithIntegerMaxHeapContainsNElements() {
        MinMaxPriorityQueue<Integer> q = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        Collections.addAll(q, 1, 7, 2, 56, 2, 5, 23, 68, 0, 3,10, 99 , 6, 101);
        assertEquals(q.pollLast().intValue(), 0);
        assertEquals(q.peek().intValue(), 101);
    }

    public void testPollLastWithStringMaxHeapContainsNElements() {
        MinMaxPriorityQueue<String> q = MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        Collections.addAll(q, "apple", "orange", "pineapple", "banana" , "pear" , "zoo");
        assertEquals(q.pollLast(), "apple");
        assertEquals(q.peek(), "zoo");
    }

    public void testPollLastWithNodeMaxHeapContainsNElements() {
        MinMaxPriorityQueue<Node> q =  MinMaxPriorityQueue.orderedBy(Ordering.natural().reverse()).create();
        Collections.addAll(q, new Node(1), new Node(7), new Node(2), new Node(56), new Node(2), new Node(5), new Node(23), new Node(68), new Node(0), new Node(3));

        assertEquals(q.pollLast().data, 0);
        assertEquals(q.peek(), new Node(68));
    }
    public void testToArray() {
        ArrayList<Integer> N = new ArrayList<>();
        N.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create(N);
        assertEquals(N.size(), queue.toArray().length);
    }
    public void testCreateWithInitialsize() {
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create();
        assertEquals(MinMaxPriorityQueue.initialQueueSize(0, Integer.MAX_VALUE, queue), queue.capacity());
    }

    public void testClearQueue() {
        MinMaxPriorityQueue<Integer> q =  MinMaxPriorityQueue.orderedBy(Ordering.natural()).create(Arrays.asList(1, 2, 3));
        q.clear();
        assertEquals(null, q.peek());
        assertEquals(0, q.size());
    }

}