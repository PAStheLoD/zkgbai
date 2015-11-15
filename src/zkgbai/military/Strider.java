package zkgbai.military;

import com.springrts.ai.oo.clb.Unit;

/**
 * Created by haplo on 11/14/2015.
 */
public class Strider extends Fighter{
    public int dgunReload;
    public int lastDgunFrame = 0;
    public Strider(Unit u, Float metal){
        super(u, metal);
        if (u.getDef().getName().equals("dante")) {
            this.dgunReload = 150;
        }else{
            this.dgunReload = 200;
        }
    }
}
