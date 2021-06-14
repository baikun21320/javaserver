package jhc.io.cn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import com.mysql.cj.jdbc.CallableStatement;
public class UserDao {
	public static void main(String[] args) throws ClassNotFoundException,SQLException{
		
//		System.out.println(UserDao.insert(new User(5, "baikun")));
// 		UserDao.insert(new User(2, "baikun"));
//		System.out.println(UserDao.find("baikun"));
//		System.out.println(UserDao.update(new User(5, "lihua")));
//		System.out.println(UserDao.find("lihua"));
//		UserDao.delete("lihua");
//		UserDao.shijian();
	}
	public static void shijian() {
		try {
		Connection conn = JdbcUtils.getConnection();
		String sql = "{call get_name_count(?,?)}";
		java.sql.CallableStatement call = conn.prepareCall(sql);
		call.setString(1, "b");
		call.registerOutParameter(2, Types.INTEGER);
		call.execute();
		System.out.println("count = " + call.getInt(2));
		} catch (Exception e) {
			  e.printStackTrace();
		}
	}
	public static int insert(User user) {
		int i = 0;
	    try {
	    Connection conn = JdbcUtils.getConnection();
	    String sql = "insert into user (id,name) values(?,?)";
	    System.out.println(sql);
	    PreparedStatement pstmt;
	        pstmt = (PreparedStatement) conn.prepareStatement(sql);
	        pstmt.setInt(1, user.getId());
	        pstmt.setString(2, user.getUsername());
	        i = pstmt.executeUpdate();
	        pstmt.close();
	        conn.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return i;
	}
	public static int delete(String name) {
		Connection conn = null;
	    int i = 0;
	    String sql = "delete from user where Name='" + name + "'";
	    System.out.println(sql);
	    PreparedStatement pstmt;
	    try {
	    	conn = JdbcUtils.getConnection();
	        pstmt = (PreparedStatement) conn.prepareStatement(sql);
	        i = pstmt.executeUpdate();
	        System.out.println("resutl: " + i);
	        pstmt.close();
	        conn.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return i;
	}
	
	public static int update(User user) {
	    Connection conn = null;
	    int i = 0;
	    String sql = "update user set name='" + user.getUsername() + "' where id=" + user.getId();
	    System.out.println(sql);
	    PreparedStatement pstmt;
	    try {
	    	conn = JdbcUtils.getConnection();
	        pstmt = (PreparedStatement) conn.prepareStatement(sql);
	        i = pstmt.executeUpdate();
	        System.out.println("resutl: " + i);
	        pstmt.close();
	        conn.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return i;
	}
	
	public static Boolean find(String name){
		Connection conn = null;
		Statement pstmt = null;
		ResultSet rs = null;
		String username = null;
		String sql = "select * from user where name='" + name + "'";
		try{
			conn = JdbcUtils.getConnection();
			System.out.println(sql);
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			rs = pstmt.executeQuery(sql);
			if(rs.next()){
				System.out.println(username = rs.getString("name"));
			}
		} catch (SQLException e) {
	        e.printStackTrace();
	    }
		if(username !=null) {
			return true;
		}else {
			return false;
		}
	}
	public static Boolean findId(int id){
		Connection conn = null;
		Statement pstmt = null;
		ResultSet rs = null;
		String username = null;
		String sql = "select * from user where id='" + id + "'";
		try{
			conn = JdbcUtils.getConnection();
			System.out.println(sql);
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			rs = pstmt.executeQuery(sql);
			if(rs.next()){
				System.out.println(username = rs.getString("name"));
			}
		} catch (SQLException e) {
	        e.printStackTrace();
	    }
		if(username !=null) {
			return true;
		}else {
			return false;
		}
	}
}
