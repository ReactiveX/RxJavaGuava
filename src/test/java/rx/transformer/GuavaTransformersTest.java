package rx.transformer;

import static org.junit.Assert.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.junit.Test;

import rx.Observable;
import rx.functions.Func1;

public class GuavaTransformersTest {
    @Test
    public void testList() {
        assertEquals(ImmutableList.builder().add(1).add(3).add(2).add(3).build(), Observable.just(1, 3, 2, 3).compose(GuavaTransformers.toImmutableList()).toBlocking().last());
    }

    @Test
    public void testSet() {
        assertEquals(ImmutableSet.builder().add(1).add(3).add(2).add(3).build(), Observable.just(1, 3, 2, 3).compose(GuavaTransformers.toImmutableSet()).toBlocking().last());
    }

    Func1<String, String> keyMapper = new Func1<String, String>() {
        @Override
        public String call(String t) {
            return t.split(":")[0];
        }
    };

    Func1<String, String> valueMapper = new Func1<String, String>() {
        @Override
        public String call(String t) {
            return t.split(":")[1];
        }
    };

    @Test
    public void testMap() {
        assertEquals(ImmutableMap.builder().put("a", "1").put("b", "2").put("c", "3").build(), Observable.just("a:1", "b:2", "c:3").compose(GuavaTransformers.toImmutableMap(keyMapper, valueMapper))
                .toBlocking().last());
    }

    @Test
    public void testListMap() {
        assertEquals(ImmutableListMultimap.builder().put("a", "1").putAll("b", "2", "4").put("c", "3").build(), Observable.just("a:1", "b:2", "c:3", "b:4").compose(
                GuavaTransformers.toImmutableListMultimap(keyMapper, valueMapper)).toBlocking().last());
    }
}
