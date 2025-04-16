package queue;
import java.util.function.Predicate;
import java.util.function.Function;

public interface Queue<T> {
    void enqueue (T element);
    void clear();
    boolean isEmpty();
    int size();
    T element();
    T dequeue();
    Queue<T> filter(Predicate<? super T> predicate);
    Queue<T> map(Function<? super T, T> foo);
    T peek();
    T remove();
    void push(T element);
    T get(int index);
    void set(int index, T value);
}
