/**
 * Created by ZLY .
 */
!(function($,window,document){

    var Tab = function(elem,options){
        this.elem = elem;
        this.$elem= $(elem);
        this.options=options;
    };
    Tab.prototype={
        setting:{
            handler: ".tab-btns a",
            content: ".tab-content",
            event  : "click"
        },
        init:function(){
            this.config = $.extend({},this.setting,this.options);

            this.method();
            return this;
        },
        method: function () {
            //console.log(this.$elem.find(this.config.handler))
            var handler = this.$elem.find(this.config.handler),
                content = this.$elem.find(this.config.content);
            //_this   = this;
            $(document).on(this.config.event,handler,function(e){
                //console.log(e.target)
                var index = handler.index(e.target);
                if (index>-1){
                    handler.removeClass("active");
                    handler.eq(index).addClass("active");
                    content.hide();
                    content.eq(index).show();
                }

            })

        }
    };

    $.fn.tab=function(options){
        return this.each(function(){
            new Tab(this,options).init();
        })
    }
})(jQuery,window,document);

var zh ={
    //    输入框默认值切换
    inputfocus: function(x){
        $(x).focus(function(){
            if (this.defaultValue == $(this).val()) {
                $(this).val("");
                $(this).css("color","#333");
            }
        }).blur(function(){
            if ($(this).val() == "") {
                $(this).val(this.defaultValue);
                $(this).css("color","#999");
            }
        })
    },
    textCount : function(box,input,n){
        var that = $(box);
        that.find(input).keyup(function(){
            var l = $.trim($(this).val()).length;
            var last = n-l;
            that.find(".comment-count-num p").html("还可以输入"+" <b>" + last + "</b> 个字！");
        });
    },
    nSlide: function (opts){
        //默认配置
        var defaults = {

            bigPic : '.ps-picd-show',//大图包裹
            smallPic: '.ps-picd-list',  //小图包裹
            showNum  : 7 ,            //展示的图片个数
            smPrev   : '.imgScrollBtn01' , //小图向左
            smNext   : '.imgScrollBtn02',    //小图向右
            smWidth  : 770                  //小图宽度
        };
        var o = $.extend({},defaults,opts);
        var index = 1;
        var con =0;
        var lenB = $(o.bigPic).find('li').length;
        var lenS = $(o.smallPic).find('li').length;
        var bgLi =$(o.bigPic).find('li');
        var SmLi =$(o.smallPic).find('li');
        var len = Math.ceil(lenS/o.showNum);
        var ani =null;
        clearInterval(ani);
        ani = setInterval(AA,3000);
        bgLi.hover(function(){
            if(ani){
                clearInterval(ani);
            }
        },function(){
            clearInterval(ani);
            ani = setInterval(AA,3000);
        });
        SmLi.on('click',function(){
            index = SmLi.index(this);
            imgShow(index);
        });

        function AA(){
            if(index == lenS){

            }else{
                imgShow(index);
                index++;
            }

        }
        function imgShow(i) {
            //console.log("imgShow"+i);
            bgLi.eq(i).fadeIn("slow").siblings("li")
                .fadeOut("slow");
            SmLi.siblings().find("a").removeClass('active');
            SmLi.eq(i).find("a").addClass('active');
            for(var m=1; m<lenS; m++){
                if(i == o.showNum*m) {
                    con += 1;
                    if(con === len-1){
                        $(o.smNext).addClass('disabled');
                    }
                    $(o.smPrev).removeClass('disabled');
                    $(o.smallPic).animate({left:-(o.smWidth)*m},200);
                }
            }
        }
        //上一页按钮
        $(o.smPrev).click(function() {
            console.log(con,len);
            con -= 1;
            if(con <= 0) {
                con=0;
                $(this).addClass('disabled');
                $(o.smNext).removeClass('disabled');
            }else{
                if(con === 1){
                    //$(this).addClass('disabled');
                }
                $(o.smNext).removeClass('disabled');
                $(this).removeClass('disabled');
            }
            console.log('prev'+con);
            $(o.smallPic).animate({left:-(o.smWidth)*con},200);

        });

        //下一页按钮
        $(o.smNext).click(function() {
            console.log(con,len);
            con += 1;
            if(con >= len) {
                con = len-1;
            }else{
                if(con === len-1){
                    $(this).addClass('disabled');
                }
                $(o.smPrev).removeClass('disabled');
                $(o.smallPic).animate({left:-(o.smWidth)*con},200);
            }
        });
    }
};
$(function(){
    zh.inputfocus("input[type='text']");
    zh.inputfocus(".normal-textarea");
    zh.textCount(".news-comments", ".normal-textarea", 140);
    $('.tab-btm-wrap').tab({
        handler: ".tab-head a",
        content: ".tab-content"
    });
    $('.info-div').tab({
        handler: ".info-tab-head a",
        content: ".info-div-content .info-tab-content"
    });
    //左侧菜单点击切换
    $('.fst-lv-ul li').on('click',function(){
        //var index = $(this).index();
        $(this).addClass('active').siblings('li').removeClass('active');
    });
    //点击浏览更多进行加载更多
    $('.article-list-wrap .loading-more').on("click",function(){
        var self = $(this);
        var dataIndex = $(this).attr('data-index');
        $.ajax({
            type: "GET",
            url: "data/data.txt",
            data: {dataIndex:dataIndex}
        }).done(function(data){
            self.siblings('.report-list').append(data);
            self.attr('data-index',parseInt(dataIndex,10)+1+"");
        }).fail(function(){

        })
    });
    zh.nSlide({
        bigPic : '.big-img-ul',//大图包裹
        smallPic: '.small-img-ul',  //小图包裹
        showNum  : 10 ,            //展示的图片个数
        smPrev   : '.now-steps .prev' , //小图向左
        smNext   : '.now-steps .next',    //小图向右
        smWidth  : 230                  //小图宽度
    });
    //baner右侧的鼠标移入轮播
    $('.news-tab-dt .tab-head a').mouseenter(function(){
        var index = $(this).index();
        $(this).addClass('active').siblings('a').removeClass('active');
        $(this).parent().siblings('.tab-content').hide().eq(index).show();
    })
});

