package tests;

import queue.ArrayQueue;
import queue.ArrayQueueADT;
import queue.ArrayQueueModule;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.NoSuchElementException;

public class QueueTests {
    @Test
    public void testArrayQueueModule() {
        ArrayQueueModule.clear();
        assertTrue(ArrayQueueModule.isEmpty());
        ArrayQueueModule.enqueue("test");
        assertEquals(1, ArrayQueueModule.size());
        assertEquals("test", ArrayQueueModule.dequeue());

        ArrayQueueModule.push("first");
        assertEquals("first", ArrayQueueModule.element());
        ArrayQueueModule.enqueue("second");
        assertEquals("second", ArrayQueueModule.peek());
        assertEquals("second", ArrayQueueModule.remove());
        assertEquals(1, ArrayQueueModule.size());
        ArrayQueueModule.set(0, "updated");
        assertEquals("updated", ArrayQueueModule.get(0));
    }

    @Test
    public void testArrayQueueADT() {
        ArrayQueueADT<Integer> queue = new ArrayQueueADT<>();
        ArrayQueueADT.enqueue(queue, 5);
        assertEquals(1, ArrayQueueADT.size(queue));
        assertEquals(Integer.valueOf(5), ArrayQueueADT.dequeue(queue));

        ArrayQueueADT.push(queue, 10);
        ArrayQueueADT.enqueue(queue, 20);
        assertEquals(Integer.valueOf(20), ArrayQueueADT.peek(queue));
        assertEquals(Integer.valueOf(20), ArrayQueueADT.remove(queue));
        assertEquals(Integer.valueOf(10), ArrayQueueADT.get(queue, 0));
        ArrayQueueADT.set(queue, 0, 30);
        assertEquals(Integer.valueOf(30), ArrayQueueADT.element(queue));
    }

    @Test
    public void testArrayQueue() {
        ArrayQueue<String> queue = new ArrayQueue<>();
        queue.enqueue("hello");
        queue.enqueue("world");
        assertEquals("hello", queue.dequeue());
        assertEquals("world", queue.element());
        queue.clear();
        assertTrue(queue.isEmpty());

        queue.push("first");
        queue.push("second");
        assertEquals("second", queue.element());
        assertEquals("first", queue.peek());
        assertEquals("first", queue.remove());
        queue.set(0, "updated");
        assertEquals("updated", queue.get(0));
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyDequeueModule() {
        ArrayQueueModule.clear();
        ArrayQueueModule.dequeue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullEnqueueADT() {
        ArrayQueueADT<String> queue = new ArrayQueueADT<>();
        ArrayQueueADT.enqueue(queue, null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidGetQueue() {
        ArrayQueue<Integer> queue = new ArrayQueue<>();
        queue.enqueue(1);
        queue.get(1);
    }
}

// java -cp "out;junit-4.13.2.jar;hamcrest-core-1.3.jar" org.junit.runner.JUnitCore tests.QueueTests
