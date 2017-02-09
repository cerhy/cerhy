package com.jeecms.cms.dao.main.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.jeecms.cms.entity.main.Focus;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class BlogDao {
	public List<Focus> findMaxFocusCount(String path) {
		Connection con = (Connection) DBConnection.getConnection(path);
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Focus> list = new ArrayList<Focus>();
		try {
			ps = (PreparedStatement) con.prepareStatement("	SELECT * from (select focus_user_id,focus_user_name,count(1) cnt from jc_focus GROUP BY focus_user_id) p where  p.cnt in (SELECT max(b.cnt) from (select focus_user_id,focus_user_name,count(1) cnt from jc_focus GROUP BY focus_user_id) b) limit 50");
			rs = ps.executeQuery();
			while (rs.next()) {
				list.add(new Focus(rs.getInt("focus_user_id"),rs.getString("focus_user_name"),rs.getInt("cnt")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return list;
	}
	
}
