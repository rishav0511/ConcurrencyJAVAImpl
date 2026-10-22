package org.example;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeLRUCache<K, V> {
    private static class Node<K,V> {
        K key;
        V value;
        Node<K,V> prev;
        Node<K,V> next;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node<K, V>> cache;
    private final Node<K, V> head, tail;
    private final ReentrantLock lock = new ReentrantLock();

    public ThreadSafeLRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>(capacity);
        this.head = new Node(null, null);
        this.tail = new Node(null, null);
        head.next = tail;
        tail.prev = head;
    }

    public V get(K key) {
        lock.lock();
        try {
            Node<K,V> node = cache.get(key);
            if (node == null) {
                return null;    //cache miss
            }
            // Cache hit: move the accessed node to the head
            moveToHead(node);
            return node.value;
        } finally {
            lock.unlock();
        }
    }

    public void put(K key, V value) {
        lock.lock();
        try {
            Node <K,V> node = cache.get(key);
            if (node != null) {
                // Update existing node and move to head
                node.value = value;
                moveToHead(node);
            } else {
                // Create new node
                Node<K, V> newNode = new Node<>(key, value);
                cache.put(key, newNode);
                addNode(newNode);

                // Evict least recently used if capacity exceeded
                if (cache.size() > capacity) {
                    Node<K,V> tail = popTail();
                    cache.remove(tail.key);
                    System.out.println("Evicted key: " + tail.key);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    // --- Linked List Helper Methods (Must be called while lock is held) ---

    // Always add the new node right after the dummy head
    private void addNode(Node node) {
        node.prev=head;
        node.next=head.next;
        head.next.prev = node;
        head.next=node;
    }

    // Remove an existing node from the linked list
    private void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;
        prevNode.next = nextNode;
        nextNode.prev=prevNode;
    }

    // Move an accessed node to the head
    private void moveToHead(Node node) {
        removeNode(node);
        addNode(node);
    }

    // Pop the least recently used item from right before the dummy tail
    private Node<K,V> popTail() {
        Node<K,V> last = tail.prev;
        removeNode(last);
        return last;
    }

    public void printCacheState() {
        lock.lock();
        try {
            System.out.print("Current Cache (MRU -> LRU): ");
            Node<K, V> curr = head.next;
            while (curr != tail) {
                System.out.print("[" + curr.key + "=" + curr.value + "] ");
                curr = curr.next;
            }
            System.out.println();
        } finally {
            lock.unlock();
        }
    }
}
