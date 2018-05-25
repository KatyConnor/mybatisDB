package hx.data.mybatis.utils;

import java.util.*;
import java.util.function.Consumer;

/**
 *  自定义一个集合类
 * @Author mingliang
 * @Date 2017-09-26 14:43
 */
public class HxArraylist<E> extends HxList<E> {

    private Object[] elementData;// 存放元素的数组
    private Object[] EMPTY_ARRAY = {};// 空数组
    private static final int DEFAULT_CAPACITY = 10;// 默认数组长度
    private int size;// 数组中元素的个数
    private transient int modCount = 0;// 线性表修改的次数

    public HxArraylist() {
        // 默认情况下为空数组
        this.elementData = EMPTY_ARRAY;
    }

    /**
     *  指定大小
     * @param initCapacity
     */
    public HxArraylist(int initCapacity) {
        // 传入的数量为负数，抛出异常
        if (initCapacity < 0) {
            throw new IllegalArgumentException("参数错误:" + initCapacity);
        } else if (initCapacity == 0) {// 空数组
            elementData = EMPTY_ARRAY;
        } else {
            elementData = new Object[initCapacity];
        }
    }

    /**
     *  直接添加collection<E>
     * @param c
     */
    public HxArraylist(Collection<E> c){
        Object[] obj = c.toArray();
        // 将Collection中的数据拷贝出来，防止污染
        if ((size = obj.length) != 0) {
            elementData = new Object[DEFAULT_CAPACITY];
            System.arraycopy(obj, 0, elementData, 0, size);
        }else {
            elementData = EMPTY_ARRAY;
        }
    }

    /**
     *
     * 删除第index个元素,将原数组的值才分成两次复制，第一次是删除下表‘index’之前的，第二次是‘index’之后的，
     * 从而达到删除目的
     *
     * @param index
     * @return
     */
    public E remove(int index) {
        Object[] newObj = new Object[elementData.length];
        E obj = (E) elementData[index];
        // 将array数组中的元素拷贝到新数组中
        System.arraycopy(elementData, 0, newObj, 0, index);
        System.arraycopy(elementData, index + 1, newObj, index, size - index - 1);
        elementData = newObj;
        size--;
        modCount++;
        return obj;
    }

    public E get(int index) {
        return (E) elementData[index];
    }

    public void set(int index, E e) {
        elementData[index] = e;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.elementData.length <= 0?true:false;
    }

