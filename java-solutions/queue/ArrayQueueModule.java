package queue;

import java.util.NoSuchElementException;

public class ArrayQueueModule {
    private static Object[] array = new Object[10];
    private static int head = 0;
    private static int tail = 0;
    private static int size = 0;

    // Модель: очередь, представляющая собой последовательность элементов, упорядоченных по принципу FIFO
    // инвариант: 0 <= head, tail < array.length, size >= 0, элементы != null
    // Предусловия: enqueue/push - элемент не null; element/peek/dequeue/remove - очередь не пуста

    public static void enqueue(Object value) {
        // Пред: value != null
        // Пост: элемент добавлен в конец очереди, size увеличен на 1
        if (value == null) {
            throw new IllegalArgumentException("Error because value == null");
        }
        if (size == array.length) {
            resize();
        }
        array[tail] = value;
        tail = (tail + 1) % array.length;
        size++;
    }

    public static void push(Object value) {
        // Пред: value != null
        // Пост: элемент добавлен в начало очереди, size увеличен на 1
        if (value == null) {
            throw new IllegalArgumentException("Error because value == null");
        }
        if (size == array.length) {
            resize();
        }
        head = (head - 1 + array.length) % array.length;
        array[head] = value;
        size++;
    }

    private static void resize() {
        // Пред: массив заполнен (size == array.length)
        // Пост: емкость массива увеличена вдвое, порядок элементов сохранен
        Object[] newQueue = new Object[array.length * 2];
        for (int i = 0; i < size; i++) {
            newQueue[i] = array[(head + i) % array.length];
        }
        array = newQueue;
        head = 0;
        tail = size;
    }

    public static Object element() {
        // Пред: очередь не пуста (size > 0)
        // Пост: возвращен первый элемент без изменения очереди
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty!");
        }
        return array[head];
    }

    public static Object peek() {
        // Пред: очередь не пуста (size > 0)
        // Пост: возвращен последний элемент без изменения очереди
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty!");
        }
        return array[(tail - 1 + array.length) % array.length];
    }

    public static Object dequeue() {
        // Пред: очередь не пуста (size > 0)
        // Пост: первый элемент удален и возвращен, size уменьшен на 1
        Object value = element();
        array[head] = null;
        head = (head + 1) % array.length;
        size--;
        return value;
    }

    public static Object remove() {
        // Пред: очередь не пуста (size > 0)
        // Пост: последний элемент удален и возвращен, size уменьшен на 1
        Object value = peek();
        tail = (tail - 1 + array.length) % array.length;
        array[tail] = null;
        size--;
        return value;
    }

    public static int size() {
        // Пред: нет
        // Пост: возвращен текущий размер очереди
        return size;
    }

    public static boolean isEmpty() {
        // Пред: нет
        // Пост: возвращено true, если очередь пуста, иначе false
        return size == 0;
    }

    public static void clear() {
        // Пред: нет
        // Пост: очередь очищена, размер сброшен до начального
        array = new Object[1];
        head = tail = size = 0;
    }

    public static Object get(int index) {
        // Пред: 0 <= index < size
        // Пост: возвращен элемент по индексу (отсчет с хвоста)
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Error: incorrect index value. Beyond the required range (index < 0 || index >= size)");
        }
        int current = (tail - 1 - index + array.length) % array.length;
        return array[current];
    }

    public static void set(int index, Object value) {
        // Пред:
        //   - value != null
        //   - 0 <= index < size
        // Пост: элемент по индексу (отсчет с хвоста) заменен на value
        if (value == null) {
            throw new IllegalArgumentException("Error because value == null");
        }
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Error: incorrect index value. Beyond the required range (index < 0 || index >= size)");
        }
        int current = (tail - 1 - index + array.length) % array.length;
        array[current] = value;
    }
}