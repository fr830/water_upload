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
        //����������
        String driver = "com.mysql.jdbc.Driver";
        //URLָ��Ҫ���ʵ����ݿ���mydata
        String url = "jdbc:mysql://localhost:3306/water";
        //MySQL����ʱ���û���
        String user = "root";
        //MySQL����ʱ������
        String password = "xxxxxxxxxxxxxxxxx";
        //������ѯ�����
        try {
            //������������
            Class.forName(driver);
            //1.getConnection()����������MySQL���ݿ⣡��
            con = DriverManager.getConnection(url,user,password);
            if(!con.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            //2.����statement���������ִ��SQL��䣡��

            PreparedStatement psql;
            //Ԥ����������ݣ���������������--������
            psql = con.prepareStatement("insert into water (id,equipment_id,rongjieyang,ph,zuodu,wendu,upload_time) "
                    + "values(?,?,?,?,?,?)");
            psql.setInt(1, 1);              //���ò���1������idΪ3212������
            psql.setString(2, "18060423");      //���ò���2��name Ϊ����
            psql.setString(3, rongjieyang);      //���ò���2��name Ϊ����
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
            //���ݿ��������쳣����
            System.out.println("Sorry,can`t find the Driver!");
            e.printStackTrace();
        } catch(SQLException e) {
            //���ݿ�����ʧ���쳣����
            e.printStackTrace();
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }finally{
            System.out.println("���ݿ����ݳɹ���ȡ����");
            try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}