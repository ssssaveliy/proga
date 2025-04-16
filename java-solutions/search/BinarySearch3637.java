package search;

// :NOTE: do O(log n)
public class BinarySearch3637 {

    // Предусловие: a != null && a.length > 0
    // Постусловие: возвращает индекс первого вхождения x в a или -1, если x не найден
    // Доказательство корректности:
    // 1. Вызов findBreakpoint гарантирует, что breakpoint корректно определяет границу изменения монотонности массива.
    // 2. Вызовы binarySearchFirstOccurrence выполняются только на правильно отсортированных подмассивах.
    // 3. Если элемент найден в левой части, он возвращается сразу. Если нет, поиск продолжается в правой части.
    // 4. Если элемент отсутствует в обоих подмассивах, возвращается -1.
    public static int search(int x, int[] a) {
        int breakpoint = findBreakpoint(a);

        // Пост для findBreakpoint: 0 <= breakpoint <= a.length &&
        //   breakpoint == 0, если a полностью отсортирован по убыванию,
        //   иначе breakpoint == i, где a[i - 1] < a[i] (и i минимально)
        boolean useRecursive = (a.length % 2 == 0);

        // useRecursive == true, если a.length четное, иначе false
        int leftResult;
        // breakpoint >= 0
        if (breakpoint == 0) {
            // Массив полностью отсортирован по убыванию
            leftResult = binarySearchFirstOccurrence(x, a, 0, a.length, true, useRecursive);
            // R == минимальный индекс i в a[left:right], где a[i] == x, если x там есть, иначе -1
            // Пост для binarySearchFirstOccurrence: возвращает индекс первого вхождения x в a[0:a.length] или -1
        } else {
            // Массив состоит из двух частей: убывающей и возрастающей
            leftResult = binarySearchFirstOccurrence(x, a, 0, breakpoint, true, useRecursive);
            // Пост для binarySearchFirstOccurrence: возвращает индекс первого вхождения x в a[0:breakpoint] или -1
        }
        if (leftResult != -1) {
            return leftResult;
        }
        // ищем в возрастающей части
        return binarySearchFirstOccurrence(x, a, breakpoint, a.length, false, useRecursive);
    }

    // Предусловие: a != null && a.length > 0
    // Постусловие: возвращает индекс точки разбиения массива
    // Доказательство корректности:
    // 1. Алгоритм проходит по массиву один раз, сравнивая соседние элементы.
    // 2. Если обнаружен переход от убывания к возрастанию, возвращается соответствующий индекс.
    // 3. Если перехода нет, возвращается 0, что корректно для полностью убывающего массива.
    private static int findBreakpoint(int[] a) {
        int left = 0, right = a.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (a[mid] < a[mid + 1]) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left == a.length - 1 ? 0 : left + 1;
    }

    // Предусловие: a != null && left <= right <= a.length
    // Постусловие: возвращает индекс первого вхождения x или -1
    // Доказательство корректности:
    // 1. Функция выбирает правильный алгоритм (итеративный или рекурсивный), обеспечивая корректную обработку.
    // 2. Оба алгоритма корректно обрабатывают убывающие и возрастающие подмассивы.
    private static int binarySearchFirstOccurrence(int x, int[] a, int left, int right, boolean descending, boolean useRecursive) {
        if (useRecursive) {
            return recursiveBinarySearchFirstOccurrence(x, a, left, right, descending);
        } else {
            return iterativeBinarySearchFirstOccurrence(x, a, left, right, descending);
        }
    }

    // Предусловие: a != null && left <= right <= a.length
    // Постусловие: возвращает индекс первого вхождения x или -1
    // Доказательство корректности:
    // 1. инвариант: a[left:right] отсортирован.
    // 2. Поиск выполняется на корректном диапазоне, гарантируя уменьшение диапазона в каждом шаге.
    private static int iterativeBinarySearchFirstOccurrence(int x, int[] a, int left, int right, boolean descending) {
        int result = -1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (a[mid] == x) {
                result = mid;
                right = mid;
            } else if (a[mid] < x) {
                if (descending) {
                    right = mid;
                } else {
                    left = mid + 1;
                }
            } else {
                if (descending) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }
        }
        return result;
    }

    // Предусловие: a != null && left <= right <= a.length
    // Постусловие: возвращает индекс первого вхождения x или -1
    // Доказательство корректности:
    // 1. База: если left >= right, поиск завершается корректно.
    // 2. Рекурсивный шаг: в каждом вызове диапазон уменьшается, приближая решение.
    private static int recursiveBinarySearchFirstOccurrence(int x, int[] a, int left, int right, boolean descending) {
        if (left >= right) {
            return -1;
        }
        int mid = left + (right - left) / 2;
        if (a[mid] == x) {
            int leftResult = recursiveBinarySearchFirstOccurrence(x, a, left, mid, descending);
            return (leftResult == -1) ? mid : leftResult;
        } else if (a[mid] < x) {
            if (descending) {
                return recursiveBinarySearchFirstOccurrence(x, a, left, mid, descending);
            } else {
                return recursiveBinarySearchFirstOccurrence(x, a, mid + 1, right, descending);
            }
        } else {
            if (descending) {
                return recursiveBinarySearchFirstOccurrence(x, a, mid + 1, right, descending);
            } else {
                return recursiveBinarySearchFirstOccurrence(x, a, left, mid, descending);
            }
        }
    }

    public static void main(String[] args) {
        // Предусловия для args:
        // 1. args != null 
        // 2. args.length >= 2 (x и как минимум один элемент массива a)
        // 3. Все элементы args[0] и args[1..] — целые числа
        // 4. Массив a (args[1..]) отсортирован по невозрастанию до findBreakpoint, а после по возрастанию (или 0)
        if (args.length < 2) {
            System.err.println("Usage: java search.BinarySearch3637 x a1 a2 ... an");
            return;
        }

        int x = Integer.parseInt(args[0]);
        int[] a = new int[args.length - 1];
        for (int i = 0; i < a.length; i++) {
            a[i] = Integer.parseInt(args[i + 1]);
        }
        System.out.println(search(x, a));
    }
}