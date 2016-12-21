package com.jeecms.cms.manager.assist;

import java.util.List;

import com.jeecms.cms.entity.assist.CmsFriendlink;

public interface CmsFriendlinkMng {
	public List<CmsFriendlink> getList(Integer siteId, Integer ctgId,
			Boolean enabled);

	/**
	 * 获得友情链接
	 * @param siteId
	 * @param ctgId
	 * @param enabled
	 * @return
	 */
	public List<CmsFriendlink> getList1(Integer siteId, Integer ctgId,Integer channelId,
			Boolean enabled);
	
	public int countByCtgId(Integer ctgId);

	public CmsFriendlink findById(Integer id);

	public int updateViews(Integer id);

	public CmsFriendlink save(CmsFriendlink bean, Integer ctgId);

	public CmsFriendlink update(CmsFriendlink bean, Integer ctgId);

	public void updatePriority(Integer[] ids, Integer[] priorities);

	public CmsFriendlink deleteById(Integer id);

	public CmsFriendlink[] deleteByIds(Integer[] ids);
}