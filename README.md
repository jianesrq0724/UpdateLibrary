> # 所有的Demo都是基于BaseProject项目来开发的
> # [BaseProject](https://github.com/jianesrq0724/BaseProject)
---





## 关于UpdateLibrary的介绍
* 将升级功能抽取为Dialog，创建对应的Dialog，将升级地址和文字描述传入就可以
* 缺陷：升级的对话框的布局样式是固定的，没法改，后期继续优化，支持自定义布局
* 需要注意的地方，权限的判断在回调用，进行权限判断

## 用法
> 创建点击事件
>> 
            updateDialog = new UpdateDialog(mContext);
               updateDialog.setValue(url, version, desc, force);
               updateDialog.setOnClickUpdateListener(() -> {
                   updateDialog.cancel();
                   if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                       ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_STORAGE);
                   } else {
                       updateDialog.onClickUpdate();
                   }
               });
           }
           updateDialog.show();

>> 权限，在点击事件中申请


# 适配7.0  2017-8-11
使用FileProvider适配7.0




