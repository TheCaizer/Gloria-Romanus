package part2Q2;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A hamper implemented using an ArrayList.
 *
 * @author Matthew Perry
 *
 * @invariant for all c in counts, c.getCount() > 0
 *
 * @param <E>
 */
public class ArrayListItemHamper<E extends Item> implements Hamper<E> {

    private ArrayList<Count<E>> counts;

    /**
     * Create a new empty hamper.
     */
    public ArrayListItemHamper() {
        this.counts = new ArrayList<Count<E>>();
    }

    private Count<E> getCount(Object o) {
        for (Count<E> c : counts)
            if (c.getElement().equals(o))
                return c;
        return null;
    }

    @Override
    public void add(E e) {
        add(e,1);
    }

    @Override
    public void add(E e, int n) {
        Count<E> c = getCount(e);
        if (c != null) {
            c.incrementCount(n);
        } else if (n > 0) {
            counts.add(new Count<E>(e, n));
        }
    }

    @Override
    public void remove(E e) {
        remove(e, 1);
    }
	/**
     * Remove multiple copies of an element from the hamper, or all copies if there
     * are not that many copies in the hamper.
     * @param e The element to remove
     * @param n The number of copies to remove.
     */
    @Override
    public void remove(E e, int n) {
        //the precondition
        if(!(n>=0)){
            return;
        }
        for(Count<E> f: counts){
            //since the number of copies to remove is zero just return
            if(n == 0){
                return;
            }
            else if(f.getElement().equals(e)){
                f.decrementCount(n);
                if(f.getCount() <= 0){
                    counts.remove(f);
                }
            }
        }
    }

    @Override
    public int count(Object o) {
        Count<E> c = getCount(o);
        if (c != null)
            return c.getCount();
        return 0;
    }

    @Override
    public int size() {
        int size = 0;
        for(Count<E> c:counts){
            size = size + c.getCount();
        }
        return size;
    }

    @Override
    public Hamper<E> sum(Hamper<? extends E> hamper) {
        ArrayListItemHamper<E> result = new ArrayListItemHamper<E>();
        for(Count<? extends E> c : hamper) {
            result.add(c.getElement(), c.getCount());
        }
        for(Count<E> f: this){
            result.add(f.getElement(), f.getCount() );
        }
        return result;
    }

    @Override
    public Iterator<Count<E>> iterator() {
        return counts.iterator();
    }

    /**
     * For this method, hampers should be the same class to be equal (ignore the generic type component). For example, a CreativeHamper cannot be equal to a FruitHamper,
     * And a FruitHamper cannot be equal to an ArrayListItemHamper<Fruit>,
     * However an ArrayListItemHamper<Fruit> can be equal to a ArrayListItemHamper<Item> if they both only contain fruit.
     * HINT: use getclass() to compare objects.
     */
    @Override
    public boolean equals(Object o) {
        if(o == null){
            return false;
        }
        if(this == o){
            return true;
        }
        if(!(o instanceof Hamper)){
            return false;
        }
        if(!(o.getClass().equals(this.getClass()))){
            return false;
        }
        else{
            Hamper<?> temp = (Hamper<?>) o;
            if(temp.size() != this.size()){
                return false;
            }
            for(Count<?> c : temp) {
                if(!(c.getElement().getClass().equals(this.getClass()))){
                    return false;
                }
            }
            return true;
        }
    }

    /**
	 * 
	 * @return price of the hamper - for ArrayListItemHamper, this should be the sum of the prices of items with each price multiplied by the number of times that item occurs
	 */
	@Override
    public int getPrice() {
        int price = 0;
        for(Count<E> f: counts){
            int count = f.getCount();
            int pri = f.getElement().getPrice();
            int temp = pri*count;
            price = price + temp;
        }
        return price;
    }

    @Override
    public String toString(){
        return counts.toString();
    }
}
