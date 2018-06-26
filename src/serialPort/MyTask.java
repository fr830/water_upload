package serialPort;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;
import java.sql.PreparedStatement;


/**
 * Created by 94829 on 2018-06-10.
 */
public class MyTask extends TimerTask {
    String rongjieyang;
    String PH;
    String zuodu;
    String Wen;
    MyTask(String rongjieyang,String PH,String zuodu,String Wen){
        this.rongjieyang = rongjieyang;
        this.PH = PH;
        this.zuodu = zuodu;
        this.Wen = Wen;
    }
    
    @Override
    public void run() {
     
        Connection con = null;
        //驱动程序名
        String driver = "com.mysql.jdbc.Driver";
        //URL指向要访问的数据库名mydata
        String url = "jdbc:mysql://localhost:3306/water";
        //MySQL配置时的用户名
        String user = "root";
        //MySQL配置时的密码
        String password = "xxxxxxxxxxxxxxxxx";
        //遍历查询结果集
        try {
            //加载驱动程序
            Class.forName(driver);
            //1.getConnection()方法，连接MySQL数据库！！
            con = DriverManager.getConnection(url,user,password);
            if(!con.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            //2.创建statement类对象，用来执行SQL语句！！

            PreparedStatement psql;
            //预处理添加数据，其中有两个参数--“？”
            psql = con.prepareStatement("insert into water (id,equipment_id,rongjieyang,ph,zuodu,wendu,upload_time) "
                    + "values(?,?,?,?,?,?)");
            psql.setInt(1, 1);              //设置参数1，创建id为3212的数据
            psql.setString(2, "18060423");      //设置参数2，name 为王刚
            psql.setString(3, rongjieyang);      //设置参数2，name 为王刚
            psql.setString(4, PH);
            psql.setString(5, zuodu);
            psql.setString(6, Wen);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format( new Date());
            psql.setString(7,time);
            psql.executeUpdate();
            psql.close();
            con.close();
        } catch(ClassNotFoundException e) {
            //数据库驱动类异常处理
            System.out.println("Sorry,can`t find the Driver!");
            e.printStackTrace();
        } catch(SQLException e) {
            //数据库连接失败异常处理
            e.printStackTrace();
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }finally{
            System.out.println("数据库数据成功获取！！");
            try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}