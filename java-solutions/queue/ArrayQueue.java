package queue;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ArrayQueue<T> extends AbstractQueue<T> {
    private T[] queue;
    private int head;
    private int tail;
    private int size;

    // модель: очередь, представляющая собой последовательность элементов, упорядоченных по принципу FIFO
    // инвариант:
    //   - queue != null
    //   - 0 <= head, tail < queue.length
    //   - size >= 0
    //   - элементы не равны null

    @SuppressWarnings("unchecked")
    public ArrayQueue() {
        // Пред: нет (конструктор)
        // Пост: создана пустая очередь с начальной емкостью 10
        queue = (T[]) new Object[1];
        head = tail = size = 0;
    }

    public void enqueue(T value) {
        // Пред: value != null
        // Пост: элемент добавлен в конец очереди, size увеличен на 1
        if (value == null) {
            throw new IllegalArgumentException("Error because value == null");
        }
        if (size == queue.length) resize();
        queue[tail] = value;
        tail = (tail + 1) % queue.length;
        size++;
    }

    public void push(T value) {
        // Пред: value != null
        // Пост: элемент добавлен в начало очереди, size увеличен на 1
        if (value == null) {
            throw new IllegalArgumentException("Error because value == null");
        }
        if (size == queue.length) {
            resize();
        }
        head = (head - 1 + queue.length) % queue.length;
        queue[head] = value;
        size++;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        // Пред: массив заполнен (size == queue.length)
        // Пост: емкость массива увеличена вдвое, порядок элементов сохранен
        T[] newQueue = (T[]) new Object[queue.length * 2];
        for (int i = 0; i < size; i++) {
            newQueue[i] = queue[(head + i) % queue.length];
        }
        queue = newQueue;
        head = 0;
        tail = size;
    }

    public T element() {
        // Пред: очередь не пуста (size > 0)
        // Пост: возвращен первый элемент без изменения очереди
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty!");
        }
        return queue[head];
    }

    @Override
    public T peek() {
        // Пред: очередь не пуста (size > 0)
        // Пост: возвращен последний элемент без изменения очереди
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty!");
        }
        return queue[(tail - 1 + queue.length) % queue.length];
    }

    public T dequeue() {
        // Пред: очередь не пуста (size > 0)
        // Пост: первый элемент удален и возвращен, size уменьшен на 1
        T value = element();
        queue[head] = null;
        head = (head + 1) % queue.length;
        size--;
        return value;
    }

    @Override
    public T remove() {
        // Пред: очередь не пуста (size > 0)
        // Пост: последний элемент удален и возвращен, size уменьшен на 1
        T value = peek();
        tail = (tail - 1 + queue.length) % queue.length;
        queue[tail] = null;
        size--;
        return value;
    }

    public int size() {
        // Пред: нет
        // Пост: возвращен текущий размер очереди
        return size;
    }

    public boolean isEmpty() {
        // Пред: нет
        // Пост: возвращено true, если очередь пуста, иначе false
        return size == 0;
    }

    @Override
    protected AbstractQueue<T> createInstance() {
        return new ArrayQueue<>();
    }

    @Override
    protected List<T> elements() {
        List<T> elements = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            elements.add(queue[(head + i) % queue.length]);
        }
        return elements;
    }

    @Override
    protected void enqueueFirst(T element) {
        push(element);
    }

    @Override
    protected void dequeueLast() {
        remove();
    }

    @Override
    protected void updateElement(int index, T value) {
        set(index, value);
    }

    @SuppressWarnings("unchecked")
    public void clear() {
        // Пред: нет
        // Пост: очередь очищена, размер сброшен до начального
        queue = (T[]) new Object[1];
        head = tail = size = 0;
    }

    @Override
    public T get(int index) {
        // Пред: 0 <= index < size
        // Пост: возвращен элемент по индексу (отсчет с хвоста)
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Error: incorrect index value. Beyond the required range (index < 0 || index >= size)");
        }
        int current = (tail - 1 - index + queue.length) % queue.length;
        return queue[current];
    }

    public void set(int index, T value) {
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
        int current = (tail - 1 - index + queue.length) % queue.length;
        queue[current] = value;
    }
}