package part2Q2;

public class CreativeHamper extends ArrayListItemHamper<Item> {
    /**
     * invariant: if hamper contains 5 or more items, it must contain at least 2 toy cars (at least 1 must be premium) and at least 2 fruits. Otherwise, adding an item should do nothing
     * creative hamper has a price surcharge of $10
     */


    @Override
    public int getPrice(){
        int price = 0;
        price = super.getPrice() + 10;
        return price;
    }

    @Override
    public void add(Item e, int n){
        int numCar = 0;
        int numpre = 0;
        if(this.size() >= 5){
            for(Count<Item> c : this) {
                if(c.getElement() instanceof ToyCar){
                    numCar += 1;
                }
                if(c.getElement() instanceof PremiumToyCar){
                    numpre += 1;
                }
            }
            if(numCar<2 && numpre<1){
                return;
            }
        }
        this.add(e, n);
    }
}
