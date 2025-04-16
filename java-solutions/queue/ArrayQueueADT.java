package queue;

import java.util.NoSuchElementException;

public class ArrayQueueADT<T> {
    private T[] array;
    private int head;
    private int tail;
    private int size;

    // Модель: очередь, представляющая собой последовательность элементов, упорядоченных по принципу FIFO    // инвариант: array != null, 0 <= head, tail < array.length, size >= 0
    // Предусловия: enqueue/push - элемент не null; element/peek/dequeue/remove - очередь не пуста
    // инвариант:
    //   - array != null
    //   - 0 <= head, tail < array.length
    //   - size == количество элементов в очереди
    //   - все элементы очереди != null 

    @SuppressWarnings("unchecked")
    public ArrayQueueADT() {
        // Пред: нет
        // Пост: создана пустая очередь с начальной емкостью 1
        array = (T[]) new Object[1];
        head = tail = size = 0;
    }

    public static <T> void enqueue(ArrayQueueADT<T> queue, T value) {
        // Пред: value != null
        // Пост: элемент добавлен в конец очереди, size увеличен на 1
        if (value == null) {
            throw new IllegalArgumentException("Error because value == null");
        }
        if (queue.size == queue.array.length) {
            resize(queue);
        }
        queue.array[queue.tail] = value;
        queue.tail = (queue.tail + 1) % queue.array.length;
        queue.size++;
    }


    public static <T> void push(ArrayQueueADT<T> queue, T value) {
        // Пред: value != null
        // Пост: элемент добавлен в начало очереди, size увеличен на 1
        if (value == null) {
            throw new IllegalArgumentException("Error because value == null");
        }
        if (queue.size == queue.array.length) {
            resize(queue);
        }
        queue.head = (queue.head - 1 + queue.array.length) % queue.array.length;
        queue.array[queue.head] = value;
        queue.size++;
    }

    @SuppressWarnings("unchecked")
    private static <T> void resize(ArrayQueueADT<T> queue) {
        // Пред: массив заполнен (size == array.length)
        // Пост: емкость массива увеличена вдвое, порядок элементов сохранен
        T[] newQueue = (T[]) new Object[queue.array.length * 2];
        for (int i = 0; i < queue.size; i++) {
            newQueue[i] = queue.array[(queue.head + i) % queue.array.length];
        }
        queue.array = newQueue;
        queue.head = 0;
        queue.tail = queue.size;
    }

    public static <T> T element(ArrayQueueADT<T> queue) {
        // Пред: очередь не пуста (size > 0)
        // Пост: возвращен первый элемент без изменения очереди
        if (isEmpty(queue)) {
            throw new NoSuchElementException("Queue is empty!");
        }
        return queue.array[queue.head];
    }

    public static <T> T peek(ArrayQueueADT<T> queue) {
        // Пред: очередь не пуста (size > 0)
        // Пост: возвращен последний элемент без изменения очереди
        if (isEmpty(queue)) {
            throw new NoSuchElementException("Queue is empty!");
        }
        return queue.array[(queue.tail - 1 + queue.array.length) % queue.array.length];
    }

    public static <T> T dequeue(ArrayQueueADT<T> queue) {
        // Пред: очередь не пуста (size > 0)
        // Пост: первый элемент удален и возвращен, size уменьшен на 1
        T value = element(queue);
        queue.array[queue.head] = null;
        queue.head = (queue.head + 1) % queue.array.length;
        queue.size--;
        return value;
    }

    public static <T> T remove(ArrayQueueADT<T> queue) {
        // Пред: очередь не пуста (size > 0)
        // Пост: последний элемент удален и возвращен, size уменьшен на 1
        T value = peek(queue);
        queue.tail = (queue.tail - 1 + queue.array.length) % queue.array.length;
        queue.array[queue.tail] = null;
        queue.size--;
        return value;
    }

    public static <T> int size(ArrayQueueADT<T> queue) {
        // Пред: нет
        // Пост: возвращен текущий размер очереди
        return queue.size;
    }

    public static <T> boolean isEmpty(ArrayQueueADT<T> queue) {
        // Пред: нет
        // Пост: возвращено true, если очередь пуста, иначе false
        return queue.size == 0;
    }

    @SuppressWarnings("unchecked")
    public static <T> void clear(ArrayQueueADT<T> queue) {
        // Пред: нет
        // Пост: очередь очищена, размер сброшен до начального
        queue.array = (T[]) new Object[1];
        queue.head = queue.tail = queue.size = 0;
    }

    public static <T> T get(ArrayQueueADT<T> queue, int index) {
        // Пред: 0 <= index < size
        // Пост: возвращен элемент по индексу (отсчет с хвоста)
        if (index < 0 || index >= queue.size) {
            throw new IndexOutOfBoundsException("Error: incorrect index value. Beyond the required range (index < 0 || index >= size)");
        }
        int current = (queue.tail - 1 - index + queue.array.length) % queue.array.length;
        return queue.array[current];
    }

    public static <T> void set(ArrayQueueADT<T> queue, int index, T value) {
        // Пред:
        //   - value != null
        //   - 0 <= index < size
        // Пост: элемент по индексу (отсчет с хвоста) заменен на value
        if (value == null) {
            throw new IllegalArgumentException("Error because value == null");
        }
        if (index < 0 || index >= queue.size) {
            throw new IndexOutOfBoundsException("Error: incorrect index value. Beyond the required range (index < 0 || index >= size)");
        }
        int current = (queue.tail - 1 - index + queue.array.length) % queue.array.length;
        queue.array[current] = value;
    }
}