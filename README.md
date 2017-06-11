Reference System Transformer
    WGS-84、GCJ-02、BD-09三种参考系统的坐标变换

参考文献
* https://en.wikipedia.org/wiki/Global_Positioning_System
* 
* http://kusowhu.net/%E5%85%B3%E4%BA%8E%E6%B5%81%E4%BC%A0%E7%9A%84-wgs-84-%E8%87%B3-gcj-02-%E8%BD%AC%E6%8D%A2%E7%AE%97%E6%B3%95/
* 
* http://blog.csdn.net/coolypf/article/details/8569813
* http://blog.csdn.net/coolypf/article/details/8686588
* https://github.com/jubincn/WGS84GCJ02Conversion
* https://github.com/JackZhouCn/JZLocationConverter
* https://github.com/taoweiji/JZLocationConverter-for-Android

http://politics.people.com.cn/n/2014/0626/c1001-25201943.html

性能测试（MacBook Pro）
1. Java Math
2. Java FastMath
3. C++

10^8点   379147ms    439226ms        1150587ms
10^7点    37973ms      43934ms       133396ms
10^6点     3828ms          4284ms    11109ms
采用Apache Commons Math的FastMath代替Math，计算处理所需时间仅为原来的一半；



可逆变换的误差测试：
GCJ-02 <--> WGS-84，其误差< 1E-8
GCJ-02 <--> BD-09，其误差 < 1E-7

