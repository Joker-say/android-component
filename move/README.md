#可移动移动视图

###### MoveLinear 继承于LinearLayout，所以可以直接代替常用的LinearLayout

##使用方法
    ```
    <包名(packages).MoveLinear
          android:id="@+id/loading"
          android:orientation="horizontal"
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:text="获取数据">

          <Button
              android:clickable="false"
              android:layout_width="wrap_content"
              android:layout_height="wrap_content"
              android:text="子view" />
      </com.hitt.mvptoframenwork.MoveLinear>
    ```

##方法调用
    ```
   setOnClickListener  点击事件
   setOnLongClickListener  长按事件
   setOrientation(int x,int y) 设置控件坐标
   getViewX  获取控件当前X轴坐标
   getViewY  获取控件当前Y轴坐标
    ```

##注意事项
   ###### 由于事件分发机制的原因，子类 clickable 要设置为false，否则点击事件不生效。
   ###### 如果需要包含多个子类，子类的宽高必须一致，否则点击位置会计算失败(后续会优化此问题)。