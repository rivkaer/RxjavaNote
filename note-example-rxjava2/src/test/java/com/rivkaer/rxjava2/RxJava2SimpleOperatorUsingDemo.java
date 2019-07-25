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

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * 版权说明 图片引用&Rxjava学习路径参照 https://www.jianshu.com/nb/14302692
 */
public class RxJava2SimpleOperatorUsingDemo {

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
     * <p>
     * * 基础创建
     * - create(ObservableOnSubscribe<T> source) 传入实例化的ObservableOnSubscribe对象创建Observable
     * <p>
     * * 快速创建
     * - just(T...)
     * 作用:
     * 1.快速创建1个被观察者对象（Observable）
     * 2.发送事件的特点：直接发送 传入的事件
     * 注：最多只能发送10个参数
     * 应用场景：
     * 快速创建 被观察者对象（Observable） & 发送10个以下事件
     * - fromArray(T... items)
     * 作用:
     * 1.快速创建1个被观察者对象（Observable）
     * 2.发送事件的特点：直接发送 传入的数组数据
     * 会将数组中的数据转换为Observable对象
     * 应用场景：
     * 快速创建 被观察者对象（Observable） & 发送10个以上事件（数组形式）
     * 数组元素遍历
     * - fromIterable(Iterable<? extends T> source)
     * 作用：
     * 1.快速创建1个被观察者对象（Observable）
     * 2.发送事件的特点：直接发送 传入的集合List数据
     * 会将数组中的数据转换为Observable对象
     * 应用场景:
     * 1.快速创建 被观察者对象（Observable） & 发送10个以上事件（集合形式）
     * 2.集合元素遍历
     * - empty() 该方法创建的被观察者对象发送事件的特点：仅发送Complete事件，直接通知完成。 即观察者接收后会直接调用onCompleted（）
     * - error(final Throwable exception) 该方法创建的被观察者对象发送事件的特点：仅发送Error事件，直接通知异常。即观察者接收后会直接调用onError（）
     * - never() 该方法创建的被观察者对象发送事件的特点：不发送任何事件 即观察者接收后什么都不调用
     * <p>
     * * 延迟创建
     * 1.定时操作：在经过了x秒后，需要自动执行y操作
     * 2.周期性操作：每隔x秒后，需要自动执行y操作
     * <p>
     * - defer()
     * 作用:
     * 直到有观察者（Observer ）订阅时，才动态创建被观察者对象（Observable） & 发送事件
     * 1.通过 Observable工厂方法创建被观察者对象（Observable）
     * 2.每次订阅后，都会得到一个刚创建的最新的Observable对象，这可以确保Observable对象里的数据是最新的
     * 应用场景:
     * 动态创建被观察者对象（Observable） & 获取最新的Observable对象数据
     * - timer()
     * 作用:
     * 1.快速创建1个被观察者对象（Observable）
     * 2.发送事件的特点：延迟指定时间后，发送1个数值0（Long类型）
     * 本质 = 延迟指定时间后，调用一次 onNext(0)
     * 应用场景:
     * 延迟指定事件，发送一个0，一般用于检测
     * - interval()
     * 作用:
     * 1.快速创建1个被观察者对象（Observable）
     * 2.发送事件的特点：每隔指定时间 就发送 事件
     * 发送的事件序列 = 从0开始、无限递增1的的整数序列
     * - intervalRange()
     * 作用:
     * 1.快速创建1个被观察者对象（Observable）
     * 2.发送事件的特点：每隔指定时间 就发送 事件，可指定发送的数据的数量
     * a. 发送的事件序列 = 从0开始、无限递增1的的整数序列
     * b. 作用类似于interval（），但可指定发送的数据的数量
     * - range()
     * 作用:
     * 1.快速创建1个被观察者对象（Observable）
     * 2.发送事件的特点：连续发送 1个事件序列，可指定范围
     * a. 发送的事件序列 = 从0开始、无限递增1的的整数序列
     * b. 作用类似于intervalRange（），但区别在于：无延迟发送事件
     * - rangeLong()
     * 作用：
     * 类似于range（），区别在于该方法支持数据类型 = Long
     */
    @Test
    public void onCreateObservable() {
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
     * <p>
     * 作用:
     * 对事件序列中的事件 / 整个事件序列 进行加工处理（即变换），使得其转变成不同的事件 / 整个事件序列
     * 原理:
     * http://note.novakj.cn/note/rxjava/944365-45e3d263d098c4ee.webp
     * <p>
     * 变换操作符:
     * - Map() Log: http://note.novakj.cn/note/rxjava/944365-d86cc16df735b4ff.webp
     * 原理: http://note.novakj.cn/note/rxjava/944365-a9c0b5eb2cc573d6.webp
     * 作用:
     * 对被观察者发送的每1个事件都通过 指定的函数 处理，从而变换成另外一种事件
     * 即：将被观察者发送的事件转换为任意的类型事件。
     * 应用场景:
     * 数据类型转换
     * - FlatMap() Log: http://note.novakj.cn/note/rxjava/944365-b019ab94b9b10359.webp
     * 原理: http://note.novakj.cn/note/rxjava/944365-a6f852c071db2f15.webp
     * 1.为事件序列中每个事件都创建一个 Observable 对象；
     * 2.将对每个 原始事件 转换后的 新事件 都放入到对应 Observable对象；
     * 3.将新建的每个Observable 都合并到一个 新建的、总的Observable 对象；
     * 4.新建的、总的Observable 对象 将 新合并的事件序列 发送给观察者（Observer）
     * 作用:
     * 将被观察者发送的事件序列进行 拆分 & 单独转换，再合并成一个新的事件序列，最后再进行发送
     * 应用场景：
     * 无序的将被观察者发送的整个事件序列进行变换
     * - ConcatMap Log: http://note.novakj.cn/note/rxjava/944365-7ff08c4f84945fa8.webp
     * 原理: http://note.novakj.cn/note/rxjava/944365-f4340f283e5a954d.webp
     * 作用:
     * 类似FlatMap()操作符
     * 与FlatMap()的 区别在于：拆分 & 重新合并生成的事件序列 的顺序 = 被观察者旧序列生产的顺序
     * 应用场景:
     * 有序的将被观察者发送的整个事件序列进行变换
     * - Buffer() Log: http://note.novakj.cn/note/rxjava/944365-f1d4e320b7c62dd9.webp
     * 原理: http://note.novakj.cn/note/rxjava/944365-5278a339e4337494.webp
     * 过程解释: http://note.novakj.cn/note/rxjava/944365-33a49ffd2ec60794.webp
     * 作用:
     * 定期从 被观察者（Observable）需要发送的事件中 获取一定数量的事件 & 放到缓存区中，最终发送
     * 应用场景:
     * 缓存被观察者发送的事件
     */
    @Test
    public void onTransformObservable() {
        /**
         * 变换操作符
         */

        // map(Function<? super T, ? extends R> mapper)
        // > map() 将参数中的 Integer 类型对象转换成一个 String类型 对象后返回 同时，事件的参数类型也由 Integer 类型变成了 String 类型
        Observable
                .just(1, 2, 3)
                .map(new Function<Integer, Object>() {
                    @Override
                    public Object apply(Integer integer) throws Exception {
                        return "使用 Map变换操作符 将事件" + integer + "的参数从 整型" + integer + " 变换成 字符串类型" + integer;
                    }
                });

        // flatMap(Function<? super T, ? extends ObservableSource<? extends R>> mapper)
        // > 注：新合并生成的事件序列顺序是无序的，即 与旧序列发送事件的顺序无关
        Observable
                .just(1, 2, 3)
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
                .just(1, 2, 3)
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
                .just(1, 2, 3, 4, 5)
                .buffer(3, 1);


    }

    /**
     * Observable 组合/合并操作符
     * 组合合并操作符说明: http://note.novakj.cn/note/rxjava/944365-a7b33256c9f07fac.webp
     * http://note.novakj.cn/note/rxjava/944365-214478680237ffb8.webp
     * <p>
     * 该类型的操作符的作用 = 组合多个被观察者
     * <p>
     * 组合/合并操作符
     * <p>
     * 组合操作符:
     * - concat() Log: http://note.novakj.cn/note/rxjava/944365-ad8d11dea3d4abd0.webp
     * concatArray() Log: http://note.novakj.cn/note/rxjava/944365-b4d6ee8caa063675.webp
     * 作用:
     * 组合多个被观察者一起发送数据，合并后 按发送顺序串行执行
     * 二者区别:
     * 组合被观察者的数量，即concat()组合被观察者数量≤4个，而concatArray()则可＞4个
     * - merge() / mergeArray() Log: http://note.novakj.cn/note/rxjava/944365-f75b9d25af87f24c.webp
     * 作用:
     * 组合多个被观察者一起发送数据，合并后 按时间线并行执行
     * 二者区别:
     * 组合被观察者的数量，即merge()组合被观察者数量≤4个，而mergeArray()则可＞4个
     * 区别上述concat()操作符：同样是组合多个被观察者一起发送数据，但concat()操作符合并后是按发送顺序串行执行
     * - concatDelayError() Log: 对比 无：http://note.novakj.cn/note/rxjava/944365-0c907ffaeb2fd449.webp
     * mergeDelayError()           有: http://note.novakj.cn/note/rxjava/944365-804c8472fc60eb6a.webp
     * 作用:
     * http://note.novakj.cn/note/rxjava/944365-0a86e8e45f1abb6c.webp
     * <p>
     * 合并操作符：
     * - zip() Log: http://note.novakj.cn/note/rxjava/944365-8986cf9178060877.webp
     * 作用:
     * 合并 多个被观察者（Observable）发送的事件，生成一个新的事件序列（即组合过后的事件序列），并最终发送
     * 原理:
     * http://note.novakj.cn/note/rxjava/944365-3fa4b1fd4f561820.webp
     * 注意事项：
     * 1.事件组合方式 = 严格按照原先事件序列 进行对位合并
     * 2.最终合并的事件数量 = 多个被观察者（Observable）中数量最少的数量、
     * http://note.novakj.cn/note/rxjava/944365-de562bab49f5de1b.webp
     * <p>
     * 1.尽管被观察者2的事件D没有事件与其合并，但还是会继续发送
     * 2.若在被观察者1 & 被观察者2的事件序列最后发送onComplete()事件，则被观察者2的事件D也不会发送，测试结果如下
     * http://note.novakj.cn/note/rxjava/944365-7b241e653250b906.webp
     * 总结:
     * http://note.novakj.cn/note/rxjava/944365-887b81d9bca4924a.webp
     * - combineLatest() Log: http://note.novakj.cn/note/rxjava/944365-0b9b56ed95835438.webp
     * 作用:
     * 当两个Observables中的任何一个发送了数据后，将先发送了数据的Observables 的最新（最后）一个数据 与 另外一个Observable发送的每个数据结合，最终基于该函数的结果发送数据
     * 与Zip()的区别：Zip() = 按个数合并，即1对1合并；CombineLatest（） = 按时间合并，即在同一个时间点上合并
     * - combineLatestDelayError()
     * 作用：
     * 类似于concatDelayError（） / mergeDelayError（） ，即错误处理，此处不作过多描述
     * - reduce() Log: http://note.novakj.cn/note/rxjava/944365-3f63477c864d7aae.webp
     * 作用:
     * 把被观察者需要发送的事件聚合成1个事件 & 发送
     * 聚合的逻辑根据需求撰写，但本质都是前2个数据聚合，然后与后1个数据继续进行聚合，依次类推
     * - collect() Log: http://note.novakj.cn/note/rxjava/944365-ab51b84d6a373330.webp
     * 作用：
     * 将被观察者Observable发送的数据事件收集到一个数据结构里
     * - startWith()/startWithArray() Log: http://note.novakj.cn/note/rxjava/944365-1fabfa60e8535de2.webp
     * 作用:
     * 在一个被观察者发送事件前，追加发送一些数据 / 一个新的被观察者
     * - count() Log: http://note.novakj.cn/note/rxjava/944365-45d279a5773edfc2.webp
     * 作用:
     * 统计被观察者发送事件的数量
     */
    @Test
    public void onCombinationMergerObservable() {
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
                Observable.just(10, 11, 12)
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

        //zip
        //合并 多个被观察者（Observable）发送的事件，生成一个新的事件序列（即组合过后的事件序列），并最终发送
        /**
         * 特别注意:
         *  1.一个观察者的时间没有事件与之合并也会继续发送
         *  2.若被观察者1&被观察者2的时间序列最后发送```onComplete```时间，则被被观察者2的事件也不会发送
         */
        Observable<Integer> firstIntegerObservable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                Thread.sleep(1000);
                emitter.onNext(2);
                Thread.sleep(1000);
                emitter.onComplete();
            }
        });

        Observable<String> lastIntegerObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("A");
                Thread.sleep(1000);
                emitter.onNext("B");
                Thread.sleep(1000);
                emitter.onNext("C");
                Thread.sleep(1000);
                emitter.onComplete();
            }
        });

        Observable.zip(
                firstIntegerObservable,
                lastIntegerObservable,
                new BiFunction<Integer, String, String>() {
                    @Override
                    public String apply(Integer integer, String string) throws Exception {
                        return integer + " -- " + string;
                    }
                });

        //combineLatest()
        //当两个Observables中的任何一个发送了数据后，将先发送了数据的Observables 的最新（最后）一个数据 与 另外一个Observable发送的每个数据结合，最终基于该函数的结果发送数据
        //与Zip（）的区别：Zip()= 按个数合并，即1对1合并；CombineLatest() = 按时间合并，即在同一个时间点上合并
        Observable.combineLatest(
                Observable.just(1, 2, 3),
                Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS),
                new BiFunction<Integer, Long, String>() {
                    @Override
                    public String apply(Integer integer, Long aLong) throws Exception {
                        return integer + " --- " + aLong;
                    }
                }
        );

        //combineLatestDelayError()  combineLatest合并异常时使用

        //reduce
        // 把被观察者需要发送的事件聚合成1个事件 & 发送
        //聚合的逻辑根据需求撰写，但本质都是前2个数据聚合，然后与后1个数据继续进行聚合，依次类推
        Observable.just(1, 2, 3)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        return integer + integer2;
                    }
                });

        //collect()
        //将被观察者Observable发送的数据事件收集到一个数据结构里
        Observable.just(1, 2, 3)
                .collect(new Callable<ArrayList<Integer>>() {
                    @Override
                    public ArrayList<Integer> call() throws Exception {
                        return new ArrayList<>();
                    }
                }, new BiConsumer<ArrayList<Integer>, Integer>() {
                    @Override
                    public void accept(ArrayList<Integer> integers, Integer integer) throws Exception {
                        integers.add(integer);
                    }
                });

        //startWith() / startWithArray()
        //在一个被观察者发送事件前，追加发送一些数据 / 一个新的被观察者
        Observable.just(1, 2, 3)
                .startWith(0)
                .startWithArray(-2, -1);

        Observable.just(1, 2, 3)
                .startWith(Observable.just(-2, -1, 0));

        //count()
        //统计被观察者发送事件的数量
        Observable.just("A", "C", "D", "E")
                .count()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        System.out.println("事件总数: " + aLong);
                    }
                });

    }

    /**
     * Observable 功能性操作符
     * 功能操作符说明：http://note.novakj.cn/note/rxjava944365-ff3df2b42968833d.webp
     * <p>
     * - 连接被观察者&观察者(subscribe)
     * 应用场景: 即使得被观察者 & 观察者 形成订阅关系
     * <p>
     * - 线程操作操作符(subscribeOn() & observeOn())
     * 作用: 线程控制，即指定 被观察者 （Observable） / 观察者（Observer） 的工作线程类型
     * 线程类型：
     * <p>
     * Schedulers.immediate()
     * 当前线程 = 不指定线程
     * 默认
     * <p>
     * AndroidSchedulers.mainThread()
     * Android主线程
     * 操作UI
     * <p>
     * Schedulers.newThread()
     * 常规新线程
     * 耗时等操作
     * <p>
     * Schedulers.io()
     * io操作线程
     * 网络请求、读写文件等io密集型操作
     * <p>
     * Schedulers.computation()
     * CPU计算操作线程
     * 大量计算操作
     * <p>
     * 注: RxJava内部使用 线程池 来维护这些线程，所以线程的调度效率非常高。
     * 特别注意:
     * 1.若Observable.subscribeOn（）多次指定被观察者 生产事件的线程，则只有第一次指定有效，其余的指定线程无效
     * 2.若Observable.observeOn（）多次指定观察者 接收 & 响应事件的线程，则每次指定均有效，即每指定一次，就会进行一次线程的切换
     * - 延迟操作(delay())
     * 应用场景: 即在被观察者发送事件前进行一些延迟的操作 使得被观察者延迟一段时间再发送事件
     * // 1. 指定延迟时间
     * // 参数1 = 时间；参数2 = 时间单位
     * delay(long delay,TimeUnit unit)
     * <p>
     * // 2. 指定延迟时间 & 调度器
     * // 参数1 = 时间；参数2 = 时间单位；参数3 = 线程调度器
     * delay(long delay,TimeUnit unit,mScheduler scheduler)
     * <p>
     * // 3. 指定延迟时间  & 错误延迟
     * // 错误延迟，即：若存在Error事件，则如常执行，执行后再抛出错误异常
     * // 参数1 = 时间；参数2 = 时间单位；参数3 = 错误延迟参数
     * delay(long delay,TimeUnit unit,boolean delayError)
     * <p>
     * // 4. 指定延迟时间 & 调度器 & 错误延迟
     * // 参数1 = 时间；参数2 = 时间单位；参数3 = 线程调度器；参数4 = 错误延迟参数
     * delay(long delay,TimeUnit unit,mScheduler scheduler,boolean delayError): 指定延迟多长时间并添加调度器，错误通知可以设置是否延迟
     * <p>
     * 在事件的生命周期中操作(do)
     * 操作符说明: http://note.novakj.cn/note/rxjava944365-3f411ad304df78d5.webp
     * <p>
     * - 错误操作
     * 错误操作符说明:http://note.novakj.cn/note/rxjava944365-abbc7ffe57770e84.webp
     * -- onErrorReturn()
     * 作用: 遇到错误时，发送1个特殊事件 & 正常终止 可捕获在它之前发生的异常
     * -- onErrorResumeNext()
     * 作用: 到错误时，发送1个新的Observable
     * 注:
     * 1.onErrorResumeNext（）拦截的错误 = Throwable；若需拦截Exception请用onExceptionResumeNext（）
     * 2.若onErrorResumeNext（）拦截的错误 = Exception，则会将错误传递给观察者的onError方法
     * -- onExceptionResumeNext()
     * 作用: 遇到错误时，发送1个新的Observable
     * 注:
     * 1.onExceptionResumeNext（）拦截的错误 = Exception；若需拦截Throwable请用onErrorResumeNext（）
     * 2.若onExceptionResumeNext（）拦截的错误 = Throwable，则会将错误传递给观察者的onError方法
     * -- retry()
     * 作用: 重试，即当出现错误时，让被观察者（Observable）重新发射数据
     * 注:
     * 1.接收到 onError（）时，重新订阅 & 发送事件
     * 2.Throwable 和 Exception都可拦截
     * 解释:
     * <-- 1. retry（） -->
     * // 作用：出现错误时，让被观察者重新发送数据
     * // 注：若一直错误，则一直重新发送
     * <p>
     * <-- 2. retry（long time） -->
     * // 作用：出现错误时，让被观察者重新发送数据（具备重试次数限制
     * // 参数 = 重试次数
     * <p>
     * <-- 3. retry（Predicate predicate） -->
     * // 作用：出现错误后，判断是否需要重新发送数据（若需要重新发送& 持续遇到错误，则持续重试）
     * // 参数 = 判断逻辑
     * <p>
     * <--  4. retry（new BiPredicate<Integer, Throwable>） -->
     * // 作用：出现错误后，判断是否需要重新发送数据（若需要重新发送 & 持续遇到错误，则持续重试
     * // 参数 =  判断逻辑（传入当前重试次数 & 异常错误信息）
     * <p>
     * <-- 5. retry（long time,Predicate predicate） -->
     * // 作用：出现错误后，判断是否需要重新发送数据（具备重试次数限制
     * // 参数 = 设置重试次数 & 判断逻辑
     * -- retryUntil()
     * 作用:  出现错误后，判断是否需要重新发送数据
     * 注:
     * 1.若需要重新发送 & 持续遇到错误，则持续重试
     * 2.作用类似于retry（Predicate predicate）
     * 使用:
     * 若需要重新发送 & 持续遇到错误，则持续重试
     * 作用类似于retry（Predicate predicate）
     * -- retryWhen()
     * 作用:
     * 遇到错误时，将发生的错误传递给一个新的被观察者（Observable），并决定是否需要重新订阅原始被观察者（Observable）& 发送事件
     * <p>
     * - 重复发送
     * 应用场景:
     * 重复不断地发送被观察者事件
     * --repeat（）
     * 作用: 无条件地、重复发送 被观察者事件  具备重载方法，可设置重复创建次数
     * <p>
     * -- repeatWhen（）
     * 作用:有条件地、重复发送 被观察者事件
     * 原理:将原始 Observable 停止发送事件的标识（Complete（） /  Error（））转换成1个 Object 类型数据传递给1个新被观察者（Observable），以此决定是否重新订阅 & 发送原来的 Observable
     * 1.若新被观察者（Observable）返回1个Complete / Error事件，则不重新订阅 & 发送原来的 Observable
     * 2.若新被观察者（Observable）返回其余事件时，则重新订阅 & 发送原来的 Observable
     */
    @Test
    public void onFunctionObservable() {

        //delay 延时操作
        Observable.just(1, 2, 3)
                .delay(3, TimeUnit.SECONDS);

        //do 操作符
        Observable.just(1)
                .doOnEach(new Consumer<Notification<Integer>>() {
                    @Override
                    public void accept(Notification<Integer> integerNotification) throws Exception {
                        //当Observable每发送1次数据事件就会调用1次
                    }
                })
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        //  执行Next事件前调用
                    }
                })
                .doAfterNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        // 执行Next事件后调用
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //Observable正常发送事件完毕后调用
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        // Observable发送错误事件时调用
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        //观察者订阅时调用
                        //可用于异步操作ui loading的开启
                    }
                })
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        //Observable发送事件完毕后调用，无论正常发送完毕 / 异常终止
                        //可用于异步操作ui loading的关闭
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        // 最后执行
                    }
                });

        /**
         * 错误处理
         */
        // onErrorReturn()
        // 遇到错误时，发送1个特殊事件 & 正常终止  可捕获在它之前发生的异常
        Observable.just(1)
                .onErrorReturn(new Function<Throwable, Integer>() {
                    @Override
                    public Integer apply(Throwable throwable) throws Exception {
                        //发生错误事件后，发送一个"1024"事件，最终正常结束
                        return 1024;
                    }
                });
        //onErrorResumeNext()
        // 遇到错误时，发送1个新的Observable
        Observable.just(1)
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
                    @Override
                    public ObservableSource<? extends Integer> apply(Throwable throwable) throws Exception {
                        return Observable.just(3);
                    }
                });
        // onExceptionResumeNext()
        //遇到错误时，发送1个新的Observable
        Observable.just(1)
                .onExceptionResumeNext(Observable.just(3));
        // retry()
        //重试，即当出现错误时，让被观察者（Observable）重新发射数据
        Observable.just(1)
                .replay(1);
        // retryUntil()
        // 出现错误后，判断是否需要重新发送数据
        //若需要重新发送 & 持续遇到错误，则持续重试
        //作用类似于retry（Predicate predicate）
        Observable.just(1)
                .repeatUntil(new BooleanSupplier() {
                    @Override
                    public boolean getAsBoolean() throws Exception {
                        return false;
                    }
                });
        //retryWhen()
        //遇到错误时，将发生的错误传递给一个新的被观察者（Observable），并决定是否需要重新订阅原始被观察者（Observable）& 发送事件
        Observable.just(1)
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                        return Observable.just("C");
                    }
                });
        /**
         * 重复发送
         */
        //repeat()
        // 无条件地、重复发送 被观察者事件 具备重载方法，可设置重复创建次数
        Observable.just(1)
                .repeat(1);
        //repeatWhen()
        //有条件地、重复发送 被观察者事件
        Observable.just(1)
                .repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(final Observable<Object> objectObservable) throws Exception {
                        return objectObservable.flatMap(new Function<Object, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Object o) throws Exception {
                                return Observable.empty();
                            }
                        });
                    }
                });

    }

    /**
     * Observable 过滤操作符
     * 过滤操作符说明:http://note.novakj.cn/note/rxjava944365-83fb8e7038dfd51a.webp
     * 应用场景:
     * 过滤操作符的应用场景包括：
     * 1.根据 指定条件 过滤事件
     * 2.根据 指定事件数量 过滤事件
     * 3.根据 指定时间 过滤事件
     * 4.根据 指定事件位置 过滤事件
     * <p>
     * - 根据 指定条件 过滤事件
     * 需求场景:通过设置指定的过滤条件，当且仅当该事件满足条件，就将该事件过滤（不发送）
     * 对应操作符类型:http://note.novakj.cn/note/rxjava944365-ac41ab7e0cc5d2fd.webp
     * -- filter()
     * 作用: 过滤 特定条件的事件
     * -- ofType()
     * 作用:过滤 特定数据类型的数据
     * -- skip/skipLast()
     * 作用: 跳过某个事件
     * -- distinct() / distinctUntilChanged()
     * 作用: 过滤事件序列中重复的事件 / 连续重复的事件
     * <p>
     * - 根据 指定事件数量 过滤事件
     * 应用场景: 通过设置指定的事件数量，仅发送特定数量的事件
     * 操作符类型: take（） & takeLast（）
     * -- take()
     * 作用: 指定观察者最多能接收到的事件数量
     * --takeLast()
     * 作用: 指定观察者只能接收到被观察者发送的最后几个事件
     * <p>
     * - 根据 指定时间 过滤事件
     * 应用场景: 通过设置指定的时间，仅发送在该时间内的事件
     * 对应操作符类型: http://note.novakj.cn/note/rxjava944365-e8287b741257a7f4.webp
     * -- throttleFirst()/ throttleLast()
     * 作用: 在某段时间内，只发送该段时间内第1次事件 / 最后1次事件
     * 如，1段时间内连续点击按钮，但只执行第1次的点击操作
     * -- Sample()
     * 作用: 在某段时间内，只发送该段时间内最新（最后）1次事件 与 throttleLast（） 操作符类似
     * -- throttleWithTimeout （） / debounce（）
     * 作用:发送数据事件时，若2次发送事件的间隔＜指定时间，就会丢弃前一次的数据，直到指定时间内都没有新数据发射时才会发送后一次的数据
     * <p>
     * - 根据 指定事件位置 过滤事件
     * 应用场景: 通过设置指定的位置，过滤在该位置的事件
     * 操作符类型:http://note.novakj.cn/note/rxjava944365-b9619b00c616923a.webp
     * -- firstElement() / lastElement()
     * 作用: 仅选取第1个元素 / 最后一个元素
     * -- elementAt()
     * 作用: 指定接收某个元素（通过 索引值 确定）
     * 注：允许越界，即获取的位置索引 ＞ 发送事件序列长度
     * -- elementAtOrError()
     * 作用:在elementAt（）的基础上，当出现越界情况（即获取的位置索引 ＞ 发送事件序列长度）时，即抛出异常
     */
    @Test
    public void onFilterObservable() {

        /**
         * 根据 指定条件 过滤事件
         */
        //filter()
        Observable.just(1, 2, 3)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer > 2;
                    }
                });
        //ofType
        Observable.just(1, "A", 2, "C")
                .ofType(String.class);

        //skip() / skipLast
        Observable.just(1, 2, 3, 4, 5)
                .skip(1) // 跳过正序的前1项
                .skipLast(2); // 跳过正序的后2项
        Observable.intervalRange(0, 5, 0, 1, TimeUnit.SECONDS)
                .skip(1, TimeUnit.SECONDS) // 跳过第1s发送的数据
                .skipLast(1, TimeUnit.SECONDS); // 跳过最后1s发送的数据

        //distinct() / distinctUntilChanged()
        //过滤事件序列中重复的事件
        Observable.just(1, 2, 3, 1, 2)
                .distinct();
        //过滤事件序列中 连续重复的事件
        // 下面序列中，连续重复的事件 = 3、4
        Observable.just(1, 2, 3, 1, 2, 3, 3, 4, 4);

        /**
         * 根据 指定事件数量 过滤事件
         */
        //take()
        //指定观察者最多能接收到的事件数量
        Observable.just(1, 2, 3)
                .take(2);
        //takeLast()
        //指定观察者只能接收到被观察者发送的最后几个事件
        Observable.just(1, 2, 3)
                .takeLast(1);

        /**
         * 根据 指定时间 过滤事件
         */
        //throttleFirst（）/ throttleLast（）
        //在某段时间内，只发送该段时间内第1次事件 / 最后1次事件
        Observable.just(1, 2, 3)
                .throttleFirst(1, TimeUnit.SECONDS)
                .throttleLast(1, TimeUnit.SECONDS);
        //Sample（）
        //在某段时间内，只发送该段时间内最新（最后）1次事件
        Observable.just(1, 2, 3)
                .sample(1, TimeUnit.SECONDS);

        /**
         * 根据 指定事件位置 过滤事件
         */
        //firstElement（） / lastElement（）
        //仅选取第1个元素 / 最后一个元素
        Observable.just(1, 2, 3)
                .firstElement();
        Observable.just(1, 2, 3)
                .lastElement();
        //elementAt（）
        //指定接收某个元素（通过 索引值 确定）
        //注：允许越界，即获取的位置索引 ＞ 发送事件序列长度
        Observable.just(1, 2, 3)
                .elementAt(1);
        //elementAtOrError（）
        //在elementAt（）的基础上，当出现越界情况（即获取的位置索引 ＞ 发送事件序列长度）时，即抛出异常
        Observable.just(1)
                .elementAtOrError(100);
    }

    /**
     * Observable 条件/布尔操作符
     * 作用: 通过设置函数，判断被观察者（Observable）发送的事件是否符合条件
     * 操作符类型:http://note.novakj.cn/944365-e25ed51afedfd780.webp
     * 操作符作用:http://note.novakj.cn/note/rxjava944365-3234d4bc974afbc8.webp
     * - all()
     * 作用: 判断发送的每项数据是否都满足 设置的函数条件
     * 若满足，返回 true；否则，返回 false
     * - takeWhile()
     * 作用:判断发送的每项数据是否满足 设置函数条件
     * 若发送的数据满足该条件，则发送该项数据；否则不发送
     * - skipWhile()
     * 作用:判断发送的每项数据是否满足 设置函数条件
     * 直到该判断条件 = false时，才开始发送Observable的数据
     * -  takeUntil()
     * 作用:执行到某个条件时，停止发送事件
     * 该判断条件也可以是Observable，即 等到 takeUntil（） 传入的Observable开始发送数据，（原始）第1个Observable的数据停止发送数据
     * - skipUntil()
     * 作用:
     * 等到 skipUntil（） 传入的Observable开始发送数据，（原始）第1个Observable的数据才开始发送数据
     * - SequenceEqual()
     * 作用:
     * 判定两个Observables需要发送的数据是否相同
     * - contains()
     * 作用:
     * 判断发送的数据中是否包含指定数据
     * 若包含，返回 true；否则，返回 false
     * 内部实现 = exists（）
     * - isEmpty()
     * 作用:
     * 判断发送的数据是否为空
     * - amb()
     * 作用:
     * 当需要发送多个 Observable时，只发送 先发送数据的Observable的数据，而其余 Observable则被丢弃。
     * - defaultIfEmpty()
     * 作用:
     * 在不发送任何有效事件（ Next事件）、仅发送了 Complete 事件的前提下，发送一个默认值
     */
    @Test
    public void onConditionsJudgment() {

        //all
        // 判断发送的每项数据是否都满足 设置的函数条件
        Observable.just(1, 2, 3)
                .all(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer > 2;
                    }
                });
        //takeWhile（）
        //判断发送的每项数据是否满足 设置函数条件
        //若发送的数据满足该条件，则发送该项数据；否则不发送
        Observable.just(1,2,3)
                .takeWhile(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer>2;
                    }
                });
        //skipWhile()
        // 判断发送的每项数据是否满足 设置函数条件
        //直到该判断条件 = false时，才开始发送Observable的数据
        //每隔1s发送1个数据 = 从0开始，每次递增1
        Observable.interval(1, TimeUnit.SECONDS)
                // 2. 通过skipWhile（）设置判断条件
                .skipWhile(new Predicate<Long>(){
                    @Override
                    public boolean test( Long aLong) throws Exception {
                        return (aLong<5);
                        // 直到判断条件不成立 = false = 发射的数据≥5，才开始发送数据
                    }
                });
        //takeUntil()
        //执行到某个条件时，停止发送事件
        //判断条件也可以是Observable，即 等到 takeUntil（） 传入的Observable开始发送数据，（原始）第1个Observable的数据停止发送数据
        Observable.interval(1,TimeUnit.SECONDS)
                .takeUntil(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        return aLong > 3;
                    }
                });
        //skipUntil()
        //等到 skipUntil（） 传入的Observable开始发送数据，（原始）第1个Observable的数据才开始发送数据
        Observable.interval(1,TimeUnit.SECONDS)
                .skipUntil(Observable.timer(5,TimeUnit.SECONDS));

        //SequenceEqual()
        // 判定两个Observables需要发送的数据是否相同
        // 若相同，返回 true；否则，返回 false
        Observable.sequenceEqual(Observable.just(1),Observable.just(2));

        //contains()
        // 判断发送的数据中是否包含指定数据
        //若包含，返回 true；否则，返回 false
        //内部实现 = exists()
        Observable.just(1,2,3)
                .contains(4);

        // isEmpty()
        // 判断发送的数据是否为空
        // 若为空，返回 true；否则，返回 false
        Observable.just(1,2,3)
                .isEmpty();

        // amb()
        // 当需要发送多个 Observable时，只发送 先发送数据的Observable的数据，而其余 Observable则被丢弃。
        // 设置2个需要发送的Observable & 放入到集合中
        List<ObservableSource<Integer>> list= new ArrayList <>();
        // 第1个Observable延迟1秒发射数据
        list.add( Observable.just(1,2,3).delay(1,TimeUnit.SECONDS));
        // 第2个Observable正常发送数据
        list.add( Observable.just(4,5,6));
        // 一共需要发送2个Observable的数据
        // 但由于使用了amba（）,所以仅发送先发送数据的Observable
        // 即第二个（因为第1个延时了）
        Observable.amb(list);

        //defaultIfEmpty()
        //在不发送任何有效事件（ Next事件）、仅发送了 Complete 事件的前提下，发送一个默认值
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                // 不发送任何有效事件
                //  e.onNext(1);
                //  e.onNext(2);

                // 仅发送Complete事件
                e.onComplete();
            }
        }).defaultIfEmpty(10); // 若仅发送了Complete事件，默认发送 值 = 10
    }
}
