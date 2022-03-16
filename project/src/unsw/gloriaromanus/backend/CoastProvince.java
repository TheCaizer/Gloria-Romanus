package unsw.gloriaromanus.backend;

import unsw.gloriaromanus.backend.infrastructure.BuildingFactory;
import unsw.gloriaromanus.backend.infrastructure.Port;

public class CoastProvince extends Province {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //private Port port;

    public CoastProvince(String name, Turn turn, BuildingFactory buildingFactory) {
        super(name, turn, buildingFactory);
        getBuildings().put("Port", new Port(buildingFactory));
    }

    @Override
    public int getProvinceWealth() {
        return super.getProvinceWealth() + getFaction().getCoastWealthBonus();
    }

    @Override
    public int getProvinceGrowth() {
        return super.getProvinceGrowth() + getFaction().getCoastGrowthBonus();
    }

    public int getCoastWealthBonus() {
        return getPort().getCoastWealthBonus();
    }

    public int getCoastGrowthBonus() {
        return getPort().getCoastGrowthBonus();
    }
    

    public Port getPort(){
        return(Port) getBuildings().get("Port");
    }
}