package queue;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.function.Function;

public abstract class AbstractQueue<T> implements Queue<T> {
    @Override
    public Queue<T> map(Function<? super T, T> foo) {
        AbstractQueue<T> result = createInstance();
        for (T element : elements()) {
            result.enqueue(foo.apply(element));
        }
        return result;
    }

    @Override
    public Queue<T> filter(Predicate<? super T> subInterface) {
        AbstractQueue<T> result = createInstance();
        for (T element : elements()) {
            if (subInterface.test(element)) {
                result.enqueue(element);
            }
        }
        return result;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    protected abstract AbstractQueue<T> createInstance();

    protected abstract List<T> elements();

    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return elements().get(elements().size() - 1);
    }

    public T remove() {
        T element = peek();
        dequeueLast();
        return element;
    }

    public void push(T element) {
        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }
        enqueueFirst(element);
    }

    protected abstract void enqueueFirst(T element);

    protected abstract void dequeueLast();

    public T get(int index) {
        List<T> elements = elements();
        if (index < 0 || index >= elements.size()) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        return elements.get(index);
    }

    public void set(int index, T value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        List<T> elements = elements();
        if (index < 0 || index >= elements.size()) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        updateElement(index, value);
    }

    protected abstract void updateElement(int index, T value);
}
