package search;

public class BinarySearch {

    // Предусловие:
    // 1. Массив a отсортирован по невозрастанию:
    //    Для всех i: 0 <= i < a.length - 1 -> a[i] >= a[i + 1]
    // 2. a != null (массив не является null)
    // 3. 0 <= a.length (размер массива неотрицателен)
    // 4. x - целое число (x принадлежит множеству int)

    // Постусловие:
    // 1. Возвращает минимальный индекс i, при котором a[i] <= x
    // 2. Если такого i нет, возвращает a.length (означает, что x больше всех элементов массива)
    public static int iterativeBinarySearch(int x, int[] a) {
        int left = 0, right = a.length;

        // инвариант:
        // 1. 0 <= left <= right <= a.length
        // 2. a[left-1] > x (если left > 0) и a[right] <= x (если right < a.length)
        while (left < right) {
            int mid = left + (right - left) / 2;

            // a[mid] > x означает, что x может быть только правее
            if (a[mid] > x) {
                left = mid + 1; // исключаем левую часть
            } else {
                right = mid; // mid может быть решением, но продолжаем сужать диапазон
            }
        }
        // :NOTE: why while ends?
        // Пояснение завершения цикла:
        // На каждой итерации right - left уменьшается как минимум на 1.
        // Начальное значение right - left <= a.length, следовательно, цикл выполнится не более a.length раз.

        // Постусловие соблюдено: left - минимальный индекс, при котором a[left] <= x,
        // либо a.length, если такого элемента нет
        return left;
    }

    // Доказательство корректности:
    // База: перед первым шагом left = 0, right = a.length, что соответствует инварианту.
    // Переход: на каждой итерации диапазон уменьшается (right - left становится меньше).
    // Завершение: цикл завершается, когда left == right, тогда left - минимальный индекс i, где a[i] <= x.

    // Предусловие:
    // 1. Массив a отсортирован по невозрастанию (a[i] >= a[i + 1])
    // 2. a != null
    // 3. 0 <= left <= right <= a.length
    // 4. x - целое число

    // Постусловие:
    // 1. Возвращает минимальный индекс i, при котором a[i] <= x
    // 2. Если такого i нет, возвращает a.length
    public static int recursiveBinarySearch(int x, int[] a, int left, int right) {
        if (left >= right) {
            return left;
        }

        int mid = left + (right - left) / 2;

        if (a[mid] > x) {
            return recursiveBinarySearch(x, a, mid + 1, right);
        } else {
            return recursiveBinarySearch(x, a, left, mid);
        }
    }

    // Доказательство корректности:
    // База: если left >= right, то left - минимальный индекс, где a[left] <= x.
    // Переход: каждая рекурсивная ветка уменьшает диапазон поиска.
    // Завершение: рекурсия завершается, когда left == right, что соответствует требуемому индексу.

    // :NOTE: what is args
    public static void main(String[] args) {
        // Предусловия для args:
        // 1. args != null
        // 3. Все элементы args[0] и args[1..] — целые числа
        // 4. Массив a (args[1..]) отсортирован по невозрастанию
        int x = Integer.parseInt(args[0]);
        int[] a = new int[args.length - 1];
        for (int i = 0; i < a.length; i++) {
            a[i] = Integer.parseInt(args[i + 1]);
        }

        int iterativeResult = iterativeBinarySearch(x, a);
        int recursiveResult = recursiveBinarySearch(x, a, 0, a.length);

        System.out.println(iterativeResult);

        // Проверка корректности: итеративный и рекурсивный методы должны давать одинаковый результат
        assert iterativeResult == recursiveResult : "Результаты итеративного и рекурсивного поиска должны совпадать";
    }
}
