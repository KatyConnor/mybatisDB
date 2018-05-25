package hx.data.mybatis.aop;

import java.util.Collections;

/**
 * @Author mingliang
 * @Date 2017-11-29 17:31
 */
public class Test {
    public static void main(String[] args) {
        // 在栈上创建引用变量user，尚未实例化，即堆中对象不存在
        User user = null;
        // 在栈上创建引用变量user1并进行实例化，即堆中给对象user1分配内存空间
        User user1 = new User();
        // 栈上创建initBean，change 并分配内存空间,并创建方法的局部引用变量user2，
        // 将栈中引用变量user1的引用地址的实际值复制一个副本给user2
        initBean1(user1);
        // 执行之后栈中，堆中，方法区的user不变， 而change，和initBean1 将会从栈中清楚
    }

    private static void initBean1(User user2){
        // 在这里局部引用变量指向的堆中对象和user指向的对象时同一个
        user2.setName("张三");
        user2.setAge(28);
        user2.setTelNo("18695486987");
    }

    private static void change(User user2){
        // 在堆中创建了一个新的User对象，并把引用地址值赋值给引用变量user2,这时在栈中的引用变量
        // user 和 user2 的引用地址就不是指向的同一个对象。这里修改的是新的对象值
        user2 = new User();
        user2.setName("张三");
        user2.setAge(28);
        user2.setTelNo("18695486987");
    }


//    public class Dog {
//        Collar c;
//        String name;
//        //1.main()方法位于栈上
//        public static void main(String[] args) {
//            //2.在栈上创建引用变量d,但Dog对象尚未存在
//            Dog d;
//            //3.创建新的Dog对象，并将其赋予d引用变量
//            d = new Dog();
//            //4.将引用变量的一个副本传递给go()方法
//            d.go(d);
//        }
//        //5.将go()方法置于栈上，并将dog参数作为局部变量
//        void go(Dog dog){
//            //6.在堆上创建新的Collar对象，并将其赋予Dog的实例变量
//            c = new Collar();
//        }
//        //7.将setName()添加到栈上，并将dogName参数作为其局部变量
//        void setName(String dogName){
//            //8.name的实例对象也引用String对象
//            name =dogName;
//        }
//        //9.程序执行完成后，setName()将会完成并从栈中清除，此时，局部变量dogName也会消失，尽管它所引用的String仍在堆上
//    }

















    // 在栈上创建变量 x,y 并存储 x,y的值
//    int x = 0;
//    int y = 0;

    /* 在栈上分配方法change的存储空间，并且arg1，arg2作为方法的局部变量被创建分配在栈区，
       并将 x,y的副本传递给方法change()参数arg1，arg2
    */
//    change(x,y);
    // 在change方法执行完之后，change()就会从栈中清楚，在这里 x,y的值没有做任何修改
//        System.out.println("X = "+x+" Y = "+y);

//    private static void change(int arg1,int arg2){
        // 在方法中修改的实际是局部变量 arg1，arg2 的实际值，对x，y没有任何影响
//        arg1 = arg1+2;
//        arg2 = arg2+5;
//    }











//    String str3 = new String("a");
//    String str1 = "a";
//    String str2 = "a";
//    String str4 = new String();
//    str4 = "a";
//        if (str1 == str2){
//        System.out.println(" str1 = str2 ");
//    }
//
//        if (str1 == str3){
//        System.out.println(" str1 = str3 ");
//    }
//
//        if (str1 == str4){
//        System.out.println(" str1 = str4 ");
//    }
//
//        if (str3 == str4){
//        System.out.println(" str3 = str4 ");
//    }
}
