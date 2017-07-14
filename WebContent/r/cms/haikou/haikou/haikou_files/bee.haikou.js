/* (c) dbs, ghljj.com. */

var cfg = {

	menu: [
		[{ '教育视窗': '9805', '视频播报': 'c62743', '海口研训简报': 'c57340', '区校信息': 'c60754' }],
		[{ '2014年': 'c255965', '2013年': 'c156234', '2012年': 'c99510', '2011年': 'c82682', '2010年': 'c57349', '2009年': 'c57337', '2008年': 'c57338', '2007年': 'c57355', '2006年': 'c57356', '2005年以前': 'http://subsite.teacher.com.cn/haikou/index.aspx' }],
		[{ '高中': null, '高中语文': '628917', '高中数学': '611681', '高中英语': '611683', '高中物理': '650165', '高中化学': '628418', '高中生物': '635848', '思想政治': '625986', '高中历史': '629302', '高中地理': '611682', '通用技术': '611693', '信息技术': '621873', '高中体育': '638706', '高中音乐': '635257', '高中美术': '619429' },
		{ '初中': null, '初中语文': '651940', '初中数学': '611686', '初中英语': '619294', '初中物理': '629919', '初中化学': '628418', '初中生物': '635848', '思想品德': '625986', '初中历史': '629302', '初中地理': '611682', '信息技术': '621873', '初中体育': '638706', '初中音乐': '635257', '初中美术': '619429' },
		{ '小学': null, '小学语文': '621561', '小学数学': '634983', '小学英语': '623892', '小学品德': '650179', '小学科学': '624308', '小学体育': '638706', '小学美术': '619429', '小学音乐': '635257' },
		{ '综合': null, '学前教育': '611691', '心理健康教育': '611678', /*'心理健康': 'c48694',*/ '综合实践活动': '603329' }],
		//{ '综合': null, '学前教育': '611691', '心理健康教育': '611678', /*'心理健康': 'c48694',*/ '综合实践活动': '603329', '旧站': null, '小学语文': 'http://blog.cersp.com/index/1157488.jspx', '小学品德': 'http://blog.cersp.com/index/1159383.jspx', '体育与健康': 'http://blog.cersp.com/index/1097263.jspx', '小学音乐': 'http://blog.cersp.com/index/1097275.jspx', '中学美术 ': 'http://blog.cersp.com/index/1025311.jspx', '初中语文': 'http://blog.cersp.com/index/1157091.jspx' }],
		[{ '教育科学研究': '644413' }],
		[{ '职业教育': '611687' }],
		[{ '中小校长培训': 'c57323', '德育主任培训': 'c57625', '教研主任培训': 'c83115', '教导主任培训': 'c83116', '幼儿园长培训': 'c57624' }],
		[{ '青骨成长助推站': '29100', '中小学骨干教师': '29100', '幼儿园骨干教师': '29100', '教师全员培训': '29100', '国培项目培训': '29100' }],
		[{ '海南省中小学教师继教网': 'http://tea.hersp.com', '海口市中小学教师继教网': '9701', '海口市教师学分管理网': '29100' }],
		[{ '信息化能力提升': '9808', '十二五整合课题': '9808', '十一五整合课题': '9808', '英特尔-未来教育': '9808', '信息化教育项目': '9808' }],
		[{ '幼儿教育': '611691' }],
		[{ '教育科研资源': '9806', '教师培训资源': '9807' }],
		[{ '行政办公室': '9211', '教科研部': '9806', '培训部': '9807', '信息部': '9808', '海口市教育学会': '9805' }],
		[{ '党建工作': '9809', '教育工会': '9805', '教代会': '9805' }]
	],

	rtl: ['', '', '', '', '', '', '', '', '', '', '', 'rtl', 'rtl'],

	initSlip: function(ar, pad, handle) {
		ar.each(function(s, idx) {
			new Element('span', { html: idx + 1 }).inject(handle).addEvent('click', function(evt) {
				evt.stop();
				this.addClass('active').getSiblings().removeClass('active');
				pad.set('html', s);
			});
		});
	}

};

if (window.Burn)
Burn.implement({

	load: function() {
		var getTopicHtml = function(data, len, showdate) {
			var title = data.Title;
			var showtitle = len && title.length > len;
			if (showtitle) title = title.substr(0, len) + '...';
			var html =
				'<li>' +
				'<a' + (showtitle ? ' title="' + data.Title + '"' : '') + ' href="http://res.hersp.com/content/' + data.ContentId + '">' + title.html() + '</a>' +
				(showdate ? (' <span class="dt">(' + data.PublishDate.utc('md') + ')</span>') : '') +
				'</li>';
			return html;
		};

		var success = function(text, doc) {
			var tmp = {};
			Xml.getNodes(doc, '//Topic').each(function(item) {
				var s = getTopicHtml(item, item.Owner == '9211' ? null : 30, true);
				var id = 'post_' + item.Owner;
				if (!tmp[id]) tmp[id] = '';
				tmp[id] += s;
			});
			for (var p in tmp) {
				var s = '<ul class="dot">' + tmp[p] + '</ul>';
				$(p).set('html', s);
				this.template = this.template.replace('<!-- ' + p + ' -->', s);
			};
		}.bind(this);

		var url = 'http://my.hersp.com/admin/blog';
		var data = { Action: 'dxQuery', Owner: '9211|10,9805|40,9806|40,9807|40,9808|40' };
		this.requestX(url, data, success);
	}

});
