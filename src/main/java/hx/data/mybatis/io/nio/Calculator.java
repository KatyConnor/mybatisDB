package hx.data.mybatis.io.nio;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 思想:
 *			把一个字符串表达式,拆分成两个集合,一个集合包含计算中的数,一个集合包含计算中的符号
 *				1.把一个表达式中的运算子提取出来
 *				2.把一个表达式中的数提取出来
 *				3.遍历运算子集合,优先运算乘法和除法(遇到乘法除法的时候,移除运算子,并计算)
 *				4.之后剩下的就是加法和减法,依次遍历计算
 *				5.如果有括号的情况,递归调用
 *			计算的核心思路
 *				从运算子集合中取出一个运算子
 *				在取出运算子的位置从运算参数的集合中的取出参与运算的两个数 	参与运算的数是两个连续数
 *
 * @Author mingliang
 * @Date 2017-10-12 9:11
 */
public class Calculator {

    private final static ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");

    /**
     * 带括弧算法
     * @param exp 表达式
     * @return 结果
     */
    public static double calculator(String exp) {
        exp = exp.trim();
        // (-2.3*-3+32/(2*(-3+(-3))-3))*2+(3+5)　
        // 先找最里面的括号
        int leftKuo = exp.lastIndexOf('(');
        // (3+5)　
        // 1. 没有括号
        if (leftKuo == -1) {
            return calc(exp);
        } else {
            int rightKuo = exp.substring(leftKuo).indexOf(')') + leftKuo;
            //int rightKuo = exp.indexOf(')', leftKuo);

            // leftKuo 和 rightKuo之间的表达式单独运算
            double res = calc(exp.substring(leftKuo + 1, rightKuo));

            if (res < 0) {

            }
            // 拼接
            exp = exp.substring(0, leftKuo) + res + exp.substring(rightKuo + 1);

            // 2. 有括号

            return calculator(exp);
        }
    }

    /**
     * 不带括弧的核心算法
     * @param exp 表达式
     * @return	结果
     */
    public static double calc(String exp) {
        //1.把一个表达式中的运算子提取出来
        List<Character> operations = getOperation(exp);
        //2.把一个表达式中的数提取出来
        List<Double> numbers = getNumbers(exp);

        //遍历计算(乘法和除法)
        for (int i = 0; i < operations.size(); i++) {
            //遍历获取运算子
            char op = operations.get(i);
            //如果这个运算子是乘法或者除法
            if (op == '*' || op == '/') {
                //移除这个运算子
                Character remove = operations.remove(i);
                //需要在同一个位置取出对应的运算数
                Double double1 = numbers.remove(i);
                //因为list的特性 移除了之后整个数据会向前移动一位 		因此第二个数据就在当前的角标位置
                Double double2 = numbers.remove(i);
                //判断是乘法还是除法	做相应的运算
                double1 = (remove == '*' ? double1*double2 : double1/double2);
                //计算完成之后 还需要在同一个位置插入运算数
                numbers.add(i,double1);
            }
        }
        //遍历计算加法和减法
        while (!operations.isEmpty()) {
            //一次计算 所以每次都是第一个运算符
            char op = operations.remove(0);
            //需要在同一个位置取出对应的运算数
            Double double1 = numbers.remove(0);
            //因为list的特性 移除了之后整个数据会向前移动一位 		因此第二个数据就在当前的角标位置
            Double double2 = numbers.remove(0);
            //判断是乘法还是除法	做相应的运算
            double1 = (op == '-' ? double1-double2 : double1+double2);
            //计算完成之后 还需要在同一个位置插入运算数
            numbers.add(0,double1);
        }

        //计算完了以后 集合中还会剩下一个元素 就是结果
        return numbers.get(0);
    }

    /**
     * 把一个表达式中的运算子提取出来
     * @param exp 表达式
     * @return 运算子集合
     */
    private static List<Character> getOperation(String exp) {
        List<Character> operations = new ArrayList<>();
        //有些时候的"-"号表示的是负号 		需要区别
        exp = change(exp);
        StringTokenizer stringTokenizer = new StringTokenizer(exp, "@0123456789.");
        while (stringTokenizer.hasMoreElements()) {
            operations.add((stringTokenizer.nextElement() + "").charAt(0));
        }
        return operations;
    }

    /**
     * 把一个表达式中的数提取出来
     * @param exp 表达式
     * @return 参与运算的数的集合
     */
    private static List<Double> getNumbers(String exp) {
        List<Double> numbers = new ArrayList<>();
        //有时候的"-"号是表示负号 需要区别对待
        exp = change(exp);
        StringTokenizer stringTokenizer = new StringTokenizer(exp, "+-*/");
        while (stringTokenizer.hasMoreElements()) {
            // 数字字符串
            String numStr = stringTokenizer.nextElement() + "";
            // 判断前面是否是@打头
            if (numStr.charAt(0) == '@') {
                numStr = "-" + numStr.substring(1);
            }

            // 把数字字符串变成数字
            double number = Double.parseDouble(numStr);

            // 添加数字容器中
            numbers.add(number);
        }
        return numbers;
    }

    /**
     *
     * @param exp
     *            表达式
     * @return 把表示负数的-换成@
     */
    public static String change(String exp) {
        // 偷梁换柱 表示负数的- 换成@符号
        // 只有三种情况 第一位， 前面是*或/
        for (int i = 0; i < exp.length(); i++) {
            char o = exp.charAt(i);
            // 判断是否是-
            if (o == '-') {
                // 1. 第一位
                if (i == 0) {
                    exp = "@" + exp.substring(1);
                } else {
                    // 判断前一个字符
                    char q = exp.charAt(i - 1);
                    if (q == '*' || q == '/' || q == '+' || q == '-') {
                        // 表示负数
                        // 把当前位置变成@符号
                        exp = exp.substring(0, i) + "@" + exp.substring(i + 1);
                    }
                }
            }
        }// end for
        return exp;
    }

    public static Object cal(String expression) throws ScriptException {
        return jse.eval(expression);
    }
}
