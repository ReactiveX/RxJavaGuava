package rx.transformer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import rx.Observable;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A class of static factory methods to transform an Observable into an Observable of Guauva collections
 */
public final class GuavaTransformers {

    private GuavaTransformers() {}

    /**
     * Returns a Transformer&lt;T,ImmutableList&lt;T&gt;&gt that maps an Observable&lt;T&gt; to an Observable&lt;ImmutableList&lt;T&gt;&gt;
     */
    public static <T> Observable.Transformer<T,ImmutableList<T>> toImmutableList() {
        return new Observable.Transformer<T, ImmutableList<T>>() {
            @Override
            public Observable<ImmutableList<T>> call(Observable<T> source) {
                return source.collect(new Func0<ImmutableList.Builder<T>>() {
                    @Override
                    public ImmutableList.Builder<T> call() {
                        return ImmutableList.builder();
                    }
                }, new Action2<ImmutableList.Builder<T>, T>() {
                    @Override
                    public void call(ImmutableList.Builder<T> builder, T t) {
                        builder.add(t);
                    }
                })
                .map(new Func1<ImmutableList.Builder<T>, ImmutableList<T>>() {
                    @Override
                    public ImmutableList<T> call(ImmutableList.Builder<T> builder) {
                        return builder.build();
                    }
                });
            }
        };
    }
    /**
     * Returns a Transformer&lt;T,ImmutableSet&lt;T&gt;&gt that maps an Observable&lt;T&gt; to an Observable&lt;ImmutableSet&lt;T&gt;&gt;
     */
    public static <T> Observable.Transformer<T,ImmutableSet<T>> toImmutableSet() {
        return new Observable.Transformer<T, ImmutableSet<T>>() {
            @Override
            public Observable<ImmutableSet<T>> call(Observable<T> source) {
                return source.collect(new Func0<ImmutableSet.Builder<T>>() {
                    @Override
                    public ImmutableSet.Builder<T> call() {
                        return ImmutableSet.builder();
                    }
                }, new Action2<ImmutableSet.Builder<T>, T>() {
                    @Override
                    public void call(ImmutableSet.Builder<T> builder, T t) {
                        builder.add(t);
                    }
                })
                .map(new Func1<ImmutableSet.Builder<T>, ImmutableSet<T>>() {
                    @Override
                    public ImmutableSet<T> call(ImmutableSet.Builder<T> builder) {
                        return builder.build();
                    }
                });
            }
        };
    }
    /**
     * Returns a Transformer&lt;T,ImmutableMap&lt;K,V&gt;&gt that maps an Observable&lt;T&gt; to an Observable&lt;ImmutableMap&lt;K,V&gt;&gt><br><br>
     * with a given Func1&lt;T,K&gt; keyMapper and Func1&lt;T,V&gt; valueMapper
     */
    public static <T,K,V> Observable.Transformer<T,ImmutableMap<K,V>> toImmutableMap(final Func1<T,K> keyMapper, final Func1<T,V> valueMapper) {
        return new Observable.Transformer<T,ImmutableMap<K,V>>() {
            @Override
            public Observable<ImmutableMap<K, V>> call(Observable<T> observable) {
                return observable.collect(new Func0<ImmutableMap.Builder<K, V>>() {
                    @Override
                    public ImmutableMap.Builder<K, V> call() {
                        return ImmutableMap.builder();
                    }
                }, new Action2<ImmutableMap.Builder<K, V>, T>() {
                    @Override
                    public void call(ImmutableMap.Builder<K, V> builder, T t) {
                        builder.put(keyMapper.call(t), valueMapper.call(t));
                    }
                })
                .map(new Func1<ImmutableMap.Builder<K, V>, ImmutableMap<K, V>>() {
                    @Override
                    public ImmutableMap<K, V> call(ImmutableMap.Builder<K, V> builder) {
                        return builder.build();
                    }
                });
            }
        };
    }

    /**
     * Returns a Transformer&lt;T,ImmutableListMultimap&lt;K,V&gt;&gt that maps an Observable&lt;T&gt; to an Observable&lt;ImmutableListMultimap&lt;K,V&gt;&gt><br><br>
     * with a given Func1&lt;T,K&gt; keyMapper and Func1&lt;T,V&gt; valueMapper
     */
    public static <T,K,V> Observable.Transformer<T,ImmutableListMultimap<K,V>> toImmutableListMultimap(final Func1<T,K> keyMapper, final Func1<T,V> valueMapper) {
        return new Observable.Transformer<T,ImmutableListMultimap<K,V>>() {
            @Override
            public Observable<ImmutableListMultimap<K, V>> call(Observable<T> observable) {
                return observable.collect(new Func0<ImmutableListMultimap.Builder<K, V>>() {
                    @Override
                    public ImmutableListMultimap.Builder<K, V> call() {
                        return ImmutableListMultimap.builder();
                    }
                }, new Action2<ImmutableListMultimap.Builder<K, V>, T>() {
                    @Override
                    public void call(ImmutableListMultimap.Builder<K, V> builder, T t) {
                        builder.put(keyMapper.call(t), valueMapper.call(t));
                    }
                })
                .map(new Func1<ImmutableListMultimap.Builder<K, V>, ImmutableListMultimap<K, V>>() {
                    @Override
                    public ImmutableListMultimap<K, V> call(ImmutableListMultimap.Builder<K, V> builder) {
                        return builder.build();
                    }
                });
            }
        };
    }
}
