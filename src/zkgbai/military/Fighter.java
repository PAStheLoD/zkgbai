package zkgbai.military;

import java.util.Deque;

import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Unit;


public class Fighter {
    public float metalValue;
    public int id;
    public int index;
    public Squad squad;
    protected Unit unit;
    protected static final short OPTION_SHIFT_KEY = (1 << 5); //  32

    public Fighter(Unit u, float metal){
        this.unit = u;
        this.id = u.getUnitId();
        this.metalValue = metal;
    }

    public Unit getUnit(){
        return unit;
    }

    public AIFloat3 getPos(){
        return unit.getPos();
    }

    public void fightTo(AIFloat3 pos, int frame){
        AIFloat3 target = getRadialPoint(pos, 200f);
        unit.fight(target, (short) 0, frame+6000);
    }

    public void moveTo(AIFloat3 pos, int frame){
        unit.moveTo(pos, (short) 0, frame+6000);
    }

    protected AIFloat3 getRadialPoint(AIFloat3 position, Float radius){
        // returns a random point lying on a circle around the given position.
        AIFloat3 pos = new AIFloat3();
        double angle = Math.random()*2*Math.PI;
        double vx = Math.cos(angle);
        double vz = Math.sin(angle);
        pos.x = (float) (position.x + radius*vx);
        pos.z = (float) (position.z + radius*vz);
        return pos;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Fighter){
            Fighter f = (Fighter) o;
            return (f.id == id);
        }
        return false;
    }
}
