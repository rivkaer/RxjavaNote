package com.rivkaer.rxjava2;

/**
 * @author: Jia Junjian
 * @date: 2019/7/20
 * @email: cnrivkaer@outlook.com
 * @describe: Rxjava2 入门使用
 **/

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 版权说明 图片引用&Rxjava学习路径参照 https://www.jianshu.com/nb/14302692
 */
public class SimpleUsingDemo {

    /**
     * * Introduction to RxJava on GitHub:
     * > a library for composing asynchronous and event-based programs using observable sequences for the Java VM
     * > RxJava 是一个在 Java VM 上使用可观测的序列来组成异步的、基于事件的程序的库
     *
     * * 作用
     * - 实现异步操作 类似于 Android中的 AsyncTask 、Handler作用
     *
     * * 特点
     * > 基于事件流的链式调用
     * >> 优雅简洁 实现优雅 使用简单
     * >> 不会因为逻辑复杂度的提高，失去函数体的简介和优雅
     *
     * * 原理
     * > 基于一种扩展的观察者模式
     *
     * ---------------------------------------------------------------
     * |       角色              |     作用            |   类比     |
     * ------------------------------------------------------------
     * | 被观察者(Observable)  |     产生事件           |    顾客    |
     * | 观察者(Observer)      |  接受事件,并响应动作    |    厨房    |
     * | 订阅(Subscribe)      |   连接被观察者和观察者   |    服务员  |
     * | 事件(Event)          | 被观察者和观察者沟通的载体|    菜品    |
     * -----------------------------------------------------------
     * #^ 现实例子阐述Rxjava中的四个角色 流程示意图 http://note.novakj.cn/note/rxjava/944365-fc3b7eb5a0ad28d0.png
     * #^ RxJava原理可总结为：被观察者 （Observable） 通过 订阅（Subscribe） 按顺序发送事件 给观察者 （Observer）， 观察者（Observer） 按顺序接收事件 & 作出对应的响应动作
     *
     */


    /**
     * Rxjava基本使用
     *
     * - Rxjava使用方式
     * -> 分步骤实现 该方法主要为了深入说明Rxjava的原理
     * -> 基于事件流的链式调用: 主要用于实际使用
     */

