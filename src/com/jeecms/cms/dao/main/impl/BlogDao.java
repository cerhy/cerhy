package com.jeecms.cms.dao.main.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.jeecms.cms.entity.main.Columns;
import com.jeecms.core.entity.CmsUser;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class BlogDao {

	public List<Columns> findByUserId(int user_id,String path) {
		Connection con = (Connection) DBConnection.getConnection(path);
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Columns> list = new ArrayList<Columns>();
		try {
			ps = (PreparedStatement) con.prepareStatement("select * from columns where user_id =? order by `order_id` desc");
			ps.setInt(1, user_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				list.add(new Columns(rs.getInt("column_id"),rs.getInt("user_id"),rs.getString("column_name"),rs.getInt("order_id")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return list;
	}
	
	public void addColumn(int user_id,String name,String order,String path) {
		Connection con = (Connection) DBConnection.getConnection(path);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if(null == order){
				order = "0";
			}
			ps = (PreparedStatement) con.prepareStatement("insert into columns(`user_id`,`column_name`,`order_id`) values (?,?,?)");
			ps.setInt(1, user_id);
			ps.setString(2, name);
			ps.setInt(3,Integer.parseInt(order));
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
	}
	
	public void deleteColumn(int column_id,String path) {
		Connection con = (Connection) DBConnection.getConnection(path);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = (PreparedStatement) con.prepareStatement("delete  from columns where column_id = ?");
			ps.setInt(1, column_id);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
	}
	
	public void updateColumn(int column_id,String name,String path) {
		Connection con = (Connection) DBConnection.getConnection(path);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = (PreparedStatement) con.prepareStatement("update columns set column_name= ? where column_id= ? ");
			ps.setString(1, name);
			ps.setInt(2, column_id);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
	}
	
	public void updateColumn(int column_id,int order_id,String path) {
		Connection con = (Connection) DBConnection.getConnection(path);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = (PreparedStatement) con.prepareStatement("update columns set order_id= ?  where column_id= ? ");
			ps.setInt(1, order_id);
			ps.setInt(2, column_id);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
	}
	
	public void updateColumn(int column_id,String name,int order_id,String path) {
		Connection con = (Connection) DBConnection.getConnection(path);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = (PreparedStatement) con.prepareStatement("update columns set order_id= ?,column_name=? where column_id= ? ");
			ps.setInt(1, order_id);
			ps.setString(2, name);
			ps.setInt(3, column_id);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
	}
	
	
}
