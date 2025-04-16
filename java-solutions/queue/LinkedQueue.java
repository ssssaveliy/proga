package queue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class LinkedQueue<T> extends AbstractQueue<T> {
    private static class IntermediateClass<T> {
        T data;
        IntermediateClass<T> next;

        IntermediateClass(T data) {
            this.data = data;
        }
    }

    private IntermediateClass<T> head;
    private IntermediateClass<T> tail;
    private int size;

    @Override
    protected AbstractQueue<T> createInstance() {
        return new LinkedQueue<>();
    }

    @Override
    protected List<T> elements() {
        List<T> list = new ArrayList<>();
        Iterator<T> iterator = iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    @Override
    protected void enqueueFirst(T element) {
        push(element);
    }

    public Iterator<T> iterator() {
        return new Iterator<>() {
            private IntermediateClass<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }

    @Override
    public void enqueue(T element) {
        if (element == null) {
            throw new IllegalArgumentException("element == null");
        }
        IntermediateClass<T> newIntermediate = new IntermediateClass<>(element);
        if (tail == null) {
            head = tail = newIntermediate;
        } else {
            tail.next = newIntermediate;
            tail = newIntermediate;
        }
        size++;
    }

    @Override
    public void clear() {
        head = tail = null;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T element() {
        if (isEmpty()) {
            throw new NoSuchElementException("T element() isEmpty");
        }
        return head.data;
    }

    @Override
    public T dequeue() {
        T data = element();
        head = head.next;
        if (head == null) {
            tail = null;
        }
        size--;
        return data;
    }

    @Override
    protected void dequeueLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        if (head == tail) {
            clear();
            return;
        }
        IntermediateClass<T> current = head;
        while (current.next != tail) {
            current = current.next;
        }
        tail = current;
        tail.next = null;
        size--;
    }

    @Override
    protected void updateElement(int index, T value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        IntermediateClass<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        current.data = value;
    }

    @Override
    public void push(T element) {
        IntermediateClass<T> newNode = new IntermediateClass<>(element);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head = newNode;
        }
        size++;
    }
}