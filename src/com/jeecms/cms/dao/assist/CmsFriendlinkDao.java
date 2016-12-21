package com.jeecms.cms.dao.assist;

import java.util.List;

import com.jeecms.cms.entity.assist.CmsFriendlink;
import com.jeecms.common.hibernate4.Updater;

public interface CmsFriendlinkDao {
	public List<CmsFriendlink> getList(Integer siteId, Integer ctgId,
			Boolean enabled);

	/**
	 * 友情链接
	 * @param siteId
	 * @param ctgId
	 * @param channelId
	 * @param enabled
	 * @return
	 */
	public List<CmsFriendlink> getList1(Integer siteId, Integer ctgId,Integer channelId,
			Boolean enabled);
	
	public int countByCtgId(Integer ctgId);

	public CmsFriendlink findById(Integer id);

	public CmsFriendlink save(CmsFriendlink bean);

	public CmsFriendlink updateByUpdater(Updater<CmsFriendlink> updater);

	public CmsFriendlink deleteById(Integer id);
}