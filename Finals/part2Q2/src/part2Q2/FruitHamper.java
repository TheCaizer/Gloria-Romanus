package part2Q2;

public class FruitHamper extends ArrayListItemHamper<Fruit> {
    /**
     * invariant: FruitHamper must have at least 2 apples and 2 avocados when have >= 6 fruits. Otherwise, adding an item should do nothing
     * fruit hamper has price surcharge of 25%, rounded up to nearest integer
     */

    @Override
    public int getPrice(){
        int price = 0;
        double adjusted = super.getPrice() * 0.25;
        int adju = (int) adjusted;
        price = super.getPrice() + adju;
        return price;
    }

    @Override
    public void add(Fruit e, int n){
        int numApple = 0;
        int numAvo = 0;
        if(this.size() >= 6){
            for(Count<Fruit> c : this) {
                if(c.getElement() instanceof Apple){
                    numApple = numApple +1;
                }
                if(c.getElement() instanceof Avocado){
                    numAvo = numAvo +1;
                }
            }
            if(numApple < 2 && numAvo < 2){
                return;
            }
        }
        this.add(e, n);
    }
}
