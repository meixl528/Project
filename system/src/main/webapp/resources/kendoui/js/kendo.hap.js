/**
 * @summary Hap
 * @description 抽象通用函数
 * @version 2.0
 * @author njq.niu@hand-china.com
 * @author shengyang.zhou@hand-china.com
 * @copyright Copyright Hand China Co.,Ltd.
 *
 */
!(function ($) {

    Date.prototype.toJSON = function () {
        return kendo.toString(this, "yyyy-MM-dd HH:mm:ss")
    };

    $.extend({
        /**
         * isEmpty( Object value, [Boolean allowEmptyString] ) : Boolean
         * Returns true if the passed value is empty, false otherwise. The value is deemed to be empty if it is either:
         * null
         * undefined
         * a zero-length array
         * a zero-length string (Unless the allowEmptyString parameter is set to true)
         */
        isEmpty: function (value, allowEmptyString) {
            return (value === null) || (value === undefined)
                || (!allowEmptyString ? value === '' : false)
                || ($.isArray(value) && value.length === 0);
        }
    });

    /**
     * 获取或设置prompt信息
     * 函数描述
     * @param code
     * @param value
     * @returns
     */
    $l = function (code, value) {
        var al = arguments.length, p = Hap.defaultPrompt;
        if (al == 1) {
            var v = p[code];
            return v ? v : code;
        } else if (al == 2) {
            p[code] = value;
        }
    }
    if (!window.Hap) {
        Hap = {
            version: '2.0',
            defaultPrompt: {}
        };

        /**
         * 根据系统编码获取对应的值的描述.
         *
         * <ul>
         * <li>data: 系统编码</li>
         * <li>v: 值</li>
         * </ul>
         * @param data
         * @param v
         */
        Hap.getCodeMeaning = function (data, v) {
            $.each(data, function (i, n) {
                if ((n.value || '').toLowerCase() == (v || '').toLowerCase()) {
                    v = n.meaning;
                    return false;
                }
            })
            return v;
        }

        Hap.showTip = function (msg) {
            if (!Hap.systemNotification) {
                Hap.systemNotification = $('<span id="systemNotification" style="display:none;"></span>').appendTo(document.body).kendoNotification({
                    stacking: "down",
                    show: function (e) {
                        if (e.sender.getNotifications().length == 1) {
                            var element = e.element.parent(),
                                eWidth = element.width(),
                                eHeight = element.height(),
                                wWidth = $(window).width(),
                                wHeight = $(window).height(),
                                newTop, newLeft;

                            newLeft = Math.floor(wWidth / 2 - eWidth / 2);
                            newTop = Math.floor(wHeight / 2 - eHeight / 2);

                            e.element.parent().css({top: newTop, left: newLeft});
                        }
                    },
                    autoHideAfter: 1500,
                    button: true
                }).data("kendoNotification")
            }
            Hap.systemNotification.show(msg)
        }
        /**
         * 删除选中数据(grid).
         *
         * <ul>
         * <li>delSelection.grid: grid dom</li>
         * </ul>
         * @param opts
         */
        Hap.deleteGridSelection = function (delSelection) {

            delSelection = delSelection || {};
            var grid = delSelection.grid.data("kendoGrid") || {};

            var checked = grid.selectedDataItems();
            if (grid.selectedDataItems().length) {
                kendo.ui.showConfirmDialog({
                    title: $l('hap.tip.info'),
                    message: $l('hap.tip.delete_confirm')
                }).done(function (event) {
                    if (event.button == 'OK') {
                        $.each(checked, function (i, v) {
                            grid.dataSource.remove(v)
                        })
                        grid.dataSource.sync();
                    }
                })
            }
        };


        /**
         * 保存头行数据(1个form,0个1个或多个 grid).
         *
         * <ul>
         * <li>opts.url: 提交的url</li>
         * <li>opts.type|method: 提交的 http method (默认 POST)</li>
         * <li>opts.formModel: form绑定的 model</li>
         * <li>opts.asArray: form 作为数组提交(默认 true)</li>
         * <li>opts.grid: grid name 和 dom</li>
         *      <ul>
         *          <li>key:bindName</li>
         *          <li>value:grid dom</li>
         *      </ul>
         * <li>opts.success: success回调函数</li>
         * <li>opts.failure: failure回调函数</li>
         * </ul>
         * @param opts
         */
        Hap.submitForm = function (opts) {
            opts = opts || {};
            var url = opts.url;
            if (!opts.formModel || !url) {
                return;
            }
            opts.asArray = ('asArray' in opts) ? (!!opts.asArray) : true;
            var grids = opts.grid || {};

            var header = opts.formModel.toJSON();
            var changedDs = {};

            $.each(grids, function (bindName, grid) {
                var ds = grid.data("kendoGrid").dataSource;
                if (!ds.hasChanges())return;
                changedDs[bindName] = ds;
                header[bindName] = []
                $.each(ds.data(), function (idx, data) {
                    if (data.dirty === true) {
                        var d = data.toJSON();
                        d['__status'] = data.isNew() ? 'add' : 'update';
                        d['__id'] = data.uid;
                        header[bindName].push(d);
                    }
                });
            })
            $.ajax({
                url: url,
                type: opts.type || opts.method || 'POST',
                contentType: opts.contentType || 'application/json',
                data: kendo.stringify(opts.asArray ? [header] : header),
                success: function (result) {
                    if (result.success === true) {
                        var h = opts.asArray ? (result.rows[0] || {}) : result;
                        if (opts.formModel.set) {
                            $.each(h, function (k, v) {
                                opts.formModel.set(k, v);
                            })
                        } else
                            $.extend(opts.formModel, h);
                        delete opts.formModel['__status'];
                        $.each(changedDs, function (bindName, source) {
                            $.each(h[bindName] || [], function (i, r) {
                                var _r = source.getByUid(r.__id);
                                if (_r) {
                                    if (r.__status == 'delete') {
                                        //source.remove(_r)
                                    } else {
                                        delete r['__status'];
                                        delete r['__id'];
                                        _r.accept(r);
                                    }
                                }
                            });
                            grids[bindName].find(".k-dirty").removeClass("k-dirty");
                        });
                        if (opts.success instanceof Function) {
                            opts.success(result)
                        } else {
                            Hap.showTip($l('hap.tip.success'))
                        }
                    } else {
                        if (opts.failure instanceof Function) {
                            opts.failure(result)
                        } else {
                            kendo.ui.showErrorDialog({
                                title: $l('hap.error'),
                                width: 400,
                                message: result.message
                            })
                        }
                    }
                }
            });
        };

        /**
         * outsizeGrid 表格随界面大小而变化
         * @param grid_id
         * 1.必须有个外层div
         * 2.传入一个grid的id值
         *
         */
        function resizeGrid(grid_id) {
            var grid = $(grid_id).data('kendoGrid');
            if (grid) {
                grid.autoResize();
            }
        }

        Hap.autoResizeGrid = function (grid_id) {
            resizeGrid(grid_id);
            $(window).resize(function () {
                resizeGrid(grid_id);
            });
        }

        var tzOffSet = new Date().getTimezoneOffset();
        Hap.timeZone = {
            getTimezoneOffset: function () {
                return tzOffSet;
            },
            set: function (tz) {
                if (!/GMT([+-]\d{4})?/.test(tz))return;
                if (tz.length > 3) {
                    var sign = tz.charAt(3) == '-' ? -1 : 1;
                    var h = +tz.substring(4, 6);
                    var m = +tz.substring(6);
                    tzOffSet = -sign * (h * 60 + m);
                } else tzOffSet = 0;//GMT
            }
        };

        /**
         * value:
         *   输入的值
         * temp:
         *     当前去匹配那个正则表达式(no_limit digits_and_letters digits_and_case_letters)
         *     no_limit:无限制
         *     digits_and_letters:混合数字和字母
         *     digits_and_case_letters:混合数字和大小写字母
         * 返回类型为布尔型
         *
         * */
        Hap.passwordFormat = function (value, temp) {
            if (temp == 'no_limit') {
                return true;
            } else if (temp == "digits_and_letters") {
                return /[a-zA-Z]/.test(value) && /\d/.test(value);
            } else if (temp == "digits_and_case_letters") {
                return /[a-z]/.test(value) && /[A-Z]/.test(value) && /\d/.test(value);
            }
        };

        /**
         * 将日期字符串转换为 Date 对象。
         * 无效的值将返回 null
         * <ul>
         *     <li>value:日期字符串</li>
         * </ul>
         */
        Hap.strToDate = function (value) {
            if (!value)return null;
            value = value.replace(/-/g, '/');
            value = value.replace('T', ' ');
            value = value.replace(/(\+[0-9]{2})(\:)([0-9]{2}$)/, ' UTC\$1\$3');
            value = value.replace(/\.[0-9]{1,3}/, '');
            return new Date(value);
        };

        Hap.formatDate = function (value) {
            if (!value)return '';
            var d = (value instanceof Date) ? value : Hap.strToDate(value);
            return kendo.toString(d, "yyyy-MM-dd")
        }
        //add by jinqin.ma@hand-china.com
        Hap.bytestosize=function (bytes) {
            if (bytes === 0) return '0 B';
            var k = 1024, // or 1024
                sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
                i = Math.floor(Math.log(bytes) / Math.log(k));
            return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
        }

        Hap.formatDateTime = function (value) {
            if (!value)return '';
            var d = (value instanceof Date) ? value : Hap.strToDate(value);
            return kendo.toString(d, "yyyy-MM-dd HH:mm:ss")
        };

        /**
         * 创建一个 A 标签,使用指定的 函数+参数 相应点击事件。
         * <ul>
         *     <li>text:a 标签内的元素,文本,也可以是其他的 html 元素</li>
         *     <li>func:点击相应函数,可以是函数名,也可以是引用</li>
         *     <li>其他参数:作为 func 的参数,类型可以是 boolean,string,number</li>
         * </ul>
         */
        Hap.createAnchor = function (text, func) {
            if (typeof func == 'function')
                func = func.name || func.toString().match(/^function\s*([^\s(]+)/)[1];
            var arr = [];
            $.each(arguments, function (i, r) {
                if (i < 2)return;
                if (typeof r == 'string') arr.push("'" + r + "'");
                else arr.push(r);
            });
            var funcCall = func + '(' + arr.join(',') + ');return false';
            return '<a href="javascript:void(0);" onclick="' + funcCall + '">' + text + '</a>';
        };

        Hap.prepareQueryParameter = function (obj, options) {
            obj = obj || {};
            if (options) {
                obj.page = options.page;
                obj.pagesize = options.pageSize;
                if (options.sort && options.sort.length > 0) {
                    obj.sortname = options.sort[0].field;
                    obj.sortorder = options.sort[0].dir;
                }
            }
            for (var k in obj) {
                if (obj[k] === '' || obj[k] === null || obj[k] === undefined)
                    delete obj[k]
                if (obj[k] instanceof Date) {
                    obj[k] = obj[k].toJSON()
                }
            }
            return obj;
        };

        Hap.prepareSubmitParameter = function (options, type) {
            var datas = options.models;
            $.each(datas, function (i, r) {
                if (type == 'create')
                    r['__status'] = 'add';
                else if (type == 'update')
                    r['__status'] = 'update';
                else if (type == 'destroy')
                    r['__status'] = 'delete';
            });
            return datas;
        };


        //处理ajax异常
        $(document).ajaxSuccess(function (event, xhr, options, json) {
            if (json && json.success === false) {
                if (json.code == 'session_expired') {
                    if (top.sessionExpiredLogin) {
                        top.sessionExpiredLogin();
                    } else {
                        alert($l('hap.tip.session_expired'));
                        top.location.href = _basePath;
                    }
                }
            }
        }).ajaxError(function (event, XMLHttpRequest, ajaxOptions, thrownError) {
            if (!ajaxOptions.error)
                ; //$.ligerDialog.alert(thrownError, '' + XMLHttpRequest.status, 'error');
        });
        
        
        
        Hap.escapeHtml = function(str){
        	if(!$.type(str) == 'string' || str.length ==0)
                return str;
            return String(str).replace(/&/gm,'&amp;').replace(/\"/gm,'&quot;').replace(/\(/gm,'&#40;').replace(/\)/gm,'&#41;').replace(/\+/gm,'&#43;').replace(/\%/gm,'&#37;')
            .replace(/</gm,'&lt;').replace(/>/gm,'&gt;');
        }
        Hap.unescapeHtml = function(str){
            if(!$.type(str) == 'string' || str.length ==0)
                return str;
            return String(str).replace(/&amp;/gm,'&').replace(/&quot;/gm,'"').replace(/&#40;/gm,'(').replace(/&#41;/gm,')').replace(/&#43;/gm,'+').replace(/&#37;/gm,'%')
            .replace(/&lt;/gm,'<').replace(/&gt;/gm,'>');
        }
    }
    
    
    //扩展Grid方法
    kendo.ui.Grid.prototype.removeRow = function (row) {
        var sf = this;
        kendo.ui.showConfirmDialog({
            title: $l('hap.prompt'),
            message: $l('hap.tip.delete_confirm')
        }).done(function(event) {
            if (event.button == 'OK') {
                sf._removeRow(row);
            }

        })
    }
    /*********
     * *为请求添加token
     */
        var header = $('meta[name=_csrf_header]').attr('content');
        var token  = $('meta[name=_csrf]').attr('content');
        $(document).ajaxSend(function (e,xhr,options) {
            xhr.setRequestHeader(header,token);
        });

})(jQuery)
