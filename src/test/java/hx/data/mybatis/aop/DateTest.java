package hx.data.mybatis.aop;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author mingliang
 * @Date 2017-11-30 18:09
 */
public class DateTest {

    public static void main(String[] args) throws Exception {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i<10 ; i++){
            User user = new User();
            user.setDate("2017-11-30 17:50:0"+i);
            user.setCreateTime(getNow(i));
            user.setCreateTime1(Timestamp.valueOf("2017-11-30 17:50:0"+i));
//            System.out.println(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(getNow(i)));
            userList.add(user);
        }

        ComparatorDate c = new ComparatorDate();
        c.setSortOrder(ComparatorDate.ASC);
        c.setCompareField("createTime1");
        Collections.sort(userList, c);

//        for (User user : userList){
//            System.out.println(user.getDate());
//        }
        System.out.println("-------------------------------------------------------------");
        for (User user : userList){
            System.out.println(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(user.getCreateTime1()));
        }
//        getNow();
    }

    private static Date getNow(int i){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY,calendar.get(Calendar.HOUR_OF_DAY)+i);
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
        return calendar.getTime();
    }
}