    @Override
    public boolean contains(Object o) {
        Iterator<E> it = iterator();
        if (o==null) {
            while (it.hasNext())
                if (it.next()==null)
                    return true;
        } else {
            while (it.hasNext())
                if (o.equals(it.next()))
                    return true;
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new HxArraylist.Itr();
    }

    @Override
    public Object[] toArray() {
        return  Arrays.copyOf(elementData, size);
    }

    /**
     * 添加一个元素到线性表尾部->先判断数组大小和元素个数是否相同－》相同的话，需要扩容
     *
     * @param e
     * @return
     */
    @Override
    public boolean add(Object e) {
        elementData = judgeIsGrow();
        elementData[size] = e;
        size++;
        modCount++;
        return true;
    }

    /**
     * 在第index位置插入一个元素
     *
     * @param index
     * @param e
     * @return
     */
    public boolean add(int index, E e) {
        checkArgument(index);
        elementData = judgeIsGrow();
        Object[] newObj = new Object[elementData.length];
        // 将array数组中的元素拷贝到新数组中
        System.arraycopy(elementData, 0, newObj, 0, index);
        System.arraycopy(elementData, index, newObj, index + 1, size- index);
        newObj[index] = e;
        elementData = newObj;
        size++;
        modCount++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        Iterator<E> it = iterator();
        if (o==null) {
            while (it.hasNext()) {
                if (it.next()==null) {
                    it.remove();
                    return true;
                }
            }
        } else {
            while (it.hasNext()) {
                if (o.equals(it.next())) {
                    it.remove();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c){
            if (add(e)){
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        modCount++;
        // clear to let GC do its work
        for (int i =0; i< elementData.length; i++){
            elementData[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection c) {
        Objects.requireNonNull(c);
        return batchRemove(c, false);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c){
            if (!contains(o)){
                return false;
            }
        }
        return true;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    /**
     *  判断是否扩容
     * @return
     */
    private Object[] judgeIsGrow() {
        if (size == elementData.length) {
            // 确定新数组的大小－－> new出来－> 将原来数组的数据拷贝到新数组中
            int newSize = 0;
            if (size == 0) {
                newSize = DEFAULT_CAPACITY;
            } else {
                // 扩容一倍，最大容量Integer.MAX_VALUE
                newSize = size < Integer.MAX_VALUE / 2 ? size << 1 : Integer.MAX_VALUE;
            }
            Object[] newArray = new Object[newSize];
            System.arraycopy(elementData, 0, newArray, 0, size);
            elementData = newArray;
        }
        return elementData;
    }

    /**
     * 参数检查,传入的位置标号是否为有效数字
     * @param index
     */
    private void checkArgument(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("参数错误:" + index);
        } else if (index > size) {
            throw new IllegalArgumentException("超过数组长度，参数错误:" + index);
        }
    }

    /**
     *  循环数组，将删除的元素之后的元素往前移位
     * @param c
     * @param complement
     * @return
     */
    private boolean batchRemove(Collection<?> c, boolean complement) {
        final Object[] elementData = this.elementData;
        int r = 0, w = 0;
        boolean modified = false;
        try {
            for (; r < size; r++){
                if (c.contains(elementData[r]) == complement){
                    elementData[w++] = elementData[r];
                }
            }
        } finally {
            // Preserve behavioral compatibility with AbstractCollection,
            // even if c.contains() throws.
            if (r != size) {
                System.arraycopy(elementData, r,
                        elementData, w,
                        size - r);
                w += size - r;
            }
            if (w != size) {
                // clear to let GC do its work
                for (int i = w; i < size; i++)
                    elementData[i] = null;
                modCount += size - w;
                size = w;
                modified = true;
            }
        }
        return modified;
    }

    private class Itr implements Iterator<E> {
        int cursor;       // index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such
        int expectedModCount = modCount;

        public boolean hasNext() {
            return cursor != size;
        }

        public E next() {
            checkForComodification();
            int i = cursor;
            if (i >= size)
                throw new NoSuchElementException();
            Object[] elementData = HxArraylist.this.elementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i + 1;
            return (E) elementData[lastRet = i];
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                HxArraylist.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super E> consumer) {
            Objects.requireNonNull(consumer);
            final int size = HxArraylist.this.size;
            int i = cursor;
            if (i >= size) {
                return;
            }
            final Object[] elementData = HxArraylist.this.elementData;
            if (i >= elementData.length) {
                throw new ConcurrentModificationException();
            }
            while (i != size && modCount == expectedModCount) {
                consumer.accept((E) elementData[i++]);
            }
            // update once at end of iteration to reduce heap write traffic
            cursor = i;
            lastRet = i - 1;
            checkForComodification();
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }



    public static void main(String[] args) {
//        int size = 4;
//        System.out.println((size << 1)+"----"+Integer.MAX_VALUE);



//        int[] ints = new int[100000000];
//        long startTime = System.currentTimeMillis();
//        for (int i =0; i< 100000000; i++){
//            ints[i] = i;
//        }
//        System.out.println(System.currentTimeMillis() - startTime);
//        startTime = System.currentTimeMillis();
//        int[] ints1 = new int[100000000];
//        for (int i =0; i< 100000000; i++){
//            ints1[i] = ints[i];
//        }
//        System.out.println(System.currentTimeMillis() - startTime);
//
//        int[] copyint = new int[100000000];
//        startTime =  System.currentTimeMillis();
//        System.arraycopy(ints,0,copyint,0,100000000);
//        System.out.println(System.currentTimeMillis() - startTime);
//
//        Object[] arrayList = new Object[100000000];
//        int size = 100000000;
//        startTime =  System.currentTimeMillis();
//        Arrays.copyOf(arrayList, size, Object[].class);
//        System.out.println(System.currentTimeMillis() - startTime);

//        long startTime =  System.currentTimeMillis();
//        int[] oneInt = new int[100000000];
//        System.out.println(System.currentTimeMillis()- startTime);
//
//        startTime =  System.currentTimeMillis();
//        int[] copyint = new int[100000000];
//        System.arraycopy(oneInt,0,copyint,0,100000000);
//        oneInt = copyint;
//        System.out.println(System.currentTimeMillis()- startTime);

//      List<String> alist = new ArrayList<>();
//      for (int i = 0; i < 10; i++) {
////        list.add("data "+i);
//          alist.add("data "+i);
//      }
//        HxList<String> list = new HxArraylist<>(alist);
//        System.out.println(list.size());

//      int[] s = {};
//      int size = 0;
//        if (size == s.length) {
//            // 确定新数组的大小－－> new出来－> 将原来数组的数据拷贝到新数组中
//            int newSize = 0;
//            if (size == 0) {
//                newSize = DEFAULT_CAPACITY;
//            } else {
//                // 扩容一倍，最大容量Integer.MAX_VALUE
//                newSize = size < Integer.MAX_VALUE / 2 ? size << 1 : Integer.MAX_VALUE;
//            }
//            int[] newArray = new int[newSize];
//            System.arraycopy(s, 0, newArray, 0, size);
//            s = newArray;
//        }
//        s[0] = 1;
//        System.out.println(s[0]+",length="+s.length);



//        final Object[] objects = {1,2,3,4,5,6,7,8,9,10};
//        for(int i = 1; i <=10; i++){
//            objects[i-1] = i+i;
//        }
//        for(int i = 0; i <10; i++){
//            System.out.println(objects[i]);
//        }

//
//        Object[] ints = new Object[100];
//        for (int i =0; i< 100; i++){
//            ints[i] = i;
//        }
//        Object[] copyint = new Object[90];
//        copyint = Arrays.copyOf(ints, 100, copyint.getClass());
////        System.arraycopy(ints,0,copyint,0,90000000);
//
//        for (int i =0; i< copyint.length; i++){
//            System.out.println(copyint[i]);
//        }

//        final List<String> list = new LinkedList<>();
//        List<String> alist = new ArrayList<>();
//        list.add("dsfsdfd");
//        list.add(0,"dff");
//        System.out.println(list.get(0));

        //= new WhereSqlEntity();


//        WhereSqlEntity sqlEntity2 = new WhereSqlEntity();
//        WhereSqlEntity sqlEntity = sqlEntity2;
//        WhereSqlEntity sqlEntity1 = new WhereSqlEntity();
//        sqlEntity1.setAnnotation("equal");
//        sqlEntity2 = sqlEntity1;
//        if (sqlEntity == null){
//            System.out.println("null");
//            sqlEntity = sqlEntity1;
//            System.out.println(sqlEntity.getAnnotation());
//        }else {
//            System.out.println("not null");
//            System.out.println(sqlEntity.getAnnotation());
//        }

//        List<String> list = new LinkedList<>();
        List<String> list = new ArrayList<>();
        list.add("12");
        list.add("13");
        list.add("14");
        for (String str : list){
            System.out.println(str);
        }
        List<String> list1 = new LinkedList<>();
        list1.add("22");
        list1.add("23");
        list1.add("24");
        for (String str : list1){
            System.out.println(str);
        }
        list1.iterator();
//
//        list1.addAll(list);

    }



}
