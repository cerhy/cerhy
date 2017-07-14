/* (c) dbs, ghljj.com. */

var cfg = {

	menu: [
		[{ '屯昌县教育研究培训中心': 'http://res.hersp.com/content/5491719'/*, '屯昌教育局': 'http://xxgk.hainan.gov.cn/tcxxgk/jyjxxgk/'*/ }],
		[{ '办公室': '9301', '培训室': '9705', '电教站': '9300', '中学教研室': '9268', '小学教研室': '9258' }],
		[{ '2016年': 'c514445', '2015年': 'c515215', '2014年': 'c515217', '2013年': 'c515218' }],
		[{ '活动动态': 'c522791', '培训简报': 'c61829', '电教动态':  'c500877','中学教研动态': 'c5919', '小学教研动态': 'c6487' }],
		[{ '高中': null, '高中语文': '9027', '高中数学': '9015', '高中英语': '9025', '高中物理': '9006', '高中化学': '9089', '高中生物': '9017', '思想政治': '9099', '高中历史': '9009', '高中地理': '9013', '通用技术': '9090', '信息技术': '9091', '高中体育': '9005', '高中音乐': '9019', '高中美术': '9011' },
		{ '初中': null, '初中语文': '9027', '初中数学': '9007', '初中英语': '9016', '初中物理': '9006', '初中化学': '9089', '初中生物': '9017', '思想品德': '9033', '初中历史': '9009', '初中地理': '9013', '信息技术': '9092', '劳动与技术': '9093', '初中体育': '9005', '初中音乐': '9019', '初中美术': '9011' },
		{ '小学': null, '小学语文': '9023', '小学数学': '9001', '小学英语': '9016', '小学品德': '9033', '小学科学': '9021', '信息技术': '9092', '劳动与技术': '9093', '小学体育': '9005', '小学音乐': '9019', '小学美术': '9011' },
		{ '综合': null, '网络教研': '9100', '学前教育': '9029', '心理健康': '9035', '综合实践活动': '9031', '职业教育教研': '9018', '特殊教育': '9030' }],
		[{ '国家课题': 'c27165', '省级课题': 'c154600', '市级课题': 'c137702', '其他': 'c137704', '优秀课例': 'c26734', '优秀设计': 'c4610', '优秀论文': 'c180685', '课程开发': 'c28781' }],
		[{ '教学常规': 'c335613', '质量检测': 'c335614', '片区教研': 'c335615' }],
		[{ '在线研讨': 'c262195', '集体备课': 'c335607', '远程研修': 'c335609' }],
		[{ '课程管理': 'c163354', '校本研修': 'c172065', '样本校建设': 'c189321', '规范化学校建设': 'c137726' }],
		[{ '资源共享': 'c16491', '省级': 'c16492', '市级': 'c163355', '其他': 'c172063' }]
	],

	rtl: ['', '', '', '', 'rtl2', '', '', '', 'rtl', 'rtl'],

	initSearch: function(id) {
		var frm = $(id || 'search');
		frm.addEvent('submit', function(evt) {
			var s = frm['wd'].value;
			evt = new Event(evt);
			if (s.length == '') { alert('请输入要打开的会员账号或要搜索的内容。'); evt.stop(); return false; }
			if (/\d+$/.test(s) && s.length > 3 && s.length < 10) { evt.stop(); window.open('http://my.hersp.com/' + s); }
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
			var html = '<li>' +
				'<a' + (showtitle ? ' title="' + data.Title + '"' : '') + ' href="http://res.hersp.com/content/' + data.ContentId + '">' + title.html() + '</a>' +
				(showdate ? (' <span class="dt">(' + data.PublishDate.utc('md') + ')</span>') : '') +
				'</li>';
			return html;
		};

		var success = function(text, doc) {
			var ss = {};
			Xml.getNodes(doc, '//Topic').each(function(data) {
				var tmp = getTopicHtml(data, null, true);
				var cid = data.Owner;
				if (data.CategoryId == '544704') cid = '544704';
				if (!ss[cid]) ss[cid] = '';
				ss[cid] += tmp;
			});
			for (var p in ss) {
				var tmp = '<ul>' + ss[p] + '</ul>';
				$('notice' + p).set('html', tmp);
				this.template = this.template.replace('<!-- notice' + p + ' -->', tmp);
			};
		}.bind(this);

		var url = 'http://my.hersp.com/admin/blog';
		//var data = { Action: 'dxQuery', Category: '514445|50,61829|10,5919|10,6487|10,345910|10,500877|10,354548|10,127491|10,544704|10' };
		var data = { Action: 'dxQuery', Owner: '9301|50,9705|10,9268|10,9258|10,859557|10,9300|10,872505|10,677698|10', Category: '544704|10' };
		this.requestX(url, data, success);
	}

});
