package unsw.collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import unsw.fruit.Apple;
import unsw.fruit.Fruit;
import unsw.fruit.Orange;

class ArrayListSetTest {

    @Test
    void testBasics() {
        Set<String> set = new ArrayListSet<>();
        set.add("Hello");
        set.add("World");
        assertTrue(set.contains("Hello"));
        assertTrue(set.contains("World"));

        set.remove("Hello");
        assertFalse(set.contains("Hello"));
        assertTrue(set.contains("World"));
    }

    @Test
    void testSubsetOf() {
        Set<Fruit> fruit = new ArrayListSet<Fruit>();
        fruit.add(new Apple("Gala"));
        fruit.add(new Apple("Fuji"));
        fruit.add(new Orange("Navel"));

        Set<Apple> apples = new ArrayListSet<>();
        apples.add(new Apple("Gala"));
        apples.add(new Apple("Fuji"));

        assertTrue(apples.subsetOf(fruit));
        assertFalse(fruit.subsetOf(apples));

        fruit.remove(new Apple("Fuji"));

        assertFalse(apples.subsetOf(fruit));
    }
    @Test
    void testIterator() {
        Set<Fruit> fruit = new ArrayListSet<Fruit>();
        Apple gala = new Apple("Gala");
        Apple Fuji = new Apple("Fuji");
        Orange Navel = new Orange("Navel");
        fruit.add(gala);
        fruit.add(Fuji);
        fruit.add(Navel);

        assertTrue(fruit.contains(gala));
        assertTrue(fruit.contains(Fuji));
        assertTrue(fruit.contains(Navel));

        Iterator<Fruit> iter = fruit.iterator();
        Fruit first = iter.next();
        Fruit second = iter.next();
        Fruit third = iter.next();
        assertTrue(first.getName().equals("Gala"));
        assertTrue(second.getName().equals("Fuji"));
        assertTrue(third.getName().equals("Navel"));
    }
    @Test
    void testUnion() {
        Set<Fruit> fruit = new ArrayListSet<Fruit>();
        fruit.add(new Apple("Gala"));
        fruit.add(new Apple("Fuji"));
        fruit.add(new Orange("Navel"));

        Set<Fruit> fruit2 = new ArrayListSet<Fruit>();
        fruit2.add(new Apple("Gala"));
        fruit2.add(new Apple("Fuji"));
        fruit2.add(new Orange("Navel"));
        Orange sour = new Orange("Sour");
        fruit2.add(sour);

        Set<Fruit> temp = fruit.union(fruit2);

        assertTrue(temp.size() == 4);
        assertTrue(temp.contains(sour));
    }
    @Test
    void testIntersection() {
        Set<Fruit> fruit = new ArrayListSet<Fruit>();
        fruit.add(new Apple("Gala"));
        fruit.add(new Apple("Fuji"));
        fruit.add(new Orange("Navel"));

        Set<Fruit> fruit2 = new ArrayListSet<Fruit>();
        fruit2.add(new Apple("Gala"));
        fruit2.add(new Apple("Fuji"));
        fruit2.add(new Orange("Navel"));
        Orange sour = new Orange("Sour");
        fruit2.add(sour);
        Set<Fruit> temp = fruit.intersection(fruit2);
        assertTrue(temp.size() == 3);
        assertFalse(temp.contains(sour));
    }
    @Test
    void testEquals() {
        Set<Fruit> fruit = new ArrayListSet<Fruit>();
        fruit.add(new Apple("Gala"));
        fruit.add(new Apple("Fuji"));
        fruit.add(new Orange("Navel"));

        Set<Fruit> fruit2 = new ArrayListSet<Fruit>();
        fruit2.add(new Apple("Gala"));
        fruit2.add(new Apple("Fuji"));
        fruit2.add(new Orange("Navel"));

        assertTrue(fruit.equals(fruit2));
        Orange sour = new Orange("Sour");
        fruit2.add(sour);
        assertFalse(fruit.equals(fruit2));
    }
}
