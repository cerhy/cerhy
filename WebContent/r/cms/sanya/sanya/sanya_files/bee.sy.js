/* (c) dbs, ghljj.com. */

var cfg = {

	menu: [
		[{ '课程管理': 'c65183', '校本研修': 'c126859', '质量监测': 'c5524', '样本校建设': 'c127146' }],
		[{ '高中语文': 'c5497', '高中数学':'c5499', '高中英语':'c5501', '高中物理':'c5503', '高中化学':'c5505', '高中生物':'c5507', '高中政治':'c5495', '高中历史':'c5509', '高中地理':'c5510' },
		{ '初中语文': 'c5496', '初中数学':'c5498', '初中英语':'c5500', '初中物理':'c5502', '初中化学':'c5504', '初中生物':'c5506', '初中思品':'c5494', '初中历史':'c5508', '初中地理':'c5509' },
		{ '小学语文':'c14165', '小学数学':'c5490', '小学英语':'c5491', '小学思品':'c5492', '小学科学':'c5493', '中小学美术':'c5516', '中小学体育':'c5514', '中小学音乐':'c5511' },
		{ '学前教研':'c65291', '综合实践':'c5517', '通用技术':'c5513', '信息技术':'c5512', '心理健康':'c15079' }],
		[{ '教研简报': 'c5431', '校园新闻': 'c127149', '教育时政': 'c12994' }],
		[{ '通知通报': 'c5432' }],
		[{ '政策法规': 'c14962', '制度建设': 'c5522', '计划总结': 'c5433' }],
		[{ '国家课题': 'c127161', '省级课题': 'c127162', '市级课题': 'c5434', '攀登英语': 'c127163' }],
		[{ '在线研讨': 'c69512' }],
		[{ '校长论坛': 'c64804', '教学论坛': 'c5435' }],
		[{ '视频中心': 'c126355', '课件专区': 'c127176', '试题专区': 'c127178', '下载专区': 'c19037' }],
		[{ '博客主页': 'c38485', '教研室简介': 'c127181', '教研员分工': 'c123182', '上网导航': 'c62559' }]
	],

	rtl: ['', '', '', '', '', '', '', 'rtl', 'rtl', 'rtl']

};

if (window.Burn)
Burn.implement({

	load: function() {
		var getTopicHtml = function(data, len, showdate) {
			var title = data.Title;
			var showtitle = len && title.length > len;
			if (showtitle) title = title.substr(0, len) + '...';
			var html = '<li>' +
				'<a' + (showtitle ? ' title="' + data.Title + '"' : '') + ' href="http://res.hersp.com/content/' + data.ContentId + '">' + title.html() + '</a>' +
				(showdate ? (' <span class="dt">(' + data.PublishDate.utc('md') + ')</span>') : '') +
				'</li>';
			return html;
		};

		var success = function(text, doc) {
			var s1 = '';
			Xml.getNodes(doc, '//Category').each(function(data) {
				s1 += getTopicHtml(data, null, true);
			});
			s1 = '<ul>' + s1 + '</ul>';
			$('notice').set('html', s1);
			this.template = this.template.replace('<!-- notice -->', s1);
		}.bind(this);

		var url = 'http://my.hersp.com/admin/blog';
		var data = { Action: 'dxQuery', Category: '5432|10' };
		this.requestX(url, data, success);
	}

});
