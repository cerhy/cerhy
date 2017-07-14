/* (c) dbs, ghljj.com. */

var cfg = {

	menu: [
		null,
		[{ '2012年': 'c128983' }],
		[{ '制度建设': 'c143752', '计划总结': 'c27903' }],
		[{ '教学设计': 'c132164', '教学课件': 'c132165', '教学视频': 'c132166', '教学论文': 'c132167' }],
		[{ '中学语文': 'c132177', '中学数学': 'c132179', '中学英语': 'c132181', '中学物理': 'c132183', '中学化学': 'c132184', '中学生物': 'c132185', '中学政治': 'c132186', '中学历史': 'c132187', '中学地理': 'c132188', '技术频道': 'c132194' },
		 { '小学语文': 'c132178', '小学数学': 'c132180', '小学英语': 'c132182', '美术频道': 'c132189', '音乐频道': 'c132190', '体育频道': 'c132191', '小学思品': 'c132192', '小学科学': 'c132193', '职教频道': 'c143748', '幼教频道': 'c143747' }],
		[{ '在线研讨': 'c5923', '集体备课': 'c130036', '课题研究': 'c5387', '教师培训': 'c5388', '远程研修': 'c5389' }]
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
			var html =
				'<li>' +
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
		var data = { Action: 'dxQuery', Category: '5384|10' };
		this.requestX(url, data, success);
	}

});