    /**
     * 分步骤实现
     * 使用步骤:
     * 1.创建被观察者(Observable) & 生产事件 (顾客入饭店 - 坐下餐桌 - 点菜)
     * 2.创建观察者(Observer) 并定义响应事件行为(厨房 确定对应菜式)
     * 3.通过订阅(Subscribe)连接观察者和被观察者(顾客找到服务员 点菜 服务员下单到厨房 厨房煮饭)
     */
    @Test
    public void onStepByStepImplement() {
        //1. 创建被观察者Observable对象
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            // create() 是 RxJava 最基本的创造事件序列的方法
            // 此处传入了一个 OnSubscribe 对象参数
            // 当 Observable 被订阅时，OnSubscribe 的 call() 方法会自动被调用，即事件序列就会依照设定依次被触发
            // 即观察者会依次调用对应事件的复写方法从而响应事件
            // 从而实现被观察者调用了观察者的回调方法 & 由被观察者向观察者的事件传递，即观察者模式

            //subscribe()里定义需要发送的事件
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        });

        /**
         * 快速创建Observable
         */
        //just(T...) 直接将传入的参数一次发送出去
        Observable justObservable = Observable.just("A", "B", "C");
        //from(T[]) from(Iterable<? extends T>) : 将传入的数组 / Iterable 拆分成具体对象后，依次发送出来
        Observable fromArrayObservable = Observable.fromArray(new String[]{"A", "B", "C"});

        //2.创建观察者(Observer)并定义响应事件行为
        //事件说明:http://note.novakj.cn/note/rxjava/944365-8cb0da34f94b0c73.png
        //采用Observer 接口
        Observer<Integer> observer = new Observer<Integer>() {

            // 观察者接收事件前，默认最先调用复写 onSubscribe（）
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("开始采用subscribe连接");
            }

            // 当被观察者生产Next事件 & 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onNext(Integer integer) {
                System.out.println("对Next事件作出响应" + integer);
            }

            // 当被观察者生产Error事件& 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onError(Throwable e) {
                System.out.println("对Error事件作出响应");
            }

            // 当被观察者生产Complete事件& 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onComplete() {
                System.out.println("对Complete事件作出响应");
            }
        };

        //采用Subscriber 抽象类
        //Subscriber类 = RxJava 内置的一个实现了 Observer 的抽象类，对 Observer 接口进行了扩展
        Subscriber<Integer> subscriber = new Subscriber<Integer>() {

            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("开始采用subscribe连接");
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("对Next事件作出响应" + integer);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("对Error事件作出响应");
            }

            @Override
            public void onComplete() {
                System.out.println("对Complete事件作出响应");
            }
        };

        /**
         * 特别注意：2种方法的区别，即Subscriber 抽象类与Observer 接口的区别 -->
         * 相同点：二者基本使用方式完全一致（实质上，在RxJava的 subscribe 过程中，Observer总是会先被转换成Subscriber再使用）
         * 不同点：Subscriber抽象类对 Observer 接口进行了扩展，新增了两个方法：
         *      1. onStart()：在还未响应事件前调用，用于做一些初始化工作
         *      2. unsubscribe()：用于取消订阅。在该方法被调用后，观察者将不再接收 & 响应事件
         * 调用该方法前，先使用 isUnsubscribed() 判断状态，确定被观察者Observable是否还持有观察者Subscriber的引用，如果引用不能及时释放，就会出现内存泄露
         */

        //3.通过订阅（Subscribe）连接观察者和被观察者
        observable.subscribe(observer);
    }

    /**
     * 链式实现
     * 优雅的实现方法 - 基于事件流的链式调用
     * 流程调用顺序：观察者.onSubscribe（）> 被观察者.subscribe（）> 观察者.onNext（）>观察者.onComplete()
     */
    @Test
    public void onChainImplement() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("开始采用subscribe连接");
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("对Next事件" + integer + "作出响应");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("对Error事件作出响应");
            }

            @Override
            public void onComplete() {
                System.out.println("对Complete事件作出响应");
            }
        });

        Observable
                .just("hello")
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("对accept事件" + s + "作出响应");
                    }
                });
    }

    /**
     * Observable多种创建方式
     * 创建符说明: http://note.novakj.cn/note/rxjava/944365-d36c6ed319565acc.webp
     *
     * * 基础创建
     * - create(ObservableOnSubscribe<T> source) 传入实例化的ObservableOnSubscribe对象创建Observable
     *
     * * 快速创建
     * - just(T...)
     *   作用:
     *      1.快速创建1个被观察者对象（Observable）
     *      2.发送事件的特点：直接发送 传入的事件
     *      注：最多只能发送10个参数
     *   应用场景：
     *      快速创建 被观察者对象（Observable） & 发送10个以下事件
     * - fromArray(T... items)
     *   作用:
     *      1.快速创建1个被观察者对象（Observable）
     *      2.发送事件的特点：直接发送 传入的数组数据
     *      会将数组中的数据转换为Observable对象
     *   应用场景：
     *      快速创建 被观察者对象（Observable） & 发送10个以上事件（数组形式）
     *      数组元素遍历
     * - fromIterable(Iterable<? extends T> source)
     *   作用：
     *      1.快速创建1个被观察者对象（Observable）
     *      2.发送事件的特点：直接发送 传入的集合List数据
     *      会将数组中的数据转换为Observable对象
     *   应用场景:
     *      1.快速创建 被观察者对象（Observable） & 发送10个以上事件（集合形式）
     *      2.集合元素遍历
     * - empty() 该方法创建的被观察者对象发送事件的特点：仅发送Complete事件，直接通知完成。 即观察者接收后会直接调用onCompleted（）
     * - error(final Throwable exception) 该方法创建的被观察者对象发送事件的特点：仅发送Error事件，直接通知异常。即观察者接收后会直接调用onError（）
     * - never() 该方法创建的被观察者对象发送事件的特点：不发送任何事件 即观察者接收后什么都不调用
     *
     * * 延迟创建
     *      1.定时操作：在经过了x秒后，需要自动执行y操作
     *      2.周期性操作：每隔x秒后，需要自动执行y操作
     *
     * - defer()
     *   作用:
     *      直到有观察者（Observer ）订阅时，才动态创建被观察者对象（Observable） & 发送事件
     *      1.通过 Observable工厂方法创建被观察者对象（Observable）
     *      2.每次订阅后，都会得到一个刚创建的最新的Observable对象，这可以确保Observable对象里的数据是最新的
     *   应用场景:
     *      动态创建被观察者对象（Observable） & 获取最新的Observable对象数据
     * - timer()
     *  作用:
     *      1.快速创建1个被观察者对象（Observable）
     *      2.发送事件的特点：延迟指定时间后，发送1个数值0（Long类型）
     *      本质 = 延迟指定时间后，调用一次 onNext(0)
     *  应用场景:
     *      延迟指定事件，发送一个0，一般用于检测
     * - interval()
     *  作用:
     *      1.快速创建1个被观察者对象（Observable）
     *      2.发送事件的特点：每隔指定时间 就发送 事件
     *      发送的事件序列 = 从0开始、无限递增1的的整数序列
     * - intervalRange()
     *  作用:
     *      1.快速创建1个被观察者对象（Observable）
     *      2.发送事件的特点：每隔指定时间 就发送 事件，可指定发送的数据的数量
     *      a. 发送的事件序列 = 从0开始、无限递增1的的整数序列
     *      b. 作用类似于interval（），但可指定发送的数据的数量
     * - range()
     *  作用:
     *      1.快速创建1个被观察者对象（Observable）
     *      2.发送事件的特点：连续发送 1个事件序列，可指定范围
     *      a. 发送的事件序列 = 从0开始、无限递增1的的整数序列
     *      b. 作用类似于intervalRange（），但区别在于：无延迟发送事件
     * - rangeLong()
     *  作用：
     *      类似于range（），区别在于该方法支持数据类型 = Long
     */
    @Test
    public void onCreateObservable(){
        /**
         * - 基础创建
         */
        // create(ObservableOnSubscribe<T> source) Log: http://note.novakj.cn/note/rxjava/944365-d0a699667eedcde6.webp
        Observable<Integer> basicObservable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        });

        /**
         * - 快速创建
         */
        // just(T...) Log: http://note.novakj.cn/note/rxjava/944365-6111f09b3abeff44.webp
        Observable<Integer> fastJustObservable = Observable.just(1, 2, 3);
        // fromArray(T... items) Log: http://note.novakj.cn/note/rxjava/944365-f75a3539b34beea8.webp
        Observable<Integer> fastFromArrayObservable = Observable.fromArray(new Integer[]{1, 2, 3});
        // fromIterable(Iterable<? extends T> source) Log: http://note.novakj.cn/944365-e25ed51afedfd780.webp
        Observable<Integer> fastFromIterableObservable = Observable.fromIterable(Arrays.asList(1, 2, 3));
        // empty()
        Observable<Object> fastEmptyObservable = Observable.empty();
        // error(final Throwable exception)
        Observable<Object> fastErrorObservable = Observable.error(new RuntimeException());
        // never()
        Observable<Object> fastNeverObservable = Observable.never();

        /**
         * - 延迟创建
         */
        // defer(Callable<? extends ObservableSource<? extends T>> supplier) Log: http://note.novakj.cn/note/rxjava/944365-7f137aac70e4ca9e.webp
        Observable<Object> delayedDeferObservable = Observable.defer(new Callable<ObservableSource<?>>() {
            @Override
            public ObservableSource<?> call() throws Exception {
                return Observable.just(1);
            }
        });
        // timer(long delay, TimeUnit unit) Log: http://note.novakj.cn/note/rxjava/944365-e391dbfb772cc6e0.webp
        // 参数1 = 延迟时间
        Observable<Long> delayedTimerObservable = Observable.timer(2, TimeUnit.SECONDS);
        // timer(long delay, TimeUnit unit, Scheduler scheduler)
        Observable<Long> delayedTimerSchedulerObservable = Observable.timer(2, TimeUnit.SECONDS, Schedulers.computation());
        //interval(long initialDelay, long period, TimeUnit unit) Log: http://note.novakj.cn/note/rxjava/944365-3db189b868dc2463.webp
        // 参数1 = 第1次延迟时间
        // 参数2 = 间隔时间数字
        // 参数3 = 时间单位
        Observable<Long> delayedIntervalObservable = Observable.interval(3, 1, TimeUnit.SECONDS);
        //intervalRange(long start, long count, long initialDelay, long period, TimeUnit unit) Log: http://note.novakj.cn/note/rxjava/944365-ed225f309949bdeb.webp
        // 参数1 = 事件序列起始点
        // 参数2 = 事件数量
        // 参数3 = 第1次事件延迟发送时间
        // 参数4 = 间隔时间数字
        // 参数5 = 时间单位
        Observable<Long> delayedLongObservableObservable = Observable.intervalRange(3, 10, 2, 1, TimeUnit.SECONDS);
        // range(final int start, final int count) Log: http://note.novakj.cn/note/rxjava/944365-d6e12b4dcedb1e2a.webp
        // 参数1 = 事件序列起始点
        // 参数2 = 事件数量
        // 注：若设置为负数，则会抛出异常
        Observable<Integer> delayedRangeObservable = Observable.range(3, 10);
        // rangeLong(long start, long count) Log: http://note.novakj.cn/note/rxjava/944365-d6e12b4dcedb1e2a.webp
        // 参数1 = 事件序列起始点
        // 参数2 = 事件数量
        // 注：若设置为负数，则会抛出异常
        Observable<Long> delayedRangeLongObservable = Observable.rangeLong(1L, 3L);
    }

    /**
     * Observable 变化操作符
     * 变换操作符说明: http://note.novakj.cn/note/rxjava/944365-dc0a7df673324e21.webp
     *
     * 作用:
     *     对事件序列中的事件 / 整个事件序列 进行加工处理（即变换），使得其转变成不同的事件 / 整个事件序列
     * 原理:
     *     http://note.novakj.cn/note/rxjava/944365-45e3d263d098c4ee.webp
     *
     * 变换操作符:
     * - Map() Log: http://note.novakj.cn/note/rxjava/944365-d86cc16df735b4ff.webp
     *   原理: http://note.novakj.cn/note/rxjava/944365-a9c0b5eb2cc573d6.webp
     *   作用:
     *      对被观察者发送的每1个事件都通过 指定的函数 处理，从而变换成另外一种事件
     *      即：将被观察者发送的事件转换为任意的类型事件。
     *   应用场景:
     *      数据类型转换
     * - FlatMap() Log: http://note.novakj.cn/note/rxjava/944365-b019ab94b9b10359.webp
     *   原理: http://note.novakj.cn/note/rxjava/944365-a6f852c071db2f15.webp
     *      1.为事件序列中每个事件都创建一个 Observable 对象；
     *      2.将对每个 原始事件 转换后的 新事件 都放入到对应 Observable对象；
     *      3.将新建的每个Observable 都合并到一个 新建的、总的Observable 对象；
     *      4.新建的、总的Observable 对象 将 新合并的事件序列 发送给观察者（Observer）
     *   作用:
     *      将被观察者发送的事件序列进行 拆分 & 单独转换，再合并成一个新的事件序列，最后再进行发送
     *   应用场景：
     *      无序的将被观察者发送的整个事件序列进行变换
     * - ConcatMap Log: http://note.novakj.cn/note/rxjava/944365-7ff08c4f84945fa8.webp
     *   原理: http://note.novakj.cn/note/rxjava/944365-f4340f283e5a954d.webp
     *   作用:
     *      类似FlatMap()操作符
     *   与FlatMap()的 区别在于：拆分 & 重新合并生成的事件序列 的顺序 = 被观察者旧序列生产的顺序
     *   应用场景:
     *      有序的将被观察者发送的整个事件序列进行变换
     * - Buffer() Log: http://note.novakj.cn/note/rxjava/944365-f1d4e320b7c62dd9.webp
     *   原理: http://note.novakj.cn/note/rxjava/944365-5278a339e4337494.webp
     *   过程解释: http://note.novakj.cn/note/rxjava/944365-33a49ffd2ec60794.webp
     *   作用:
     *      定期从 被观察者（Observable）需要发送的事件中 获取一定数量的事件 & 放到缓存区中，最终发送
     *   应用场景:
     *      缓存被观察者发送的事件
     */
    @Test
    public void onTransformObservable(){
        /**
         * 变换操作符
         */

        // map(Function<? super T, ? extends R> mapper)
        // > map() 将参数中的 Integer 类型对象转换成一个 String类型 对象后返回 同时，事件的参数类型也由 Integer 类型变成了 String 类型
        Observable
                .just(1,2,3)
                .map(new Function<Integer, Object>() {
                    @Override
                    public Object apply(Integer integer) throws Exception {
                        return "使用 Map变换操作符 将事件" + integer +"的参数从 整型"+integer + " 变换成 字符串类型" + integer;
                    }
                });

        // flatMap(Function<? super T, ? extends ObservableSource<? extends R>> mapper)
        // > 注：新合并生成的事件序列顺序是无序的，即 与旧序列发送事件的顺序无关
        Observable
                .just(1,2,3)
                .flatMap(new Function<Integer, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Integer integer) throws Exception {
                        final List<String> list = new ArrayList<>();
                        for (int i = 0; i < 3; i++) {
                            list.add("我是事件 " + integer + "拆分后的子事件" + i);
                            // 通过flatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String事件
                            // 最终合并，再发送给被观察者
                        }
                        return Observable.fromIterable(list);
                    }
                });

        //concatMap(Function<? super T, ? extends ObservableSource<? extends R>> mapper)
        // > 注：新合并生成的事件序列顺序是有序的，即 严格按照旧序列发送事件的顺序
        Observable
                .just(1,2,3)
                .concatMap(new Function<Integer, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Integer integer) throws Exception {
                        final List<String> list = new ArrayList<>();
                        for (int i = 0; i < 3; i++) {
                            list.add("我是事件 " + integer + "拆分后的子事件" + i);
                            // 通过concatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String事件
                            // 最终合并，再发送给被观察者
                        }
                        return Observable.fromIterable(list);
                    }
                });

        // buffer(int count, int skip)
        // 设置缓存区大小 & 步长
        // 缓存区大小 = 每次从被观察者中获取的事件数量
        // 步长 = 每次获取新事件的数量
        Observable
                .just(1,2,3,4,5)
                .buffer(3,1);


    }

    /**
     * Observable 组合/合并操作符
     * 组合合并操作符说明: http://note.novakj.cn/note/rxjava/944365-a7b33256c9f07fac.webp
     *                  http://note.novakj.cn/note/rxjava/944365-214478680237ffb8.webp
     *
     * 该类型的操作符的作用 = 组合多个被观察者
     *
     * 组合/合并操作符
     *
     * 组合操作符:
     * - concat() Log: http://note.novakj.cn/note/rxjava/944365-ad8d11dea3d4abd0.webp
     *   concatArray() Log: http://note.novakj.cn/note/rxjava/944365-b4d6ee8caa063675.webp
     *   作用:
     *      组合多个被观察者一起发送数据，合并后 按发送顺序串行执行
     *   二者区别:
     *      组合被观察者的数量，即concat()组合被观察者数量≤4个，而concatArray()则可＞4个
     * - merge() / mergeArray() Log: http://note.novakj.cn/note/rxjava/944365-f75b9d25af87f24c.webp
     *   作用:
     *      组合多个被观察者一起发送数据，合并后 按时间线并行执行
     *   二者区别:
     *      组合被观察者的数量，即merge()组合被观察者数量≤4个，而mergeArray()则可＞4个
     *   区别上述concat()操作符：同样是组合多个被观察者一起发送数据，但concat()操作符合并后是按发送顺序串行执行
     * - concatDelayError() Log: 对比 无：http://note.novakj.cn/note/rxjava/944365-0c907ffaeb2fd449.webp
     *   mergeDelayError()           有: http://note.novakj.cn/note/rxjava/944365-804c8472fc60eb6a.webp
     *   作用:
     *      http://note.novakj.cn/note/rxjava/944365-0a86e8e45f1abb6c.webp
     *
     * 合并操作符：
     * - zip() Log: http://note.novakj.cn/note/rxjava/944365-8986cf9178060877.webp
     *   作用:
     *      合并 多个被观察者（Observable）发送的事件，生成一个新的事件序列（即组合过后的事件序列），并最终发送
     *   原理:
     *      http://note.novakj.cn/note/rxjava/944365-3fa4b1fd4f561820.webp
     *   注意事项：
     *      1.事件组合方式 = 严格按照原先事件序列 进行对位合并
     *      2.最终合并的事件数量 = 多个被观察者（Observable）中数量最少的数量、
     *      http://note.novakj.cn/note/rxjava/944365-de562bab49f5de1b.webp
     *
     *      1.尽管被观察者2的事件D没有事件与其合并，但还是会继续发送
     *      2.若在被观察者1 & 被观察者2的事件序列最后发送onComplete()事件，则被观察者2的事件D也不会发送，测试结果如下
     *      http://note.novakj.cn/note/rxjava/944365-7b241e653250b906.webp
     *   总结:
     *      http://note.novakj.cn/note/rxjava/944365-887b81d9bca4924a.webp
     * - combineLatest() Log: http://note.novakj.cn/note/rxjava/944365-0b9b56ed95835438.webp
     *   作用:
     *      当两个Observables中的任何一个发送了数据后，将先发送了数据的Observables 的最新（最后）一个数据 与 另外一个Observable发送的每个数据结合，最终基于该函数的结果发送数据
     *      与Zip()的区别：Zip() = 按个数合并，即1对1合并；CombineLatest（） = 按时间合并，即在同一个时间点上合并
     * - combineLatestDelayError()
     *   作用：
     *      类似于concatDelayError（） / mergeDelayError（） ，即错误处理，此处不作过多描述
     * - reduce() Log: http://note.novakj.cn/note/rxjava/944365-3f63477c864d7aae.webp
     *   作用:
     *      把被观察者需要发送的事件聚合成1个事件 & 发送
     *      聚合的逻辑根据需求撰写，但本质都是前2个数据聚合，然后与后1个数据继续进行聚合，依次类推
     * - collect() Log: http://note.novakj.cn/note/rxjava/944365-ab51b84d6a373330.webp
     *   作用：
     *      将被观察者Observable发送的数据事件收集到一个数据结构里
     * - startWith()/startWithArray() Log: http://note.novakj.cn/note/rxjava/944365-1fabfa60e8535de2.webp
     *   作用:
     *      在一个被观察者发送事件前，追加发送一些数据 / 一个新的被观察者
     * - count() Log: http://note.novakj.cn/note/rxjava/944365-45d279a5773edfc2.webp
     *   作用:
     *      统计被观察者发送事件的数量
     */
    @Test
    public void onCombinationMergerObservable(){
        //concat(ObservableSource<? extends T> source1, ObservableSource<? extends T> source2,ObservableSource<? extends T> source3)
        // 组合多个被观察者（≤4个）一起发送数据 串行执行
        Observable<Integer> concatObservable = Observable.concat(
                Observable.just(1, 2, 3),
                Observable.just(4, 5, 6),
                Observable.just(7, 8, 9)
        );
        //concatArray(ObservableSource<? extends T>... sources)
        // 串行执行
        Observable.concatArray(
                Observable.just(1, 2, 3),
                Observable.just(4, 5, 6),
                Observable.just(7, 8, 9),
                Observable.just(10,11,12)
        );
        //merge(ObservableSource<? extends T> source1, ObservableSource<? extends T> source2)
        // 注：合并后按照时间线并行执行
        Observable<Long> merge = Observable.merge(
                Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS),
                Observable.intervalRange(2, 3, 1, 1, TimeUnit.SECONDS)
        );
        //mergeArray() = 组合4个以上的被观察者一起发送数据
        Observable.mergeArray(
                Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS),
                Observable.intervalRange(2, 3, 1, 1, TimeUnit.SECONDS),
                Observable.intervalRange(4, 3, 1, 1, TimeUnit.SECONDS),
                Observable.intervalRange(6, 3, 1, 1, TimeUnit.SECONDS)
        );

        // 无使用concatDelayError()的情况
        Observable.concat(
                Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        emitter.onNext(1);
                        emitter.onNext(2);
                        emitter.onNext(3);
                        emitter.onError(new NullPointerException()); // 发送Error事件，因为无使用concatDelayError，所以第2个Observable将不会发送事件
                        emitter.onComplete();
                    }
                }),
                Observable.just(4, 5, 6));
        //使用了concatDelayError()的情况
        Observable.concatArrayDelayError(
                Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        emitter.onNext(1);
                        emitter.onNext(2);
                        emitter.onNext(3);
                        emitter.onError(new NullPointerException()); // 发送Error事件，因为使用了concatDelayError，所以第2个Observable将会发送事件，等发送完毕后，再发送错误事件
                        emitter.onComplete();
                    }
                }),
                Observable.just(4, 5, 6));
    }
}
